package parser.astNode;

import jdk.jfr.EventSettings;
import utils.IoUtils;
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

    @Override
    public void print() {
        for (DeclNode decl : decls) {
            decl.print();
        }
        for (FuncDefNode funcDefNode : func) {
            funcDefNode.print();
        }
        mainFun.print();
        IoUtils.writeFile(Settings.syntaxOutputPath, "<CompUnit>\n");

    }
}
