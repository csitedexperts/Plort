package plort.core.ast;

public final class NullNode extends AST.Node {
  
  public static final NullNode INSTANCE = new NullNode();
  
  private NullNode() {}
  
  @Override
  public <R> R accept(ASTVisitor<R> visitor) {
    return visitor.visit(this);
  }
  
}
