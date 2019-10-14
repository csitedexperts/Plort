package plort.core.ast;

import plort.core.PlortException;
import plort.core.value.*;

import static plort.core.value.Value.otherwise;
import static plort.core.value.Value.when;

@FunctionalInterface
public interface UnOpBlob extends AST.Blob {
  
  UnOpBlob
    ADD = operand -> operand.as(NumberValue.class).orElseThrow(() -> new PlortException("only numbers can be positive")),
    SUB = operand -> operand.as(NumberValue.class).map(number -> NumberValue.of(-number.value)).orElseThrow(() -> new PlortException("only numbers can be negative")),
    NOT = operand -> operand.as(BoolValue.class).map(bool -> BoolValue.of(!bool.value)).orElseThrow(() -> new PlortException("only bools can be logically not'd")),
    ABS = operand -> NumberValue.of(operand.match(
      when(MapValue.class, map -> (double) map.values.size()),
      when(ListValue.class, list -> (double) list.values.size()),
      when(StringValue.class, string -> (double) string.value.length()),
      when(NumberValue.class, number -> Math.abs(number.value)),
      otherwise(value -> new PlortException("invalid type for magnitude").throwExpr())
    ));
  
  Value operate(Value operand);
  
}
