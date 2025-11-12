package br.com.mlp.compiler.ast;

/**
 * Expressão binária, ex:
 *  (a + 1)
 *  (x * (y / 2))
 */
public class BinaryExprNode extends ExpressionNode {

    private final ExpressionNode left;
    private final String op; // "+", "*", "/", "RESTO"
    private final ExpressionNode right;

    public BinaryExprNode(ExpressionNode left, String op, ExpressionNode right) {
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
        return "BinOp(" + left + " " + op + " " + right + ")";
    }
}
