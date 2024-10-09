package parser.astNode;

import lexer.Token;
import utils.IoUtils;
import utils.Settings;

public class LAndExpNode implements Node {
    //  LAndExp → EqExp | LAndExp '&&' EqExp
    private EqExpNode eqExp;
    private Token operator;
    private LAndExpNode lAndExp;

    public LAndExpNode(EqExpNode eqExp, Token operator, LAndExpNode lAndExp) {
        this.eqExp = eqExp;
        this.operator = operator;
        this.lAndExp = lAndExp;
    }

    @Override
    public void print() {
        eqExp.print();
        IoUtils.writeFile(Settings.syntaxOutputPath, "<LAndExp>\n");
        if (lAndExp != null) {
            IoUtils.writeFile(Settings.syntaxOutputPath, operator.toString());
            lAndExp.print();
        }
    }
}
