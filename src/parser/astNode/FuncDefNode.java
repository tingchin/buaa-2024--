package parser.astNode;


import lexer.Token;
import utils.IOUtils;
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

    public FuncTypeNode getFuncType() {
        return funcType;
    }

    public Token getIdentToken() {
        return identToken;
    }

    public FuncFParamsNode getFuncFParams() {
        return funcFParams;
    }

    public BlockNode getBlock() {
        return block;
    }

    @Override
    public void print() {
        funcType.print();
        IOUtils.writeFile(Settings.syntaxOutputPath, identToken.toString());
        IOUtils.writeFile(Settings.syntaxOutputPath, leftParent.toString());
        if (funcFParams != null) {
            funcFParams.print();
        }
        IOUtils.writeFile(Settings.syntaxOutputPath, rightParent.toString());
        block.print();
        IOUtils.writeFile(Settings.syntaxOutputPath, "<FuncDef>\n");
    }
}
