package plort.core;

import plort.core.ast.*;
import plort.core.value.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.lang.Math.round;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static plort.core.value.MapValue.entry;
import static plort.core.value.Value.otherwise;
import static plort.core.value.Value.when;

public final class ASTExecutor implements ASTVisitor<Value> {
  
  private Scope scope = Scope.empty();
  
  public Scoper childScope(Map<String, Value> scope) {
    this.scope = Scope.of(this.scope, scope);
    return () -> this.scope = this.scope.parent;
  }
  
  public Scoper childScope(List<Scope.Def> scope) {
    this.scope = Scope.of(this.scope, scope);
    return () -> this.scope = this.scope.parent;
  }
  
  public Scoper childScope(Scope.Def... scope) {
    this.scope = Scope.of(this.scope, scope);
    return () -> this.scope = this.scope.parent;
  }
  
  public Scoper overrideScope(Scope scope) {
    var previous = this.scope;
    this.scope = scope;
    return () -> this.scope = previous;
  }
  
  @Override
  public Value visit(VarDefsNode node) {
    var defs = new HashMap<String, Value>();
    var rawDefs = node.defs().stream().collect(toMap(VarDefBlob::name, VarDefBlob::value, (l, r) -> new PlortException("can't override variables of the same scope").throwExpr()));
    for (var key : rawDefs.keySet()) defs.put(key, NullValue.INSTANCE);
    try (var defScope = childScope(defs)) {
      for (var entry : rawDefs.entrySet()) scope.set(entry.getKey(), entry.getValue().accept(this));
      return node.inner().accept(this);
    }
  }
  
  @Override
  public Value visit(IfNode node) {
    return node.condition().accept(this).as(BoolValue.class).map(bool -> bool.value).orElseThrow(() -> new PlortException("if expressions only branch on bools")) ? node.ifBranch().accept(this) : node.elseBranch().map(elseBranch -> elseBranch.accept(this)).orElse(NullValue.INSTANCE);
  }
  
  @Override
  public Value visit(WhileNode node) {
    var indexName = node.indexName();
    Value value = null;
    FuncValue operation = null;
    for (int i = 0;; i++) {
      var index = i;
      try (var indexScope = indexName.map(name -> childScope(Scope.def(name, NumberValue.of(index)))).orElse(null)) {
        if (!node.condition().accept(this).as(BoolValue.class).map(bool -> bool.value).orElseThrow(() -> new PlortException("while expressions only branch on bools")))
          break;
        var body = node.body().accept(this);
        if (value == null) {
          value = body;
        } else {
          if (operation == null)
            operation = node.operation().accept(this).as(FuncValue.class).filter(f -> f.varargs() ? f.argc() <= 2 : f.argc() == 2).orElseThrow(() -> new PlortException("while expressions only operates with functions that can be called with two arguments"));
          value = operation.invoke(this, value, body);
        }
      }
    }
    return value == null ? node.elseBranch().map(elseBranch -> elseBranch.accept(this)).orElse(NullValue.INSTANCE) : value;
  }
  
  @Override
  public Value visit(BinOpNode node) {
    return node.op().operate(() -> node.left().accept(this), () -> node.right().accept(this));
  }
  
  @Override
  public Value visit(UnOpNode node) {
    return node.op().operate(node.operand().accept(this));
  }
  
  @Override
  public Value visit(FuncCallNode node) {
    return node.callee().accept(this).as(FuncValue.class).map(f -> f.invoke(this, node.args().stream()
      .flatMap(arg ->
        arg instanceof SpreadNode ?
          ((SpreadNode) arg).inner()
            .accept(this)
            .as(ListValue.class)
            .map(list -> list.values.stream())
            .orElseThrow(() -> new PlortException("only lists can be spread into arguments"))
        : Stream.of(arg.accept(this))
      ).collect(toList())
    )).orElseThrow(() -> new PlortException("only functions can be invoked"));
  }
  
  @Override
  public Value visit(MemberAccessNode node) {
    return node.object().accept(this).match(
      when(MapValue.class, map -> {
        var value = map.values.get(node.member().accept(this));
        if (value == null) throw new PlortException("invalid member access");
        return value;
      }),
      when(ListValue.class, list -> {
        var index = node.member().accept(this).as(NumberValue.class);
        if (index.map(i -> i.value < 0 || i.value >= list.values.size() || round(i.value) != i.value).orElse(false)) throw new PlortException("list indicies must only be positive integers less than the list's length");
        return list.values.get((int) index.orElseThrow().value);
      }),
      otherwise(value -> new PlortException("only maps and lists can have members").throwExpr())
    );
  }
  
  @Override
  public Value visit(SpreadNode node) {
    throw new PlortException("spread expressions outside of applicable context");
  }
  
  @Override
  public Value visit(VarNode node) {
    return scope.get(node.name()).orElseThrow(() -> new PlortException("no such variable: " + node.name()));
  }
  
  @Override
  public FuncValue visit(FuncNode node) {
    return FuncValue.definedFunc(scope, node.params().names(), node.params().varargs(), node.body());
  }
  
  @Override
  public FuncValue visit(NativeFuncNode node) {
    return NativeFuncRegistry.get(NativeFuncRegistry.id(node.nativeRef())).orElseThrow(() -> new PlortException("no such native function: " + String.join(":", node.nativeRef())));
  }
  
  @Override
  public MapValue visit(MapNode node) {
    return MapValue.of(node.map().stream().flatMap(member ->
      member instanceof MemberBlob.Spread ?
        ((MemberBlob.Spread) member).inner()
          .accept(this)
          .as(MapValue.class)
          .map(map ->
            map.values.entrySet().stream().map(entry -> entry(entry.getKey(), entry.getValue()))
          ).orElseThrow(() -> new PlortException("only maps can be spread into maps"))
      : Stream.of(
        entry(((MemberBlob.Single) member).key().accept(this), ((MemberBlob.Single) member).value().accept(this))
      )
    ).collect(toList()));
  }
  
  @Override
  public ListValue visit(ListNode node) {
    return ListValue.of(node.list().stream().flatMap(element ->
      element instanceof SpreadNode ?
        ((SpreadNode) element).inner()
          .accept(this)
          .as(ListValue.class)
          .map(list -> list.values.stream())
          .orElseThrow(() -> new PlortException("only lists can be spread into lists"))
        : Stream.of(element.accept(this))
    ).collect(toList()));
  }
  
  @Override
  public StringValue visit(StringNode node) {
    return StringValue.of(node.value());
  }
  
  @Override
  public NumberValue visit(NumberNode node) {
    return NumberValue.of(node.value());
  }
  
  @Override
  public BoolValue visit(BoolNode node) {
    return BoolValue.of(node.value());
  }
  
  @Override
  public NullValue visit(NullNode node) {
    return NullValue.INSTANCE;
  }
  
  @FunctionalInterface
  public interface Scoper extends AutoCloseable {
    
    @Override
    void close();
    
  }
  
}