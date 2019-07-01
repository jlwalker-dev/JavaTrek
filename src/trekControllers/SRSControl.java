package trekControllers;

import trekGame.JavaTrek;

import javax.swing.JLabel;

import trekGame.GameObjects;
import trekGame.Utilities;


/*
 * Short Range Sensors controller
 * 
 * When the SRS executes, it gets the population of this sector and randomly
 * places items onto the SRS grid.  It also builds a list of all objects
 * so that it can manage the damage and create movement tracks of each piece.
 * 
 */
public class SRSControl extends ControllerSuperClass implements ControllerInterface {
	public static final int SRSGRIDSIZE=10;
	
	private int[][] shortRangeSensors=new int[SRSGRIDSIZE][SRSGRIDSIZE];
	private JLabel[][] srsLabels=new JLabel[SRSGRIDSIZE][SRSGRIDSIZE];
	private int myRow,myCol;

	/*
	 * Constructor to initialize the interface
	 */
	public SRSControl(JavaTrek g) {
		super(g);
		super.init("Short Range Sensors", 12, 200);
	
		// set up a JLabel array so the SRS display can be
		// altered quickly by just updating the correct
		// JLabel element as a change occurs
		for(int r=0;r<SRSGRIDSIZE;r++) {
			for(int c=0;c<SRSGRIDSIZE;c++) {
				srsLabels[r][c]=new JLabel(".",JLabel.CENTER);
			}
		}
	}

	
	// set up the grid upon entering.  First place the
	// asteroids with a spacing requirement.  Then add
	// the starbase, Klingons, and finally, Enterprise.
	@Override
	public Boolean execute() {
		game.gameObjects.clearObjects();
		setGrid(-1,-1);
		return true;
	}
	

	/*
	 * Set up the SRS map for the sector, randomly placing objects
	 * onto the board with certain pieces requiring space between
	 * other objects when placing.
	 * 
	 * If invalid row/col information is passed in, the Enterprise
	 * will be randomly placed onto the board.
	 */
	public void setGrid(int row, int col) {
		int count,attempts;
		int population = game.myBoard.getGameBoard();

		// clear the array
		for(int r=0;r<SRSGRIDSIZE;r++) { 
			for (int c=0;c<SRSGRIDSIZE;c++) {
				setShortRangeSensors(r,c,0);
			}
		}
		
		// Place the Enterprise
		if(row<0 || col<0) {
			// don't randomly put the Enterprise near the edges
			this.myRow=Utilities.diceRoll(SRSGRIDSIZE-2);
			this.myCol=Utilities.diceRoll(SRSGRIDSIZE-2);
		}
		else {
			// must have moved from a different sector
			this.myRow=row;
			this.myCol=col;
		}
			
		addStarObject(GameObjects.ENTERPRISE,myRow,myCol,0);

		
		// Add asteroids, make sure they are next to each other
		count=population % 10;
		attempts=0;
		while(count>0 && attempts<100) {
			attempts++;
			row=Utilities.diceRoll(SRSGRIDSIZE-2);  // don't want asteroids on the edges
			col=Utilities.diceRoll(SRSGRIDSIZE-2);

			if (addStarObject(GameObjects.ASTEROID,row,col,1))
				count--;
		}

		// add starbase
		while(population>99) {
			row=Utilities.diceRoll(SRSGRIDSIZE-2);
			col=Utilities.diceRoll(SRSGRIDSIZE-2); // don't want the starbase on the edges

			// don't put the starbase near anything
			if (addStarObject(GameObjects.STARBASE,row,col,2))
				break;
		}
		
		// klingons
		count=(population % 100) / 10;
		while(count>0) {
			row=Utilities.diceRoll(SRSGRIDSIZE)-1;
			col=Utilities.diceRoll(SRSGRIDSIZE)-1;

			if (addStarObject(GameObjects.KLINGON,row,col,0))
				count--;
		}
	}

	

	
	/*
	 *  Add an object to the starObjects list and place it into the 
	 * SRS map at the requested coordinates.  Optionally require a certain
	 * amount of distance between other objects.  Return true if placement
	 *  was successful.
	 *  
	 */
	public boolean addStarObject(int type, int row, int col, int distance) {
		boolean ok2Place=true;
		
		if (shortRangeSensors[row][col]==0) {
			if (distance>0) {
				for(int r=row-distance;r<=row+distance;r++) {
					for(int c=col-distance;c<=col+distance;c++) {
						if(r>=0 && r<trekGame.GameBoard.BOARDSIZE && c>=0 && c<trekGame.GameBoard.BOARDSIZE)
							try {
								ok2Place=ok2Place && shortRangeSensors[r][c]==0;
								game.gameObjects.addStarObject(GameObjects.ENTERPRISE,myRow,myCol);
							}
							catch (Exception ex) {
								Utilities.writeToLog("SRSControl.addStarObject - ERROR - "+r+","+c+"out of bounds");
							}
					}
				}
			}
			
			if (ok2Place) {
				game.gameObjects.addStarObject(type, row, col);

				// place it on the map and signal a successful insertion
				setShortRangeSensors(row,col,type);
				type=0;
			}
		}
		
		return type==0;
	}

	
	/*
	 *  Removing an asteroid, klingon, or starbase
	 * from the current location
	 */
	public void removeStarObject(int row, int col) {
		this.shortRangeSensors[row][col]=0;
		this.srsLabels[row][col].setText(".");
		game.gameObjects.removeStarObjectAt(row, col);
	}


	
	/*
	 * Get the srs map's value at this location
	 */
	public int getShortRangeSensors(int row, int col) {
		return this.shortRangeSensors[row][col];
	}

	/*
	 * Get the srs map's JLabel at this location
	 */
	public JLabel getSRSLabel(int row, int col) {
		return this.srsLabels[row][col];
	}
	

	/*
	 *  update the SRS board and the correct JLabel.  This is
	 *
	 * very quick and helps keep things pseudo-thread-safe
	 * 
	 */
	public void setShortRangeSensors(int row, int col, int value) {
		this.shortRangeSensors[row][col]=value;
		srsLabels[row][col].setText(game.gameObjects.getObjectAt(value));	
	}
	

	/*
	 * look to see if there is a starbase in an adjacent cell
	 */
	public Boolean areWeDocked() {
		boolean result=false;
		
		// this goes in srs
		// if we're done, check to see if we've docked with a starbase
		for(int i=this.myRow-1;i<this.myRow+2;i++) {
			for(int j=this.myCol-1;j<this.myCol+2;j++) {
				if(i>=0 && i<SRSGRIDSIZE && j>=0 && j<SRSGRIDSIZE) {
					if(this.shortRangeSensors[i][j]==3) {
						result=true;
					}
				}
			}
		}
		
		return result;
	}

	
	/*
	 *  Return the row coordinate of the Enterprise
	 */
	public int getMyRow() {
		return this.myRow;
	}

	/*
	 *  Set the row coordinate of the Enterprise
	 */
	public void setMyRow(int value) {
		this.myRow=value;
	}

	
	/*
	 *  Return the column coordinate of the Enterprise
	 */
	public int getMyCol() {
		return this.myCol;
	}


	/*
	 *  Set the column coordinate of the Enterprise
	 */
	public void setMyCol(int value) {
		this.myCol=value;
	}
}

