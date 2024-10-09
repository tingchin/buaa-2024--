package parser;

import error.Error;
import error.ErrorHandler;
import error.ErrorType;
import lexer.Token;
import lexer.TokenType;
import parser.astNode.*;

import java.util.ArrayList;
import java.util.List;


public class Parser {
    private static final Parser instance = new Parser();

    public static Parser getInstance() {
        return instance;
    }

    private List<Token> tokens;
    private int index = 0;
    private Token curToken;
    private CompUnitNode root;

    public void setTokens(List<Token> tokens) {
        this.tokens = tokens;
        this.index = 0;
        this.curToken = tokens.get(index);
    }

    public CompUnitNode getRoot() {
        return root;
    }

    public void parse() {
        this.root = compUnit();
    }

    public void printAns() {
        root.print();
    }

    private CompUnitNode compUnit() {
        // CompUnit -> {Decl} {FuncDef} MainFuncDef
        List<DeclNode> declNodes = new ArrayList<>();
        List<FuncDefNode> funcDefNodes = new ArrayList<>();
        MainFuncDefNode mainFuncDefNode = null;
        // Decl
        while ((!tokens.get(index + 1).getType().equals(TokenType.MAINTK)) && (!tokens.get(index + 2).getType().equals(TokenType.LPARENT))) {
            // 下一个不是main也不是(，就保证了不是函数定义也不是主函数
            DeclNode decl = Decl();
            declNodes.add(decl);
        }
        // FuncDef
        while ((!tokens.get(index + 1).getType().equals(TokenType.MAINTK))) {
            // 没有Decl之后，下一个不是main就是普通函数定义
            FuncDefNode funcDefNode = FuncDef();
            funcDefNodes.add(funcDefNode);
        }
        // MainFuncDef
        mainFuncDefNode = MainFuncDef();
        return new CompUnitNode(declNodes, funcDefNodes, mainFuncDefNode);
    }

    private DeclNode Decl() {
        // Decl -> ConstDecl | VarDecl
        ConstDeclNode constDecl = null;
        VarDeclNode varDecl = null;
        if (curToken.getType().equals(TokenType.CONSTTK)) {
            constDecl = ConstDecl();
        } else {
            varDecl = VarDecl();
        }
        return new DeclNode(constDecl, varDecl);
    }

    private FuncDefNode FuncDef() {
        // FuncDef → FuncType Ident '(' [FuncFParams] ')' Block // j
        FuncTypeNode funcType = FuncType();

        Token identToken = curToken;
        if (index < tokens.size() - 1) {
            curToken = tokens.get(++index);
        }

        Token leftParenToken = curToken;
        if (index < tokens.size() - 1) {
            curToken = tokens.get(++index);
        }


        FuncFParamsNode funcFParams = null;
        if (curToken.getType().equals(TokenType.INTTK) || curToken.getType().equals(TokenType.CHARTK)) {
            funcFParams = FuncFParams();
        }

        Token rightParenToken = null;
        if (curToken.getType().equals(TokenType.RPARENT)) {
            rightParenToken = curToken;
            if (index < tokens.size() - 1) {
                curToken = tokens.get(++index);
            }
        } else {
            ErrorHandler.getInstance().addError(new Error(ErrorType.j, tokens.get(index - 1).getLine()));
            rightParenToken = new Token(TokenType.RPARENT, ")", tokens.get(index - 1).getLine());
        }

        BlockNode block = Block();

        return new FuncDefNode(funcType, identToken, leftParenToken, funcFParams, rightParenToken, block);
    }

    private BlockNode Block() {
        // Block → '{' { BlockItem } '}'
        Token leftBraceToken = curToken;
        if (index < tokens.size() - 1) {
            curToken = tokens.get(++index);
        }

        List<BlockItemNode> blockItemNodes = new ArrayList<>();
        while (!curToken.getType().equals(TokenType.RBRACE)) {
            blockItemNodes.add(BlockItem());
        }

        Token rightBraceToken = curToken;
        if (index < tokens.size() - 1) {
            curToken = tokens.get(++index);
        }
        return new BlockNode(leftBraceToken, rightBraceToken, blockItemNodes);
    }

    private BlockItemNode BlockItem() {
        // BlockItem → Decl | Stmt
        DeclNode decl = null;
        StmtNode stmt = null;

        if (curToken.getType().equals(TokenType.CONSTTK) || curToken.getType().equals(TokenType.INTTK) || curToken.getType().equals(TokenType.CHARTK)) {
            decl = Decl();
        } else {
            stmt = Stmt();
        }
        return new BlockItemNode(decl, stmt);
    }

