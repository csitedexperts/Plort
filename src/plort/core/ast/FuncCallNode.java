package plort.core.ast;

import java.util.List;

public final class FuncCallNode extends AST.Node {
  
  private final Node callee;
  private final List<Node> args;
  
  public FuncCallNode(Node callee, List<? extends Node> args) {
    if (callee == null) throw new NullPointerException();
    this.callee = callee;
    this.args = args == null ? List.of() : List.copyOf(args);
  }
  
  public Node callee() {
    return callee;
  }
  
  public List<Node> args() {
    return args;
  }
  
  @Override
  public <R> R accept(ASTVisitor<R> visitor) {
    return visitor.visit(this);
  }
  
}
