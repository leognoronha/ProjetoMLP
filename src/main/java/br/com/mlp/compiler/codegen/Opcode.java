package br.com.mlp.compiler.codegen;

public enum Opcode {
    // Carregamento e armazenamento
    LOAD,      // LOAD R, var        - carrega variável para registrador
    LOADI,     // LOADI R, const     - carrega constante para registrador
    STORE,     // STORE var, R       - armazena registrador em variável

    // Aritmética
    ADD,       // ADD Rdest, R1, R2  - Rdest = R1 + R2
    ADDI,      // ADDI Rdest, const  - Rdest = Rdest + const
    SUB,       // SUB Rdest, R1, R2  - Rdest = R1 - R2
    SUBI,      // SUBI Rdest, const  - Rdest = Rdest - const
    MUL,       // MUL Rdest, R1, R2  - Rdest = R1 * R2
    DIV,       // DIV Rdest, R1, R2  - Rdest = R1 / R2

    // Comparações
    CMPGT,     // >    (R1 > R2)
    CMPLT,     // <    (R1 < R2)
    CMPGE,     // >=   (R1 >= R2)
    CMPLE,     // <=   (R1 <= R2)
    CMPEQ,     // ==   (R1 == R2)
    CMPNE,     // !=   (R1 != R2)

    // Saltos
    JMP,       // JMP L          - salto incondicional para L
    JMPFALSE,  // JMPFALSE R, L  - salta para L se R == 0 (falso)
    JMPTRUE,   // JMPTRUE R, L   - salta para L se R != 0 (verdadeiro)

    // Rótulo
    LABEL      // LABEL L        - define rótulo L
}
