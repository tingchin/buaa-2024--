package semantic;

import error.Error;
import error.ErrorHandler;
import error.ErrorType;
import lexer.Token;
import lexer.TokenType;
import parser.astNode.*;
import symbol.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;


public class Visitor {
    private SymbolTable current;
    private int loop;
    private int layerCnt;
    private SymbolTable global;
    private int voidFun;
    private List<Token> tokens;


    public Visitor() {
        this.voidFun = 0;
        this.loop = 0;
        this.layerCnt = 1;
        this.global = new SymbolTable();
        this.current = global;
    }

    public void setTokens(List<Token> tokens) {
        this.tokens = tokens;
    }

    public void print() {
        global.print();
    }

    public void visit(CompUnitNode compUnit) {
        // CompUnit → {Decl} {FuncDef} MainFuncDef
        current = global;
        layerCnt = 1;
        current.setLayer(layerCnt);
        if (compUnit.getDecls() != null) {
            for (DeclNode decl : compUnit.getDecls()) {
                visitDecl(decl);
            }
        }

        if (compUnit.getFunc() != null) {
            for (FuncDefNode funcDefNode : compUnit.getFunc()) {
                visitFuncDef(funcDefNode);
            }
        }

        visitMainFuncDef(compUnit.getMainFun());
    }

    private void visitDecl(DeclNode decl) {
        // Decl → ConstDecl | VarDecl
        if (decl.getConstDecl() != null) {
            visitConstDecl(decl.getConstDecl());
        } else {
            visitVarDecl(decl.getVarDecl());
        }
    }

    private void visitConstDecl(ConstDeclNode constDecl) {
        //  ConstDecl → 'const' BType ConstDef { ',' ConstDef } ';'
        String type;
        if (constDecl.getbTypeNode().getToken().getType().equals(TokenType.INTTK)) {
            type = "int";
        } else {
            type = "char";
        }
        if (constDecl.getConstDefNodes() != null) {
            for (ConstDefNode constDef : constDecl.getConstDefNodes()) {
                visitConstDef(type, constDef);
            }
        }
    }

    private void visitConstExp(ConstExpNode constExp) {
        //  ConstExp → AddExp
        visitAddExp(constExp.getAddExp());
    }

    private void visitConstInitVal(ConstInitValNode constInitVal) {
        // ConstInitVal → ConstExp | '{' [ ConstExp { ',' ConstExp } ] '}' | StringConst
        if (constInitVal.getConstExp() != null) {
            visitConstExp(constInitVal.getConstExp());
        } else {
            assert constInitVal.getConstExpNodes() != null;
            for (ConstExpNode constExpNode : constInitVal.getConstExpNodes()) {
                visitConstExp(constExpNode);
            }
        }
    }

    private void visitConstDef(String type, ConstDefNode constDef) {
        // ConstDef → Ident [ '[' ConstExp ']' ] '=' ConstInitVal // b
        String name = constDef.getIdentToken().getValue();
        if (!constDef.getLeftBrackets().isEmpty()) {
            // error check
            if (current.getSymbolTable().containsKey(name)) {
                ErrorHandler.getInstance().addError(new Error(ErrorType.b, constDef.getIdentToken().getLine()));
                return;
            } else {
                if (type.equals("int")) {
                    current.getSymbolTable().put(name, new ArrayVariableSymbol(SymbolType.ConstIntArray, name, current.getLayer()));
                } else {
                    current.getSymbolTable().put(name, new ArrayVariableSymbol(SymbolType.ConstCharArray, name, current.getLayer()));
                }
            }
            if (constDef.getConstExpNodes() != null) {
                for (ConstExpNode constExpNode : constDef.getConstExpNodes()) {
                    visitConstExp(constExpNode);
                }
            }
            if (constDef.getConstInitValNode() != null) {
                visitConstInitVal(constDef.getConstInitValNode());
            }
        } else {
            // error check
            if (current.getSymbolTable().containsKey(name)) {
                ErrorHandler.getInstance().addError(new Error(ErrorType.b, constDef.getIdentToken().getLine()));
                return;
            } else {
                if (type.equals("int")) {
                    current.getSymbolTable().put(name, new VariableSymbol(SymbolType.ConstInt, name, current.getLayer()));
                } else {
                    current.getSymbolTable().put(name, new VariableSymbol(SymbolType.ConstChar, name, current.getLayer()));
                }
            }
            if (constDef.getConstExpNodes() != null) {
                for (ConstExpNode constExpNode : constDef.getConstExpNodes()) {
                    visitConstExp(constExpNode);
                }
            }
            if (constDef.getConstInitValNode() != null) {
                visitConstInitVal(constDef.getConstInitValNode());
            }
        }
    }

