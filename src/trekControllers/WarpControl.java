package trekControllers;
import java.util.List;

import trekGame.JavaTrek;
import trekGame.Utilities;
import trekGame.Track;


import javax.swing.JOptionPane;

/*
 * Warp engines controller - allow rapid movement throughout the game board.
 * Warp engines return 5% power for each sector traversed.
 * 
 * If not healthy, allow impulse to take over with a higher use of time and 
 * use of energy of about 1% per sector.
 * 
 */
public class WarpControl  extends ControllerSuperClass implements ControllerInterface {
	
	public WarpControl(JavaTrek g) {
		super(g);
		super.init("Warp Engines", 12, 100);
	}

	public Boolean execute() {
		boolean clearToWarp=usable();
		boolean executed=false;
		
		if(! clearToWarp && game.impulse.isHealthy()) {
			// warp is down, but you can use impulse to go where you want
			// at a cost of .3 star dates per warp... do you want to do that?
			String dialog="<html>The warp engines are down, but you can accomplish the same effect<br>"
						+ "by using impulse engines at one half stardate for each sector traversed.<br><br>"
						+ "Do you want to continue?";
			
			clearToWarp=JOptionPane.showConfirmDialog(game, dialog, "Warp Down", JOptionPane.YES_NO_OPTION)==0;
		}
		
		
		if(clearToWarp) {
			int loc = game.myBoard.getMyLocation();
			int row = loc/trekGame.GameBoard.BOARDSIZE;
			int col = loc%trekGame.GameBoard.BOARDSIZE;
			
			double course=Utilities.setCourse(Utilities.CourseControllerType.WARP);
			
			if (course>=1D) {
				//System.out.println("Starting Track at "+row+","+col);
				List<Track> track = Utilities.createTrackList(row, col, course);
				int maxDuration=Utilities.setDuration(Utilities.DurationControllerType.WARP);

				int i=(maxDuration>track.size()?track.size():maxDuration);
				
				if(i>0) {
					Boolean inGalaxy=true;
					executed=true;

					while (i>0) {
						i--;

						// add time and energy for each sector traversed
						game.myBoard.starDateAdd(isHealthy()?0.1:.5);
						game.adjustEnergy(5);

						// Run DC for any unhealthy critical systems for each sector traversed
						// If running on impulse, we have more time to do work, so more improvement
						// would be expected for each critical system.
						@SuppressWarnings("unused")
						int a=(isHealthy()?0:repairDamage(isHealthy()?1:4)); 
						a=(game.impulse.isHealthy()?0:game.impulse.repairDamage(isHealthy()?1:3));
						a=(game.phasers.isHealthy()?0:game.phasers.repairDamage(isHealthy()?1:3));
						a=(game.phasers.isHealthy()?0:game.phasers.repairDamage(isHealthy()?1:3));
						
						game.adjustEnergy(5);
						
						// get coordinates
						int c=track.get(i).x;
						int r=track.get(i).y;
						
						//System.out.println("Warp Track "+i+" - "+r+","+c);
						
						// is the destination in the galaxy?
						if(r>=0 && r<trekGame.GameBoard.BOARDSIZE && c>=0 && c<trekGame.GameBoard.BOARDSIZE) {
							// found a valid track location, moving to that sector
							i=0;
							game.myBoard.setMyLocation(r*trekGame.GameBoard.BOARDSIZE+c);
							game.comsChatter("Now in sector "+game.myBoard.getLocation());
						}
						else {
							// we can't go out of bounds
							if(inGalaxy) {
								game.comsChatter("Lt Uhura reports: \"Captain, Star Fleet forbids us from leaving the galaxy.\"");
								inGalaxy=false;
							}
						}
					}
				}
			}
		}
		else {
			game.comsChatter("Mr Scot says \"I'm sorry Captain, but me poor wee bairns are down!\"");
		}
		
		return executed;
	}
}
