package model;

import sound.Sound;

public class Shot extends Moveable {
	
	public Shot(double x, double y, double direction, int hitpoints, double speed){
		super((int)x, (int)y, direction, hitpoints, speed);
		loadImage("graphics/bullet.png");
		Sound.instanceOf().playShotSound();
		_forward = true;
		updatePolygon();
	}
	
	protected void checkAlive() {
		if(!insideArena() || hitpoints <= 0) die();
	}

	protected void colliding(Element e) {
		die();
		e.hit();
	}
}
