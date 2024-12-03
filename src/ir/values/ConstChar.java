package ir.values;

import ir.types.IntegerType;

import java.util.Objects;

public class ConstChar extends Const {
    private int value;
    public static ConstChar ZERO = new ConstChar(0);

    public ConstChar() {
        super("", IntegerType.i8);
        this.value = 0;
    }

    public ConstChar(int value) {
        super(String.valueOf(value), IntegerType.i8);
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConstChar that = (ConstChar) o;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "i8 " + this.value;
    }
}
