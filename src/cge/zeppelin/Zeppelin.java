package cge.zeppelin;

import java.io.File;

import de.bht.jvr.collada14.loader.ColladaLoader;
import de.bht.jvr.core.GroupNode;
import de.bht.jvr.core.SceneNode;
import de.bht.jvr.core.Transform;

public class Zeppelin extends Entity{

	private SceneNode hull;
	private SceneNode cockpit;
	private float gasLevel;
	private float balast;
	private SceneNode gasMeter;
	private float gasHeight = 0.1f;
	private GroupNode sizeN;
	
	public Zeppelin(GroupNode n){
		try {
			node = n;
			//TODO Modell
			hull 	= ColladaLoader.load(new File("models/sphere.dae"));
			cockpit = ColladaLoader.load(new File("models/box.dae"));
			gasMeter= ColladaLoader.load(new File("models/box.dae"));

			hull.setTransform(Transform.scale(1, 1, 6).mul(Transform.translate(0, 1f, 0)));
			cockpit.setTransform(Transform.scale(0.4f, 0.5f, 1));
			gasMeter.setTransform(Transform.scale(0.001f, 0.1f, 0.1f).mul(Transform.translate(-1f, 0, -2)));

			GroupNode xformN 	= new GroupNode();
			sizeN 				= new GroupNode();
			node.addChildNode(xformN);
			xformN.addChildNode(sizeN);
			
			sizeN.addChildNode(hull);
//			sizeN.addChildNode(cockpit);
			node.addChildNode(gasMeter);
						
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void sendState(float gas, float load) {
		this.gasLevel 	= gas;
		this.balast 	= load;
		
//		gasMeter.setTransform(gasMeter.getTransform().mul(Transform.translate(0, -gasHeight/2, 0)));			
//		gasMeter.setTransform(gasMeter.getTransform().mul(Transform.scale(1,1/gasHeight,1)));
//		gasMeter.setTransform(gasMeter.getTransform().mul(Transform.scale(0.9f,1,1)));			
//		
		gasMeter.setTransform(Transform.rotateZ(gasHeight).mul(gasMeter.getTransform()));			
//		
		gasHeight = (float) (gas/25f);
		gasMeter.setTransform(Transform.rotateZ(-gasHeight).mul(gasMeter.getTransform()));			
//		
//		gasMeter.setTransform(gasMeter.getTransform().mul(Transform.translate(0, -gasHeight/2, 0)));			
//		
//		gasMeter.setTransform(gasMeter.getTransform().mul(Transform.scale(1,gasHeight,1)));
//		gasMeter.setTransform(gasMeter.getTransform().mul(Transform.translate(0, gasHeight/2, 0)));
	}
	
}
