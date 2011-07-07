package cge.zeppelin.environnement;

import java.awt.event.KeyEvent;
import java.util.Calendar;
import java.util.Random;

import cge.zeppelin.Flyer;
import cge.zeppelin.World;
import de.bht.jvr.core.GroupNode;
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
	private GroupNode node;
	public RainEntity rainEntity;
	private World world;
	
	public EnvironnementManager(World world, GroupNode n) {
		node = n;
		this.world = world;
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
        
        rainEntity = new RainEntity(node);
        world.add(rainEntity);
	}
	
	public void makeWind() {
		wind = new Wind();
    	windLastTime = Calendar.getInstance().getTimeInMillis();
        System.out.println("WIND");
	}
	
	public void makeRain() {
		rain = new Rain();
		rainEntity.start();
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
	        	windLastTime = Calendar.getInstance().getTimeInMillis();
	    	}
	    } else if (randomGenerator.nextInt(200) == 0 && windLastTime + 10000 < (Calendar.getInstance().getTimeInMillis())) {
	    	this.makeWind();
	    }
	    
	    if (rain instanceof Rain) {
    		rain.update();
	    	if (rain.stop()) {
	    		rain = null;
	    		rainEntity.stop();
	        	rainLastTime = Calendar.getInstance().getTimeInMillis();
	    	}
	    } else if (randomGenerator.nextInt(200) == 0 && rainLastTime + 10000 < (Calendar.getInstance().getTimeInMillis())) {
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

	float brightness = 1;
	float MINBRIGHTNESS = 0.1f;
	float MAXBRIGHTNESS = 0.7f;
	
	protected void affectRain(Flyer entity, float dt) {
		//if (!(rain instanceof Rain)) return ;
		if (!(rain instanceof Rain)){
			if (brightness<MAXBRIGHTNESS){
				brightness+=0.01;
				world.skybox.setBrightness(brightness);
				world.terrain.setBrightness(brightness);
			}
			return;
		}
		if (brightness>MINBRIGHTNESS){
			brightness-=0.01;
			float v =  (Math.abs(brightness - 0.5f) < 0.01 | 
						Math.abs(brightness - 0.4f) < 0.01 | 
						Math.abs(brightness - 0.2f) < 0.01 ) ? 10 : brightness;
			
			world.skybox.setBrightness(v);
			world.terrain.setBrightness(v);
			
		}
		
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
			System.out.println("START WEATHER");
		}
	} 
}
