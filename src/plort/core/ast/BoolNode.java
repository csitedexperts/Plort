package plort.core.ast;

public final class BoolNode extends AST.Node {
  
  public static final BoolNode TRUE = new BoolNode(true), FALSE = new BoolNode(false);
  
  private final boolean value;
  
  private BoolNode(boolean value) {
    this.value = value;
  }
  
  public static BoolNode of(boolean value) {
    return value ? TRUE : FALSE;
  }
  
  public boolean value() {
    return value;
  }
  
  @Override
  public <R> R accept(ASTVisitor<R> visitor) {
    return visitor.visit(this);
  }
  
}
