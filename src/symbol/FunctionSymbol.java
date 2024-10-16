package symbol;

import java.util.List;

public class FunctionSymbol extends Symbol {

    private List<FunctionParam> params;

    public FunctionSymbol(SymbolType type, String name, int layer) {
        super(type, name, layer);
    }

    public void setParams(List<FunctionParam> params) {
        this.params = params;
    }
}
