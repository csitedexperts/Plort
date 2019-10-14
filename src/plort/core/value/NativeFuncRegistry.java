package plort.core.value;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public final class NativeFuncRegistry {
  
  // for lack of a better way to prevent construction
  private NativeFuncRegistry() {}
  
  private static final HashMap<Id, FuncValue> registry = new HashMap<>();
  
  public static boolean register(Id id, FuncValue func) {
    return id != null && func != null && registry.putIfAbsent(id, func) == null;
  }
  
  public static boolean register(Id id, int argc, boolean varargs, FuncValue.NativeFunc f) {
    return register(id, FuncValue.nativeFunc(argc, varargs, f));
  }
  
  public static Optional<FuncValue> get(Id id) {
    return Optional.ofNullable(registry.get(id));
  }
  
  public static Id id(List<String> id) {
    if (id == null || id.isEmpty()) throw new IllegalArgumentException();
    return new Id(List.copyOf(id));
  }
  
  public static Id id(String... id) {
    if (id == null || id.length == 0) throw new IllegalArgumentException();
    return new Id(List.of(id));
  }
  
  public static final class Id {
    
    private final List<String> ids;
    
    private Id(List<String> ids) {
      this.ids = ids;
    }
    
    @Override
    public String toString() {
      return String.join(":", ids);
    }
    
    @Override
    public boolean equals(Object o) {
      return o instanceof Id && ids.equals(((Id) o).ids);
    }
    
    @Override
    public int hashCode() {
      return ids.hashCode();
    }
    
  }
  
}
