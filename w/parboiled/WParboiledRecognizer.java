package ece351.w.parboiled;
import org.parboiled.Rule;
import org.parboiled.annotations.BuildParseTree;
import org.parboiled.common.FileUtils;

import ece351.util.BaseParser351;
import ece351.w.ast.WProgram;


@BuildParseTree
//Parboiled requires that this class not be final
public /*final*/ class WParboiledRecognizer extends BaseParser351 {

	/**
	 * Run this recognizer, exit with error code 1 to reject.
	 * This method is called by wave/Makefile.
	 * @param args args[0] is the name of the input file to read
	 */
	public static void main(final String[] args) {
    	process(WParboiledRecognizer.class, FileUtils.readAllText(args[0]));
    }
	
	public static void recognize(final String inputText) {
		process(WParboiledRecognizer.class, inputText);
	}

	
	/** 
	 * Use this method to print the parse tree for debugging.
	 * @param w the text of the W program to recognize
	 */
	public static void printParseTree(final String w) {
		printParseTree(WParboiledRecognizer.class, w);
	}

	/**
	 * By convention we name the top production in the grammar "Program".
	 */
	@Override
	public Rule Program() {
		return Sequence(OneOrMore(Waveform()),W0(),EOI); 
	}
    
	/**
	 * Each line of the input W file represents a "pin" in the circuit.
	 */
    public Rule Waveform() {
		return Sequence(Name(),W0(),':',W0(),BitString(),W0(),';',W0()); // TODO: replace this stub
    }

    /**
     * The first token in each statement is the name of the waveform 
     * that statement represents.
     */
    public Rule Name() {
		return ZeroOrMore(Letter()); // TODO: replace this stub
    }

    /**
     * A Name is composed of a sequence of Letters. 
     * Recall that PEGs incorporate lexing into the parser.
     */
    public Rule Letter() {
		return FirstOf(CharRange('a','z'),CharRange('A','Z')); // TODO: replace this stub
    }

    /**
     * A BitString is the sequence of values for a pin.
     */
    public Rule BitString() {

		return Sequence(Bit(),ZeroOrMore(Sequence(W1(),Bit())));
    }
    
    /**
     * A BitString is composed of a sequence of Bits. 
     * Recall that PEGs incorporate lexing into the parser.
     */
    public Rule Bit() {       
		return AnyOf("01");
    }

}

