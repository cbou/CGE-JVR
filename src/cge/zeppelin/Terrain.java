package cge.zeppelin;

import java.io.File;

import cge.zeppelin.util.Helper;

import processing.core.PApplet;
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
	private float amplitude = 10;
	private Material mat;
	private int[] indices;
	private terrainMesh mesh;
	private float zOffset = 0;
	private float xOffset = 0;
	private int grid  = 20;
	private int xSize = 40; //Fuer quadratisch halb so gross wie ySize
	private int zSize = 80;
	private float oldXOffset = Float.MAX_VALUE;
	private float oldZOffset = Float.MAX_VALUE;

	Terrain() {
		try {
			float[] texCoords;
			float[] tangents;
			float[] binormals;

			mesh = createTriangleArea(xSize,zSize, xOffset,zOffset);

			indices = new int[mesh.positions.length];		
			for (int i=0;i<mesh.positions.length;indices[i]=i++);

			box 		 = ColladaLoader.load(Helper.getFileResource("models/sphere.dae"));
			mat 		 = fetchMat(box,"null_Shape");
			triangleMesh = new TriangleMesh(indices, mesh.positions, mesh.normals, mesh.textCoords, null, null);
			meshNode 	 = new ShapeNode("terrain",triangleMesh, mat);
			node 		 = new GroupNode();
			node.addChildNode(meshNode);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		body = null;
	}

	private float[] createNormals(float[] positions) {
		float[] tmp = new float[positions.length];
		for (int i=0;i<tmp.length;i+=3){
			float x = positions[i];
			float z = positions[i+2];
			float xDiff = noise((x+grid),z)-noise((x-grid), z);
			float zDiff = noise(x,(z+grid))-noise(x, (z-grid));

			tmp[i]   = xDiff;
			tmp[i+1] = 0.6f;
			tmp[i+2] = zDiff;
		}
		return tmp;
	}

	private terrainMesh createTriangleArea(int rows, int columns, float x, float z) {
		terrainMesh tmpMesh = new terrainMesh();
		tmpMesh.positions 	= new float[rows*columns*9];
		for(int row=0;row<rows;row++){
			float[] tmp= createTriangleStripe(columns,x,z+row*grid, grid);
			System.arraycopy(tmp, 0, tmpMesh.positions, row*tmp.length, tmp.length);
		}
		//TODO in einem Schritt die Normalen richtig berechnen!
		tmpMesh.normals 	= createNormals(tmpMesh.positions);
		tmpMesh.textCoords 	= createTextCoords(tmpMesh.positions);
		return tmpMesh;
	}


	private float[] createTextCoords(float[] positions) {
		float[] tmp = new float[(positions.length/3)*2];
		for (int i=0;i<tmp.length;i+=2){
			tmp[i]   = 0;//(float) Math.random();//positions[i];
			tmp[i+1] = 1;//(float) Math.random();//positions[i+2];
		}
		return tmp;
	}

	private Material fetchMat(SceneNode node,String name) {
		ShapeNode shape = Finder.find(node, ShapeNode.class, name);
		return shape.getMaterial();

	}

	private float[] createTriangleStripe(int triangles, float x, float z, int h){

		float[] tmp  = new float[triangles*9];
		
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
		return (float) noiseMaker.noise(x,y);
	}

	class terrainMesh{
	    float[] textCoords;
		float[] positions ;
		float[] normals ;
	}

	public float getHeight(float x, float y) {
		return amplitude*noise(x, y);
	}

	public void postPosition(Vector3 translation) {
		
		xOffset = translation.x();
		zOffset = translation.z();
		
		if (Math.abs(xOffset-oldXOffset)>=10 | Math.abs(zOffset-oldZOffset)>=10){
			oldXOffset = xOffset;
			oldZOffset = zOffset;
			try {
				//Dauer 9-24 milliSekunden
				mesh  = createTriangleArea(xSize,zSize, 
						(int)(Math.round(xOffset/grid)*grid)-xSize*5, 
						(int)(Math.round(zOffset/grid)*grid)-zSize*2.5f);	
				triangleMesh.setVertices(new TriangleMesh(indices, mesh.positions, mesh.normals, mesh.textCoords, null, null).getVertices());
				triangleMesh.setAttribute("jvr_Normal",  new AttributeVector3(mesh.normals));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
