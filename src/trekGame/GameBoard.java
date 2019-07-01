package trekGame;
/*
 * Game board class that is used to hold the main map of the
 * galaxy.  The board size can by 8x8 (default), 16x16, or 24x24.
 * 
 */
public class GameBoard {
	public static int BOARDSIZE=16;
	public static int MAPGRIDSIZE=18;
	
	private JavaTrek game;
	private int[][] gameBoard=new int[BOARDSIZE][BOARDSIZE];
	private int klingonCount=0;
	private int myLocation=0;
	private int kSectors=0;
	private double starDate=3421;
	private double endDate=0;

	public GameBoard(JavaTrek g) {
		this.game=g;
		init(8);
	}
	
	
	public GameBoard(JavaTrek g, int size) {
		this.game=g;

		init(size);
	}
	

	// start a new game board
	private void init(int size) {
		int temp;
		
		switch(size) {
			case 8:
				BOARDSIZE=8;
				MAPGRIDSIZE=10;
				break;
			
			case 24:
				BOARDSIZE=24;
				MAPGRIDSIZE=26;
				break;
			
			default:
				// just use the 16
		}
	
		gameBoard=new int[BOARDSIZE][BOARDSIZE];
		
		// set up the game board
		for(int row=0;row<BOARDSIZE;row++) { 
			for (int col=0;col<BOARDSIZE;col++) {
				temp=(Utilities.diceRoll(100)>60?Utilities.diceRoll(100)/30+1:0); // 40% of 1-4 klingons in each cell
				klingonCount+=temp;		// total number of klingons
				kSectors+=(temp>0?1:0);  // number of sectors with klingons
				
				temp= (temp*10) 
					+(Utilities.diceRoll(100)+15)/20; // 0 - 5 stars in system, 5% chance of 0

				gameBoard[row][col]=temp;
			}
		}

		// add starbases - nothing in Delta Quadrant 
		gameBoard[Utilities.diceRoll(3)+2][Utilities.diceRoll(3)+2]+=100;

		// 16x16
		if(BOARDSIZE==16) {
			gameBoard[Utilities.diceRoll(4)+2][Utilities.diceRoll(4)+9]+=100;
			gameBoard[Utilities.diceRoll(4)+9][Utilities.diceRoll(4)+2]+=100;
			gameBoard[Utilities.diceRoll(4)+9][Utilities.diceRoll(4)+9]+=100;
		}
		
		// adding more for 24x24
		if(BOARDSIZE==24) {
			gameBoard[Utilities.diceRoll(3)+7][Utilities.diceRoll(3)+7]+=100;
			gameBoard[Utilities.diceRoll(3)+2][Utilities.diceRoll(3)+17]+=100;

			gameBoard[Utilities.diceRoll(3)+12][Utilities.diceRoll(3)+2]+=100;
			gameBoard[Utilities.diceRoll(3)+17][Utilities.diceRoll(3)+7]+=100;
			
			gameBoard[Utilities.diceRoll(3)+12][Utilities.diceRoll(3)+12]+=100;
			gameBoard[Utilities.diceRoll(3)+17][Utilities.diceRoll(3)+17]+=100;
		}

		this.endDate+=this.starDate+kSectors;
	}
	
	
	public void randomEnterpriseLocation() {
		// set up starting location away from any klingons
		setMyLocation((Utilities.diceRoll(GameBoard.BOARDSIZE)-1)*GameBoard.BOARDSIZE+Utilities.diceRoll(GameBoard.BOARDSIZE/2));

		int a=getGameBoard();
		Utilities.writeToLog("a%100="+(a%100));

		while(a%100>9) {
			setMyLocation((Utilities.diceRoll(GameBoard.BOARDSIZE)-1)*GameBoard.BOARDSIZE+Utilities.diceRoll(GameBoard.BOARDSIZE/2));
			a=getGameBoard();
			Utilities.writeToLog("a%100="+(a%100));
		}
	}
	
	
	public String getLocation() {
		return ""+myLocation/BOARDSIZE+","+myLocation%BOARDSIZE;
	}

	
	public void starDateAdd(Double t) {
		this.starDate+=t;
	}

	
	public double currentStarDate() {
		return starDate;
	}

	
	public double timeLeft() {
		return this.endDate-this.starDate;
	}


