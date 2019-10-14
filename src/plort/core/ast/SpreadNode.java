package plort.core.ast;

public final class SpreadNode extends AST.Node {
  
  private final Node inner;
  
  public SpreadNode(Node inner) {
    this.inner = inner;
  }
  
  public Node inner() {
    return inner;
  }
  
  @Override
  public <R> R accept(ASTVisitor<R> visitor) {
    return visitor.visit(this);
  }
  
}
