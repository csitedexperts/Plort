package plort.core.ast;

public final class UnOpNode extends AST.Node {
  
  private final Node operand;
  private final UnOpBlob op;
  
  public UnOpNode(Node operand, UnOpBlob op) {
    if (operand == null || op == null) throw new NullPointerException();
    this.operand = operand;
    this.op = op;
  }
  
  public Node operand() {
    return operand;
  }
  
  public UnOpBlob op() {
    return op;
  }
  
  @Override
  public <R> R accept(ASTVisitor<R> visitor) {
    return visitor.visit(this);
  }
  
}
