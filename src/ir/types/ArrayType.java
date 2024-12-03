package ir.types;


import ir.values.ConstInt;
import ir.values.Value;

import java.util.ArrayList;
import java.util.List;

public class ArrayType implements IrType {
    private IrType elementType;
    private int length; // 只有一维数组，所以length = capacity

    public ArrayType(IrType elementType) {
        this.elementType = elementType;
        this.length = 0;
    }

    public ArrayType(IrType elementType, int length) {
        this.elementType = elementType;
        this.length = length;
    }

    public IrType getElementType() {
        return elementType;
    }

    public int getLength() {
        return length;
    }

    public boolean isIntArray() {
        return elementType instanceof IntegerType && ((IntegerType) elementType).isI32();
    }

    public boolean isString() {
        return elementType instanceof IntegerType && ((IntegerType) elementType).isI8();
    }

    @Override
    public String toString() {
        return "[" + length + " x " + elementType.toString() + "]";
    }

    public List<Value> offset2Index(int offset) {
        List<Value> index = new ArrayList<>();
        IrType type = this;
        while (type instanceof ArrayType) {
            index.add(new ConstInt(offset / ((ArrayType) type).getLength()));
            offset %= ((ArrayType) type).getLength();
            type = ((ArrayType) type).getElementType();
        }
        index.add(new ConstInt(offset));
        return index;
    }
}
