package plort.core.ast;

public abstract class MemberBlob implements AST.Blob {
  
  private MemberBlob() {}
  
  public static final class Single extends MemberBlob {
    
    private final AST.Node key, value;
    
    public Single(AST.Node key, AST.Node value) {
      if (key == null || value == null) throw new NullPointerException();
      this.key = key;
      this.value = value;
    }
    
    public AST.Node key() {
      return key;
    }
    
    public AST.Node value() {
      return value;
    }
    
  }
  
  public static final class Spread extends MemberBlob {
    
    private final AST.Node inner;
    
    public Spread(AST.Node inner) {
      if (inner == null) throw new NullPointerException();
      this.inner = inner;
    }
    
    public AST.Node inner() {
      return inner;
    }
    
  }
  
}
