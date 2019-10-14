package plort.core.ast;

public interface ASTVisitor<R> {
  
  R visit(VarDefsNode node);
  
  R visit(IfNode node);
  
  R visit(WhileNode node);
  
  R visit(BinOpNode node);
  
  R visit(UnOpNode node);
  
  R visit(FuncCallNode node);
  
  R visit(MemberAccessNode node);
  
  R visit(SpreadNode node);
  
  R visit(VarNode node);
  
  R visit(FuncNode node);
  
  R visit(NativeFuncNode node);
  
  R visit(MapNode node);
  
  R visit(ListNode node);
  
  R visit(StringNode node);
  
  R visit(NumberNode node);
  
  R visit(BoolNode node);
  
  R visit(NullNode node);
  
}
