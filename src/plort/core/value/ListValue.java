package plort.core.value;

import java.util.List;

import static java.util.stream.Collectors.joining;

public final class ListValue extends Value {
  
  public static final ListValue EMPTY = new ListValue(List.of());
  
  public final List<Value> values;
  
  private ListValue(List<Value> values) {
    this.values = values;
  }
  
  public static ListValue of(List<Value> values) {
    return values == null || values.isEmpty() ? EMPTY : new ListValue(List.copyOf(values));
  }
  
  public static ListValue of(Value... values) {
    return values == null || values.length == 0 ? EMPTY : new ListValue(List.of(values));
  }
  
  @Override
  <R> R match(Matcher<R> matcher) {
    return matcher.match(ListValue.class, this);
  }
  
  @Override
  public String toString() {
    return values.stream().map(Value::toString).collect(joining(", ", "[", "]"));
  }
  
  @Override
  public boolean equals(Object o) {
    return o instanceof ListValue && values.equals(((ListValue) o).values);
  }
  
  @Override
  public int hashCode() {
    return values.hashCode();
  }
  
}
