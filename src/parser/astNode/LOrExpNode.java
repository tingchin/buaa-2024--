package parser.astNode;

import lexer.Token;
import utils.IoUtils;
import utils.Settings;

public class LOrExpNode implements Node {
    // LOrExp → LAndExp | LOrExp '||' LAndExp
    private LAndExpNode lAndExpNode;
    private Token operator;
    private LOrExpNode lOrExpNode;

    public LOrExpNode(LAndExpNode lAndExpNode, Token operator, LOrExpNode lOrExpNode) {
        this.lAndExpNode = lAndExpNode;
        this.operator = operator;
        this.lOrExpNode = lOrExpNode;
    }

    @Override
    public void print() {
        lAndExpNode.print();
        IoUtils.writeFile(Settings.syntaxOutputPath, "<LOrExp>\n");
        if (operator != null) {
            IoUtils.writeFile(Settings.syntaxOutputPath, operator.toString());
            lOrExpNode.print();
        }
    }
}
