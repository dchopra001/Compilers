package ece351.w.svg;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import ece351.util.Examinable;

final class Pin implements Examinable {
	
	final static String TextX = "x";
	final static String TextY = "y";

	final String id;
	final int x, y;

	Pin(final String id, final int x, final int y) {
		this.id = id;
		this.x = x;
		this.y = y;
		assert repOk();
	}
	
	public boolean repOk() {
		assert id != null : "id is null";
		return true;
	}

	@Override
	public String toString() {
		return id + " (" + x + "," + y + ")";
	}
	
	public String toSVG() {
		return "<text x=\"" + x + "\" y=\"" + y + "\">" + id + "</text>";
	}
	
	public static String toSVG(final String id, final int x, final int y) {
		final Pin p = new Pin(id, x, y);
		return p.toSVG();
	}
	
	public static Pin fromSVG(final Node node) {
		String nodeName = node.getNodeName();
		if (nodeName.equals("text") && node.hasAttributes() && node.getChildNodes().getLength() == 1) {
			final NamedNodeMap nnMap = node.getAttributes();
			if (nnMap.getNamedItem(TextX) != null && nnMap.getNamedItem(TextY) != null && node.getFirstChild().getNodeValue() != null) {
				final int x = Integer.parseInt(nnMap.getNamedItem(TextX).getNodeValue());
				final int y = Integer.parseInt(nnMap.getNamedItem(TextY).getNodeValue());
				return new Pin(node.getFirstChild().getNodeValue(), x, y);
			}
		}
		return null;
	}

	/**
	 * If we override equals() then we must override hashCode().
	 */
	@Override
	public int hashCode() {
		int hash = 13;
		hash = hash * 17 + id.hashCode();
		hash = hash * 17 + x;
		hash = hash * 17 + y;
		return hash;
	}
	
	/**
	 * Pins are immutable, so one could be equals to another if they
	 * have the same field values.
	 */
	@Override
	public boolean equals(final Object obj) {
		// basics
		if (obj == null){
			return false;
		}
		boolean result = false;
		if(obj instanceof Pin){
			Pin toTest = (Pin)obj;
			result = toTest.id.equals(this.id) && toTest.x == this.x && toTest.y == this.y;
			
		}
		
		return result;
	}

	/**
	 * Requires same coordinates, so define in terms of equals().
	 */
	@Override
	public boolean isomorphic(Examinable obj) {
		return equals(obj);
	}

	/**
	 * Allows geometric translations.
	 */
	@Override
	public boolean equivalent(Examinable obj) {
		// basics
		if (obj == null){
			return false;
		}
		boolean result = false;
		if(obj instanceof Pin){
			Pin toTest = (Pin)obj;
			result = toTest.id.equals(this.id);
		}
		return result;
	}
}
