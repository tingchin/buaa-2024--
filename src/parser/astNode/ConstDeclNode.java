package parser.astNode;

import lexer.Token;
import utils.IOUtils;
import utils.Settings;

import java.util.List;

public class ConstDeclNode implements Node{
    private Token constToken;
    private BTypeNode bTypeNode;
    private List<ConstDefNode> constDefNodes;
    private List<Token> commas;
    private Token semicn;

    public BTypeNode getbTypeNode() {
        return bTypeNode;
    }

    public List<ConstDefNode> getConstDefNodes() {
        return constDefNodes;
    }

    public ConstDeclNode(Token constToken, BTypeNode bTypeNode, List<ConstDefNode> constDefNodes, List<Token> commas, Token semicn) {
        this.constToken = constToken;
        this.bTypeNode = bTypeNode;
        this.constDefNodes = constDefNodes;
        this.commas = commas;
        this.semicn = semicn;
    }

    @Override
    public void print() {
        IOUtils.writeFile(Settings.syntaxOutputPath, constToken.toString());
        bTypeNode.print();
        constDefNodes.get(0).print();
        for (int i = 1; i < constDefNodes.size(); i++) {
            IOUtils.writeFile(Settings.syntaxOutputPath, commas.get(i - 1).toString());
            constDefNodes.get(i).print();
        }
        IOUtils.writeFile(Settings.syntaxOutputPath, semicn.toString());
        IOUtils.writeFile(Settings.syntaxOutputPath, "<ConstDecl>\n");
    }
}
