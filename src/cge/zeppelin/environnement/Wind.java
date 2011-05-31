package cge.zeppelin.environnement;

import java.util.Random;

import de.bht.jvr.math.Vector3;

public class Wind extends AbstractEnvironnementElement {
	
	public Wind() {
		super();
		
		forceMax = 4;
		durationMax = 6000;
		
		Random randomGenerator = new Random();
		
		direction = new Vector3(randomGenerator.nextInt(2) - 1,
				0,
				randomGenerator.nextInt(2) - 1);

		force = new Vector3(randomGenerator.nextInt(forceMax)+1,
				0,
				randomGenerator.nextInt(forceMax)+1);
	}
}
