package parser.astNode;


import lexer.Token;
import utils.IoUtils;
import utils.Settings;

public class FuncDefNode implements Node {
    // FuncDef → FuncType Ident '(' [FuncFParams] ')' Block // j
    private FuncTypeNode funcType;
    private Token identToken;
    private Token leftParent;
    private FuncFParamsNode funcFParams;
    private Token rightParent;
    private BlockNode block;

    public FuncDefNode(FuncTypeNode funcType, Token identToken, Token leftParent, FuncFParamsNode funcFParams, Token rightParent, BlockNode block) {
        this.funcType = funcType;
        this.leftParent = leftParent;
        this.funcFParams = funcFParams;
        this.rightParent = rightParent;
        this.block = block;
        this.identToken = identToken;
    }

    @Override
    public void print() {
        funcType.print();
        IoUtils.writeFile(Settings.syntaxOutputPath, identToken.toString());
        IoUtils.writeFile(Settings.syntaxOutputPath, leftParent.toString());
        if (funcFParams != null) {
            funcFParams.print();
        }
        IoUtils.writeFile(Settings.syntaxOutputPath, rightParent.toString());
        block.print();
        IoUtils.writeFile(Settings.syntaxOutputPath, "<FuncDef>\n");
    }
}
