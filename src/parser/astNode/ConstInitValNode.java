package parser.astNode;

import lexer.Token;
import utils.IOUtils;
import utils.Settings;

import java.util.List;

public class ConstInitValNode implements Node{
    //  ConstInitVal → ConstExp | '{' [ ConstExp { ',' ConstExp } ] '}' | StringConst
    private ConstExpNode constExp;
    private Token leftBrace;
    private List<ConstExpNode> constExpNodes;
    private List<Token> commas;
    private Token rightBrace;
    private Token stringToken;

    public ConstInitValNode(ConstExpNode constExp, Token leftBrace, List<ConstExpNode> constExpNodes, List<Token> commas, Token rightBrace, Token stringToken) {
        this.constExp = constExp;
        this.leftBrace = leftBrace;
        this.constExpNodes = constExpNodes;
        this.commas = commas;
        this.rightBrace = rightBrace;
        this.stringToken = stringToken;
    }

    @Override
    public void print() {
        if (constExp != null) {
            constExp.print();
        } else if (stringToken != null) {
            IOUtils.writeFile(Settings.syntaxOutputPath, stringToken.toString());
        } else {
            IOUtils.writeFile(Settings.syntaxOutputPath, leftBrace.toString());
            if (!constExpNodes.isEmpty()) {
                constExpNodes.get(0).print();
                for (int i = 1; i < constExpNodes.size(); i++) {
                    IOUtils.writeFile(Settings.syntaxOutputPath, commas.get(i - 1).toString());
                    constExpNodes.get(i).print();
                }
            }
            IOUtils.writeFile(Settings.syntaxOutputPath, rightBrace.toString());
        }
        IOUtils.writeFile(Settings.syntaxOutputPath, "<ConstInitVal>\n");
    }
}
