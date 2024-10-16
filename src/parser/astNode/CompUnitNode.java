package parser.astNode;

import utils.IOUtils;
import utils.Settings;

import java.util.List;


public class CompUnitNode implements Node{
    List<DeclNode> decls;
    List<FuncDefNode> func;
    MainFuncDefNode mainFun;

    public CompUnitNode(List<DeclNode> decls, List<FuncDefNode> func, MainFuncDefNode mainFun) {
        this.decls = decls;
        this.func = func;
        this.mainFun = mainFun;
    }

    public List<DeclNode> getDecls() {
        return decls;
    }

    public List<FuncDefNode> getFunc() {
        return func;
    }

    public MainFuncDefNode getMainFun() {
        return mainFun;
    }

    @Override
    public void print() {
        for (DeclNode decl : decls) {
            decl.print();
        }
        for (FuncDefNode funcDefNode : func) {
            funcDefNode.print();
        }
        mainFun.print();
        IOUtils.writeFile(Settings.syntaxOutputPath, "<CompUnit>\n");

    }
}
