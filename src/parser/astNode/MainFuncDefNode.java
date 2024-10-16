package parser.astNode;

import lexer.Token;
import utils.IOUtils;
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

    public BlockNode getBlock() {
        return block;
    }

    @Override
    public void print() {
        IOUtils.writeFile(Settings.syntaxOutputPath, intToken.toString());
        IOUtils.writeFile(Settings.syntaxOutputPath, mainToken.toString());
        IOUtils.writeFile(Settings.syntaxOutputPath, leftParen.toString());
        IOUtils.writeFile(Settings.syntaxOutputPath, rightParen.toString());
        block.print();
        IOUtils.writeFile(Settings.syntaxOutputPath, "<MainFuncDef>\n");
    }
}
