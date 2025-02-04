import error.ErrorHandler;
import ir.IRVisitor;
import ir.Module;
import lexer.Lexer;
import lexer.Token;
import parser.Parser;
import semantic.Visitor;
import utils.IOUtils;
import utils.Settings;

public class Compiler {
    public static void main(String[] args) {
        Settings.init();
        String sourceCode = IOUtils.readFile(Settings.inputPath);

        // 词法分析
        Lexer.getInstance().lexerAnalyze(sourceCode);

        // 语法分析
        Parser.getInstance().setTokens(Lexer.getInstance().getTokens());
        Parser.getInstance().parse();

        // 语义分析
        Visitor visitor = new Visitor();
        visitor.setTokens(Lexer.getInstance().getTokens());
        visitor.visit(Parser.getInstance().getRoot());



        // ERROR
        if (ErrorHandler.getInstance().isHaveError()) {
            ErrorHandler.getInstance().printErrors(Settings.errorPath);
            return;
        }

        // llvm
        IRVisitor.getInstance().visitCompUnitNode(Parser.getInstance().getRoot());

        //输出
        if (Settings.lexer) {
            StringBuilder sb = new StringBuilder();
            for (Token token : Lexer.getInstance().getTokens()) {
                sb.append(token.toString()).append('\n');
            }
            utils.IOUtils.writeFile(Settings.lexerOutputPath, sb.toString());
        }

        if (Settings.syntax) {
            Parser.getInstance().printAns();
        }

        if (Settings.symbol) {
            visitor.print();
        }

        if (Settings.llvm) {
            IOUtils.writeFile(Settings.llvmOutputPath, Module.getInstance().toString());
        }


    }
}
