package ece351.common.ast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import ece351.util.CommandLine.FSimplifierOptions;
import ece351.vhdl.VConstants;


public final class NaryAndExpr extends NaryExpr {

	public NaryAndExpr(final Expr... exprs) {
		super(exprs);
	}

	public NaryAndExpr(final List<Expr> children) {
		super(children);
	}
	

	@Override
	public Expr accept(ExprVisitor v) { v.visit(this); return this; }

	@Override
	public String operator() {
		return VConstants.AND;
	}

	@Override
	protected String displayName() {
		return "NaryAnd";
	}

	@Override
	public ConstantExpr getIdentityElement() {
// TODO: 1 lines snipped
		return ConstantExpr.TrueExpr; // TODO: return the correct value
	}
	
	@Override
	public ConstantExpr getAbsorbingElement() {
// TODO: 1 lines snipped
		return ConstantExpr.FalseExpr; // TODO: return the correct value
	}
	
	@Override
	protected Expr simplifySelf(final Set<FSimplifierOptions> opts) {
		assert repOk();
		return this; // TODO: replace with useful code
		// make a mutable temporary copy of children to work with
		// (x+y) . x ... = x ...
		// classify expressions into two groups: (1) disjunctions (2) others
			// check if there are any disjunctions that can be removed
			// remove all possible disjunctions
			// Cover the more difficult case when there exists N-ary and expressions within 
			// the disjunctions that may match a subset of children
			// Partition children into two lists: one containing all n-ary or expressions, and one containing the rest
			// For each n-ary or expression child, determine if the n-ary or expression has any n-ary and expressions and search 
			// other children to see if there's a 'nested' match
				// Aggregate list of n-ary and expressions within the n-ary or expression to check
					// For each n-ary and expression found within n-ary or expression, search for match 
					// in subset of this.children that are not n-ary or expressions
						// Check for match
									// There's a match
			// Remove the applicable n-ary or expressions
			// only one new child, return it
			// multiple newchildren. are they any different?
				// same, just return this
				// different, construct a new NaryAndExpr
// TODO: 120 lines snipped
	}

	@Override
	protected Class<? extends BinaryExpr> getCorrespondingBinaryExprClass() {
		return null; // TODO: replace this with something useful
// TODO: 1 lines snipped
	}

	@Override
	public NaryExpr newNaryExpr(final List<Expr> children) {
		return new NaryAndExpr(children);
	}
	
}
