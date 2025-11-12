package br.com.mlp.compiler.ast;

/**
 * Número literal (inteiro ou real).
 * Por enquanto guardamos como String.
 */
public class NumLiteralNode extends ExpressionNode {

    private final String value;

    public NumLiteralNode(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Num(" + value + ")";
    }
}
