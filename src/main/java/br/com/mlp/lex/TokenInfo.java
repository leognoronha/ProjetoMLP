package br.com.mlp.lex;

public class TokenInfo {
    public final String text;
    public final String typeName;
    public final int type;
    public final int line;     // 1-based
    public final int column;   // 0-based do ANTLR (vamos exibir +1)

    public final boolean isReserved; // palavra reservada (token de palavra-chave)

    public TokenInfo(String text, String typeName, int type, int line, int column, boolean isReserved) {
        this.text = text;
        this.typeName = typeName;
        this.type = type;
        this.line = line;
        this.column = column;
        this.isReserved = isReserved;
    }
}