    private StmtNode Stmt() {
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
        if (curToken.getType().equals(TokenType.LBRACE)) {
            // Block
            BlockNode block = Block();
            return new StmtNode(StmtNode.StmtType.Block, block);
        } else if (curToken.getType().equals(TokenType.PRINTFTK)) {
            // 'printf''('StringConst {','Exp}')'';' // i j
            Token printfToken = curToken;
            if (index < tokens.size() - 1) {
                curToken = tokens.get(++index);
            }

            Token leftParenToken = curToken;
            if (index < tokens.size() - 1) {
                curToken = tokens.get(++index);
            }

            Token stringToken = curToken;
            if (index < tokens.size() - 1) {
                curToken = tokens.get(++index);
            }

            List<Token> commas = new ArrayList<>();
            List<ExpNode> expNodes = new ArrayList<>();
            while (curToken.getType().equals(TokenType.COMMA)) {
                commas.add(curToken);
                if (index < tokens.size() - 1) {
                    curToken = tokens.get(++index);
                }
                expNodes.add(Exp());
            }
            Token rightParenToken = null;
            if (curToken.getType().equals(TokenType.RPARENT)) {
                rightParenToken = curToken;
                if (index < tokens.size() - 1) {
                    curToken = tokens.get(++index);
                }
            } else {
                // error
                ErrorHandler.getInstance().addError(new Error(ErrorType.j, tokens.get(index - 1).getLine()));
                rightParenToken = new Token(TokenType.RPARENT, ")", tokens.get(index - 1).getLine());
            }

            Token semicnToken = null;
            if (curToken.getType().equals(TokenType.SEMICN)) {
                semicnToken = curToken;
                if (index < tokens.size() - 1) {
                    curToken = tokens.get(++index);
                }
            } else {
                // error
                ErrorHandler.getInstance().addError(new Error(ErrorType.i, tokens.get(index - 1).getLine()));
                semicnToken = new Token(TokenType.SEMICN, ";", tokens.get(index - 1).getLine());
            }
            return new StmtNode(StmtNode.StmtType.Printf, printfToken, leftParenToken, stringToken, commas, expNodes, semicnToken, rightParenToken);
        } else if (curToken.getType().equals(TokenType.IFTK)) {
            // 'if' '(' Cond ')' Stmt [ 'else' Stmt ] // j
            Token ifToken = curToken;
            if (index < tokens.size() - 1) {
                curToken = tokens.get(++index);
            }

            Token leftParenToken = curToken;
            if (index < tokens.size() - 1) {
                curToken = tokens.get(++index);
            }

            CondNode condNode = Cond();
            Token rightParenToken = null;
            if (curToken.getType().equals(TokenType.RPARENT)) {
                rightParenToken = curToken;
                if (index < tokens.size() - 1) {
                    curToken = tokens.get(++index);
                }
            } else {
                // error
                ErrorHandler.getInstance().addError(new Error(ErrorType.j, tokens.get(index - 1).getLine()));
                rightParenToken = new Token(TokenType.RPARENT, ")", tokens.get(index - 1).getLine());
            }
            List<StmtNode> stmtNodes = new ArrayList<>();
            stmtNodes.add(Stmt());
            Token elseToken = null;
            if (curToken.getType().equals(TokenType.ELSETK)) {
                elseToken = curToken;
                if (index < tokens.size() - 1) {
                    curToken = tokens.get(++index);
                }
                stmtNodes.add(Stmt());
            }
            return new StmtNode(StmtNode.StmtType.IF, ifToken, leftParenToken, condNode, rightParenToken, stmtNodes, elseToken);
        } else if (curToken.getType().equals(TokenType.FORTK)) {
            // 'for' '(' [ForStmt] ';' [Cond] ';' [ForStmt] ')' Stmt
            // forToken
            Token forToken = curToken;
            if (index < tokens.size() - 1) {
                curToken = tokens.get(++index);
            }

            // ( token
            Token leftParenToken = curToken;
            if (index < tokens.size() - 1) {
                curToken = tokens.get(++index);
            }

            // [ForStmt] 1
            ForStmtNode forStmtNode1 = null;
            ForStmtNode forStmtNode2 = null;
            CondNode condNode = null;
            List<Token> semicTokens = new ArrayList<>();
            if (!curToken.getType().equals(TokenType.SEMICN)) {
                forStmtNode1 = ForStmt();
            }

            // ; 1
            semicTokens.add(curToken);
            if (index < tokens.size() - 1) {
                curToken = tokens.get(++index);
            }

            // [cond]
            if (!curToken.getType().equals(TokenType.SEMICN)) {
                condNode = Cond();
            }

            // ; 2
            semicTokens.add(curToken);
            if (index < tokens.size() - 1) {
                curToken = tokens.get(++index);
            }

            // [forStmt] 2
            if (!curToken.getType().equals(TokenType.RPARENT)) {
                forStmtNode2 = ForStmt();
            }

            // ) token
            Token rightParenToken = curToken;
            if (index < tokens.size() - 1) {
                curToken = tokens.get(++index);
            }

            StmtNode stmtNode = Stmt();

            return new StmtNode(StmtNode.StmtType.FOR, forToken, forStmtNode1, forStmtNode2, leftParenToken, rightParenToken, condNode, stmtNode, semicTokens);
        } else if (curToken.getType().equals(TokenType.BREAKTK)) {
            // 'break' ';' | 'continue' ';' // i
            Token breakToken = curToken;
            if (index < tokens.size() - 1) {
                curToken = tokens.get(++index);
            }
            Token semicToken = null;
            if (curToken.getType().equals(TokenType.SEMICN)) {
                semicToken = curToken;
                if (index < tokens.size() - 1) {
                    curToken = tokens.get(++index);
                }
            } else {
                ErrorHandler.getInstance().addError(new Error(ErrorType.i, tokens.get(index - 1).getLine()));
                semicToken = new Token(TokenType.SEMICN, ";", tokens.get(index - 1).getLine());
            }
            return new StmtNode(StmtNode.StmtType.Break, breakToken, semicToken);
        } else if (curToken.getType().equals(TokenType.CONTINUETK)) {
            // 'break' ';' | 'continue' ';' // i
            Token continueToken = curToken;
            if (index < tokens.size() - 1) {
                curToken = tokens.get(++index);
            }
            Token semicToken = null;
            if (curToken.getType().equals(TokenType.SEMICN)) {
                semicToken = curToken;
                if (index < tokens.size() - 1) {
                    curToken = tokens.get(++index);
                }
            } else {
                ErrorHandler.getInstance().addError(new Error(ErrorType.i, tokens.get(index - 1).getLine()));
                semicToken = new Token(TokenType.SEMICN, ";", tokens.get(index - 1).getLine());
            }
            return new StmtNode(StmtNode.StmtType.Continue, continueToken, semicToken);
        } else if (curToken.getType().equals(TokenType.RETURNTK)) {
            // 'return' [Exp] ';' // i
            Token returnToken = curToken;
            if (index < tokens.size() - 1) {
                curToken = tokens.get(++index);
            }

            ExpNode expNode = null;
            if (isExp()) {
                expNode = Exp();
            }

            Token semicToken = null;
            if (curToken.getType().equals(TokenType.SEMICN)) {
                semicToken = curToken;
                if (index < tokens.size() - 1) {
                    curToken = tokens.get(++index);
                }
            } else {
                ErrorHandler.getInstance().addError(new Error(ErrorType.i, tokens.get(index - 1).getLine()));
                semicToken = new Token(TokenType.SEMICN, ";", tokens.get(index - 1).getLine());
            }
            return new StmtNode(StmtNode.StmtType.Return, semicToken, returnToken, expNode);
        } else {
            int equalNum = index;
            for (int i = index; i < tokens.size() && tokens.get(i).getLine() == curToken.getLine(); i++) {
                if (tokens.get(i).getType().equals(TokenType.ASSIGN)) {
                    equalNum = i;
                    break;
                }
            }

            if (equalNum > index) {
                // LVal = 'Exp
                // LVal = get
                LValNode lValNode = LVal();
                Token equalToken = curToken;
                if (index < tokens.size() - 1) {
                    curToken = tokens.get(++index);
                }

                if (curToken.getType().equals(TokenType.GETINTTK)) {
                    // LVal '=' 'getint''('')'';' // i j
                    Token getIntToken = curToken;
                    if (index < tokens.size() - 1) {
                        curToken = tokens.get(++index);
                    }
                    Token leftParenToken = curToken;
                    if (index < tokens.size() - 1) {
                        curToken = tokens.get(++index);
                    }

                    // ) or error
                    Token rightParenToken = null;
                    if (curToken.getType().equals(TokenType.RPARENT)) {
                        rightParenToken = curToken;
                        if (index < tokens.size() - 1) {
                            curToken = tokens.get(++index);
                        }
                    } else {
                        ErrorHandler.getInstance().addError(new Error(ErrorType.j, tokens.get(index - 1).getLine()));
                        rightParenToken = new Token(TokenType.RPARENT, ")", tokens.get(index - 1).getLine());
                    }

                    // ; or error
                    Token semicToken = null;
                    if (curToken.getType().equals(TokenType.SEMICN)) {
                        semicToken = curToken;
                        if (index < tokens.size() - 1) {
                            curToken = tokens.get(++index);
                        }
                    } else {
                        ErrorHandler.getInstance().addError(new Error(ErrorType.i, tokens.get(index - 1).getLine()));
                        semicToken = new Token(TokenType.SEMICN, ";", tokens.get(index - 1).getLine());
                    }

                    return new StmtNode(StmtNode.StmtType.GetInt, lValNode, equalToken, getIntToken, leftParenToken, rightParenToken, semicToken);
                } else if (curToken.getType().equals(TokenType.GETCHARTK)) {
                    // LVal '=' 'getchar''('')'';' // i j
                    Token getCharToken = curToken;
                    if (index < tokens.size() - 1) {
                        curToken = tokens.get(++index);
                    }
                    Token leftParenToken = curToken;
                    if (index < tokens.size() - 1) {
                        curToken = tokens.get(++index);
                    }

                    // ) or error
                    Token rightParenToken = null;
                    if (curToken.getType().equals(TokenType.RPARENT)) {
                        rightParenToken = curToken;
                        if (index < tokens.size() - 1) {
                            curToken = tokens.get(++index);
                        }
                    } else {
                        ErrorHandler.getInstance().addError(new Error(ErrorType.j, tokens.get(index - 1).getLine()));
                        rightParenToken = new Token(TokenType.RPARENT, ")", tokens.get(index - 1).getLine());
                    }

                    // ; or error
                    Token semicToken = null;
                    if (curToken.getType().equals(TokenType.SEMICN)) {
                        semicToken = curToken;
                        if (index < tokens.size() - 1) {
                            curToken = tokens.get(++index);
                        }
                    } else {
                        ErrorHandler.getInstance().addError(new Error(ErrorType.i, tokens.get(index - 1).getLine()));
                        semicToken = new Token(TokenType.SEMICN, ";", tokens.get(index - 1).getLine());
                    }

                    return new StmtNode(StmtNode.StmtType.GetChar, lValNode, equalToken, getCharToken, leftParenToken, rightParenToken, semicToken);
                } else {
                    //  LVal '=' Exp ';' // i
                    ExpNode expNode = Exp();
                    Token semicToken = null;
                    if (curToken.getType().equals(TokenType.SEMICN)) {
                        semicToken = curToken;
                        if (index < tokens.size() - 1) {
                            curToken = tokens.get(++index);
                        }
                    } else {
                        ErrorHandler.getInstance().addError(new Error(ErrorType.i, tokens.get(index - 1).getLine()));
                        semicToken = new Token(TokenType.SEMICN, ";", tokens.get(index - 1).getLine());
                    }
                    return new StmtNode(StmtNode.StmtType.LVal, lValNode, equalToken, expNode, semicToken);
                }
            } else {
                //  [Exp] ';' // i
                ExpNode expNode = null;
                if (isExp()) {
                    expNode = Exp();
                }
                Token semicToken = null;
                if (curToken.getType().equals(TokenType.SEMICN)) {
                    semicToken = curToken;
                    if (index < tokens.size() - 1) {
                        curToken = tokens.get(++index);
                    }
                } else {
                    ErrorHandler.getInstance().addError(new Error(ErrorType.i, tokens.get(index - 1).getLine()));
                    semicToken = new Token(TokenType.SEMICN, ";", tokens.get(index - 1).getLine());
                }
                return new StmtNode(StmtNode.StmtType.Exp, expNode, semicToken);

            }

        }
    }