    private void visitVarDecl(VarDeclNode varDecl) {
        // VarDecl → BType VarDef { ',' VarDef } ';'
        String type;
        if (varDecl.getbType().getToken().getType().equals(TokenType.INTTK)) {
            type = "int";
        } else {
            type = "char";
        }
        if (varDecl.getVarDefs() != null) {
            for (VarDefNode varDef : varDecl.getVarDefs()) {
                visitVarDef(type, varDef);
            }
        }
    }

    private void visitVarDef(String type, VarDefNode varDef) {
        // VarDef → Ident [ '[' ConstExp ']' ] | Ident [ '[' ConstExp ']' ] '=' InitVal // b
        String name = varDef.getIdentNode().getValue();
        if (varDef.getLeftBracket() != null) {
            // error check
            if (current.getSymbolTable().containsKey(name)) {
                ErrorHandler.getInstance().addError(new Error(ErrorType.b, varDef.getIdentNode().getLine()));
                return;
            } else {
                if (type.equals("int")) {
                    current.getSymbolTable().put(name, new ArrayVariableSymbol(SymbolType.IntArray, name, current.getLayer()));
                } else {
                    current.getSymbolTable().put(name, new ArrayVariableSymbol(SymbolType.CharArray, name, current.getLayer()));
                }
            }
            if (varDef.getConstExpNode() != null) {
                visitConstExp(varDef.getConstExpNode());
            }
            if (varDef.getInitValNode() != null) {
                visitInitVal(varDef.getInitValNode());
            }
        } else {
            // error check
            if (current.getSymbolTable().containsKey(name)) {
                ErrorHandler.getInstance().addError(new Error(ErrorType.b, varDef.getIdentNode().getLine()));
                return;
            } else {
                if (type.equals("int")) {
                    current.getSymbolTable().put(name, new VariableSymbol(SymbolType.Int, name, current.getLayer()));
                } else {
                    current.getSymbolTable().put(name, new VariableSymbol(SymbolType.Char, name, current.getLayer()));
                }
            }
            if (varDef.getConstExpNode() != null) {
                visitConstExp(varDef.getConstExpNode());
            }
            if (varDef.getInitValNode() != null) {
                visitInitVal(varDef.getInitValNode());
            }
        }
    }

    private void visitInitVal(InitValNode initVal) {
        // //  InitVal → Exp | '{' [ Exp { ',' Exp } ] '}' | StringConst
        if (initVal.getExpr() != null) {
            visitExp(initVal.getExpr());
        } else {
            assert initVal.getExpNodes() != null;
            for (ExpNode expNode : initVal.getExpNodes()) {
                visitExp(expNode);
            }
        }
    }

