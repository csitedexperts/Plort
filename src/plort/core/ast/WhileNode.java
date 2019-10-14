package plort.core.ast;

import java.util.Optional;

public final class WhileNode extends AST.Node {
  
  private final String indexName;
  private final Node condition, operation, body, elseBranch;
  
  public WhileNode(String indexName, Node condition, Node operation, Node body, Node elseBranch) {
    if (condition == null || operation == null || body == null) throw new NullPointerException();
    this.indexName = indexName;
    this.condition = condition;
    this.operation = operation;
    this.body = body;
    this.elseBranch = elseBranch;
  }
  
  public Optional<String> indexName() {
    return Optional.ofNullable(indexName);
  }
  
  public Node condition() {
    return condition;
  }
  
  public Node operation() {
    return operation;
  }
  
  public Node body() {
    return body;
  }
  
  public Optional<Node> elseBranch() {
    return Optional.ofNullable(elseBranch);
  }
  
  @Override
  public <R> R accept(ASTVisitor<R> visitor) {
    return visitor.visit(this);
  }
  
}
