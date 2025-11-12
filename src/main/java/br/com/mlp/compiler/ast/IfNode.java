package br.com.mlp.compiler.ast;

/**
 * Representa um comando condicional:
 *   se (condicao) entao cmd;
 *   se (condicao) entao cmd senao cmd;
 */
public class IfNode extends CommandNode {

  private final ConditionNode condition;
  private final CommandNode thenCommand;
  private final CommandNode elseCommand; // pode ser null

  public IfNode(ConditionNode condition, CommandNode thenCommand, CommandNode elseCommand) {
      this.condition = condition;
      this.thenCommand = thenCommand;
      this.elseCommand = elseCommand;
  }

  public ConditionNode getCondition() {
      return condition;
  }

  public CommandNode getThenCommand() {
      return thenCommand;
  }

  public CommandNode getElseCommand() {
      return elseCommand;
  }

  @Override
  public String toString() {
      if (elseCommand == null) {
          return "If(" + condition + ", then=" + thenCommand + ")";
      }
      return "If(" + condition + ", then=" + thenCommand + ", else=" + elseCommand + ")";
  }
}
