package ir.values;

import ir.Module;
import ir.types.IrType;
import ir.types.PointerType;

import java.util.ArrayList;
import java.util.List;

public class GlobalVar extends User {
    private Value value;
    private boolean isConst;

    public GlobalVar(String name, IrType type, boolean isConst, Value value) {
        super("@" + name, new PointerType(type));
        this.value = value;
        this.isConst = isConst;
        Module.getInstance().addGlobalVar(this);

    }

    public boolean isConst() {
        return isConst;
    }

    public void setConst(boolean aConst) {
        isConst = aConst;
    }

    public Value getValue() {
        return value;
    }

    public void setValue(Value value) {
        this.value = value;
    }

    public boolean isString() {
        return value instanceof ConstString;
    }

    public boolean isInt() {
        return value instanceof ConstInt;
    }

    public boolean isArray() {
        return value instanceof ConstArray;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getName()).append(" = ");
        if (isConst) {
            sb.append("constant ");
        } else {
            sb.append("dso_local global ");
        }
        if (value != null) {
            sb.append(value);
        }
        return sb.toString();
    }


}
