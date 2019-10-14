package plort.core.ast;

public final class FuncNode extends AST.Node {
  
  private final ParamsBlob params;
  private final Node body;
  
  public FuncNode(ParamsBlob params, Node body) {
    if (params == null || body == null) throw new NullPointerException();
    this.params = params;
    this.body = body;
  }
  
  public ParamsBlob params() {
    return params;
  }
  
  public Node body() {
    return body;
  }
  
  @Override
  public <R> R accept(ASTVisitor<R> visitor) {
    return visitor.visit(this);
  }
  
}
