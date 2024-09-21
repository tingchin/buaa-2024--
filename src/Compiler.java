import error.ErrorHandler;
import lexer.Lexer;
import lexer.Token;

public class Compiler {
    public static void main(String[] args) {
        String sourceCode = utils.IoUtils.readFile(Settings.inputPath);

        // 词法分析
        Lexer.getInstance().lexerAnalyze(sourceCode);

        // error
        if (ErrorHandler.getInstance().isHaveError()) {
            ErrorHandler.getInstance().printErrors(Settings.errorPath);
            return;
        }
        // 输出
        if (Settings.lexer) {
            StringBuilder sb = new StringBuilder();
            for (Token token : Lexer.getInstance().getTokens()) {
                sb.append(token.toString()).append('\n');
            }
            utils.IoUtils.writeFile(Settings.lexerOutputPath, sb.toString());
        }
    }
}
