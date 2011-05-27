package cge.zeppelin;

import java.io.File;

import de.bht.jvr.collada14.loader.ColladaLoader;
import de.bht.jvr.core.GroupNode;
import de.bht.jvr.core.SceneNode;
import de.bht.jvr.core.Transform;
import de.bht.jvr.math.Vector3;

public class Checkpoint extends Entity {
	
	private SceneNode sphereModel;
	float size;
	public Checkpoint(GroupNode n, float s, Vector3 start){
		//node;
		size = s;
		try {
			sphereModel 	= ColladaLoader.load(new File("models/sphere.dae"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		sphereModel.setTransform(Transform.scale(size, size, size));
		node 	= new GroupNode();
		node.addChildNode(sphereModel);
		node.setTransform(Transform.translate(start));
		n.addChildNode(node);
	}

}
