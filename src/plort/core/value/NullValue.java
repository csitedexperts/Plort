package plort.core.value;

public final class NullValue extends Value {
  
  public static final NullValue INSTANCE = new NullValue();
  
  private NullValue() {}
  
  @Override
  <R> R match(Matcher<R> matcher) {
    return matcher.match(NullValue.class, this);
  }
  
  @Override
  public String toString() {
    return "null";
  }
  
  @Override
  public boolean equals(Object o) {
    return this == o;
  }
  
  @Override
  public int hashCode() {
    return 0;
  }
  
}
