package model;

import controller.Controller;

public abstract class NotMoveable extends Element {

	public NotMoveable(Controller controller, int x, int y, double direction, int hitpoints){
		super(controller, x, y, direction, hitpoints);
	}
}
