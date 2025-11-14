package br.com.mlp.lex;

public class TokenInfo {
    public final String text;
    public final String typeName;
    public final int type;
    public final int line;
    public final int column;

    public final boolean isReserved;

    public TokenInfo(String text, String typeName, int type, int line, int column, boolean isReserved) {
        this.text = text;
        this.typeName = typeName;
        this.type = type;
        this.line = line;
        this.column = column;
        this.isReserved = isReserved;
    }
}
