package plort.core.ast;

public final class MemberAccessNode extends AST.Node {
  
  private final Node object, member;
  
  public MemberAccessNode(Node object, Node member) {
    if (object == null || member == null) throw new NullPointerException();
    this.object = object;
    this.member = member;
  }
  
  public Node object() {
    return object;
  }
  
  public Node member() {
    return member;
  }
  
  @Override
  public <R> R accept(ASTVisitor<R> visitor) {
    return visitor.visit(this);
  }
  
}
