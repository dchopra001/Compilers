package ece351.vhdl.ast;

import java.util.concurrent.atomic.AtomicInteger;

import ece351.util.Examinable;


public final class DesignUnit implements Examinable {
	private static final AtomicInteger counter = new AtomicInteger();
	public final int id;
	public final Architecture arch;
	public final Entity entity;
	public final String identifier;

	//An Entity and an Architecture make up a Design Unit
	public DesignUnit(Architecture arch, Entity entity) {
		this.arch = arch;
		this.entity = entity;
		this.identifier = entity.identifier;
		id = counter.getAndIncrement();
	}

	@Override
	public String toString() {
		return this.entity + "\n" + this.arch + "\n";
	}

	@Override
	public boolean equals(final Object obj) {
		// basics
		if (obj == null) return false;
		if (!obj.getClass().equals(this.getClass())) return false;
		final DesignUnit that = (DesignUnit) obj;

		// compare field values
		if (!this.arch.equals(that.arch)) return false;
		if (!this.entity.equals(that.entity)) return false;
		if (!this.identifier.equals(that.identifier)) return false;

		// no significant differences
		return true;
	}

	@Override
	public boolean isomorphic(final Examinable obj) {
		// basics
		if (obj == null) return false;
		if (!obj.getClass().equals(this.getClass())) return false;
		final DesignUnit that = (DesignUnit) obj;

		// compare field values
		if (!this.arch.isomorphic((that.arch))) return false;
		if (!this.entity.isomorphic((that.entity))) return false;
		if (!this.identifier.equals(that.identifier)) return false;
		
		// no significant differences
		return true;
	}

	@Override
	public boolean equivalent(final Examinable obj) {
		return isomorphic(obj);
	}

}
