package ece351.vhdl.ast;
import org.parboiled.common.ImmutableList;

import ece351.util.Examinable;
import ece351.util.Examiner;

public final class VProgram implements Examinable {
	//may not need to make AST for these nodes because we do not need to do 
	//anything with it, we COULD check if they are using things that are defined in the libraries
	//but that is too much work
	
	public final ImmutableList<DesignUnit> designUnits;
	
	public VProgram(final ImmutableList<DesignUnit> designUnits) {
		this.designUnits = designUnits;
	}

    @Override
    public String toString() {
        final StringBuilder b = new StringBuilder();
        for (final DesignUnit d : designUnits) {                 
                b.append(d.toString());
        }
        return b.toString();
    }

    @Override
	public boolean equals(final Object obj) {
		// basics
		if (obj == null) return false;
		if (!obj.getClass().equals(this.getClass())) return false;
		final VProgram that = (VProgram) obj;
		
		// compare field values using Examiner.orderedExamination()
		if (!Examiner.orderedExamination(Examiner.Equals, this.designUnits, that.designUnits)
) return false;
		// no significant differences
		return true;
	}
	
	@Override
	public boolean isomorphic(final Examinable obj) {
		// basics
		if (obj == null) return false;
		if (!obj.getClass().equals(this.getClass())) return false;
		final VProgram that = (VProgram) obj;
		
		// compare field values using Examiner.unorderedExamination()
		if (!Examiner.orderedExamination(Examiner.Isomorphic, this.designUnits, that.designUnits)
) return false;
		
		// no significant differences
		return true;
	}

	@Override
	public boolean equivalent(final Examinable obj) {
		return isomorphic(obj);
	}
}
