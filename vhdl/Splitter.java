package ece351.vhdl;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

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
import ece351.vhdl.ast.Architecture;
import ece351.vhdl.ast.DesignUnit;
import ece351.vhdl.ast.IfElseStatement;
import ece351.vhdl.ast.PostOrderVExprVisitor;
import ece351.vhdl.ast.Process;
import ece351.vhdl.ast.Statement;
import ece351.vhdl.ast.VProgram;

/**
 * Process splitter.
 */
public final class Splitter extends PostOrderVExprVisitor {
	private final Set<String> usedVarsInExpr = new LinkedHashSet<String>();

	public static void main(String[] args) {
		System.out.println(split(args));
	}
	
	public static VProgram split(final String[] args) {
		return split(new CommandLine(args));
	}
	
	public static VProgram split(final CommandLine c) {
		final VProgram program = DeSugarer.desugar(c);
        return split(program);
	}
	
	public static VProgram split(final VProgram program) {
		VProgram p = Elaborator.elaborate(program);
		final Splitter s = new Splitter();
		return s.splitit(p);
	}

	private VProgram splitit(final VProgram program) {
		ImmutableList<DesignUnit> modifiedUnits = ImmutableList.of();
		for (DesignUnit dunit : program.designUnits){
			Architecture modifiedArchitecture = null;
			ImmutableList<Statement> modifiedStatements = ImmutableList.of();
			for (Statement statementOn : dunit.arch.statements){
				if (statementOn instanceof Process){
					Process processOn = (Process)statementOn;
					// Determine if the process needs to be split into multiple processes
					for (Statement processStmtOn : processOn.sequentialStatements){					if (processStmtOn instanceof IfElseStatement){
						IfElseStatement ifstmtOn = (IfElseStatement)processStmtOn;
							// We need to split the process
							// start by making two new if and new else clauses
							Iterator<AssignmentStatement> ifBodyIter = ifstmtOn.ifBody.iterator();
							while (ifBodyIter.hasNext()){
								this.usedVarsInExpr.clear();
								ImmutableList<AssignmentStatement> newifList = ImmutableList.of();
								ImmutableList<AssignmentStatement> newelseList = ImmutableList.of();
								AssignmentStatement ifBody = ifBodyIter.next();
								newifList = newifList.append(ifBody);
								// now find the signal in the else clause
								Iterator<AssignmentStatement> elseBodyIter = ifstmtOn.elseBody.iterator();
								while (elseBodyIter.hasNext()){
									AssignmentStatement elseBody = elseBodyIter.next();
									if (ifBody.outputVar.equals(elseBody.outputVar)){
										newelseList = newelseList.append(elseBody);
										break;
									}
								}
								ImmutableList<Statement> newProcessStmts = ImmutableList.of();
								traverse(ifstmtOn.condition);
								IfElseStatement newIfStmt = new IfElseStatement(newelseList, newifList, ifstmtOn.condition);
								newProcessStmts = newProcessStmts.append(newIfStmt);
								// determine new sensitivity list
								for(AssignmentStatement ifstmts : newIfStmt.ifBody){
									traverse(ifstmts.expr);
								}
								for(AssignmentStatement elsestmts : newIfStmt.elseBody){
									traverse(elsestmts.expr);
								}
								ImmutableList<String> sensList = ImmutableList.of();
								for (String signal : this.usedVarsInExpr){
									sensList = sensList.append(signal);
								}
								Process newSplitProcess = new Process(newProcessStmts, sensList);
								modifiedStatements = modifiedStatements.append(newSplitProcess);					
							}
						}else {
							modifiedStatements = modifiedStatements.append(processOn);
							break;
						}
					}
				}else{
					modifiedStatements = modifiedStatements.append(statementOn);
				}
				modifiedArchitecture = new Architecture(modifiedStatements, dunit.arch.components, dunit.arch.signals, dunit.arch.entityName, dunit.arch.architectureName);
				
			}
			modifiedUnits = modifiedUnits.append(new DesignUnit(modifiedArchitecture, dunit.entity));
		}
		return new VProgram(modifiedUnits);
	}

	@Override
	public Expr visit(ConstantExpr e) {
		return e;
	}

	@Override
	public Expr visit(VarExpr e) {
		usedVarsInExpr.add(e.identifier);
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
	public Expr visit(EqualExpr e) {
		return e;
	}
	
	@Override public Expr visit(NaryAndExpr e) { return e; }
	@Override public Expr visit(NaryOrExpr e) { return e; }

}
