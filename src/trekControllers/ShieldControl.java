package trekControllers;

import trekGame.JavaTrek;
import trekGame.Utilities;

/*
 * Shield controller - sheilds protect against enemy fire.  Shields start out
 * at 98% effective and reduce that number based on health.  Healthy shields 
 * also provide about a 50% chance of not being hit, while unhealthy shields
 * or shields that are down will allow something to be damaged each time
 * 
 */
public class ShieldControl extends ControllerSuperClass implements ControllerInterface {
	private Boolean active=false;
	
	public ShieldControl(JavaTrek g) {
		super(g);
		super.init("Shields", 100, 100);
	}

	
	@Override
	public Boolean execute() {
		if(usable() && getHealth()>0) {
			active=(! active);
			game.comsChatter((active?"Raising":"Lowering")+" shields, Captain.");
		}
		else {
			active=false;
			game.comsChatter("Shield controls are destroyed, sir.");
		}
		
		return true;
	}


	/*
	 * Docking forces shields to be lowered and are automatically
	 * raised again when undocked (if they were raised before docking).
	 * 
	 * A silly little detail, but I'm told I'm a bit OCD
	 * 
	 */
	@Override
	public void docked() {
		if(game.isDocked() && areUp()) {
			game.comsChatter("Lowering sheilds to dock with starbase, Captain");
			active=false;
			super.docked();
		}
	}


	/*
	 * @return (Boolean) are shields up?
	 * 
	 */
	public Boolean areUp() {
		return active;
	}
	
	public void divertPower() {
		double e=game.getEnergyLevel();
		int p;

		if(this.getHealth()==0) {
			game.comsChatter("Soctty reports, \"Captain, yur askin' fer a miracle!  The shield controls ha' been destroyed.\"");
		}
		else if(this.getDamage()==0) {
			game.comsChatter("Sheilds are at full strength, sir!");
		}
		else {
			if(e<10.0) {
				game.comsChatter("Ship's energy levels are too low to divert to shields!");
			}
			else {
				// You can't divert the last 10% of energy to the shields
				e=((e-10)>this.getDamage()/2?Double.valueOf(this.getDamage())/2.0:(e-10));
				p = (int) Utilities.getValue("Amount of power to divert to shields?", 0, e);
				
				if(p>0) {
					if(p==e)
						this.repairAllDamage(); // Otherwise may come up 99%
					else
						this.repairDamage(p*2); // shield health is improved by 2 times energy diverted

					game.adjustEnergy(-p);
				}
			}
		}
	}

	// Adjust active status if shield health goes to zero
	@Override
	public String takeDamage(int d) {
		String r=super.takeDamage(d);
		
		this.active=(this.areUp() && super.getHealth()>0);
		return r;
	}
	
	
	
	
}
