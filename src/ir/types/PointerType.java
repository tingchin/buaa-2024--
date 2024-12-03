package ir.types;

public class PointerType implements IrType {
    private IrType targetType;

    public PointerType(IrType targetType) {
        this.targetType = targetType;
    }

    public IrType getTargetType() {
        return targetType;
    }

    public boolean isString() {
        return targetType instanceof ArrayType && ((ArrayType) targetType).isString();
    }

    @Override
    public String toString() {
        return targetType.toString() + "*";
    }
}