    private LValNode LVal() {
        // LVal → Ident ['[' Exp ']'] // k
        Token identToken = curToken;
        if (index < tokens.size() - 1) {
            curToken = tokens.get(++index);
        }

        Token leftBracketToken = null;
        ExpNode expNode = null;
        Token rightBracketToken = null;
        if (curToken.getType().equals(TokenType.LBRACK)) {
            leftBracketToken = curToken;
            if (index < tokens.size() - 1) {
                curToken = tokens.get(++index);
            }
            expNode = Exp();
            if (curToken.getType().equals(TokenType.RBRACK)) {
                rightBracketToken = curToken;
                if (index < tokens.size() - 1) {
                    curToken = tokens.get(++index);
                }
            } else {
                // error
                ErrorHandler.getInstance().addError(new Error(ErrorType.k, tokens.get(index - 1).getLine()));
                rightBracketToken = new Token(TokenType.RBRACK, "]", tokens.get(index - 1).getLine());
            }
        }
        return new LValNode(identToken, leftBracketToken, expNode, rightBracketToken);
    }

    private boolean isExp() {
        return curToken.getType().equals(TokenType.IDENFR) ||
                curToken.getType().equals(TokenType.PLUS) ||
                curToken.getType().equals(TokenType.MINU) ||
                curToken.getType().equals(TokenType.NOT) ||
                curToken.getType().equals(TokenType.LPARENT) ||
                curToken.getType().equals(TokenType.INTCON) ||
                curToken.getType().equals(TokenType.CHRCON);
    }

