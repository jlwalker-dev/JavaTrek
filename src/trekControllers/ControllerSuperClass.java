package trekControllers;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import trekGame.JavaTrek;
import trekGame.Utilities;

/*
 * This is the Controller super class that gives the majority of the capabilities
 * to all of the various controllers for the ship.  Hits are registered, repairs are
 * performed, and basic functionality for the game to detect health, availability, and
 * any docking/undocking procedures. 
 */
public class ControllerSuperClass {
	JavaTrek game;
	
	// basic properties of each class
	private int maxAllowed=0;		// max count allowed (such as torpedoes)
	private int currentCount=0;		// current count
	private int maxHealth=0;		// maximum health points
	private int currentHealth=0;	// current health points
	private int healthPoint=0;		// point at which system becomes inoperable
	
	private String description;		// description (phasers, warp engines, etc)

	// we just load the game object  reference during instantiation
	public ControllerSuperClass(JavaTrek g) {
		this.game=g;
	} 

	// Initialize as a specific type of controller.  Nothing really changes
	// based on what's sent, it's just recorded and used by the subclass 
	public void init(String desc, int maxAllowed, int maxHealth) {
		this.description=desc;
		this.maxAllowed=maxAllowed;
		this.maxHealth=maxHealth;
		this.currentCount=maxAllowed;
		this.currentHealth=maxHealth;
		this.healthPoint=maxHealth/5;
	}
	
	public Boolean usable() {
		Boolean usable=isHealthy();
		
		if(this.currentHealth<20 && game.getAlertLevel()!=JavaTrek.REDALERT) {
			// get time to repair + 1
			double t=game.damageControl.calculateDamageTime(getDamage())+1;
			
			// do you want to repair?
			int answer=JOptionPane.showConfirmDialog((JFrame) game, "<html>It will take "+String.format("%.1f", t)+" days to fabricate<br>and install the parts to get the<br>system working again.<br><br>Do you want to do that?","Repare Estimate",JOptionPane.YES_NO_OPTION);
			
			// if yes, then repair between 22-44 percent
			if(answer==JOptionPane.YES_OPTION) {
				// adjust time
				game.myBoard.starDateAdd(t);
				this.currentHealth=Utilities.diceRoll(20)+22;
				usable=true;
			}
		}
		
		return usable;
	}


	/*
	 * Given a number of repair points, effect what repairs
	 * we can.  Return how many repair points were used.
	 */
	public int repairDamage(int repairPoints) {
		Utilities.writeToLog("    Controller "+this.description+".repairDamage("+repairPoints+")");
		Utilities.writeToLog("        currentHealth="+(currentHealth));

		int usedPoints=repairPoints;

		// If we roll a multiple of 33, then use that as a basis for 
		// some extra repair work on this part of the ship, with no 
		// carryover to another part of the ship. 
		int diceRoll=Utilities.diceRoll(330);
		int extraRepairPointsExtra=(diceRoll%33==0?diceRoll/33:0);
		
		// Are there any repair points to use?
		if(repairPoints+extraRepairPointsExtra>0) {
			if(currentHealth+repairPoints<=maxHealth) {
				// we don't have enough for full repairs without 
				// the extra repair points, so we're using up
				// all of what's been sent to us
				usedPoints=repairPoints;
				
				// now add everything up
				currentHealth += repairPoints+extraRepairPointsExtra;
				
				// make sure we didn't go over with any extra points
				currentHealth = (currentHealth>maxHealth?maxHealth:currentHealth);
			}
			else {
				// we're repairing it to full health and
				// sending back how much we actually used
				usedPoints=(maxHealth-currentHealth);
				currentHealth=maxHealth;
			}
		}

		Utilities.writeToLog("        new currentHealth="+currentHealth+" and returning used="+usedPoints);
		return usedPoints;
	}
	
	
	/*
	 * Damage to the controller is sent here.  About a 3% chance of 
	 * extra damage from a lucky hit.
	 * 
	 * @return (String) 
	 */
	public String takeDamage(int hitPoints) {
		String response=this.description+" takes "+hitPoints+" damage.";

		if(Utilities.diceRoll(100)%33==0) {
			hitPoints+=5+Utilities.diceRoll(10);
			response="A lucky hit by the Klingon causes "+hitPoints+" damage.";
		}
		
		currentHealth = (currentHealth>=hitPoints?currentHealth-hitPoints:0);
		return response;
	}


	/*
	 * By default we just "restock" when we dock with the
	 * starbase.  Most controllers don't use this value.
	 */
	public void docked() {
		if(game.isDocked()) {
			currentCount=maxAllowed;
		}
	}
	

	/*
	 * @return (Boolean) if controller should work
	 */
	public boolean isHealthy() {
		return currentHealth>=healthPoint;
	}


	/* 
	 * Set up the health point, typically 20% of max
	 */
	public void setHealthPoint(int hp) {
		this.healthPoint=hp;
	}
	
	
	/*
	 * @return (Boolean) is controller at full health?
	 */
	public boolean atFullHealth() {
		return currentHealth==maxHealth;
	}


	/*
	 * @return (int) percentage of total health
	 */
	public int healthPercent() {
		return currentHealth*100/maxHealth;
		
	}
	

	/*
	 * @return (int) percentage of stocked items left
	 */
	public int levelPercent() {
		return currentCount*100/maxAllowed;
	}

	
	/*
	 * @param (int) value - amount to add or (-) subtract
	 * 	or subtract to current amount
	 */
	public void updateCurrentCount(int value) {
		currentCount+=value;
	}
	
	
	/*
	 * @return (int) current count of stocked items
	 */
	public int getCurrentCount() {
		return currentCount;
	}


	/*
	 * @return (int) number of points needed to bring to full health
	 */
	public int getDamage() {
		return maxHealth-currentHealth;
	}

	
	/*
	 * @return (String) getter for description
	 */
	public String getDesc() {
		return description;
	}
	
	
	/*
	 * Set current health to max health
	 */
	public void repairAllDamage() {
		currentHealth=maxHealth;
	}
	

	/*
	 * @return (int) current health points
	 */
	public int getHealth() {
		return currentHealth;
	}
}