    //
    private void visitFuncDef(FuncDefNode funcDef) {
        // FuncDef → FuncType Ident '(' [FuncFParams] ')' Block // b g

        // 函数返回值
        SymbolType type = null;
        if (funcDef.getFuncType().getFuncType().getType().equals(TokenType.INTTK)) {
            type = SymbolType.IntFunc;
        } else if (funcDef.getFuncType().getFuncType().getType().equals(TokenType.CHARTK)) {
            type = SymbolType.CharFunc;
        } else {
            type = SymbolType.VoidFunc;
        }

        // 函数名称 error b

        String funName = funcDef.getIdentToken().getValue();
        FunctionSymbol functionSymbol = null;
        if (current.getSymbolTable().containsKey(funName)) {
            ErrorHandler.getInstance().addError(new Error(ErrorType.b, funcDef.getIdentToken().getLine()));
            return;
        } else {
            functionSymbol = new FunctionSymbol(type, funName, current.getLayer());
            current.getSymbolTable().put(funName, functionSymbol);
        }

        // 函数参数
        List<FunctionParam> params = new ArrayList<>();
        // 下一层符号表
        goToNextTable();
        if (funcDef.getFuncFParams() != null) {
            // 同时把参数加入
            visitFuncFParams(funcDef.getFuncFParams(), params);
        }
        functionSymbol.setParams(params);

        if (type.equals(SymbolType.VoidFunc)) {
            voidFun++;
        }
        visitBlock(funcDef.getBlock());
        if (type.equals(SymbolType.VoidFunc)) {
            voidFun--;
        }
        // 符号表回来
        current = current.getLastLayer();
//        // 为函数符号添加参数
//        addParamToFunDef(funcDef.getFuncFParams());


        // 检查 return 是否存在 g错误
//        if (!type.equals(SymbolType.VoidFunc)) {
//            if (!checkReturn(funcDef.getBlock())) {
//                ErrorHandler.getInstance().addError(new Error(ErrorType.g, funcDef.getBlock().getRightBrace().getLine()));
//            }
//        }
        if (!type.equals(SymbolType.VoidFunc)) {
//            if (funcDef.getBlock().getBlockItemNodes().isEmpty()) {
//                return;
//            }
//            BlockItemNode blockItemNode = funcDef.getBlock().getBlockItemNodes().get(funcDef.getBlock().getBlockItemNodes().size() - 1);
//            if (!blockItemNode.getStmt().getType().equals(StmtNode.StmtType.Return)) {
//                ErrorHandler.getInstance().addError(new Error(ErrorType.g, funcDef.getBlock().getRightBrace().getLine()));
//            }
            if (!checkReturn(funcDef.getBlock())) {
                ErrorHandler.getInstance().addError(new Error(ErrorType.g, funcDef.getBlock().getRightBrace().getLine()));
            }
        }
    }

    private boolean checkReturn(BlockNode block) {
        List<BlockItemNode> blockItemNodes = block.getBlockItemNodes();
        boolean hasReturn = false;
        for (BlockItemNode blockItemNode : blockItemNodes) {
            if (blockItemNode.getDecl() != null) {
                continue;
            }
            // stmt to check return
            if (blockItemNode.getStmt().getType().equals(StmtNode.StmtType.Return)) {
                hasReturn = true;
            }
        }
        return hasReturn;
    }


    //
    private void visitFuncFParams(FuncFParamsNode funcFParamsNode, List<FunctionParam> params) {
        // FuncFParams → FuncFParam { ',' FuncFParam }
        if (funcFParamsNode.getParams() != null) {
            for (FuncFParamNode param : funcFParamsNode.getParams()) {
                visitFuncFParam(param, params);
            }
        }

    }

    //
    private void visitFuncFParam(FuncFParamNode funcFParamNode, List<FunctionParam> params) {
        // FuncFParam → BType Ident ['[' ']'] // b
        String paramName = funcFParamNode.getIdentToken().getValue();
        SymbolType type = null;
        if (funcFParamNode.getbTypeNode().getToken().getType().equals(TokenType.INTTK)) {
            if (funcFParamNode.getLeftBracket() != null) {
                type = SymbolType.IntArray;
            } else {
                type = SymbolType.Int;
            }
        } else {
            if (funcFParamNode.getLeftBracket() != null) {
                type = SymbolType.CharArray;
            } else {
                type = SymbolType.Char;
            }
        }

        // error !!!!!!!!!!!!
        if (current.getSymbolTable().containsKey(paramName)) {
            ErrorHandler.getInstance().addError(new Error(ErrorType.b, funcFParamNode.getIdentToken().getLine()));
        } else {
            current.getSymbolTable().put(paramName, new VariableSymbol(type, paramName, current.getLayer()));
            params.add(new FunctionParam(type, paramName, current.getLayer()));
        }


    }

    //
    private void visitBlock(BlockNode block) {
        // Block → '{' { BlockItem } '}'
        for (BlockItemNode blockItemNode : block.getBlockItemNodes()) {
            visitBlockItem(blockItemNode);
        }
    }

    //
    private void visitBlockItem(BlockItemNode blockItemNode) {
        // BlockItem → Decl | Stmt
        if (blockItemNode.getDecl() != null) {
            visitDecl(blockItemNode.getDecl());
        } else {
            visitStmt(blockItemNode.getStmt());
        }
    }

