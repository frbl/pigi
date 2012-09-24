package view;

import javax.swing.JLabel;
import javax.swing.JPanel;

import controller.Controller;

public class ScoreBoard extends JPanel {
	
	private Controller controller = null;
	private JLabel labelHitpointsTank1 = new JLabel("test");
	private JLabel labelHitpointsTank2 = new JLabel("test2");
	
	public ScoreBoard(Controller controller){
		setLayout(null);
		this.controller = controller;
		labelHitpointsTank1.setLocation(10, 10);
		add(labelHitpointsTank1);
		add(labelHitpointsTank2);
		//updatePlayersHitpoints();
	}
	
	private void updatePlayersHitpoints(){
		String tank1Hitpoints = "Player 1: " + controller.getUserTank1().getHitpoints();
		labelHitpointsTank1.setText(tank1Hitpoints);
		
		String tank2Hitpoints = "Player 2: " + controller.getUserTank2().getHitpoints();
		labelHitpointsTank2.setText(tank2Hitpoints);
	}
}
