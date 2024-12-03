package ir.types;

public class VoidType implements IrType {
    public static final VoidType voidType = new VoidType();

    @Override
    public String toString() {
        return "void";
    }
}
