package parser.astNode;

import lexer.Token;
import utils.IOUtils;
import utils.Settings;

public class ForStmtNode implements Node {
    // ForStmt → LVal '=' Exp
    private LValNode lValNode;
    private Token operator;
    private ExpNode expNode;

    public ForStmtNode(LValNode lValNode, Token operator, ExpNode expNode) {
        this.lValNode = lValNode;
        this.operator = operator;
        this.expNode = expNode;
    }

    public LValNode getlValNode() {
        return lValNode;
    }

    public Token getOperator() {
        return operator;
    }

    public ExpNode getExpNode() {
        return expNode;
    }

    @Override
    public void print() {
        lValNode.print();
        IOUtils.writeFile(Settings.syntaxOutputPath, operator.toString());
        expNode.print();
        IOUtils.writeFile(Settings.syntaxOutputPath, "<ForStmt>\n");
    }
}
