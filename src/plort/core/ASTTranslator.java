package plort.core;

import org.antlr.v4.runtime.tree.TerminalNode;
import plort.core.ast.*;
import plort.gen.PlortBaseVisitor;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static plort.gen.PlortParser.*;

public class ASTTranslator extends PlortBaseVisitor<AST> {
  
  @Override
  public VarDefsNode visitFile(FileContext ctx) {
    var defs = new ArrayList<VarDefBlob>();
    for (var def : ctx.def()) defs.addAll(((VarDefsNode) def.accept(this)).defs());
    return new VarDefsNode(defs, NullNode.INSTANCE);
  }
  
  public VarDefsNode visitDef(DefContext ctx) {
    var defs = new ArrayList<VarDefBlob>();
    for (var def : ctx.varInit()) defs.add((VarDefBlob) def.accept(this));
    return new VarDefsNode(defs, NullNode.INSTANCE);
  }
  
  @Override
  public AST.Node visitExpr(ExprContext ctx) {
    var control = ctx.control();
    if (control != null) return (AST.Node) control.accept(this);
    var varInits = new ArrayList<VarDefBlob>();
    for (var varInit : ctx.varInit()) varInits.add((VarDefBlob) varInit.accept(this));
    return new VarDefsNode(varInits, (AST.Node) ctx.expr().accept(this));
  }
  
  @Override
  public VarDefBlob visitVarInit(VarInitContext ctx) {
    AST.Node value;
    if (ctx.ARROW() != null) {
      var nativeRef = ctx.nativeRef();
      if (nativeRef != null) {
        value = (AST.Node) nativeRef.accept(this);
      } else {
        var params = ctx.params();
        value = new FuncNode(params == null ? new ParamsBlob(List.of(), false) : (ParamsBlob) params.accept(this), (AST.Node) ctx.expr().accept(this));
      }
    } else {
      value = (AST.Node) ctx.expr().accept(this);
    }
    return new VarDefBlob(ctx.ID().getText(), value);
  }
  
  @Override
  public AST.Node visitControl(ControlContext ctx) {
    var expr = ctx.expr();
    if (ctx.IF() != null) return new IfNode((AST.Node) expr.get(0).accept(this), (AST.Node) expr.get(1).accept(this), expr.size() == 3 ? (AST.Node) expr.get(2).accept(this) : null);
    if (ctx.WHILE() != null) return new WhileNode(ctx.IN() == null ? null : ctx.ID().getText(), (AST.Node) expr.get(0).accept(this), (AST.Node) expr.get(1).accept(this), (AST.Node) expr.get(2).accept(this), expr.size() == 4 ? (AST.Node) expr.get(3).accept(this) : null);
    return (AST.Node) ctx.or().accept(this);
  }
  
  @Override
  public AST.Node visitOr(OrContext ctx) {
    var node = (AST.Node) ctx.and().accept(this);
    if (ctx.OR() != null) {
      node = new BinOpNode(node, (AST.Node) ctx.or().accept(this), BinOpBlob.OR);
      return ctx.NOT() == null ? node : new UnOpNode(node, UnOpBlob.NOT);
    }
    return node;
  }
  
  @Override
  public AST.Node visitAnd(AndContext ctx) {
    var node = (AST.Node) ctx.eq().accept(this);
    if (ctx.AND() != null) {
      node = new BinOpNode(node, (AST.Node) ctx.and().accept(this), BinOpBlob.AND);
      return ctx.NOT() == null ? node : new UnOpNode(node, UnOpBlob.NOT);
    }
    return node;
  }
  
  @Override
  public AST.Node visitEq(EqContext ctx) {
    var node = (AST.Node) ctx.cmp().accept(this);
    if (ctx.EQ() != null) {
      node = new BinOpNode(node, (AST.Node) ctx.eq().accept(this), BinOpBlob.EQ);
      return ctx.NOT() == null ? node : new UnOpNode(node, UnOpBlob.NOT);
    }
    return node;
  }
  
