package ir;

import ir.instruction.Operator;
import ir.instruction.mem.GEP;
import ir.types.*;
import ir.values.*;
import lexer.TokenType;
import parser.astNode.*;
import symbol.Symbol;
import symbol.SymbolTable;

import java.util.*;

public class IRVisitor {
    private static final IRVisitor instance = new IRVisitor();

    public static IRVisitor getInstance() {
        return instance;
    }

    private BuildFactory buildFactory = BuildFactory.getInstance();

    private BasicBlock curBlock = null;
    private BasicBlock curTrueBlock = null;
    private BasicBlock curFalseBlock = null;
    private BasicBlock continueBlock = null;
    private BasicBlock curFinalBlock = null;
    private BasicBlock curForBlock = null;
    private Function curFunction = null;


    private SymbolTable table = new SymbolTable();
    private SymbolTable curTable = new SymbolTable();

    private List<Map<String, Value>> symbolTable = new ArrayList<>();

    public Map<String, Value> getCurTable() {
        return symbolTable.get(symbolTable.size() - 1);
    }

    public void addSymbol(String name, Value value) {
        getCurTable().put(name, value);
    }

    public void addGlobalSymbol(String name, Value value) {
        symbolTable.get(0).put(name, value);
    }

    public Value getValue(String name) {
        for (int i = symbolTable.size() - 1; i >= 0; i--) {
            if (symbolTable.get(i).containsKey(name)) {
                return symbolTable.get(i).get(name);
            }
        }
        return null;
    }

    private List<Map<String, Integer>> constTable = new ArrayList<>();

    public Map<String, Integer> getCurConstTable() {
        return constTable.get(constTable.size() - 1);
    }

    public void addConst(String name, Integer value) {
        getCurConstTable().put(name, value);
    }

    public Integer getConst(String name) {
        for (int i = constTable.size() - 1; i >= 0; i--) {
            if (constTable.get(i).containsKey(name)) {
                return constTable.get(i).get(name);
            }
        }
        return 0;
    }

    public void addSymbolTableAndConstTable() {
        symbolTable.add(new HashMap<>());
        constTable.add(new HashMap<>());
    }

    public void removeSymbolAndConstTable() {
        symbolTable.remove(symbolTable.size() - 1);
        constTable.remove(constTable.size() - 1);
    }


    private int loop = 0;
    private boolean isGlobal = true;
    private boolean isConst = false;
    private boolean isArray = false;
    private boolean isReturn = false;
    private boolean isRegister = false;

    private Value temValue = null;
    private Integer saveValue = null;
    private Operator saveOp = null;
    private Operator temOp = null;
    private IrType temType = null;

    private Value curArray = null;
    private String temName = null;
    private int temDepth = 0;
    private int temOffset = 0;
    private List<Integer> temDims;

    private List<IrType> temTypeList = null;
    private List<Value> funcArgsList = null;
    private int temIndex = 0;
    private List<Value> temList;

    // 转义字符hash表
    private HashMap<String, Integer> escapeCharacters = new HashMap<>() {{
        put("'\\a'", 7);
        put("'\\b'", 8);
        put("'\\t'", 9);
        put("'\\n'", 10);
        put("'\\v'", 11);
        put("'\\\\'", 92);
        put("\\f", 12);
        put("'\\\"'", 34);
        put("'\\''", 39);
        put("'\\0'", 0);
    }};



    public void visitCompUnitNode(CompUnitNode root) {
        addSymbolTableAndConstTable();
        addSymbol("getint", buildFactory.buildLibrary("getint", IntegerType.i32, new ArrayList<>()));
        addSymbol("getchar", buildFactory.buildLibrary("getchar", IntegerType.i32, new ArrayList<>()));
        addSymbol("putint", buildFactory.buildLibrary("putint", VoidType.voidType, new ArrayList<>(Collections.singleton(IntegerType.i32))));
        addSymbol("putch", buildFactory.buildLibrary("putch", VoidType.voidType, new ArrayList<>(Collections.singleton(IntegerType.i8))));
        addSymbol("putstr", buildFactory.buildLibrary("putstr", VoidType.voidType, new ArrayList<>(Collections.singleton(new PointerType(IntegerType.i8)))));

        isGlobal = true;
        if (root.getDecls() != null) {
            for (DeclNode decl : root.getDecls()) {
                visitDeclNode(decl);
            }
        }

        if (root.getFunc() != null) {
            for (FuncDefNode funcDefNode : root.getFunc()) {
                visitFuncDef(funcDefNode);
            }
        }


        visitMainFuncDefNode(root.getMainFun());

    }

    private void visitMainFuncDefNode(MainFuncDefNode mainFun) {
        isGlobal = false;
        Function function = buildFactory.buildFunction("main", IntegerType.i32, new ArrayList<>());
        curFunction = function;

        addSymbol("main", function);
        addSymbolTableAndConstTable();
        addSymbol("main", function);

        curBlock = buildFactory.buildBasicBlock(curFunction);
        funcArgsList = buildFactory.getArguments(curFunction);

        visitBlockNode(mainFun.getBlock());

        isGlobal = false;
        removeSymbolAndConstTable();
        buildFactory.checkBlockEnd(curBlock);

    }

    private void visitBlockNode(BlockNode block) {
        for (BlockItemNode blockItemNode : block.getBlockItemNodes()) {
            visitBlockItemNode(blockItemNode);
        }
    }

    private void visitBlockItemNode(BlockItemNode blockItemNode) {
        if (blockItemNode.getDecl() != null) {
            visitDeclNode(blockItemNode.getDecl());
        } else {
            visitStmtNode(blockItemNode.getStmt());
        }
    }

