package plort.core.ast;

public final class VarDefBlob implements AST.Blob {
  
  private final String name;
  private final Node value;
  
  public VarDefBlob(String name, Node value) {
    if (name == null || value == null) throw new NullPointerException();
    this.name = name;
    this.value = value;
  }
  
  public String name() {
    return name;
  }
  
  public Node value() {
    return value;
  }
  
}
