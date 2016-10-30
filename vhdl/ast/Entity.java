package ece351.vhdl.ast;

import org.parboiled.common.ImmutableList;

import ece351.util.Examinable;
import ece351.util.Examiner;

public final class Entity implements Examinable {
	public final String identifier;
	public final ImmutableList<String> input;
	public final ImmutableList<String> output;

	public Entity(final ImmutableList<String> out,
			final ImmutableList<String> in, final String id) {
		this.identifier = id;
		this.input = in;
		this.output = out;
	}
	
	static String bitListToString(final ImmutableList<String> list) {
		StringBuilder s = new StringBuilder();
		for(final String item : list) {
			s.append(item); s.append(", ");
		}
		if (s.length() > 0) {s.delete(s.length()-2,	s.length()-1);}
		return s.toString();
	}

    @Override
    public String toString() {
    	final String inputBits = bitListToString(input);
    	final String outputBits = bitListToString(output);
        return "entity " + identifier + " is port(\n" 
                + ((inputBits.length() > 0) ? "\t" + inputBits + " : in bit;\n" : "") 
                + ((outputBits.length() > 0) ? "\t" + outputBits + " : out bit\n" : "") 
                + ");\nend " + identifier + ";";
    }

    @Override
	public boolean equals(final Object obj) {
		// basics
		if (obj == null) return false;
		if (!obj.getClass().equals(this.getClass())) return false;
		final Entity that = (Entity) obj;
		
		// compare field values using Examiner.orderedEquals()
		if (!this.identifier.equals(that.identifier)) return false;
		if (!Examiner.orderedEquals(this.input, that.input)) return false; 
		if (!Examiner.orderedEquals(this.output, that.output)) return false; 

		// no significant differences
		return true;
	}
	
	@Override
	public boolean isomorphic(final Examinable obj) {
		return equals(obj);
	}

	@Override
	public boolean equivalent(final Examinable obj) {
		return isomorphic(obj);
	}

}
