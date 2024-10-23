package parser.astNode;

import utils.IOUtils;
import utils.Settings;

public class ExpNode implements Node {
    private AddExpNode addExpNode;

    public ExpNode(AddExpNode addExpNode) {
        this.addExpNode = addExpNode;
    }

    public AddExpNode getAddExpNode() {
        return addExpNode;
    }

    @Override
    public void print() {
        addExpNode.print();
        IOUtils.writeFile(Settings.syntaxOutputPath, "<Exp>\n");
    }
}