    private void visitStmtNode(StmtNode stmt) {
        switch (stmt.getType()) {
            case IF -> visitStmtForIf(stmt);
            case Block -> visitStmtForBlock(stmt);
            case Exp -> visitStmtForExp(stmt);
            case FOR -> visitStmtForFor(stmt);
            case LVal -> visitStmtForLaval(stmt);
            case Break -> visitStmtForBreak(stmt);
            case Continue -> visitStmtForContinue(stmt);
            case GetInt -> visitStmtForGetInt(stmt);
            case Printf -> visitStmtForPrint(stmt);
            case Return -> visitStmtForReturn(stmt);
            case GetChar -> visitStmtForGetChar(stmt);
        }
    }

    private void visitStmtForGetChar(StmtNode stmt) {
        if (stmt.getlValNode().getExpNode() == null) {
            // 普通变量
            Value input = getValue(stmt.getlValNode().getIdentToken().getValue());
            temValue = buildFactory.buildCall(curBlock, (Function) getValue("getchar"), new ArrayList<>());
            // 转型
            temValue = buildFactory.buildTrunc(temValue, curBlock, IntegerType.i8);
            buildFactory.buildStore(curBlock, input, temValue);
        } else {
            List<Value> indexList = new ArrayList<>();
            visitExpNode(stmt.getlValNode().getExpNode());
            indexList.add(temValue);
            temValue = getValue(stmt.getlValNode().getIdentToken().getValue());
            Value addr;
            IrType type = temValue.getType(), targetType = ((PointerType) type).getTargetType();
            if (targetType instanceof PointerType) {
                temValue = buildFactory.buildLoad(curBlock, temValue);
            } else {
                indexList.add(0, ConstInt.ZERO);
            }
            addr = buildFactory.buildGEP(curBlock, temValue, indexList);
            Value input = buildFactory.buildCall(curBlock, (Function) getValue("getchar"), new ArrayList<>());
            temValue = buildFactory.buildStore(curBlock, addr, input);
        }

    }

    private void visitStmtForReturn(StmtNode stmt) {
        if (stmt.getExpNode() == null) {
            // void函数返回
            buildFactory.buildRet(curBlock);
        } else {
            // int char 函数返回，包括main函数
            visitExpNode(stmt.getExpNode());
            // 此时temValue是返回值，但是没考虑类型
            if (curFunction != null) {
                if (!temValue.getType().equals(((FunctionType) curFunction.getType()).getReturnType())) {
                    if (temValue.getType().equals(IntegerType.i32)) {
                        temValue = buildFactory.buildTrunc(temValue, curBlock, IntegerType.i8);
                    } else {
                        temValue = buildFactory.buildZext(temValue, curBlock, IntegerType.i32);
                    }
                }
            }
            buildFactory.buildRet(curBlock, temValue);
        }
    }

    private void visitExpNode(ExpNode expNode) {
        temValue = null;
        saveValue = null;
        visitAddExpNode(expNode.getAddExpNode());
    }

    private void visitAddExpNode(AddExpNode addExpNode) {
        if (isConst) {
            Integer value = saveValue;
            Operator op = saveOp;
            saveValue = null;
            visitMulExpNode(addExpNode.getMulExp());
            if (value != null) {
                saveValue = calculate(op, value, saveValue);
            }
            if (addExpNode.getAddExp() != null) {
                saveOp = addExpNode.getOperator().getType() == TokenType.PLUS ? Operator.Add : Operator.Sub;
                visitAddExpNode(addExpNode.getAddExp());
            }
        } else {
            Value value = temValue;
            Operator op = temOp;
            temValue = null;
            visitMulExpNode(addExpNode.getMulExp());
            if (value != null) {
                temValue = buildFactory.buildBinary(curBlock, op, value, temValue);
            }
            if (addExpNode.getAddExp() != null) {
                temOp = addExpNode.getOperator().getType() == TokenType.PLUS ? Operator.Add : Operator.Sub;
                visitAddExpNode(addExpNode.getAddExp());
            }
        }
    }



    private void visitMulExpNode(MulExpNode mulExp) {
        if (isConst) {
            Integer value = saveValue;
            Operator op = saveOp;
            saveValue = null;
            visitUnaryExpNode(mulExp.getUnaryExp());
            if (value != null) {
                saveValue = calculate(op, value, saveValue);
            }
            if (mulExp.getMulExp() != null) {
                switch (mulExp.getOperator().getType()) {
                    case MULT:
                        saveOp = Operator.Mul;
                        break;
                    case DIV:
                        saveOp = Operator.Div;
                        break;
                    case MOD:
                        saveOp = Operator.Mod;
                        break;
                    default:
                        throw new RuntimeException("unknown operator type");
                }
                visitMulExpNode(mulExp.getMulExp());
            }
        } else {
            Value value = temValue;
            Operator op = temOp;
            temValue = null;
            visitUnaryExpNode(mulExp.getUnaryExp());
            if (value != null) {
                temValue = buildFactory.buildBinary(curBlock, op, value, temValue);
            }
            if (mulExp.getMulExp() != null) {
                if (mulExp.getOperator().getType().equals(TokenType.MULT)) {
                    temOp = Operator.Mul;
                } else if (mulExp.getOperator().getType().equals(TokenType.DIV)) {
                    temOp = Operator.Div;
                } else {
                    temOp = Operator.Mod;
                }
                visitMulExpNode(mulExp.getMulExp());
            }
        }
    }

    // TODO
    private void visitUnaryExpNode(UnaryExpNode unaryExp) {
        if (unaryExp.getPrimary() != null) {
            visitPrimaryExpNode(unaryExp.getPrimary());
        } else if (unaryExp.getIdentToken() != null) {
            // Ident '(' [FuncRParams] ')' 函数
            temList =new ArrayList<>();
            if (unaryExp.getFuncRParams() != null) {
                visitFuncRParamsNode(unaryExp.getFuncRParams());
            }
            temValue = buildFactory.buildCall(curBlock, (Function) getValue(unaryExp.getIdentToken().getValue()), temList);
        } else {
            // UnaryOp UnaryExp
            if (unaryExp.getUnaryOp().getOperator().getType().equals(TokenType.PLUS)) {
                visitUnaryExpNode(unaryExp.getUnaryExp());
            } else if (unaryExp.getUnaryOp().getOperator().getType().equals(TokenType.MINU)) {
                visitUnaryExpNode(unaryExp.getUnaryExp());
                if (isConst) {
                    saveValue = -saveValue;
                } else {
                    temValue = buildFactory.buildBinary(curBlock, Operator.Sub, ConstInt.ZERO, temValue);
                }
            } else {
                visitUnaryExpNode(unaryExp.getUnaryExp());
                temValue = buildFactory.buildNot(curBlock, temValue);
            }
        }
    }

