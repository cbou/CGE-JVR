package cge.zeppelin;

import java.io.File;

import de.bht.jvr.collada14.loader.ColladaLoader;
import de.bht.jvr.core.GroupNode;
import de.bht.jvr.core.SceneNode;
import de.bht.jvr.core.Transform;

public class Zeppelin extends Entity{

	private SceneNode hull;
	private SceneNode cockpit;
	private SceneNode gasLevel;
	
	public Zeppelin(GroupNode n){
		try {
			//TODO Modell
			hull 	= ColladaLoader.load(new File("models/sphere.dae"));
			cockpit = ColladaLoader.load(new File("models/box.dae"));
			gasLevel = ColladaLoader.load(new File("models/box.dae"));

			hull.setTransform(Transform.scale(1, 1, 6).mul(Transform.translate(0, 1f, 0)));
			cockpit.setTransform(Transform.scale(0.4f, 0.5f, 1));
			gasLevel.setTransform(Transform.scale(0.1f, 0.1f, 0.1f).mul(Transform.translate(0, -1f, -2)));

			GroupNode xformN 	= new GroupNode();
			GroupNode sizeN 	= new GroupNode();
			xformN.addChildNode(sizeN);
			sizeN.addChildNode(hull);
			sizeN.addChildNode(cockpit);
			node.addChildNode(sizeN);
						
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
}
