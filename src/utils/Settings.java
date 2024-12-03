package utils;

public class Settings {
    // 文件路径
    public static final String inputPath = "testfile.txt";
    public static final String lexerOutputPath = "lexer.txt";
    public static final String errorPath = "error.txt";
    public static final String syntaxOutputPath = "parser.txt";
    public static final String symbolOutputPath = "symbol.txt";
    public static final String llvmOutputPath = "llvm_ir.txt";

    // 是否输出
    public static final boolean lexer = false;
    public static final boolean syntax = false;
    public static final boolean symbol = true;
    public static final boolean llvm = true;

    public static void init() {
        IOUtils.clear(errorPath);
        IOUtils.clear(llvmOutputPath);

    }


}
