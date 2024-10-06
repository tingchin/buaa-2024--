package parser;

import lexer.Token;

import java.util.ArrayList;
import java.util.List;

public class Node {
    private NodeType type;
    private List<Node> children;
    private Token token;

    public Node(NodeType type, Token token) {
        this.type = type;
        this.token = token;;
        this.children = new ArrayList<>();
    }

    public NodeType getType() {
        return type;
    }

    public Token getToken() {
        return token;
    }

    public void addChild(Node chile) {
        children.add(chile);
    }
}
