package cge.zeppelin;

import cge.zeppelin.util.Helper;
import de.bht.jvr.collada14.loader.ColladaLoader;
import de.bht.jvr.core.GroupNode;
import de.bht.jvr.core.SceneNode;
import de.bht.jvr.core.Transform;

public class Zeppelin extends Entity {

	private SceneNode hull;
	private SceneNode cockpit;
	private SceneNode gasMeter;
	private float gasRot;
	private GroupNode gasNode;
	private float gasMeterHeight = 0.04f;
	private GroupNode loadNode;
	private float loadRot;
	
	public Zeppelin(GroupNode n){
		try {
			node = n;
			//TODO Modell
			hull 	= ColladaLoader.load(Helper.getFileResource("models/sphere.dae"));
			cockpit = ColladaLoader.load(Helper.getFileResource("models/box.dae"));
			gasMeter= ColladaLoader.load(Helper.getFileResource("models/box.dae"));

			hull.setTransform(Transform.scale(1, 1, 6).mul(Transform.translate(0, 1f, 0)));
			cockpit.setTransform(Transform.scale(0.4f, 0.5f, 1));
		
			GroupNode xformN 	= new GroupNode();
			GroupNode sizeN 	= new GroupNode();
		    gasNode 			= new GroupNode();
		    loadNode 			= new GroupNode();
			
			gasNode.setTransform(Transform.translate(-0.1f, -0.1f, -0.2f));
			gasNode.addChildNode(gasMeter);
			gasMeter.setTransform(Transform.translate(0,gasMeterHeight/2,0).mul(Transform.scale(gasMeterHeight/10, gasMeterHeight, gasMeterHeight/10)));
			
			loadNode.setTransform(Transform.translate(0.1f, -0.1f, -0.2f));
			loadNode.addChildNode(gasMeter);

			node.addChildNode(xformN);
			xformN.addChildNode(sizeN);
			
			sizeN.addChildNode(hull);
			sizeN.addChildNode(cockpit);

			node.addChildNode(gasNode);
			node.addChildNode(loadNode);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void updateState(float gas, float load) {
		gasNode.setTransform(gasNode.getTransform().mul(Transform.rotateZ(-gasRot)));
		gasRot = (float) (2-(gas/25f*Math.PI));
		gasNode.setTransform(gasNode.getTransform().mul(Transform.rotateZ(gasRot)));
		
		loadNode.setTransform(loadNode.getTransform().mul(Transform.rotateZ(-loadRot)));
		loadRot = (float) (2-(load/15f*Math.PI));
		loadNode.setTransform(loadNode.getTransform().mul(Transform.rotateZ(loadRot)));
		
	}
	
}
