package parser.astNode;


import lexer.Token;
import utils.IoUtils;
import utils.Settings;

public class BTypeNode implements Node {
    // int or char
    private Token token;

    public BTypeNode(Token token) {
        this.token = token;
    }

    @Override
    public void print() {
        IoUtils.writeFile(Settings.syntaxOutputPath, token.toString());
    }
}
