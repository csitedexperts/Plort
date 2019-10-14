package plort.core.ast;

public final class BinOpNode extends AST.Node {
  
  private final Node left, right;
  private final BinOpBlob op;
  
  public BinOpNode(Node left, Node right, BinOpBlob op) {
    if (left == null || right == null || op == null) throw new NullPointerException();
    this.left = left;
    this.right = right;
    this.op = op;
  }
  
  public Node left() {
    return left;
  }
  
  public Node right() {
    return right;
  }
  
  public BinOpBlob op() {
    return op;
  }
  
  @Override
  public <R> R accept(ASTVisitor<R> visitor) {
    return visitor.visit(this);
  }
  
}
