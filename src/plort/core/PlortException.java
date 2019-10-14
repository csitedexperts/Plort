package plort.core;

import java.util.function.Supplier;

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
  
  public <R> R throwExpr() {
    throw this;
  }
  
}
