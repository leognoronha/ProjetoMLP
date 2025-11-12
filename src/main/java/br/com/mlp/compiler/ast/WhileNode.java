package br.com.mlp.compiler.ast;

/**
 * Representa um comando iterativo:
 *   enquanto (condicao) comando;
 */
public class WhileNode extends CommandNode {

  private final ConditionNode condition;
  private final CommandNode body;

  public WhileNode(ConditionNode condition, CommandNode body) {
      this.condition = condition;
      this.body = body;
  }

  public ConditionNode getCondition() {
      return condition;
  }

  public CommandNode getBody() {
      return body;
  }

  @Override
  public String toString() {
      return "While(" + condition + ", body=" + body + ")";
  }
}