    private void visitFuncRParamsNode(FuncRParamsNode funcRParams) {
        // FuncRParams → Exp { ',' Exp }
        List<Value> args = new ArrayList<>();
        for (ExpNode expNode : funcRParams.getExpNodes()) {
            visitExpNode(expNode);
            args.add(temValue);
        }
        temList = args;
    }

    private void visitPrimaryExpNode(PrimaryExpNode primary) {
        if (primary.getExpNode() != null) {
            visitExpNode(primary.getExpNode());
        } else if (primary.getlValNode() != null) {
            visitLValNode(primary.getlValNode());
        } else if (primary.getNumberNode() != null) {
            visitNumberNode(primary.getNumberNode());
        } else {
            visitCharacterNode(primary.getCharacterNode());
        }
    }

    private void visitCharacterNode(CharacterNode characterNode) {
        String value = characterNode.getToken().getValue();
        int ascii;
        if (escapeCharacters.containsKey(value)) {
            ascii = escapeCharacters.get(value);
        } else {
            ascii = characterNode.getToken().getValue().charAt(1);
        }
        if (isConst) {
            saveValue = ascii;
        } else {
            temValue = buildFactory.getConstChar(ascii);
        }
    }

    private void visitNumberNode(NumberNode numberNode) {
        if (isConst) {
            saveValue = Integer.parseInt(numberNode.getToken().getValue());
        } else {
            temValue = buildFactory.getConstInt(Integer.parseInt(numberNode.getToken().getValue()));
        }
    }

    private void visitLValNode(LValNode lValNode) {
        //  LVal → Ident ['[' Exp ']']
        if (isConst) {
            StringBuilder sb = new StringBuilder(lValNode.getIdentToken().getValue());
            if (lValNode.getExpNode() != null) {
                sb.append("0;");
                visitExpNode(lValNode.getExpNode());

                sb.append(saveValue == null ? 0 : saveValue).append(";");
            }
            saveValue = getConst(sb.toString());
        } else {
            if (lValNode.getExpNode() == null) {
                // 普通变量
                Value addr = getValue(lValNode.getIdentToken().getValue());
                temValue = addr;
                IrType type = addr.getType();

                if (!((((PointerType) type).getTargetType()) instanceof ArrayType)) {
                    temValue = buildFactory.buildLoad(curBlock, temValue);
                } else {
                    List<Value> indexList = new ArrayList<>();
                    indexList.add(ConstInt.ZERO);
                    indexList.add(ConstInt.ZERO);
                    temValue = buildFactory.buildGEP(curBlock, temValue, indexList);
                }

            } else {
                List<Value> indexList = new ArrayList<>();
                visitExpNode(lValNode.getExpNode());
                indexList.add(temValue);
                temValue = getValue(lValNode.getIdentToken().getValue());
                Value addr;
                IrType type = temValue.getType(), targetType = ((PointerType) type).getTargetType();
                if (targetType instanceof PointerType) {
                    temValue = buildFactory.buildLoad(curBlock, temValue);
                } else {
                    indexList.add(0, ConstInt.ZERO);
                }
                addr = buildFactory.buildGEP(curBlock, temValue, indexList);
                if (((PointerType) addr.getType()).getTargetType() instanceof  ArrayType) {
                    List<Value> indexList2 = new ArrayList<>();
                    indexList2.add(ConstInt.ZERO);
                    indexList2.add(ConstInt.ZERO);
                    temValue = buildFactory.buildGEP(curBlock, addr, indexList2);
                } else {
                    temValue = buildFactory.buildLoad(curBlock, addr);
                }
            }
        }
    }

    private void visitStmtForPrint(StmtNode stmt) {
        String format = stmt.getStringToken().getValue().replace("\\n", "\n").replace("\"", "");
        List<Value> args = new ArrayList<>();
        for (ExpNode expNode : stmt.getExpNodes()) {
            visitExpNode(expNode);
            args.add(temValue);
        }

        for (int i = 0; i < format.length(); i++) {
            if (format.charAt(i) == '%' && format.charAt(i + 1) == 'd') {
                buildFactory.buildCall(curBlock, (Function) getValue("putint"), new ArrayList<Value>() {{
                    add(args.remove(0));
                }});
                i++;
            } else if (format.charAt(i) == '%' && format.charAt(i + 1) == 'c') {
                buildFactory.buildCall(curBlock, (Function) getValue("putch"), new ArrayList<Value>() {{
                    add(args.remove(0));
                }});
                i++;
            } else {
                int j = i;
                while (j < format.length() && format.charAt(j) != '%') {
                    j++;
                }
                String str = format.substring(i, j);
                if (str.length() == 1) {
                    buildFactory.buildCall(curBlock, (Function) getValue("putch"), new ArrayList<>() {{
                        add(buildFactory.getConstChar(str.charAt(0)));
                    }});
                } else {
                    Value strAddr = buildFactory.buildGEP(curBlock, getValue(getStringName(str)), new ArrayList<>() {{
                        add(ConstInt.ZERO);
                        add(ConstInt.ZERO);
                    }});
                    buildFactory.buildCall(curBlock, (Function) getValue("putstr"), new ArrayList<>() {{
                        add(strAddr);
                    }});
                    i = j - 1;
                }
            }
        }

    }


