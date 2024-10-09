package parser.astNode;


public class DeclNode implements Node{
    private ConstDeclNode constDecl;
    private VarDeclNode varDecl;

    public DeclNode(ConstDeclNode constDecl, VarDeclNode varDecl) {
        this.constDecl = constDecl;
        this.varDecl = varDecl;
    }

    @Override
    public void print() {
        if (constDecl != null) {
            constDecl.print();
        } else {
            varDecl.print();
        }
    }
}
