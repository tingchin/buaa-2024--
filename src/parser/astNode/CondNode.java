package parser.astNode;

import utils.IOUtils;
import utils.Settings;

public class CondNode implements Node {
    private LOrExpNode lOrExp;

    public CondNode(LOrExpNode lOrExp) {
        this.lOrExp = lOrExp;
    }

    public LOrExpNode getlOrExp() {
        return lOrExp;
    }

    @Override
    public void print() {
        lOrExp.print();
        IOUtils.writeFile(Settings.syntaxOutputPath, "<Cond>\n");
    }
}
