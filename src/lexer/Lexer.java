package lexer;


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
    private Map<String, Token.TokenType> reserveWords = new HashMap<>() {{
        put("main", Token.TokenType.MAINTK);
        put("const", Token.TokenType.CONSTTK);
        put("int", Token.TokenType.INTTK);
        put("char", Token.TokenType.CHARTK);
        put("break", Token.TokenType.BREAKTK);
        put("continue", Token.TokenType.CONTINUETK);
        put("if", Token.TokenType.IFTK);
        put("else", Token.TokenType.ELSETK);
        put("for", Token.TokenType.FORTK);
        put("void", Token.TokenType.VOIDTK);
    }};

    // 单字符map，快速定位
    private Map<Character, Token.TokenType> singleChars = new HashMap<>() {{
        put('+', Token.TokenType.PLUS);
        put('-', Token.TokenType.MINU);
        put('*', Token.TokenType.MULT);
        put('%', Token.TokenType.MOD);
        put(';', Token.TokenType.SEMICN);
        put(',', Token.TokenType.COMMA);
        put('(', Token.TokenType.LPARENT);
        put(')', Token.TokenType.RPARENT);
        put('{', Token.TokenType.LBRACE);
        put('}', Token.TokenType.RBRACE);
        put('[', Token.TokenType.LBRACK);
        put(']', Token.TokenType.RBRACK);
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
                tokensList.add(new Token(Token.TokenType.INTCON, tokenValue, line));
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
                    tokensList.add(new Token(Token.TokenType.DIV, "/", line));
                }

            } else if (curChar == '_' || Character.isLetter(curChar)) { // 保留字或者标识符
                StringBuilder sbToken = new StringBuilder();
                sbToken.append(curChar);
                while ((i < codeLen) && (Character.isLetter(sourceCode.charAt(i)) || Character.isDigit(sourceCode.charAt(i)) || sourceCode.charAt(i) == '_') ) {
                    sbToken.append(sourceCode.charAt(i));
                    i++;
                }
                tokenValue = sbToken.toString();
                tokensList.add(new Token(reserveWords.getOrDefault(tokenValue, Token.TokenType.IDENFR), tokenValue, line));
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
                tokensList.add(new Token(Token.TokenType.CHRCON, tokenValue, line));
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
                tokensList.add(new Token(Token.TokenType.STRCON, tokenValue, line));
                i++;
                continue;
            } else if (curChar == '!' ) { // 某些可单可双分界符
                if (nextChar == '=') {
                    tokenValue = "!=";
                    tokensList.add(new Token(Token.TokenType.NEQ, tokenValue, line));
                    i++;
                } else {
                    tokenValue = "!";
                    tokensList.add(new Token(Token.TokenType.NOT, tokenValue, line));
                }
            } else if (curChar == '<' ) { // 某些可单可双分界符
                if (nextChar == '=') {
                    tokenValue = "<=";
                    tokensList.add(new Token(Token.TokenType.LEQ, tokenValue, line));
                    i++;
                } else {
                    tokenValue = "<";
                    tokensList.add(new Token(Token.TokenType.LSS, tokenValue, line));
                }
            } else if (curChar == '>' ) {
                if (nextChar == '=') {
                    tokenValue = ">=";
                    tokensList.add(new Token(Token.TokenType.GEQ, tokenValue, line));
                    i++;
                } else {
                    tokenValue = ">";
                    tokensList.add(new Token(Token.TokenType.GRE, tokenValue, line));
                }
            } else if (curChar == '=') {
                if (nextChar == '=') {
                    tokenValue = "==";
                    tokensList.add(new Token(Token.TokenType.EQL, tokenValue, line));
                    i++;
                } else {
                    tokenValue = "=";
                    tokensList.add(new Token(Token.TokenType.ASSIGN, tokenValue, line));
                }
            } else if (curChar == '|') {
                if (nextChar == '|') {
                    tokenValue = "||";
                    tokensList.add(new Token(Token.TokenType.OR, tokenValue, line));
                    i++;
                } else {
                    // error
                    System.out.println("error " + line);
                }
                continue;
            } else if (curChar == '&') {
                if (nextChar == '&') {
                    tokenValue = "&&";
                    tokensList.add(new Token(Token.TokenType.AND, tokenValue, line));
                    i++;
                } else {
                    // error
                    System.out.println("error" + line);
                }
            }
            i++;
        }

    }
}