    private PrimaryExpNode PrimaryExp() {
        // PrimaryExp → '(' Exp ')' | LVal | Number | Character// j
        if (curToken.getType().equals(TokenType.LPARENT)) {
            // '(' Exp ')'
            Token leftParenToken = curToken;
            if (index < tokens.size() - 1) {
                curToken = tokens.get(++index);
            }

            ExpNode expNode = Exp();

            Token rightParenToken = null;
            if (curToken.getType().equals(TokenType.RPARENT)) {
                rightParenToken = curToken;
                if (index < tokens.size() - 1) {
                    curToken = tokens.get(++index);
                }
            } else {
                ErrorHandler.getInstance().addError(new Error(ErrorType.j, tokens.get(index - 1).getLine()));
                rightParenToken = new Token(TokenType.RPARENT, ")", tokens.get(index - 1).getLine());
            }
            return new PrimaryExpNode(leftParenToken, expNode, rightParenToken);
        } else if (curToken.getType().equals(TokenType.INTCON)) {
            NumberNode numberNode = Number();
            return new PrimaryExpNode(numberNode);
        } else if (curToken.getType().equals(TokenType.CHRCON)) {
            CharacterNode characterNode = Character();
            return new PrimaryExpNode(characterNode);
        } else {
            LValNode lValNode = LVal();
            return new PrimaryExpNode(lValNode);
        }
    }

