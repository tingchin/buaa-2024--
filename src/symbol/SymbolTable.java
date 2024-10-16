package symbol;

import utils.IOUtils;
import utils.Settings;

import java.util.*;

public class SymbolTable {
    private SymbolTable lastLayer;
    private LinkedHashMap<String, Symbol> symbolTable;
    private int layer;
    private List<SymbolTable> nextLayer;

    public SymbolTable() {
        this.lastLayer = null;
        this.symbolTable = new LinkedHashMap<String, Symbol>();
        this.nextLayer = new ArrayList<SymbolTable>();
    }

    public SymbolTable(SymbolTable lastLayer, LinkedHashMap<String, Symbol> symbolTable, int layer, List<SymbolTable> nextLayer) {
        this.lastLayer = lastLayer;
        this.symbolTable = symbolTable;
        this.nextLayer = nextLayer;
        this.layer = layer;
    }

    public int getLayer() {
        return layer;
    }

    public void setLayer(int layer) {
        this.layer = layer;
    }

    public SymbolTable getLastLayer() {
        return lastLayer;
    }

    public void setLastLayer(SymbolTable lastLayer) {
        this.lastLayer = lastLayer;
    }

    public HashMap<String, Symbol> getSymbolTable() {
        return symbolTable;
    }

    public void setSymbolTable(LinkedHashMap<String, Symbol> symbolTable) {
        this.symbolTable = symbolTable;
    }

    public List<SymbolTable> getNextLayer() {
        return nextLayer;
    }

    public void setNextLayer(List<SymbolTable> nextLayer) {
        this.nextLayer = nextLayer;
    }

    public void print() {
        Set<Map.Entry<String, Symbol>> entries = symbolTable.entrySet();
        for (Map.Entry<String, Symbol> entry : entries) {
            String content = layer + " " + entry.getKey() + " " + entry.getValue().getType().toString() + "\n";
            IOUtils.writeFile(Settings.symbolOutputPath, content);
        }
        if (nextLayer != null) {
            for (SymbolTable table : nextLayer) {
                table.print();
            }
        }
    }
}
