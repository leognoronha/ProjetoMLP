package br.com.mlp.compiler.ast;

import java.util.List;

/**
 * Representa uma declaração de variáveis, ex:
 *   inteiro a, b;
 */
public class DeclNode implements AstNode {

    private final Type type;
    private final List<String> varNames;

    public DeclNode(Type type, List<String> varNames) {
        this.type = type;
        this.varNames = varNames;
    }

    public Type getType() {
        return type;
    }

    public List<String> getVarNames() {
        return varNames;
    }

    @Override
    public String toString() {
        return "Decl(" + type + " " + String.join(", ", varNames) + ")";
    }
}