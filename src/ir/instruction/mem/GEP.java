package ir.instruction.mem;

import ir.instruction.Operator;

import ir.types.*;

import ir.values.*;

import java.util.ArrayList;


import java.util.List;

public class GEP extends MemInst {
    private IrType elementType;
    private Value target;

    public GEP(BasicBlock basicBlock, Value pointer, List<Value> indices) {
        super(new PointerType(getElementType(pointer, indices)), Operator.GEP, basicBlock);
        this.setName("%" + REGNUM++);
        if (pointer instanceof GEP) {
            target = ((GEP) pointer).target;
        } else if (pointer instanceof Alloca) {
            target = pointer;
        } else if (pointer instanceof GlobalVar) {
            target = pointer;
        }
        this.addOperand(pointer);
        for (Value value : indices) {
            this.addOperand(value);
        }
        this.elementType = getElementType(pointer, indices);
    }

    public GEP(BasicBlock basicBlock, Value pointer, int offset) {
        this(basicBlock, pointer, ((ArrayType) ((PointerType) pointer.getType()).getTargetType()).offset2Index(offset));
    }

    public Value getPointer() {
        return getOperands().get(0);
    }

    public IrType getElementType() {
        return elementType;
    }

    private static IrType getElementType(Value pointer, List<Value> indices) {
        IrType type = pointer.getType();
        for (Value ignored : indices) {
            if (type instanceof ArrayType) {
                type = ((ArrayType) type).getElementType();
            } else if (type instanceof PointerType) {
                type = ((PointerType) type).getTargetType();
            } else {
                break;
            }
        }
        return type;
    }

    public List<Integer> getGEPIndex() {
        List<Integer> index = new ArrayList<>();
        for (int i = 1; i < getOperands().size(); i++) {
            index.add(((ConstInt) getOperand(i)).getValue());
        }
        return index;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(getName()).append(" = getelementptr ");
        // 如果是字符串，需要加 inbounds
        if (getPointer().getType() instanceof PointerType && ((PointerType) getPointer().getType()).isString()) {
            s.append("inbounds ");
        }
        s.append(((PointerType) getPointer().getType()).getTargetType()).append(", ");
        for (int i = 0; i < getOperands().size(); i++) {
            if (i == 0) {
                s.append(getPointer().getType()).append(" ").append(getPointer().getName());
            } else {
                s.append(", ").append(getOperands().get(i).getType()).append(" ").append(getOperands().get(i).getName());
            }
        }
        return s.toString();
    }
}
