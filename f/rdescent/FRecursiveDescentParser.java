package ece351.f.rdescent;
import org.parboiled.common.ImmutableList;

import ece351.common.ast.AndExpr;
import ece351.common.ast.AssignmentStatement;
import ece351.common.ast.ConstantExpr;
import ece351.common.ast.Expr;
import ece351.common.ast.NotExpr;
import ece351.common.ast.OrExpr;
import ece351.common.ast.VarExpr;
import ece351.f.ast.FProgram;
import ece351.util.CommandLine;
import ece351.util.Lexer;
import ece351.vhdl.VConstants;



public final class FRecursiveDescentParser implements VConstants {
   
	// instance variables
	private final Lexer lexer;

    public FRecursiveDescentParser(String... args) {
    	final CommandLine c = new CommandLine(args);
        lexer = new Lexer(c.readInputSpec());
    }
    
    public FRecursiveDescentParser(final Lexer lexer) {
        this.lexer = lexer;
    }

    public static void main(final String arg) {
    	main(new String[]{arg});
    }
    
    public static void main(final String[] args) {
    	parse(args);
    }

    public static FProgram parse(final String... args) {
        final FRecursiveDescentParser p = new FRecursiveDescentParser(args);
        return p.parse();
    }
    
    public FProgram parse() {
        return program();
    }

    FProgram program() {
        ImmutableList<AssignmentStatement> formulas = ImmutableList.of();
        while (!lexer.inspectEOF()) {
        	formulas = formulas.append(formula());
        }
        lexer.consumeEOF();
        return new FProgram(formulas);
    }

    AssignmentStatement formula() {
        final VarExpr var = var();
        lexer.consume("<=");
        final Expr expr = expr();
        lexer.consume(";");
        return new AssignmentStatement(var, expr);
    }
    
    VarExpr var() {
    	VarExpr newVar = new VarExpr(lexer.consumeID());
    	return newVar;
    }
    
    Expr term() {
    	AndExpr and = null;
    	Expr left = factor();
    	while (lexer.inspect("and")){
    		lexer.consume("and");
    		and = new AndExpr(left,factor());
    		left = and;
    	}
    	if (and != null){
    		return and;
    	}else{
    		return left;
    	}
    }
    
    Expr factor(){
    	if (lexer.inspect("not")){
    		lexer.consume("not");
    		NotExpr not = new NotExpr(factor());
    		return not;
    	} else if(lexer.inspect("(")){
    		lexer.consume("(");
    		Expr ex = expr();
    		lexer.consume(")");
    		return ex;
    	} else if(peekConstant()){
    		lexer.consume("'");
    		ConstantExpr newConstant = null;
    		if( lexer.inspect("0"))
    		{
    			newConstant = ConstantExpr.FalseExpr;
    		}else if (lexer.inspect("1")){
    			newConstant = ConstantExpr.TrueExpr;
    		}
    		lexer.consume("0","1");   		
    		lexer.consume("'");
    		return newConstant;
    	}else{
    		return var();
    	}
    }
    
    Expr expr() { 
    	OrExpr or = null;
    	Expr left = term();
    	while (lexer.inspect("or")){
    		lexer.consume("or");
    		or = new OrExpr(left,term());
    		left = or;
    	}
    	if (or != null){
    		return or;
    	}else{
    		return left;
    	}
    	
    }

    
// TODO: 51 lines snipped

    // helper functions
    private boolean peekConstant() {
        return lexer.inspect("'");
    }

}

