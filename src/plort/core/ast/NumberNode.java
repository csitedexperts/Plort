package plort.core.ast;

public final class NumberNode extends AST.Node {
  
  private final double value;
  
  public NumberNode(double value) {
    this.value = value;
  }
  
  public double value() {
    return value;
  }
  
  @Override
  public <R> R accept(ASTVisitor<R> visitor) {
    return visitor.visit(this);
  }
  
}