    private CharacterNode Character() {
        Token token = curToken;
        if (index < tokens.size() - 1) {
            curToken = tokens.get(++index);
        }
        return new CharacterNode(token);
    }

    private NumberNode Number() {
        Token token = curToken;
        if (index < tokens.size() - 1) {
            curToken = tokens.get(++index);
        }
        return new NumberNode(token);
    }

    private UnaryExpNode UnaryExp() {
        // UnaryExp → PrimaryExp | Ident '(' [FuncRParams] ')' | UnaryOp UnaryExp // j
        if (curToken.getType().equals(TokenType.IDENFR) && tokens.get(index + 1).getType().equals(TokenType.LPARENT)) {
            Token identToken = curToken;
            if (index < tokens.size() - 1) {
                curToken = tokens.get(++index);
            }
            Token leftParen = curToken;
            if (index < tokens.size() - 1) {
                curToken = tokens.get(++index);
            }

            FuncRParamsNode funcRParamsNode = null;
            if (isExp()) {
                funcRParamsNode = FuncRParams();
            }

            Token rightParen = null;
            if (curToken.getType().equals(TokenType.RPARENT)) {
                rightParen = curToken;
                if (index < tokens.size() - 1) {
                    curToken = tokens.get(++index);
                }
            } else {
                ErrorHandler.getInstance().addError(new Error(ErrorType.j, tokens.get(index - 1).getLine()));
                rightParen = new Token(TokenType.RPARENT, ")", tokens.get(index - 1).getLine());
            }
            return new UnaryExpNode(identToken, leftParen, funcRParamsNode, rightParen);
        } else if (curToken.getType().equals(TokenType.PLUS) || curToken.getType().equals(TokenType.MINU) || curToken.getType().equals(TokenType.NOT)) {
            UnaryOpNode unaryOpNode = UnaryOp();
            UnaryExpNode unaryExpNode = UnaryExp();
            return new UnaryExpNode(unaryOpNode, unaryExpNode);
        } else {
            PrimaryExpNode primaryExpNode = PrimaryExp();
            return new UnaryExpNode(primaryExpNode);
        }
    }

    private UnaryOpNode UnaryOp() {
        Token token = curToken;
        if (index < tokens.size() - 1) {
            curToken = tokens.get(++index);
        }
        return new UnaryOpNode(token);
    }

    private FuncRParamsNode FuncRParams() {
        // FuncRParams → Exp { ',' Exp }
        List<ExpNode> expNodes = new ArrayList<>();
        List<Token> commas = new ArrayList<>();
        expNodes.add(Exp());
        while (curToken.getType().equals(TokenType.COMMA)) {
            commas.add(curToken);
            if (index < tokens.size() - 1) {
                curToken = tokens.get(++index);
            }
            expNodes.add(Exp());
        }
        return new FuncRParamsNode(expNodes, commas);
    }

    private ForStmtNode ForStmt() {
        // ForStmt → LVal '=' Exp
        LValNode lValNode = LVal();
        Token operator = curToken;
        if (index < tokens.size() - 1) {
            curToken = tokens.get(++index);
        }
        ExpNode expNode = Exp();
        return new ForStmtNode(lValNode, operator, expNode);
    }

    private CondNode Cond() {
        LOrExpNode lOrExpNode = LOrExp();
        return new CondNode(lOrExpNode);
    }


    private FuncFParamsNode FuncFParams() {
        List<FuncFParamNode> funcFParamNodes = new ArrayList<>();
        List<Token> commas = new ArrayList<>();

        funcFParamNodes.add(FuncFParam());
        while (curToken.getType().equals(TokenType.COMMA)) {
            commas.add(curToken);
            if (index < tokens.size() - 1) {
                curToken = tokens.get(++index);
            }
            funcFParamNodes.add(FuncFParam());
        }

        return new FuncFParamsNode(funcFParamNodes, commas);
    }

