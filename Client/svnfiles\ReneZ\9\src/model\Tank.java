package model;

public class Tank extends Moveable {
	
	private double bulletSpeed = 3;
	private int bulletHitpoints = 1;
	
	public Tank(int x, int y, int direction, int hitpoints, double speed){
		super(x, y, direction, hitpoints, speed);
		loadImage("graphics/tank1.png");
		updatePolygon();
	}
	
	protected void checkAlive() {
		if(!insideArena() || hitpoints <= 0) die();
	}
	
	protected void colliding(Element e){
		stopMoving();
	}
	
	public void shoot(){
		double x = this.x + Math.cos(Math.toRadians(direction))*((width/2) + 10);
		double y = this.y + Math.sin(Math.toRadians(direction))*((width/2) + 10);
		new Shot(x, y, direction, bulletHitpoints, bulletSpeed);
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
