package com.steepgraph.ta.framework.common;

import java.util.Iterator;
import java.util.Stack;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.fathzer.soft.javaluator.AbstractEvaluator;
import com.fathzer.soft.javaluator.BracketPair;
import com.fathzer.soft.javaluator.Operator;
import com.fathzer.soft.javaluator.Parameters;
import com.steepgraph.ta.framework.Constants;
import com.steepgraph.ta.framework.common.pages.Driver;
import com.steepgraph.ta.framework.utils.pages.LoggerUtil;

public class ExpressionEvaluator extends AbstractEvaluator<String> {

	/** The negate unary operator. */
	public final static Operator NEGATE = new Operator("NOT", 1, Operator.Associativity.RIGHT, 5);

	/** The logical AND operator. */
	private static final Operator AND = new Operator("AND", 2, Operator.Associativity.LEFT, 4);

	/** The logical OR operator. */
	public final static Operator OR = new Operator("OR", 2, Operator.Associativity.LEFT, 3);

	public final static Operator EQ = new Operator("=", 2, Operator.Associativity.LEFT, 2);

	public final static Operator GT = new Operator(">", 2, Operator.Associativity.LEFT, 1);

	public final static Operator LT = new Operator("<", 2, Operator.Associativity.LEFT, 1);

	private static final Parameters PARAMETERS;

	public boolean isIndentedTableExpression = false;

	public WebElement wbElement;

	public Driver driver;

	public String rowId;

	static {
		// Create the evaluator's parameters
		PARAMETERS = new Parameters();
		// Add the supported operators
		PARAMETERS.add(AND);
		PARAMETERS.add(OR);
		PARAMETERS.add(EQ);
		PARAMETERS.add(GT);
		PARAMETERS.add(LT);
		// Add the parentheses
		PARAMETERS.addExpressionBracket(BracketPair.PARENTHESES);
	}

	public ExpressionEvaluator() {
		super(PARAMETERS);
	}

	/**
	 * Return actual value of literal in the expression.
	 * 
	 * @author Steepgraph Systems
	 * @param literal
	 * @return evaluationContext
	 * @throws Exception
	 */
	@Override
	protected String toValue(String literal, Object evaluationContext) {

		try {

			if (isIndentedTableExpression) {
				if (literal.startsWith("cell")) {

					String literalTmp = literal.replaceAll("cell[\\[]", "");

					String strCellPosition = literalTmp.substring(0, literalTmp.indexOf("]"));
					if (strCellPosition == null || "".equals(strCellPosition))
						throw new Exception("expression written in tag is not valid.");

					int position = Integer.parseInt(strCellPosition);

					LoggerUtil.debug("position : " + position);

					String strTableID = "bodyTable";
					if (position == 1)
						strTableID = "treeBodyTable";

					WebElement weTableRowColumn = driver.findElement(By.xpath("//table[@id='" + strTableID
							+ "']/tbody/tr[@id='" + rowId + "']/td[@position='" + position + "']"));

					LoggerUtil.debug("weTableRowColumn : " + weTableRowColumn);

					String strCellValue = weTableRowColumn.getText();
					if (strCellValue != null || !"".equals(strCellValue))
						strCellValue = strCellValue.trim();

					strCellValue = strCellValue.replace("(", Constants.ESACE_OPEN_SQ_BRACKET).replace(")",
							Constants.ESACE_CLOSED_SQ_BRACKET);

					LoggerUtil.debug("literal : " + literal + " => " + strCellValue);

					return strCellValue;

				}
			} else if (literal.equals("text")) {
				String elementText = wbElement.getText();
				if (elementText != null && !"".equals(elementText))
					elementText = elementText.trim();

				elementText = elementText.replace("(", Constants.ESACE_OPEN_SQ_BRACKET).replace(")",
						Constants.ESACE_CLOSED_SQ_BRACKET);

				LoggerUtil.debug("literal : " + literal + " => " + elementText);
				return elementText;
			}

			if (literal.startsWith("\"") || literal.startsWith("'")) {

				String returnResult = literal.substring(1, literal.length() - 1);
				LoggerUtil.debug("literal : " + literal + " => " + returnResult);
				return returnResult;
			}

			LoggerUtil.debug("literal : " + literal + " => " + literal);
			return literal;
		} catch (Exception e) {
			// TODO: handle exception
			return "EXCEPTION : " + e.getMessage();
		}
	}

