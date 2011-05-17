package cge.zeppelin;

import java.awt.event.KeyEvent;
import java.util.Calendar;

import cge.zeppelin.util.Helper;


import de.bht.jvr.core.Transform;
import de.bht.jvr.math.Vector3;

public class Environnement {

	float windForce = 0;
	float duration = 3000;
	long lastWindTime = 0;
	Calendar cal;
	
	public Environnement(World world) {
        world.input.addKeyListener(KeyEvent.VK_P, new Runnable() {

            @Override
            public void run() {
            	windForce = 1;
            	lastWindTime = Calendar.getInstance().getTimeInMillis();
                System.out.println("WIND");
            }
        });
	}
	
	public void affectWind(Flyer entity, float dt) {
		// if no Wind no affectation
		if (windForce < 0.01f) return ;
		
		float currentMili = Calendar.getInstance().getTimeInMillis() - lastWindTime;
		windForce = Helper.map(currentMili, 0, duration, 1, 0);

		entity.translation = entity.translation.mul(
			Transform.translate(
				entity.translation.getMatrix().mulDir(
					new Vector3( +windForce * dt, 0, -(windForce * dt)/2)
				)
			)
		);
	}
	
	public void affect(Flyer entity, float dt) {
		affectWind(entity, dt);
	}
}
