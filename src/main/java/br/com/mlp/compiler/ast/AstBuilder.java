package br.com.mlp.compiler.ast;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.tree.TerminalNode;

import br.com.mlp.compiler.parser.MlpBaseVisitor;
import br.com.mlp.compiler.parser.MlpParser;

/**
 * Visitor que percorre a parse tree do ANTLR e constrói a AST.
 */
public class AstBuilder extends MlpBaseVisitor<AstNode> {

    // --------- programa ---------

    @Override
    public AstNode visitPrograma(MlpParser.ProgramaContext ctx) {
        List<DeclNode> decls = new ArrayList<>();
        for (MlpParser.TipoContext tctx : ctx.tipo()) {
            decls.add((DeclNode) visit(tctx));
        }

        List<CommandNode> cmds = new ArrayList<>();
        for (MlpParser.ComandoContext cctx : ctx.comando()) {
            CommandNode cmd = (CommandNode) visit(cctx);
            if (cmd != null) {
                cmds.add(cmd);
            }
        }

        return new ProgramNode(decls, cmds);
    }

    // --------- tipo / listaIdent ---------

    @Override
    public AstNode visitTipo(MlpParser.TipoContext ctx) {
        Type type;
        if (ctx.INTEIRO() != null) {
            type = Type.INTEIRO;
        } else if (ctx.REAL() != null) {
            type = Type.REAL;
        } else {
            type = Type.CARACTER;
        }

        List<String> names = new ArrayList<>();
        for (TerminalNode identToken : ctx.listaIdent().IDENT()) {
            names.add(identToken.getText());
        }

        return new DeclNode(type, names);
    }

    // --------- comando / comandoSimples ---------

    @Override
    public AstNode visitComando(MlpParser.ComandoContext ctx) {
        return visit(ctx.comandoSimples());
    }

    @Override
    public AstNode visitComandoSimples(MlpParser.ComandoSimplesContext ctx) {
        if (ctx.condicional() != null) {
            return visit(ctx.condicional());
        } else if (ctx.iterativo() != null) {
            return visit(ctx.iterativo());
        } else if (ctx.atribuicao() != null) {
            return visit(ctx.atribuicao());
        }
        return null;
    }

    // --------- atribuicao ---------

    @Override
    public AstNode visitAtribuicao(MlpParser.AtribuicaoContext ctx) {

        String varName = ctx.IDENT(0).getText();

        int expressaoIndex = 0;
        int identIndex = 1;
        ExpressionNode expr;
        
        if (!ctx.expressao().isEmpty() && ctx.expressao().size() > ctx.operador().size()) {
            expr = (ExpressionNode) visit(ctx.expressao(0));
            expressaoIndex = 1;
        } else if (ctx.IDENT().size() > 1) {
            expr = new VarRefNode(ctx.IDENT(1).getText());
            identIndex = 2;
        } else {
            expr = null;
        }

        for (int i = 0; i < ctx.operador().size(); i++) {
            String op = ctx.operador(i).getText();
            
            ExpressionNode right;
            if (expressaoIndex < ctx.expressao().size()) {
                right = (ExpressionNode) visit(ctx.expressao(expressaoIndex));
                expressaoIndex++;
            } else if (identIndex < ctx.IDENT().size()) {
                right = new VarRefNode(ctx.IDENT(identIndex).getText());
                identIndex++;
            } else {
                break;
            }
            
            expr = new BinaryExprNode(expr, op, right);
        }

        return new AssignNode(varName, expr);
    }

    // --------- condicional (se) ---------

    @Override
    public AstNode visitCondicional(MlpParser.CondicionalContext ctx) {
        ConditionNode cond = (ConditionNode) visit(ctx.condicao());

        CommandNode thenCmd = (CommandNode) visit(ctx.comandoSimples(0));
        CommandNode elseCmd = null;

        if (ctx.SENAO() != null) {
            elseCmd = (CommandNode) visit(ctx.comandoSimples(1));
        }

        return new IfNode(cond, thenCmd, elseCmd);
    }

    // --------- iterativo (enquanto) ---------

    @Override
    public AstNode visitIterativo(MlpParser.IterativoContext ctx) {
        ConditionNode cond = (ConditionNode) visit(ctx.condicao());
        CommandNode body = (CommandNode) visit(ctx.comandoSimples());
        return new WhileNode(cond, body);
    }

    // --------- condicao / compSimples ---------

    @Override
    public AstNode visitCondicao(MlpParser.CondicaoContext ctx) {
        MlpParser.CompSimplesContext cctx = ctx.compSimples(0);
        return visit(cctx);
    }

    @Override
    public AstNode visitCompSimples(MlpParser.CompSimplesContext ctx) {
        TerminalNode firstIdent = ctx.IDENT(0);
        ExpressionNode left = new VarRefNode(firstIdent.getText());

        String op = ctx.logico().getText();

        ExpressionNode right;
        if (ctx.NUM() != null) {
            right = new NumLiteralNode(ctx.NUM().getText());
        } else {
            TerminalNode secondIdent = ctx.IDENT(1);
            right = new VarRefNode(secondIdent.getText());
        }

        return new ConditionNode(left, op, right);
    }

    // --------- expressao / numero ---------

    @Override
    public AstNode visitExpressao(MlpParser.ExpressaoContext ctx) {
        // Caso 1: número
        if (ctx.numero() != null) {
            return visit(ctx.numero());
        }

        // Caso 2: identificador
        if (ctx.IDENT() != null) {
            return new VarRefNode(ctx.IDENT().getText());
        }
        
        // Caso 3: parênteses com operador binário
        if (ctx.expressao().size() == 2 && ctx.operador() != null) {
            ExpressionNode left = (ExpressionNode) visit(ctx.expressao(0));
            String op = ctx.operador().getText();
            ExpressionNode right = (ExpressionNode) visit(ctx.expressao(1));
            return new BinaryExprNode(left, op, right);
        }
        return null;
    }

    @Override
    public AstNode visitNumero(MlpParser.NumeroContext ctx) {
        return new NumLiteralNode(ctx.NUM().getText());
    }
}
