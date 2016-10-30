package ece351.vhdl;

import org.parboiled.Rule;
import org.parboiled.annotations.MemoMismatches;

import ece351.util.BaseParser351;

abstract class VBase extends BaseParser351 implements VConstants {

    Rule Char() {
    	return FirstOf( CharRange('a', 'z'), CharRange('A', 'Z') );
    }
    
    Rule Digit() {
    	return CharRange('0', '9');
    }    

    //*************************************Keyword Rules****************************************** 
    // You can use these rules to match keywords in the VHDL grammar rules. Theses rules account
    // for VHDL's case-insensitivity and enforce whitespaces following the keywords more carefully
    // than just using the rule, WhiteSpace, or adding a space at the end of each keyword literal
    
    // Boolean Operators
    Rule NOT()  { return Sequence(IgnoreCase(NOT), Test(AnyOf("( \t\n\r\f")), W0()); }
    Rule AND()  { return Sequence(IgnoreCase(AND), Test(AnyOf("( \t\n\r\f")), W0()); }
    Rule OR()   { return Sequence(IgnoreCase(OR),  Test(AnyOf("( \t\n\r\f")), W0()); }
    Rule XOR()  { return Sequence(IgnoreCase(XOR), Test(AnyOf("( \t\n\r\f")), W0()); }
    Rule NAND() { return Sequence(IgnoreCase(NAND),Test(AnyOf("( \t\n\r\f")), W0()); }
    Rule NOR()  { return Sequence(IgnoreCase(NOR), Test(AnyOf("( \t\n\r\f")), W0()); }
    Rule XNOR() { return Sequence(IgnoreCase(XNOR),Test(AnyOf("( \t\n\r\f")), W0()); }
    
    // Constructs
    Rule IF()    { return Sequence(IgnoreCase("if"),   Test(AnyOf("( \t\n\r\f")), W0()); }
    Rule THEN()  { return Sequence(IgnoreCase("then"), Test(AnyOf(" \t\n\r\f")), W0()); }
    Rule ELSE()  { return Sequence(IgnoreCase("else"), Test(AnyOf(" \t\n\r\f")), W0()); }
    Rule ENDIF() { return Sequence(IgnoreCase("end"),  W(), IgnoreCase("if"), Test(AnyOf("; \t\n\r\f")), W0()); }
    
    Rule PROCESS()    { return Sequence(IgnoreCase("process"), W0()); }
    Rule ENDPROCESS() { return Sequence(IgnoreCase("end"), W(), IgnoreCase("process"), Test(AnyOf("; \t\n\r\f")), W0()); }
    
    // Misc
    Rule LIBRARY()		{ return Sequence(IgnoreCase("library"),      W()); }
    Rule USE()			{ return Sequence(IgnoreCase("use"),          W()); }
    Rule ENTITY()		{ return Sequence(IgnoreCase("entity"),Test(AnyOf("; \t\n\r\f")), W0()); }
    Rule PORT()			{ return Sequence(IgnoreCase("port"),  Test(AnyOf("( \t\n\r\f")), W0()); }
    Rule MAP()			{ return Sequence(IgnoreCase("map"),   Test(AnyOf("( \t\n\r\f")), W0()); }
    Rule ARCHITECTURE() { return Sequence(IgnoreCase("architecture"), W()); }
    
    Rule OF()			{ return Sequence(IgnoreCase("of"), W());}
    Rule IS()			{ return Sequence(IgnoreCase("is"), W());}
    Rule BEGIN()		{ return Sequence(IgnoreCase("begin"), W());}
    Rule END()			{ return Sequence(IgnoreCase("end"), Test(AnyOf("; \t\n\r\f")), W0()); }
    
    Rule SIGNAL()		{ return Sequence(IgnoreCase("signal"), W()); }
    Rule BIT()			{ return Sequence(IgnoreCase("bit"),    W0()); }
    Rule IN()			{ return Sequence(IgnoreCase("in"),  W()); }
    Rule OUT()			{ return Sequence(IgnoreCase("out"), W()); }
    
    
    @MemoMismatches
    Rule Keyword() {
    	return Sequence( FirstOf(
	    			IgnoreCase("and"),
	    			IgnoreCase("architecture"), 
	    			IgnoreCase("begin"),
	    			IgnoreCase("bit"),
	    			IgnoreCase("end"), 
	    			IgnoreCase("entity"),
	    			IgnoreCase("else"),
	    			IgnoreCase("exit"),
	    			IgnoreCase("if"),
	    			IgnoreCase("in"),
	    			IgnoreCase("is"),
	    			IgnoreCase("library"),
	    			IgnoreCase("map"),
	    			IgnoreCase("nand"),
	    			IgnoreCase("nor"),
	    			IgnoreCase("not"),
	    			IgnoreCase("of"),
	    			IgnoreCase("or"),
	    			IgnoreCase("out"),
	    			IgnoreCase("port"),
	    			IgnoreCase("process"),
	    			IgnoreCase("signal"),
    				IgnoreCase("then"), 
    				IgnoreCase("use"),
    				IgnoreCase("xnor"), 
    		    	IgnoreCase("xor")
    		    	),
    		    	TestNot(FirstOf(Char(),Digit(), "_"))
    			);
    }
    
    public Rule W() {
    	return OneOrMore(AnyOf(" \t\f\r\n"));
    }

}