    // TODO
    private void visitStmt(StmtNode stmtNode) {
        // Stmt → LVal '=' Exp ';' // h
        //| [Exp] ';'
        //| Block
        //| 'if' '(' Cond ')' Stmt [ 'else' Stmt ]
        //| 'for' '(' [ForStmt] ';' [Cond] ';' [ForStmt] ')' Stmt // h
        //| 'break' ';' | 'continue' ';' // m
        //| 'return' [Exp] ';' // f
        //| LVal '=' 'getint''('')'';' // h
        //| LVal '=' 'getchar''('')'';' // h
        //| 'printf''('StringConst {','Exp}')'';' // l
        StmtNode.StmtType type = stmtNode.getType();
        if (type.equals(StmtNode.StmtType.LVal)) {
            // LVal '=' Exp ';' // h
            if (visitLVal(stmtNode.getlValNode())) {
                // error c
                return;
            }
            Symbol lVal = findVariable(stmtNode.getlValNode().getIdentToken().getValue());
            assert lVal != null;
            if (lVal.getType().equals(SymbolType.ConstChar)
                    || lVal.getType().equals(SymbolType.ConstInt)
                    || lVal.getType().equals(SymbolType.ConstCharArray)
                    || lVal.getType().equals(SymbolType.ConstIntArray)) {
                ErrorHandler.getInstance().addError(new Error(ErrorType.h, stmtNode.getlValNode().getIdentToken().getLine()));
                return;
            }
            visitExp(stmtNode.getExpNode());

        } else if (type.equals(StmtNode.StmtType.Exp)) {
            if (stmtNode.getExpNode() != null) {
                visitExp(stmtNode.getExpNode());
            }

        } else if (type.equals(StmtNode.StmtType.Block)) {
            //| Block
            // 加一层作用域
            goToNextTable();
            visitBlock(stmtNode.getBlockNode());
            // 结束后返回上一层
            current = current.getLastLayer();
        } else if (type.equals(StmtNode.StmtType.IF)) {
            //| 'if' '(' Cond ')' Stmt [ 'else' Stmt ]
            visitCond(stmtNode.getCondNode());
            for (StmtNode node : stmtNode.getStmtNodes()) {
                visitStmt(node);
            }

        } else if (type.equals(StmtNode.StmtType.FOR)) {
            //| 'for' '(' [ForStmt] ';' [Cond] ';' [ForStmt] ')' Stmt // h
            // forStmt
            if (stmtNode.getForStmtNode1() != null) {
                visitForStmt(stmtNode.getForStmtNode1());
            }
            if (stmtNode.getCondNode() != null) {
                visitCond(stmtNode.getCondNode());
            }
            if (stmtNode.getForStmtNode2() != null) {
                visitForStmt(stmtNode.getForStmtNode2());
            }
            loop++;
            // stmt
            visitStmt(stmtNode.getStmtNode());
            loop--;

        } else if (type.equals(StmtNode.StmtType.Break)) {
            //| 'break' ';' | 'continue' ';' // m
            if (loop == 0) {
                ErrorHandler.getInstance().addError(new Error(ErrorType.m, stmtNode.getBreakOrContinueToken().getLine()));
            }
        } else if (type.equals(StmtNode.StmtType.Continue)) {
            //| 'break' ';' | 'continue' ';' // m
            if (loop == 0) {
                ErrorHandler.getInstance().addError(new Error(ErrorType.m, stmtNode.getBreakOrContinueToken().getLine()));
            }
        } else if (type.equals(StmtNode.StmtType.Return)) {
            //| 'return' [Exp] ';' // f
            if (voidFun != 0 && stmtNode.getExpNode() != null) {
                ErrorHandler.getInstance().addError(new Error(ErrorType.f, stmtNode.getReturnToken().getLine()));
                return;
            }
            if (stmtNode.getExpNode() != null) {
                visitExp(stmtNode.getExpNode());
            }
        } else if (type.equals(StmtNode.StmtType.GetInt)) {
            //| LVal '=' 'getint''('')'';' // h
            if (visitLVal(stmtNode.getlValNode())) {
                return;
            }
            Symbol lVal = findVariable(stmtNode.getlValNode().getIdentToken().getValue());
            assert lVal != null;
            if (lVal.getType().equals(SymbolType.ConstChar)
                    || lVal.getType().equals(SymbolType.ConstInt)
                    || lVal.getType().equals(SymbolType.ConstCharArray)
                    || lVal.getType().equals(SymbolType.ConstIntArray)) {
                ErrorHandler.getInstance().addError(new Error(ErrorType.h, stmtNode.getlValNode().getIdentToken().getLine()));
            }
        } else if (type.equals(StmtNode.StmtType.GetChar)) {
            //| LVal '=' 'getint''('')'';' // h
            if (visitLVal(stmtNode.getlValNode())) {
                return;
            }
            Symbol lVal = findVariable(stmtNode.getlValNode().getIdentToken().getValue());
            assert lVal != null;
            if (lVal.getType().equals(SymbolType.ConstChar)
                    || lVal.getType().equals(SymbolType.ConstInt)
                    || lVal.getType().equals(SymbolType.ConstCharArray)
                    || lVal.getType().equals(SymbolType.ConstIntArray)) {
                ErrorHandler.getInstance().addError(new Error(ErrorType.h, stmtNode.getlValNode().getIdentToken().getLine()));
            }
        } else if (type.equals(StmtNode.StmtType.Printf)) {
            //| 'printf''('StringConst {','Exp}')'';' // l
            String formatString = stmtNode.getStringToken().getValue();
            int lLen = 0;
            lLen += formatString.split("%d").length + formatString.split("%c").length - 2;
            int rLen = 0;
            rLen = stmtNode.getExpNodes().size();
            if (lLen != rLen) {
                ErrorHandler.getInstance().addError(new Error(ErrorType.l, stmtNode.getPrintfToken().getLine()));
            }
            for (ExpNode expNode : stmtNode.getExpNodes()) {
                visitExp(expNode);
            }
        }

    }

