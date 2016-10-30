package ece351.vhdl;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
import ece351.util.CommandLine;
import ece351.util.CommandLine.FSimplifierOptions;
import ece351.vhdl.ast.Architecture;
import ece351.vhdl.ast.Component;
import ece351.vhdl.ast.DesignUnit;
import ece351.vhdl.ast.IfElseStatement;
import ece351.vhdl.ast.PostOrderVExprVisitor;
import ece351.vhdl.ast.Process;
import ece351.vhdl.ast.Statement;
import ece351.vhdl.ast.VProgram;

/**
 * Inlines logic in components to architecture body.
 */
public final class Elaborator extends PostOrderVExprVisitor {

	private final Map<String, String> current_map = new LinkedHashMap<String, String>();
	
	public static void main(String[] args) {
		System.out.println(elaborate(args));
	}
	
	public static VProgram elaborate(final String[] args) {
		return elaborate(new CommandLine(args));
	}
	
	public static VProgram elaborate(final CommandLine c) {
        final VProgram program = DeSugarer.desugar(c);
        return elaborate(program);
	}
	
	public static VProgram elaborate(final VProgram program) {
		final Elaborator e = new Elaborator();
		return e.elaborateit(program);
	}

	private VProgram elaborateit(VProgram root) {
			ImmutableList<DesignUnit> modifiedDesignUnits = ImmutableList.of();
			int componentsElaborated = 0;
			for (DesignUnit currDunit : root.designUnits ){
				if (currDunit.arch.components.isEmpty()){
					modifiedDesignUnits = modifiedDesignUnits.append(currDunit);
					continue;
				}
				// In the elaborator, an architecture's list of signals, and set of statements may change; make copies of these lists
				ImmutableList<String> signalCopy = currDunit.arch.signals;
				ImmutableList<Statement> statementsCopy = currDunit.arch.statements;
				
				// start elaborating components
				for (Component currComponent : currDunit.arch.components){
					componentsElaborated++;
					//find the entity this component maps to
					current_map.clear();
					for (DesignUnit searchDunit : modifiedDesignUnits){
						if (searchDunit.entity.identifier.equals(currComponent.entityName)){
							//add intermediate signals to the current design unit from the found
							//design unit
								for( String toaddSignal : searchDunit.arch.signals){
									signalCopy = signalCopy.append("comp" + componentsElaborated + "_" + toaddSignal);
									current_map.put(toaddSignal, "comp" + componentsElaborated + "_" + toaddSignal);
								}
							//populate dictionary/map
							Iterator<String> entityInputIterator = searchDunit.entity.input.iterator();
							Iterator<String> entityOutputIterator = searchDunit.entity.output.iterator();
							Iterator<String> ComponentIterator =  currComponent.signalList.iterator();
							//add input signals, map to ports
							while (entityInputIterator.hasNext())
							{
								current_map.put(entityInputIterator.next(), ComponentIterator.next());
							}
							//add output signals, map to ports
							while(entityOutputIterator.hasNext()){
								String outputSignal = entityOutputIterator.next();
								//if (searchDunit.arch.signals.size() > 0){
								//	current_map.put(outputSignal,"comp" + componentsElaborated + "_" + outputSignal);
								//}else{
									current_map.put(outputSignal, ComponentIterator.next());
								//}
									
							}
							//inline the component
							for(Statement toCopy : searchDunit.arch.statements){			
								statementsCopy = statementsCopy.append(this.substituteVars(toCopy));
							}					
						}
					}		
				}
				// new Architecture
				ImmutableList<Component> emptyList = ImmutableList.of();
				Architecture modifiedArch = new Architecture(statementsCopy, emptyList, signalCopy, currDunit.arch.entityName, currDunit.arch.architectureName);
				// make new designunit
				DesignUnit modifiedDesignUnit = new DesignUnit(modifiedArch, currDunit.entity);
				modifiedDesignUnits = modifiedDesignUnits.append(modifiedDesignUnit);
			}
			// so that they can be modified
						
						//add local signals, add to signal list of i						
						//loop through the statements in the architecture body		
							//make the appropriate variable substitutions for signal assignment statements
							//make the appropriate variable substitutions for processes (sensitivity list, if/else body statements)
// TODO: 108 lines snipped
		return new VProgram(modifiedDesignUnits);
	}
	
	private Statement substituteVars(final Statement e){
		if (e instanceof AssignmentStatement){
			AssignmentStatement toModify = (AssignmentStatement) e;
			AssignmentStatement modifiedAssignment = this.substituteVars(toModify);
			return (Statement) modifiedAssignment;
		}else if(e instanceof Process){
			Process toModify = (Process) e;
			ImmutableList<String> newSensitivityList = ImmutableList.of();
			for( String signal : toModify.sensitivityList){
				newSensitivityList = newSensitivityList.append(current_map.get(signal));
			}
			ImmutableList<Statement> newStatementList = ImmutableList.of();
			for (Statement oldStatement : toModify.sequentialStatements){
				Statement newStatement = substituteVars(oldStatement);
				newStatementList = newStatementList.append(newStatement);
			}
			return (Statement) new Process(newStatementList,newSensitivityList);
				
		}else if (e instanceof IfElseStatement ){
			IfElseStatement toModify = (IfElseStatement) e;
			Expr newCondition = traverse(toModify.condition);
			ImmutableList<AssignmentStatement> newIfBody = ImmutableList.of();
			ImmutableList<AssignmentStatement> newElseBody = ImmutableList.of();
			for (AssignmentStatement elsestmt : toModify.elseBody){
				newElseBody = newElseBody.append(this.substituteVars(elsestmt));
			}
			for (AssignmentStatement ifstmt : toModify.ifBody){
				newIfBody = newIfBody.append(this.substituteVars(ifstmt));
			}
			
			IfElseStatement newIfElseStatement = new IfElseStatement(newElseBody, newIfBody, newCondition);
			return newIfElseStatement;
		}
		return e;
	}
	private AssignmentStatement substituteVars(final AssignmentStatement e) {
		VarExpr newOutputVar = (VarExpr)traverse(e.outputVar);
		Expr newExpr = traverse(e.expr);
		return new AssignmentStatement(newOutputVar,newExpr);
	}
	
	@Override
	public Expr visit(ConstantExpr e) {
		return e;
	}
	
	@Override
	public Expr visit(VarExpr e) {
		String Var = current_map.get(e.identifier);
		if (Var == null){
			return e;
		}
		return new VarExpr(Var);
	}
	
	@Override
	public Expr visit(NotExpr e) {
		return e;
	}
	
	@Override
	public Expr visit(AndExpr e) {
		return e;
	}
	
	@Override
	public Expr visit(OrExpr e) {
		return e;
	}
	
	@Override
	public Expr visit(XOrExpr e) {
		return e;
	}
	
	@Override
	public Expr visit(EqualExpr e) {
		return e;
	}

	@Override
	public Expr visit(NAndExpr e) {
		return null;
	}

	@Override
	public Expr visit(NOrExpr e) {
		return null;
	}

	@Override
	public Expr visit(XNOrExpr e) {
		return null;
	}
	
	@Override public Expr visit(NaryAndExpr e) { return e; }
	@Override public Expr visit(NaryOrExpr e) { return e; }
}
