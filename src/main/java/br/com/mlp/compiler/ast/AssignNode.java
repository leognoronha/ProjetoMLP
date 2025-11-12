package br.com.mlp.compiler.ast;

/**
 * Representa uma atribuição, ex:
 *   a = b + 1;
 */
public class AssignNode extends CommandNode {

  private final String varName;
  private final ExpressionNode expression;

  public AssignNode(String varName, ExpressionNode expression) {
      this.varName = varName;
      this.expression = expression;
  }

  public String getVarName() {
      return varName;
  }

  public ExpressionNode getExpression() {
      return expression;
  }

  @Override
  public String toString() {
      return "Assign(" + varName + " = " + expression + ")";
  }
}
