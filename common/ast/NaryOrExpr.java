package ece351.common.ast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import ece351.util.CommandLine.FSimplifierOptions;
import ece351.vhdl.VConstants;

public final class NaryOrExpr extends NaryExpr {

	public NaryOrExpr(final Expr... exprs) {
		super(exprs);
	}
	
	public NaryOrExpr(final List<Expr> children) {
		super(children);
	}
	
	@Override
	public Expr accept(ExprVisitor v) { v.visit(this); return this;}

	@Override
	public String operator() {
		return VConstants.OR;
	}

	@Override
	protected String displayName() {
		return "NaryOr";
	}

	@Override
	protected Expr simplifySelf(final Set<FSimplifierOptions> opts) {
		assert repOk();
		return this; // TODO: replace with useful code

		// make a mutable temporary copy of children to work with
		// (x.y) + x ... = x ...
		// classify expressions into two groups: (1) conjunctions (2) others
			// check if there are any conjunctions that can be removed
			// remove all possible conjunctions
			// Cover the more difficult case when there exists N-ary and expressions within 
			// the conjunctions that may match a subset of children
			// Partition children into two lists: one containing all n-ary and expressions, and one containing the rest
			// For each n-ary and expression child, determine if the n-ary and expression has any n-ary or expressions and search 
			// other children to see if there's a 'nested' match
				// Aggregate list of n-ary or expressions within the n-ary and expression to check
					// For each n-ary or expression found within n-ary and expression, search for match 
					// in subset of this.children that are not n-ary and expressions
						// Check for match
									// There's a match
			// Remove the applicable n-ary or expressions
			// only one new child, return it
			// multiple newchildren. are they any different?
// TODO: 120 lines snipped
	}

	@Override
	protected Class<? extends BinaryExpr> getCorrespondingBinaryExprClass() {
		return null; // TODO: replace this with something useful
// TODO: 1 lines snipped
	}

	@Override
	public ConstantExpr getAbsorbingElement() {
// TODO: 1 lines snipped
		return ConstantExpr.TrueExpr; // TODO: return the correct value
	}

	@Override
	public ConstantExpr getIdentityElement() {
// TODO: 1 lines snipped
		return ConstantExpr.FalseExpr; // TODO: return the correct value
	}
	
	@Override
	public NaryExpr newNaryExpr(final List<Expr> children) {
		return new NaryOrExpr(children);
	}

}