    private void visitStmtForGetInt(StmtNode stmt) {
        // LVal '=' 'getint''('')'';'
        if (stmt.getlValNode().getExpNode() == null) {
            // 普通变量
            Value input = getValue(stmt.getlValNode().getIdentToken().getValue());
            temValue = buildFactory.buildCall(curBlock, (Function) getValue("getint"), new ArrayList<>());
            buildFactory.buildStore(curBlock, input, temValue);
        } else {
            List<Value> indexList = new ArrayList<>();
            visitExpNode(stmt.getlValNode().getExpNode());
            indexList.add(temValue);
            temValue = getValue(stmt.getlValNode().getIdentToken().getValue());
            Value addr;
            IrType type = temValue.getType(), targetType = ((PointerType) type).getTargetType();
            if (targetType instanceof PointerType) {
                temValue = buildFactory.buildLoad(curBlock, temValue);
            } else {
                indexList.add(0, ConstInt.ZERO);
            }
            addr = buildFactory.buildGEP(curBlock, temValue, indexList);
            Value input = buildFactory.buildCall(curBlock, (Function) getValue("getint"), new ArrayList<>());
            temValue = buildFactory.buildStore(curBlock, addr, input);
        }
    }



    private void visitStmtForLaval(StmtNode stmt) {
        // LVal '=' Exp ';'
        if (stmt.getlValNode().getExpNode() == null) {
            // 普通变量
            Value input = getValue(stmt.getlValNode().getIdentToken().getValue());
            visitExpNode(stmt.getExpNode());
            if (!((PointerType) input.getType()).getTargetType().equals(temValue.getType())) {
                if (temValue.getType().equals(IntegerType.i32)) {
                    temValue = buildFactory.buildTrunc(temValue, curBlock, IntegerType.i8);
                } else {
                    temValue = buildFactory.buildZext(temValue, curBlock, IntegerType.i32);
                }
            }
            temValue = buildFactory.buildStore(curBlock, input, temValue);
        } else {
            List<Value> indexList = new ArrayList<>();
            visitExpNode(stmt.getlValNode().getExpNode());
            indexList.add(temValue);
            temValue = getValue(stmt.getlValNode().getIdentToken().getValue());
            Value addr;
            IrType type = temValue.getType(), targetType = ((PointerType) type).getTargetType();
            if (targetType instanceof PointerType) {
                // arr[][3]
                temValue = buildFactory.buildLoad(curBlock, temValue);
            } else {
                // arr[3][2]
                indexList.add(0, ConstInt.ZERO);
            }
            addr = buildFactory.buildGEP(curBlock, temValue, indexList);
            visitExpNode(stmt.getExpNode());
            if (!((GEP) addr).getElementType().equals(temValue.getType())) {
                if (temValue.getType().equals(IntegerType.i32)) {
                    temValue = buildFactory.buildTrunc(temValue, curBlock, IntegerType.i8);
                } else {
                    temValue = buildFactory.buildZext(temValue, curBlock, IntegerType.i32);
                }
            }
            temValue = buildFactory.buildStore(curBlock, addr, temValue);
        }
    }

    private void visitStmtForContinue(StmtNode stmt) {
        buildFactory.buildBr(curBlock, continueBlock);
    }

    private void visitStmtForBreak(StmtNode stmt) {
        buildFactory.buildBr(curBlock, curFinalBlock);
    }

    private void visitStmtForFor(StmtNode stmt) {
        // 'for' '(' [ForStmt] ';' [Cond] ';' [ForStmt] ')' Stmt
        BasicBlock basicBlock = curBlock;
        BasicBlock temContinueBlock = continueBlock;
        BasicBlock temFinalBlock = curFinalBlock;

        // 处理 ForStmt 初始化部分
        if (stmt.getForStmtNode1() != null) {
            visitForStmtNode(stmt.getForStmtNode1());
        }
//        BasicBlock initBlock = buildFactory.buildBasicBlock(curFunction);
//        buildFactory.buildBr(basicBlock, initBlock);

        BasicBlock forCondBlock = buildFactory.buildBasicBlock(curFunction); // 循环条件
        BasicBlock forBodyBlock = buildFactory.buildBasicBlock(curFunction); // 循环体
        BasicBlock forStepBlock = buildFactory.buildBasicBlock(curFunction); // 自增forStmt
        BasicBlock forFinalBlock = buildFactory.buildBasicBlock(curFunction); // 结束
        curFinalBlock = forFinalBlock;

        // 处理cond
        buildFactory.buildBr(curBlock, forCondBlock);
        curBlock = forCondBlock;
        curTrueBlock = forBodyBlock;
        curFalseBlock = forFinalBlock;
        curBlock = forCondBlock;
        if (stmt.getCondNode() != null) {
            visitCondNode(stmt.getCondNode());
        } else {
            buildFactory.buildBr(curBlock, forBodyBlock);
        }

        // 处理循环体
        curBlock = forBodyBlock;
        continueBlock = forStepBlock;
        visitStmtNode(stmt.getStmtNode());
        buildFactory.buildBr(curBlock, forStepBlock); // 进入自增部分

        // 处理 ForStmt 自增部分

        curBlock = forStepBlock;
        if (stmt.getForStmtNode2() != null) {
            visitForStmtNode(stmt.getForStmtNode2());
        }

        // 跳回判断
        buildFactory.buildBr(curBlock, forCondBlock);

        // 恢复状态
        continueBlock = temContinueBlock;
        curFinalBlock = temFinalBlock;
        curTrueBlock = forBodyBlock;
        curFalseBlock = forFinalBlock;
        curBlock = forFinalBlock;
    }


