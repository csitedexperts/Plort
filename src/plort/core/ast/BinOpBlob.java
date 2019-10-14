package plort.core.ast;

import plort.core.PlortException;
import plort.core.value.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Supplier;

import static plort.core.value.Value.otherwise;
import static plort.core.value.Value.when;

@FunctionalInterface
public interface BinOpBlob extends AST.Blob {
  
  BinOpBlob
    OR = (left, right) -> BoolValue.of(left.get().as(BoolValue.class).orElseThrow(() -> new PlortException("only bools can be logically or'd")).value || right.get().as(BoolValue.class).orElseThrow(() -> new PlortException("only bools can be logically or'd")).value),
    AND = (left, right) -> BoolValue.of(left.get().as(BoolValue.class).orElseThrow(() -> new PlortException("only bools can be logically and'd")).value && right.get().as(BoolValue.class).orElseThrow(() -> new PlortException("only bools can be logically and'd")).value),
    EQ = (left, right) -> BoolValue.of(left.get().equals(right.get())),
    LT = (left, right) -> BoolValue.of(left.get().as(NumberValue.class).orElseThrow(() -> new PlortException("only numbers can be compared")).value < right.get().as(NumberValue.class).orElseThrow(() -> new PlortException("only numbers can be compared")).value),
    GT = (left, right) -> BoolValue.of(left.get().as(NumberValue.class).orElseThrow(() -> new PlortException("only numbers can be compared")).value > right.get().as(NumberValue.class).orElseThrow(() -> new PlortException("only numbers can be compared")).value),
    LTE = (left, right) -> BoolValue.of(left.get().as(NumberValue.class).orElseThrow(() -> new PlortException("only numbers can be compared")).value <= right.get().as(NumberValue.class).orElseThrow(() -> new PlortException("only numbers can be compared")).value),
    GTE = (left, right) -> BoolValue.of(left.get().as(NumberValue.class).orElseThrow(() -> new PlortException("only numbers can be compared")).value >= right.get().as(NumberValue.class).orElseThrow(() -> new PlortException("only numbers can be compared")).value),
    CMP = (left, right) -> NumberValue.of(Math.signum(left.get().as(NumberValue.class).orElseThrow(() -> new PlortException("only numbers can be compared")).value - right.get().as(NumberValue.class).orElseThrow(() -> new PlortException("only numbers can be compared")).value)),
    ADD = (left, right) -> left.get().match(
      when(MapValue.class, map -> right.get().match(
        when(MapValue.class, other -> {
          var values = new HashMap<>(map.values);
          values.putAll(other.values);
          return MapValue.of(values);
        }),
        when(StringValue.class, string -> StringValue.of(map.stringValue() + string.value)),
        otherwise(value -> new PlortException("invalid addition types").throwExpr())
      )),
      when(ListValue.class, list -> right.get().match(
        when(ListValue.class, other -> {
          var values = new ArrayList<>(list.values);
          values.addAll(other.values);
          return ListValue.of(values);
        }),
        when(StringValue.class, string -> StringValue.of(list.stringValue() + string.value)),
        otherwise(value -> new PlortException("invalid addition types").throwExpr())
      )),
      when(StringValue.class, string -> StringValue.of(string.value + right.get().stringValue())),
      when(NumberValue.class, number -> right.get().match(
        when(NumberValue.class, other -> NumberValue.of(number.value + other.value)),
        when(StringValue.class, string -> StringValue.of(number.stringValue() + string.value)),
        otherwise(value -> new PlortException("invalid addition types").throwExpr())
      )),
      otherwise(value -> right.get().as(StringValue.class).map(string -> StringValue.of(value.stringValue() + string.value)).orElseThrow(() -> new PlortException("invalid addition types")))
    ),
    SUB = (left, right) -> NumberValue.of(left.get().as(NumberValue.class).orElseThrow(() -> new PlortException("invalid subtraction types")).value - right.get().as(NumberValue.class).orElseThrow(() -> new PlortException("invalid subtraction types")).value),
    MUL = (left, right) -> left.get().match(
      when(NumberValue.class, number -> right.get().match(
        when(NumberValue.class, other -> NumberValue.of(number.value * other.value)),
        when(BoolValue.class, bool -> bool.value ? number : bool),
        when(NullValue.class, nul -> nul),
        otherwise(value -> new PlortException("invalid multiplication types").throwExpr())
      )),
      when(BoolValue.class, bool -> bool.value ? right.get() : bool),
      when(NullValue.class, nul -> nul),
      otherwise(value -> right.get().match(
        when(BoolValue.class, bool -> bool.value ? right.get() : bool),
        when(NullValue.class, nul -> nul),
        otherwise(other -> new PlortException("invalid multiplication types").throwExpr())
      ))
    ),
    DIV = (left, right) -> NumberValue.of(left.get().as(NumberValue.class).orElseThrow(() -> new PlortException("invalid division types")).value / right.get().as(NumberValue.class).orElseThrow(() -> new PlortException("invalid division types")).value),
    MOD = (left, right) -> NumberValue.of(left.get().as(NumberValue.class).orElseThrow(() -> new PlortException("invalid modulo types")).value % right.get().as(NumberValue.class).orElseThrow(() -> new PlortException("invalid modulo types")).value);
  
  Value operate(Supplier<Value> left, Supplier<Value> right);
  
}
