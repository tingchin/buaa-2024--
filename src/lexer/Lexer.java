package lexer;


import error.Error;
import error.ErrorHandler;
import error.ErrorType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Lexer {
    // 单例模式
    private static final Lexer instance = new Lexer();

    // 获取方法
    public static Lexer getInstance() {
        return instance;
    }

    // 保存词法分析生成的tokens
    private List<Token> tokensList = new ArrayList<>();

    // 获取token表
    public List<Token> getTokens() {
        return tokensList;
    }

    // 保留字map，快速定位
    private Map<String, TokenType> reserveWords = new HashMap<>() {{
        put("main", TokenType.MAINTK);
        put("const", TokenType.CONSTTK);
        put("int", TokenType.INTTK);
        put("char", TokenType.CHARTK);
        put("break", TokenType.BREAKTK);
        put("continue", TokenType.CONTINUETK);
        put("if", TokenType.IFTK);
        put("else", TokenType.ELSETK);
        put("for", TokenType.FORTK);
        put("void", TokenType.VOIDTK);
        put("getint", TokenType.GETINTTK);
        put("getchar", TokenType.GETCHARTK);
        put("printf", TokenType.PRINTFTK);
        put("return", TokenType.RETURNTK);
    }};

    // 单字符map，快速定位
    private Map<Character, TokenType> singleChars = new HashMap<>() {{
        put('+', TokenType.PLUS);
        put('-', TokenType.MINU);
        put('*', TokenType.MULT);
        put('%', TokenType.MOD);
        put(';', TokenType.SEMICN);
        put(',', TokenType.COMMA);
        put('(', TokenType.LPARENT);
        put(')', TokenType.RPARENT);
        put('{', TokenType.LBRACE);
        put('}', TokenType.RBRACE);
        put('[', TokenType.LBRACK);
        put(']', TokenType.RBRACK);
    }};
    // 词法分析
    public void lexerAnalyze(String sourceCode) {
        int line = 1;
        int codeLen = sourceCode.length();

        char curChar;
        char nextChar;
        int number;
        String tokenValue;
        for (int i = 0; i < codeLen;) {
            curChar = sourceCode.charAt(i);
            if (i + 1 < codeLen) {
                nextChar = sourceCode.charAt(i + 1);
            } else {
                nextChar = '\0';
            }

            if (curChar == '\n') {
                // 换行
                line++;
            } else if (Character.isDigit(curChar)) {
                // 数字
                StringBuilder sbToken = new StringBuilder();
                sbToken.append(curChar);
                while (i < codeLen && Character.isDigit(sourceCode.charAt(i))) {
                    sbToken.append(sourceCode.charAt(i));
                    i++;
                }
                number = Integer.parseInt(sbToken.toString()); // 留有接口防止后面需要
                tokenValue = sbToken.toString();
                tokensList.add(new Token(TokenType.INTCON, tokenValue, line));
                continue;
            } else if (curChar == '/') { // 读入第一个 /
                // 注释和除号处理
                if (nextChar == '/') { // 表示单行注释
                    while ((i < codeLen) && (sourceCode.charAt(i) != '\n')) {
                        i++;
                    }
                    continue;
                } else if (nextChar == '*') { // 表示多行注释
                    while (i < codeLen) {
                        while ((i < codeLen) && (sourceCode.charAt(i) != '*')) { // 没有遇到*表示还在多行注释中
                            if (sourceCode.charAt(i) == '\n') {
                                line++;
                            }
                            i++;
                        }
                        while ((i < codeLen) && (sourceCode.charAt(i) == '*')) { // 碰到*表示可能结束多行注释
                            i++;
                        }
                        if (sourceCode.charAt(i) == '/') { // 多行注释结束
                            i++;
                            break;
                        }
                    }
                    continue;
                } else { // 表示除号
                    tokensList.add(new Token(TokenType.DIV, "/", line));
                }

            } else if (curChar == '_' || Character.isLetter(curChar)) { // 保留字或者标识符
                StringBuilder sbToken = new StringBuilder();
                sbToken.append(curChar);
                while ((i < codeLen) && (Character.isLetter(sourceCode.charAt(i)) || Character.isDigit(sourceCode.charAt(i)) || sourceCode.charAt(i) == '_') ) {
                    sbToken.append(sourceCode.charAt(i));
                    i++;
                }
                tokenValue = sbToken.toString();
                tokensList.add(new Token(reserveWords.getOrDefault(tokenValue, TokenType.IDENFR), tokenValue, line));
                continue;
            } else if (singleChars.containsKey(curChar)) { // 单字符分界 + - * / ; , () [] {}
                tokensList.add(new Token(singleChars.get(curChar), String.valueOf(curChar), line));
            } else if (curChar == '\'') { // 字符常量
                StringBuilder sbToken = new StringBuilder();
                while ((i < codeLen) && (sourceCode.charAt(i) != '\'')) {
                    sbToken.append(sourceCode.charAt(i));
                    i++;
                }
                sbToken.append(sourceCode.charAt(i));
                tokenValue = sbToken.toString();
                tokensList.add(new Token(TokenType.CHRCON, tokenValue, line));
                i++;
                continue;
            } else if (curChar == '\"') { // 字符串常量
                StringBuilder sbToken = new StringBuilder();
                while ((i < codeLen) && (sourceCode.charAt(i) != '\"')) {
                    sbToken.append(sourceCode.charAt(i));
                    i++;
                }
                sbToken.append(sourceCode.charAt(i));
                tokenValue = sbToken.toString();
                tokensList.add(new Token(TokenType.STRCON, tokenValue, line));
                i++;
                continue;
            } else if (curChar == '!' ) { // 某些可单可双分界符
                if (nextChar == '=') {
                    tokenValue = "!=";
                    tokensList.add(new Token(TokenType.NEQ, tokenValue, line));
                    i++;
                } else {
                    tokenValue = "!";
                    tokensList.add(new Token(TokenType.NOT, tokenValue, line));
                }
            } else if (curChar == '<' ) { // 某些可单可双分界符
                if (nextChar == '=') {
                    tokenValue = "<=";
                    tokensList.add(new Token(TokenType.LEQ, tokenValue, line));
                    i++;
                } else {
                    tokenValue = "<";
                    tokensList.add(new Token(TokenType.LSS, tokenValue, line));
                }
            } else if (curChar == '>' ) {
                if (nextChar == '=') {
                    tokenValue = ">=";
                    tokensList.add(new Token(TokenType.GEQ, tokenValue, line));
                    i++;
                } else {
                    tokenValue = ">";
                    tokensList.add(new Token(TokenType.GRE, tokenValue, line));
                }
            } else if (curChar == '=') {
                if (nextChar == '=') {
                    tokenValue = "==";
                    tokensList.add(new Token(TokenType.EQL, tokenValue, line));
                    i++;
                } else {
                    tokenValue = "=";
                    tokensList.add(new Token(TokenType.ASSIGN, tokenValue, line));
                }
            } else if (curChar == '|') {
                if (nextChar == '|') {
                    tokenValue = "||";
                    tokensList.add(new Token(TokenType.OR, tokenValue, line));
                    i++;
                } else {
                    // error
                    ErrorHandler.getInstance().getErrors().add(new Error(ErrorType.a, line));
                    //System.out.println("error " + line);
                }
                continue;
            } else if (curChar == '&') {
                if (nextChar == '&') {
                    tokenValue = "&&";
                    tokensList.add(new Token(TokenType.AND, tokenValue, line));
                    i++;
                } else {
                    // error
                    ErrorHandler.getInstance().getErrors().add(new Error(ErrorType.a, line));
                    //System.out.println("error" + line);
                }
            }
            i++;
        }

    }
}
