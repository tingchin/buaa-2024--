package parser.astNode;

import lexer.Token;
import utils.IOUtils;
import utils.Settings;

import java.util.List;

public class FuncFParamsNode implements Node {
    //  FuncFParams → FuncFParam { ',' FuncFParam }
    private List<FuncFParamNode> params;
    private List<Token> commas;

    public List<FuncFParamNode> getParams() {
        return params;
    }

    public FuncFParamsNode(List<FuncFParamNode> params, List<Token> commas) {
        this.params = params;
        this.commas = commas;
    }


    @Override
    public void print() {
        params.get(0).print();
        for (int i = 1; i < params.size(); i++) {
            IOUtils.writeFile(Settings.syntaxOutputPath, commas.get(i - 1).toString());
            params.get(i).print();
        }
        IOUtils.writeFile(Settings.syntaxOutputPath, "<FuncFParams>\n");
    }
}
