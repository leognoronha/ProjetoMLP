package br.com.mlp.compiler.ast;

/**
 * Representa uma comparação simples em uma condição:
 *   a > 10
 *   x == y
 */
public class ConditionNode implements AstNode {

    private final ExpressionNode left;
    private final String op;     // ">", "<", "==", "<=", ">=", "!="
    private final ExpressionNode right;

    public ConditionNode(ExpressionNode left, String op, ExpressionNode right) {
        this.left = left;
        this.op = op;
        this.right = right;
    }

    public ExpressionNode getLeft() {
        return left;
    }

    public String getOp() {
        return op;
    }

    public ExpressionNode getRight() {
        return right;
    }

    @Override
    public String toString() {
        return "Cond(" + left + " " + op + " " + right + ")";
    }
}
