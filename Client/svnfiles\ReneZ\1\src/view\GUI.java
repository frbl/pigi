package view;

import javax.swing.JFrame;

import controller.Controller;

public class GUI extends JFrame implements Runnable {

	private Arena arena = new Arena();
	private boolean running = true;
	
	public GUI(Controller controller, int width, int height){
		setSize(width, height);
		add(arena);
		addKeyListener(controller.keyListener());
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public void run(){
		while(running){
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			repaint();
		}
	}
}
