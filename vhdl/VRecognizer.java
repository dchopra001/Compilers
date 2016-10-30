package ece351.vhdl;

import org.parboiled.Rule;
import org.parboiled.common.FileUtils;

import ece351.util.CommandLine;

//Parboiled requires that this class not be final
public /*final*/ class VRecognizer extends VBase {

	public static void main(final String arg) {
		main(new String[]{arg});
	}
	
	public static void main(final String[] args) {
		final CommandLine c = new CommandLine(args);
    	process(VRecognizer.class, c.readInputSpec());
    }

    public Rule Program() {
    	return OneOrMore(DesignUnit());
    }
    
    public Rule DesignUnit(){
    	return Sequence(EntityDecl(),ArchBody());
    }
    
    public Rule IdList(){
    	return Sequence(
    			Id(),
    			ZeroOrMore(W0(),Ch(','),W0(),Id()),
    			W0()
    			);
    }
    
    public Rule ArchBody(){
    	return Sequence(
    			ARCHITECTURE(),
    			Id(),
    			W(),
    			OF(),
    			Id(),
    			W(),
    			IS(),
    			Optional(SIGNAL(),IdList(),Ch(':'),BIT(),Ch(';')),
    			BEGIN(),
    			ZeroOrMore(CompInst()),
    			FirstOf(ProcessStmts(),SigAssnStmts()),
    			END(),
    			Id(),
    			Ch(';'));
    }
    
    public Rule SigAssnStmts(){
    	return OneOrMore(SigAssnStmt());
    }
    
    public Rule SigAssnStmt(){
    	return Sequence(
    			Id(),
    			W(),
    			String("<="),
    			W(),
    			Expr(),
    			Ch(';'),
    			W());
    }
    
    public Rule ProcessStmts(){
    	return OneOrMore(ProcessStmt());
    }
    
    public Rule ProcessStmt(){
    	return Sequence(
    			PROCESS(),
    			Ch('('),
    			IdList(),
    			Ch(')'),
    			W(),
    			BEGIN(),
    			FirstOf(IfElseStmts(),SigAssnStmts()),
    			ENDPROCESS(),
    			Ch(';'),
    			W()
    			);
    }
    
    public Rule IfElseStmts(){
    	return OneOrMore(IfElseStmt());
    }
    
    public Rule IfElseStmt(){
    	return Sequence(
    			IF(),
    			Expr(),
    			THEN(),
    			SigAssnStmts(),
    			ELSE(),
    			SigAssnStmts(),
    			ENDIF(),
    			Optional(Id()),
    			Ch(';'),
    			W()
    			);
    }
    
    public Rule CompInst(){
    	return Sequence(
    			Id(),
    			Ch(':'),
    			ENTITY(),
    			String("work."),
    			Id(),
    			PORT(),
    			MAP(),
    			Ch('('),
    			IdList(),
    			Ch(')'),
    			Ch(';')
    			);
    }
    
    public Rule Expr(){
    	return Sequence(
    			Relation(),
    			ZeroOrMore(LogicOp(),Relation())
    			);
    }
    
    public Rule Relation(){
    	return Sequence(
    			Factor(),
    			Optional(RelOp(),Factor())
    			);   			
    }
    
    public Rule Factor(){
    	return Sequence(
    			Optional(NOT()),
    			Literal()
    			);
    }
    
    public Rule Literal(){
    	return Sequence(
    				FirstOf(
			    			Id(),
			    			Constant(),
			    			Sequence(Ch('('),Expr(),Ch(')'))
			    		   ),
	    			W0()
    			);
    }
    
    public Rule LogicOp(){
    	return FirstOf(
    			AND(),
    			OR(),
    			XOR(),
    			NAND(),
    			NOR(),
    			XNOR()
    			);
    }
    
    public Rule RelOp(){
    	return Sequence(Ch('='),W0());
    }
    
    public Rule Constant(){
    	return FirstOf(
    			String("'0'"),
    			String("'1'")
    			);
    }
    
    public Rule Id(){
    	return Sequence(
    			TestNot(Keyword()),
    			Char(),
    			ZeroOrMore(
    					FirstOf(
    							Char(),
    							Digit(),
    							Ch('_')
    							))
    			);
    }
    
    public Rule EntityDecl(){
    	return Sequence(
    			ENTITY(),
    			Id(),
    			W1(),
    			IS(),
    			PORT(),
    			W0(),
    			Ch('('),
    			W(),
    			IdList(),
    			Ch(':'),
    			W(),
    			IN(),
    			BIT(),
    			Ch(';'),
    			W(),
    			IdList(),
    			Ch(':'),
    			W(),
    			OUT(),
    			BIT(),
    			Ch(')'),
    			Ch(';'),
    			W(),
    			END(),
    			FirstOf(ENTITY(),Id()),
    			Ch(';'),
    			W()
    			); 
    }
}
