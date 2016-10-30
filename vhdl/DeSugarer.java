package ece351.vhdl;

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
import ece351.util.CommandLine.FSimplifierOptions;
import ece351.vhdl.ast.Architecture;
import ece351.vhdl.ast.DesignUnit;
import ece351.vhdl.ast.IfElseStatement;
import ece351.vhdl.ast.PostOrderVExprVisitor;
import ece351.vhdl.ast.Process;
import ece351.vhdl.ast.Statement;
import ece351.vhdl.ast.VProgram;

public final class DeSugarer extends PostOrderVExprVisitor {

	private final CommandLine c;
	
	public DeSugarer() {
		c = null;
	}
	
	public DeSugarer(final CommandLine c) {
		this.c = c;
	}
	
	public static void main(final String[] args) {
		System.out.println(desugar(args));
    }
	
	public static VProgram desugar(final String[] args) {
		return desugar(new CommandLine(args));
	}
	
	public static VProgram desugar(final CommandLine c) {
        final VProgram program = VParser.parse(c.readInputSpec());
        return desugar(program, c);
	}
	
	public static VProgram desugar(final VProgram program) {
		final DeSugarer d = new DeSugarer();
		return d.desugarit(program);
	}
	
	public static VProgram desugar(final VProgram program, final CommandLine c) {
		final DeSugarer d = new DeSugarer(c);
		return d.desugarit(program);
	}
	
	private Expr desugarExpr(final Expr e) {
		Expr desugared = traverse(e);
		
		if (c == null) return desugared;
		
		if (c.simplifierOpts.contains(FSimplifierOptions.STANDARDIZE)) {
			desugared = desugared.standardize();
    	}
        final Expr simplified = desugared.simplify(c.simplifierOpts);
        
        return simplified;
	}
	

	private Statement desugarit(final Statement stmt){
		Statement newStmt = null;
		if (stmt instanceof AssignmentStatement){
			AssignmentStatement assnStatement = ((AssignmentStatement) stmt);
			newStmt = desugarit(assnStatement);
		}else if(stmt instanceof IfElseStatement){
			IfElseStatement ifElseStatement = ((IfElseStatement) stmt);
			newStmt = desugarit(ifElseStatement);
		}else if(stmt instanceof Process){
			Process prcss = ((Process) stmt);
			newStmt = desugarit(prcss);
		}
		return newStmt;
	}
	
	private AssignmentStatement desugarit(final AssignmentStatement AssnStatement){
		Expr newExpression = desugarExpr(AssnStatement.expr);
		AssignmentStatement newstate = new AssignmentStatement(AssnStatement.outputVar,newExpression);
		return newstate;
	}
	
	private IfElseStatement desugarit(final IfElseStatement ifElseStmt){
		Expr newExpression = desugarExpr(ifElseStmt.condition);
		ImmutableList<AssignmentStatement> newElseBody = ImmutableList.of();
		for (AssignmentStatement stmtOn : ifElseStmt.elseBody){
			newElseBody = newElseBody.append(desugarit(stmtOn));
		}
		ImmutableList<AssignmentStatement> newIfBody = ImmutableList.of();
		for (AssignmentStatement stmtOn : ifElseStmt.ifBody){
			newIfBody = newIfBody.append(desugarit(stmtOn));
		}
		return new IfElseStatement(newElseBody,newIfBody,newExpression);
	}
	
	private Process desugarit(final Process prcss){
		ImmutableList<Statement> newStmts = ImmutableList.of();
		for ( Statement stmton : prcss.sequentialStatements){
			newStmts = newStmts.append(desugarit(stmton));
		}
		return new Process(newStmts,prcss.sensitivityList);
	}	

	
	
	private Architecture desugarit(final Architecture arch){
		String newArchName = arch.architectureName;
		ImmutableList<Statement> stmts = ImmutableList.of();
		for(Statement statementOn : arch.statements){
			stmts = stmts.append(desugarit(statementOn));
		}
		return new Architecture(stmts,arch.components,arch.signals, arch.entityName,newArchName);
	}
		
	private DesignUnit desugarit(final DesignUnit dunit){
		Architecture newArch = desugarit(dunit.arch);
		return new DesignUnit(newArch, dunit.entity);
		
		
	}

	private VProgram desugarit(final VProgram program) {

		ImmutableList<DesignUnit> dunitList = ImmutableList.of();
		for(DesignUnit dunit : program.designUnits){
			dunitList = dunitList.append(desugarit(dunit));
		}

		return new VProgram(dunitList);
	}
	

	@Override
	public Expr visit(final XOrExpr e) {

		Expr newLeft = new AndExpr(e.left,new NotExpr(e.right));
		Expr newRight = new AndExpr(new NotExpr(e.left),e.right);
		return new OrExpr(newLeft,newRight);

	}
	
	@Override
	public Expr visit(final NAndExpr e) {
		Expr newAnd = new AndExpr(e.left,e.right);
		
		return new NotExpr(newAnd);
	}
	
	@Override
	public Expr visit(final NOrExpr e) {
		Expr newOr = new OrExpr(e.left,e.right);
		
		return new NotExpr(newOr);
	}
	
	@Override
	public Expr visit(final XNOrExpr e) {
		Expr newLeft = new AndExpr(e.left,new NotExpr(e.right));
		Expr newRight = new AndExpr(new NotExpr(e.left),e.right);
		return new NotExpr(new OrExpr(newLeft,newRight));
	}

	@Override
	public Expr visit(final ConstantExpr e) {
		return e;
	}

	@Override
	public Expr visit(final VarExpr e) {
		return e;
	}

	@Override
	public Expr visit(final NotExpr e) {
		return e;
	}

	@Override
	public Expr visit(final AndExpr e) {
		return e;
	}

	@Override
	public Expr visit(final OrExpr e) {
		return e;
	}
	
	@Override
	public Expr visit(final EqualExpr e) {
		//equals operator has the same truth table as xnor
		return new NotExpr(new OrExpr(new AndExpr(e.left, new NotExpr(e.right)),
	  			new AndExpr(new NotExpr(e.left), e.right)));
	}
	
	@Override public Expr visit(NaryAndExpr e) { return e; }
	@Override public Expr visit(NaryOrExpr e) { return e; }
}
