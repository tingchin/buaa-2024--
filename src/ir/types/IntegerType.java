package ir.types;

public class IntegerType implements IrType{
    private final int bit;

    public static final IntegerType i1 = new IntegerType(1);

    public static final IntegerType i8 = new IntegerType(8);

    public static final IntegerType i32 = new IntegerType(32);

    public IntegerType(int bit) {
        this.bit = bit;
    }

    public boolean isI1() {
        return bit == 1;
    }

    public boolean isI8() {
        return bit == 8;
    }

    public boolean isI32() {
        return bit == 32;
    }

    @Override
    public String toString() {
        return "i" + bit;
    }
}
