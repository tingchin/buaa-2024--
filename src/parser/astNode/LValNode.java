package parser.astNode;

import lexer.Token;
import utils.IOUtils;
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
        IOUtils.writeFile(Settings.syntaxOutputPath, identToken.toString());
        if (leftBracketToken != null) {
            IOUtils.writeFile(Settings.syntaxOutputPath, leftBracketToken.toString());
            expNode.print();
            IOUtils.writeFile(Settings.syntaxOutputPath, rightBracketToken.toString());
        }
        IOUtils.writeFile(Settings.syntaxOutputPath, "<LVal>\n");
    }
}
