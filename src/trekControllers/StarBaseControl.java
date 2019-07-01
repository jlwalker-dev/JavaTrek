package trekControllers;
import java.util.ArrayList;
import java.util.List;

import trekGame.GameBoard;
import trekGame.GameObjects;
import trekGame.JavaTrek;
import trekGame.Utilities;


/*
 * Star Base Controller
 * 
 * Manages all of the starbases in the galaxy by creating a list of locations.  Star bases will 
 * send updates, every stardate, to myMap as different sectors are scanned.  When executed, each 
 * starbase is validated, and if missing from the game board, will be removed from the list.
 * 
 * One thought is to have each starbase represented by a separate StarBaseControl object.  In 
 * an arcade  game, I can see that, but as part of a board game I see just one controller handling
 * all starbases.  The same thought comes to mind if we want to create Klingons that move on their
 * own... a Klingon class for each instance might be the best way to go.
 * 
 * The big issue would be on how many resources each instance takes up.   A K or so would be very
 * acceptable and might add a lot to the design.  FUTURE RESEARCH!
 * 
 */
public class StarBaseControl extends ControllerSuperClass implements ControllerInterface {
	public static final int CHECK_IN_ODDS=75;
	private double lastUpdate=0;
	
	public class StarBase {
		// standard elements about the base
		public int gameBoardRow;
		public int gameBoardCol;
		public int gameBoardLoc;
		public Boolean sendUpdates=true;
		public int baseNo=starBases.size()+1;
		public int Health=100;
		
		// we only care about the values of these elements
		// if they are less than the current stardate
		public double destroyedAfter=9999.9;  // reset if Klingons in area to current SD+1.5
		public double lastAlert=9999.9; // used to send out calls for help every .25 SD
				
		public StarBase(int row, int col) {
			this.gameBoardRow=row;
			this.gameBoardCol=col;
			this.gameBoardLoc=row*GameBoard.BOARDSIZE+col;
		}
	}

	List<StarBase> starBases = new ArrayList<>();
	
	public StarBaseControl(JavaTrek g) {
		super(g);
	}


	/*
	 * Each starbase sends periodic sector update
	 * 
	 */
	@Override
	public Boolean execute() {
		int s=0;
		int k=0;
		while(s<starBases.size()) {
			k=game.myMap.getMyMap(starBases.get(s).gameBoardRow, starBases.get(s).gameBoardCol);
			
			if(k<100) {
				starBases.remove(s);
			}
			else {
				k=(k%100)/10;
				
				if(k>0 && starBases.get(s).destroyedAfter>9000.0) {
					// we just discovered klingons in the sector
					starBases.get(s).destroyedAfter=Utilities.fixToOneDecimal(game.myBoard.currentStarDate()+(4.0/Double.valueOf(k)));
					game.comsChatter(Utilities.EOL+"* * * Starbase "+starBases.get(s).baseNo+" is under attack * * *");
					starBases.get(s).lastAlert=game.myBoard.currentStarDate();
				}
				s++;
			}
		}
		
		s=0;
		while(s<starBases.size()) {
			// have we passed the destroyed after date?
			if(starBases.get(s).destroyedAfter<game.myBoard.currentStarDate()) {
				// blow it up - but don't tell anyone
				game.myBoard.removeObjectFromBoard(GameObjects.STARBASE,starBases.get(s).gameBoardLoc);
				starBases.remove(s);
			}
			else {
				if(game.myBoard.currentStarDate()-starBases.get(s).lastAlert>0.25) {
					game.comsChatter("* * * Starbase "+starBases.get(s).baseNo+" is under attack! * * *");
					starBases.get(s).lastAlert=game.myBoard.currentStarDate();
				}
				s++;
			}
		}

		if(starBases.size()>0) {
			if(lastUpdate-game.myBoard.currentStarDate()>0.5) {
				lastUpdate=game.myBoard.currentStarDate();

				Boolean done=false;
				
				for(int i=0;i<starBases.size();i++) {
					done=false;
					s=100;
					
					while(! done && s>0 && starBases.get(i).sendUpdates) {
						s--;
						int row=starBases.get(i).gameBoardRow-3+Utilities.diceRoll(6);
						int col=starBases.get(i).gameBoardCol-3+Utilities.diceRoll(6);
		
						if(row>=0 && row<trekGame.GameBoard.BOARDSIZE && col>=0 && col<trekGame.GameBoard.BOARDSIZE) {
							if(game.myMap.getMyMap(row, col)<0) {
								game.myMap.setMyMap(row, col, game.myBoard.getGameBoard(row, col));
								game.comsChatter("Starbase "+starBases.get(i).baseNo
										+" completed sensor sweep of sector ("
										+(row%8+1)+","+(col%8+1)+")");
								done=true;
							}
						}
					}
					
					if(s<3) {
						// not enough open cells, so no more updates
						starBases.get(i).sendUpdates=false;
					}
				}
			}
		}
		
		return true;
	}
	
	
	// Add a starbase at the requested row,col
	public void addStarbase(int row, int col) {
		if(row>=0 && row<trekGame.GameBoard.BOARDSIZE && col>=0 && col<trekGame.GameBoard.BOARDSIZE) {
			// add to the list
			starBases.add(new StarBase(row, col));
			
			// update myMap for local population and give check in msg
			game.myMap.setMyMap(row, col, game.myBoard.getGameBoard(row, col));
			game.comsChatter("Starbase "+starBases.size()+" checking in!");
		}
	}
}
