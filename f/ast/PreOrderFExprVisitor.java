package ece351.f.ast;

import ece351.common.ast.BinaryExpr;
import ece351.common.ast.Expr;
import ece351.common.ast.NaryExpr;
import ece351.common.ast.UnaryExpr;

public abstract class PreOrderFExprVisitor extends FExprVisitor {

	@Override
	public Expr traverse(final NaryExpr e) {
		e.accept(this);
		for (Expr c : e.children) {
			traverse(c);
		}
		return e;
	}

	@Override
	public Expr traverse(final BinaryExpr b) {
		b.accept(this);
		traverse(b.left);
		traverse(b.right);
		return b;
	}

	@Override
	public Expr traverse(final UnaryExpr u) {
		u.accept(this);
		traverse(u.expr);
		return u;
	}
}
