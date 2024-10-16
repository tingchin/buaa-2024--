package symbol;

public abstract class Symbol {
    private SymbolType type;
    private String name;
    private int layer;

    public Symbol(SymbolType type, String name, int layer) {
        this.type = type;
        this.name = name;
        this.layer = layer;
    }

    public SymbolType getType() {
        return type;
    }
}
