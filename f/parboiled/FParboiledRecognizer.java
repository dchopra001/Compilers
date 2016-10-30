package ece351.f.parboiled;

import org.parboiled.Rule;

import ece351.util.CommandLine;
import ece351.vhdl.VConstants;

//Parboiled requires that this class not be final
public /*final*/ class FParboiledRecognizer extends FBase implements VConstants {

	
	public static void main(final String... args) {
		final CommandLine c = new CommandLine(args);
    	process(FParboiledRecognizer.class, c.readInputSpec());
    }

	@Override
	public Rule Program() {
		return Sequence(OneOrMore(Formula()),EOI); // TODO: replace this stub
		// For the grammar production Id, ensure that the Id does not match any of the keywords specified
		// in the rule, 'Keyword'
// TODO: 39 lines snipped
	}
	
	public Rule Formula(){
		return OneOrMore(Var(),"<= ",Expr(),"; "); 

	}
	
	public Rule Var(){
		
		return Sequence(TestNot(Keyword()),OneOrMore(Char()),W0());
		
	}
	
	public Rule Expr(){
		return Sequence(Term(),ZeroOrMore(OR(),Term()));
	}
	
	
	public Rule Term(){
		return Sequence(Factor(),ZeroOrMore(AND(),Factor()));
	}
	
	public Rule Factor(){
		return FirstOf(
				Sequence(NOT(),Factor()),
				Sequence("( ",Expr(),") "),
				Var(),
				Constant()
				);
		
	}
	
	public Rule Constant(){
		return FirstOf("'0' ","'1' ");
	}
	


}
