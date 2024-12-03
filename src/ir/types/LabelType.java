package ir.types;

public class LabelType implements IrType {
    private final int handler;
    private static int HANDLER = 0;

    public LabelType() {
        this.handler = HANDLER++;
    }

    @Override
    public String toString() {
        return "label_" + handler;
    }
}
