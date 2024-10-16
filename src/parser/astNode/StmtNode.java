package parser.astNode;

import lexer.Token;
import utils.IOUtils;
import utils.Settings;

import java.util.List;

public class StmtNode implements Node {
    // Stmt → LVal '=' Exp ';' // i
    //| [Exp] ';' // i
    //| Block
    //| 'if' '(' Cond ')' Stmt [ 'else' Stmt ] // j
    //| 'for' '(' [ForStmt] ';' [Cond] ';' [ForStmt] ')' Stmt
    //| 'break' ';' | 'continue' ';' // i
    //| 'return' [Exp] ';' // i
    //| LVal '=' 'getint''('')'';' // i j
    //| LVal '=' 'getchar''('')'';' // i j
    //| 'printf''('StringConst {','Exp}')'';' // i j

    public enum StmtType {
        LVal, Exp, Block, IF, FOR, Break, Continue, Return, GetInt, GetChar, Printf
    }

    private StmtType type;
    private LValNode lValNode;
    private Token equalToken;
    private ExpNode expNode;
    private Token semicnToken;
    private BlockNode blockNode;
    private Token ifToken;
    private Token leftParenToken;
    private CondNode condNode;
    private Token rightParenToken;
    private List<StmtNode> stmtNodes;
    private Token elseToken;
    private Token forToken;
    // private List<ForStmtNode> forStmtNodes;
    private ForStmtNode forStmtNode1;
    private ForStmtNode forStmtNode2;
    private List<Token> semicnTokens;
    private Token breakOrContinueToken;
    private Token returnToken;
    private Token getToken;
    private Token printfToken;
    private Token stringToken;
    private List<Token> commas;
    private List<ExpNode> expNodes;

    public StmtNode(StmtType type, Token printfToken, Token leftParenToken, Token stringToken, List<Token> commas, List<ExpNode> expNodes, Token semicnToken, Token rightParenToken) {
        // printf
        this.type = type;
        this.printfToken = printfToken;
        this.leftParenToken = leftParenToken;
        this.stringToken = stringToken;
        this.commas = commas;
        this.expNodes = expNodes;
        this.semicnToken = semicnToken;
        this.rightParenToken = rightParenToken;
    }

    private StmtNode stmtNode;

    public StmtNode(StmtType type, LValNode lValNode, Token equalToken, Token getToken, Token leftParenToken, Token rightParenToken, Token semicnToken) {
        // getInt or getChar
        this.type = type;
        this.lValNode = lValNode;
        this.equalToken = equalToken;
        this.getToken = getToken;
        this.leftParenToken = leftParenToken;
        this.rightParenToken = rightParenToken;
        this.semicnToken = semicnToken;
    }

    public StmtNode(StmtType type, Token semicnToken, Token returnToken, ExpNode expNode) {
        // return
        this.type = type;
        this.semicnToken = semicnToken;
        this.returnToken = returnToken;
        this.expNode = expNode;
    }

    public StmtNode(StmtType type, Token breakOrContinueToken, Token semicnToken) {
        // break or continue
        this.type = type;
        this.breakOrContinueToken = breakOrContinueToken;
        this.semicnToken = semicnToken;
    }

    public StmtNode(StmtType type, Token forToken, ForStmtNode forStmtNode1, ForStmtNode forStmtNode2 , Token leftParenToken, Token rightParenToken, CondNode condNode, StmtNode stmtNode, List<Token> semicnTokens) {
        // for
        this.type = type;
        this.forToken = forToken;
        this.forStmtNode1 = forStmtNode1;
        this.forStmtNode2 = forStmtNode2;
        this.leftParenToken = leftParenToken;
        this.rightParenToken = rightParenToken;
        this.condNode = condNode;
        this.stmtNode = stmtNode;
        this.semicnTokens = semicnTokens;
    }

    public StmtNode(StmtType type, LValNode lValNode, Token equalToken, ExpNode expNode, Token semicnToken) {
        // LVal '=' Exp ';' // i
        this.type = type;
        this.lValNode = lValNode;
        this.equalToken = equalToken;
        this.expNode = expNode;
        this.semicnToken = semicnToken;
    }

    public StmtNode(StmtType type, ExpNode expNode, Token semicnToken) {
        // [Exp] ';' // i
        this.type = type;
        this.expNode = expNode;
        this.semicnToken = semicnToken;
    }

    public StmtNode(StmtType type, Token ifToken, Token leftParenToken, CondNode condNode, Token rightParenToken, List<StmtNode> stmtNodes, Token elseToken) {
        // if
        this.type = type;
        this.ifToken = ifToken;
        this.leftParenToken = leftParenToken;
        this.condNode = condNode;
        this.rightParenToken = rightParenToken;
        this.stmtNodes = stmtNodes;
        this.elseToken = elseToken;
    }

    public StmtNode(StmtType type, BlockNode blockNode) {
        // Block
        this.type = type;
        this.blockNode = blockNode;
    }

    public StmtType getType() {
        return type;
    }

    public LValNode getlValNode() {
        return lValNode;
    }

    public ExpNode getExpNode() {
        return expNode;
    }

    public BlockNode getBlockNode() {
        return blockNode;
    }

    public Token getIfToken() {
        return ifToken;
    }

    public CondNode getCondNode() {
        return condNode;
    }

    public List<StmtNode> getStmtNodes() {
        return stmtNodes;
    }

