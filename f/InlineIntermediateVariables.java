package ece351.f;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.parboiled.common.ImmutableList;

import ece351.common.ast.AndExpr;
import ece351.common.ast.AssignmentStatement;
import ece351.common.ast.BinaryExpr;
import ece351.common.ast.ConstantExpr;
import ece351.common.ast.Expr;
import ece351.common.ast.NaryAndExpr;
import ece351.common.ast.NaryExpr;
import ece351.common.ast.NaryOrExpr;
import ece351.common.ast.NotExpr;
import ece351.common.ast.OrExpr;
import ece351.common.ast.UnaryExpr;
import ece351.common.ast.VarExpr;
import ece351.f.ast.FExprVisitor;
import ece351.f.ast.FProgram;

public class InlineIntermediateVariables extends FExprVisitor {

	private final Set<String> intermediateVars = new TreeSet<String>();
	private final Map<String,AssignmentStatement> defns = new TreeMap<String,AssignmentStatement>();
	private int count = 0;

	/**
	 * Maximum depth that nesting is allowed before inlining stops working.
	 * This approach is less expensive than explicit cycle detection.
	 */
	private final int MAX = 5;
	
	private InlineIntermediateVariables(final FProgram p1) {
		// defns
		for (final AssignmentStatement a : p1.formulas) {
			defns.put(a.outputVar.identifier, a);
		}		
	}
	
	/**
	 * Assume that there are no cycles.
	 * @param p1
	 * @return
	 */
	public static FProgram inline(final FProgram p1) {
		final InlineIntermediateVariables fiv = new InlineIntermediateVariables(p1);
		return fiv.doit(0);
	}

	private final FProgram doit(final int iteration) {
		final int oldestCount = count;

		// replace uses of intermediate vars with their defns
		final List<AssignmentStatement> as = new ArrayList<AssignmentStatement>(defns.values());
		for (final AssignmentStatement a : as) {
			final int oldCount = count;
			final Expr e = traverse(a.expr);
			if (oldCount != count) {
				// something changed: save it
				final String n = a.outputVar.identifier;
				defns.put(n, new AssignmentStatement(a.outputVar, e));
			}
		}
		
		if (oldestCount == count || iteration >= MAX) {
			// nothing changed, or we've run out of budget: we're done
			// construct a new FProgram to return
			ImmutableList<AssignmentStatement> result = ImmutableList.of();
			for (final Map.Entry<String, AssignmentStatement> me : defns.entrySet()) {
				final String n = me.getKey();
				if (!intermediateVars.contains(n)) {
					// not an intermediate var, so must be an output
					result = result.append(me.getValue());
				}
			}
			return new FProgram(result);
		} else {
			// something changed, and we have budget: keep working
			return doit(iteration+1);
		}
	}
	
	/** 
	 * Rewrite VarExprs with their definitions, if available.
	 */
	@Override
	public Expr visit(final VarExpr e) {
		final String n = e.identifier;
		final AssignmentStatement a = defns.get(n);
		if (a == null) {
			// a true input variable
			return e;
		} else {
			// an intermediate variable: replace with defn
			intermediateVars.add(n);
			count++;
			return a.expr;
		}
	}

	// no-ops
	@Override public Expr visit(ConstantExpr e) { return e; }
	@Override public Expr visit(NotExpr e) { return e; }
	@Override public Expr visit(AndExpr e) { return e; }
	@Override public Expr visit(OrExpr e) { return e; }
	@Override public Expr visit(NaryAndExpr e) { return e; }
	@Override public Expr visit(NaryOrExpr e) { return e; }

	@Override
	public Expr traverse(NaryExpr e) {
		ImmutableList<Expr> children = ImmutableList.of();
		boolean change = false;
		for (final Expr c1 : e.children) {
			final Expr c2 = traverse(c1);
			children = children.append(c2);
			if (c2 != c1) {
				change = true;
			}
		}
		if (change) {
			e = e.newNaryExpr(children);
		}
		return e.accept(this);
	}

	@Override
	public Expr traverse(BinaryExpr b) {
		// children first
		final Expr left = traverse(b.left);
		final Expr right = traverse(b.right);
		// only rewrite if something has changed
		if (left != b.left || right != b.right) {
			b = b.newBinaryExpr(left, right);
		}
		return b.accept(this);
	}

	@Override
	public Expr traverse(UnaryExpr u) {
		// children first
		final Expr child = traverse(u.expr);
		// only rewrite if something has changed
		if (child != u.expr) {
			u = u.newUnaryExpr(child);
		}
		return u.accept(this);
	}
	
}
