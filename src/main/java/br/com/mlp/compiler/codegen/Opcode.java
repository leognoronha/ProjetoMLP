package br.com.mlp.compiler.codegen;

public enum Opcode {
    LOAD,      // LOAD R, var
    LOADI,     // LOADI R, const
    STORE,     // STORE var, R
    ADD,       // ADD Rdest, R1, R2
    ADDI,      // ADDI Rdest, const
    SUB,       // SUB Rdest, R1, R2
    SUBI,      // SUBI Rdest, const
    MUL,       // MUL Rdest, R1, R2
    DIV,       // DIV Rdest, R1, R2

    CMPGT,     // CMPGT R1, R2   (R1 = (R1 > R2) ? 1 : 0)
    CMPLT,     // CMPLT R1, R2
    CMPGE,     // CMPGE R1, R2   (R1 = (R1 >= R2) ? 1 : 0)
    CMPLE,     // CMPLE R1, R2   (R1 = (R1 <= R2) ? 1 : 0)
    CMPEQ,     // CMPEQ R1, R2
    CMPNE,     // CMPNE R1, R2   (R1 = (R1 != R2) ? 1 : 0)

    JMP,       // JMP L
    JMPFALSE,  // JMPFALSE R, L
    JMPTRUE,   // JMPTRUE R, L

    LABEL      // LABEL L
}
