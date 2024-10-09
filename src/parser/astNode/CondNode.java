package parser.astNode;

import utils.IoUtils;
import utils.Settings;

public class CondNode implements Node {
    private LOrExpNode lOrExp;

    public CondNode(LOrExpNode lOrExp) {
        this.lOrExp = lOrExp;
    }

    @Override
    public void print() {
        lOrExp.print();
        IoUtils.writeFile(Settings.syntaxOutputPath, "<Cond>\n");
    }
}
