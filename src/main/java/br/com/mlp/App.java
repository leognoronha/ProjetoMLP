package br.com.mlp;

import java.nio.file.*;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import br.com.mlp.compiler.parser.MlpLexer;
import br.com.mlp.compiler.parser.MlpParser;
import br.com.mlp.compiler.ast.AstBuilder;
import br.com.mlp.compiler.ast.ProgramNode;

public class App {
    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.out.println("Uso: mvn exec:java -Dexec.args=\"programas/teste1.mlp\"");
            return;
        }

        String caminho = args[0];
        String codigo = Files.readString(Path.of(caminho));

        // 1) Lexer + Parser
        CharStream input = CharStreams.fromString(codigo);
        MlpLexer lexer = new MlpLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        MlpParser parser = new MlpParser(tokens);

        ParseTree tree = parser.programa();

        // SE deu erro sintático, não tenta construir AST
        if (parser.getNumberOfSyntaxErrors() > 0) {
            System.err.println("[AVISO] Foram encontrados erros sintáticos. AST não será construída.");
            return;
        }

        // 2) Construir AST
        AstBuilder builder = new AstBuilder();
        ProgramNode ast = (ProgramNode) builder.visit(tree);

        // 3) Mostrar AST
        System.out.println("AST construída:");
        System.out.println(ast);
    }
}
