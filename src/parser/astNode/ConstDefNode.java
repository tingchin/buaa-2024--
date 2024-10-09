package parser.astNode;

import lexer.Token;
import utils.IoUtils;
import utils.Settings;

import java.util.List;

public class ConstDefNode implements Node {
    @Override
    public void print() {
        IoUtils.writeFile(Settings.syntaxOutputPath, identToken.toString());
        for (int i = 0; i < constExpNodes.size(); i++) {
            IoUtils.writeFile(Settings.syntaxOutputPath, leftBrackets.get(i).toString());
            constExpNodes.get(i).print();
            IoUtils.writeFile(Settings.syntaxOutputPath, rightBrackets.get(i).toString());
        }
        IoUtils.writeFile(Settings.syntaxOutputPath, equalToken.toString());
        constInitValNode.print();
        IoUtils.writeFile(Settings.syntaxOutputPath, "<ConstDef>\n");
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
}
