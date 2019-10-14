package plort.core.value;

public final class BoolValue extends Value {
  
  public static final BoolValue TRUE = new BoolValue(true), FALSE = new BoolValue(false);
  
  public final boolean value;
  
  private BoolValue(boolean value) {
    this.value = value;
  }
  
  public static BoolValue of(boolean value) {
    return value ? BoolValue.TRUE : BoolValue.FALSE;
  }
  
  @Override
  <R> R match(Matcher<R> matcher) {
    return matcher.match(BoolValue.class, this);
  }
  
  @Override
  public String toString() {
    return String.valueOf(value);
  }
  
  @Override
  public boolean equals(Object o) {
    return o instanceof BoolValue && value == ((BoolValue) o).value;
  }
  
  @Override
  public int hashCode() {
    return value ? 1 : 0;
  }
  
}