  @Override
  public AST.Node visitCmp(CmpContext ctx) {
    var node = (AST.Node) ctx.add().accept(this);
    var not = ctx.NOT() != null;
    BinOpBlob op = null;
    if (ctx.LT() != null) op = not ? BinOpBlob.GTE : BinOpBlob.LT;
    if (ctx.GT() != null) op = not ? BinOpBlob.LTE : BinOpBlob.GT;
    if (ctx.LTE() != null) op = not ? BinOpBlob.GT : BinOpBlob.LTE;
    if (ctx.GTE() != null) op = not ? BinOpBlob.LT : BinOpBlob.GTE;
    if (ctx.CMP() != null) op = BinOpBlob.CMP;
    if (op != null) return new BinOpNode(node, (AST.Node) ctx.cmp().accept(this), op);
    return node;
  }
  
  @Override
  public AST.Node visitAdd(AddContext ctx) {
    var node = (AST.Node) ctx.mul().accept(this);
    BinOpBlob op = null;
    if (ctx.ADD() != null) op = BinOpBlob.ADD;
    if (ctx.SUB() != null) op = BinOpBlob.SUB;
    if (op != null) return new BinOpNode(node, (AST.Node) ctx.add().accept(this), op);
    return node;
  }
  
  @Override
  public AST.Node visitMul(MulContext ctx) {
    var node = (AST.Node) ctx.unary().accept(this);
    BinOpBlob op = null;
    if (ctx.MUL() != null) op = BinOpBlob.MUL;
    if (ctx.DIV() != null) op = BinOpBlob.DIV;
    if (ctx.MOD() != null) op = BinOpBlob.MOD;
    if (op != null) return new BinOpNode(node, (AST.Node) ctx.mul().accept(this), op);
    return node;
  }
  
  @Override
  public AST.Node visitUnary(UnaryContext ctx) {
    UnOpBlob op = null;
    if (ctx.ADD() != null) op = UnOpBlob.ADD;
    if (ctx.SUB() != null) op = UnOpBlob.SUB;
    if (ctx.NOT() != null) op = UnOpBlob.NOT;
    if (op != null) return new UnOpNode((AST.Node) ctx.unary().accept(this), op);
    return (AST.Node) ctx.factor().accept(this);
  }
  
  @Override
  public AST.Node visitFactor(FactorContext ctx) {
    var factor = ctx.factor();
    if (factor != null) {
      if (ctx.ELLIPSIS() != null) return new SpreadNode((AST.Node) factor.accept(this));
      if (ctx.MEMBER() != null) {
        var ID = ctx.ID();
        return new MemberAccessNode((AST.Node) factor.accept(this), ID == null ? (AST.Node) ctx.simpleLit().accept(this) : new StringNode(ID.getText()));
      }
      if (ctx.LBRACK() != null) return new MemberAccessNode((AST.Node) factor.accept(this), (AST.Node) ctx.expr(0).accept(this));
      var args = new ArrayList<AST.Node>();
      if (!ctx.ABS().isEmpty()) args.add(new UnOpNode((AST.Node) ctx.expr(0).accept(this), UnOpBlob.ABS));
      else for (var expr : ctx.expr()) args.add((AST.Node) expr.accept(this));
      var funcLit = ctx.funcLit();
      if (funcLit != null) args.add((AST.Node) funcLit.accept(this));
      return new FuncCallNode((AST.Node) factor.accept(this), args);
    } else {
      if (!ctx.ABS().isEmpty()) return new UnOpNode((AST.Node) ctx.expr(0).accept(this), UnOpBlob.ABS);
      if (ctx.LET() != null || ctx.WHERE() != null) {
        var varInits = new ArrayList<VarDefBlob>();
        for (var varInit : ctx.varInit()) varInits.add((VarDefBlob) varInit.accept(this));
        return new VarDefsNode(varInits, (AST.Node) ctx.expr(0).accept(this));
      }
      var literal = ctx.literal();
      if (literal != null) return (AST.Node) literal.accept(this);
      var ID = ctx.ID();
      if (ID != null) return new VarNode(ID.getText());
      return (AST.Node) ctx.expr(0).accept(this);
    }
  }
  