	/**
	 * Evaluate given expression.
	 * 
	 * @author Steepgraph Systems
	 * @param operator
	 * @return operands
	 * @throws evaluationContext
	 */
	@Override
	protected String evaluate(Operator operator, Iterator<String> operands, Object evaluationContext) {

		try {

			String opSymbol = operator.getSymbol();
			LoggerUtil.debug("evaluate : operator " + opSymbol);
			if (operator == NEGATE) {
				return String.valueOf(!Boolean.valueOf(operands.next()));
			} else if (operator == OR) {
				Boolean o1 = Boolean.valueOf(operands.next());
				Boolean o2 = Boolean.valueOf(operands.next());
				LoggerUtil.debug(o1 + " " + opSymbol + " " + o2);
				return String.valueOf(o1 || o2);
			} else if (operator == AND) {
				Boolean o1 = Boolean.valueOf(operands.next());
				Boolean o2 = Boolean.valueOf(operands.next());
				LoggerUtil.debug(o1 + " " + opSymbol + " " + o2);
				return String.valueOf(o1 && o2);
			} else if (operator == EQ) {
				String o1 = operands.next();
				String o2 = operands.next();
				LoggerUtil.debug(o1 + " " + opSymbol + " " + o2);
				return String.valueOf(o1.equals(o2));
			} else if (operator == GT) {
				double dInput1 = Double.parseDouble(operands.next());
				double dInput2 = Double.parseDouble(operands.next());
				LoggerUtil.debug(dInput1 + " " + opSymbol + " " + dInput2);
				int retValue = Double.compare(dInput1, dInput2);
				return String.valueOf(retValue > 0);
			} else if (operator == LT) {
				double dInput1 = Double.parseDouble(operands.next());
				double dInput2 = Double.parseDouble(operands.next());
				int retValue = Double.compare(dInput1, dInput2);
				LoggerUtil.debug(dInput1 + " " + opSymbol + " " + dInput2);
				return String.valueOf(retValue < 0);
			} else {
				return super.evaluate(operator, operands, evaluationContext);
			}
		} catch (Exception e) {
			// TODO: handle exception
			return "EXCEPTION : " + e.getMessage();
		}
	}

	public boolean validateExpression(String s) {
		Stack<Character> stack = new Stack<Character>();
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (c == '(' || c == '[') {
				stack.push(c);
			} else if (c == ')') {
				if (stack.isEmpty() || stack.pop() != '(') {
					return false;
				}
			} else if (c == ']') {
				if (stack.isEmpty() || stack.pop() != ']') {
					return false;
				}
			}

		}
		return stack.isEmpty();
	}

	/**
	 * Evaluate given expression using web element
	 * 
	 * @author Steepgraph Systems
	 * @param driver
	 * @return wbElement
	 * @param expression
	 * @throws evaluationContext
	 */
	public boolean evaluateExpression(Driver driver, WebElement wbElement, String expression) throws Exception {
		this.wbElement = wbElement;
		this.driver = driver;

		expression = expression.replace("\\(", Constants.ESACE_OPEN_SQ_BRACKET).replace("\\)",
				Constants.ESACE_CLOSED_SQ_BRACKET);

		String result = evaluate(expression);
		if (result.startsWith("EXCEPTION : "))
			throw new Exception(result);

		return Boolean.valueOf(result);

	}

	/**
	 * Evaluate given expression using indented table row id.
	 * 
	 * @author Steepgraph Systems
	 * @param driver
	 * @return rowId
	 * @param expression
	 * @throws evaluationContext
	 */
	public boolean evaluateExpression(Driver driver, String rowId, String expression) throws Exception {
		this.rowId = rowId;
		this.driver = driver;
		isIndentedTableExpression = true;

		expression = expression.replace("\\(", Constants.ESACE_OPEN_SQ_BRACKET).replace("\\)",
				Constants.ESACE_CLOSED_SQ_BRACKET);
		String result = evaluate(expression);
		if (result.startsWith("EXCEPTION : "))
			throw new Exception(result);

		return Boolean.valueOf(result);

	}
}