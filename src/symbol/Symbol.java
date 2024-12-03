package symbol;

import ir.values.Value;

public abstract class Symbol {
    private SymbolType type;
    private String name;
    private int layer;
    private Value targetValue;

    public Symbol(SymbolType type, String name, int layer) {
        this.type = type;
        this.name = name;
        this.layer = layer;
    }

    public Value getTargetValue() {
        return targetValue;
    }

    public void setTargetValue(Value targetValue) {
        this.targetValue = targetValue;
    }

    public SymbolType getType() {
        return type;
    }
}
