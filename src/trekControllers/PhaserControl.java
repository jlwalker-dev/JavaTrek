package trekControllers;


import trekGame.JavaTrek;
import trekGame.GameObjects;
import trekGame.Utilities;

/*
 * Phaser controller
 * Phaser fire is split evenly between all enemy ships and adjusted for distance
 * 
 */
public class PhaserControl extends ControllerSuperClass implements ControllerInterface {

	public PhaserControl(JavaTrek g) {
		super(g);
		super.init("Phasers", 1000, 100);
	}


	@Override
	public Boolean execute() {
		Boolean executed=false;
		
		if(isHealthy()) {
			int power = Utilities.setDuration(Utilities.DurationControllerType.PHASERS);
			int kCounter=0;
			
			if(power>0) {
				// add time for each shot fired
				game.myBoard.starDateAdd(.05);
				executed=true;
	
				// fire phasers at all klingons
				for(int i=0;i<game.gameObjects.getGameObjects().size();i++) {
					kCounter +=(game.gameObjects.getGameObject(i).type==GameObjects.KLINGON?1:0);
				}
	
				if(kCounter<1) {
					game.comsChatter("Sir, there are no Klingons in system!");
				}
				else {
					power=power/kCounter;
					game.adjustEnergy(-Double.valueOf(power/10));
		
					int i=0;
					while(i<game.gameObjects.getGameObjects().size()) {
						if(game.gameObjects.getGameObject(i).type==GameObjects.KLINGON) {
							game.gameObjects.getGameObject(i).health-=Utilities.hitPower(game.gameObjects.getGameObject(i),game.srs,power);
		
							if(game.gameObjects.getGameObject(i).health<=0) {
								game.comsChatter("Klingon at ("+(game.gameObjects.getGameObject(i).row+1)+","+(game.gameObjects.getGameObject(i).col+1)+") was destroyed!");
								game.srs.removeStarObject(game.gameObjects.getGameObject(i).row, game.gameObjects.getGameObject(i).col);
							}
							else {
								game.comsChatter("Klingon at ("+(game.gameObjects.getGameObject(i).row+1)+","+(game.gameObjects.getGameObject(i).col+1)+") was hit and is down to "+game.gameObjects.getGameObject(i).health+"% power");
								i++;
							}
						}
						else
							i++;
					}
				}
			}
		}
		else {
			game.comsChatter("Mr Chekov reports that phasers are inoperative!");
		}

		return executed;
	}
}
