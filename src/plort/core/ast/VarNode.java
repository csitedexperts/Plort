package plort.core.ast;

public final class VarNode extends AST.Node {
  
  private final String name;
  
  public VarNode(String name) {
    if (name == null) throw new NullPointerException();
    this.name = name;
  }
  
  public String name() {
    return name;
  }
  
  @Override
  public <R> R accept(ASTVisitor<R> visitor) {
    return visitor.visit(this);
  }
  
}
