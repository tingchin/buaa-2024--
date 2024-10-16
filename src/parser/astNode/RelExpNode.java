package parser.astNode;

import lexer.Token;
import utils.IOUtils;
import utils.Settings;

public class RelExpNode implements Node {
    // RelExp → AddExp | RelExp ('<' | '>' | '<=' | '>=') AddExp
    private AddExpNode addExp;
    private Token operator;
    private RelExpNode relExp;

    public RelExpNode(AddExpNode addExp, Token operator, RelExpNode relExp) {
        this.addExp = addExp;
        this.operator = operator;
        this.relExp = relExp;
    }

    @Override
    public void print() {
        addExp.print();
        IOUtils.writeFile(Settings.syntaxOutputPath, "<RelExp>\n");
        if (operator != null) {
            IOUtils.writeFile(Settings.syntaxOutputPath, operator.toString());
            relExp.print();
        }
    }
}
