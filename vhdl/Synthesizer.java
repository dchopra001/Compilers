package ece351.vhdl;

import java.util.Iterator;

import ece351.common.ast.AndExpr;
import ece351.common.ast.AssignmentStatement;
import ece351.common.ast.BinaryExpr;
import ece351.common.ast.ConstantExpr;
import ece351.common.ast.EqualExpr;
import ece351.common.ast.Expr;
import ece351.common.ast.NAndExpr;
import ece351.common.ast.NOrExpr;
import ece351.common.ast.NaryAndExpr;
import ece351.common.ast.NaryExpr;
import ece351.common.ast.NaryOrExpr;
import ece351.common.ast.NotExpr;
import ece351.common.ast.OrExpr;
import ece351.common.ast.UnaryExpr;
import ece351.common.ast.VarExpr;
import ece351.common.ast.XNOrExpr;
import ece351.common.ast.XOrExpr;
import ece351.util.CommandLine;
import ece351.vhdl.ast.DesignUnit;
import ece351.vhdl.ast.IfElseStatement;
import ece351.vhdl.ast.Statement;
import ece351.vhdl.ast.VExprVisitor;
import ece351.vhdl.ast.VProgram;
import ece351.vhdl.ast.Process;

/**
 * Translates VHDL to F.
 */
public final class Synthesizer extends VExprVisitor {
	private String varPrefix;
	private StringBuilder fprogram;
	private int condCount;
	private static String conditionPrefix = "condition";
	
	public static void main(String[] args) { 
		System.out.println(synthesize(args));
	}
	
	public static String synthesize(final String[] args) {
		return synthesize(new CommandLine(args));
	}
	
	public static String synthesize(final CommandLine c) {
        final VProgram program = DeSugarer.desugar(c);
        return synthesize(program);
	}
	
	public static String synthesize(final VProgram program) {
		VProgram p = Splitter.split(program);
		final Synthesizer synth = new Synthesizer();
		return synth.synthesizeit(p);
	}
	
	public Synthesizer(){
		fprogram = new StringBuilder();
		condCount = 0;
	}
	
	private void synthesizeit(Statement stmtOn){
		if (stmtOn instanceof Process){
			Process processOn = (Process)stmtOn;
			for (Statement processStmt : processOn.sequentialStatements){
				synthesizeit(processStmt);
			}
			
		}else if (stmtOn instanceof AssignmentStatement){
			AssignmentStatement stmt = (AssignmentStatement)stmtOn;
			this.outputFormula(stmt.outputVar.identifier, stmt.expr);
			
		}else if (stmtOn instanceof IfElseStatement){
			outputImplicationFormula((IfElseStatement)stmtOn);
			
		}
				
	}
		
	private String synthesizeit(VProgram root) {
		
		for (DesignUnit dunit : root.designUnits){
			for(Statement statementOn : dunit.arch.statements){
				this.varPrefix = dunit.arch.entityName;
				synthesizeit(statementOn);
			}
		}
		return this.fprogram.toString();
// TODO: 18 lines snipped
	}
	
	private void outputImplicationFormula(IfElseStatement statement) {
		condCount++;
		this.outputFormula(Synthesizer.conditionPrefix + this.condCount, statement.condition);
		VarExpr cond = new VarExpr(Synthesizer.conditionPrefix + this.condCount);
		NotExpr notCond = new NotExpr(cond);
		VarExpr outputVar = statement.ifBody.get(0).outputVar;
		AndExpr innerAnd = new AndExpr(notCond,statement.elseBody.get(0).expr);
		AndExpr outerAnd = new AndExpr(statement.ifBody.get(0).expr,cond);	
		OrExpr Or = new OrExpr(innerAnd,outerAnd);
		outputFormula(outputVar.identifier,(Expr)Or);
		return;
// TODO: 13 lines snipped
	}
	
	private void outputFormula(String outputVar, Expr vexpr) {
		if (outputVar.startsWith(Synthesizer.conditionPrefix)){
			fprogram.append(outputVar + " <= ");

		}else{
			fprogram.append(this.varPrefix + outputVar + " <= ");
		}
		traverse(vexpr);
		fprogram.append(";\n");
		
		return;
// TODO: 4 lines snipped
	}

	// traverse VExpr ASTs in infix order
	@Override
	public Expr traverse(final BinaryExpr e) {
		fprogram.append("( ");
		traverse(e.left);
		e.accept(this);
		traverse(e.right);
		fprogram.append(" )");
		return e;
	}

	@Override
	public Expr traverse(final UnaryExpr e) {
		fprogram.append("( ");
		e.accept(this);
		traverse(e.expr);
		fprogram.append(" )");
		return e;
	}
	
	@Override
	public Expr traverse(final NaryExpr e) {
		fprogram.append("( ");
		e.accept(this);
		fprogram.append(" )");
		return e;
	}

	@Override
	public Expr visit(ConstantExpr e) {
		fprogram.append(e.toString());
// TODO: 5 lines snipped
		return e;
	}

	@Override
	public Expr visit(VarExpr e) {
		if (e.identifier.startsWith(Synthesizer.conditionPrefix)){
			fprogram.append(e.identifier);
		}else{
			fprogram.append(this.varPrefix + e.identifier);
		}
		
		return e;
	}

	@Override
	public Expr visit(NotExpr e) {
		fprogram.append(VConstants.NOT + " ");
		return e;
	}
	
	@Override public Expr visit(NaryAndExpr e) {
// TODO: 9 lines snipped
		
		Iterator<Expr> childIter = e.children.iterator();
		while(childIter.hasNext()){
			Expr child = childIter.next();
			traverse(child);
			if (childIter.hasNext()){
				fprogram.append(" " + VConstants.AND + " ");
			}
		}
		
		return e;
	}
	
	@Override public Expr visit(NaryOrExpr e) {
// TODO: 9 lines snipped
		Iterator<Expr> childIter = e.children.iterator();
		while(childIter.hasNext()){
			Expr child = childIter.next();
			traverse(child);
			if (childIter.hasNext()){
				fprogram.append(" " + VConstants.OR + " ");
			}
		}
		
		return e;
	}

	@Override
	public Expr visit(AndExpr e) {	
		fprogram.append(" " + VConstants.AND + " ");
		return e;
	}

	@Override
	public Expr visit(OrExpr e) {
		fprogram.append(" " + VConstants.OR + " ");
		return e;
	}
	
	@Override public Expr visit(XOrExpr e) { return e; } 
	@Override public Expr visit(EqualExpr e) { return e; }
	@Override public Expr visit(NAndExpr e) { return e; }
	@Override public Expr visit(NOrExpr e) { return e; }
	@Override public Expr visit(XNOrExpr e) { return e; }
	
}



