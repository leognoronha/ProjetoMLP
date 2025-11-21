package br.com.mlp.diagnostics;

/**
 * Tabela de códigos de erro — extensível.
 * Ajuste/adicione conforme as necessidades da disciplina.
 */
public enum ErrorCode {
    // Léxico
    LEXICO_TOKEN_DESCONHECIDO(1, "símbolo não identificado como token"),

    // Sintático
    SINTAXE_ESPERADO_TOKEN(100, "token esperado não encontrado"),
    SINTAXE_ESTRUTURA_INVALIDA(101, "estrutura inválida na gramática"),

    // Semântico (parte 2)
    SEMANTICO_VARIAVEL_NAO_DECLARADA(200, "variável não declarada"),
    SEMANTICO_TIPO_INCOMPATIVEL(201, "atribuição ou operação com tipos incompatíveis"),
    SEMANTICO_IDENT_TAMANHO_EXCEDIDO(202, "identificador excede 10 caracteres"),
    SEMANTICO_PROFUNDIDADE_COMANDOS(203, "profundidade de comandos excede 10"),
    SEMANTICO_VARIAVEL_REDECLARADA(204, "variável redeclarada"),
    SEMANTICO_DIVISAO_POR_ZERO(205, "divisão por zero"),
    SEMANTICO_OVERFLOW_NUMERICO(206, "overflow numérico"),
    SEMANTICO_VARIAVEL_NAO_INICIALIZADA(207, "uso de variável não inicializada"),
    SEMANTICO_VARIAVEL_NAO_UTILIZADA(208, "variável declarada mas não utilizada"),
    SEMANTICO_AUTO_ATRIBUICAO(209, "auto-atribuição desnecessária");

    private final int code;
    private final String description;

    ErrorCode(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() { return code; }
    public String getDescription() { return description; }
}
