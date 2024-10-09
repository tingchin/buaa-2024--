package parser.astNode;

import lexer.Token;
import utils.IoUtils;
import utils.Settings;

import java.util.List;

public class ConstDeclNode implements Node{
    private Token constToken;
    private BTypeNode bTypeNode;
    private List<ConstDefNode> constDefNodes;
    private List<Token> commas;
    private Token semicn;

    public ConstDeclNode(Token constToken, BTypeNode bTypeNode, List<ConstDefNode> constDefNodes, List<Token> commas, Token semicn) {
        this.constToken = constToken;
        this.bTypeNode = bTypeNode;
        this.constDefNodes = constDefNodes;
        this.commas = commas;
        this.semicn = semicn;
    }

    @Override
    public void print() {
        IoUtils.writeFile(Settings.syntaxOutputPath, constToken.toString());
        bTypeNode.print();
        constDefNodes.get(0).print();
        for (int i = 1; i < constDefNodes.size(); i++) {
            IoUtils.writeFile(Settings.syntaxOutputPath, commas.get(i - 1).toString());
            constDefNodes.get(i).print();
        }
        IoUtils.writeFile(Settings.syntaxOutputPath, semicn.toString());
        IoUtils.writeFile(Settings.syntaxOutputPath, "<ConstDecl>\n");
    }
}
