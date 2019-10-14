package plort.core.value;

import plort.core.ASTExecutor;
import plort.core.PlortException;
import plort.core.Scope;
import plort.core.ast.AST;

import java.util.ArrayList;
import java.util.List;

public abstract class FuncValue extends Value {
  
  private FuncValue() {}
  
  public static FuncValue definedFunc(Scope override, List<String> params, boolean varargs, AST.Node body) {
    if (override == null || body == null) throw new NullPointerException();
    return new Defined(override, params == null ? List.of() : List.copyOf(params), varargs, body);
  }
  
  public static FuncValue nativeFunc(int argc, boolean varargs, NativeFunc f) {
    if (f == null) throw new NullPointerException();
    if (argc < 0) throw new IllegalArgumentException();
    return new Native(argc, varargs, f);
  }
  
  public abstract int argc();
  public abstract boolean varargs();
  
  public abstract Value invoke(ASTExecutor executor, List<Value> args);
  
  public final Value invoke(ASTExecutor executor, Value... args) {
    return invoke(executor, List.of(args));
  }
  
  @Override
  final <R> R match(Matcher<R> matcher) {
    return matcher.match(FuncValue.class, this);
  }
  
  private static final class Defined extends FuncValue {
    
    final Scope scope;
    final List<String> params;
    final boolean varargs;
    final AST.Node body;
    
    Defined(Scope scope, List<String> params, boolean varargs, AST.Node body) {
      this.scope = scope;
      this.params = params;
      this.varargs = varargs;
      this.body = body;
    }
    
    @Override
    public int argc() {
      return params.size() - (varargs ? 1 : 0);
    }
    
    @Override
    public boolean varargs() {
      return varargs;
    }
    
    @Override
    public Value invoke(ASTExecutor executor, List<Value> args) {
      int argsSize = args.size(), paramsSize = params.size();
      if (varargs ? argsSize < paramsSize - 1 : argsSize != paramsSize) throw new PlortException("invalid number of arguments");
      var defs = new ArrayList<Scope.Def>();
      for (var i = 0; i < paramsSize - (varargs ? 1 : 0); i++) {
        defs.add(Scope.def(params.get(i), args.get(i)));
      }
      if (varargs) defs.add(Scope.def(params.get(paramsSize - 1), ListValue.of(args.subList(paramsSize - 1, argsSize))));
      //executor.scope.pushOverride(scope);
      //if (!defs.isEmpty()) executor.scope.push(defs);
      //var result = body.accept(executor);
      //if (!defs.isEmpty()) executor.scope.pop();
      //executor.scope.popOverride();
      //return result;
      try (var clojure = executor.overrideScope(scope); var argsScope = defs.isEmpty() ? null : executor.childScope(defs)) {
        return body.accept(executor);
      }
    }
    
    @Override
    public String toString() {
      return "Function(" + String.join(", ", params) + (varargs ? "..." : "") + ')';
    }
    
    @Override
    public boolean equals(Object o) {
      return o instanceof Defined && body == ((Defined) o).body;
    }
    
    @Override
    public int hashCode() {
      return body.hashCode();
    }
    
  }
  
  private static final class Native extends FuncValue {
    
    final int argc;
    final boolean varargs;
    final NativeFunc f;
    
    Native(int argc, boolean varargs, NativeFunc f) {
      this.argc = argc;
      this.varargs = varargs;
      this.f = f;
    }
    
    @Override
    public int argc() {
      return argc;
    }
    
    @Override
    public boolean varargs() {
      return varargs;
    }
    
    @Override
    public Value invoke(ASTExecutor executor, List<Value> args) {
      if (varargs ? args.size() < argc : args.size() != argc) throw new PlortException("invalid number of arguments");
      return f.invoke(List.copyOf(args));
    }
    
    @Override
    public String toString() {
      return "Function(" + argc + (varargs ? "..." : "") + " [native])";
    }
    
    @Override
    public boolean equals(Object o) {
      return o instanceof Native && f == ((Native) o).f;
    }
    
    @Override
    public int hashCode() {
      return f.hashCode();
    }
    
  }
  
  @FunctionalInterface
  public interface NativeFunc {
    
    Value invoke(List<Value> args);
    
  }
  
}
