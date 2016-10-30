package ece351.vhdl;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import org.parboiled.common.ImmutableList;

import ece351.common.ast.AndExpr;
import ece351.common.ast.AssignmentStatement;
import ece351.common.ast.ConstantExpr;
import ece351.common.ast.EqualExpr;
import ece351.common.ast.Expr;
import ece351.common.ast.NAndExpr;
import ece351.common.ast.NOrExpr;
import ece351.common.ast.NaryAndExpr;
import ece351.common.ast.NaryOrExpr;
import ece351.common.ast.NotExpr;
import ece351.common.ast.OrExpr;
import ece351.common.ast.VarExpr;
import ece351.common.ast.XNOrExpr;
import ece351.common.ast.XOrExpr;
import ece351.vhdl.ast.PostOrderVExprVisitor;


	public final class DetermineInputPins extends PostOrderVExprVisitor {
		private final Set<String> inputPins = new LinkedHashSet<String>();
		
		private DetermineInputPins(final AssignmentStatement conc, final ImmutableList<String> entityOutputs) { 
			if(entityOutputs.contains(conc.outputVar))
				traverse(conc.expr); 
		}
		
		public static Set<String> inputVars(final AssignmentStatement conc,final ImmutableList<String> entityOutputs) {
			final DetermineInputPins div = new DetermineInputPins(conc,entityOutputs);
			return Collections.unmodifiableSet(div.inputPins);
		}
		
//		public static Set<String> inputVars(final FProgram p) {
//			final Set<String> vars = new TreeSet<String>();
//			for (final Formula f : p.formulas) {
//				vars.addAll(DetermineInputPins.inputVars(f));
//			}
//			return Collections.unmodifiableSet(vars);
//		}
		
//		@Override public void visit(final ConstantExpr e) { return; }
//		@Override public void visit(final VarExpr e) { inputVars.add(e.name); }
//		@Override public void visit(final NotExpr e) { return; }
//		@Override public void visit(final AndExpr e) { return; }
//		@Override public void visit(final OrExpr e) { return; }

		@Override
		public Expr visit(EqualExpr equalVExpr) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Expr visit(VarExpr e) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Expr visit(NotExpr e) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Expr visit(AndExpr e) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Expr visit(OrExpr e) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Expr visit(XOrExpr e) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Expr visit(ConstantExpr constantVExpr) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Expr visit(NAndExpr e) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Expr visit(NOrExpr e) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Expr visit(XNOrExpr e) {
			// TODO Auto-generated method stub
			return null;
		}
		
		@Override public Expr visit(NaryAndExpr e) { return e; }
		@Override public Expr visit(NaryOrExpr e) { return e; }
	}
