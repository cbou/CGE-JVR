package cge.zeppelin.environnement;

import java.util.Calendar;

import processing.core.PApplet;

import cge.zeppelin.util.Helper;
import de.bht.jvr.math.Vector3;

public abstract class AbstractEnvironnementElement {

	Vector3 direction;
	protected Vector3 force;
	
	long startAt;
	long currentMili;
	
	int forceMax;

	float durationMax;
	
	public AbstractEnvironnementElement() {
		startAt = Calendar.getInstance().getTimeInMillis();
		currentMili = 0;
	}
	
	public void update() {
		if (currentMili < durationMax + 1) { 
			currentMili = Calendar.getInstance().getTimeInMillis() - startAt;
		}		
	}
	
	public Vector3 getForce() {
		float multiplicator = Math.abs(Math.abs(PApplet.map(currentMili, 0, durationMax, -forceMax, forceMax)) - forceMax);
		
		return new Vector3( force.x() * multiplicator,
				force.y() * multiplicator,
				force.z() * multiplicator
		);
	}
	
	public boolean stop() {
		return durationMax / currentMili < 1.0f;
	}

}
