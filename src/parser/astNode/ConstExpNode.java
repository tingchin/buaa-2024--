package parser.astNode;

import utils.IoUtils;
import utils.Settings;

public class ConstExpNode implements Node {
    // ConstExp → AddExp
    private AddExpNode addExp;

    @Override
    public void print() {
        addExp.print();
        IoUtils.writeFile(Settings.syntaxOutputPath, "<ConstExp>\n");
    }

    public ConstExpNode(AddExpNode addExp) {
        this.addExp = addExp;
    }
}
