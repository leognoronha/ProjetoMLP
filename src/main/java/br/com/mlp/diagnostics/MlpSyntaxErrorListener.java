package br.com.mlp.diagnostics;

import org.antlr.v4.runtime.*;

public class MlpSyntaxErrorListener extends BaseErrorListener {
    private final ErrorReporter reporter;

    public MlpSyntaxErrorListener(ErrorReporter reporter) {
        this.reporter = reporter;
    }

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer,
                            Object offendingSymbol,
                            int line, int charPositionInLine,
                            String msg,
                            RecognitionException e) {

        ErrorCode code = ErrorCode.SINTAXE_ESTRUTURA_INVALIDA;

        String offending = "";
        if (offendingSymbol instanceof Token t) {
            offending = t.getText();
        }

        reporter.add(new Diagnostic(
                ErrorType.SINTATICO,
                code,
                line,
                charPositionInLine + 1,
                msg,
                offending
        ));
    }
}
