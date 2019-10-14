package plort.core.value;

public final class StringValue extends Value {
  
  public static final StringValue EMTPY = new StringValue("");
  
  public final String value;
  
  private StringValue(String value) {
    this.value = value;
  }
  
  public static StringValue of(String value) {
    return value == null || value.isEmpty() ? EMTPY : new StringValue(value);
  }
  
  @Override
  <R> R match(Matcher<R> matcher) {
    return matcher.match(StringValue.class,this);
  }
  
  @Override
  public String stringValue() {
    return value;
  }
  
  @Override
  public String toString() {
    return '"' + value.replace("\"", "\\\"") + '"';
  }
  
  @Override
  public boolean equals(Object o) {
    return o instanceof StringValue && value.equals(((StringValue) o).value);
  }
  
  @Override
  public int hashCode() {
    return value.hashCode();
  }
  
}
