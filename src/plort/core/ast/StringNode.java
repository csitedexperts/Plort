package plort.core.ast;

public final class StringNode extends AST.Node {
  
  private final String value;
  
  public StringNode(String value) {
    this.value = value;
  }
  
  public String value() {
    return value;
  }
  
  @Override
  public <R> R accept(ASTVisitor<R> visitor) {
    return visitor.visit(this);
  }
  
}