    private void visitForStmtNode(ForStmtNode forStmt) {
        // LVal '=' Exp
        // LVal '=' Exp ';'
        if (forStmt.getlValNode().getExpNode() == null) {
            // 普通变量
            Value input = getValue(forStmt.getlValNode().getIdentToken().getValue());
            visitExpNode(forStmt.getExpNode());
            if (!((PointerType) input.getType()).getTargetType().equals(temValue.getType())) {
                if (temValue.getType().equals(IntegerType.i32)) {
                    temValue = buildFactory.buildTrunc(temValue, curBlock, IntegerType.i8);
                } else {
                    temValue = buildFactory.buildZext(temValue, curBlock, IntegerType.i32);
                }
            }
            temValue = buildFactory.buildStore(curBlock, input, temValue);
        } else {
            List<Value> indexList = new ArrayList<>();
            visitExpNode(forStmt.getlValNode().getExpNode());
            indexList.add(temValue);
            temValue = getValue(forStmt.getlValNode().getIdentToken().getValue());
            Value addr;
            IrType type = temValue.getType(), targetType = ((PointerType) type).getTargetType();
            if (targetType instanceof PointerType) {
                // arr[][3]
                temValue = buildFactory.buildLoad(curBlock, temValue);
            } else {
                // arr[3][2]
                indexList.add(0, ConstInt.ZERO);
            }
            addr = buildFactory.buildGEP(curBlock, temValue, indexList);
            visitExpNode(forStmt.getExpNode());
            if (!((GEP) addr).getElementType().equals(temValue.getType())) {
                if (temValue.getType().equals(IntegerType.i32)) {
                    temValue = buildFactory.buildTrunc(temValue, curBlock, IntegerType.i8);
                } else {
                    temValue = buildFactory.buildZext(temValue, curBlock, IntegerType.i32);
                }
            }
            temValue = buildFactory.buildStore(curBlock, addr, temValue);
        }
    }

    private void visitStmtForExp(StmtNode stmt) {
        if (stmt.getExpNode() != null) {
            visitExpNode(stmt.getExpNode());
        }
    }

    private void visitStmtForBlock(StmtNode stmt) {
        addSymbolTableAndConstTable();
        visitBlockNode(stmt.getBlockNode());
        removeSymbolAndConstTable();
    }

    private void visitStmtForIf(StmtNode stmt) {
        if (stmt.getElseToken() == null) {
            BasicBlock basicBlock = curBlock;

            BasicBlock trueBlock = buildFactory.buildBasicBlock(curFunction);
            curBlock = trueBlock;
            visitStmtNode(stmt.getStmtNodes().get(0));
            BasicBlock outBlock = buildFactory.buildBasicBlock(curFunction);
            buildFactory.buildBr(curBlock, outBlock);

            curTrueBlock = trueBlock;
            curFalseBlock = outBlock;
            curBlock = basicBlock;
            visitCondNode(stmt.getCondNode());
            curBlock = outBlock;
        } else {
            BasicBlock basicBlock = curBlock;

            BasicBlock trueBlock = buildFactory.buildBasicBlock(curFunction);
            curBlock = trueBlock;
            visitStmtNode(stmt.getStmtNodes().get(0));
            BasicBlock trueEndBlock = curBlock;

            BasicBlock falseBlock = buildFactory.buildBasicBlock(curFunction);
            curBlock = falseBlock;
            visitStmtNode(stmt.getStmtNodes().get(1));
            BasicBlock falseEndBlock = curBlock;

            curBlock = basicBlock;
            curTrueBlock = trueBlock;
            curFalseBlock = falseBlock;
            visitCondNode(stmt.getCondNode());

            BasicBlock finalBlock = buildFactory.buildBasicBlock(curFunction);
            buildFactory.buildBr(trueEndBlock, finalBlock);
            buildFactory.buildBr(falseEndBlock, finalBlock);
            curBlock = finalBlock;
        }
    }

    private void visitCondNode(CondNode condNode) {
        //  Cond → LOrExp
        visitLOrExpNode(condNode.getlOrExp());
    }

    private void visitLOrExpNode(LOrExpNode lOrExpNode) {
        // LOrExp → LAndExp | LOrExp '||' LAndExp
        BasicBlock trueBlock = curTrueBlock;
        BasicBlock falseBlock = curFalseBlock;
        BasicBlock temFalseBlock = curFalseBlock;
        BasicBlock thenBlock = null;

        if (lOrExpNode.getlOrExpNode() != null) {
            thenBlock = buildFactory.buildBasicBlock(curFunction);
            temFalseBlock = thenBlock;
        }

        curFalseBlock = temFalseBlock;
        visitLAndExpNode(lOrExpNode.getlAndExpNode());
        curTrueBlock = trueBlock;
        curFalseBlock = falseBlock;
        if (lOrExpNode.getlOrExpNode() != null) {
            curBlock = thenBlock;
            visitLOrExpNode(lOrExpNode.getlOrExpNode());
        }
    }

    private void visitLAndExpNode(LAndExpNode lAndExpNode) {
        //  LAndExp → EqExp | LAndExp '&&' EqExp
        BasicBlock trueBlock = curTrueBlock;
        BasicBlock falseBlock = curFalseBlock;
        BasicBlock temTrueBlock = curTrueBlock;
        BasicBlock thenBlock = null;
        if (lAndExpNode.getlAndExp() != null) {
            thenBlock = buildFactory.buildBasicBlock(curFunction);
            temTrueBlock = thenBlock;
        }
        curTrueBlock = temTrueBlock;
        temValue = null;
        visitEqExpNode(lAndExpNode.getEqExp());
        buildFactory.buildBr(curBlock, temValue, curTrueBlock, curFalseBlock);
        curTrueBlock = trueBlock;
        curFalseBlock = falseBlock;
        if (lAndExpNode.getlAndExp() != null) {
            curBlock = thenBlock;
            visitLAndExpNode(lAndExpNode.getlAndExp());
        }
    }

