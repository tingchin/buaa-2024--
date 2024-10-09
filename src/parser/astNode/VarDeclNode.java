package parser.astNode;

import lexer.Token;
import utils.IoUtils;
import utils.Settings;

import java.util.List;

public class VarDeclNode implements Node {
    private BTypeNode bType;
    private List<Token> commas;
    private List<VarDefNode> varDefs;
    private Token semicnToken;

    public VarDeclNode(BTypeNode bType, List<Token> commas, List<VarDefNode> varDefs, Token semicnToken) {
        this.bType = bType;
        this.commas = commas;
        this.varDefs = varDefs;
        this.semicnToken = semicnToken;
    }

    @Override
    public void print() {
        bType.print();
        varDefs.get(0).print();
        for (int i = 1; i < varDefs.size(); i++) {
            IoUtils.writeFile(Settings.syntaxOutputPath, commas.get(i - 1).toString());
            varDefs.get(i).print();
        }
        IoUtils.writeFile(Settings.syntaxOutputPath, semicnToken.toString());
        IoUtils.writeFile(Settings.syntaxOutputPath, "<VarDecl>\n");
    }
}
