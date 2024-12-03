package ir.instruction.mem;

import ir.instruction.Operator;
import ir.types.ArrayType;
import ir.types.PointerType;
import ir.values.BasicBlock;
import ir.values.Value;

public class Load extends MemInst {
    public Load(BasicBlock basicBlock, Value pointer) {
        super(((PointerType) pointer.getType()).getTargetType(), Operator.Load, basicBlock);
        this.setName("%" + REGNUM++);
        if (getType() instanceof ArrayType) {
            setType(new PointerType(((ArrayType) getType()).getElementType()));
        }
        this.addOperand(pointer);
    }

    public Value getPointer() {
        return getOperands().get(0);
    }

    public Value getIndex() {
        return getOperands().get(1);
    }

    @Override
    public String toString() {
        return getName() + " = load " + getType() + ", " + getPointer().getType() + " " + getPointer().getName();
    }
}
