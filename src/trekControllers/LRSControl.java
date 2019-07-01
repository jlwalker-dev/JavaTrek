package trekControllers;

import trekGame.JavaTrek;

/*
 * Long Range Sensors - get the population of all sectors adjacent
 * to the current sector from the the game board and put them onto
 * the user's map so they can see them
 * 
 */
public class LRSControl extends ControllerSuperClass implements ControllerInterface {
	
	public LRSControl(JavaTrek g) {
		super(g);
		super.init("Long Range Sensors", 0, 30);
	}
	
	
	/*
	 * Fill in the surrounding squares with LRS data if eveything
	 * is working correctly.  This routine is only called by the
	 * Enterprise logic.
	 * 
	 * If not healthy, then only return the current location's population
	 */
	@Override
	public Boolean execute() {
		int row=game.myBoard.getMyLocation()/trekGame.GameBoard.BOARDSIZE;
		int col=game.myBoard.getMyLocation()%trekGame.GameBoard.BOARDSIZE;

		// We always map what's in our current sector unless SRS are also out
		if(isHealthy() || game.srs.isHealthy())
			game.myMap.setMyMap(row,col,game.myBoard.getGameBoard(row,col));

		// Enterprise only updates all squares if LRS is working
		if (isHealthy())
			setLRS(row,col);
		
		return true;
	}

	
	/*
	 * Load the LRS data (3x3) for the current location
	 * This routine will also be called by probes
	 * 
	 */
	public void setLRS(int row, int col) {
		for(int r=row-1;r<row+2;r++) {
			for(int c=col-1;c<col+2;c++) {
				setLRSCell(r,c);
			}
		}
	}

	
	/*
	 * Load a single cell into myMap
	 * This routine is also called by the starbase controller
	 */
	public void setLRSCell(int row, int col) {
		if (row>=0 && row<trekGame.GameBoard.BOARDSIZE && col>=0 && col<trekGame.GameBoard.BOARDSIZE) {
			Integer b=game.myBoard.getGameBoard(row,col);
			game.myMap.setMyMap(row,col,b);
		}
	}
	
	

	/*
	 *  return the LRS data for a particular location based
	 *  on row,col location
	 *  
	 */
	public int getLRS(int row, int col) {
		if(row>=0 && row<trekGame.GameBoard.BOARDSIZE && col>=0 && col<trekGame.GameBoard.BOARDSIZE)
			return (super.isHealthy()?game.myMap.getMyMap(row,col):-1);
		else {
			return -1;
		}
	}
}
