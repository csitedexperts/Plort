package plort.core.ast;

import java.util.Optional;

public final class IfNode extends AST.Node {
  
  private final Node condition, ifBranch, elseBranch;
  
  public IfNode(Node condition, Node ifBranch, Node elseBranch) {
    if (condition == null || ifBranch == null) throw new NullPointerException();
    this.condition = condition;
    this.ifBranch = ifBranch;
    this.elseBranch = elseBranch;
  }
  
  public Node condition() {
    return condition;
  }
  
  public Node ifBranch() {
    return ifBranch;
  }
  
  public Optional<Node> elseBranch() {
    return Optional.ofNullable(elseBranch);
  }
  
  @Override
  public <R> R accept(ASTVisitor<R> visitor) {
    return visitor.visit(this);
  }
  
}
