package plort.core.ast;

import java.util.List;

public final class ListNode extends AST.Node {
  
  private final List<Node> list;
  
  public ListNode(List<? extends Node> list) {
    this.list = list == null ? List.of() : List.copyOf(list);
  }
  
  public List<Node> list() {
    return list;
  }
  
  @Override
  public <R> R accept(ASTVisitor<R> visitor) {
    return visitor.visit(this);
  }
  
}
