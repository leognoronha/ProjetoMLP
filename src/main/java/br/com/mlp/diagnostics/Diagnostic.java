package br.com.mlp.diagnostics;

public class Diagnostic {
    private final ErrorType type;
    private final ErrorCode code;
    private final int line;
    private final int column;
    private final String message;
    private final String offendingSymbol;

    public Diagnostic(ErrorType type, ErrorCode code, int line, int column, String message, String offendingSymbol) {
        this.type = type;
        this.code = code;
        this.line = line;
        this.column = column;
        this.message = message;
        this.offendingSymbol = offendingSymbol;
    }

    public ErrorType getType() { return type; }
    public ErrorCode getCode() { return code; }
    public int getLine() { return line; }
    public int getColumn() { return column; }
    public String getMessage() { return message; }
    public String getOffendingSymbol() { return offendingSymbol; }

    @Override
    public String toString() {
        return String.format("COD.%03d: erro %s (linha %d, coluna %d): %s [%s]",
                code.getCode(), type.name().toLowerCase(), line, column, code.getDescription(), message);
    }
}
