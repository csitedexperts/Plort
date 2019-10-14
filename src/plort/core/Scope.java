package plort.core;

import plort.core.value.FuncValue;
import plort.core.value.Value;

import java.util.*;

import static java.util.stream.Collectors.toMap;

public final class Scope {
  
  public final Scope parent;
  private final Map<String, Value> defs;
  
  private Scope(Scope parent, Map<String, Value> defs) {
    this.parent = parent;
    this.defs = defs;
  }
  
  public static Scope empty() {
    return new Scope(null, Map.of());
  }
  
  public static Scope of(Scope parent, Map<String, Value> defs) {
    if (defs == null || defs.isEmpty()) throw new IllegalArgumentException();
    return new Scope(parent, new HashMap<>(defs));
  }
  
  public static Scope of(Map<String, Value> defs) {
    return of(null, defs);
  }
  
  public static Scope of(Scope parent, List<Def> defs) {
    if (defs == null || defs.isEmpty()) throw new IllegalArgumentException();
    return new Scope(parent, defs.stream().collect(toMap(def -> def.name, def -> def.value, (l, r) -> r)));
  }
  
  public static Scope of(List<Def> defs) {
    return of(null, defs);
  }
  
  public static Scope of(Scope parent, Def... defs) {
    if (defs == null || defs.length == 0) throw new IllegalArgumentException();
    return new Scope(parent, Arrays.stream(defs).collect(toMap(var -> var.name, var -> var.value, (l, r) -> r)));
  }
  
  public static Scope of(Def... defs) {
    return of(null, defs);
  }
  
  public static Def def(String name, Value value) {
    if (name == null || value == null) throw new NullPointerException();
    return new Def(name, value);
  }
  
  public Optional<Value> get(String name) {
    return Optional.ofNullable(defs.get(name)).or(() -> parent == null ? Optional.empty() : parent.get(name));
  }
  
  void set(String name, Value value) {
    if (name == null || value == null) throw new NullPointerException();
    if (defs.computeIfPresent(name, (k, v) -> value) == null) throw new IllegalArgumentException();
  }
  
  public static final class Def {
    
    final String name;
    final Value value;
    
    private Def(String name, Value value) {
      this.name = name;
      this.value = value;
    }
    
  }
  
}
