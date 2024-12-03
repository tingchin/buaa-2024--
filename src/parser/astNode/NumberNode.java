package parser.astNode;

import lexer.Token;
import utils.IOUtils;
import utils.Settings;

public class NumberNode implements Node {
    private Token token;

    public NumberNode(Token token) {
        this.token = token;
    }

    public Token getToken() {
        return token;
    }

    @Override
    public void print() {
        IOUtils.writeFile(Settings.syntaxOutputPath, token.toString());
        IOUtils.writeFile(Settings.syntaxOutputPath, "<Number>\n");
    }
}
