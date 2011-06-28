package cge.zeppelin;

import java.util.ArrayList;

import de.bht.jvr.core.GroupNode;
import de.bht.jvr.math.Vector3;

public class RaceManager {

	GroupNode node;
	World world;
	ArrayList<Checkpoint> checkpoints = new ArrayList<Checkpoint>();
	int status = 0;
	float sumOfElapsed = 0;
	
	public RaceManager(World w, GroupNode n) {
		node = n;
		world = w;
		
		populate();
	}
	
	public void addCheckpoint(Checkpoint checkpoint){
		world.add(checkpoint);
		checkpoints.add(checkpoint);
	}
	
	public void populate() {
		addCheckpoint(new Checkpoint(node, 3, new Vector3(3,10,-10)));
		addCheckpoint(new Checkpoint(node, 3, new Vector3(2,8,-20)));
		addCheckpoint(new Checkpoint(node, 3, new Vector3(-6, 6,-30)));
		addCheckpoint(new Checkpoint(node, 3, new Vector3(-14, 4,-30)));
		
		checkpoints.get(0).activateArrow();
	}
	
	public void update(float elapsed) {
		// uncomment this to modify position in debug mode
		/*
		checkpoints.get(0).node.setTransform(Transform.translate(3,10,-10));
		checkpoints.get(1).node.setTransform(Transform.translate(2,8,-20));
		checkpoints.get(2).node.setTransform(Transform.translate(-6, 6,-30));
		checkpoints.get(3).node.setTransform(Transform.translate(-14, 4,-30));
		*/
		
		Checkpoint currentCheckpoint = null, nextCheckpoint = null;
		
		currentCheckpoint = checkpoints.get(status);
		if (status < checkpoints.size() - 1) {
			nextCheckpoint = checkpoints.get(status + 1);
		}
		Vector3 checkpointVector = currentCheckpoint.node.getTransform().extractTranslation().getMatrix().translation();
		Vector3 flyerVector = world.flyer.node.getTransform().extractTranslation().getMatrix().translation();
		
		if (flyerVector.sub(checkpointVector).length() < currentCheckpoint.size) {			
			if (status < checkpoints.size() -1) {
				status++;
				System.out.printf("Great! Checkpoint number %s Ok!\n", status);
			} else {
				sumOfElapsed += elapsed;
				if (sumOfElapsed > 3) {
					System.out.println("You won!");
				}
			}
			
			currentCheckpoint.deactivateArrow();
			if (nextCheckpoint instanceof Checkpoint) {
				nextCheckpoint.activateArrow();
			}
		} else {
			sumOfElapsed = 0;
		}
	}
}
