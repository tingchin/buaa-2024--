package parser.astNode;

public class BlockItemNode implements Node {
    // BlockItem → Decl | Stmt
    private DeclNode decl;
    private StmtNode stmt;

    public BlockItemNode(DeclNode decl, StmtNode stmt) {
        this.decl = decl;
        this.stmt = stmt;
    }

    public DeclNode getDecl() {
        return decl;
    }

    public StmtNode getStmt() {
        return stmt;
    }

    @Override
    public void print() {
        if (decl != null) {
            decl.print();
        } else {
            stmt.print();
        }
    }
}
