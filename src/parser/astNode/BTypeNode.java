package parser.astNode;


import lexer.Token;
import utils.IOUtils;
import utils.Settings;

public class BTypeNode implements Node {
    // int or char
    private Token token;

    public BTypeNode(Token token) {
        this.token = token;
    }

    public Token getToken() {
        return token;
    }

    @Override
    public void print() {
        IOUtils.writeFile(Settings.syntaxOutputPath, token.toString());
    }
}
