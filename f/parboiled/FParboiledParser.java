package ece351.f.parboiled;

import java.util.List;

import org.parboiled.Context;
import org.parboiled.Rule;
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
import ece351.vhdl.VConstants;

// Parboiled requires that this class not be final
public /*final*/ class FParboiledParser extends FBase implements VConstants {
	
	public static void main(final String[] args) {
    	final CommandLine c = new CommandLine(args);
    	final String input = c.readInputSpec();
    	final FProgram fprogram = parse(input);
    	final String output = fprogram.toString();
    	
    	// if we strip spaces and parens input and output should be the same
    	if (strip(input).equals(strip(output))) {
    		// success: return quietly
    		return;
    	} else {
    		// failure: make a noise
    		System.err.println("parsed value not equal to input:");
    		System.err.println("    " + strip(input));
    		System.err.println("    " + strip(output));
    		System.exit(1);
    	}
    }
	
	private static String strip(final String s) {
		return s.replaceAll("\\s", "").replaceAll("\\(", "").replaceAll("\\)", "");
	}
	
	public static FProgram parse(final String inputText) {
		return (FProgram) process(FParboiledParser.class, inputText).resultValue;
	}

	@Override
	public Rule Program() {
		return Sequence(
				// push empty list on stack
				push(ImmutableList.of()),
				OneOrMore(Formula()),
				EOI,
				// expect list on top of stack
				checkType(peek(),List.class),
				push(new FProgram((ImmutableList<AssignmentStatement>)pop())));
	}
	
	public Rule Formula(){
		return OneOrMore(
					Var(),
					"<= ",
					Expr(),
					"; ",
					debugStack(),
					swap(),
					debugStack(),
					push(new AssignmentStatement(((VarExpr)pop()).identifier,(Expr)pop())),
					debugStack(),
					swap(),
					debugStack(),
					push(((ImmutableList<AssignmentStatement>)pop()).append((AssignmentStatement)pop())),
					debugStack()
				);

	}
	
	
	public Rule Var(){
		
		return Sequence(
				TestNot(Keyword()),
				Sequence(Char(),ZeroOrMore(FirstOf(Char(),Digit(),Ch('_')))),
				push(new VarExpr(match())),
				W0(),
				debug(getContext())
				//debugmsg(peek())
				);
	
	}
	
	public Rule Expr(){
		return Sequence(
				Term(),
				//debug(getContext()),
				//debugmsg(peek()),
				ZeroOrMore(
						OR(),
						Term(),
						//debugmsg(peek()),
						swap(),
						push(new OrExpr((Expr)pop(),(Expr)pop())))
						//debug(getContext())
				);
	}
	
	public Rule Term(){
		return Sequence(
				Factor(),
				//debugmsg(peek()),
				ZeroOrMore(
						AND(),
						Factor(),
						//debugmsg(peek()),
						swap(),
						push(new AndExpr((Expr)pop(),(Expr)pop())))
						//debug(getContext())
				);
	}
	
	public Rule Factor(){
		return FirstOf(
				Sequence(
							NOT(),
							Factor(),
							push(new NotExpr((Expr)pop()))),
				Sequence("( ",Expr(),") "),
				Var(),
				Constant()
				);
		
	}
	
	public Rule Constant(){
		return Sequence(
				FirstOf("'0' ","'1' "),
				match().contains("0") ? push(ConstantExpr.FalseExpr) : push(ConstantExpr.TrueExpr)
				//debug(getContext())
				);
	}
	
	boolean debug(Context context){
		return true;
	}
	

	
}
