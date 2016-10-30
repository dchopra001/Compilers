package ece351.f.ast;

import ece351.common.ast.*;

public abstract class PostOrderFExprVisitor extends FExprVisitor {

	@Override
	public Expr traverse(final NaryExpr e) {
		for (Expr c : e.children) {
			traverse(c);
			}
			e.accept(this);
		return e;
	}

	@Override
	public Expr traverse(final BinaryExpr b) {
		traverse(b.left);
		traverse(b.right);
		b.accept(this);
		return b;
	}

	@Override
	public Expr traverse(final UnaryExpr u) {
		traverse(u.expr);
		u.accept(this);
		return u;
	}
}
