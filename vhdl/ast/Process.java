package ece351.vhdl.ast;

import org.parboiled.common.ImmutableList;

import ece351.common.ast.AssignmentStatement;
import ece351.util.Examinable;
import ece351.util.Examiner;

public final class Process extends Statement implements Examinable {
	public final ImmutableList<String> sensitivityList;
	public final ImmutableList<Statement> sequentialStatements;
	
	public Process(ImmutableList<Statement> statements,
			ImmutableList<String> sensitivityList) {
		this.sensitivityList = sensitivityList;
		this.sequentialStatements = statements;
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
    	output.append("process ( ");
    	output.append(bitListToString(sensitivityList));
    	output.append(" ) \n\t\tbegin\n");
    	for (Statement stmt : sequentialStatements) {
    		if (stmt instanceof AssignmentStatement) output.append("\t\t\t");
    		output.append(stmt);
    	}
    	output.append("\t\tend process;\n");
    	return output.toString();
    }

    @Override
	public boolean equals(final Object obj) {
		// basics
		if (obj == null) return false;
		if (!obj.getClass().equals(this.getClass())) return false;
		final Process that = (Process) obj;
		
		// compare field values using Examiner.orderedExamination()
		if (!Examiner.orderedEquals(this.sensitivityList, that.sensitivityList)) return false;
		if (!Examiner.orderedExamination(Examiner.Equals, this.sequentialStatements, that.sequentialStatements)) return false; 
		
		// no significant differences
		return true;
	}
	
	@Override
	public boolean isomorphic(final Examinable obj) {
		// basics
		if (obj == null) return false;
		if (!obj.getClass().equals(this.getClass())) return false;
		final Process that = (Process) obj;
		
		// compare field values using Examiner.orderedExamination()
		// the statements within each process should be ordered, since the statements execute in sequence (and not parallel)
		// however, compare each statement isomorphically
		if (!Examiner.orderedEquals(this.sensitivityList, that.sensitivityList)) return false;
		if (!Examiner.orderedExamination(Examiner.Isomorphic, this.sequentialStatements, that.sequentialStatements)) return false; 
		
		// no significant differences
		return true;
	}

	@Override
	public boolean equivalent(final Examinable obj) {
		return isomorphic(obj);
	}
}
