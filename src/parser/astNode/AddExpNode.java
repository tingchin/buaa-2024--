package parser.astNode;

import lexer.Token;
import utils.IOUtils;
import utils.Settings;

public class AddExpNode implements Node {
    // AddExp → MulExp | AddExp ('+' | '−') MulExp
    private MulExpNode mulExp;
    private Token operator;
    private AddExpNode addExp;

    public MulExpNode getMulExp() {
        return mulExp;
    }

    public AddExpNode getAddExp() {
        return addExp;
    }

    public AddExpNode(MulExpNode mulExp, Token operator, AddExpNode addExp) {
        this.mulExp = mulExp;
        this.operator = operator;
        this.addExp = addExp;
    }

    @Override
    public void print() {
        mulExp.print();
        IOUtils.writeFile(Settings.syntaxOutputPath, "<AddExp>\n");
        if (operator != null) {
            IOUtils.writeFile(Settings.syntaxOutputPath, operator.toString());
            addExp.print();
        }
    }
}
