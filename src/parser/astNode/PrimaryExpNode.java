package parser.astNode;

import lexer.Token;
import lexer.TokenType;
import utils.IoUtils;
import utils.Settings;

public class PrimaryExpNode implements Node {
    // PrimaryExp → '(' Exp ')' | LVal | Number | Character// j
    private Token leftParenToken;
    private ExpNode expNode;
    private Token rightParenToken;
    private LValNode lValNode;
    private NumberNode numberNode;
    private CharacterNode characterNode;

    public PrimaryExpNode(Token leftParenToken, ExpNode expNode, Token rightParenToken) {
        this.leftParenToken = leftParenToken;
        this.expNode = expNode;
        this.rightParenToken = rightParenToken;
    }

    public PrimaryExpNode(NumberNode numberNode) {
        this.numberNode = numberNode;
    }

    public PrimaryExpNode(CharacterNode characterNode) {
        this.characterNode = characterNode;
    }

    public PrimaryExpNode(LValNode lValNode) {
        this.lValNode = lValNode;
    }

    @Override
    public void print() {
        if (expNode != null) {
            IoUtils.writeFile(Settings.syntaxOutputPath, leftParenToken.toString());
            expNode.print();
            IoUtils.writeFile(Settings.syntaxOutputPath, rightParenToken.toString());
        } else if (lValNode != null) {
            lValNode.print();
        } else if (characterNode != null) {
            characterNode.print();
        } else {
            numberNode.print();
        }
        IoUtils.writeFile(Settings.syntaxOutputPath, "<PrimaryExp>\n");
    }
}
