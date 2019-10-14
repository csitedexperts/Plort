package plort.core.value;

import plort.core.PlortException;

import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.Optional;

public abstract class Value {
  
  Value() {}
  
  public static <V extends Value, R> MatchCase<V, R> when(Class<V> vClass, MatchFn<? super V, R> f) {
    if (vClass == null || f == null) throw new NullPointerException();
    return new MatchCase<>(vClass, f);
  }
  
  public static <R> MatchCase<Value, R> otherwise(MatchFn<? super Value, R> f) {
    return when(Value.class, f);
  }
  
  abstract <R> R match(Matcher<R> match);
  
  @SafeVarargs
  public final <R> R match(MatchCase<?, R>... matches) {
    return match(new Matcher<>() {
        
        final IdentityHashMap<Class<?>, MatchFn<?, R>> matchMap = Arrays.stream(matches).collect(IdentityHashMap::new, (map, match) -> map.put(match.vClass, match.f), IdentityHashMap::putAll);
        
        @Override
        @SuppressWarnings("unchecked")
        public <V extends Value> R match(Class<V> vClass, V value) {
          var f = matchMap.get(vClass);
          if (f == null) {
            f = matchMap.get(Value.class);
            if (f == null) throw new PlortException("missing match case for " + vClass.getSimpleName());
          }
          return ((MatchFn<? super V, R>) f).match(value);
        }
        
    });
  }
  
  public final <V extends Value> Optional<V> as(Class<V> vClass) {
    return match(new Matcher<>() {
      
      @Override
      public <V2 extends Value> Optional<V> match(Class<V2> v2Class, V2 value) {
        return vClass == v2Class ? Optional.of(vClass.cast(value)) : Optional.empty();
      }
      
    });
  }
  
  public String stringValue() {
    return toString();
  }
  
  @Override
  public abstract String toString();
  
  @Override
  public abstract boolean equals(Object o);
  
  @Override
  public abstract int hashCode();
  
  static abstract class Matcher<R> {
    
    private Matcher() {}
    
    abstract <V extends Value> R match(Class<V> vClass, V value);
    
  }
  
  @FunctionalInterface
  public interface MatchFn<V extends Value, R> {
    
    R match(V value);
    
  }
  
  public static class MatchCase<V extends Value, R> {
    
    private final Class<V> vClass;
    private final MatchFn<? super V, R> f;
    
    private MatchCase(Class<V> vClass, MatchFn<? super V, R> f) {
      this.vClass = vClass;
      this.f = f;
    }
    
  }
  
}
