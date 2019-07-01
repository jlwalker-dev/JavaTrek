package trekControllers;
import java.math.BigDecimal;
import java.math.RoundingMode;

import trekGame.JavaTrek;
import trekGame.Utilities;


/*
 * All damage is recorded and all repairs are done to the ship by
 * this controller subclass.  This does not act like the others
 * because we don't assign damage to the DC crew.
 * 
 */
public class DamageControl extends ControllerSuperClass implements ControllerInterface {

	// We use this to create the time needed to 
	// effect full repairs to the ship
	private double repairPointsPerStarDate=40;

	/*
	 * Constructor to set up this controller 
	 */
	public DamageControl(JavaTrek g) {
		super(g);
		super.init("Damage Control", 0, 1);  // should never be given damage/repair
	}
	

	/*
	 * damage control controller - used to controller class 
	 * for future version expansion
	 */
	@Override
	public Boolean execute() {
		// DC crews are always working on certain parts of the
		// ship if they are not healthy (unless they fall to zero)
		// in which case they are not reparable without spending time
		fix(game.shields);
		fix(game.phasers);
		fix(game.warp);
		fix(game.impulse);
		
		return true;
	}


	/* 
	 * Calculates the amount of time that will be needed to repair 
	 * all damages based on the repairPointsPerStarDate value
	 * 
	 * @return (double) stardays to complete all repairs to 1 significant digit
	 */
	public double calculateDamageTime(int damage) {
		double repairTime=damage/repairPointsPerStarDate;
		repairTime *= (game.isDocked()?0.5:1); // takes half the time when docked
		repairTime=BigDecimal.valueOf(repairTime<0.1?0.1:repairTime).setScale(1,RoundingMode.HALF_UP).doubleValue();
		return repairTime;
	}


	/* 
	 * DC works on certain controls (if they haven't
	 * fallen to zero) until it can be considered healthy.
	 * And occasionally something just gets fixed
	 */
	public void fix(ControllerSuperClass obj) {
		if(! obj.isHealthy() && obj.getHealth()>0) {
			// if it's healthy, we might get some extra work done
			// if it's not healthy, we're going to make progress
			obj.repairDamage(Utilities.diceRoll(3-1));
			if(obj.getDamage()==0) {
				game.comsChatter("Damage control chief reports repairs completed on "+obj.getDesc()+"!");
			}
			else if(obj.isHealthy()) {
				game.comsChatter("Damage control chief reports that the "+obj.getDesc()+" is working!");
			}
				
		}
		else {
			// Occasionally Scotty or Spock figures something out
			if(!obj.isHealthy() && Utilities.diceRoll(100)%30==0) {
				obj.repairDamage(Utilities.diceRoll(obj.getDamage()+5));
				game.comsChatter("Mr Sott pulled a miracle fix on the "+obj.getDesc()+", Captain!");
			}
		}
	}


