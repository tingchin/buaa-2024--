package parser.astNode;

import lexer.Token;
import utils.IoUtils;
import utils.Settings;

public class VarDefNode implements Node{
    //  VarDef → Ident [ '[' ConstExp ']' ] | Ident [ '[' ConstExp ']' ] '=' InitVal // k
    private Token identNode;
    private Token leftBracket;
    private ConstExpNode constExpNode;
    private Token rightBracket;
    private Token equalToken;
    private InitValNode initValNode;

    public VarDefNode(Token identNode, Token leftBracket, ConstExpNode constExpNode, Token rightBracket, Token equalToken, InitValNode initValNode) {
        this.identNode = identNode;
        this.leftBracket = leftBracket;
        this.constExpNode = constExpNode;
        this.rightBracket = rightBracket;
        this.equalToken = equalToken;
        this.initValNode = initValNode;
    }

    @Override
    public void print() {
        IoUtils.writeFile(Settings.syntaxOutputPath, identNode.toString());
        if (leftBracket != null) {
            IoUtils.writeFile(Settings.syntaxOutputPath, leftBracket.toString());
            constExpNode.print();
            IoUtils.writeFile(Settings.syntaxOutputPath, rightBracket.toString());
        }
        if (initValNode != null) {
            IoUtils.writeFile(Settings.syntaxOutputPath, equalToken.toString());
            initValNode.print();
        }
        IoUtils.writeFile(Settings.syntaxOutputPath, "<VarDef>\n");
    }
}
