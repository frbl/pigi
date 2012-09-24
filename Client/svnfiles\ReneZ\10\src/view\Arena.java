package view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.IllegalPathStateException;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import model.*;

public class Arena extends JPanel implements Runnable {

	private static final long serialVersionUID = 1L;
	private boolean running = true;
	
	public Arena(){
		startArenaThread();
	}
	
	private void startArenaThread(){
		Runnable arenaRunnable = this;
		Thread arenaThread = new Thread(arenaRunnable);
		arenaThread.start();
	}
	
	public void paint(Graphics g) {
		try{
			List<Element> elements = new ArrayList<Element>();
			elements.addAll(Element.ELEMENTS);
			Graphics2D g2 = (Graphics2D)g;
			g2.setColor(Color.LIGHT_GRAY);
			g2.fillRect(0, 0, this.getWidth(), this.getHeight());
			for(Element e : elements){
				if(e.alive()){
					BufferedImage img = e.getImage();
					AffineTransform at = new AffineTransform();
					at.translate(e.getX() - img.getWidth()/2, e.getY() - img.getHeight()/2);
					at.rotate(Math.toRadians(e.getDirection()), img.getWidth()/2, img.getHeight()/2);
					g2.drawImage(img, at, null);
					
					/*
					 * Uncomment the two lines below to see the polygons
					 * which are used to calculate collisions
					 */
					//g2.setColor(Color.BLACK);
					//Polygon poly = e.getPolygon();
					//g2.drawPolygon(poly);
				}
			}
		} catch(IllegalPathStateException e) {} // Hier nog ff naar kijken
		catch(NullPointerException npe) {} // En hier ook 
	}
	
	public void run(){
		while(running){
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			repaint();
		}
	}
}
