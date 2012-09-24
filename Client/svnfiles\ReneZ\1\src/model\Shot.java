package model;

import sound.Sound;
import controller.Controller;

public class Shot extends Moveable {
	
	public Shot(Controller controller, double x, double y, double direction, int hitpoints, double speed){
		super(controller, (int)x, (int)y, direction, hitpoints, speed);
		loadImage("./graphics/bullet.png");
		Sound.instanceOf().playShotSound();
		_forward = true;
		createPolygon();
	}
	
	protected void checkAlive() {
		if(!insideArena() || hitpoints <= 0) die();
	}

	protected void colliding(Element e) {
		die();
		e.hit();
	}
}