    private void visitCond(CondNode condNode) {
        // Cond → LOrExp
        visitLOrExp(condNode.getlOrExp());
    }

    private void visitLOrExp(LOrExpNode lOrExpNode) {
        // LOrExp → LAndExp | LOrExp '||' LAndExp
        visitLAndExp(lOrExpNode.getlAndExpNode());
        if (lOrExpNode.getlOrExpNode() != null) {
            visitLOrExp(lOrExpNode.getlOrExpNode());
        }
    }

    private void visitLAndExp(LAndExpNode lAndExpNode) {
        //  LAndExp → EqExp | LAndExp '&&' EqExp
        visitEqExp(lAndExpNode.getEqExp());
        if (lAndExpNode.getlAndExp() != null) {
            visitLAndExp(lAndExpNode.getlAndExp());
        }
    }

    private void visitEqExp(EqExpNode eqExpNode) {
        // EqExp → RelExp | EqExp ('==' | '!=') RelExp
        visitRelExp(eqExpNode.getRelExpNode());
        if (eqExpNode.getEqExpNode() != null) {
            visitEqExp(eqExpNode.getEqExpNode());
        }
    }

    private void visitRelExp(RelExpNode relExpNode) {
        // RelExp → AddExp | RelExp ('<' | '>' | '<=' | '>=') AddExp
        visitAddExp(relExpNode.getAddExp());
        if (relExpNode.getRelExp() != null) {
            visitRelExp(relExpNode.getRelExp());
        }
    }



    //
    private void visitForStmt(ForStmtNode forStmtNode) {
        //  ForStmt → LVal '=' Exp // h
        if (visitLVal(forStmtNode.getlValNode())) {
            // error
            return;
        }
        Symbol lVal = findVariable(forStmtNode.getlValNode().getIdentToken().getValue());
        assert lVal != null;
        if (lVal.getType().equals(SymbolType.ConstChar)
                || lVal.getType().equals(SymbolType.ConstInt)
                || lVal.getType().equals(SymbolType.ConstCharArray)
                || lVal.getType().equals(SymbolType.ConstIntArray)) {
            ErrorHandler.getInstance().addError(new Error(ErrorType.h, forStmtNode.getlValNode().getIdentToken().getLine()));
            return;
        }
        visitExp(forStmtNode.getExpNode());
    }

    private Symbol findVariable(String name) {
        SymbolTable temp = current;
        while (temp != null) {
            if (temp.getSymbolTable().containsKey(name)) {
                return temp.getSymbolTable().get(name);
            }
            temp = temp.getLastLayer();
        }
        return null;
    }

