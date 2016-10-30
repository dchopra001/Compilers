package ece351.vhdl.ast;

import org.parboiled.common.ImmutableList;

import ece351.util.Examinable;
import ece351.util.Examiner;

public final class Architecture implements Examinable {
	public final String architectureName;
	public final String entityName;
	public final ImmutableList<String> signals;
	public final ImmutableList<Component> components;
	public final ImmutableList<Statement> statements;

	public Architecture(final ImmutableList<Statement> statementList,
			ImmutableList<Component> components, ImmutableList<String> signalList, final String ent, final String arch) {
		this.architectureName = arch;
		this.entityName = ent;
		this.statements = statementList;
		this.signals = signalList;
		this.components = components;
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
    	final StringBuilder output = new StringBuilder();
    	output.append("architecture ");
    	output.append(architectureName);
    	output.append(" of ");
    	output.append(entityName);
    	output.append(" is\n");
    	if (signals.size() > 0) {
    		output.append("\tsignal ");
    		output.append(bitListToString(signals));
    		output.append(" : bit;");
    	}
    	output.append("\nbegin\n");
    	for(Component c : components) {
    		output.append("\t"); output.append(c); output.append("\n");
    	}
    	for (Statement stmt : statements) {
    		output.append("\t");
    		output.append(stmt);
    	}
    	output.append("end ");
    	output.append(architectureName);
    	output.append(";\n");
    	return output.toString();
    }

    @Override
	public boolean equals(final Object obj) {
		// basics
		if (obj == null) return false;
		if (!obj.getClass().equals(this.getClass())) return false;
		final Architecture that = (Architecture) obj;
		
		// compare field values using Examiner.orderedExamination()
		if(!Examiner.orderedExamination(Examiner.Equals,this.components, that.components))  return false;
		if(!Examiner.orderedExamination(Examiner.Equals,this.statements, that.statements))  return false;
		if(!Examiner.orderedEquals(this.statements, that.statements))  return false;
		if(!this.architectureName.equals(that.architectureName)) return false;

		// no significant differences
		return true;
	}
	
	@Override
	public boolean isomorphic(final Examinable obj) {
		// basics
		if (obj == null) return false;
		if (!obj.getClass().equals(this.getClass())) return false;
		final Architecture that = (Architecture) obj;
		
		// compare field values using Examiner.unorderedExamination()
		if(!Examiner.unorderedExamination(Examiner.Isomorphic,this.components, that.components))  return false;
		if(!Examiner.unorderedExamination(Examiner.Isomorphic,this.statements, that.statements))  return false;
		if(!Examiner.unorderedEquals(this.signals, that.signals))  return false;
		if(!this.architectureName.equals(that.architectureName)) return false;
		
		// no significant differences
		return true;
	}

	@Override
	public boolean equivalent(final Examinable obj) {
		return isomorphic(obj);
	}        
        
}
