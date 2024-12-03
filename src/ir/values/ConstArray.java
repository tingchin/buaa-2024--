package ir.values;

import ir.types.ArrayType;
import ir.types.IntegerType;
import ir.types.*;

import java.util.ArrayList;
import java.util.List;

public class ConstArray extends Const {
    private IrType elementType;
    private List<Value> array;
    private int capacity;
    private boolean init = false;

    public ConstArray(IrType type, IrType elementType, int capacity) {
        super("", type);
        this.elementType = elementType;
        this.capacity = capacity;
        this.array = new ArrayList<Value>();
        for (int i = 0; i < ((ArrayType) type).getLength(); i++) {
            if (((IntegerType) elementType).isI32()) {
                array.add(ConstInt.ZERO);
            } else {
                array.add(ConstChar.ZERO);
            }

        }
    }

    public IrType getElementType() {
        return elementType;
    }

    public void setElementType(IrType elementType) {
        this.elementType = elementType;
    }

    public List<Value> getArray() {
        return array;
    }

    public void setArray(List<Value> array) {
        this.array = array;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public boolean isInit() {
        return init || !allZero();
    }

    public void setInit(boolean init) {
        this.init = init;
    }

    public List<Value> get1DArray() {
        List<Value> result = new ArrayList<>();
        for (Value value : array) {
            if (value instanceof ConstArray) {
                result.addAll(((ConstArray) value).get1DArray());
            } else {
                result.add(value);
            }
        }
        return result;
    }

    public void storeValue(int offset, Value value) {
        array.set(offset, value);
    }

    public boolean allZero() {
        for (Value value : array) {
            if (value instanceof ConstInt) {
                if (((ConstInt) value).getValue() != 0) {
                    return false;
                }
            } else if (value instanceof ConstChar) {
              if (((ConstChar) value).getValue() != 0) {
                  return false;
              }
            } else {
                if (!((ConstArray) value).allZero()) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public String toString() {
        if (allZero()) {
            return this.getType().toString() + " " + "zeroinitializer";
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append(this.getType().toString()).append(" ").append("[");
            for (int i = 0; i < array.size(); i++) {
                if (i != 0) {
                    sb.append(", ");
                }
                sb.append(array.get(i).toString());
            }
            sb.append("]");
            return sb.toString();
        }
    }


}