    //
    private boolean visitLVal(LValNode lValNode) {
        // LVal → Ident ['[' Exp ']'] // c
        boolean hasErrorC = false;
        Symbol ident = findVariable(lValNode.getIdentToken().getValue());
        if (ident == null) {
            ErrorHandler.getInstance().addError(new Error(ErrorType.c, lValNode.getIdentToken().getLine()));
            hasErrorC = true;
            return hasErrorC;
        } else {
//            if (lValNode.getExpNode() != null) {
//                visitExp(lValNode.getExpNode());
//            }
            return hasErrorC;
        }
    }

    //
    private void visitExp(ExpNode expNode) {
        if (expNode.getAddExpNode() != null) {
            visitAddExp(expNode.getAddExpNode());
        }
    }

    private void visitAddExp(AddExpNode addExpNode) {
        //  AddExp → MulExp | AddExp ('+' | '−') MulExp
        if (addExpNode.getMulExp() != null) {
            visitMulExp(addExpNode.getMulExp());
        }

        if (addExpNode.getAddExp() != null) {
            visitAddExp(addExpNode.getAddExp());
        }
    }

    private void visitMulExp(MulExpNode mulExpNode) {
        //  MulExp → UnaryExp | MulExp ('*' | '/' | '%') UnaryExp
        if (mulExpNode.getUnaryExp() != null) {
            visitUnaryExp(mulExpNode.getUnaryExp());
        }

        if (mulExpNode.getMulExp() != null) {
            visitMulExp(mulExpNode.getMulExp());
        }
    }

    private void visitUnaryExp(UnaryExpNode unaryExpNode) {
        // UnaryExp → PrimaryExp | Ident '(' [FuncRParams] ')' | UnaryOp UnaryExp // c d e
        if (unaryExpNode.getPrimary() != null) {
            visitPrimaryExp(unaryExpNode.getPrimary());
        } else if (unaryExpNode.getIdentToken() != null) {
            // 函数调用
            Symbol fun = findVariable(unaryExpNode.getIdentToken().getValue());
            if (fun == null) {
                // error c
                ErrorHandler.getInstance().addError(new Error(ErrorType.c, unaryExpNode.getIdentToken().getLine()));
                return;
            }

            fun = (FunctionSymbol)fun;
            // 检查函数参数个数
            if (((FunctionSymbol) fun).getParams().isEmpty()) {
                if (unaryExpNode.getFuncRParams() != null) {
                    // error d
                    ErrorHandler.getInstance().addError(new Error(ErrorType.d, unaryExpNode.getIdentToken().getLine()));
                    return;
                }
            } else {
                if (unaryExpNode.getFuncRParams() == null || unaryExpNode.getFuncRParams().getExpNodes().size() != ((FunctionSymbol) fun).getParams().size()) {
                    ErrorHandler.getInstance().addError(new Error(ErrorType.d, unaryExpNode.getIdentToken().getLine()));
                    return;
                }
            }

            // 检查函数参数类型是否匹配
            if (((FunctionSymbol) fun).getParams().isEmpty()) {
                // 空参数
                return;
            }
            int index = unaryExpNode.getDefineIndex(); // index是第一个参数
            for (int i = 0; i < ((FunctionSymbol) fun).getParams().size(); i++) {
                // 确定形参
                int Stype = -1;
                if (((FunctionSymbol) fun).getParams().get(i).getType().equals(SymbolType.IntArray)) {
                    Stype = 1;
                } else if (((FunctionSymbol) fun).getParams().get(i).getType().equals(SymbolType.CharArray)) {
                    Stype = 2;
                } else {
                    Stype = 0;
                }
                // 确定实参
                int Rtype = -1;
                Rtype = getRParamType(unaryExpNode.getFuncRParams().getExpNodes().get(i));
                if (Rtype != Stype) {
                    ErrorHandler.getInstance().addError(new Error(ErrorType.e, unaryExpNode.getIdentToken().getLine()));
                }

            }
//            for (FunctionParam param : ((FunctionSymbol) fun).getParams()) { // 遍历每一个形参
//                while (!tokens.get(index).getType().equals(TokenType.IDENFR) && !tokens.get(index).getType().equals(TokenType.INTCON) && !tokens.get(index).getType().equals(TokenType.CHRCON)) {
//                    index++;
//                }
//                // 确定实参类型 var:0; intArr:1; charArr:2
//                int Rtype = -1;
//                if (tokens.get(index).getType().equals(TokenType.INTCON) || tokens.get(index).getType().equals(TokenType.CHRCON)) {
//                    Rtype = 0;
//                } else {
//                    Symbol variable = findVariable(tokens.get(index).getValue());
//                    assert variable != null;
//                    if (variable.getType().equals(SymbolType.IntArray) && !tokens.get(index + 1).getType().equals(TokenType.LBRACK)) {
//                        Rtype = 1;
//                    } else if (variable.getType().equals(SymbolType.CharArray) && !tokens.get(index + 1).getType().equals(TokenType.LBRACK)) {
//                        Rtype = 2;
//                    } else {
//                        Rtype = 0;
//                    }
//                }
//                // 确定形参类型
//                int Stype = -1;
//                if (param.getType().equals(SymbolType.IntArray)) {
//                    Stype = 1;
//                } else if (param.getType().equals(SymbolType.CharArray)) {
//                    Stype = 2;
//                } else {
//                    Stype = 0;
//                }
//                // 比较
//                if (Rtype != Stype) {
//                    ErrorHandler.getInstance().addError(new Error(ErrorType.e, unaryExpNode.getIdentToken().getLine()));
//
//                }
//                // 调到,
//                while (!tokens.get(index).getType().equals(TokenType.COMMA) && !tokens.get(index).getType().equals(TokenType.SEMICN)) {
//                    index++;
//                }
//            }


        } else {
            visitUnaryExp(unaryExpNode.getUnaryExp());
        }
    }

