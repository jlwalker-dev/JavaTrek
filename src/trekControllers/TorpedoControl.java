package trekControllers;

import trekGame.JavaTrek;
import trekGame.Utilities;

/*
 * Torpedoes are the kill shot, if you land them.  
 * You can only carry so many torpedoes (8 is expected value) and
 * once course is set, an animation is created to show it's path
 * and potential destruction of whatever is hit
 * 
 */
public class TorpedoControl extends ControllerSuperClass implements ControllerInterface {

	public TorpedoControl(JavaTrek g) {
		super(g);
		super.init("Photon Torpedos", 8, 100);
	}


	@Override
	public Boolean execute() {
		Boolean executed=false;
		
		if(isHealthy()) {
			// get the course
			double course=Utilities.setCourse(Utilities.CourseControllerType.IMPULSE);
			
			// if a valid course, fire the torpedo
			if (course>0) {
				super.updateCurrentCount(-1);
	
				// Enterprise with distance of -1 means fire a torpedo along the requested course
				game.gameObjects.setObjectMovement(Utilities.createTrackList(game.srs.getMyRow(), game.srs.getMyCol(), course), -1);
	
				// add time for each shot fired
				game.myBoard.starDateAdd(.1);
				executed=true;
			}
		}
		else {
			game.comsChatter("Mr Chekov reports that photon torpedoes are unavailable!");
		}
		
		return executed;
	}
}
