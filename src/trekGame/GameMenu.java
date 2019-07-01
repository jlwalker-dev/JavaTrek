package trekGame;

import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

@SuppressWarnings("serial")
public class GameMenu extends JMenuBar {
	public GameMenu(ActionListener actionListener) {
		// create a menu
		JMenu gameMenu=new JMenu("Game");
		JMenuItem gameItem1 = new JMenuItem("Instructions");
		gameItem1.addActionListener(actionListener);
		gameMenu.add(gameItem1);
		
		JMenuItem gameItem2 = new JMenuItem("Targeting Calculator");
		gameItem2.addActionListener(actionListener);
		gameMenu.add(gameItem2);

		JMenuItem gameItem3 = new JMenuItem("Warp Calculator");
		gameItem3.addActionListener(actionListener);
		gameMenu.add(gameItem3);

		JMenuItem gameItem9 = new JMenuItem("Exit");
		gameItem9.addActionListener(actionListener);
		gameMenu.add(gameItem9);
		
		this.add(gameMenu);
	}


}
