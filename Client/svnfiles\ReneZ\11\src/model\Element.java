package model;

import java.awt.Color;
import java.awt.Polygon;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

public abstract class Element extends Thread {

	public static List<Element> ELEMENTS = new ArrayList<Element>();
	protected BufferedImage image = null;
	protected Collision collision = null;
	protected boolean alive = true;
	protected int width, height;
	protected double x, y, direction;
	protected int hitpoints;
	protected Polygon polygon = new Polygon();
	protected Color color = Color.GRAY;
	protected boolean invincible = false;
	
	public Element(int x, int y, double direction, int hitpoints){
		this.x = x;
		this.y = y;
		this.direction = direction;
		this.hitpoints = hitpoints;
		collision = new Collision(this);
		ELEMENTS.add(this);
		this.start();
	}
	
	protected abstract void colliding(Element e);
	protected abstract void checkAlive();
	
	public Polygon getPolygon(){
		return polygon;
	}
	
	/*
	 * Rotate and move cornerpoints
	 */
	public void updatePolygon(){
		double distance = Math.sqrt(Math.pow(getWidth()/2, 2) + Math.pow(getHeight()/2, 2));
		double angle = Math.sin((getHeight()/2)/(getWidth()/2));
		polygon.reset();
		polygon.npoints = 4;
		polygon.xpoints[0] = (int) (x + Math.cos(angle + Math.toRadians(getDirection())) * distance);
		polygon.ypoints[0] = (int) (y + Math.sin(angle + Math.toRadians(getDirection())) * distance);
		
		polygon.xpoints[1] = (int) (x + Math.cos((Math.toRadians(90) - angle) + Math.toRadians(getDirection() + 90)) * distance);
		polygon.ypoints[1] = (int) (y + Math.sin((Math.toRadians(90) - angle) + Math.toRadians(getDirection() + 90)) * distance);
		
		polygon.xpoints[2] = (int) (x + Math.cos(angle + Math.toRadians(getDirection() + 180)) * distance);
		polygon.ypoints[2] = (int) (y + Math.sin(angle + Math.toRadians(getDirection() + 180)) * distance);
		
		polygon.xpoints[3] = (int) (x + Math.cos((Math.toRadians(90) - angle) + Math.toRadians(getDirection() + 270)) * distance);
		polygon.ypoints[3] = (int) (y + Math.sin((Math.toRadians(90) - angle) + Math.toRadians(getDirection() + 270)) * distance);
	}
	
	protected void loadImage(String imgUrl){
		BufferedImage img = null;
		try {
			img = ImageIO.read(new File(imgUrl));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		width = img.getWidth();
		height = img.getHeight();
		image = img;
	}
	
	public BufferedImage getImage(){
		return image;
	}
	
	public double getX(){
		return x;
	}
	
	public double getY(){
		return y;
	}
	
	public double getWidth(){
		return width;
	}
	
	public double getHeight(){
		return height;
	}
	
	public double getDirection(){
		return direction;
	}
	
	public int getHitpoints(){
		return hitpoints;
	}
	
	public void setInvincible(boolean invincible){
		this.invincible = invincible;
	}
	
	public void setColor(Color color){
		this.color = color;
	}
	
	public Color getColor(){
		return color;
	}	
	
	public void hit(){
		hitpoints--;
		checkAlive();
	}
	
	public void die(){
		if(invincible) return;
		alive = false;
		ELEMENTS.remove(this);
	}
	
	public boolean alive(){
		return alive;
	}
}
