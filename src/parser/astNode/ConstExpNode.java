package parser.astNode;

import utils.IOUtils;
import utils.Settings;

public class ConstExpNode implements Node {
    // ConstExp → AddExp
    private AddExpNode addExp;

    @Override
    public void print() {
        addExp.print();
        IOUtils.writeFile(Settings.syntaxOutputPath, "<ConstExp>\n");
    }

    public ConstExpNode(AddExpNode addExp) {
        this.addExp = addExp;
    }
}
