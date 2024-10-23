package parser.astNode;

import lexer.Token;
import utils.IOUtils;
import utils.Settings;

import java.util.List;

public class FuncRParamsNode implements Node {
    // FuncRParams → Exp { ',' Exp }
    private List<ExpNode> expNodes;
    private List<Token> commas;

    public FuncRParamsNode(List<ExpNode> expNodes, List<Token> commas) {
        this.expNodes = expNodes;
        this.commas = commas;
    }

    public List<ExpNode> getExpNodes() {
        return expNodes;
    }

    @Override
    public void print() {
        expNodes.get(0).print();
        for (int i = 1; i < expNodes.size(); i++) {
            IOUtils.writeFile(Settings.syntaxOutputPath, commas.get(i - 1).toString());
            expNodes.get(i).print();
        }
        IOUtils.writeFile(Settings.syntaxOutputPath, "<FuncRParams>\n");
    }
}
