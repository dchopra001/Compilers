package ece351.vhdl;

import java.util.LinkedHashSet;
import java.util.Set;

import org.parboiled.common.FileUtils;
import org.parboiled.common.ImmutableList;

import ece351.common.ast.AndExpr;
import ece351.common.ast.AssignmentStatement;
import ece351.common.ast.ConstantExpr;
import ece351.common.ast.EqualExpr;
import ece351.common.ast.Expr;
import ece351.common.ast.NAndExpr;
import ece351.common.ast.NOrExpr;
import ece351.common.ast.NaryAndExpr;
import ece351.common.ast.NaryOrExpr;
import ece351.common.ast.NotExpr;
import ece351.common.ast.OrExpr;
import ece351.common.ast.VarExpr;
import ece351.common.ast.XNOrExpr;
import ece351.common.ast.XOrExpr;
import ece351.util.CommandLine;
import ece351.vhdl.ast.Component;
import ece351.vhdl.ast.DesignUnit;
import ece351.vhdl.ast.IfElseStatement;
import ece351.vhdl.ast.PostOrderVExprVisitor;
import ece351.vhdl.ast.Process;
import ece351.vhdl.ast.Statement;
import ece351.vhdl.ast.VProgram;

/**
 * Checks that variables are defined before they are used.
 */
public final class DefBeforeUseChecker extends PostOrderVExprVisitor {
	
	private final Set<String> outputPins = new LinkedHashSet<String>();
	private final Set<String> inputPins = new LinkedHashSet<String>();
	private final Set<String> signals = new LinkedHashSet<String>();
	private final Set<String> varsInExpr = new LinkedHashSet<String>();
	
	private DefBeforeUseChecker() {
		super();
	}
	
	public static void main(final String arg) {
		main(new String[]{arg});
	}

	/**
	 * Throws a RuntimeException if there's a problem.
	 * Otherwise returns silently.
	 * @param args
	 */
	public static void main(final String[] args) {
		checkUse(VParser.parse(FileUtils.readAllText(args[0])));
	}
	
	public static void checkUse(final String[] args) {
		checkUse(new CommandLine(args));
	}
	
	public static void checkUse(final CommandLine c) {
		checkUse(VParser.parse(c.readInputSpec()));
	}
	
	public static void checkUse(final VProgram program) {
		final DefBeforeUseChecker useChecker = new DefBeforeUseChecker();
		useChecker.check(program);
	}
	
	private void check(final VProgram program) {
		// TODO: 
		// For each design unit, update the sets that will be used to determine if variables are used before they are defined
		// Upon the first occurrence of the use of an undefined variable, throw an exception
			// for each statement in the architecture, perform checks on instances of AssignmentStatement
		// TODO: Check all signals in sensitivity list in process, p
		// TODO: Check all assignment statements in the process, p
		// TODO: check all variables used in expression
		// Obtain all variables used in expression
		// Check to see if all variables that are used in the expression are defined
		// TODO: check all variables used in the assignment statement
		// Ensure that the output variable is not an input pin
		// Check the right hand side of the statement
// TODO: 72 lines snipped
	}
	
	private void updateDefinedVars(DesignUnit d) {
		this.inputPins.clear();
		this.outputPins.clear();
		this.signals.clear();
		for(String pin : d.entity.input) {
			this.inputPins.add(pin);
		}
		for(String pin : d.entity.output) {
			this.outputPins.add(pin);
		}
		for(String signal : d.arch.signals) {
			this.signals.add(signal);
		}
	}

	@Override
	public Expr visit(EqualExpr equalVExpr) {
		return equalVExpr;
	}

	@Override
	public Expr visit(VarExpr e) {
// TODO: 1 lines snipped
		return e;
	}

	@Override
	public Expr visit(NotExpr e) {
		return e;
	}

	@Override
	public Expr visit(AndExpr e) {
		return e;
	}

	@Override
	public Expr visit(OrExpr e) {
		return e;
	}

	@Override
	public Expr visit(XOrExpr e) {
		return e;
	}

	@Override
	public Expr visit(NAndExpr e) {
		return e;
	}

	@Override
	public Expr visit(NOrExpr e) {
		return e;
	}

	@Override
	public Expr visit(XNOrExpr e) {
		return e;
	}

	@Override
	public Expr visit(ConstantExpr constantVExpr) {
		return constantVExpr;
	}
	
	@Override public Expr visit(NaryAndExpr e) { return e; }
	@Override public Expr visit(NaryOrExpr e) { return e; }
}
