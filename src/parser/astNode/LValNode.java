package parser.astNode;

import lexer.Token;
import utils.IoUtils;
import utils.Settings;

public class LValNode implements Node {
    // LVal → Ident ['[' Exp ']'] // k
    private Token identToken;
    private Token leftBracketToken;
    private ExpNode expNode;
    private Token rightBracketToken;

    public LValNode(Token identToken, Token leftBracketToken, ExpNode expNode, Token rightBracketToken) {
        this.identToken = identToken;
        this.leftBracketToken = leftBracketToken;
        this.expNode = expNode;
        this.rightBracketToken = rightBracketToken;
    }

    @Override
    public void print() {
        IoUtils.writeFile(Settings.syntaxOutputPath, identToken.toString());
        if (leftBracketToken != null) {
            IoUtils.writeFile(Settings.syntaxOutputPath, leftBracketToken.toString());
            expNode.print();
            IoUtils.writeFile(Settings.syntaxOutputPath, rightBracketToken.toString());
        }
        IoUtils.writeFile(Settings.syntaxOutputPath, "<LVal>\n");
    }
}
