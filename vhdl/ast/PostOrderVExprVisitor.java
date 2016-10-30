package ece351.vhdl.ast;

import org.parboiled.common.ImmutableList;

import ece351.common.ast.BinaryExpr;
import ece351.common.ast.Expr;
import ece351.common.ast.NaryExpr;
import ece351.common.ast.UnaryExpr;

public abstract class PostOrderVExprVisitor extends VExprVisitor {

	@Override
	public Expr traverse(final BinaryExpr b) {
		final Expr left = traverse(b.left);
		final Expr right = traverse(b.right);
		final BinaryExpr be = b.newBinaryExpr(left,right);
		return be.accept(this);
	}

	@Override
	public Expr traverse(final UnaryExpr u) {
		final Expr expr = traverse(u.expr);
		final UnaryExpr ue = u.newUnaryExpr(expr);
		return ue.accept(this);
	}
	
	@Override
	public Expr traverse(final NaryExpr n) {
		ImmutableList<Expr> children = ImmutableList.of();
		for(Expr child : n.children) {
			children = children.append(traverse(child));
		}
		return n.newNaryExpr(children).accept(this);
	}


}
