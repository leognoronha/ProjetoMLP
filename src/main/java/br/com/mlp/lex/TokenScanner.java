package br.com.mlp.lex;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.*;
import br.com.mlp.compiler.parser.MlpLexer;
import br.com.mlp.diagnostics.*;

public class TokenScanner {

    private final ErrorReporter reporter;

    public TokenScanner(ErrorReporter reporter) {
        this.reporter = reporter;
    }

    public List<TokenInfo> scan(CharStream input) {
        MlpLexer lexer = new MlpLexer(input);
        List<TokenInfo> tokens = new ArrayList<>();

        while (true) {
            Token t = lexer.nextToken();
            if (t.getType() == Token.EOF) break;

            String symbolic = MlpLexer.VOCABULARY.getSymbolicName(t.getType());
            boolean isReserved = isReservedToken(t.getType());

            // Se for token de erro léxico (ERROR_CHAR), reporta diagnóstico
            if (t.getType() == MlpLexer.ERROR_CHAR) {
                reporter.add(new Diagnostic(
                        ErrorType.LEXICO,
                        ErrorCode.LEXICO_TOKEN_DESCONHECIDO,
                        t.getLine(),
                        t.getCharPositionInLine() + 1,
                        "símbolo '" + t.getText() + "'",
                        t.getText()
                ));
            }

            tokens.add(new TokenInfo(
                    t.getText(),
                    symbolic,
                    t.getType(),
                    t.getLine(),
                    t.getCharPositionInLine(),
                    isReserved
            ));
        }

        return tokens;
    }

    // Palavra reservada = tokens definidos como palavras-chave no lexer
    // (INTEIRO, REAL, CARACTER, SE, ENTAO, SENAO, ENQUANTO, E, OR, NOT, RESTO)
    private boolean isReservedToken(int type) {
        return type == MlpLexer.INTEIRO
            || type == MlpLexer.REAL
            || type == MlpLexer.CARACTER
            || type == MlpLexer.SE
            || type == MlpLexer.ENTAO
            || type == MlpLexer.SENAO
            || type == MlpLexer.ENQUANTO
            || type == MlpLexer.E
            || type == MlpLexer.OR
            || type == MlpLexer.NOT
            || type == MlpLexer.RESTO;
    }
}