    private void visitEqExpNode(EqExpNode eqExp) {
        // EqExp → RelExp | EqExp ('==' | '!=') RelExp
        Value value = temValue;
        Operator op = temOp;
        temValue = null;
        visitRelExpNode(eqExp.getRelExpNode());
        if (value != null) {
            temValue = buildFactory.buildBinary(curBlock, op, value, temValue);
        }
        if (eqExp.getEqExpNode() != null) {
            temOp = eqExp.getOperator().getType().equals(TokenType.EQL) ? Operator.Eq : Operator.Ne;
            visitEqExpNode(eqExp.getEqExpNode());
        }
    }

    private void visitRelExpNode(RelExpNode relExpNode) {
        // RelExp → AddExp | RelExp ('<' | '>' | '<=' | '>=') AddExp
        Value value = temValue;
        Operator op = temOp;
        temValue = null;
        visitAddExpNode(relExpNode.getAddExp());
        if (value != null) {
            temValue = buildFactory.buildBinary(curBlock, op, value, temValue);
        }
        if (relExpNode.getRelExp() != null) {
            switch (relExpNode.getOperator().getType()) {
                case LSS:
                    temOp = Operator.Lt;
                    break;
                case LEQ:
                    temOp = Operator.Le;
                    break;
                case GRE:
                    temOp = Operator.Gt;
                    break;
                case GEQ:
                    temOp = Operator.Ge;
                    break;
            }
            visitRelExpNode(relExpNode.getRelExp());
        }
    }

    private void visitFuncDef(FuncDefNode funcDefNode) {
        // FuncDef → FuncType Ident '(' [FuncFParams] ')' Block
        isGlobal = false;
        String funcName = funcDefNode.getIdentToken().getValue();
        IrType retType = null;
        if (funcDefNode.getFuncType().getFuncType().getType().equals(TokenType.INTTK)) {
            retType = IntegerType.i32;
        } else if (funcDefNode.getFuncType().getFuncType().getType().equals(TokenType.CHARTK)) {
            retType = IntegerType.i8;
        } else {
            retType = VoidType.voidType;
        }
        temTypeList = new ArrayList<>();
        if (funcDefNode.getFuncFParams() != null) {
            visitFuncFParamsNode(funcDefNode.getFuncFParams());
        }
        Function function = buildFactory.buildFunction(funcName, retType, temTypeList);
        curFunction = function;
        addSymbol(funcName, function);
        addSymbolTableAndConstTable();
        addSymbol(funcName, function);
        curBlock = buildFactory.buildBasicBlock(curFunction);
        funcArgsList = buildFactory.getFunctionArguments(curFunction);
        isRegister = true;
        if (funcDefNode.getFuncFParams() != null) {
            visitFuncFParamsNode(funcDefNode.getFuncFParams());
        }
        isRegister = false;
        visitBlockNode(funcDefNode.getBlock());
        isGlobal = true;
        removeSymbolAndConstTable();
        buildFactory.checkBlockEnd(curBlock);
    }

    private void visitFuncFParamsNode(FuncFParamsNode funcFParams) {
        // FuncFParams → FuncFParam { ',' FuncFParam }
        if (isRegister) {
            temIndex = 0;
            for (FuncFParamNode param : funcFParams.getParams()) {
                visitFuncFParamNode(param);
                temIndex++;
            }
        } else {
            temTypeList = new ArrayList<>();
            for (FuncFParamNode param : funcFParams.getParams()) {
                visitFuncFParamNode(param);
                temTypeList.add(temType);
            }
        }
    }

    private void visitFuncFParamNode(FuncFParamNode param) {
        // FuncFParam → BType Ident ['[' ']']
        if (isRegister) {
            int i = temIndex;
            Value value = buildFactory.buildVar(curBlock, funcArgsList.get(i), false, temTypeList.get(i));
            addSymbol(param.getIdentToken().getValue(), value);
        } else {
            if (param.getLeftBracket() == null) {
                temType = param.getbTypeNode().getToken().getType().equals(TokenType.INTTK) ? IntegerType.i32 : IntegerType.i8;
            } else {
                List<Integer> dims = new ArrayList<>();
                dims.add(-1);
                temType = null;
                for (int i = dims.size() - 1; i >= 0; i--) {
                    if (temType == null) {
                        temType = param.getbTypeNode().getToken().getType().equals(TokenType.INTTK) ? IntegerType.i32 : IntegerType.i8;
                    }
                    temType = buildFactory.getArrayType(temType, dims.get(i));
                }


            }
        }
    }


    private void visitDeclNode(DeclNode decl) {
        if (decl.getConstDecl() != null) {
            visitConstDeclNode(decl.getConstDecl());
        } else {
            visitVarDeclNode(decl.getVarDecl());
        }
    }

    private void visitVarDeclNode(VarDeclNode varDecl) {
        temType = varDecl.getbType().getToken().getType().equals(TokenType.INTTK) ? IntegerType.i32 : IntegerType.i8;
        for (VarDefNode varDef : varDecl.getVarDefs()) {
            visitVarDefNode(varDef);
        }
    }

