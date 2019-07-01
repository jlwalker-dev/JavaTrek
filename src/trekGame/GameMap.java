package trekGame;
import java.awt.Color;

import javax.swing.JLabel;

/*
 * This class contains the user's map of explored sectors.
 * Sectors are explored by visiting them, using LRS to scan
 * the adjacent sectors, receiving updates from starbases, or
 * shooting probes through the galaxy (future update).
 * 
 */
public class GameMap {
	JavaTrek game;
	private int[][] myMap;
	private JLabel[][] mapLabels;
	
	/* 
	 * Constructor to create a blank map
	 * 
	 */
	public GameMap(JavaTrek g) {
		this.game=g;
		
		// create a blank map
		myMap=new int[GameBoard.BOARDSIZE][GameBoard.BOARDSIZE];
		mapLabels=new JLabel[GameBoard.BOARDSIZE][GameBoard.BOARDSIZE];
		
		for(int i=0;i<GameBoard.BOARDSIZE;i++) {
			for(int j=0;j<GameBoard.BOARDSIZE;j++) {
				myMap[i][j]= -1;
			}
		}
	}

	
	/*
	 * @return the myMap population based on row,col position
	 */
	public int getMyMap(int row, int col) {
		if(row>=0 && row<GameBoard.BOARDSIZE && col>=0 && col<GameBoard.BOARDSIZE)
			return myMap[row][col];
		else
			return -2;
	}


	/*
	 * Place the population of a cell onto the user's map
	 * 
	 * @param (int) row
	 * @param (int) col
	 * @param (int) population to put into myMap cell
	 * 
	 */
	public void setMyMap(int row, int col, int value) {
		if(row>=0 && row<GameBoard.BOARDSIZE && col>=0 && col<GameBoard.BOARDSIZE) {
			this.myMap[row][col]=value;
			String strVal=String.format("%03d",value);
			
			if(mapLabels[row][col]==null) {
				Utilities.writeToLog("mapLabels["+row+"]["+col+"] is null");
			}
			else {
				mapLabels[row][col].setText(strVal);
				mapLabels[row][col].setForeground((value>99?(value%100>9?Color.RED:Color.BLUE):(value>9?Color.BLACK:Color.GRAY)));
			}
		}
	}

	
	/*
	 * Assign a label to the mapLabels array
	 */
	public void setMapLabels(int row, int col, JLabel jlabel) {
		if(row>=0 && row<GameBoard.BOARDSIZE && col>=0 && col<GameBoard.BOARDSIZE) {
			mapLabels[row][col]=jlabel;
		}
	}


	/*
	 * Return access to the JLabel stored in the mapLabels array
	 */
	public JLabel getMapLabels(int row, int col) {
		if(row>=0 && row<GameBoard.BOARDSIZE && col>=0 && col<GameBoard.BOARDSIZE) {
			return mapLabels[row][col];
		}
		
		return null;
	}
	
	
	/*
	 * Change the colors of the labels for movement of the Enterprise
	 * 
	 */
	public void EnterpriseMapMove(int locFrom, int locTo) {
		int rowFrom=locFrom/GameBoard.BOARDSIZE;
		int colFrom=locFrom%GameBoard.BOARDSIZE;
		int rowTo=locTo/GameBoard.BOARDSIZE;
		int colTo=locTo%GameBoard.BOARDSIZE;

		if(rowFrom>=0 && rowFrom<GameBoard.BOARDSIZE && colFrom>=0 && colFrom<GameBoard.BOARDSIZE) {
			mapLabels[rowFrom][colFrom].setOpaque(false);
			mapLabels[rowFrom][colFrom].setBackground(Color.WHITE);
		}

		if(rowTo>=0 && rowTo<GameBoard.BOARDSIZE && colTo>=0 && colTo<GameBoard.BOARDSIZE) {
			mapLabels[rowTo][colTo].setOpaque(true);
			mapLabels[rowTo][colTo].setBackground(Color.CYAN);
		}
		
	}
}