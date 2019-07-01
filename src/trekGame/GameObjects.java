package trekGame;
import java.util.ArrayList;
import java.util.List;

import trekControllers.SRSControl;

/*
 * This class keeps track of the objects in the current sector
 * and is used in drawing and moving the pieces on the sector
 * board (also known as the Short Range Sensors or SRS)
 * 
 */
public class GameObjects {
	private JavaTrek game;

	public static final int ASTEROID=1;
	public static final int KLINGON=2;
	public static final int STARBASE=3;
	public static final int ENTERPRISE=4;
	public static final int TORPEDO=9;
	
	

	private List<StarObject> starObjects=new ArrayList<>();
	private List<ObjectMovement> moveObjects=new ArrayList<>();

	/*
	 * Constructor for the class
	 */
	public GameObjects(JavaTrek g) {
		this.game=g;
		clearObjects();
	}
	
	
	/*
	 * Clear all objects from the sector board
	 */
	public void clearObjects() {
		starObjects=new ArrayList<StarObject>();
	}
	
	
	/*
	 * Record an object to the starObjects list
	 * 
	 */
	public void addStarObject(int type, int row, int col) {
		// remember this object for later use
		StarObject s=new StarObject();
		s.row=row;		// row on sector map
		s.col=col;		// column on sector map
		s.type=type;	// type of object
		
		// hit points for the object
		s.health= (type==GameObjects.ASTEROID?20:(type==GameObjects.KLINGON?80:100));
		starObjects.add(s);
	}

	
	/* 
	 * Remove the object form the list that matches
	 * the current row,col location
	 * 
	 */
	public void removeStarObjectAt(int row, int col) {
		// remove the object from the starObjects list
		// the list is very short so nothing fancy needed for the search
		for(int i=0;i<starObjects.size();i++) {
			if(starObjects.get(i).row==row && starObjects.get(i).col==col) {
				Utilities.writeToLog("gameObjects.removeStartObjectAt "+row+","+col+" type="+starObjects.get(i).type);
				game.myBoard.removeObjectFromBoard(starObjects.get(i).type);
				starObjects.remove(i);
				i=starObjects.size();
			}
		}
	}


	
	/*
	 *  Return the map key for the object in the cell
	 */
	public String getObjectAt(int type) {
		String key=".";
		
		switch (type) {
			case ASTEROID: 
				key="*";
				break;
			case KLINGON:
				key=">K<";
				break;
			case -KLINGON:
				key="#K#";
				break;
			case STARBASE: 
				key="<S>";
				break;
			case ENTERPRISE:
				key="<E>";
				break;
			case TORPEDO:
				key="#";
		}
		
		return key;
	}
	

	/*
	 *  We're moving the Enterprise
	 *  
	 */
	public int setObjectMovement(List<Track> track, int distance) {
		return setObjectMovement(game.srs.getMyRow(), game.srs.getMyCol(), track, distance);
	}

	
	/* 
	 * Klingons will typically move from - to and a track has to be created
	 * 
	 */
	public int setObjectMovement(int fromRow, int fromCol, int toRow, int toCol, int distance) {
		// create a track from - to and send it on
		List<Track> track=new ArrayList<>();
		return setObjectMovement(fromRow, fromCol, track, distance);
	}