    private void visitVarDefNode(VarDefNode varDef) {
        String name = varDef.getIdentNode().getValue();
        if (varDef.getConstExpNode() == null) {
            // 普通变量
            if (varDef.getInitValNode() != null) {
                temValue = null;
                if (isGlobal) {
                    isConst = true;
                    saveValue = null;
                }
                visitInitValNode(varDef.getInitValNode());
                isConst = false;
            } else {
                temValue = null;
                if (isGlobal) {
                    saveValue = null;
                }
            }
            if (isGlobal) {
                temValue = buildFactory.buildGlobalVar(name, temType, false,
                        temType.equals(IntegerType.i32) ? buildFactory.getConstInt(saveValue == null ? 0 :saveValue) : buildFactory.getConstChar(saveValue == null ? 0 : saveValue));
                addSymbol(name, temValue);
            } else {
                if ((temValue != null) && (!temValue.getType().equals(temType))) {
                    if (temType.equals(IntegerType.i32)) {
                        temValue = buildFactory.buildZext(temValue, curBlock, IntegerType.i32);
                    } else {
                        temValue = buildFactory.buildTrunc(temValue, curBlock, IntegerType.i8);
                    }
                }
                temValue = buildFactory.buildVar(curBlock, temValue, isConst, temType);
                addSymbol(name, temValue);
            }
        } else {
            // 数组
            isConst = true;
            List<Integer> dims = new ArrayList<>();
            visitConstExp(varDef.getConstExpNode());
            dims.add(saveValue);
            isConst = false;
            temDims = new ArrayList<>(dims);
            IrType type = null;
            type = buildFactory.getArrayType(temType, dims.get(0));
            if (isGlobal) {
                temValue = buildFactory.buildGlobalArray(name, type, false);
                if (varDef.getInitValNode() != null) {
                    ((ConstArray) ((GlobalVar) temValue).getValue()).setInit(true);
                }
            } else {
                temValue = buildFactory.buildArray(curBlock, false, type);
            }
            addSymbol(name, temValue);
            curArray = temValue;
            if (varDef.getInitValNode() != null) {
                isArray = true;
                temName = name;
                temDepth = 0;
                temOffset = 0;
                visitInitValNode(varDef.getInitValNode());
                isArray = false;
            }
            isConst = false;
        }
    }

    private void visitInitValNode(InitValNode initValNode) {
        // InitVal → Exp | '{' [ Exp { ',' Exp } ] '}' | StringConst
        if (initValNode.getStringToken() != null) {
            int depth = 0, offset = temOffset;
            String strConst = initValNode.getStringToken().getValue();
            int ascii = 0;
            for (int i = 1; i < strConst.length() - 1; i++) {
                if (isGlobal) {
                    isConst = true;
                }
                saveValue = null;
                temValue = null;
                if (strConst.charAt(i) == '\\') {
                    String tmp = "'" + strConst.substring(i, i + 2) + "'";
//                    System.out.println(tmp);
                    ascii = escapeCharacters.get(tmp);
                } else {
                    ascii = strConst.charAt(i);
                }
                saveValue = ascii;
                isConst = false;
                temDepth = 1;
                temValue = buildFactory.getConstChar(saveValue);
                if (isGlobal) {
                    // temValue = buildFactory.getConstChar(saveValue);
                    buildFactory.buildInitArray(curArray, temOffset, temValue);
                } else {
                    buildFactory.buildStore(curBlock, buildFactory.buildGEP(curBlock, curArray, temOffset), temValue);
                }
                temOffset++;
                depth = Math.max(depth, temDepth);
            }
            // 如果是非全局变量，补一个Store 0
            if (!isGlobal) {
                temValue = buildFactory.getConstChar(0);
                buildFactory.buildStore(curBlock, buildFactory.buildGEP(curBlock, curArray, temOffset), temValue);
                temOffset++;
            }
            depth++;
            int size = 1;
            for (int i = 1; i < depth; i++) {
                size *= temDims.get(temDims.size() - 1);
            }
            temOffset = Math.max(temOffset, size + offset);
            temDepth = depth;

        } else if (initValNode.getExpr() != null && !isArray) {
            visitExpNode(initValNode.getExpr());
        } else {
            if (!initValNode.getExpNodes().isEmpty()) {
                int depth = 0, offset = temOffset;
                for (ExpNode expNode : initValNode.getExpNodes()) {
                    visitExpNodeForVarArray(expNode);
                    depth = Math.max(depth, temDepth);
                }
                depth++;
                int size = 1;
                for (int i = 1; i < depth; i++) {
                    size *= temDims.get(temDims.size() - 1);
                }
                temOffset = Math.max(temOffset, offset + size);
                temDepth = depth;
            }
        }
    }

    private void visitExpNodeForVarArray(ExpNode expNode) {
        if (isGlobal) {
            isConst = true;
        }
        saveValue = null;
        temValue = null;
        visitExpNode(expNode);
        isConst = false;
        temDepth = 1;
        if (isGlobal) {
            temValue = temType == IntegerType.i32 ? buildFactory.getConstInt(saveValue) : buildFactory.getConstChar(saveValue);
            buildFactory.buildInitArray(curArray, temOffset, temValue);
        } else {
            GEP gep = buildFactory.buildGEP(curBlock, curArray, temOffset);
            if (!gep.getElementType().equals(temValue.getType())) {
                if (temValue.getType().equals(IntegerType.i32)) {
                    temValue = buildFactory.buildTrunc(temValue, curBlock, IntegerType.i8);
                } else {
                    temValue = buildFactory.buildZext(temValue, curBlock, IntegerType.i32);
                }
            }
            buildFactory.buildStore(curBlock, gep, temValue);
        }
        temOffset++;
    }

    private void visitConstDeclNode(ConstDeclNode constDecl) {
        temType = constDecl.getbTypeNode().getToken().getType().equals(TokenType.INTTK) ? IntegerType.i32 : IntegerType.i8;
        for (ConstDefNode constDefNode : constDecl.getConstDefNodes()) {
            visitConstDefNode(constDefNode);
        }
    }

