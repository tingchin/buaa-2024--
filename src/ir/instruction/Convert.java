package ir.instruction;

import ir.types.*;
import ir.values.BasicBlock;
import ir.values.Value;

public class Convert extends Instruction {
    private IrType target;
    public Convert(BasicBlock basicBlock, Operator op, Value value, IrType target) {
        super(VoidType.voidType, op, basicBlock);
        this.target = target;
        this.setName("%" + REGNUM++);
        if (op == Operator.Bitcast) {
            setType(new PointerType(IntegerType.i32));
        } else {
            setType(target);
        }
        addOperand(value);
    }


    @Override
    public String toString() {
        Value source = getOperands().get(0);
        IrType sourceType = source.getType();
        if (getOperator() == Operator.Zext) {
            return getName() + " = zext " + sourceType + " " + source.getName() + " to " + target;
        } else if (getOperator() == Operator.Bitcast) {
            return getName() + " = bitcast " + sourceType + " " + source.getName() + " to " + target;
        } else {
            return getName() + " = trunc " + sourceType + " " + source.getName() + " to " + target;
        }
    }
}
