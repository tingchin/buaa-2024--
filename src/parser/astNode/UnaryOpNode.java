package parser.astNode;

import lexer.Token;
import utils.IoUtils;
import utils.Settings;

public class UnaryOpNode implements Node {
    private Token operator;

    public UnaryOpNode(Token operator) {
        this.operator = operator;
    }

    @Override
    public void print() {
        IoUtils.writeFile(Settings.syntaxOutputPath, operator.toString());
        IoUtils.writeFile(Settings.syntaxOutputPath, "<UnaryOp>\n");
    }
}
