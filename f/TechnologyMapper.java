package ece351.f;

import java.io.PrintWriter;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;

import kodkod.util.collections.IdentityHashSet;
import ece351.common.ast.AndExpr;
import ece351.common.ast.AssignmentStatement;
import ece351.common.ast.ConstantExpr;
import ece351.common.ast.Expr;
import ece351.common.ast.NaryAndExpr;
import ece351.common.ast.NaryOrExpr;
import ece351.common.ast.NotExpr;
import ece351.common.ast.OrExpr;
import ece351.common.ast.VarExpr;
import ece351.f.ast.FProgram;
import ece351.f.ast.PostOrderFExprVisitor;

public final class TechnologyMapper extends PostOrderFExprVisitor {

	/** Where we will write the output to. */
	private final PrintWriter out;
	
	/** 
	 * Table of substitutions for common subexpression elimination.
	 * We use a LinkedHashMap instead of a HashMap to get
	 * deterministic ordering.
	 */
	private final IdentityHashMap<Expr,Expr> substitutions = new IdentityHashMap<Expr,Expr>();

	/**
	 * The set of nodes in our circuit diagram. (We'll produce a node
	 * for each .) We could just print
	 * the nodes directly to the output stream instead of building up
	 * this set, but then we might output the same node twice. The set
	 * uniqueness property ensure that we will ultimately print each
	 * node exactly once.
	 */
	private final Set<String> nodes = new LinkedHashSet<String>();
	
	/**
	 * The set of edges in our circuit diagram. We could just print
	 * the edges directly to the output stream instead of building up
	 * this set, but then we might output the same edge twice. The set
	 * uniqueness property ensure that we will ultimately print each
	 * edge exactly once.
	 */
	private final Set<String> edges = new LinkedHashSet<String>();
	
	public TechnologyMapper(final PrintWriter out) {
		this.out = out;
	}
	
	public TechnologyMapper() {
		 this(new PrintWriter(System.out));
	}
	
	public static void main(final String arg) {
		main(new String[]{arg});
	}
	public static void main(final String[] args) {
		render(FParser.parse(args), new PrintWriter(System.out));
	}
	
	/**
	 * Translate an FProgram to Graphviz format.
	 */
	public static void render(final FProgram program, final PrintWriter out) {
		final TechnologyMapper tm = new TechnologyMapper(out);
		tm.render(program);
	}

	/** Where the real work happens. */
	public void render(final FProgram program) {
		header(out);
		
		// build a set of all of the exprs in the program
		Set<Expr> expressions = ExtractAllExprs.allExprs(program);
		
		// map all the duplicate exprs to the single one in the set
		//program.formulas.
		IdentityHashMap<Expr,Expr> equivalentSet = new IdentityHashMap<Expr,Expr>();

		// build substitutions by determining equivalences of exprs
		for (Expr expressiona : expressions){
			// set to hold equivalent expressions
			equivalentSet.clear();
			equivalentSet.put(expressiona, expressiona);
			for (Expr expressionb : expressions){
				if (expressiona.equivalent(expressionb)){
					equivalentSet.put(expressionb,expressionb);
				}
			}
			// pick a representative and use it to build substitutions
			for(Expr equivs : equivalentSet.keySet()){
				substitutions.put(equivs, (Expr) (equivalentSet.keySet().toArray())[0]);
			}
		}
		// attach images to gates	
		// compute edges
		// compute output edges
		for(AssignmentStatement formula : program.formulas){
			traverse(formula.expr);
			final Expr e2 = substitutions.get(formula.outputVar);
			assert e2 != null : "no substitution for " + formula.outputVar + " " + formula.outputVar.nameID();
			node(e2.nameID(), e2.toString());
			edge(formula.expr,formula.outputVar);		
		}
		// print nodes
		for( String node : nodes){
			out.println(node);
		}
		// print edges
		for (String edge: edges){
			out.println(edge);
		}
		
// TODO: 50 lines snipped
		// print footer
		footer(out);
		out.flush();
		
		// release memory
		substitutions.clear();
		edges.clear();
	}

	
	private static void header(final PrintWriter out) {
		out.println("digraph g {");
		out.println("    // header");
		out.println("    rankdir=LR;");
		out.println("    margin=0.01;");
		out.println("    node [shape=\"plaintext\"];");
		out.println("    edge [arrowhead=\"diamond\"];");
		out.println("    // circuit ");
	}

	private static void footer(final PrintWriter out) {
		out.println("}");
	}

	@Override
	public Expr visit(final ConstantExpr e) {
		node(e.nameID(), e.toString());
		return e;
	}

	@Override
	public Expr visit(final VarExpr e) {
		final Expr e2 = substitutions.get(e);
		assert e2 != null : "no substitution for " + e + " " + e.nameID();
		node(e2.nameID(), e2.toString());
		return e;
	}

	@Override
	public Expr visit(final NotExpr e) {
		edge(e.expr, e);
		return e;
	}

	@Override
	public Expr visit(final AndExpr e) {
		node(e.nameID(),"","and_noleads.png");
		edge(e.left,e);
		edge(e.right,e);
		
// TODO: 2 lines snipped
		return e;
	}

	@Override
	public Expr visit(final OrExpr e) {
		edge(e.left,e);
		edge(e.right,e);
// TODO: 2 lines snipped
		return e;
	}
	
	@Override public Expr visit(final NaryAndExpr e) {
// TODO: 3 lines snipped
		node(substitutions.get(e).nameID(),substitutions.get(e).nameID(),"gates/and_noleads.png");
		for (Expr child : e.children)
		{
			edge(child,e);
		}
		return e;
	}

	@Override public Expr visit(final NaryOrExpr e) { 
		node(substitutions.get(e).nameID(),substitutions.get(e).nameID(),"gates/or_noleads.png");
		for (Expr child : e.children)
		{
			edge(child,e);
		}
// TODO: 3 lines snipped
		return e;
	}


	private void node(final String name, final String label) {
		nodes.add("    " + name + "[label=\"" + label + "\"];");
	}

	private void node(final String name, final String label, final String image) {
		nodes.add(String.format("    %s [label=\"%s\", image=\"%s\"];", name, label, image));
	}
	
	private void edge(final Expr source, final Expr target) {
			edge(substitutions.get(source).nameID(), substitutions.get(target).nameID());
	}
	
	private void edge(final String source, final String target) {
		edges.add("    " + source + " -> " + target + " ;");
	}
}
