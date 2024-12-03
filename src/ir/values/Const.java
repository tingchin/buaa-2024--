package ir.values;

import ir.types.IrType;

public abstract class Const extends Value {
    public Const(String name, IrType type) {
        super(name, type);
    }
}
