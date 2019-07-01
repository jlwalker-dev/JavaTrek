package trekControllers;

import trekGame.JavaTrek;
import trekGame.Utilities;

/*
 * Impulse controller for ship - provides the logic for setting the
 * course and calling the movement animation routine to provide
 * movement through the sector grid and on to the next if needed.
 * 
 * If not healthy then you can move through the sector
 * 
 */
 public class ImpulseControl extends ControllerSuperClass implements ControllerInterface {
 

	/* 
	 * Setup the controller
	 */
	public ImpulseControl(JavaTrek g) {
		super(g);
		super.init("Impulse Engines", 12, 100);
	}


	/*
	 * Ask for course and distance then create animation track and
	 * adjust the energy and time resources
	 * 
	 */
	@Override
	public Boolean execute() {
		Boolean executed=false;
		
		if (usable()) {
			// get the course
			double course=Utilities.setCourse(Utilities.CourseControllerType.IMPULSE);
			
			// if a valid course, move the enterprise
			if (course>0) {
				int distance=Utilities.setDuration(Utilities.DurationControllerType.IMPULSE);
				
				if (distance>0) {
					int dist = game.gameObjects.setObjectMovement(Utilities.createTrackList(game.srs.getMyRow(), game.srs.getMyCol(), course), distance);
					
					// impulse burns time
					game.myBoard.starDateAdd(Double.valueOf(dist)*0.05);
					game.adjustEnergy(dist*0.25);
					executed=true;
				}
			}
		}
		else {
			// send error messsage to console that impulse is down
			game.comsChatter("Mr Scot reports impulse engines are still down!");

			// offer to fix it
			if(game.getAlertLevel()!=JavaTrek.REDALERT) {
				double starDays=game.damageControl.calculateDamageTime(this.getDamage());
				double spend=Utilities.getValue("It will take "+starDays+" star days to completetly fix the impulse engines.  Choose how many to spend.", 0,starDays);
				
				if (spend>0) {
					int i=(int) (this.getDamage()*(spend/starDays));
					this.repairDamage(i);
				}
			}
		}
		
		return executed;
	}
}
