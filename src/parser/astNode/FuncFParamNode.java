package parser.astNode;

import lexer.Token;
import utils.IoUtils;
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

    @Override
    public void print() {
        bTypeNode.print();
        IoUtils.writeFile(Settings.syntaxOutputPath, identToken.toString());
        if (leftBracket != null) {
            IoUtils.writeFile(Settings.syntaxOutputPath, leftBracket.toString());
            IoUtils.writeFile(Settings.syntaxOutputPath, rightBracket.toString());
        }
        IoUtils.writeFile(Settings.syntaxOutputPath, "<FuncFParam>\n");
    }
}
