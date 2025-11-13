package br.com.mlp.compiler.semantics;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import br.com.mlp.compiler.ast.*;
import br.com.mlp.diagnostics.*;
import br.com.mlp.lex.TokenInfo;
import br.com.mlp.compiler.parser.MlpLexer;

public class SemanticAnalyzer {

    private final ErrorReporter reporter;
    private final SymbolTable symbols = new SymbolTable();

    // tokens do léxico para localizar linha/coluna (melhor esforço)
    private final List<TokenInfo> tokens;

    public SemanticAnalyzer(ErrorReporter reporter, List<TokenInfo> tokens) {
        this.reporter = reporter;
        this.tokens = tokens != null ? tokens : new ArrayList<>();
    }

    public SymbolTable analyze(ProgramNode program) {
        // 1) Declarações
        for (DeclNode d : program.getDeclarations()) {
            for (String name : d.getVarNames()) {
                // Premissa 1: tamanho do identificador
                if (name.length() > 10) {
                    var pos = findFirstToken(name);
                    reportSem(ErrorCode.SEMANTICO_IDENT_TAMANHO_EXCEDIDO, pos[0], pos[1],
                            "identificador '" + name + "' tem " + name.length() + " caracteres");
                }
                var pos = findFirstToken(name);
                symbols.declare(name, d.getType(), pos[0], pos[1]);
            }
        }

        // 2) Comandos (com verificação de profundidade)
        checkCommands(program.getCommands(), 1);

        return symbols;
    }

    // ----- Profundidade e checagem de comandos -----

    private void checkCommands(List<CommandNode> cmds, int depth) {
        if (cmds == null) return;
        if (depth > 10) {
            // Premissa 2: profundidade > 10
            reportSem(ErrorCode.SEMANTICO_PROFUNDIDADE_COMANDOS, 1, 1,
                    "profundidade de comandos excede 10 (=" + depth + ")");
            return; // continua, mas já reportou
        }
        for (CommandNode c : cmds) {
            if (c instanceof AssignNode a) {
                checkAssign(a);
            } else if (c instanceof IfNode i) {
                checkCondition(i.getCondition());
                // then / else incrementam profundidade
                List<CommandNode> thenList = new ArrayList<>();
                if (i.getThenCommand() != null) thenList.add(i.getThenCommand());
                checkCommands(thenList, depth + 1);

                if (i.getElseCommand() != null) {
                    List<CommandNode> elseList = new ArrayList<>();
                    elseList.add(i.getElseCommand());
                    checkCommands(elseList, depth + 1);
                }
            } else if (c instanceof WhileNode w) {
                checkCondition(w.getCondition());
                List<CommandNode> body = new ArrayList<>();
                if (w.getBody() != null) body.add(w.getBody());
                checkCommands(body, depth + 1);
            }
        }
    }

    // ----- Atribuição -----

    private void checkAssign(AssignNode a) {
        String var = a.getVarName();
        var varEntry = symbols.lookup(var);
        var varPos = findFirstToken(var);

        // 4a) variável deve estar declarada
        if (varEntry == null) {
            reportSem(ErrorCode.SEMANTICO_VARIAVEL_NAO_DECLARADA, varPos[0], varPos[1],
                    "uso de variável '" + var + "' sem declaração");
            return;
        }

        // 1) tamanho do ident no uso
        if (var.length() > 10) {
            reportSem(ErrorCode.SEMANTICO_IDENT_TAMANHO_EXCEDIDO, varPos[0], varPos[1],
                    "identificador '" + var + "' tem " + var.length() + " caracteres (uso)");
        }

        // tipo do RHS
        Type rhs = evalExpr(a.getExpression());

        // 4b/4c) compatibilidade
        if (!isAssignable(varEntry.type, rhs)) {
            reportSem(ErrorCode.SEMANTICO_TIPO_INCOMPATIVEL, varPos[0], varPos[1],
                    "atribuição incompatível: " + varEntry.type + " <- " + rhs);
        }
    }

    // ----- Condição -----

