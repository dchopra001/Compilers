package ece351.w.rdescent;

import org.parboiled.common.ImmutableList;

import ece351.util.Lexer;
import ece351.w.ast.WProgram;
import ece351.w.ast.Waveform;

public final class WRecursiveDescentParser {
    private final Lexer lexer;

    public WRecursiveDescentParser(final Lexer lexer) {
        this.lexer = lexer;
    }

    public static WProgram parse(final String input) {
    	final WRecursiveDescentParser p = new WRecursiveDescentParser(new Lexer(input));
        return p.parse();
    }

    public WProgram parse() {
    	return program();
// TODO: 29 lines snipped
    }
    
    public WProgram program() {
    	ImmutableList<Waveform> waveforms = ImmutableList.of();
        while (!lexer.inspectEOF()) {      
            waveforms = waveforms.append(waveform());
        }
        WProgram p = new WProgram(waveforms);
        lexer.consumeEOF();
        return p;
    }

    public Waveform waveform() {
    	String name = "";
		ImmutableList<String> bits = ImmutableList.of();
		name = lexer.consumeID();
		lexer.consume(":");
		while(!lexer.inspect(";")){
			bits = bits.append(lexer.consume("0","1"));
		}		
		lexer.consume(";");
		Waveform w = new Waveform(bits, name);
		return w;
    }
}
