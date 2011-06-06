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

public class Terrain extends Entity{

	
	private SceneNode box;
	private TriangleMesh triangleMesh;
	private ShapeNode meshNode;
	private PApplet noiseMaker = new PApplet();
	
	Terrain() {
		try {
			int[] indices;
			terrainMesh mesh = new terrainMesh();
			float[] texCoords;
			float[] tangents;
			float[] binormals;
			
			mesh = createTriangleArea(10,10);
		
			indices = new int[mesh.positions.length];		
			for (int i=0;i<mesh.positions.length;indices[i]=i++);
			
			box = ColladaLoader.load(new File("models/sphere.dae"));
			triangleMesh = new TriangleMesh(indices, mesh.positions, mesh.normals, null, null, null);
			meshNode = new ShapeNode("terrain",triangleMesh, fetchMat(box,"null_Shape"));
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

	private terrainMesh createTriangleArea(int rows, int columns) {
		terrainMesh tmpMesh = new terrainMesh();
		tmpMesh.positions = new float[rows*columns*9];
	
		for(int row=0;row<rows;row++){
			float[] tmp= createTriangleStripe(columns,row*10, 10);
			System.arraycopy(tmp, 0, tmpMesh.positions, row*tmp.length, tmp.length);
		}
		//TODO in einem Schritt die Normalen richtig berechnen!
		tmpMesh.normals = createNormals(tmpMesh.positions);
		
		return tmpMesh;
	}

	Material mt;
	
	private Material fetchMat(SceneNode node,String name) {
	    ShapeNode shape = Finder.find(node, ShapeNode.class, name);
	    return shape.getMaterial();
	    
	}
	
	private float[] createTriangleStripe(int triangles, int y, int h){
		float z = 10;
		float[] tmp = new float[triangles*9];
		for (int i=0;i<triangles/2;i++){
			int triPair = i*18;
			tmp[triPair]   = i*h;
			tmp[triPair+1] = z*noise(i*h,y);
			tmp[triPair+2] = y;
			
			tmp[triPair+3] = i*h;
			tmp[triPair+4] = z*noise(i*h,y+h);
			tmp[triPair+5] = y+h;
			
			tmp[triPair+6] = i*h+h;
			tmp[triPair+7] = z*noise(i*h+h,y);
			tmp[triPair+8] = y;
			
			tmp[triPair+9]  = i*h;
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

	class terrainMesh{
		float[] positions ;
		float[] normals ;
		
	}

}
