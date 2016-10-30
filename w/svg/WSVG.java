package ece351.w.svg;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.batik.dom.svg.SAXSVGDocumentFactory;
import org.apache.batik.util.XMLResourceDescriptor;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import ece351.util.Examinable;
import ece351.util.Examiner;

final class WSVG implements Examinable {

	final List<Pin> pins;
	final List<Line> segments;

	private WSVG(final List<Pin> pins, final List<Line> segments) {
		this.pins = pins;
		this.segments = segments;
	}
	
	static WSVG fromSVG(final URI uri, final boolean parseDOM) throws Exception {
		final List<Pin> pins = new ArrayList<Pin>();
		final List<Line> segments = new ArrayList<Line>();
		try {
			final Document doc;
			if (parseDOM) {
			    doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(uri.toString());
			} else {
		    	SAXSVGDocumentFactory f = new SAXSVGDocumentFactory(XMLResourceDescriptor.getXMLParserClassName());
		    	doc = f.createDocument(uri.toString());
			}
		    
		    for (Node node = doc.getDocumentElement().getFirstChild(); 
		    		node != null; 
		    		node = node.getNextSibling()) {
		    	if (node.getNodeType() == Node.ELEMENT_NODE) {
		    		final Pin p = Pin.fromSVG(node);
		    		if (p != null) {
		    			pins.add(p);
		    		}
		    		final Line line = Line.fromXML(node);
		    		if (line != null) {
						// ignore dots (i.e., lines of zero length)
						if (!(line.x1 == line.x2 && line.y1 == line.y2)) {
							// it's not a dot, so keep it
							segments.add(line);
						}
		    		}
		    	}
		    }
		} catch (IOException e) {
		    throw e;
		} catch (Exception e) {
			throw e;
		}
		return new WSVG(Collections.unmodifiableList(pins), Collections.unmodifiableList(segments));
	}
	
	/**
	 * If we override equals() then we must override hashCode().
	 */
	@Override
	public int hashCode() {
		int hash = 31;
		hash = hash * 13 + pins.hashCode();
		hash = hash * 13 + segments.hashCode();
		return hash;
	}

	/**
	 * WSVG objects are immutable, and so may be Liskov substitutable.
	 */
	@Override
	public boolean equals(final Object obj) {
		// basics
		if (obj == null){
			return false;
		}
		boolean result = false;
		if(obj instanceof WSVG){
			// compare pins and lines with Examiner.orderedExamination()
			// no significant differences, return true
			WSVG toTest = (WSVG)obj;
			result = Examiner.orderedExamination(Examiner.Equals, this.pins, toTest.pins) && Examiner.orderedExamination(Examiner.Equals, this.segments, toTest.segments);
		}
		return result;
	}

	/**
	 * Same coordinates, possibly different orderings of elements in SVG.
	 */
	@Override
	public boolean isomorphic(final Examinable obj) {
		// basics
		if (obj == null){
			return false;
		}
		boolean result = false;
		if(obj instanceof WSVG){
			// compare pins and lines with Examiner.unorderedExamination()
			// no significant differences, return true
			WSVG toTest = (WSVG)obj;
			result = Examiner.unorderedExamination(Examiner.Isomorphic, this.pins, toTest.pins) && Examiner.unorderedExamination(Examiner.Equals, this.segments, toTest.segments);
		}
		return result;
	}

	/**
	 * Allow geometric translations.
	 */
	@Override
	public boolean equivalent(final Examinable obj) {
		// basics
		if (obj == null){
			return false;
		}
		boolean result = false;
		if(obj instanceof WSVG){
			// compare pins and lines with Examiner.unorderedExamination()
			// no significant differences, return true
			WSVG toTest = (WSVG)obj;
			result = Examiner.unorderedExamination(Examiner.Equivalent, this.pins, toTest.pins) && Examiner.unorderedExamination(Examiner.Equivalent, this.segments, toTest.segments);
		}
		return result;
	}
	
	
	/**
	 * Minimize an SVG file by eliminating zero length line segments and merging
	 * adjacent line segments.
	 * 
	 * @param originalWSVG
	 * @return minimized version of original SVG
	 */
	static WSVG minimalWSVG( final WSVG originalWSVG ) {
		final List<Line> segments = new ArrayList<Line>();
		
		for ( final Line originalLine : segments ) {
			// Ignore zero-length lines.
			if ( originalLine.x1 == originalLine.x2 && originalLine.y1 == originalLine.y2 ) {
				continue;
			}

			// Simplify checks later on by requiring the first end point to be "smaller" than the second 
			// end point(i.e. if the first end point has a smaller y-coordinate, or if it has same 
			// y-coordinate as the second end point but has a smaller x-coordinate).
			Line line = ( originalLine.x1 < originalLine.x2 || 
						( originalLine.x1 == originalLine.x2 && originalLine.y1 < originalLine.y2 ) ) ?
						new Line( originalLine.x1, originalLine.y1, originalLine.x2, originalLine.y2 ) : 
						new Line( originalLine.x2, originalLine.y2, originalLine.x1, originalLine.y1 );
			
			// Assuming that there are no overlapping line segments (intersections at single points 
			// excluded), then a line segment can only be merged with at most two other existing line 
			// segments, one at each end point. This loop searches for these two potential line 
			// segments. The line segment that has yet to be added to the minimal WSVG object is simply 
			// discarded if it can be merged with an existing line segment in the minimal WSVG object. 
			// If strictly two potential line segments can be merged with the new line segment, 
			// the first one that is found is kept and recreated as the newly merged line segment, 
			// while the second one is removed from the minimal WSVG object that is to be returned.
			int merged_pos = -1;
			for ( int i = 0; i < segments.size(); ++i ) {
				final Line l = segments.get( i );
				if ( l.y1 == l.y2 && line.y1 == line.y2 && l.y1 == line.y1 && 
					 ( l.x1 == line.x2 || l.x2 == line.x1 ) ) {
					// Handles merger of horizontal line segments.
					if ( merged_pos == -1 ) {
						segments.set( i, new Line( Math.min( l.x1, line.x1 ), l.y1, 
									  Math.max( l.x2, line.x2 ), l.y1 ) );
						line = segments.get( i );
						merged_pos = i;
					} else {
						segments.set( merged_pos, new Line( Math.min( l.x1, line.x1 ), l.y1, 
										  Math.max( l.x2, line.x2 ), l.y1 ) );
						segments.remove( i );
						break;
					}
				} else if ( l.x1 == l.x2 && line.x1 == line.x2 && l.x1 == line.x1 && 
							( l.y1 == line.y2 || l.y2 == line.y1 ) ) {
					// Handles merger of vertical line segments.
					if ( merged_pos == -1 ) {
						segments.set( i, new Line( l.x1, Math.min( l.y1, line.y1 ), l.x2, 
									  Math.max( l.y2, line.y2 ) ) );
						line = segments.get( i );
						merged_pos = i;
					} else {
						segments.set( merged_pos, new Line( l.x1, Math.min( l.y1, line.y1 ), 
									  l.x2, Math.max( l.y2, line.y2 ) ) );
						segments.remove( i );
						break;
					}
				}
			}
			
			// If required, add input line segment to minimal WSVG object.
			if ( merged_pos == -1 ) {
				segments.add(line);
			}
		}
		
		return new WSVG( originalWSVG.pins, Collections.unmodifiableList( segments ) );
	}
	
}
