package controller;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import model.*;
import sound.Sound;
import view.*;

public class Controller {

	private static int arenaWidth = 1000;
	private static int arenaHeight = 700;
	private boolean up1, down1, right1, left1, shoot1, up2, down2, right2, left2, shoot2 = false;
	private Tank userTank1 = null;
	private Tank userTank2 = null;
	
	public Controller(){
		new GUI(this, getArenaWidth(), getArenaHeight());
		createGameFieldElements();
		Sound.instanceOf().playBattlefieldSound();
	}
	
	private void createGameFieldElements(){
		userTank1 = new Tank(300, 300, 0, 10, 2);
		userTank2 = new Tank(600, 600, 0, 10, 2);
		userTank1.setColor(Color.DARK_GRAY);
		userTank2.setColor(Color.LIGHT_GRAY);
		for(int i = 0; i < getArenaHeight(); i = i + 49){
			new Wall(getArenaWidth()/2, i, 0, 30);
		}
		new Wall(40, 400, 30, 30);
		new Wall(800, 400, 192, 30);
		new Wall(50, 100, 39, 30);
		new Wall(400, 500, 256, 30);
		new Wall(250, 600, 394, 30);
		new Wall(900, 300, 105, 30);
		new Wall(700, 100, 12, 30);
		new Wall(300, 250, 29, 30);
	}
	
	public Tank getUserTank1(){
		return userTank1;
	}
	
	public Tank getUserTank2(){
		return userTank2;
	}
	
	public KeyListener keyListener(){
		return new KeyListener(){
			public void keyPressed(KeyEvent e) {
				//Listeners for tank 1
				if(userTank1.alive()){
					if(e.getKeyCode() == KeyEvent.VK_UP){
						if(up1) return;
						userTank1.forward(true);
						up1 = true;
					} else if(e.getKeyCode() == KeyEvent.VK_RIGHT){
						if(right1) return;
						userTank1.right(true);
						right1 = true;
					} else if(e.getKeyCode() == KeyEvent.VK_DOWN){
						if(down1) return;
						userTank1.back(true);
						down1 = true;
					} else if(e.getKeyCode() == KeyEvent.VK_LEFT){
						if(left1) return;
						userTank1.left(true);
						left1 = true;
					} else if(e.getKeyCode() == KeyEvent.VK_NUMPAD3){
						if(shoot1) return;
						userTank1.shoot();
						shoot1 = true;
					}
				}
				
				//Listeners for tank 2
				if(userTank2.alive()){
					if(e.getKeyCode() == KeyEvent.VK_W){
						if(up2) return;
						userTank2.forward(true);
						up2 = true;
					} else if(e.getKeyCode() == KeyEvent.VK_A){
						if(left2) return;
						userTank2.left(true);
						left2 = true;
					} else if(e.getKeyCode() == KeyEvent.VK_S){
						if(down2) return;
						userTank2.back(true);
						down2 = true;
					} else if(e.getKeyCode() == KeyEvent.VK_D){
						if(right2) return;
						userTank2.right(true);
						right2 = true;
					} else if(e.getKeyCode() == KeyEvent.VK_B){
						if(shoot2) return;
						userTank2.shoot();
						shoot2 = true;
					}
				}
			}
			public void keyReleased(KeyEvent e) {
				//Listeners for tank 1
				if(userTank1.alive()){
					if(e.getKeyCode() == KeyEvent.VK_UP){
						userTank1.forward(false);
						up1 = false;
					} else if(e.getKeyCode() == KeyEvent.VK_RIGHT){
						userTank1.right(false);
						right1 = false;
					} else if(e.getKeyCode() == KeyEvent.VK_LEFT){
						userTank1.left(false);
						left1 = false;
					} else if(e.getKeyCode() == KeyEvent.VK_DOWN){
						userTank1.back(false);
						down1 = false;
					} else if(e.getKeyCode() == KeyEvent.VK_NUMPAD3){
						userTank1.back(false);
						shoot1 = false;
					}
				}
				
				//Listeners for tank 2
				if(userTank2.alive()){
					if(e.getKeyCode() == KeyEvent.VK_W){
						userTank2.forward(false);
						up2 = false;
					} else if(e.getKeyCode() == KeyEvent.VK_A){
						userTank2.left(false);
						left2 = false;
					} else if(e.getKeyCode() == KeyEvent.VK_S){
						userTank2.back(false);
						down2 = false;
					} else if(e.getKeyCode() == KeyEvent.VK_D){
						userTank2.right(false);
						right2 = false;
					} else if(e.getKeyCode() == KeyEvent.VK_B){
						shoot2 = false;
					}
				}
			}
			public void keyTyped(KeyEvent e) {}
		};
	}
	
	public static int getArenaWidth(){
		return arenaWidth;
	}
	
	public static int getArenaHeight(){
		return arenaHeight;
	}
	
	public static void main(String[] args){
		new Controller();
	}
}
