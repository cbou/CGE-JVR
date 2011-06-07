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
import de.bht.jvr.core.attributes.AttributeVector3;
import de.bht.jvr.math.Vector3;

public class Terrain extends Entity{

	private SceneNode box;
	private TriangleMesh triangleMesh;
	ShapeNode meshNode;
	private PApplet noiseMaker = new PApplet();
	Material mt;
	private float amplitude = 20;
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

			mesh = createTriangleArea(20,20, xOffset,zOffset);

			indices = new int[mesh.positions.length];		
			for (int i=0;i<mesh.positions.length;indices[i]=i++);

			box = ColladaLoader.load(new File("models/sphere.dae"));
			mat = fetchMat(box,"null_Shape");
			triangleMesh = new TriangleMesh(indices, mesh.positions, mesh.normals, null, null, null);
			meshNode = new ShapeNode("terrain",triangleMesh, mat);
			node = new GroupNode();
			node.addChildNode(box);
			node.addChildNode(meshNode);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

//		if (shape != null) {
//			Vector3f inertia = new Vector3f(0, 0, 0);
//			shape.calculateLocalInertia(mass, inertia);
//
//			RigidBodyConstructionInfo info = new RigidBodyConstructionInfo(mass, this, shape,
//					inertia);
//			info.restitution = 0.4f;
//			info.linearDamping = 0.2f;
//			info.angularDamping = 0.2f;
//
//			body = new RigidBody(info);
//			body.setUserPointer(this);
//
//			if (mass == 0.0f)
//				body.setActivationState(CollisionObject.DISABLE_DEACTIVATION);
//		} else {
			body = null;
//		}
	}

	private float[] createNormals(float[] positions) {
		float[] tmp = new float[positions.length];
		for (int i=0;i<tmp.length;i+=3){
			float x = positions[i];
			float z = positions[i+2];
			float xDiff = noise((x+10),z)-noise((x-10), z);
			float zDiff = noise(x,(z+10))-noise(x, (z-10));

			tmp[i]   = xDiff;
			tmp[i+1] = 0.5f;
			tmp[i+2] = zDiff;
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
			tmp[triPair+1] = amplitude*noise(x+i*h,z);
			tmp[triPair+2] = z;
			
			tmp[triPair+3] = x+i*h;
			tmp[triPair+4] = amplitude*noise(x+i*h,z+h);
			tmp[triPair+5] = z+h;

			tmp[triPair+6] = x+i*h+h;
			tmp[triPair+7] = amplitude*noise(x+i*h+h,z);
			tmp[triPair+8] = z;

			tmp[triPair+9]  = x+i*h;
			tmp[triPair+10] = amplitude*noise(x+i*h,z+h);
			tmp[triPair+11] = z+h;

			tmp[triPair+12] = x+i*h+h;
			tmp[triPair+13] = amplitude*noise(x+i*h+h,z+h);
			tmp[triPair+14] = z+h;

			tmp[triPair+15] = x+i*h+h;
			tmp[triPair+16] = amplitude*noise(x+i*h+h,z);
			tmp[triPair+17] = z;
		}
		return tmp;
	}


	private float noise(float x, float y) {
		noiseMaker.noiseDetail(4,0.1f);
//		System.out.println("nn");
//		return (float) Math.sqrt(noiseMaker.noise(x,y));
		return (float) noiseMaker.noise(x,y);
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
		try {
			mesh  = createTriangleArea(20,20, 
					(int)(Math.round(xOffset/10)*10)-50, 
					(int)(Math.round(zOffset/10)*10)-50);	
			
			triangleMesh.setVertices(new TriangleMesh(indices, mesh.positions, mesh.normals, null, null, null).getVertices());
			triangleMesh.setAttribute("jvr_Normal",  new AttributeVector3(mesh.normals));
		
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