  @Override
  public AST.Node visitLiteral(LiteralContext ctx) {
    var funcLit = ctx.funcLit();
    if (funcLit != null) return (AST.Node) funcLit.accept(this);
    var listLit = ctx.listLit();
    if (listLit != null) return (AST.Node) listLit.accept(this);
    var mapLit = ctx.mapLit();
    if (mapLit != null) return (AST.Node) mapLit.accept(this);
    return (AST.Node) ctx.simpleLit().accept(this);
  }
  
  @Override
  public AST.Node visitFuncLit(FuncLitContext ctx) {
    var nativeRef = ctx.nativeRef();
    return nativeRef != null ? (AST.Node) nativeRef.accept(this) : new FuncNode((ParamsBlob) ctx.params().accept(this), (AST.Node) ctx.expr().accept(this));
  }
  
  @Override
  public ParamsBlob visitParams(ParamsContext ctx) {
    var names = new ArrayList<String>();
    for (var ID : ctx.ID()) names.add(ID.getText());
    return new ParamsBlob(names, ctx.ELLIPSIS() != null);
  }
  
  @Override
  public NativeFuncNode visitNativeRef(NativeRefContext ctx) {
    return new NativeFuncNode(ctx.ID().stream().map(TerminalNode::getText).collect(toList()));
  }
  
  @Override
  public ListNode visitListLit(ListLitContext ctx) {
    var list = new ArrayList<AST.Node>();
    for (var expr : ctx.expr()) list.add((AST.Node) expr.accept(this));
    return new ListNode(list);
  }
  
  @Override
  public MapNode visitMapLit(MapLitContext ctx) {
    var map = new ArrayList<MemberBlob>();
    for (var member : ctx.member()) map.add((MemberBlob) member.accept(this));
    return new MapNode(map);
  }
  
  @Override
  public MemberBlob visitMember(MemberContext ctx) {
    if (ctx.ELLIPSIS() != null) return new MemberBlob.Spread((AST.Node) ctx.factor().accept(this));
    var valIndex = 0;
    AST.Node key = null;
    var ID = ctx.ID();
    if (ID != null) key = new StringNode(ID.getText());
    var simpleLit = ctx.simpleLit();
    if (simpleLit != null) key = (AST.Node) simpleLit.accept(this);
    if (key == null) key = (AST.Node) ctx.expr(valIndex++).accept(this);
    return new MemberBlob.Single(key, (AST.Node) ctx.expr(valIndex).accept(this));
  }
  
  @Override
  public AST.Node visitSimpleLit(SimpleLitContext ctx) {
    var STRING = ctx.STRING();
    if (STRING != null) return new StringNode(escape(STRING.getText()));
    var NUMBER = ctx.NUMBER();
    if (NUMBER != null) return new NumberNode(Double.parseDouble(NUMBER.getText()));
    if (ctx.TRUE() != null) return BoolNode.TRUE;
    if (ctx.FALSE() != null) return BoolNode.FALSE;
    return NullNode.INSTANCE;
  }
  
  private String escape(String source) {
    return Pattern.compile("\\\\(\r\n|.)").matcher(source.substring(1, source.length() - 1)).replaceAll(match -> {
      switch (match.group(1)) {
        case "\\": return "\\";
        case "\"": return "\"";
        case "`": return "`";
        case "\r\n": return "\r\n";
        case "r": case "\r": return "\r";
        case "n": case "\n": return "\n";
        case "t": return "\t";
        case "b": return "\b";
        case "f": return "\f";
        default: throw new IllegalArgumentException("invalid escape sequence: " + match.group(0));
      }
    });
  }
  
}
