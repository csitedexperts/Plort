package plort.core.ast;

import java.util.List;

public final class NativeFuncNode extends AST.Node {
  
  private final List<String> nativeRef;
  
  public NativeFuncNode(List<String> nativeRef) {
    if (nativeRef == null || nativeRef.isEmpty()) throw new IllegalArgumentException();
    this.nativeRef = List.copyOf(nativeRef);
  }
  
  public List<String> nativeRef() {
    return nativeRef;
  }
  
  @Override
  public <R> R accept(ASTVisitor<R> visitor) {
    return visitor.visit(this);
  }
  
}