    private int getRParamType(ExpNode expNode) {
        return checkExp(expNode);
    }

    private int checkExp(ExpNode expNode) {
        return checkAddExp(expNode.getAddExpNode());
    }

    private int checkAddExp(AddExpNode addExpNode) {
        return checkMulExp(addExpNode.getMulExp());
    }

    private int checkMulExp(MulExpNode mulExp) {
        return checkUnaryExp(mulExp.getUnaryExp());
    }

    private int checkUnaryExp(UnaryExpNode unaryExp) {
        if (unaryExp.getIdentToken() != null) {
            return 0;
        } else if (unaryExp.getPrimary() != null) {
            return checkPrimaryExp(unaryExp.getPrimary());
        } else {
            return checkUnaryExp(unaryExp.getUnaryExp());
        }
    }

    private int checkPrimaryExp(PrimaryExpNode primary) {
        if (primary.getExpNode() != null) {
            return checkExp(primary.getExpNode());
        } else if (primary.getlValNode() != null) {
            return checkLVal(primary.getlValNode());
        } else {
            return 0;
        }
    }

    private int checkLVal(LValNode lValNode) {
        Symbol variable = findVariable(lValNode.getIdentToken().getValue());
        assert variable != null;
        if (variable.getType().equals(SymbolType.IntArray) && lValNode.getLeftBracketToken() == null) {
            return 1;
        } else if (variable.getType().equals(SymbolType.CharArray) && lValNode.getLeftBracketToken() == null) {
            return 2;
        } else {
            return 0;
        }
    }

    private void visitPrimaryExp(PrimaryExpNode primaryExpNode) {
        // PrimaryExp → '(' Exp ')' | LVal | Number | Character
        if (primaryExpNode.getExpNode() != null) {
            visitExp(primaryExpNode.getExpNode());
        } else if (primaryExpNode.getlValNode() != null) {
            visitLVal(primaryExpNode.getlValNode());
        }
    }

    //
    private void visitMainFuncDef(MainFuncDefNode mainFuncDef) {
        //  MainFuncDef → 'int' 'main' '(' ')' Block // g
        goToNextTable();
        visitBlock(mainFuncDef.getBlock());
        current = current.getLastLayer();

        if (!checkReturn(mainFuncDef.getBlock())) {
            ErrorHandler.getInstance().addError(new Error(ErrorType.g, mainFuncDef.getBlock().getRightBrace().getLine()));
        }
    }

    private void goToNextTable() {
        layerCnt++;
        SymbolTable tempCur = current;
        SymbolTable newTable = new SymbolTable(tempCur, new LinkedHashMap<String, Symbol>(), layerCnt, new ArrayList<SymbolTable>());
        current.getNextLayer().add(newTable);
        current = newTable;
    }

}
