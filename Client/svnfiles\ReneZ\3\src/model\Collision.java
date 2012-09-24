package model;

import java.awt.Polygon;
import java.util.ArrayList;
import java.util.List;

public class Collision {
	
	private Element element = null;
	
	public Collision(Element e){
		element = e;
	}
	
	public Element CollidingWith(){
		Polygon poly = element.getPolygon();
		List<Element> elements = new ArrayList<Element>();
		elements.addAll(Element.ELEMENTS);
		for(Element e : elements){
			try{
				if(!element.equals(e)) {
					Polygon otherPoly = e.getPolygon();
					for(int i = 0; i < otherPoly.npoints; i++){
						if(otherPoly.intersects(poly.xpoints[i], poly.ypoints[i], 1, 1)){
							return e;
						}
					}
				}
			} catch (Exception ex){}
		}
		return null;
	}
}
