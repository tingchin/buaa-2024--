package parser.astNode;

import lexer.Token;
import utils.IoUtils;
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

    @Override
    public void print() {
        lValNode.print();
        IoUtils.writeFile(Settings.syntaxOutputPath, operator.toString());
        expNode.print();
        IoUtils.writeFile(Settings.syntaxOutputPath, "<ForStmt>\n");
    }
}