    private void checkCondition(ConditionNode cond) {
        if (cond == null) return;
        Type left = evalExpr(cond.getLeft());
        Type right = evalExpr(cond.getRight());

        // ambos precisam ser numéricos (int/real) ou ambos char?
        // Aqui consideramos numéricos, já que operadores relacionais vêm de expr aritmética/ident/num
        if (!(isNumeric(left) && isNumeric(right)) && !(left == Type.CARACTER && right == Type.CARACTER)) {
            // posição aproximada no identificador esquerdo
            int[] pos = {1,1};
            if (cond.getLeft() instanceof VarRefNode v) pos = findFirstToken(v.getName());
            reportSem(ErrorCode.SEMANTICO_TIPO_INCOMPATIVEL, pos[0], pos[1],
                    "comparação inválida: " + left + " vs " + right);
        }
    }

    // ----- Expressões -----

    private Type evalExpr(ExpressionNode e) {
        if (e == null) return null;

        if (e instanceof NumLiteralNode n) {
            // se tem ponto, REAL; senão, INTEIRO
            return n.getValue().contains(".") ? Type.REAL : Type.INTEIRO;
        }
        if (e instanceof VarRefNode v) {
            String name = v.getName();
            var entry = symbols.lookup(name);
            int[] pos = findFirstToken(name);

            // 4a) variável usada deve existir
            if (entry == null) {
                reportSem(ErrorCode.SEMANTICO_VARIAVEL_NAO_DECLARADA, pos[0], pos[1],
                        "uso de variável '" + name + "' sem declaração");
                return null;
            }

            // 1) tamanho no uso
            if (name.length() > 10) {
                reportSem(ErrorCode.SEMANTICO_IDENT_TAMANHO_EXCEDIDO, pos[0], pos[1],
                        "identificador '" + name + "' tem " + name.length() + " caracteres (uso)");
            }
            return entry.type;
        }
        if (e instanceof BinaryExprNode b) {
            Type l = evalExpr(b.getLeft());
            Type r = evalExpr(b.getRight());
            String op = b.getOp();

            // operações aritméticas só com numéricos
            if (!isNumeric(l) || !isNumeric(r)) {
                int[] pos = {1,1};
                if (b.getLeft() instanceof VarRefNode v) pos = findFirstToken(v.getName());
                reportSem(ErrorCode.SEMANTICO_TIPO_INCOMPATIVEL, pos[0], pos[1],
                        "operação '" + op + "' inválida para tipos: " + l + " e " + r);
                return null;
            }

            // promoção para REAL se algum lado é REAL
            if (l == Type.REAL || r == Type.REAL) return Type.REAL;
            return Type.INTEIRO;
        }

        return null;
    }

    // ----- Helpers de tipos -----

    private boolean isNumeric(Type t) {
        return t == Type.INTEIRO || t == Type.REAL;
    }

    // Regras de atribuição:
    // - INTEIRO <- INTEIRO
    // - REAL    <- REAL | INTEIRO
    // - CARACTER<- CARACTER
    private boolean isAssignable(Type target, Type source) {
        if (target == null || source == null) return false;
        if (target == source) return true;
        if (target == Type.REAL && source == Type.INTEIRO) return true; // widening
        return false;
    }

    // ----- Report & localização aproximada -----

    private void reportSem(ErrorCode code, int line, int col, String msg) {
        reporter.add(new Diagnostic(
                ErrorType.SEMANTICO,
                code,
                Math.max(line, 1),
                Math.max(col, 1),
                msg,
                ""
        ));
    }

    // busca primeira ocorrência do lexema nos tokens para estimar linha/coluna
    private int[] findFirstToken(String text) {
        if (text == null) return new int[]{1,1};
        for (TokenInfo t : tokens) {
            if (Objects.equals(t.text, text)
                && (t.type == MlpLexer.IDENT || t.type == MlpLexer.NUM)) {
                return new int[]{t.line, t.column + 1}; // coluna 1-based
            }
        }
        return new int[]{1,1};
    }
}
