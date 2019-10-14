package plort.core.ast;

import java.util.List;

public final class MapNode extends AST.Node {
  
  private final List<MemberBlob> map;
  
  public MapNode(List<MemberBlob> map) {
    this.map = map == null ? List.of() : List.copyOf(map);
  }
  
  public List<MemberBlob> map() {
    return map;
  }
  
  @Override
  public <R> R accept(ASTVisitor<R> visitor) {
    return visitor.visit(this);
  }
  
}
