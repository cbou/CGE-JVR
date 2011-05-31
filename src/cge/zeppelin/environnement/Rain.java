package cge.zeppelin.environnement;

import java.util.Random;

import de.bht.jvr.math.Vector3;

public class Rain extends AbstractEnvironnementElement {
	
	public Rain() {
		super();
		
		forceMax = 2;
		durationMax = 6000;
		
		Random randomGenerator = new Random();
		
		direction = new Vector3(0,
				-1,
				0);
		force = new Vector3(0,
				randomGenerator.nextInt(forceMax)+1,
				0);
	}
}
