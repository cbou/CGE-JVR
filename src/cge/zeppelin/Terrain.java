package cge.zeppelin;

import java.io.File;

import javax.vecmath.Vector3f;

import processing.core.PApplet;

import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;

import de.bht.jvr.collada14.loader.ColladaLoader;
import de.bht.jvr.core.GroupNode;
import de.bht.jvr.core.Material;
import de.bht.jvr.core.SceneNode;
import de.bht.jvr.core.ShapeNode;
import de.bht.jvr.core.TriangleMesh;

public class Terrain extends Entity{

	private SceneNode box;
	Entity b;
	private TriangleMesh mesh;
	ShapeNode meshNode;
	PApplet noiseMaker = new PApplet();
	
	Terrain() {
		try {
			int[] indices;
			float[] positions ;
			float[] normals ;
			
			float[] texCoords = new float[]{1,1,1,1,1,1};
			float[] tangents = new float[]{0,0,0,1,1,1};
			float[] binormals = new float[]{1,1,1,1,1,1};
			
			positions = createTriangleArea(20,20);
			normals = createNormals(positions);
			indices = new int[positions.length];		
			for (int i=0;i<positions.length;indices[i]=i++);
			
			box = ColladaLoader.load(new File("models/sphere.dae"));
			mesh = new TriangleMesh(indices, positions, normals, null, null, null);
			fetchMat(box);
			
			meshNode = new ShapeNode("flup",mesh,mt);
			node = new GroupNode();
			node.addChildNode(box);
			node.addChildNode(meshNode);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (shape != null) {
			Vector3f inertia = new Vector3f(0, 0, 0);
			shape.calculateLocalInertia(mass, inertia);

			RigidBodyConstructionInfo info = new RigidBodyConstructionInfo(mass, this, shape,
					inertia);
			info.restitution = 0.4f;
			info.linearDamping = 0.2f;
			info.angularDamping = 0.2f;

			body = new RigidBody(info);
			body.setUserPointer(this);

			if (mass == 0.0f)
				body.setActivationState(CollisionObject.DISABLE_DEACTIVATION);
		} else {
			body = null;
		}
	}
	
	private float[] createNormals(float[] positions) {
		float[] tmp = new float[positions.length];
		for (int i=0;i<tmp.length;i+=3){
			float x = positions[i];
			float y = positions[i+1];
			
			float xDiff = noise(x,y)-noise(x+1, y);
			float yDiff = noise(x,y)-noise(x, y+1);
			tmp[i]   = xDiff;
			tmp[i+1] = yDiff;
			tmp[i+2] = 1;
		}
		return tmp;
	}

	private float[] createTriangleArea(int rows, int columns) {
		float[][] tmp = new float[rows][];
		for(int y=0;y<rows;y++){
			tmp[y] = createTriangleStripe(columns,y);
		}
		
		int rowlength= tmp[0].length;
		float[] ret = new float[tmp.length*rowlength];
		for (int row=0;row<rows;row++){
			System.arraycopy(tmp[row], 0, ret, row*rowlength, rowlength);
		}
		return ret;
	}

	Material mt;
	
	private void fetchMat(SceneNode node) {
//		Material tmp;
		if (node instanceof ShapeNode){
			System.out.println("MAT");
			mt = (((ShapeNode)node).getMaterial());
			System.out.println(mt);
		} else if (node instanceof GroupNode) {
			for (SceneNode cn:((GroupNode)node).getChildNodes()){
//				System.out.println(cn);
//				return(fetchMat(cn));
				fetchMat(cn);
			}
		} 
//		return fetchMat(node);
		 
	}
	
	private float[] createTriangleStripe(int triangles, int y){
		int h = 1;
		float z = 2;
		float[]tmp = new float[triangles*9];
		for (int i=0;i<triangles/2;i++){
			int triPair = i*18;
			z = (float) Math.random();
			tmp[triPair]   = i*h;
			tmp[triPair+1] = z*noise(i*h,y);
			tmp[triPair+2] = y;
			
			tmp[triPair+3] = i*h;
			tmp[triPair+4] = z*noise(i*h,y+h);
			tmp[triPair+5] = y+h;
			
			tmp[triPair+6] = i*h+h;
			tmp[triPair+7] = z*noise(i*h+h,y);
			tmp[triPair+8] = y;
			
			tmp[triPair+9] = i*h;
			tmp[triPair+10] = z*noise(i*h,y+h);
			tmp[triPair+11] = y+h;
		
			tmp[triPair+12] = i*h+h;
			tmp[triPair+13] = z*noise(i*h+h,y+h);
			tmp[triPair+14] = y+h;
			
			tmp[triPair+15] = i*h+h;
			tmp[triPair+16] = z*noise(i*h+h,y);
			tmp[triPair+17] = y;
		}
		return tmp;
	}

	
	private float noise(float x, float y) {
		return noiseMaker.noise(x,y);
	}

	public static void main(String[] args) {
	
		Terrain t = new Terrain();
		float[] ps = t.createTriangleArea(1,1);
		for (int i=0;i<ps.length;i+=3){
			System.out.println(ps[i]+" "+ps[i+1]+" "+ps[i+2]);
		}
		
	}
}