	// Removing an asteriod, klingon, or starbase
	// from the indicated location on the board
	public void removeObjectFromBoard(int object) {
		removeObjectFromBoard(object,this.myLocation);
	}

	
	// Removing an asteroid, klingon, or starbase
	// from the indicated location on the board
	public void removeObjectFromBoard(int object, int loc) {
		if(object==2) 
			klingonCount--;
		
		Utilities.writeToLog(""+klingonCount+" klingons left");
		
		gameBoard[loc/BOARDSIZE][loc%BOARDSIZE]-=(object==1?1:(object==2?10:100));
	}

	
	// Adding an asteriod, klingon, or starbase
	// from the indicated location on the board
	public void addObjectToBoard(int object, int loc) {
		if(object==2) 
			klingonCount++;
		
		gameBoard[loc/BOARDSIZE][loc%BOARDSIZE]+=(object==1?1:(object==2?10:100));
	}

	
	// Return the quadrant and sector
	public String[] getLocationInfo() {
		int row=this.myLocation/GameBoard.BOARDSIZE;
		int col=this.myLocation%GameBoard.BOARDSIZE;
				
		String [] results=new String[2];
		results[0]=getQuadName(row, col);
		results[1]="("+(row%8+1)+","+(col%8+1)+")";
		return results;
	}
	

	/* 
	 * Return the name of the quadrant based on board row & column
	 * 8x8 board is just alpha quadrant, while 16x16 follows the
	 * Star Trek designation.  24x24 throws in some constellation
	 * names to round out the list
	 */
	public String getQuadName(int row, int col) {
		String response;
		
		if(BOARDSIZE==8) {
			response="Alpha";
		}
		else {
			String[] names = {"Gamma","Delta","Alpha","Beta"};
			response=names[(row/(BOARDSIZE/2))*2+(col/(BOARDSIZE/2))];
		}
		
		return response;
	}

	
	/* 
	 * Population is returned in the following format:
	 * 	Starbase count *100 + Klingon count *10 + asteroid count
	 *	Example: 132 = 1 Starbase, 3 Klingons, 2 Asteroids
	 * 
	 * @return (int) population of the current location of the Enterprise
	 * 
	 */
	public int getGameBoard() {
		int response=getGameBoard(this.myLocation/BOARDSIZE,this.myLocation%BOARDSIZE);
		return response;
	}

	
	/*
	 * @param (int) row of game board
	 * @param (int) col of game board
	 * @return (int) the gameBoard population based on row,col
	 * 
	 */
	public Integer getGameBoard(int row, int col) {
		int response=-1;
		if(row>=0 && row<BOARDSIZE && col>=0 && col<BOARDSIZE) {
			response=gameBoard[row][col];
		}
		return response;
	}
	

	/*
	 * @param (int) location value of game board
	 * @return (int) population of game board location
	 * 
	 */
	public void setGameBoard(int value) {
		gameBoard[this.myLocation/BOARDSIZE][this.myLocation%BOARDSIZE]=value;
	}


	/*
	 * @return the location of Enterprise
	 * 
	 */
	public int getMyLocation() {
		return myLocation;
	}
	

	/*
	 * @param myLocation setter
	 */
	public void setMyLocation(int myLocation) {
		if(myLocation>=0 && myLocation<BOARDSIZE*BOARDSIZE) {
			game.myMap.EnterpriseMapMove(this.myLocation, myLocation);
			this.myLocation = myLocation;
		}
	}
	

	/*
	 * @return number of klingons on the board
	 * 
	 */
	public int getKlingonCount() {
		return klingonCount;
	}
	
}
