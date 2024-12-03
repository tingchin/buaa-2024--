package ir.instruction;

import ir.BuildFactory;
import ir.types.IntegerType;
import ir.types.VoidType;
import ir.values.BasicBlock;
import ir.values.Value;

public class Binary extends Instruction {
    public Binary(BasicBlock basicBlock, Operator op, Value lhs, Value rhs) {
        super(VoidType.voidType, op, basicBlock);

        boolean isLeftI1 = lhs.getType() instanceof IntegerType && ((IntegerType) lhs.getType()).isI1();
        boolean isRightI1 = rhs.getType() instanceof IntegerType && ((IntegerType) rhs.getType()).isI1();
        boolean isLeftI32 = lhs.getType() instanceof IntegerType && ((IntegerType) lhs.getType()).isI32();
        boolean isRightI32 = rhs.getType() instanceof IntegerType && ((IntegerType) rhs.getType()).isI32();
        boolean isLeftI8 = lhs.getType() instanceof  IntegerType && ((IntegerType) lhs.getType()).isI8();
        boolean isRightI8 = rhs.getType() instanceof IntegerType && ((IntegerType) rhs.getType()).isI8();
        if (isLeftI1 && isRightI32) {
            addOperands(BuildFactory.getInstance().buildZext(lhs, basicBlock, IntegerType.i32), rhs);
        } else if (isLeftI32 && isRightI1) {
            addOperands(lhs, BuildFactory.getInstance().buildZext(rhs, basicBlock, IntegerType.i32));
        } else if (isLeftI8 && isRightI32) {
            addOperands(BuildFactory.getInstance().buildZext(lhs, basicBlock, IntegerType.i32), rhs);
        } else if (isLeftI32 && isRightI8) {
            addOperands(lhs, BuildFactory.getInstance().buildZext(rhs, basicBlock, IntegerType.i32));
        } else if (isLeftI8 && isRightI8) {
            addOperands(BuildFactory.getInstance().buildZext(lhs, basicBlock, IntegerType.i32), BuildFactory.getInstance().buildZext(rhs, basicBlock, IntegerType.i32));
        } else {
            addOperands(lhs, rhs);
        }
        this.setType(this.getOperands().get(0).getType());
        if (isCond()) {
            this.setType(IntegerType.i1);
        }
        this.setName("%" + REGNUM++);
    }

    private void addOperands(Value left, Value right) {
        this.addOperand(left);
        this.addOperand(right);
    }

    public boolean isAdd() {
        return this.getOperator() == Operator.Add;
    }

    public boolean isSub() {
        return this.getOperator() == Operator.Sub;
    }

    public boolean isMul() {
        return this.getOperator() == Operator.Mul;
    }

    public boolean isDiv() {
        return this.getOperator() == Operator.Div;
    }

    public boolean isMod() {
        return this.getOperator() == Operator.Mod;
    }

    public boolean isShl() {
        return this.getOperator() == Operator.Shl;
    }

    public boolean isShr() {
        return this.getOperator() == Operator.Shr;
    }

    public boolean isAnd() {
        return this.getOperator() == Operator.And;
    }

    public boolean isOr() {
        return this.getOperator() == Operator.Or;
    }

    public boolean isLt() {
        return this.getOperator() == Operator.Lt;
    }

    public boolean isLe() {
        return this.getOperator() == Operator.Le;
    }

    public boolean isGe() {
        return this.getOperator() == Operator.Ge;
    }

    public boolean isGt() {
        return this.getOperator() == Operator.Gt;
    }

    public boolean isEq() {
        return this.getOperator() == Operator.Eq;
    }

    public boolean isNe() {
        return this.getOperator() == Operator.Ne;
    }

    public boolean isCond() {
        return this.isLt() || this.isLe() || this.isGe() || this.isGt() || this.isEq() || this.isNe();
    }

    public boolean isNot() {
        return this.getOperator() == Operator.Not;
    }

    @Override
    public String toString() {
        String s = getName() + " = ";
        switch (this.getOperator()) {
            case Add:
                s += "add i32 ";
                break;
            case Sub:
                s += "sub i32 ";
                break;
            case Mul:
                s += "mul i32 ";
                break;
            case Div:
                s += "sdiv i32 ";
                break;
            case Mod:
                s += "srem i32 ";
                break;
            case Shl:
                s += "shl i32 ";
                break;
            case Shr:
                s += "ashr i32 ";
                break;
            case And:
                s += "and " + this.getOperands().get(0).getType().toString() + " ";
                break;
            case Or:
                s += "or " + this.getOperands().get(0).getType().toString() + " ";
                break;
            case Lt:
                s += "icmp slt " + this.getOperands().get(0).getType().toString() + " ";
                break;
            case Le:
                s += "icmp sle " + this.getOperands().get(0).getType().toString() + " ";
                break;
            case Ge:
                s += "icmp sge " + this.getOperands().get(0).getType().toString() + " ";
                break;
            case Gt:
                s += "icmp sgt " + this.getOperands().get(0).getType().toString() + " ";
                break;
            case Eq:
                s += "icmp eq " + this.getOperands().get(0).getType().toString() + " ";
                break;
            case Ne:
                s += "icmp ne " + this.getOperands().get(0).getType().toString() + " ";
                break;
            default:
                break;
        }
        return s + this.getOperands().get(0).getName() + ", " + this.getOperands().get(1).getName();
    }

}
