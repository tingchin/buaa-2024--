package ir.instruction.Terminator;

import ir.BuildFactory;
import ir.instruction.Operator;
import ir.types.FunctionType;
import ir.types.IntegerType;
import ir.types.IrType;
import ir.types.VoidType;
import ir.values.BasicBlock;
import ir.values.Function;
import ir.values.Value;

import java.util.List;

public class Call extends Terminator {
    public Call(BasicBlock basicBlock, Function function, List<Value> args) {
        super(((FunctionType) function.getType()).getReturnType(), Operator.Call, basicBlock);
        this.addOperand(function);

        for (int i = 0; i < args.size(); i++) {
            IrType curType = args.get(i).getType();
            IrType realType = ((FunctionType) function.getType()).getParameters().get(i);
            Value tmp = convType(args.get(i), basicBlock, curType, realType);
            this.addOperand(tmp);
        }

        if (!(((FunctionType) function.getType()).getReturnType() instanceof VoidType)) {
            this.setName("%" + REGNUM++);
        }

        Function curFunction = basicBlock.getNode().getParent().getValue();
        function.addNext(curFunction);
        curFunction.addNext(function);
    }

    private Value convType(Value value, BasicBlock basicBlock, IrType curType, IrType realType) {
        boolean isCurI1 = curType instanceof IntegerType && ((IntegerType) curType).isI1();
        boolean isCurI32 = curType instanceof IntegerType && ((IntegerType) curType).isI32();
        boolean isRealI1 = realType instanceof IntegerType && ((IntegerType) realType).isI1();
        boolean isRealI32 = realType instanceof IntegerType && ((IntegerType) realType).isI32();
        boolean isCurI8 = curType instanceof IntegerType && ((IntegerType) curType).isI8();
        boolean isRealI8 = realType instanceof IntegerType && ((IntegerType) realType).isI8();
        if (!isCurI1 && !isCurI32 && !isRealI1 && !isRealI32 && !isCurI8 && !isRealI8) {
            return value;
        } else if ((isCurI1 && isRealI1) || (isCurI32 && isRealI32) || (isCurI8 && isRealI8)) {
            return value;
        } else if (isCurI1 && isRealI32) {
            return BuildFactory.getInstance().buildZext(value, basicBlock, IntegerType.i32);
        } else if (isCurI1 && isRealI8) {
            return BuildFactory.getInstance().buildZext(value, basicBlock, IntegerType.i8);
        } else if ((isCurI32 || isCurI8) && isRealI1) {
            return BuildFactory.getInstance().buildConvToI1(value, basicBlock);
        } else if (isCurI8 && isRealI32) {
            return BuildFactory.getInstance().buildZext(value, basicBlock, IntegerType.i32);
        } else if (isCurI32 && isRealI8) {
            return BuildFactory.getInstance().buildTrunc(value, basicBlock, IntegerType.i8);
        } else {
            return value;
        }
    }

    public Function getCalledFunction() {
        return (Function) this.getOperands().get(0);
    }

    @Override
    public String toString() {
       StringBuilder sb = new StringBuilder();
       IrType retType = ((FunctionType) this.getCalledFunction().getType()).getReturnType();
       if (retType instanceof VoidType) {
           sb.append("call ");
       } else {
           sb.append(this.getName()).append(" = call ");
       }
       sb.append(retType.toString()).append(" @").append(this.getCalledFunction().getName()).append("(");
       for (int i = 1; i < this.getOperands().size(); i++) {
           sb.append(this.getOperands().get(i).getType().toString()).append(" ").append(this.getOperands().get(i).getName());
           if (i != this.getOperands().size() - 1) {
               sb.append(", ");
           }
       }
       sb.append(")");
       return sb.toString();
    }
}
