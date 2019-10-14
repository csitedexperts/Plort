package plort.core.ast;

import java.util.List;

public final class ParamsBlob implements AST.Blob {
  
  private final List<String> names;
  private final boolean varargs;
  
  public ParamsBlob(List<String> names, boolean varargs) {
    this.names = names == null ? List.of() : List.copyOf(names);
    this.varargs = varargs;
  }
  
  public List<String> names() {
    return names;
  }
  
  public boolean varargs() {
    return varargs;
  }
  
}
