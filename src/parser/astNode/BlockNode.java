package parser.astNode;

import lexer.Token;
import utils.IoUtils;
import utils.Settings;

import java.util.List;

public class BlockNode implements Node {
    // Block → '{' { BlockItem } '}'
    private Token leftBrace;
    private Token rightBrace;

    public BlockNode(Token leftBrace, Token rightBrace, List<BlockItemNode> blockItemNodes) {
        this.leftBrace = leftBrace;
        this.rightBrace = rightBrace;
        this.blockItemNodes = blockItemNodes;
    }

    private List<BlockItemNode> blockItemNodes;

    @Override
    public void print() {
        IoUtils.writeFile(Settings.syntaxOutputPath, leftBrace.toString());
        for (BlockItemNode blockItemNode : blockItemNodes) {
            blockItemNode.print();
        }
        IoUtils.writeFile(Settings.syntaxOutputPath, rightBrace.toString());
        IoUtils.writeFile(Settings.syntaxOutputPath, "<Block>\n");
    }
}
