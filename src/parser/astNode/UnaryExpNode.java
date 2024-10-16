package parser.astNode;

import lexer.Token;
import utils.IOUtils;
import utils.Settings;

public class UnaryExpNode implements Node {
    // UnaryExp → PrimaryExp | Ident '(' [FuncRParams] ')' | UnaryOp UnaryExp // j
    private PrimaryExpNode primary;
    private Token identToken;
    private Token leftParen;
    private FuncRParamsNode funcRParams;
    private Token rightParen;
    private UnaryOpNode unaryOp;
    private UnaryExpNode unaryExp;

    public UnaryExpNode(Token identToken, Token leftParen, FuncRParamsNode funcRParams, Token rightParen) {
        this.identToken = identToken;
        this.leftParen = leftParen;
        this.funcRParams = funcRParams;
        this.rightParen = rightParen;
    }

    public UnaryExpNode(PrimaryExpNode primary) {
        this.primary = primary;
    }

    public UnaryExpNode(UnaryOpNode unaryOp, UnaryExpNode unaryExp) {
        this.unaryOp = unaryOp;
        this.unaryExp = unaryExp;
    }

    @Override
    public void print() {
        // UnaryExp → PrimaryExp | Ident '(' [FuncRParams] ')' | UnaryOp UnaryExp // j
        if (primary != null) {
            primary.print();
        } else if (identToken != null) {
            IOUtils.writeFile(Settings.syntaxOutputPath, identToken.toString());
            IOUtils.writeFile(Settings.syntaxOutputPath, leftParen.toString());
            if (funcRParams != null) {
                funcRParams.print();
            }
            IOUtils.writeFile(Settings.syntaxOutputPath, rightParen.toString());
        } else {
            unaryOp.print();
            unaryExp.print();
        }
        IOUtils.writeFile(Settings.syntaxOutputPath, "<UnaryExp>\n");
    }
}