	/*
	 * This routine tells us how much time will be required to
	 * get to full health and we can then select how much time
	 * to lose getting some things fixed up.  Shields, warp 
	 * engines, and phasers have priority for repair.
	 * 
	 */
	public void fixAllDamage() {
		Utilities.writeToLog("DamageControl.fixAllDamage ");

		double spend=0;
		
		int damage=game.impulse.getDamage() 
				+game.warp.getDamage()
				+game.torpedoes.getDamage()
				+game.shields.getDamage()
				+game.phasers.getDamage()
				+game.lrs.getDamage();

		
		double starDays=game.damageControl.calculateDamageTime(damage);
		
		if(game.isDocked()) {
			if(starDays>.3)
				starDays=BigDecimal.valueOf(game.isDocked()?starDays/2:starDays).setScale(1,RoundingMode.HALF_UP).doubleValue();
		}

		Utilities.writeToLog("    to fix = "+starDays+" starDays");

		if(starDays>0) {
			spend=Utilities.getValue("It will take "+starDays+" star days to fix all repairs.  Choose how many to spend.", 0,starDays);
			Utilities.writeToLog("    requested="+spend);
		
			if(spend>0) {
				if (spend>=starDays) {
					// just fix everything
					game.impulse.repairAllDamage();
					game.warp.repairAllDamage();
					game.torpedoes.repairAllDamage();
					game.shields.repairAllDamage();
					game.phasers.repairAllDamage();
					game.lrs.repairAllDamage();
					
					game.comsChatter("All damage was repaired on time.");
				}
				else {
					int repairAvailable= (int) (damage*(spend/starDays)+1);
					int repairSpread=repairAvailable/4;
		
					// TODO - put these controllers to a list or array...
					// perhaps some sort of registration process to DC...
					// Then loop through fixing anything not healthy, and
					// then any critical component under 50%, and finally
					// everything that has damage
					
					// loop until we've either fixed everything or
					// we run out of points to fix the damages
					while(repairAvailable>0) {
						Utilities.writeToLog("    damage in="+damage+"   repairAvailable="+repairAvailable);
						
						// priority is given to shields, warp, and phasers
						repairAvailable -= game.shields.repairDamage(repairAvailable>repairSpread?repairSpread:repairAvailable); 
						repairAvailable -= game.warp.repairDamage(repairAvailable>repairSpread?repairSpread:repairAvailable);
						repairAvailable -= game.phasers.repairDamage(repairAvailable>repairSpread?repairSpread:repairAvailable);
	
						// if below 50%, do some more work
						repairAvailable -= game.shields.repairDamage((game.shields.healthPercent()<40?repairAvailable>repairSpread?repairSpread:repairAvailable:0)); 
						repairAvailable -= game.warp.repairDamage((game.shields.healthPercent()<40?repairAvailable>repairSpread?repairSpread:repairAvailable:0));
						repairAvailable -= game.phasers.repairDamage((game.shields.healthPercent()<40?repairAvailable>repairSpread?repairSpread:repairAvailable:0));
	
						repairAvailable -= game.impulse.repairDamage(repairAvailable>repairSpread?repairSpread:repairAvailable);
						repairAvailable -= game.torpedoes.repairDamage(repairAvailable>repairSpread?repairSpread:repairAvailable);
						repairAvailable -= game.lrs.repairDamage(repairAvailable>repairSpread?repairSpread:repairAvailable);
						
						Utilities.writeToLog("    repairAvailable="+repairAvailable);
					}
		
					// return any time left
					starDays -=(damage>0?starDays-damage/repairPointsPerStarDate:0);
		
					game.comsChatter("Damage control crews report ready.");
				}
			}
			
			Utilities.writeToLog(""+starDays+" added to current date");
			game.myBoard.starDateAdd(starDays);
		}
	}
	
	
	/*
	 * Damage is recored here and randomly assigned
	 */
	public String takingDamage(int hit) {
		String response="";
		
		if(game.isDocked()) {
			response="Starbase shields protect the Enterprise";
		}
		else {
			// base effectiveness for shields at full strength
			double shieldEffectiveness=0.98;	
			
			hit=(hit<5?5:hit);  // enemy always hits with at least 5 pts
			
			if(game.shields.areUp()) {
				double hitAdjust= 1D-(Double.valueOf(game.shields.healthPercent())*shieldEffectiveness)/100;
				hit=(int) (Double.valueOf(hit)*(hitAdjust>.5?.5:hitAdjust));
				game.shields.takeDamage((Utilities.diceRoll(5)==1?0:hit));
			}
	
			// Every hit has potential of damaging something
			hit+=(hit>0?0:1);
			
			// if shields are up and healthy, about 50% chance of no damage
			// but if down or not healthy, then damage does occur
			switch(Utilities.diceRoll(game.shields.isHealthy() && game.shields.areUp()?10:5)) {
				case 1:
					response=game.warp.takeDamage(hit);
					break;
				case 2:
					response=game.impulse.takeDamage(hit);
					break;
				case 3:
					response=game.phasers.takeDamage(hit);
					break;
				case 4:
					response=game.torpedoes.takeDamage(hit);
					break;
				case 5:
					response=game.shields.takeDamage(hit);
					break;
				default:
			}
		}
		return response;
	}
}
