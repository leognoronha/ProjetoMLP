package br.com.mlp.compiler.ast;

import java.util.List;

/**
 * Nó raiz da AST: representa todo o programa MLP.
 */
public class ProgramNode implements AstNode {

    private final List<DeclNode> declarations;
    private final List<CommandNode> commands;

    public ProgramNode(List<DeclNode> declarations, List<CommandNode> commands) {
        this.declarations = declarations;
        this.commands = commands;
    }

    public List<DeclNode> getDeclarations() {
        return declarations;
    }

    public List<CommandNode> getCommands() {
        return commands;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Program(\n  Decls:\n");
        for (DeclNode d : declarations) {
            sb.append("    ").append(d).append("\n");
        }
        sb.append("  Commands:\n");
        for (CommandNode c : commands) {
            sb.append("    ").append(c).append("\n");
        }
        sb.append(")");
        return sb.toString();
    }
}
