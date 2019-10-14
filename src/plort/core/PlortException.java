package plort.core;

public class PlortException extends RuntimeException {
  
  public PlortException() {
    super();
  }
  
  public PlortException(String message) {
    super(message);
  }
  
  public PlortException(String message, Throwable cause) {
    super(message, cause);
  }
  
  public PlortException(Throwable cause) {
    super(cause);
  }
  
  // this method can be called in any expression and should infer a suitable return type;
  // it effectively enables throw statements to be used as expressions, as in languages like C# and Kotlin
  public <R> R throwExpr() {
    throw this;
  }
  
}
