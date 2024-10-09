package parser.astNode;

import lexer.Token;
import utils.IoUtils;
import utils.Settings;

import java.util.List;

public class InitValNode implements Node {
    //  InitVal → Exp | '{' [ Exp { ',' Exp } ] '}' | StringConst
    private ExpNode expr;
    private Token leftBrace;
    private List<ExpNode> expNodes;
    private Token rightBrace;
    private List<Token> commas;
    private Token stringToken;

    public InitValNode(ExpNode expr, Token leftBrace, List<ExpNode> expNodes, Token rightBrace, List<Token> commas, Token stringToken) {
        this.expr = expr;
        this.leftBrace = leftBrace;
        this.expNodes = expNodes;
        this.rightBrace = rightBrace;
        this.commas = commas;
        this.stringToken = stringToken;
    }

    @Override
    public void print() {
        if (expr != null) {
            expr.print();
        } else if (stringToken != null) {
            IoUtils.writeFile(Settings.syntaxOutputPath, stringToken.toString());
        } else {
            IoUtils.writeFile(Settings.syntaxOutputPath, leftBrace.toString());
            if (!expNodes.isEmpty()) {
                for (int i = 0; i < expNodes.size(); i++) {
                    expNodes.get(i).print();
                    if (i != expNodes.size() - 1) {
                        IoUtils.writeFile(Settings.syntaxOutputPath, commas.get(i).toString());
                    }
                }
            }
            IoUtils.writeFile(Settings.syntaxOutputPath, rightBrace.toString());
        }
        IoUtils.writeFile(Settings.syntaxOutputPath, "<InitVal>\n");
    }
}
