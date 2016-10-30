package ece351.vhdl.ast;

import org.parboiled.common.ImmutableList;

import ece351.util.Examinable;
import ece351.util.Examiner;

public final class Component implements Examinable {
	public final String entityName;
	public final String instanceName;
	public final ImmutableList<String> signalList;
	
	public Component(ImmutableList<String> signals,
			String entityName , String instanceName) {
		this.entityName = entityName;
		this.instanceName = instanceName;
		this.signalList = signals;
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
        return instanceName + " : entity work." + entityName + " port map( " + bitListToString(signalList) + ");";
    }

    @Override
	public boolean equals(final Object obj) {
		// basics
		if (obj == null) return false;
		if (!obj.getClass().equals(this.getClass())) return false;
		final Component that = (Component) obj;
		
		if (!this.entityName.equals(that.entityName)) return false;
		if (!this.instanceName.equals(that.instanceName)) return false;
		if(!Examiner.orderedEquals(this.signalList, that.signalList)) return false;
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
