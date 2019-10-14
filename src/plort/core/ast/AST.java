package plort.core.ast;

public interface AST {
  
  abstract class Node implements AST {
    
    Node() {}
    
    public abstract <R> R accept(ASTVisitor<R> visitor);
  
  }
  
  interface Blob extends AST {}
  
}