    private FuncFParamNode FuncFParam() {
        // FuncFParam → BType Ident ['[' ']'] // k
        BTypeNode bTypeNode = BType();

        Token identToken = curToken;
        if (index < tokens.size() - 1) {
            curToken = tokens.get(++index);
        }

        Token leftBracketToken = null;
        Token rightBracketToken = null;
        if (curToken.getType().equals(TokenType.LBRACK)) {
            leftBracketToken = curToken;
            if (index < tokens.size() - 1) {
                curToken = tokens.get(++index);
            }

            if (curToken.getType().equals(TokenType.RBRACK)) {
                rightBracketToken = curToken;
                if (index < tokens.size() - 1) {
                    curToken = tokens.get(++index);
                }
            } else {
                ErrorHandler.getInstance().addError(new Error(ErrorType.k, tokens.get(index - 1).getLine()));
                rightBracketToken = new Token(TokenType.RBRACK, "]", tokens.get(index - 1).getLine());
            }
        }
        return new FuncFParamNode(bTypeNode, identToken, leftBracketToken, rightBracketToken);
    }

    private FuncTypeNode FuncType() {
        // FuncType → 'void' | 'int' | 'char'
        Token funcTypeToken = curToken;
        if (index < tokens.size() - 1) {
            curToken = tokens.get(++index);
        }
        return new FuncTypeNode(funcTypeToken);
    }

    private MainFuncDefNode MainFuncDef() {
        // MainFuncDef → 'int' 'main' '(' ')' Block // j
        Token intToken = curToken;
        if (index < tokens.size() - 1) {
            curToken = tokens.get(++index);
        }

        Token mainToken = curToken;
        if (index < tokens.size() - 1) {
            curToken = tokens.get(++index);
        }

        Token leftParenToken = curToken;
        if (index < tokens.size() - 1) {
            curToken = tokens.get(++index);
        }

        Token rightParenToken = null;
        if (curToken.getType().equals(TokenType.RPARENT)) {
            rightParenToken = curToken;
            if (index < tokens.size() - 1) {
                curToken = tokens.get(++index);
            }
        } else {
            ErrorHandler.getInstance().addError(new Error(ErrorType.j, tokens.get(index - 1).getLine()));
            rightParenToken = new Token(TokenType.RPARENT, ")", tokens.get(index - 1).getLine());
        }

        BlockNode block = Block();
        return new MainFuncDefNode(intToken, mainToken, leftParenToken, rightParenToken, block);
    }

    private ConstDeclNode ConstDecl() {
        // ConstDecl -> 'const' BType ConstDef { ',' ConstDef } ';'

        // const
        Token constToken = curToken;
        if (index < tokens.size() - 1) {
            curToken = tokens.get(++index);
        }

        // BType
        BTypeNode bTypeNode = BType();

        // ConstDef
        List<ConstDefNode> constDefNodes = new ArrayList<>();
        List<Token> commas = new ArrayList<>();
        // 第一个ConstDef
        constDefNodes.add(ConstDef());
        while (curToken.getType().equals(TokenType.COMMA)) {
            commas.add(curToken);
            if (index < tokens.size() - 1) {
                curToken = tokens.get(++index);
            }
            constDefNodes.add(ConstDef());
        }
        // ;以及错误处理
        Token semicnToken;
        if (curToken.getType().equals(TokenType.SEMICN)) {
            semicnToken = curToken;
            if (index < tokens.size() - 1) {
                curToken = tokens.get(++index);
            }
        } else {
            // error
            ErrorHandler.getInstance().addError(new Error(ErrorType.i, tokens.get(index - 1).getLine()));
            semicnToken = new Token(TokenType.SEMICN, ";", tokens.get(index - 1).getLine());
        }
        return new ConstDeclNode(constToken, bTypeNode, constDefNodes, commas, semicnToken);
    }

    private VarDeclNode VarDecl() {
        // VarDecl → BType VarDef { ',' VarDef } ';'
        BTypeNode bTypeNode = BType();
        List<VarDefNode> varDefNodes = new ArrayList<>();
        List<Token> commas = new ArrayList<>();
        varDefNodes.add(VarDef());
        while (curToken.getType().equals(TokenType.COMMA)) {
            commas.add(curToken);
            if (index < tokens.size() - 1) {
                curToken = tokens.get(++index);
            }
            varDefNodes.add(VarDef());
        }
        Token semicnToken;
        if (curToken.getType().equals(TokenType.SEMICN)) {
            semicnToken = curToken;
            if (index < tokens.size() - 1) {
                curToken = tokens.get(++index);
            }
        } else {
            // error
            ErrorHandler.getInstance().addError(new Error(ErrorType.i, tokens.get(index - 1).getLine()));
            semicnToken = new Token(TokenType.SEMICN, ";", tokens.get(index - 1).getLine());
        }

        return new VarDeclNode(bTypeNode, commas, varDefNodes, semicnToken);
    }

