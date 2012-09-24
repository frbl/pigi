package view;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.geom.AffineTransform;
import java.awt.geom.IllegalPathStateException;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import model.*;

public class Arena extends JPanel {
	
	public Arena(){
		
	}
	
	public void paint(Graphics g) {
		try{
			List<Element> elements = new ArrayList<Element>();
			elements.addAll(Element.ELEMENTS);
			Graphics2D g2 = (Graphics2D)g;
			for(Element e : elements){
				if(e.alive()){
					BufferedImage img = e.getImage();
					AffineTransform at = new AffineTransform();
					at.translate(e.getX() - img.getWidth()/2, e.getY() - img.getHeight()/2);
					at.rotate(Math.toRadians(e.getDirection()), img.getWidth()/2, img.getHeight()/2);
					g2.drawImage(img, at, null);
					
					Polygon poly = e.getPolygon();
					g2.drawPolygon(poly);
				}
			}
		} catch(IllegalPathStateException e) {} // Hier nog ff naar kijken
		catch(NullPointerException npe) {} // En hier ook 
	}
}
