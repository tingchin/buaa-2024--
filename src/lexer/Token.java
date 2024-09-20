package lexer;

// Token类，用于记录词法分析的token
public class Token {
    // token类别
    public enum TokenType {
        IDENFR, INTCON, STRCON, CHRCON, // 变量命名
        // 关键字和符号
        MAINTK, CONSTTK, INTTK, CHARTK, BREAKTK, CONTINUETK, IFTK,
        ELSETK, NOT, AND, OR, FORTK, GETINTTK, GETCHARTK, PRINTFTK,
        RETURNTK, PLUS, MINU, VOIDTK, MULT, DIV, MOD, LSS, LEQ,
        GRE, GEQ, EQL, NEQ, ASSIGN, SEMICN, COMMA, LPARENT, RPARENT, LBRACK,
        RBRACK, LBRACE, RBRACE
    }

    private TokenType type; // 单词类别码
    private String value; // 单词值
    private int line; // 所在行号

    public Token(TokenType type, String value, int line) {
        this.type = type;
        this.value = value;
        this.line = line;
    }

    public Token() {
    }

    public TokenType getType() {
        return type;
    }

    public void setType(TokenType type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

}
