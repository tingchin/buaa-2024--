package parser.astNode;

import lexer.Token;
import utils.IOUtils;
import utils.Settings;

public class UnaryOpNode implements Node {
    private Token operator;

    public UnaryOpNode(Token operator) {
        this.operator = operator;
    }

    @Override
    public void print() {
        IOUtils.writeFile(Settings.syntaxOutputPath, operator.toString());
        IOUtils.writeFile(Settings.syntaxOutputPath, "<UnaryOp>\n");
    }
}
