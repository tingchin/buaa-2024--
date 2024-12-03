package ir.instruction.mem;

import ir.instruction.Instruction;
import ir.instruction.Operator;
import ir.types.IrType;
import ir.values.BasicBlock;

public abstract class MemInst extends Instruction {
    public MemInst(IrType type, Operator op, BasicBlock block) {
        super(type, op, block);
    }
}
