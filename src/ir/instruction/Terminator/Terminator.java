package ir.instruction.Terminator;

import ir.instruction.Instruction;
import ir.instruction.Operator;
import ir.types.IrType;
import ir.values.BasicBlock;

public abstract class Terminator extends Instruction {
    public Terminator(IrType type, Operator op, BasicBlock basicBlock) {
        super(type, op, basicBlock);
    }
}
