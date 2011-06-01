package cge.zeppelin.environnement;

import java.awt.event.KeyEvent;
import java.util.Calendar;
import java.util.Random;

import cge.zeppelin.Flyer;
import cge.zeppelin.World;
import de.bht.jvr.core.Transform;
import de.bht.jvr.math.Vector3;
/**
 * control the weather of the game
 * 
 * @author charles
 */
public class EnvironnementManager {

	Wind wind = null;
	Rain rain = null;
	
	float windDuration = 3000;
	float rainDuration = 3000;
	
	long windLastTime = 0;
	long rainLastTime = 0;
	
	public boolean disabled;
	
	public EnvironnementManager(World world) {
        world.input.addKeyListener(KeyEvent.VK_P, new Runnable() {

            @Override
            public void run() {
            	EnvironnementManager.this.makeWind();
            }
        });
        world.input.addKeyListener(KeyEvent.VK_O, new Runnable() {

            @Override
            public void run() {
            	EnvironnementManager.this.makeRain();
            }
        });
	}
	
	public void makeWind() {
		wind = new Wind();
    	windLastTime = Calendar.getInstance().getTimeInMillis();
        System.out.println("WIND");
	}
	
	public void makeRain() {
		rain = new Rain();
    	rainLastTime = Calendar.getInstance().getTimeInMillis();
        System.out.println("Rain");
	}
	
	/**
	 * It produces randomly rain or wind
	 */
	public void update() {
		Random randomGenerator = new Random();
		
		if (disabled) return; 

	    if (wind instanceof Wind) {
	    	wind.update();
	    	if (wind.stop()) {
	    		wind = null;
	    	}
	    } else if (randomGenerator.nextInt(100) == 0) {
	    	this.makeWind();
	    }
	    
	    if (rain instanceof Rain) {
    		rain.update();
	    	if (rain.stop()) {
	    		rain = null;
	    	}
	    } else if (randomGenerator.nextInt(100) == 0) {
	    	this.makeRain();
	    }
	}
	
	protected void affectWind(Flyer entity, float dt) {
		if (!(wind instanceof Wind) && !disabled) return ;
		
		entity.translation = entity.translation.mul(
			Transform.translate(
				entity.translation.getMatrix().mulDir(
					new Vector3( wind.getForce().x() * wind.direction.x() * dt,
							wind.getForce().y() * wind.direction.y() * dt,
							wind.getForce().z() * wind.direction.z() * dt)
				)
			)
		);
	}

	protected void affectRain(Flyer entity, float dt) {
		if (!(rain instanceof Rain) && !disabled) return ;

		entity.translation = entity.translation.mul(
			Transform.translate(
				entity.translation.getMatrix().mulDir(
						new Vector3( rain.getForce().x() * rain.direction.x() * dt,
								rain.getForce().y() * rain.direction.y() * dt,
								rain.getForce().z() * rain.direction.z() * dt)
				)
			)
		);
	}
	
	public void affect(Flyer entity, float dt) {
		if (!disabled) {
			affectWind(entity, dt);
			affectRain(entity, dt);
		}
	}

	public void reset() {
		wind = null;
		rain = null;
	}
	
	public void switchDisable() {
		disabled = !disabled;
		if (disabled) {
			System.out.println("STOP WEATHER");
		} else {
			System.out.println("STOP WEATHER");
		}
	} 
}
