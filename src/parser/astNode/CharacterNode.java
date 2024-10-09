package parser.astNode;

import lexer.Token;
import utils.IoUtils;
import utils.Settings;

public class CharacterNode implements Node {
    private Token token;

    public CharacterNode(Token token) {
        this.token = token;
    }

    @Override
    public void print() {
        IoUtils.writeFile(Settings.syntaxOutputPath, token.toString());
        IoUtils.writeFile(Settings.syntaxOutputPath, "<Character>\n");
    }
}
