package parser.astNode;

import lexer.Token;
import utils.IOUtils;
import utils.Settings;

import java.util.List;

public class ConstDefNode implements Node {
    public ConstInitValNode getConstInitValNode() {
        return constInitValNode;
    }

    @Override
    public void print() {
        IOUtils.writeFile(Settings.syntaxOutputPath, identToken.toString());
        for (int i = 0; i < constExpNodes.size(); i++) {
            IOUtils.writeFile(Settings.syntaxOutputPath, leftBrackets.get(i).toString());
            constExpNodes.get(i).print();
            IOUtils.writeFile(Settings.syntaxOutputPath, rightBrackets.get(i).toString());
        }
        IOUtils.writeFile(Settings.syntaxOutputPath, equalToken.toString());
        constInitValNode.print();
        IOUtils.writeFile(Settings.syntaxOutputPath, "<ConstDef>\n");
    }

    // ConstDef → Ident [ '[' ConstExp ']' ] '=' ConstInitVal
    private Token identToken;
    private List<Token> leftBrackets;
    private List<ConstExpNode> constExpNodes;
    private List<Token> rightBrackets;
    private Token equalToken;
    private ConstInitValNode constInitValNode;

    public ConstDefNode(Token identToken, List<Token> leftBrackets, List<ConstExpNode> constExpNodes, List<Token> rightBrackets, Token equalToken, ConstInitValNode constInitValNode) {
        this.identToken = identToken;
        this.leftBrackets = leftBrackets;
        this.constExpNodes = constExpNodes;
        this.rightBrackets = rightBrackets;
        this.equalToken = equalToken;
        this.constInitValNode = constInitValNode;
    }

    public Token getIdentToken() {
        return identToken;
    }

    public List<Token> getLeftBrackets() {
        return leftBrackets;
    }

    public List<ConstExpNode> getConstExpNodes() {
        return constExpNodes;
    }
}
