package parser.astNode;

import lexer.Token;
import utils.IOUtils;
import utils.Settings;

public class EqExpNode implements Node {
    // EqExp → RelExp | EqExp ('==' | '!=') RelExp
    private RelExpNode relExpNode;
    private Token operator;
    private EqExpNode eqExpNode;

    public EqExpNode(RelExpNode relExpNode, Token operator, EqExpNode eqExpNode) {
        this.relExpNode = relExpNode;
        this.operator = operator;
        this.eqExpNode = eqExpNode;
    }

    public RelExpNode getRelExpNode() {
        return relExpNode;
    }

    public Token getOperator() {
        return operator;
    }

    public EqExpNode getEqExpNode() {
        return eqExpNode;
    }

    @Override
    public void print() {
        relExpNode.print();
        IOUtils.writeFile(Settings.syntaxOutputPath, "<EqExp>\n");
        if (operator != null) {
            IOUtils.writeFile(Settings.syntaxOutputPath, operator.toString());
            eqExpNode.print();
        }
    }
}