	/*
	 *  Create the list of movement animation actions
	 *  
	 */
	public int setObjectMovement(int fromRow, int fromCol, List<Track> track, int distance) {
		// initialize everything
		int type=game.srs.getShortRangeSensors(fromRow,fromCol);
		int trackIndex=0;
		int r,c,t;
		int dist=0;
		Boolean done=false;
		
		int lastr=fromRow;
		int lastc=fromCol;

		int currentLoc=game.myBoard.getMyLocation();
		int gbLimit=GameBoard.BOARDSIZE-1;
		
		ObjectMovement m;

		// movement only works if there is a ship in the cell
		if (type==KLINGON || type==ENTERPRISE) {

			// If it's the enterprise, and distance is negative, we're 
			// firing a torpedo down the listed track
			if (type==4 && distance<0) {
				// firing a torpedo!
				type=TORPEDO;
				distance=99;
			}
			
			// move the object along the track, stopping if it runs into something
			while(! done) {
				r=track.get(trackIndex).y;
				c=track.get(trackIndex).x;
			
				if(r>=0 && r<SRSControl.SRSGRIDSIZE && c>=0 && c<SRSControl.SRSGRIDSIZE) {
					if(r!=fromRow || c!=fromCol) { // we don't want to mess with starting location
						dist++;
						
						//get what's in the cell along this track
						t=game.srs.getShortRangeSensors(r,c);
						
						if(t>0) {
							// there is something at this location, so the 
							// track ends here and something may happen
							switch(type) {
								case ENTERPRISE:
									// enterprise ran into something, stop engines
									m=new ObjectMovement(0,"CMr Scot says, \"Engines shut down to prevent collision.\"",-1,-1);
									moveObjects.add(m);
									break;
	
								case TORPEDO:
									// torpedo ran into something, make it go away
									m=new ObjectMovement(0,"CChekov yells, \"Direct hit, Kiptan!\"",-1,-1);
									moveObjects.add(m);

									for(int i=0;i<starObjects.size();i++) {
										if(starObjects.get(i).row==r && starObjects.get(i).col==c) {
											// deliver 40-100 hit points
											starObjects.get(i).health -= Utilities.diceRoll(30)+70;

											// did we destroy it?
											if(starObjects.get(i).health<1) {
												m=new ObjectMovement(t,"D",r,c);
												moveObjects.add(m);
											}
										}
									}
									break;
								
								default:
									// klingon ran into something
									// don't bother mentioning it
							}
							
							// track ends
							done=true;
						}
						else {
							// nothing found along the track (yet) so
							// continue the movement animation
							if(type==ENTERPRISE) { 
								m=new ObjectMovement(ENTERPRISE,"R",game.srs.getMyRow(),game.srs.getMyCol());
								moveObjects.add(m);
								m=new ObjectMovement(ENTERPRISE,"A",r,c);
								moveObjects.add(m);
								game.srs.setMyRow(r);
								game.srs.setMyCol(c);
							}
							else if(type!=TORPEDO) {
								m=new ObjectMovement(type,"R",lastr,lastc);
								moveObjects.add(m);

								m=new ObjectMovement(type,"A",r,c);
								moveObjects.add(m);
							}
							else {
								m=new ObjectMovement(TORPEDO,"A",r,c);
								moveObjects.add(m);
								moveObjects.add(m);

								m=new ObjectMovement(TORPEDO,"R",r,c);
								moveObjects.add(m);
							}
						}

						lastr=r;
						lastc=c;
					}
				}
				else {
					// the track takes us out of the sector, torpedos just sail away
					// klingons will stop and the enterprise goes to a different sector
					// if we're not at the edge of the galaxy
					switch(type) {
						case ENTERPRISE:
							int lr,lc;
							int rfix=0;
							int cfix=0;
							
							// moving into other sector
							while(! done) {
								dist++;

								lr=currentLoc/GameBoard.BOARDSIZE;
								lc=currentLoc%GameBoard.BOARDSIZE;

								// are we trying to leave the galaxy?
								if(lr<0 || lc<0 || lr>gbLimit || lc>gbLimit) {
									m=new ObjectMovement(0,"CImpulse engines shut down at galactic border.",-1,-1);
									moveObjects.add(m);
									done=true;
								}

								// continue to track through to the next sector(s)
								// rfix & cfix make sure the track appears to be in the grid
								// and when it leaves the grid, the sector location and fix 
								// values are updated
								if(! done) { 
									currentLoc += (r+rfix<0? -GameBoard.BOARDSIZE:(r+rfix>=SRSControl.SRSGRIDSIZE?GameBoard.BOARDSIZE:0));
									currentLoc += (c+cfix<0? -1 :(c+cfix>=SRSControl.SRSGRIDSIZE?1:0));
	
									rfix +=(r+rfix>=SRSControl.SRSGRIDSIZE? -SRSControl.SRSGRIDSIZE:0);
									rfix +=(r+rfix<0?SRSControl.SRSGRIDSIZE:0);
									cfix +=(c+cfix>=SRSControl.SRSGRIDSIZE? -SRSControl.SRSGRIDSIZE:0);
									cfix +=(c+cfix<0?SRSControl.SRSGRIDSIZE:0);

									// have we gone the requested distance?
									if(dist<distance) {
										// we're just calculating moves, not showing
										trackIndex++;
										r=(track.get(trackIndex).y);
										c=(track.get(trackIndex).x);
									}
									else {
										// we're done, so set up the Location command
										m=new ObjectMovement(currentLoc,"L",r+rfix,c+cfix);
										moveObjects.add(m);
										done=true;
									}
								}
							}
							break;

						case TORPEDO:
							m=new ObjectMovement(0,"CYou watch the torpedo sailing out of the sector; a clean miss!",-1,-1);
							moveObjects.add(m);
							done=true;
							break;

						default:
							// nothing to see here
					}
				}

				trackIndex++;
				
				// we're done if we run out of track or we go the expected distance
				done=(done || trackIndex>=track.size() || dist>=distance);
			}
		}
		
		return dist; // actual distance traveled
	}
	

