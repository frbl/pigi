package view;

import javax.swing.JFrame;

import controller.Controller;

public class GUI extends JFrame {
	
	private int width;
	private int arenaHeight;
	private int scoreBoardHeight = 300;
	private Arena arena = null;
	private ScoreBoard scoreBoard = null;
	private Controller controller = null;
	
	public GUI(Controller controller, int guiWidth, int arenaHeight){
		setLayout(null);
		setSize(guiWidth, arenaHeight + scoreBoardHeight);
		this.controller = controller;
		this.width = guiWidth;
		this.arenaHeight = arenaHeight;
		initArena();
		initScoreBoard(arenaHeight);
		addKeyListener(controller.keyListener());
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	private void initArena(){
		arena = new Arena();
		arena.setBounds(0, 0, width, arenaHeight);
		add(arena);
	}
	
	private void initScoreBoard(int top){
		scoreBoard = new ScoreBoard(controller);
		scoreBoard.setBounds(0, arenaHeight, width, scoreBoardHeight);
		add(scoreBoard);
	}
}
