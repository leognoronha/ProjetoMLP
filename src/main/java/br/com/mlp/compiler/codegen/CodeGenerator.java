package br.com.mlp.compiler.codegen;

import br.com.mlp.compiler.ast.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Gera código intermediário em forma de TAC (Three-Address Code)
 * seguindo os mnemônicos da MLP.
 *
 * Convenções:
 *  - Registradores são nomeados como R1, R2, R3, ...
 *  - Rótulos são nomeados como L1, L2, L3, ...
 *  - Variáveis são usadas diretamente pelo seu nome (STORE a, R1).
 */
public class CodeGenerator {

    private int nextReg = 1;
    private int nextLabel = 1;

    private final List<TacInstruction> instructions = new ArrayList<>();

    public List<TacInstruction> generate(ProgramNode program) {
        instructions.clear();

        for (CommandNode cmd : program.getCommands()) {
            genCommand(cmd);
        }

        return new ArrayList<>(instructions);
    }

    /* ------------ Helpers de registradores e rótulos ------------ */

    private String newReg() {
        return "R" + (nextReg++);
    }

    private String newLabel() {
        return "L" + (nextLabel++);
    }

    private void emit(Opcode op, String... args) {
        instructions.add(new TacInstruction(op, args));
    }

    /* ------------ Geração de comandos ------------ */

    private void genCommand(CommandNode cmd) {
        if (cmd instanceof AssignNode assign) {
            genAssign(assign);
        } else if (cmd instanceof IfNode ifNode) {
            genIf(ifNode);
        } else if (cmd instanceof WhileNode whileNode) {
            genWhile(whileNode);
        } else {
        }
    }

    private void genAssign(AssignNode node) {
        String varName = node.getVarName();

        String regValue = genExpr(node.getExpression());

        emit(Opcode.STORE, varName, regValue);
    }

    private void genIf(IfNode node) {
        String labelElse = newLabel();
        String labelEnd = newLabel();

        // Gera condição: se falsa, pula para labelElse
        String condReg = genCond(node.getCondition());
        emit(Opcode.JMPFALSE, condReg, labelElse);

        // then
        genCommand(node.getThenCommand());
        emit(Opcode.JMP, labelEnd);

        // else (se existir)
        emit(Opcode.LABEL, labelElse);
        if (node.getElseCommand() != null) {
            genCommand(node.getElseCommand());
        }

        // fim do if
        emit(Opcode.LABEL, labelEnd);
    }

    private void genWhile(WhileNode node) {
        String labelStart = newLabel();
        String labelEnd = newLabel();

        emit(Opcode.LABEL, labelStart);

        String condReg = genCond(node.getCondition());
        emit(Opcode.JMPFALSE, condReg, labelEnd);

        genCommand(node.getBody());

        emit(Opcode.JMP, labelStart);
        emit(Opcode.LABEL, labelEnd);
    }

    /* ------------ Geração de condição (comparações) ------------ */

    private String genCond(ConditionNode cond) {
        ExpressionNode left = cond.getLeft();
        ExpressionNode right = cond.getRight();
        String op = cond.getOp(); // ">", "<", "==", "!=", ">=", "<="

        String r1 = genExpr(left);
        String r2 = genExpr(right);

        switch (op) {
            case ">" -> emit(Opcode.CMPGT, r1, r2);
            case "<" -> emit(Opcode.CMPLT, r1, r2);
            case ">=" -> emit(Opcode.CMPGE, r1, r2);
            case "<=" -> emit(Opcode.CMPLE, r1, r2);
            case "==" -> emit(Opcode.CMPEQ, r1, r2);
            case "!=" -> emit(Opcode.CMPNE, r1, r2);
            default -> {
                emit(Opcode.CMPEQ, r1, r2);
            }
        }

        return r1;
    }

    /* ------------ Geração de expressões aritméticas ------------ */

    private String genExpr(ExpressionNode expr) {
        if (expr instanceof NumLiteralNode num) {
            String r = newReg();
            emit(Opcode.LOADI, r, num.getValue()); // "10", "1.0", etc.
            return r;
        }

        if (expr instanceof VarRefNode var) {
            String r = newReg();
            emit(Opcode.LOAD, r, var.getName());
            return r;
        }

        if (expr instanceof BinaryExprNode bin) {
            String rLeft = genExpr(bin.getLeft());
            String rRight = genExpr(bin.getRight());
            String rDest = newReg();

            String op = bin.getOp(); // "+", "-", "*", "/", "RESTO"

            switch (op) {
                case "+" -> emit(Opcode.ADD, rDest, rLeft, rRight);
                case "-" -> emit(Opcode.SUB, rDest, rLeft, rRight);
                case "*" -> emit(Opcode.MUL, rDest, rLeft, rRight);
                case "/" -> emit(Opcode.DIV, rDest, rLeft, rRight);
                case "RESTO" -> {
                    // Implementar resto usando DIV/MUL/SUB
                    // q = left / right
                    String rQ = newReg();
                    emit(Opcode.DIV, rQ, rLeft, rRight);

                    String rQY = newReg();
                    emit(Opcode.MUL, rQY, rQ, rRight);

                    emit(Opcode.SUB, rDest, rLeft, rQY);
                }
                default -> {
                    emit(Opcode.ADD, rDest, rLeft, rRight);
                }
            }

            return rDest;
        }

        String r = newReg();
        emit(Opcode.LOADI, r, "0");
        return r;
    }
}