    private void visitConstDefNode(ConstDefNode constDefNode) {
        String name = constDefNode.getIdentToken().getValue();
        if (constDefNode.getConstExpNodes() == null || constDefNode.getConstExpNodes().isEmpty()) {
            // 普通常量
            visitConstInitValNode(constDefNode.getConstInitValNode());
            if (temType == IntegerType.i32) {
                temValue = buildFactory.getConstInt(saveValue == null ? 0 : saveValue);
            } else {
                temValue = buildFactory.getConstChar(saveValue == null ? 0 : saveValue);
            }

            addConst(name, saveValue);
            if (isGlobal) {
                temValue = buildFactory.buildGlobalVar(name, temType, true, temValue);
                addSymbol(name, temValue);
            } else {
                temValue = buildFactory.buildVar(curBlock, temValue, true, temType);
                addSymbol(name, temValue);
            }
        } else {
            // 数组
            List<Integer> dims = new ArrayList<>();
            for (ConstExpNode constExpNode : constDefNode.getConstExpNodes()) {
                visitConstExp(constExpNode);
                dims.add(saveValue);
            }
            temDims = new ArrayList<>(dims);
            IrType type = null;
            for (int i = dims.size() - 1; i >= 0; i--) {
                if (type == null) {
                    type = buildFactory.getArrayType(temType, dims.get(i));
                } else {
                    type = buildFactory.getArrayType(type, dims.get(i));
                }
            }
            if (isGlobal) {
                temValue = buildFactory.buildGlobalArray(name, type, true);
                ((ConstArray)((GlobalVar) temValue).getValue()).setInit(true);
            } else {
                temValue = buildFactory.buildArray(curBlock,true, type);
            }
            addSymbol(name, temValue);
            curArray = temValue;
            isArray = true;
            temName = name;
            temDepth = 0;
            temOffset = 0;
            visitConstInitValNode(constDefNode.getConstInitValNode());
            isArray = false;
        }
    }

    private void visitConstInitValNode(ConstInitValNode constInitValNode) {
        // ConstInitVal → ConstExp | '{' [ ConstExp { ',' ConstExp } ] '}' | stringConst
        if (constInitValNode.getStringToken() != null) {
            // stringConst
            int depth = 0, offset = temOffset;
            String strConst = constInitValNode.getStringToken().getValue();
            int ascii = 0;
            for (int i = 1; i < strConst.length() - 1; i++) {
                temValue = null;
                // 确定字符
                if (strConst.charAt(i) == '\\') {
                    String tmp = "'" + strConst.substring(i, i + 2) + "'";
                    i = i + 1;
                    ascii = escapeCharacters.get(tmp);
                } else {
                    ascii = strConst.charAt(i);
                }
                saveValue = ascii;
                temValue = buildFactory.getConstChar(saveValue);
                temDepth = 1;
                if (isGlobal) {
                    buildFactory.buildInitArray(curArray, temOffset, temValue);
                } else {
                    buildFactory.buildStore(curBlock, buildFactory.buildGEP(curBlock, curArray, temOffset), temValue);
                }
                StringBuilder name = new StringBuilder();
                List<Value> args = ((ArrayType) ((PointerType) curArray.getType()).getTargetType()).offset2Index(temOffset);
                for (Value arg : args) {
                    name.append(((ConstInt) arg).getValue()).append(";");
                }
                addConst(name.toString(), saveValue);
                temOffset++;
                depth = Math.max(depth, temDepth);
            }
            depth++;
            int size = 1;
            for (int i = 1; i < depth; i++) {
                size *= temDims.get(temDims.size() - 1);
            }
            temOffset = Math.max(temOffset, offset + size);
            temDepth = depth;

        } else if (constInitValNode.getConstExp() != null && !isArray ) {
            // 非数组
            visitConstExp(constInitValNode.getConstExp());
        } else {

            if (!constInitValNode.getConstExpNodes().isEmpty()) {
                int depth = 0, offset = temOffset;
                for (ConstExpNode constExpNode : constInitValNode.getConstExpNodes()) {
                    visitConstExpForArray(constExpNode);
                    depth = Math.max(depth, temDepth);
                }
                depth++;
                int size = 1;
                for (int i = 1; i <depth; i++) {
                    size *= temDims.get(temDims.size() - 1);
                }
                temOffset = Math.max(temOffset, offset + size);
                temDepth = depth;
            }
        }
    }

    private void visitConstExpForArray(ConstExpNode constExpNode) {
        temValue = null;
        visitConstExp(constExpNode);
        temDepth = 1;
        temValue = temType == IntegerType.i32 ? buildFactory.getConstInt(saveValue) : buildFactory.getConstChar(saveValue);
        if (isGlobal) {
            buildFactory.buildInitArray(curArray, temOffset, temValue);
        } else {
            buildFactory.buildStore(curBlock, buildFactory.buildGEP(curBlock, curArray, temOffset), temValue);
        }
        StringBuilder name = new StringBuilder(temName);
        List<Value> args = ((ArrayType) ((PointerType) curArray.getType()).getTargetType()).offset2Index(temOffset);
        for (Value arg : args) {
            name.append(((ConstInt) arg).getValue()).append(";");
        }
        addConst(name.toString(), saveValue);
        temOffset++;
    }

    private void visitConstExp(ConstExpNode constExp) {
        isConst = true;
        saveValue = null;
        visitAddExpNode(constExp.getAddExp());
        isConst = false;
    }

    private void goToNextTable() {
        SymbolTable tempCur = curTable;
        SymbolTable newTable = new SymbolTable(tempCur, new LinkedHashMap<String, Symbol>(), 0, new ArrayList<SymbolTable>());
        curTable.getNextLayer().add(newTable);
        curTable = newTable;
    }

    private Integer calculate(Operator op, Integer a, Integer b) {
        return switch (op) {
            case Add -> a + b;
            case Sub -> a - b;
            case Mul -> a * b;
            case Div -> a / b;
            case Mod -> a % b;
            default -> 0;
        };
    }

    private List<String> stringList = new ArrayList<>();

    private int getStringIndex(String str) {
        for (int i = 0; i < stringList.size(); i++) {
            if (stringList.get(i).equals(str)) {
                return i;
            }
        }
        stringList.add(str);
        IrType type = buildFactory.getArrayType(IntegerType.i8, str.length() + 1);
        Value value = buildFactory.buildGlobalVar(getStringName(str), type, true, buildFactory.getConstString(str));
        addGlobalSymbol(getStringName(str), value);
        return stringList.size() - 1;
    }

    private String getStringName(int Index) {
        return "_str_" + Index;
    }

    private String getStringName(String str) {
        return getStringName(getStringIndex(str));
    }
}
