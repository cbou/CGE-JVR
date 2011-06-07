package cge.zeppelin;

import java.io.File;

import javax.vecmath.Vector3f;

import processing.core.PApplet;

import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;

import de.bht.jvr.collada14.loader.ColladaLoader;
import de.bht.jvr.core.Finder;
import de.bht.jvr.core.GroupNode;
import de.bht.jvr.core.Material;
import de.bht.jvr.core.SceneNode;
import de.bht.jvr.core.ShapeNode;
import de.bht.jvr.core.TriangleMesh;
import de.bht.jvr.math.Vector3;

public class Terrain extends Entity{

	private SceneNode box;
	private TriangleMesh triangleMesh;
	private ShapeNode meshNode;
	private PApplet noiseMaker = new PApplet();
	Material mt;
	private float amplitude = 1;
	private Material mat;
	private int[] indices;
	private terrainMesh mesh;
	private float zOffset = 0;
	private float xOffset = 0;
	
	
	Terrain() {
		try {
			float[] texCoords;
			float[] tangents;
			float[] binormals;
			
			mesh = createTriangleArea(10,10, xOffset,zOffset);
		
			indices = new int[mesh.positions.length];		
			for (int i=0;i<mesh.positions.length;indices[i]=i++);
			
			box = ColladaLoader.load(new File("models/sphere.dae"));
			mat = fetchMat(box,"null_Shape");
			triangleMesh = new TriangleMesh(indices, mesh.positions, mesh.normals, null, null, null);
			meshNode = new ShapeNode("terrain",triangleMesh, mat);
			node = new GroupNode();
			//node.addChildNode(box);
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
			
			float xDiff = noise((x-1)*10,y*10)-noise((x+1)*10, y*10);
			float yDiff = noise(x*10,(y-1)*10)-noise(x*10, (y+1)*10);
			
			tmp[i]   = xDiff;
			tmp[i+1] = yDiff;
			tmp[i+2] = 0.5f;
		}
		return tmp;
	}

	private terrainMesh createTriangleArea(int rows, int columns, float x, float z) {
		terrainMesh tmpMesh = new terrainMesh();
		tmpMesh.positions = new float[rows*columns*9];
	
		for(int row=0;row<rows;row++){
			float[] tmp= createTriangleStripe(columns,x,z+row*10, 10);
			System.arraycopy(tmp, 0, tmpMesh.positions, row*tmp.length, tmp.length);
		}
		//TODO in einem Schritt die Normalen richtig berechnen!
		tmpMesh.normals = createNormals(tmpMesh.positions);
		
		return tmpMesh;
	}

	
	private Material fetchMat(SceneNode node,String name) {
	    ShapeNode shape = Finder.find(node, ShapeNode.class, name);
	    return shape.getMaterial();
	    
	}
	
	private float[] createTriangleStripe(int triangles, float x, float z, int h){
		
		float[] tmp = new float[triangles*9];
		for (int i=0;i<triangles/2;i++){
			int triPair    = i*18;
			tmp[triPair]   = x+i*h;
			tmp[triPair+1] = amplitude*noise(i*h,z);
			tmp[triPair+2] = z;
			
			tmp[triPair+3] = x+i*h;
			tmp[triPair+4] = amplitude*noise(i*h,z+h);
			tmp[triPair+5] = z+h;
			
			tmp[triPair+6] = x+i*h+h;
			tmp[triPair+7] = amplitude*noise(i*h+h,z);
			tmp[triPair+8] = z;
			
			tmp[triPair+9]  = x+i*h;
			tmp[triPair+10] = amplitude*noise(i*h,z+h);
			tmp[triPair+11] = z+h;
		
			tmp[triPair+12] = x+i*h+h;
			tmp[triPair+13] = amplitude*noise(i*h+h,z+h);
			tmp[triPair+14] = z+h;
			
			tmp[triPair+15] = x+i*h+h;
			tmp[triPair+16] = amplitude*noise(i*h+h,z);
			tmp[triPair+17] = z;
		}
		return tmp;
	}

	
	private float noise(float x, float y) {
		return noiseMaker.noise(x,y);
	}
	
	public void manipulate(float elapsed){
		
	}

	class terrainMesh{
		float[] positions ;
		float[] normals ;
		
	}

	public float getHeight(float x, float y) {
		return amplitude*noise(x, y);
	}

	public void postPosition(Vector3 translation) {
		xOffset = translation.x();
		zOffset = translation.z();
		
//		node.removeAllChildNodes();
		try {
//
			mesh 		 = createTriangleArea(10,10, xOffset-15, zOffset-15);	
//			triangleMesh = new TriangleMesh(indices, mesh.positions, mesh.normals, null, null, null);
			triangleMesh.setVertices(new TriangleMesh(indices, mesh.positions, mesh.normals, null, null, null).getVertices());
			//			node.removeChildNode(meshNode);

//			meshNode 	 = new ShapeNode("terrain",triangleMesh, mat);
//			node.removeAllChildNodes();
//			node.addChildNode(meshNode);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
