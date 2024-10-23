package parser.astNode;

import lexer.Token;
import utils.IOUtils;
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

    public EqExpNode getEqExp() {
        return eqExp;
    }

    public Token getOperator() {
        return operator;
    }

    public LAndExpNode getlAndExp() {
        return lAndExp;
    }

    @Override
    public void print() {
        eqExp.print();
        IOUtils.writeFile(Settings.syntaxOutputPath, "<LAndExp>\n");
        if (lAndExp != null) {
            IOUtils.writeFile(Settings.syntaxOutputPath, operator.toString());
            lAndExp.print();
        }
    }
}
