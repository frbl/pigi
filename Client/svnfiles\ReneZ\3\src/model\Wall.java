package model;

import controller.Controller;

public class Wall extends NotMoveable {
	
	public Wall(Controller controller, int x, int y, int direction, int hitpoints){
		super(controller, x, y, direction, hitpoints);
		loadImage("./graphics/wall.png");
		this.controller = controller;
		updatePolygon();
	}

	public void checkAlive() {
		if(hitpoints <= 0) die();
	}

	public void colliding(Element e) {
		
	}
}
