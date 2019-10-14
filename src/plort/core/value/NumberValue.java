package plort.core.value;

import static java.lang.Math.round;

public final class NumberValue extends Value {
  
  public static final NumberValue
    ZERO = new NumberValue(0),
    ONE = new NumberValue(1),
    NaN = new NumberValue(Double.NaN),
    INFINITY = new NumberValue(Double.POSITIVE_INFINITY),
    N_INFINITY = new NumberValue(Double.NEGATIVE_INFINITY);
  
  public final double value;
  
  private NumberValue(double value) {
    this.value = value;
  }
  
  public static NumberValue of(double value) {
    return
      value == 0 ? ZERO :
      value == 1 ? ONE :
      Double.isNaN(value) ? NaN :
      value == Double.POSITIVE_INFINITY ? INFINITY :
      value == Double.NEGATIVE_INFINITY ? N_INFINITY :
      new NumberValue(value);
  }
  
  @Override
  <R> R match(Matcher<R> matcher) {
    return matcher.match(NumberValue.class, this);
  }
  
  @Override
  public String toString() {
    return round(value) == value ? String.valueOf(round(value)) : String.valueOf(value);
  }
  
  @Override
  public boolean equals(Object o) {
    return o instanceof NumberValue && value == ((NumberValue) o).value;
  }
  
  @Override
  public int hashCode() {
    return (int) value;
  }
  
}
