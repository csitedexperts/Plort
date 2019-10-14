package plort.core.ast;

import java.util.List;

public final class VarDefsNode extends AST.Node {
  
  private final List<VarDefBlob> defs;
  private final Node inner;
  
  public VarDefsNode(List<VarDefBlob> defs, Node inner) {
    if (inner == null) throw new NullPointerException();
    this.defs = defs == null ? List.of() : List.copyOf(defs);
    this.inner = inner;
  }
  
  public List<VarDefBlob> defs() {
    return defs;
  }
  
  public Node inner() {
    return inner;
  }
  
  @Override
  public <R> R accept(ASTVisitor<R> visitor) {
    return visitor.visit(this);
  }
  
}