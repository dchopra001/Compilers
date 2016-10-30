package ece351.w.parboiled;
import java.io.File;

import org.parboiled.Rule;
import org.parboiled.annotations.BuildParseTree;
import org.parboiled.common.FileUtils;
import org.parboiled.common.ImmutableList;

import ece351.util.BaseParser351;
import ece351.w.ast.WProgram;
import ece351.w.ast.Waveform;

@BuildParseTree
//Parboiled requires that this class not be final
public /*final*/ class WParboiledParser extends BaseParser351 {

	/**
	 * Run this parser, exit with error code 1 for malformed input.
	 * Called by wave/Makefile.
	 * @param args
	 */
	public static void main(final String[] args) {
    	process(WParboiledParser.class, FileUtils.readAllText(args[0]));
    }

	/**
	 * Construct an AST for a W program. Use this for debugging.
	 */
	public static WProgram parse(final String inputText) {
		return (WProgram) process(WParboiledParser.class, inputText).resultValue;
	}

	/**
	 * By convention we name the top production in the grammar "Program".
	 */
	@Override
    public Rule Program() {
        		// push empty ImmutableList, which will grow to hold the waveform objects
		return Sequence(
				push(ImmutableList.of()),
				OneOrMore(Waveform()),
				push(new WProgram((ImmutableList<Waveform>)pop())),
				W0(),
				EOI);
    }

	/**
	 * Each line of the input W file represents a "pin" in the circuit.
	 */
    public Rule Waveform() {
    	return Sequence(
    			Name(),
    			swap(),
    			W0(),
    			':',
    			W0(),
    			BitString(),
    			swap(),
    			push(((ImmutableList<Waveform>)pop()).append(new Waveform((ImmutableList)pop(),(String)pop()))),
    			W0(),
    			';',
    			W0());

    			 // peek() == pin name
    			// peek() == immutable list of bits
    			// push the new Waveform on the stack
    }
    
    Rule Digit() {
    	return CharRange('0', '9');
	}
    /**
     * The first token on each line is the name of the pin that line represents.
     */
    public Rule Name() {
// TODO: 4 lines snipped
    	return Sequence(
    			Sequence(Letter(),ZeroOrMore(FirstOf(Letter(),Digit(),Ch('_')))),
    			push(match()));
    }
    
    /**
     * A Name is composed of a sequence of Letters. 
     * Recall that PEGs incorporate lexing into the parser.
     */
    public Rule Letter() {
    	return FirstOf(CharRange('a','z'),CharRange('A','Z'));
    }

    /**
     * A BitString is the sequence of values for a pin.
     */
    public Rule BitString() {
// TODO: 4 lines snipped
    	return Sequence(
    			// push bitlist on stack
    			push(ImmutableList.of()),
    			Sequence(Bit(),ZeroOrMore(Sequence(W1(),Bit()))));

    }
    
    /**
     * A BitString is composed of a sequence of Bits. 
     * Recall that PEGs incorporate lexing into the parser.
     */
    public Rule Bit() {   
        return Sequence(
        		AnyOf("01"),
        		push(match()),
        		swap(),
        		push( ((ImmutableList<String>)pop()).append((String)pop()) )); 
// TODO: 5 lines snipped
    }

}

