package parser.astNode;

import lexer.Token;
import utils.IoUtils;
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

    @Override
    public void print() {
        switch (type) {
            case LVal:
                // LVal '=' Exp ';' // i
                lValNode.print();
                IoUtils.writeFile(Settings.syntaxOutputPath, equalToken.toString());
                expNode.print();
                IoUtils.writeFile(Settings.syntaxOutputPath, semicnToken.toString());
                break;
            case Exp:
                // [Exp] ';' // i
                if (expNode != null) {
                    expNode.print();
                }
                IoUtils.writeFile(Settings.syntaxOutputPath, semicnToken.toString());
                break;
            case Block:
                // Block
                blockNode.print();
                break;
            case IF:
                // 'if' '(' Cond ')' Stmt [ 'else' Stmt ] // j
                IoUtils.writeFile(Settings.syntaxOutputPath, ifToken.toString());
                IoUtils.writeFile(Settings.syntaxOutputPath, leftParenToken.toString());
                condNode.print();
                IoUtils.writeFile(Settings.syntaxOutputPath, rightParenToken.toString());
                stmtNodes.get(0).print();
                if (elseToken != null) {
                    IoUtils.writeFile(Settings.syntaxOutputPath, elseToken.toString());
                    stmtNodes.get(1).print();
                }
                break;
            case FOR:
                // 'for' '(' [ForStmt] ';' [Cond] ';' [ForStmt] ')' Stmt
                IoUtils.writeFile(Settings.syntaxOutputPath, forToken.toString());
                IoUtils.writeFile(Settings.syntaxOutputPath, leftParenToken.toString());
                if (forStmtNode1 != null) {
                    forStmtNode1.print();
                }
                IoUtils.writeFile(Settings.syntaxOutputPath, semicnTokens.get(0).toString());
                if (condNode != null) {
                    condNode.print();
                }
                IoUtils.writeFile(Settings.syntaxOutputPath, semicnTokens.get(1).toString());
                if (forStmtNode2 != null) {
                    forStmtNode2.print();
                }
                IoUtils.writeFile(Settings.syntaxOutputPath, rightParenToken.toString());
                stmtNode.print();
                break;
            case Break:
                // 'break' ';' | 'continue' ';' // i
            case Continue:
                // 'break' ';' | 'continue' ';' // i
                IoUtils.writeFile(Settings.syntaxOutputPath, breakOrContinueToken.toString());
                IoUtils.writeFile(Settings.syntaxOutputPath, semicnToken.toString());
                break;
            case Return:
                // 'return' [Exp] ';' // i
                IoUtils.writeFile(Settings.syntaxOutputPath, returnToken.toString());
                if (expNode != null) {
                    expNode.print();
                }
                IoUtils.writeFile(Settings.syntaxOutputPath, semicnToken.toString());
                break;
            case GetInt:
                //  LVal '=' 'getint''('')'';' // i j
            case GetChar:
                //| LVal '=' 'getchar''('')'';' // i j
                lValNode.print();
                IoUtils.writeFile(Settings.syntaxOutputPath, equalToken.toString());
                IoUtils.writeFile(Settings.syntaxOutputPath, getToken.toString());
                IoUtils.writeFile(Settings.syntaxOutputPath, leftParenToken.toString());
                IoUtils.writeFile(Settings.syntaxOutputPath, rightParenToken.toString());
                IoUtils.writeFile(Settings.syntaxOutputPath, semicnToken.toString());
                break;
            case Printf:
                // 'printf''('StringConst {','Exp}')'';' // i j
                IoUtils.writeFile(Settings.syntaxOutputPath, printfToken.toString());
                IoUtils.writeFile(Settings.syntaxOutputPath, leftParenToken.toString());
                IoUtils.writeFile(Settings.syntaxOutputPath, stringToken.toString());
                for (int i = 0; i < commas.size(); i++) {
                    IoUtils.writeFile(Settings.syntaxOutputPath, commas.get(i).toString());
                    expNodes.get(i).print();
                }
                IoUtils.writeFile(Settings.syntaxOutputPath, rightParenToken.toString());
                IoUtils.writeFile(Settings.syntaxOutputPath, semicnToken.toString());
                break;
        }
        IoUtils.writeFile(Settings.syntaxOutputPath, "<Stmt>\n");
    }
}
