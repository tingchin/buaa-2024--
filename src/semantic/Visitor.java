package semantic;

import error.Error;
import error.ErrorHandler;
import error.ErrorType;
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

    public Visitor() {
        this.loop = 0;
        this.layerCnt = 1;
        this.global = new SymbolTable();
        this.current = global;
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

    private void visitConstDef(String type, ConstDefNode constDef) {
        // ConstDef → Ident [ '[' ConstExp ']' ] '=' ConstInitVal // b
        String name = constDef.getIdentToken().getValue();
        if (!constDef.getLeftBrackets().isEmpty()) {
            // error check
            if (current.getSymbolTable().containsKey(name)) {
                ErrorHandler.getInstance().addError(new Error(ErrorType.b, constDef.getIdentToken().getLine()));
            } else {
                if (type.equals("int")) {
                    current.getSymbolTable().put(name, new ArrayVariableSymbol(SymbolType.ConstIntArray, name, current.getLayer()));
                } else {
                    current.getSymbolTable().put(name, new ArrayVariableSymbol(SymbolType.ConstCharArray, name, current.getLayer()));
                }
            }
        } else {
            // error check
            if (current.getSymbolTable().containsKey(name)) {
                ErrorHandler.getInstance().addError(new Error(ErrorType.b, constDef.getIdentToken().getLine()));
            } else {
                if (type.equals("int")) {
                    current.getSymbolTable().put(name, new VariableSymbol(SymbolType.ConstInt, name, current.getLayer()));
                } else {
                    current.getSymbolTable().put(name, new VariableSymbol(SymbolType.ConstChar, name, current.getLayer()));
                }
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
            } else {
                if (type.equals("int")) {
                    current.getSymbolTable().put(name, new ArrayVariableSymbol(SymbolType.IntArray, name, current.getLayer()));
                } else {
                    current.getSymbolTable().put(name, new ArrayVariableSymbol(SymbolType.CharArray, name, current.getLayer()));
                }
            }
        } else {
            // error check
            if (current.getSymbolTable().containsKey(name)) {
                ErrorHandler.getInstance().addError(new Error(ErrorType.b, varDef.getIdentNode().getLine()));
            } else {
                if (type.equals("int")) {
                    current.getSymbolTable().put(name, new VariableSymbol(SymbolType.Int, name, current.getLayer()));
                } else {
                    current.getSymbolTable().put(name, new VariableSymbol(SymbolType.Char, name, current.getLayer()));
                }
            }
        }
    }

    // TODO
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

        // 函数名称 error
        boolean hasErrorB = false;
        String funName = funcDef.getIdentToken().getValue();
        if (current.getSymbolTable().containsKey(funName)) {
            ErrorHandler.getInstance().addError(new Error(ErrorType.b, funcDef.getIdentToken().getLine()));
            hasErrorB = true;
        } else {
            current.getSymbolTable().put(funName, new FunctionSymbol(type, funName, current.getLayer()));
        }

        // 函数参数
        List<FunctionParam> params = new ArrayList<>();
        // 下一层符号表
        goToNextTable();
        if (funcDef.getFuncFParams() != null) {
            visitFuncFParams(funcDef.getFuncFParams());
        }

        visitBlock(funcDef.getBlock());

        // 符号表回来
        current = current.getLastLayer();
        // 为函数符号添加参数
        addParamToFunDef();
    }

    // TODO
    private void addParamToFunDef() {

    }

    //
    private void visitFuncFParams(FuncFParamsNode funcFParamsNode) {
        // FuncFParams → FuncFParam { ',' FuncFParam }
        if (funcFParamsNode.getParams() != null) {
            for (FuncFParamNode param : funcFParamsNode.getParams()) {
                visitFuncFParam(param);
            }
        }

    }

    // TODO
    private void visitFuncFParam(FuncFParamNode funcFParamNode) {
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
        current.getSymbolTable().put(paramName, new VariableSymbol(type, paramName, current.getLayer()));

    }

    // TODO
    private void visitBlock(BlockNode block) {
        // Block → '{' { BlockItem } '}'
        for (BlockItemNode blockItemNode : block.getBlockItemNodes()) {
            visitBlockItem(blockItemNode);
        }
    }

    // TODO
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

        } else if (type.equals(StmtNode.StmtType.Exp)) {

        } else if (type.equals(StmtNode.StmtType.Block)) {
            //| Block
            // 加一层作用域
            goToNextTable();
            visitBlock(stmtNode.getBlockNode());
            // 结束后返回上一层
            current = current.getLastLayer();
        } else if (type.equals(StmtNode.StmtType.IF)) {
            //| 'if' '(' Cond ')' Stmt [ 'else' Stmt ]
            for (StmtNode node : stmtNode.getStmtNodes()) {
                visitStmt(node);
            }

        } else if (type.equals(StmtNode.StmtType.FOR)) {
            //| 'for' '(' [ForStmt] ';' [Cond] ';' [ForStmt] ')' Stmt // h
            //

            // stmt
            visitStmt(stmtNode.getStmtNode());

        } else if (type.equals(StmtNode.StmtType.Break)) {

        } else if (type.equals(StmtNode.StmtType.Return)) {

        } else if (type.equals(StmtNode.StmtType.GetInt)) {

        } else if (type.equals(StmtNode.StmtType.GetChar)) {

        } else if (type.equals(StmtNode.StmtType.Printf)) {

        }

    }

    // TODO
    private void visitMainFuncDef(MainFuncDefNode mainFuncDef) {
        //  MainFuncDef → 'int' 'main' '(' ')' Block // g
        goToNextTable();
        visitBlock(mainFuncDef.getBlock());
        current = current.getLastLayer();
    }

    private void goToNextTable() {
        layerCnt++;
        SymbolTable tempCur = current;
        SymbolTable newTable = new SymbolTable(tempCur, new LinkedHashMap<String, Symbol>(), layerCnt, new ArrayList<SymbolTable>());
        current.getNextLayer().add(newTable);
        current = newTable;
    }

}
