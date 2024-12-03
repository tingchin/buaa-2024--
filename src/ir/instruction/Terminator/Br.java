package ir.instruction.Terminator;

import ir.BuildFactory;
import ir.instruction.Operator;
import ir.types.IntegerType;
import ir.types.VoidType;
import ir.values.BasicBlock;
import ir.values.ConstInt;
import ir.values.Value;

public class Br extends Terminator {
    public Br(BasicBlock basicBlock, BasicBlock target) {
        super(VoidType.voidType, Operator.Br, basicBlock);
        this.addOperand(target);
        if (basicBlock != null) {
            if (basicBlock.getInstructions().getTail() == null ||
                    (!(basicBlock.getInstructions().getTail().getValue() instanceof Br) &&
                            !(basicBlock.getInstructions().getTail().getValue() instanceof Ret))) {
                basicBlock.addNext(target);
                target.addPrev(basicBlock);
            }
        }
    }

    public Br(BasicBlock basicBlock, Value cond, BasicBlock trueBlock, BasicBlock falseBlock) {
        super(VoidType.voidType, Operator.Br, basicBlock);
        // conversion handler
        Value condTmp = cond;
        if (!(cond.getType() instanceof IntegerType && ((IntegerType) cond.getType()).isI1())) {
            condTmp = BuildFactory.getInstance().buildBinary(basicBlock, Operator.Ne, cond, new ConstInt(0));
        }
        this.addOperand(condTmp);
        this.addOperand(trueBlock);
        this.addOperand(falseBlock);
        // 添加前驱后继
        if (basicBlock.getInstructions().getTail() == null ||
                (!(basicBlock.getInstructions().getTail().getValue() instanceof Br) &&
                        !(basicBlock.getInstructions().getTail().getValue() instanceof Ret))) {
            basicBlock.addNext(trueBlock);
            basicBlock.addNext(falseBlock);
            trueBlock.addPrev(basicBlock);
            falseBlock.addPrev(basicBlock);
        }
    }

    public Value getTarget() {
        return this.getOperand(0);
    }

    @Override
    public String toString() {
        if (this.getOperands().size() == 1) {
            return "br label %" + this.getOperands().get(0).getName();
        } else {
            return "br " + this.getOperands().get(0).getType() + " " + this.getOperands().get(0).getName() + ", label %" + this.getOperands().get(1).getName() + ", label %" + this.getOperands().get(2).getName();
        }
    }
}
