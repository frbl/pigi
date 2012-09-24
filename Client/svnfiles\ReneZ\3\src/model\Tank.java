package model;

import controller.Controller;

public class Tank extends Moveable {
	
	private double bulletSpeed = 3;
	private int bulletHitpoints = 1;
	
	public Tank(Controller controller, int x, int y, int direction, int hitpoints, double speed){
		super(controller, x, y, direction, hitpoints, speed);
		loadImage("./graphics/tank1.png");
		updatePolygon();
	}
	
	protected void checkAlive() {
		if(!insideArena() || hitpoints <= 0) die();
	}
	
	protected void colliding(Element e){
		stopMoving();
	}
	
	public void shoot(){
		double x = this.x + Math.cos(Math.toRadians(direction))*((width/2)+4);
		double y = this.y + Math.sin(Math.toRadians(direction))*((width/2)+4);
		new Shot(controller, x, y, direction, bulletHitpoints, bulletSpeed);
	}
	
	public void forward(boolean pressed){
		_forward = pressed;
	}
	
	public void right(boolean pressed){
		_right = pressed;
	}
	
	public void left(boolean pressed){
		_left = pressed;
	}
	
	public void back(boolean pressed){
		_back = pressed;
	}
}