    public Token getElseToken() {
        return elseToken;
    }

    public Token getForToken() {
        return forToken;
    }

    public ForStmtNode getForStmtNode1() {
        return forStmtNode1;
    }

    public ForStmtNode getForStmtNode2() {
        return forStmtNode2;
    }

    public Token getEqualToken() {
        return equalToken;
    }

    public Token getSemicnToken() {
        return semicnToken;
    }

    public Token getLeftParenToken() {
        return leftParenToken;
    }

    public Token getRightParenToken() {
        return rightParenToken;
    }

    public List<Token> getSemicnTokens() {
        return semicnTokens;
    }

    public Token getBreakOrContinueToken() {
        return breakOrContinueToken;
    }

    public Token getReturnToken() {
        return returnToken;
    }

    public Token getGetToken() {
        return getToken;
    }

    public Token getPrintfToken() {
        return printfToken;
    }

    public Token getStringToken() {
        return stringToken;
    }

    public List<Token> getCommas() {
        return commas;
    }

    public List<ExpNode> getExpNodes() {
        return expNodes;
    }

    public StmtNode getStmtNode() {
        return stmtNode;
    }

    @Override
    public void print() {
        switch (type) {
            case LVal:
                // LVal '=' Exp ';' // i
                lValNode.print();
                IOUtils.writeFile(Settings.syntaxOutputPath, equalToken.toString());
                expNode.print();
                IOUtils.writeFile(Settings.syntaxOutputPath, semicnToken.toString());
                break;
            case Exp:
                // [Exp] ';' // i
                if (expNode != null) {
                    expNode.print();
                }
                IOUtils.writeFile(Settings.syntaxOutputPath, semicnToken.toString());
                break;
            case Block:
                // Block
                blockNode.print();
                break;
            case IF:
                // 'if' '(' Cond ')' Stmt [ 'else' Stmt ] // j
                IOUtils.writeFile(Settings.syntaxOutputPath, ifToken.toString());
                IOUtils.writeFile(Settings.syntaxOutputPath, leftParenToken.toString());
                condNode.print();
                IOUtils.writeFile(Settings.syntaxOutputPath, rightParenToken.toString());
                stmtNodes.get(0).print();
                if (elseToken != null) {
                    IOUtils.writeFile(Settings.syntaxOutputPath, elseToken.toString());
                    stmtNodes.get(1).print();
                }
                break;
            case FOR:
                // 'for' '(' [ForStmt] ';' [Cond] ';' [ForStmt] ')' Stmt
                IOUtils.writeFile(Settings.syntaxOutputPath, forToken.toString());
                IOUtils.writeFile(Settings.syntaxOutputPath, leftParenToken.toString());
                if (forStmtNode1 != null) {
                    forStmtNode1.print();
                }
                IOUtils.writeFile(Settings.syntaxOutputPath, semicnTokens.get(0).toString());
                if (condNode != null) {
                    condNode.print();
                }
                IOUtils.writeFile(Settings.syntaxOutputPath, semicnTokens.get(1).toString());
                if (forStmtNode2 != null) {
                    forStmtNode2.print();
                }
                IOUtils.writeFile(Settings.syntaxOutputPath, rightParenToken.toString());
                stmtNode.print();
                break;
            case Break:
                // 'break' ';' | 'continue' ';' // i
            case Continue:
                // 'break' ';' | 'continue' ';' // i
                IOUtils.writeFile(Settings.syntaxOutputPath, breakOrContinueToken.toString());
                IOUtils.writeFile(Settings.syntaxOutputPath, semicnToken.toString());
                break;
            case Return:
                // 'return' [Exp] ';' // i
                IOUtils.writeFile(Settings.syntaxOutputPath, returnToken.toString());
                if (expNode != null) {
                    expNode.print();
                }
                IOUtils.writeFile(Settings.syntaxOutputPath, semicnToken.toString());
                break;
            case GetInt:
                //  LVal '=' 'getint''('')'';' // i j
            case GetChar:
                //| LVal '=' 'getchar''('')'';' // i j
                lValNode.print();
                IOUtils.writeFile(Settings.syntaxOutputPath, equalToken.toString());
                IOUtils.writeFile(Settings.syntaxOutputPath, getToken.toString());
                IOUtils.writeFile(Settings.syntaxOutputPath, leftParenToken.toString());
                IOUtils.writeFile(Settings.syntaxOutputPath, rightParenToken.toString());
                IOUtils.writeFile(Settings.syntaxOutputPath, semicnToken.toString());
                break;
            case Printf:
                // 'printf''('StringConst {','Exp}')'';' // i j
                IOUtils.writeFile(Settings.syntaxOutputPath, printfToken.toString());
                IOUtils.writeFile(Settings.syntaxOutputPath, leftParenToken.toString());
                IOUtils.writeFile(Settings.syntaxOutputPath, stringToken.toString());
                for (int i = 0; i < commas.size(); i++) {
                    IOUtils.writeFile(Settings.syntaxOutputPath, commas.get(i).toString());
                    expNodes.get(i).print();
                }
                IOUtils.writeFile(Settings.syntaxOutputPath, rightParenToken.toString());
                IOUtils.writeFile(Settings.syntaxOutputPath, semicnToken.toString());
                break;
        }
        IOUtils.writeFile(Settings.syntaxOutputPath, "<Stmt>\n");
    }
}
