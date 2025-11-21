package br.com.mlp.compiler.semantics;

import java.math.BigDecimal;
import java.math.BigInteger;
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
    private final List<TokenInfo> tokens;

    public SemanticAnalyzer(ErrorReporter reporter, List<TokenInfo> tokens) {
        this.reporter = reporter;
        this.tokens = tokens != null ? tokens : new ArrayList<>();
    }

    public SymbolTable analyze(ProgramNode program) {
        // 1) Declarações
        for (DeclNode d : program.getDeclarations()) {
            for (String name : d.getVarNames()) {
                var pos = findFirstToken(name);
                
                // Premissa 1: tamanho do identificador
                if (name.length() > 10) {
                    reportSem(ErrorCode.SEMANTICO_IDENT_TAMANHO_EXCEDIDO, pos[0], pos[1],
                            "identificador '" + name + "' tem " + name.length() + " caracteres");
                }
                
                // COD.204: Verificar redeclaração
                if (!symbols.declare(name, d.getType(), pos[0], pos[1])) {
                    // Usar última ocorrência para reportar na linha da redeclaração
                    var redeclPos = findLastToken(name);
                    reportSem(ErrorCode.SEMANTICO_VARIAVEL_REDECLARADA, redeclPos[0], redeclPos[1],
                            "COD.204 - Variável '" + name + "' redeclarada");
                }
            }
        }

        // 2) Comandos (com verificação de profundidade)
        checkCommands(program.getCommands(), 1);

        // 3) COD.208: Verificar variáveis declaradas mas não utilizadas
        for (SymbolTable.Entry entry : symbols.all()) {
            if (!entry.usada) {
                reportSem(ErrorCode.SEMANTICO_VARIAVEL_NAO_UTILIZADA, entry.line, entry.column,
                        "COD.208 - Variável declarada mas não utilizada [variável '" + entry.name + "']");
            }
        }

        return symbols;
    }

    // ----- Profundidade e checagem de comandos -----

    private void checkCommands(List<CommandNode> cmds, int depth) {
        if (cmds == null) return;
        if (depth > 10) {
            // Premissa 2: profundidade > 10
            reportSem(ErrorCode.SEMANTICO_PROFUNDIDADE_COMANDOS, 1, 1,
                    "profundidade de comandos excede 10 (=" + depth + ")");
            return;
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

        // COD.209: Verificar auto-atribuição desnecessária (x = x)
        ExpressionNode expr = a.getExpression();
        if (expr instanceof VarRefNode varRef && varRef.getName().equals(var)) {
            // Encontrar posição da variável no RHS (uso após declaração)
            int[] exprPos = findTokenUsage(var, varEntry.line);
            // Se não encontrar uso após declaração, usar posição do LHS como fallback
            if (exprPos[0] == varEntry.line && exprPos[1] == varEntry.column) {
                exprPos = varPos;
            }
            reportSem(ErrorCode.SEMANTICO_AUTO_ATRIBUICAO, exprPos[0], exprPos[1],
                    "COD.209 - Auto-atribuição desnecessária [variável '" + var + "']");
        }

        // Avaliar expressão (isso vai marcar variáveis usadas e verificar inicialização)
        Type rhs = evalExpr(a.getExpression());

        // 4b/4c) compatibilidade
        if (!isAssignable(varEntry.type, rhs)) {
            reportSem(ErrorCode.SEMANTICO_TIPO_INCOMPATIVEL, varPos[0], varPos[1],
                    "atribuição incompatível: " + varEntry.type + " <- " + rhs);
        }

        // Marcar variável como inicializada após atribuição
        varEntry.inicializada = true;
        // Também marcar como usada quando aparece no LHS (para COD.208 mais amigável)
        varEntry.usada = true;
    }

    // ----- Condição -----

    private void checkCondition(ConditionNode cond) {
        if (cond == null) return;
        Type left = evalExpr(cond.getLeft());
        Type right = evalExpr(cond.getRight());

        if (!(isNumeric(left) && isNumeric(right)) && !(left == Type.CARACTER && right == Type.CARACTER)) {
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
            String value = n.getValue();
            boolean isReal = value.contains(".");
            int[] pos = findFirstToken(value);
            
            // COD.206: Verificar overflow numérico
            if (isReal) {
                checkRealOverflow(value, pos[0], pos[1]);
            } else {
                checkIntegerOverflow(value, pos[0], pos[1]);
            }
            
            return isReal ? Type.REAL : Type.INTEIRO;
        }
        if (e instanceof VarRefNode v) {
            String name = v.getName();
            var entry = symbols.lookup(name);
            // Encontrar posição do uso atual (após a declaração)
            int[] pos = entry != null ? findTokenUsage(name, entry.line) : findFirstToken(name);

            if (entry == null) {
                reportSem(ErrorCode.SEMANTICO_VARIAVEL_NAO_DECLARADA, pos[0], pos[1],
                        "uso de variável '" + name + "' sem declaração");
                return null;
            }

            if (name.length() > 10) {
                reportSem(ErrorCode.SEMANTICO_IDENT_TAMANHO_EXCEDIDO, pos[0], pos[1],
                        "identificador '" + name + "' tem " + name.length() + " caracteres (uso)");
            }

            // COD.207: Verificar se variável foi inicializada antes de usar
            if (!entry.inicializada) {
                reportSem(ErrorCode.SEMANTICO_VARIAVEL_NAO_INICIALIZADA, pos[0], pos[1],
                        "COD.207 - Uso de variável não inicializada [variável '" + name + "']");
            }

            // Marcar variável como usada
            entry.usada = true;

            return entry.type;
        }
        if (e instanceof BinaryExprNode b) {
            Type l = evalExpr(b.getLeft());
            Type r = evalExpr(b.getRight());
            String op = b.getOp();

            if (!isNumeric(l) || !isNumeric(r)) {
                int[] pos = {1,1};
                if (b.getLeft() instanceof VarRefNode v) pos = findFirstToken(v.getName());
                reportSem(ErrorCode.SEMANTICO_TIPO_INCOMPATIVEL, pos[0], pos[1],
                        "operação '" + op + "' inválida para tipos: " + l + " e " + r);
                return null;
            }

            // COD.205: Verificar divisão por zero (apenas para constantes)
            if (op.equals("/") || op.equals("RESTO")) {
                Double rightValue = evalConstantValue(b.getRight());
                if (rightValue != null && rightValue == 0.0) {
                    // Encontrar posição do operador "/" ou "RESTO"
                    int[] opPos = findOperatorPosition(op, b);
                    reportSem(ErrorCode.SEMANTICO_DIVISAO_POR_ZERO, opPos[0], opPos[1],
                            "COD.205 – Divisão por zero");
                }
            }

            // COD.206: Verificar overflow em expressões constantes
            Double constValue = evalConstantValue(b);
            if (constValue != null) {
                Type resultType = (l == Type.REAL || r == Type.REAL) ? Type.REAL : Type.INTEIRO;
                int[] exprPos = findExpressionPosition(b);
                checkConstantOverflow(constValue, resultType, exprPos[0], exprPos[1]);
            }

            if (l == Type.REAL || r == Type.REAL) return Type.REAL;
            return Type.INTEIRO;
        }

        return null;
    }

    private boolean isNumeric(Type t) {
        return t == Type.INTEIRO || t == Type.REAL;
    }

    private boolean isAssignable(Type target, Type source) {
        if (target == null || source == null) return false;
        if (target == source) return true;
        if (target == Type.REAL && source == Type.INTEIRO) return true;
        return false;
    }

    // ----- Avaliação de expressões constantes (para divisão por zero) -----

    /**
     * Avalia uma expressão e retorna seu valor numérico se for constante,
     * ou null se depender de variáveis (não constante em tempo de compilação).
     */
    private Double evalConstantValue(ExpressionNode e) {
        if (e == null) return null;

        if (e instanceof NumLiteralNode n) {
            try {
                return Double.parseDouble(n.getValue());
            } catch (NumberFormatException ex) {
                return null;
            }
        }

        if (e instanceof VarRefNode) {
            // Variável não é constante
            return null;
        }

        if (e instanceof BinaryExprNode b) {
            Double leftVal = evalConstantValue(b.getLeft());
            Double rightVal = evalConstantValue(b.getRight());

            // Se qualquer operando não for constante, a expressão não é constante
            if (leftVal == null || rightVal == null) {
                return null;
            }

            String op = b.getOp();
            return switch (op) {
                case "+" -> leftVal + rightVal;
                case "-" -> leftVal - rightVal;
                case "*" -> leftVal * rightVal;
                case "/" -> leftVal / rightVal; // Pode ser Infinity se rightVal == 0, mas já detectamos antes
                case "RESTO" -> leftVal % rightVal;
                default -> null;
            };
        }

        return null;
    }

    // Encontra a posição do operador nos tokens
    private int[] findOperatorPosition(String operator, BinaryExprNode expr) {
        // Buscar pelo operador nos tokens
        // O texto do operador pode ser: "+", "-", "*", "/", "RESTO"
        String opText = operator;
        
        // Encontrar posições dos operandos
        int[] leftPos = findExpressionPosition(expr.getLeft());
        int[] rightPos = findExpressionPosition(expr.getRight());
        
        // Procurar o operador entre os operandos esquerdo e direito
        for (TokenInfo t : tokens) {
            if (Objects.equals(t.text, opText)) {
                // Verificar se está entre os operandos (após o esquerdo e antes do direito)
                boolean afterLeft = (t.line > leftPos[0]) || 
                                   (t.line == leftPos[0] && t.column >= leftPos[1]);
                boolean beforeRight = (t.line < rightPos[0]) || 
                                     (t.line == rightPos[0] && t.column < rightPos[1]);
                
                if (afterLeft && beforeRight) {
                    return new int[]{t.line, t.column + 1};
                }
            }
        }
        
        // Fallback: usar posição do operando direito
        return rightPos;
    }
    
    // Encontra a posição de uma expressão nos tokens
    private int[] findExpressionPosition(ExpressionNode e) {
        if (e instanceof NumLiteralNode n) {
            return findFirstToken(n.getValue());
        }
        if (e instanceof VarRefNode v) {
            return findFirstToken(v.getName());
        }
        if (e instanceof BinaryExprNode b) {
            // Usar posição do operando direito como aproximação
            return findExpressionPosition(b.getRight());
        }
        return new int[]{1, 1};
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
                return new int[]{t.line, t.column + 1};
            }
        }
        return new int[]{1,1};
    }

    // busca última ocorrência do lexema nos tokens (útil para redeclarações)
    private int[] findLastToken(String text) {
        if (text == null) return new int[]{1,1};
        int[] lastPos = new int[]{1,1};
        for (TokenInfo t : tokens) {
            if (Objects.equals(t.text, text)
                && (t.type == MlpLexer.IDENT || t.type == MlpLexer.NUM)) {
                lastPos = new int[]{t.line, t.column + 1};
            }
        }
        return lastPos;
    }

    // busca ocorrência do token após a declaração (para uso em expressões)
    private int[] findTokenUsage(String text, int declLine) {
        if (text == null) return new int[]{1,1};
        // Procurar a primeira ocorrência após a linha de declaração
        for (TokenInfo t : tokens) {
            if (Objects.equals(t.text, text)
                && (t.type == MlpLexer.IDENT || t.type == MlpLexer.NUM)
                && t.line > declLine) {
                return new int[]{t.line, t.column + 1};
            }
        }
        // Se não encontrar após declaração, usar última ocorrência
        return findLastToken(text);
    }

    // ----- Validação de Overflow Numérico (COD.206) -----

    // Limites para inteiro de 32 bits
    private static final BigInteger MIN_INTEGER = BigInteger.valueOf(Integer.MIN_VALUE);
    private static final BigInteger MAX_INTEGER = BigInteger.valueOf(Integer.MAX_VALUE);
    
    // Limites para real (double de 64 bits)
    private static final BigDecimal MAX_REAL_POSITIVE = BigDecimal.valueOf(Double.MAX_VALUE);
    private static final BigDecimal MIN_REAL_NEGATIVE = BigDecimal.valueOf(-Double.MAX_VALUE);

    /**
     * Verifica se um literal inteiro causa overflow.
     */
    private void checkIntegerOverflow(String value, int line, int col) {
        try {
            BigInteger bigInt = new BigInteger(value);
            if (bigInt.compareTo(MIN_INTEGER) < 0 || bigInt.compareTo(MAX_INTEGER) > 0) {
                reportSem(ErrorCode.SEMANTICO_OVERFLOW_NUMERICO, line, col,
                        "COD.206 - Overflow Numérico — literal fora do intervalo permitido");
            }
        } catch (NumberFormatException e) {
            // Se não conseguir fazer parse, já é um problema, mas não é overflow
            // O lexer já deveria ter validado o formato
        }
    }

    /**
     * Verifica se um literal real causa overflow.
     */
    private void checkRealOverflow(String value, int line, int col) {
        try {
            BigDecimal bigDec = new BigDecimal(value);
            
            // Verificar se está dentro dos limites do double
            if (bigDec.compareTo(MIN_REAL_NEGATIVE) < 0 || bigDec.compareTo(MAX_REAL_POSITIVE) > 0) {
                reportSem(ErrorCode.SEMANTICO_OVERFLOW_NUMERICO, line, col,
                        "COD.206 - Overflow Numérico — literal fora do intervalo permitido");
                return;
            }
            
            // Verificar também se é infinito ou NaN (casos especiais)
            double doubleValue = bigDec.doubleValue();
            if (Double.isInfinite(doubleValue) || Double.isNaN(doubleValue)) {
                reportSem(ErrorCode.SEMANTICO_OVERFLOW_NUMERICO, line, col,
                        "COD.206 - Overflow Numérico — literal fora do intervalo permitido");
            }
        } catch (NumberFormatException e) {
            // Se não conseguir fazer parse, já é um problema
        } catch (ArithmeticException e) {
            // Overflow ao converter para double
            reportSem(ErrorCode.SEMANTICO_OVERFLOW_NUMERICO, line, col,
                    "COD.206 - Overflow Numérico — literal fora do intervalo permitido");
        }
    }

    /**
     * Verifica se uma expressão constante causa overflow.
     */
    private void checkConstantOverflow(Double value, Type targetType, int line, int col) {
        if (value == null) return;
        
        if (targetType == Type.INTEIRO) {
            // Verificar se cabe em 32 bits
            BigInteger bigInt = BigDecimal.valueOf(value).toBigInteger();
            if (bigInt.compareTo(MIN_INTEGER) < 0 || bigInt.compareTo(MAX_INTEGER) > 0) {
                reportSem(ErrorCode.SEMANTICO_OVERFLOW_NUMERICO, line, col,
                        "COD.206 - Overflow Numérico — literal fora do intervalo permitido");
            }
        } else if (targetType == Type.REAL) {
            // Verificar se é infinito ou NaN
            if (Double.isInfinite(value) || Double.isNaN(value)) {
                reportSem(ErrorCode.SEMANTICO_OVERFLOW_NUMERICO, line, col,
                        "COD.206 - Overflow Numérico — literal fora do intervalo permitido");
            } else {
                // Verificar se está dentro dos limites do double
                BigDecimal bigDec = BigDecimal.valueOf(value);
                if (bigDec.compareTo(MIN_REAL_NEGATIVE) < 0 || bigDec.compareTo(MAX_REAL_POSITIVE) > 0) {
                    reportSem(ErrorCode.SEMANTICO_OVERFLOW_NUMERICO, line, col,
                            "COD.206 - Overflow Numérico — literal fora do intervalo permitido");
                }
            }
        }
    }
}