    private VarDefNode VarDef() {
        //  VarDef → Ident [ '[' ConstExp ']' ] | Ident [ '[' ConstExp ']' ] '=' InitVal // k
        Token identToken = curToken;
        if (index < tokens.size() - 1) {
            curToken = tokens.get(++index);
        }
        Token leftBracket = null;
        ConstExpNode constExpNode = null;
        Token rightBracket = null;
        Token equalToken = null;
        InitValNode initValNode = null;

        while (curToken.getType().equals(TokenType.LBRACK)) {
            leftBracket = curToken;
            if (index < tokens.size() - 1) {
                curToken = tokens.get(++index);
            }
            constExpNode = ConstExp();
            // ] or error
            if (curToken.getType().equals(TokenType.RBRACK)) {
                rightBracket = curToken;
                if (index < tokens.size() - 1) {
                    curToken = tokens.get(++index);
                }
            } else {
                ErrorHandler.getInstance().addError(new Error(ErrorType.k, tokens.get(index - 1).getLine()));
                rightBracket = new Token(TokenType.RBRACK, "]", tokens.get(index - 1).getLine());
            }
        }

        while (curToken.getType().equals(TokenType.ASSIGN)) {
            equalToken = curToken;
            if (index < tokens.size() - 1) {
                curToken = tokens.get(++index);
            }
            initValNode = InitVal();
        }

        return new VarDefNode(identToken, leftBracket, constExpNode, rightBracket, equalToken, initValNode);
    }

    private InitValNode InitVal() {
        //  InitVal → Exp | '{' [ Exp { ',' Exp } ] '}' | StringConst
        ExpNode expNode = null;
        Token leftBrace = null;
        Token rightBrace = null;
        List<ExpNode> expNodes = new ArrayList<>();
        List<Token> commas = new ArrayList<>();
        Token stringToken = null;
        if (curToken.getType().equals(TokenType.STRCON)) {
            stringToken = curToken;
            if (index < tokens.size() - 1) {
                curToken = tokens.get(++index);
            }
        } else if (curToken.getType().equals(TokenType.LBRACE)) {
            leftBrace = curToken;
            if (index < tokens.size() - 1) {
                curToken = tokens.get(++index);
            }
            if (!curToken.getType().equals(TokenType.RBRACE)) {
                expNodes.add(Exp());
                while (!curToken.getType().equals(TokenType.RBRACE)) {
                    commas.add(curToken);
                    if (index < tokens.size() - 1) {
                        curToken = tokens.get(++index);
                    }
                    expNodes.add(Exp());
                }
            }
            rightBrace = curToken;
            if (index < tokens.size() - 1) {
                curToken = tokens.get(++index);
            }

        } else {
            expNode = Exp();
        }
        return new InitValNode(expNode, leftBrace, expNodes, rightBrace, commas, stringToken);
    }

    private ExpNode Exp() {
        AddExpNode addExpNode = AddExp();
        return new ExpNode(addExpNode);
    }

    private MulExpNode MulExp() {
        // MulExp → UnaryExp | MulExp ('*' | '/' | '%') UnaryExp
        UnaryExpNode unaryExpNode = UnaryExp();
        Token operator = null;
        MulExpNode mulExpNode = null;
        if (curToken.getType().equals(TokenType.MULT) || curToken.getType().equals(TokenType.DIV) || curToken.getType().equals(TokenType.MOD)) {
            operator = curToken;
            if (index < tokens.size() - 1) {
                curToken = tokens.get(++index);
            }
            mulExpNode = MulExp();
        }
        return new MulExpNode(unaryExpNode, operator, mulExpNode);
    }

    private AddExpNode AddExp() {
        // AddExp → MulExp | AddExp ('+' | '−') MulExp
        MulExpNode mulExpNode = MulExp();
        Token operator = null;
        AddExpNode addExpNode = null;
        if (curToken.getType().equals(TokenType.PLUS) || curToken.getType().equals(TokenType.MINU)) {
            operator = curToken;
            if (index < tokens.size() - 1) {
                curToken = tokens.get(++index);
            }
            addExpNode = AddExp();
        }
        return new AddExpNode(mulExpNode, operator, addExpNode);
    }

    private RelExpNode RelExp() {
        // RelExp → AddExp | RelExp ('<' | '>' | '<=' | '>=') AddExp
        AddExpNode addExpNode = AddExp();
        Token operator = null;
        RelExpNode relExpNode = null;
        if (curToken.getType().equals(TokenType.LSS) || curToken.getType().equals(TokenType.GRE) || curToken.getType().equals(TokenType.LEQ) || curToken.getType().equals(TokenType.GEQ)) {
            operator = curToken;
            if (index < tokens.size() - 1) {
                curToken = tokens.get(++index);
            }
            relExpNode = RelExp();
        }
        return new RelExpNode(addExpNode, operator, relExpNode);
    }

