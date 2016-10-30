package ece351.common.ast;

import java.util.Set;

import ece351.util.CommandLine.FSimplifierOptions;
import ece351.vhdl.VConstants;

public final class NotExpr extends UnaryExpr{
	public NotExpr(Expr argument) {
		super(argument);
	}

	public NotExpr() { this(null); }
	
	@Override
    protected final Expr simplifyOnce(final Set<FSimplifierOptions> opts) {		
    	// simplify our child
    	final Expr localexpr = expr.simplify(opts);
		if (localexpr instanceof ConstantExpr){
			ConstantExpr constant = (ConstantExpr) localexpr;
			// !true = false
			if (constant.b){
				return ConstantExpr.FalseExpr;
			// !false = true
			}else{
				return ConstantExpr.TrueExpr;
			}
					
 		}else if(localexpr instanceof NotExpr){
 			return ((NotExpr) localexpr).expr;
 		}else{
 			return new NotExpr(localexpr);
 		}
    }
	
    public Expr accept(final ExprVisitor v){
    	return v.visit(this);
    }
	
	@Override
	public String operator() {
		return VConstants.NOT;
	}
	@Override
	public UnaryExpr newUnaryExpr(final Expr expr) {
		return new NotExpr(expr);
	}

}
