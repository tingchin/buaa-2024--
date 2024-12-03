package ir.instruction.mem;


import ir.instruction.Operator;
import ir.types.ArrayType;
import ir.types.IrType;
import ir.types.PointerType;
import ir.values.BasicBlock;

public class Alloca extends MemInst {
    private boolean isConst;
    private IrType allocaType;

    public Alloca(BasicBlock basicBlock, boolean isConst, IrType allocaType) {
        super(new PointerType(allocaType), Operator.Alloca, basicBlock);
        this.isConst = isConst;
        this.allocaType = allocaType;
        this.setName("%" + REGNUM++);
        if (allocaType instanceof ArrayType) {
            if (((ArrayType) allocaType).getLength() == -1) {
                this.allocaType = new PointerType(((ArrayType) allocaType).getElementType());
                setType(new PointerType(this.allocaType));
            }
        }
    }


    public IrType getAllocaType() {
        return allocaType;
    }


    public void setAllocaType(IrType type) {
        this.allocaType = type;
    }

    public boolean isConst() {
        return isConst;
    }

    public void setConst(boolean aConst) {
        isConst = aConst;
    }

    @Override
    public String toString() {
        return this.getName() + " = alloca " + this.getAllocaType();
    }
}
