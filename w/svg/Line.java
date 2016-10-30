package ece351.w.svg;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import ece351.util.Examinable;

final class Line implements Examinable {
	
	final static String LineX1 = "x1";
	final static String LineY1 = "y1";
	final static String LineX2 = "x2";
	final static String LineY2 = "y2";
	
	final int x1, x2, y1, y2;

	Line(int x1, int y1, int x2, int y2) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		assert repOk();
	}
	
	public boolean repOk() {
		// valid lines are either horizontal
		// or vertical
		if(x1 == x2 || y1 == y2){
			return true;
		}else{
			return false;
		}
		
	}

	@Override
	public String toString() {
		return "(" + x1 + "," + y1 + "," + x2 + "," + y2 + ")";
	}
	
	public String toXML() {
		return "<line x1=\"" + x1 + "\" y1=\"" + y1 + "\" x2=\""
				+ x2 + "\" y2=\"" + y2 + "\" />";
	}

	public static String toXML(final int x1, final int y1, final int x2, final int y2) {
		final Line line = new Line(x1, y1, x2, y2);
		return line.toXML();
	}
	
	public static Line fromXML(final Node node) {
		final String nodeName = node.getNodeName();
		if (nodeName.equals("line")) {
			NamedNodeMap nnMap = node.getAttributes();
			if (nnMap.getNamedItem(LineX1) != null && nnMap.getNamedItem(LineY1) != null
					&& nnMap.getNamedItem(LineX2) != null && nnMap.getNamedItem(LineY2) != null) {
				final int x1, y1, x2, y2;
				x1 = Integer.parseInt(nnMap.getNamedItem(LineX1).getNodeValue());
				y1 = Integer.parseInt(nnMap.getNamedItem(LineY1).getNodeValue());
				x2 = Integer.parseInt(nnMap.getNamedItem(LineX2).getNodeValue());
				y2 = Integer.parseInt(nnMap.getNamedItem(LineY2).getNodeValue());
				return new Line(x1, y1, x2, y2);
			}
		}
		return null;
	}

	/**
	 * If we override equals() then we must override hashCode().
	 */
	@Override
	public int hashCode() {
		int hash = 17;
		hash = 31 * hash + x1;
		hash = 31 * hash + x2;
		hash = 31 * hash + y1;
		hash = 31 * hash + y2;
		return hash;
	}
	
	/**
	 * Line objects are immutable, and so may be equals.
	 */
	@Override
	public boolean equals(final Object obj) {
		// basics
		if(obj == null){
			return false;
		}
		// compare field values
		// no differences
		boolean result = false;
		if(obj instanceof Line){
			Line toTest = (Line)obj;
			result = toTest.x1 == this.x1 && toTest.x2 == this.x2 && toTest.y1 == this.y1 && toTest.y2 == this.y2;
			
		}
		
		return result;
	}

	/**
	 * Define in terms of equals().
	 */
	@Override
	public boolean isomorphic(final Examinable obj) {
		return equals(obj);
	}

	/**
	 * Allow geometric translations.
	 */
	@Override
	public boolean equivalent(final Examinable obj) {
		// basics
		if(obj == null){
			return false;
		}
		boolean result = false;
		if(obj instanceof Line){
			Line toTest = (Line)obj;
			// compute deltas
			int dx1 = Math.abs(toTest.x1 - toTest.x2);
			int dx2 = Math.abs(this.x1 - this.x2);
			int dy1 = Math.abs(toTest.y1 - toTest.y2);
			int dy2 = Math.abs(this.y1 - this.y2);
			// are deltas equivalent?
			if (dx1 == dx2 && dy1 == dy2){
				result = true;
			}
		}

		return result;
		
	}
}
