package model;

public class Wall extends NotMoveable {
	
	public Wall(int x, int y, int direction, int hitpoints){
		super(x, y, direction, hitpoints);
		loadImage("graphics/wall.png");
		updatePolygon();
	}

	public void checkAlive() {
		if(hitpoints <= 0) die();
	}

	public void colliding(Element e) {
		
	}
}
