package parser.astNode;

import lexer.Token;
import utils.IOUtils;
import utils.Settings;

public class FuncTypeNode implements Node {
    // FuncType → 'void' | 'int' | 'char'
    private Token funcType;

    public FuncTypeNode(Token funcType) {
        this.funcType = funcType;
    }

    public Token getFuncType() {
        return funcType;
    }

    @Override
    public void print() {
        IOUtils.writeFile(Settings.syntaxOutputPath, funcType.toString());
        IOUtils.writeFile(Settings.syntaxOutputPath, "<FuncType>\n");
    }
}
