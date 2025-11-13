package br.com.mlp;

import java.nio.file.*;
import java.util.List;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import br.com.mlp.compiler.parser.MlpLexer;
import br.com.mlp.compiler.parser.MlpParser;
import br.com.mlp.compiler.ast.AstBuilder;
import br.com.mlp.compiler.ast.ProgramNode;
import br.com.mlp.diagnostics.*;
import br.com.mlp.lex.*;

public class App {
    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.out.println("Uso: mvn exec:java -Dexec.args=\"programas/teste1.mlp\"");
            return;
        }

        String caminho = args[0];
        String codigo = Files.readString(Path.of(caminho));

        ErrorReporter reporter = new ErrorReporter();

        // ---------------- Fase A: Tokenização (léxico) ----------------
        System.out.println("== Léxico ==");
        CharStream inputA = CharStreams.fromString(codigo);
        TokenScanner scanner = new TokenScanner(reporter);
        List<TokenInfo> tokenList = scanner.scan(inputA);

        for (TokenInfo ti : tokenList) {
            System.out.printf("Linha %d, Col %d -> %-12s '%s'%s%n",
                ti.line, ti.column + 1,
                ti.typeName, ti.text,
                ti.isReserved ? "  [reservada]" : ""
            );
        }

        // Se houve erro léxico, continua para parser, mas já imprime aviso
        if (reporter.hasErrorsOfType(ErrorType.LEXICO)) {
            System.out.println("\n[AVISO] Foram encontrados erros léxicos.");
        }

        // ---------------- Fase B: Sintático (parser) ----------------
        System.out.println("\n== Sintático ==");
        CharStream inputB = CharStreams.fromString(codigo);
        MlpLexer lexer = new MlpLexer(inputB);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        MlpParser parser = new MlpParser(tokens);

        // Remove listeners padrão e adiciona o nosso
        parser.removeErrorListeners();
        parser.addErrorListener(new MlpSyntaxErrorListener(reporter));

        ParseTree tree = parser.programa();

        ProgramNode ast = null;

        if (reporter.hasErrorsOfType(ErrorType.SINTATICO)) {
            System.out.println("[AVISO] Foram encontrados erros sintáticos. AST não será construída.");
        } else {
            // ---------------- Fase C: AST (se sintaxe ok) ----------------
            System.out.println("\n== AST ==");
            AstBuilder builder = new AstBuilder();
            ast = (ProgramNode) builder.visit(tree);
            System.out.println(ast);
        }

        // ---------------- Fase D: Semântica ----------------
        if (ast != null) {
            System.out.println("\n== Semântica ==");
            var sema = new br.com.mlp.compiler.semantics.SemanticAnalyzer(reporter, tokenList);
            var symtab = sema.analyze(ast);
      
            System.out.println("Tabela de Símbolos:");
            for (var e : symtab.all()) {
              System.out.println("  - " + e);
            }
        }

        // ---------------- Consolidação: imprimir diagnósticos ----------------
        if (reporter.hasAnyError()) {
            System.out.println("\n== Erros (consolidados) ==");
            for (Diagnostic d : reporter.all()) {
                System.out.println(d.toString());
            }
        } else {
            System.out.println("\nSem erros léxicos/sintáticos/semânticos nesta fase.");
        }
    }
}
