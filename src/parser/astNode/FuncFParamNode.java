package parser.astNode;

import lexer.Token;
import utils.IOUtils;
import utils.Settings;

public class FuncFParamNode implements Node {
    // FuncFParam → BType Ident ['[' ']'] // k
    private BTypeNode bTypeNode;
    private Token identToken;
    private Token leftBracket;
    private Token rightBracket;

    public FuncFParamNode(BTypeNode bTypeNode, Token identToken, Token leftBracket, Token rightBracket) {
        this.bTypeNode = bTypeNode;
        this.identToken = identToken;
        this.leftBracket = leftBracket;
        this.rightBracket = rightBracket;
    }

    public BTypeNode getbTypeNode() {
        return bTypeNode;
    }

    public Token getIdentToken() {
        return identToken;
    }

    public Token getLeftBracket() {
        return leftBracket;
    }

    @Override
    public void print() {
        bTypeNode.print();
        IOUtils.writeFile(Settings.syntaxOutputPath, identToken.toString());
        if (leftBracket != null) {
            IOUtils.writeFile(Settings.syntaxOutputPath, leftBracket.toString());
            IOUtils.writeFile(Settings.syntaxOutputPath, rightBracket.toString());
        }
        IOUtils.writeFile(Settings.syntaxOutputPath, "<FuncFParam>\n");
    }
}