    private EqExpNode EqExp() {
        // EqExp → RelExp | EqExp ('==' | '!=') RelExp
        RelExpNode relExpNode = RelExp();
        Token operator = null;
        EqExpNode eqExpNode = null;
        if (curToken.getType().equals(TokenType.EQL) || curToken.getType().equals(TokenType.NEQ)) {
            operator = curToken;
            if (index < tokens.size() - 1) {
                curToken = tokens.get(++index);
            }
            eqExpNode = EqExp();
        }
        return new EqExpNode(relExpNode, operator, eqExpNode);
    }

    private LAndExpNode LAndExp() {
        //  LAndExp → EqExp | LAndExp '&&' EqExp
        EqExpNode eqExpNode = EqExp();
        Token operator = null;
        LAndExpNode lAndExpNode = null;
        if (curToken.getType().equals(TokenType.AND)) {
            operator = curToken;
            if (index < tokens.size() - 1) {
                curToken = tokens.get(++index);
            }
            lAndExpNode = LAndExp();
        }
        return new LAndExpNode(eqExpNode, operator, lAndExpNode);
    }

    private LOrExpNode LOrExp() {
        // LOrExp → LAndExp | LOrExp '||' LAndExp
        LAndExpNode lAndExpNode = LAndExp();
        Token operator = null;
        LOrExpNode lOrExpNode = null;
        if (curToken.getType().equals(TokenType.OR)) {
            operator = curToken;
            if (index < tokens.size() - 1) {
                curToken = tokens.get(++index);
            }
            lOrExpNode = LOrExp();
        }
        return new LOrExpNode(lAndExpNode, operator, lOrExpNode);
    }

    private BTypeNode BType() {
        // BType → 'int' | 'char'
        Token bTypeToken = curToken;
        if (index < tokens.size() - 1) {
            curToken = tokens.get(++index);
        }
        return new BTypeNode(bTypeToken);
    }

    private ConstDefNode ConstDef() {
        // ConstDef → Ident [ '[' ConstExp ']' ] '=' ConstInitVal
        Token identToken = curToken;
        if (index < tokens.size() - 1) {
            curToken = tokens.get(++index);
        }
        List<Token> leftBrackets = new ArrayList<>();
        List<ConstExpNode> constExpNodes = new ArrayList<>();
        List<Token> rightBrackets = new ArrayList<>();
        while (curToken.getType().equals(TokenType.LBRACK)) {
            leftBrackets.add(curToken);
            if (index < tokens.size() - 1) {
                curToken = tokens.get(++index);
            }
            constExpNodes.add(ConstExp());
            // ] or error
            if (curToken.getType().equals(TokenType.RBRACK)) {
                rightBrackets.add(curToken);
                if (index < tokens.size() - 1) {
                    curToken = tokens.get(++index);
                }
            } else {
                // error
                ErrorHandler.getInstance().addError(new Error(ErrorType.k, tokens.get(index - 1).getLine()));
                rightBrackets.add(new Token(TokenType.RBRACK, "]", tokens.get(index - 1).getLine()));
            }
        }
        Token equalsToken = curToken;
        if (index < tokens.size() - 1) {
            curToken = tokens.get(++index);
        }
        ConstInitValNode constInitValNode = ConstInitVal();

        return new ConstDefNode(identToken, leftBrackets, constExpNodes, rightBrackets, equalsToken, constInitValNode);
    }

    private ConstExpNode ConstExp() {
        // ConstExp → AddExp
        AddExpNode addExpNode = AddExp();
        return new ConstExpNode(addExpNode);
    }

    private ConstInitValNode ConstInitVal() {
        //  ConstInitVal → ConstExp | '{' [ ConstExp { ',' ConstExp } ] '}' | StringConst
        ConstExpNode constExpNode = null;
        Token leftBraceToken = null;
        Token rightBraceToken = null;
        List<ConstExpNode> constExpNodes = new ArrayList<>();
        List<Token> commas = new ArrayList<>();
        Token stringToken = null;
        if (curToken.getType().equals(TokenType.STRCON)) {
            stringToken = curToken;
            if (index < tokens.size() - 1) {
                curToken = tokens.get(++index);
            }
        } else if (curToken.getType().equals(TokenType.LBRACE)) {
            leftBraceToken = curToken;
            if (index < tokens.size() - 1) {
                curToken = tokens.get(++index);
            }
            if (!curToken.getType().equals(TokenType.RBRACE)) {
                constExpNodes.add(ConstExp());
                while (!curToken.getType().equals(TokenType.RBRACE)) {
                    commas.add(curToken);
                    if (index < tokens.size() - 1) {
                        curToken = tokens.get(++index);
                    }
                    constExpNodes.add(ConstExp());
                }
            }
            rightBraceToken = curToken;
            if (index < tokens.size() - 1) {
                curToken = tokens.get(++index);
            }
        } else {
            constExpNode = ConstExp();
        }

        return new ConstInitValNode(constExpNode, leftBraceToken, constExpNodes, commas, rightBraceToken, stringToken);
    }


}
