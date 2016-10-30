package ece351.vhdl.ast;

import java.util.List;

import org.parboiled.common.ImmutableList;

import ece351.common.ast.AssignmentStatement;
import ece351.common.ast.Expr;
import ece351.util.Examinable;
import ece351.util.Examiner;

public final class IfElseStatement extends Statement implements Examinable {
	public final Expr condition;
	public final List<AssignmentStatement> ifBody;
	public final List<AssignmentStatement> elseBody;
	
	public IfElseStatement(ImmutableList<AssignmentStatement> elseBody,
			ImmutableList<AssignmentStatement> ifBody,
			Expr cond) {
		this.condition = cond;
		this.elseBody = elseBody;
		this.ifBody = ifBody;
	}
        
    @Override
    public String toString() {
    	final StringBuilder output = new StringBuilder();
    	output.append("\t\t\tif ( ");
    	output.append(condition);
    	output.append(" ) then\n");
    	for (AssignmentStatement stmt : ifBody) {
    		output.append("\t\t\t\t");
    		output.append(stmt);
    	}
    	output.append("\t\t\telse\n");
    	for (AssignmentStatement stmt : elseBody) {
    		output.append("\t\t\t\t");
    		output.append(stmt);
    	}
    	output.append("\t\t\tend if;\n");
    	return output.toString();
    }

    @Override
	public boolean equals(final Object obj) {
		// basics
		if (obj == null) return false;
		if (!obj.getClass().equals(this.getClass())) return false;
		final IfElseStatement that = (IfElseStatement) obj;
		
		// compare field values using Examiner.orderedExamination()
		if (!Examiner.orderedExamination(Examiner.Equals, this.elseBody, that.elseBody)) return false;
		if (!Examiner.orderedExamination(Examiner.Equals, this.ifBody, that.ifBody)) return false; 
		if (!this.condition.equals(that.condition)) return false; 
		
		// no significant differences
		return true;
	}
	
	@Override
	public boolean isomorphic(final Examinable obj) {
		// basics
		if (obj == null) return false;
		if (!obj.getClass().equals(this.getClass())) return false;
		final IfElseStatement that = (IfElseStatement) obj;
		
		// compare field values using Examiner.orderedExamination()
		// the statements within each process should be ordered, since the statements execute in sequence (and not parallel)
		// however, compare each statement isomorphically
		if (!Examiner.orderedExamination(Examiner.Isomorphic, this.elseBody, that.elseBody)) return false;
		if (!Examiner.orderedExamination(Examiner.Isomorphic, this.ifBody, that.ifBody)) return false; 
		if (!this.condition.isomorphic((that.condition))) return false; 
		
		// no significant differences
		return true;
	}

	@Override
	public boolean equivalent(final Examinable obj) {
		return isomorphic(obj);
	}
}
