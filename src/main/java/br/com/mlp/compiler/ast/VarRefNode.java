package br.com.mlp.compiler.ast;

/**
 * Referência a uma variável em uma expressão.
 */
public class VarRefNode extends ExpressionNode {

    private final String name;

    public VarRefNode(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Var(" + name + ")";
    }
}
