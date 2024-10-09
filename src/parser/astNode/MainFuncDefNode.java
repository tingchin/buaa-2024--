package parser.astNode;

import lexer.Token;
import utils.IoUtils;
import utils.Settings;

public class MainFuncDefNode implements Node{
    // MainFuncDef → 'int' 'main' '(' ')' Block // j
    private Token intToken;
    private Token mainToken;
    private Token leftParen;
    private Token rightParen;
    private BlockNode block;

    public MainFuncDefNode(Token intToken, Token mainToken, Token leftParen, Token rightParen, BlockNode block) {
        this.intToken = intToken;
        this.mainToken = mainToken;
        this.leftParen = leftParen;
        this.rightParen = rightParen;
        this.block = block;
    }

    @Override
    public void print() {
        IoUtils.writeFile(Settings.syntaxOutputPath, intToken.toString());
        IoUtils.writeFile(Settings.syntaxOutputPath, mainToken.toString());
        IoUtils.writeFile(Settings.syntaxOutputPath, leftParen.toString());
        IoUtils.writeFile(Settings.syntaxOutputPath, rightParen.toString());
        block.print();
        IoUtils.writeFile(Settings.syntaxOutputPath, "<MainFuncDef>\n");
    }
}
