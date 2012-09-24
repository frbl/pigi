package model;

import controller.Controller;

public abstract class Moveable extends Element {
	
	protected boolean _forward, _right, _left, _back = false;
	private double _rotatingSpeed = 2;
	protected double _speed;
	
	public Moveable(Controller controller, int x, int y, double direction, int hitpoints, double speed){
		super(controller, x, y, direction, hitpoints);
		_speed = speed;
	}
	
	public void run(){
		while(alive){
			try {
				sleep(10);
			} catch (InterruptedException e) {}
			move();
			checkAlive();
		}
	}
	
	protected boolean insideArena(){
		if(x < 0 || x > controller.getArenaWidth() ||
				y < 0 || y > controller.getArenaHeight()) {
			return false;
		}
		return true;
	}
	
	protected void move(){
		if(_forward){
			goForward();
			Element collidingElement = collision.CollidingWith();
			if(collidingElement != null){
				colliding(collidingElement);
				goBack();
			}
		}
		if(_back){
			goBack();
			Element collidingElement = collision.CollidingWith();
			if(collidingElement != null){
				colliding(collidingElement);
				goForward();
			}
		}
		if((_right && _forward) || (_left && _back)){
			goRight();
		}
		if((_left && _forward) || (_right && _back)){
			goLeft();
		}
	}
	
	protected void stopMoving(){
		_forward  = false;
		_back = false;
		_left = false; 
		_right = false;
	}
	
	private void goForward(){
		x += Math.cos(Math.toRadians(direction)) * _speed;
		y += Math.sin(Math.toRadians(direction)) * _speed;
		updatePolygon();
	}
	
	protected void goBack(){
		x -= Math.cos(Math.toRadians(direction)) * _speed;
		y -= Math.sin(Math.toRadians(direction)) * _speed;
		updatePolygon();
	}
	
	private void goLeft(){
		double direction = this.direction - _rotatingSpeed;
		if(direction < 0)
			direction = direction + 360;
		this.direction = direction;
		updatePolygon();
	}
	
	private void goRight(){
		double direction = this.direction + _rotatingSpeed;
		if(direction > 360)
			direction = direction - 360;
		this.direction = direction;
		updatePolygon();
	}
}
