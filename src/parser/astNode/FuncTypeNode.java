package parser.astNode;

import lexer.Token;
import utils.IoUtils;
import utils.Settings;

public class FuncTypeNode implements Node {
    // FuncType → 'void' | 'int' | 'char'
    private Token funcType;

    public FuncTypeNode(Token funcType) {
        this.funcType = funcType;
    }

    @Override
    public void print() {
        IoUtils.writeFile(Settings.syntaxOutputPath, funcType.toString());
        IoUtils.writeFile(Settings.syntaxOutputPath, "<FuncType>\n");
    }
}
