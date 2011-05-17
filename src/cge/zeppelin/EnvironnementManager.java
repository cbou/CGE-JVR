package cge.zeppelin;

import java.awt.event.KeyEvent;
import java.util.Calendar;

import cge.zeppelin.util.Helper;


import de.bht.jvr.core.Transform;
import de.bht.jvr.math.Vector3;
/**
 * control the weather of the game
 * 
 * @author charles
 */
public class EnvironnementManager {

	float windForce = 0;
	float rainForce = 0;
	
	float windDuration = 3000;
	float rainDuration = 3000;
	
	long windLastTime = 0;
	long rainLastTime = 0;
	
	public EnvironnementManager(World world) {
        world.input.addKeyListener(KeyEvent.VK_P, new Runnable() {

            @Override
            public void run() {
            	windForce = 1;
            	windLastTime = Calendar.getInstance().getTimeInMillis();
                System.out.println("WIND");
            }
        });
        world.input.addKeyListener(KeyEvent.VK_O, new Runnable() {

            @Override
            public void run() {
            	rainForce = 1;
            	rainLastTime = Calendar.getInstance().getTimeInMillis();
                System.out.println("Rain");
            }
        });
	}
	
	protected void affectWind(Flyer entity, float dt) {
		// if no Wind no affectation
		if (windForce < 0.01f) return ;
		
		float currentMili = Calendar.getInstance().getTimeInMillis() - windLastTime;
		windForce = Helper.map(currentMili, 0, windDuration, 1, 0);

		entity.translation = entity.translation.mul(
			Transform.translate(
				entity.translation.getMatrix().mulDir(
					new Vector3( +windForce * dt, 0, -(windForce * dt)/2)
				)
			)
		);
	}

	protected void affectRain(Flyer entity, float dt) {
		// if no Wind no affectation
		if (rainForce < 0.01f) return ;
		
		float currentMili = Calendar.getInstance().getTimeInMillis() - rainLastTime;
		rainForce = Helper.map(currentMili, 0, rainDuration, 1, 0);

		entity.translation = entity.translation.mul(
			Transform.translate(
				entity.translation.getMatrix().mulDir(
					new Vector3( 0, -(rainForce * dt)/2, 0)
				)
			)
		);
	}
	
	public void affect(Flyer entity, float dt) {
		affectWind(entity, dt);
		affectRain(entity, dt);
	}
}
