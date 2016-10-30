package ece351.vhdl;

import org.parboiled.Rule;
import org.parboiled.annotations.MemoMismatches;
import org.parboiled.common.FileUtils;
import org.parboiled.common.ImmutableList;

import ece351.common.ast.AndExpr;
import ece351.common.ast.AssignmentStatement;
import ece351.common.ast.ConstantExpr;
import ece351.common.ast.EqualExpr;
import ece351.common.ast.Expr;
import ece351.common.ast.NAndExpr;
import ece351.common.ast.NOrExpr;
import ece351.common.ast.NotExpr;
import ece351.common.ast.OrExpr;
import ece351.common.ast.VarExpr;
import ece351.common.ast.XNOrExpr;
import ece351.common.ast.XOrExpr;
import ece351.util.BaseParser351;
import ece351.util.CommandLine;
import ece351.vhdl.ast.Architecture;
import ece351.vhdl.ast.Component;
import ece351.vhdl.ast.DesignUnit;
import ece351.vhdl.ast.Entity;
import ece351.vhdl.ast.IfElseStatement;
import ece351.vhdl.ast.Process;
import ece351.vhdl.ast.Statement;
import ece351.vhdl.ast.VProgram;

//Parboiled requires that this class not be final
public /*final*/ class VParser extends VBase {

	public static void main(final String arg) {
		main(new String[]{arg});
	}
	
	public static void main(final String[] args) {
		final CommandLine c = new CommandLine(args);
        VProgram vprog = parse(c.readInputSpec());
        System.out.println(vprog);
    }
	
	public static VProgram parse(final String arg) {
       return (VProgram) process(VParser.class, arg).resultValue;
    }

	
		// TODO: Write a VHDL parser that pushes an instance of VProgram to the top of the stack when it is done parsing
    	//start creating list of IDs
    	// For the grammar production Id, ensure that the Id does not match any of the keywords specified
    	// in the rule, 'Keyword'
	
	  public Rule Program() {
	    	return Sequence(
	    			// push empty list of Design Units on stack
					push(ImmutableList.of()),
	    			OneOrMore(DesignUnit()),
	    			W0(),
	    			EOI,
	    			// expect a list of Design Units on top of stack
	    			// and turn them into a VProgram
	    			push(new VProgram((ImmutableList<DesignUnit>)pop()))
	    			);
	    }
	    
	    public Rule DesignUnit(){
	    	return Sequence(
	    			EntityDecl(),
	    			//debugStack(),
	    			ArchBody(),
	    			//debugStack(),
	    			// at this point expect an architecture and an entity
	    			// on top of the stack
	    			push(new DesignUnit((Architecture)pop(),(Entity)pop())),
	    			swap(),
	    			// now pop list of Design Units and add the one we just
	    			// created
	    			push(((ImmutableList<DesignUnit>)pop()).append((DesignUnit)pop()))
	    			);
	    }
	    
	    public Rule IdList(){
	    	return Sequence(
	    			Id(),
	    			// at this point an Id string is ontop of the stack
	    			// with the list of strings below it
	    			swap(), // get list ontop
	    			push(((ImmutableList<String>)pop()).append((String)pop())),
	    			ZeroOrMore(Sequence(W0(),Ch(','),W0(),Id(),swap(),push(((ImmutableList<String>)pop()).append((String)pop())))),
	    			W0()
	    			);
	    }
	    
	    public Rule ArchBody(){
	    	
	    	return Sequence(
	    			ARCHITECTURE(),
	    			Id(),
	    			//debugStack(),
	    			W(),
	    			OF(),
	    			Id(),
	    			//debugStack(),
	    			W(),
	    			IS(),
	    			// push list for signals
	    			push(ImmutableList.of()),
	    			Optional(SIGNAL(),IdList(),Ch(':'),W0(),BIT(),Ch(';'),W()),
	    			//debugStack(),
	    			BEGIN(),
	    			// push list for components
	    			push(ImmutableList.of()),
	    			ZeroOrMore(CompInst(),swap(),push(((ImmutableList<Component>)pop()).append((Component)pop()))),
	    			//debugStack(),
	    			// push lists for statements
	    			push(ImmutableList.of()),
	    			FirstOf(ProcessStmts(),SigAssnStmts()),
	    			//debugStack(),
	    			// now create the Architecture
	    			push(new Architecture(
	    					(ImmutableList<Statement>)pop(),
	    					(ImmutableList<Component>)pop(),
	    					(ImmutableList<String>)pop(),
	    					(String)pop(),
	    					(String)pop()
	    					)),
	    			END(),
	    			Id(),
	    			drop(),
	    			Ch(';'),
	    			W()
	    			);
	    }
	    
	    public Rule SigAssnStmts(){
	    	return OneOrMore(
	    			SigAssnStmt(),
	    			swap(),
	    			push( ((ImmutableList<Statement>)pop()).append((Statement)pop()))
	    			//debugStack()
	    			);
	    }
	    
	    public Rule SigAssnStmt(){
	    	return Sequence(
	    			Id(),
	    			W(),
	    			String("<="),
	    			W(),
	    			Expr(),
	    			swap(),
	    			push(new AssignmentStatement((String)pop(),(Expr)pop())),
	    			Ch(';'),
	    			W());
	    }
	    
	    public Rule ProcessStmts(){
	    	return  OneOrMore(
	    			ProcessStmt(),
	    			//debugStack(),
	    			swap(),
	    			//debugStack(),
	    			push( ((ImmutableList<Statement>)pop()).append((Statement)pop()))
	    			//debugStack()
	    			);
	    }
	    
	    public Rule ProcessStmt(){
	    	return Sequence(
	    			PROCESS(),
	    			Ch('('),
	    			W0(),
	    			push(ImmutableList.of()),
	    			IdList(),
	    			Ch(')'),
	    			W(),
	    			BEGIN(),
	    			// put empty list on stack to put assignments in
	    			push(ImmutableList.of()),
	    			FirstOf(IfElseStmts(),SigAssnStmts()),
	    			// at this point build a process
	    			push(new Process(
	    					(ImmutableList<Statement>)pop(),
	    					(ImmutableList<String>)pop()
	    					)),
	    			ENDPROCESS(),
	    			Ch(';'),
	    			W()
	    			);
	    }
	    
	    public Rule IfElseStmts(){
	    	return  OneOrMore(IfElseStmt(),
	    			swap(),
	    			push( ((ImmutableList<Statement>)pop()).append((Statement)pop()))
	    			);
	    }
	    
	    public Rule IfElseStmt(){
	    	return Sequence(
	    			IF(),
	    			Expr(),
	    			THEN(),
	    			push(ImmutableList.of()),
	    			SigAssnStmts(),
	    			ELSE(),
	    			push(ImmutableList.of()),
	    			SigAssnStmts(),
	    			push(new IfElseStatement(
	    					(ImmutableList<AssignmentStatement>)pop(),
	    					(ImmutableList<AssignmentStatement>)pop(),
	    					(Expr)pop()
	    					)),
	    			ENDIF(),
	    			Optional(Id(),drop()),
	    			Ch(';'),
	    			W()
	    			);
	    }
	    
	    public Rule CompInst(){
	    	return Sequence(
	    			Id(),
	    			W0(),
	    			Ch(':'),
	    			W0(),
	    			ENTITY(),
	    			String("work."),
	    			Id(),
	    			W0(),
	    			PORT(),
	    			MAP(),
	    			Ch('('),
	    			W0(),
	    			push(ImmutableList.of()),
	    			IdList(),
	    			// build the component
	    			push(new Component(
	    					(ImmutableList<String>)pop(),
	    					(String)pop(),
	    					(String)pop())),
	    			Ch(')'),
	    			Ch(';'),
	    			W()
	    			);
	    }
	    
	    public Rule Expr(){
	    	return Sequence(
	    			Relation(),
	    			ZeroOrMore(LogicOp(), push(match()),Relation(),swap3(),swap(),push(DetermineType((String)pop(),(Expr)pop(),(Expr)pop())))
	    			);
	    }
	    
	    public Expr DetermineType(String id, Expr left, Expr right){
	    	
	    	id = id.trim();
	    	if (id.equals("and")){
	    		return new AndExpr(left,right);
	    	}else if (id.equals("or")){
	    		return new OrExpr(left,right);
	    	}else if (id.equals("nand")){
	    		return new NAndExpr(left,right);
	    	}else if (id.equals("nor")){
	    		return new NOrExpr(left,right);
	    	}else if (id.equals("xnor")){
	    		return new XNOrExpr(left,right);
	    	}else if (id.equals("xor")){
	    		return new XOrExpr(left,right);
	    	}else{
	    		return null;
	    	}
	    }
	    
	    public Rule Relation(){
	    	return Sequence(
	    			Factor(),
	    			Optional(RelOp(),Factor(),push(new EqualExpr((Expr)pop(),(Expr)pop())))
	    			);   			
	    }
	    
	    public Rule Factor(){
	    	return Sequence(
	    			push("0"),
	    			Optional(Sequence(NOT(),drop(),push("1"))),
	    			Literal(),
	    			swap(),
	    			push(applyNot((String)pop(),(Expr)pop()))
	    			);
	    }
	    
	    public Expr applyNot(String isNot, Expr expression){
	    	if(isNot.equals("1")){
	    		return new NotExpr(expression);
	    	}else{
	    		return expression;
	    	}
	    	
	    }
	    
	    public Rule Literal(){
	    	return Sequence(
	    				FirstOf(
				    			Sequence(
				    					Id(),
				    					push( new VarExpr((String)pop()))
				    					),
				    			Constant(),
				    			Sequence(Ch('('),W0(),Expr(),Ch(')'),W0())
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
	    	return Sequence(
	    			Ch('='),
	    			W0()
	    			);
	    }
	    
	    public Rule Constant(){
	    	return Sequence(
	    			FirstOf(
	    					String("'0'"),
	    					String("'1'")
	    					),
	    					match().contains("0") ? push(ConstantExpr.FalseExpr) : push(ConstantExpr.TrueExpr)		
	    			);
	    }
	    
	    public Rule Id(){
	    	return Sequence(
	    			TestNot(Keyword()),
	    			// push empty string
	    			push(""),
	    			Char(),
	    			// build the Id string
	    			push((String)pop() + match()),
	    			ZeroOrMore(
	    					FirstOf(
	    							Char(),
	    							Digit(),
	    							Ch('_')
	    							)),
					push((String)pop() + match())
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
	    			push(ImmutableList.of()),
	    			IdList(),
	    			//swap as nextime idlist is called need ouputs on top
	    			Ch(':'),
	    			W(),
	    			IN(),
	    			BIT(),
	    			Ch(';'),
	    			W(),
	    			push(ImmutableList.of()),
	    			IdList(),
	    			// at this point stack is output -> input -> id
	    			push(new Entity((ImmutableList<String>)pop(),(ImmutableList<String>)pop(),(String)pop())),
	    			Ch(':'),
	    			W(),
	    			OUT(),
	    			BIT(),
	    			Ch(')'),
	    			Ch(';'),
	    			W(),
	    			END(),
	    			FirstOf(ENTITY(),Sequence(Id(),drop())),
	    			Ch(';'),
	    			W()
	    			); 
	    }
    
}
