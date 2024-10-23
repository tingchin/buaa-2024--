package parser.astNode;

import lexer.Token;
import utils.IOUtils;
import utils.Settings;

public class MulExpNode implements Node {
    // MulExp → UnaryExp | MulExp ('*' | '/' | '%') UnaryExp
    private UnaryExpNode unaryExp;
    private Token operator;
    private MulExpNode mulExp;

    public MulExpNode(UnaryExpNode unaryExp, Token operator, MulExpNode mulExp) {
        this.unaryExp = unaryExp;
        this.operator = operator;
        this.mulExp = mulExp;
    }

    public UnaryExpNode getUnaryExp() {
        return unaryExp;
    }

    public MulExpNode getMulExp() {
        return mulExp;
    }

    @Override
    public void print() {
        unaryExp.print();
        IOUtils.writeFile(Settings.syntaxOutputPath, "<MulExp>\n");
        if (operator != null) {
            IOUtils.writeFile(Settings.syntaxOutputPath, operator.toString());
            mulExp.print();
        }
    }
}
