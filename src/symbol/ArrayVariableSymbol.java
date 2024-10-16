package symbol;

public class ArrayVariableSymbol extends Symbol{
    int arraySize;

    public ArrayVariableSymbol(SymbolType type, String name, int layer) {
        super(type, name, layer);
    }
}