	/*
	 * Process the top animation object and pop
	 * it off the stack.  When done, return a null.
	 * 
	 */
	public String showObjectMovement() {
		Utilities.writeToLog("Showing Object Movement");

		String result=null;
		
		if(moveObjects!=null && moveObjects.size()>0) {
			// get the info and remove from the top of the stack
			int type=moveObjects.get(0).type;
			String action=moveObjects.get(0).action;
			int row=moveObjects.get(0).row;
			int col=moveObjects.get(0).col;
			moveObjects.remove(0);
			
			//System.out.println("Action "+action+" with "+type+" @ ("+row+","+col+")");

			if(action.substring(0,1).equalsIgnoreCase("F")) {
				if(action.length()>1) {
					game.comsChatter(action.substring(1));
				}
				game.srs.setShortRangeSensors(row,col,type);
				result="F";
			}
			else if(action.equalsIgnoreCase("L")) {
				// set new location, clear objects, call SRS
				game.myBoard.setMyLocation(type);
				this.clearObjects();
				game.srs.setGrid(row,col);
			}
			else if(action.substring(0,1).equalsIgnoreCase("C")) {
					// coms chatter
					game.comsChatter(action.substring(1));
					result="C";
			}
			else {
				// update the SRS map, Add to cell or zero out for Remove/Destroy
				game.srs.setShortRangeSensors(row,col,action.equalsIgnoreCase("A")?type:0);
				
				// did we destroy something?
				if(action.equalsIgnoreCase("D")) {
					for(int i=0;i<starObjects.size();i++) {
						// Is this the object?
						if(row==starObjects.get(i).row && col==starObjects.get(i).col) {
							// Tell us what got destroyed
							switch(type) {
								case ASTEROID:
									game.comsChatter("Asteroid destroyed.");
									break;
								case KLINGON:
									game.comsChatter("Klingon destroyed!");
									break;
								case STARBASE:
									game.comsChatter("Starfleet is ordering a General Courtmartial!");
									break;
								default:
							}

							// Destroyed, so remove it
							this.removeStarObjectAt(row, col);
						}
					}
				}

				// Return what we did so we can decide if we want to do 
				// anything extra, or null if it was the last node
				result=(moveObjects.size()>0?" AKSE    T".substring(type,type+1)+action:null);
			}
		}
		
		return result;
	}

	
	/*
	 * Process the taking of fire from the enemy
	 */
	public void takingFire() {
		int p;
		String resp;
		ObjectMovement m;
		
		// loop through and take fire from Klingons based on their health
		for(int s=0;s<starObjects.size();s++) {
			p=starObjects.get(s).health;
			
			if(starObjects.get(s).type==KLINGON && p>0) {
				// disrupter fire from klingon ship
				p=Utilities.hitPower(starObjects.get(s),game.srs,(int) p);
				
				// create update on hit
				m=new ObjectMovement(-KLINGON,"F"+p+" point hit from Klingon at ("+(starObjects.get(s).row+1)+","+(starObjects.get(s).col+1)+")",starObjects.get(s).row,starObjects.get(s).col);
				moveObjects.add(m);
				
				m=new ObjectMovement(-KLINGON,"F",starObjects.get(s).row,starObjects.get(s).col);
				moveObjects.add(m);

				m=new ObjectMovement(-KLINGON,"F",starObjects.get(s).row,starObjects.get(s).col);
				moveObjects.add(m);

				m=new ObjectMovement(KLINGON,"F",starObjects.get(s).row,starObjects.get(s).col);
				moveObjects.add(m);

				resp=game.damageControl.takingDamage(p);
				
				if(resp.length()>0) {
					m=new ObjectMovement(0,"C"+resp,-1,-1);
					moveObjects.add(m);
				}
			}
		}
	}
	
	
	/*
	 *  Return the list of starObjects in this sector
	 */
	public List<StarObject> getGameObjects() {
		return this.starObjects;
	}
	
	
	/*
	 * Return the object located at row,col
	 */
	public StarObject getGameObject(int row, int col) {
		StarObject response=null;
		
		for(int i=0;i<starObjects.size();i++) {
			if(starObjects.get(i).row==row && starObjects.get(i).col==col) {
				response=starObjects.get(i);
				break;
			}
		}
		
		return response;
	}

	
	/*
	 * Return the objects in the sector
	 */
	public StarObject getGameObject(int i) {
		return starObjects.get(i);
	}
}
