package parser.astNode;

import lexer.Token;
import utils.IOUtils;
import utils.Settings;

public class CharacterNode implements Node {
    private Token token;

    public CharacterNode(Token token) {
        this.token = token;
    }

    public Token getToken() {
        return token;
    }

    @Override
    public void print() {
        IOUtils.writeFile(Settings.syntaxOutputPath, token.toString());
        IOUtils.writeFile(Settings.syntaxOutputPath, "<Character>\n");
    }
}
