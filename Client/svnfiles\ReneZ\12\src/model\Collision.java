package model;

import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.List;

public class Collision {
	
	private Element element = null;
	
	public Collision(Element e){
		element = e;
	}
	
	public Element CollidingWith(){
		List<Element> elements = new ArrayList<Element>();
		elements.addAll(Element.ELEMENTS);
		for(Element e : elements){
			try{
				if(!element.equals(e)) {
					Area area1 = new Area(element.getPolygon());
					Area area2 = new Area(e.getPolygon());
					area1.intersect(area2);
					if(!area1.isEmpty()){
						return e;
					}
				}
			} catch (Exception ex){}
		}
		return null;
	}
}
