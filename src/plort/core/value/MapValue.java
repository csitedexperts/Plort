package plort.core.value;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toMap;

public final class MapValue extends Value {
  
  public static final MapValue EMPTY = new MapValue(Map.of());
  
  public final Map<Value, Value> values;
  
  private MapValue(Map<Value, Value> values) {
    this.values = values;
  }
  
  public static MapValue of(Map<Value, Value> values) {
    return values == null || values.isEmpty() ? EMPTY : new MapValue(Map.copyOf(values));
  }
  
  public static MapValue of(List<Entry> values) {
    return values == null || values.isEmpty() ? EMPTY : new MapValue(values.stream().collect(toMap(entry -> entry.key, entry -> entry.value, (l, r) -> r)));
  }
  
  public static MapValue of(Entry... values) {
    return values == null || values.length == 0 ? EMPTY : new MapValue(Arrays.stream(values).collect(toMap(entry -> entry.key, entry -> entry.value, (l, r) -> r)));
  }
  
  public static Entry entry(Value key, Value value) {
    return new Entry(key, value);
  }
  
  @Override
  <R> R match(Matcher<R> matcher) {
    return matcher.match(MapValue.class,this);
  }
  
  @Override
  public String toString() {
    return values.entrySet().stream().map(entry -> entry.getKey() + ": " + entry.getValue()).collect(joining(", ", "{", "}"));
  }
  
  @Override
  public boolean equals(Object o) {
    return o instanceof MapValue && values.equals(((MapValue) o).values);
  }
  
  @Override
  public int hashCode() {
    return values.hashCode();
  }
  
  public static final class Entry {
    
    public final Value key, value;
    
    private Entry(Value key, Value value) {
      if (key == null || value == null) throw new NullPointerException();
      this.key = key;
      this.value = value;
    }
    
  }
  
}
