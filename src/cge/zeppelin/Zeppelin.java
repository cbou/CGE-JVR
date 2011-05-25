package cge.zeppelin;

import java.io.File;

import javax.vecmath.Vector3f;

import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;

import de.bht.jvr.collada14.loader.ColladaLoader;
import de.bht.jvr.core.BBox;
import de.bht.jvr.core.GroupNode;
import de.bht.jvr.core.SceneNode;
import de.bht.jvr.core.Transform;

public class Zeppelin extends Entity{

	
	public Zeppelin(GroupNode n){
		try {
			node = n;
			
			//TODO Modell
			hull 	= ColladaLoader.load(new File("models/sphere.dae"));
			cockpit = ColladaLoader.load(new File("models/box.dae"));
			gasLevel = ColladaLoader.load(new File("models/box.dae"));

			hull.setTransform(Transform.scale(1, 1, 6).mul(Transform.translate(0, 1f, 0)));
			cockpit.setTransform(Transform.scale(0.4f, 0.5f, 1));
			gasLevel.setTransform(Transform.scale(0.1f, 0.1f, 0.1f).mul(Transform.translate(0, -1f, -2)));

			CollisionShape bs = createBoundingShape(hull);
			float mass = 0;//density * size.x() * size.y() * size.z();

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
	
	private SceneNode hull;
	private SceneNode cockpit;
	private float gas = 1;
	private SceneNode gasLevel;
	private Entity entity;

	public Entity createEntitiy(){
		try {
			
			//TODO Modell
			hull 	= ColladaLoader.load(new File("models/sphere.dae"));
			cockpit = ColladaLoader.load(new File("models/box.dae"));
			gasLevel = ColladaLoader.load(new File("models/box.dae"));
			
			hull.setTransform(Transform.scale(1, 1, 6).mul(Transform.translate(0, 1f, 0)));
			cockpit.setTransform(Transform.scale(0.4f, 0.5f, 1));
			gasLevel.setTransform(Transform.scale(0.1f, 0.1f, 0.1f).mul(Transform.translate(0, -1f, -2)));
			
			CollisionShape bs = createBoundingShape(hull);
			float mass = 0;//density * size.x() * size.y() * size.z();

			GroupNode xformN 	= new GroupNode();
			GroupNode sizeN 	= new GroupNode();
			xformN.addChildNode(sizeN);
			sizeN.addChildNode(hull);
			sizeN.addChildNode(cockpit);
//			sizeN.addChildNode(gasLevel);
			
			//xformN.setTransform(new Transform(initialXform));
			//sizeN.setTransform(Transform.scale(1, 1, 1));
//			entity = new Entity(xformN, bs, mass){
//				 void manipulate(float elapsed, World world) {
//					 gasLevel.setTransform(Transform.scale(0.1f,gas-25,0.1f));
////					 System.out.println(gas);
//				 }
//			};
			
			return entity;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public void setGas(float amount){
		this.gas = amount;
	}
	
	private CollisionShape createBoundingShape(SceneNode node){
		BBox box = node.getBBox();
		return  new BoxShape(new Vector3f(0.5f * box.getWidth(), 0.5f * box.getHeight(), 0.5f * box.getDepth()));
	}

}
