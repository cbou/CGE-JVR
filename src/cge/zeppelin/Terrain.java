package cge.zeppelin;

import java.io.IOException;

import javax.media.opengl.GL2GL3;

import processing.core.PApplet;
import cge.zeppelin.util.Helper;
import de.bht.jvr.core.GroupNode;
import de.bht.jvr.core.Material;
import de.bht.jvr.core.Shader;
import de.bht.jvr.core.ShaderMaterial;
import de.bht.jvr.core.ShaderProgram;
import de.bht.jvr.core.ShapeNode;
import de.bht.jvr.core.Texture2D;
import de.bht.jvr.core.TriangleMesh;
import de.bht.jvr.core.attributes.AttributeVector3;
import de.bht.jvr.core.uniforms.UniformFloat;
import de.bht.jvr.math.Vector2;
import de.bht.jvr.math.Vector3;

public class Terrain extends Entity{

	private static final float WATERLEVEL = 0;
	private TriangleMesh triangleMesh;
	private ShapeNode meshNode;
	private PApplet noiseMaker = new PApplet();
	private float amplitude = 20;
	private Material mat;
	private int[] indices;
	private terrainMesh mesh;
	private float zOffset = 0;
	private float xOffset = 0;
	private int grid  = 20;
	private int xSize = 60; //Fuer quadratisch halb so gross wie ySize
	private int zSize = 120;
	private float oldXOffset = Float.MAX_VALUE;
	private float oldZOffset = Float.MAX_VALUE;
	private Texture2D textureHigh;
	private Texture2D textureMiddle;
	private Texture2D textureLow;
	private float textureScaling = 2f;
	private int basinX;
	private int basinZ;
	private ShaderMaterial earthMat;
	private World world;
	private float bigNoiseAmplitude = 100;
	private boolean debug = false;
	
	Terrain(World w) {
		world = w;
		
		basinX = (int) (Math.random()*100);
		basinZ = (int) (Math.random()*100);
		
		mesh = createTriangleArea(xSize,zSize, xOffset,zOffset);

		indices = new int[mesh.positions.length];		
		for (int i=0;i<mesh.positions.length;indices[i]=i++);

		try {
			triangleMesh = new TriangleMesh(indices, mesh.positions, mesh.normals, mesh.textCoords, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		meshNode 	 = new ShapeNode("terrain", triangleMesh, mat);
		node 		 = new GroupNode();
		node.addChildNode(meshNode);

		body = null;
		
		refreshShader();
	}
	
	public void resetTerrain(){
		mesh  = createTriangleArea(xSize,zSize, 
				(int)(Math.round(xOffset/grid)*grid)-xSize*5, 
				(int)(Math.round(zOffset/grid)*grid)-zSize*2.5f);	
		try {
			triangleMesh.setVertices(new TriangleMesh(indices, mesh.positions, mesh.normals, mesh.textCoords, null, null).getVertices());
			triangleMesh.setAttribute("jvr_Normal",  new AttributeVector3(mesh.normals));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private float[] createNormals(float[] positions) {
		float[] tmp = new float[positions.length];
		
		for (int i=0;i<tmp.length;i+=3){
			
			if (positions[i+1] <= WATERLEVEL){
				tmp[i]   = 0;
				tmp[i+1] = 1.0f;
				tmp[i+2] = 0;
				
			} else {
				
				float x = positions[i];
				float z = positions[i+2];
				float x1Diff = getElevation(x,z)-getElevation((x+grid), z);
				float z1Diff = getElevation(x,z)-getElevation(x, (z+grid));
		
				Vector3 nx1 = new Vector3(-x1Diff,amplitude,0);
				Vector3 nz1 = new Vector3(0,amplitude,-z1Diff);	
				
				float x2Diff = getElevation(x,z)-getElevation((x-grid), z);
				float z2Diff = getElevation(x,z)-getElevation(x, (z-grid));
				
				Vector3 nx2 = new Vector3(-x2Diff,amplitude,0);
				Vector3 nz2 = new Vector3(0,amplitude,-z2Diff);	
				
				Vector3 n1 = nx1.add(nx2).add(nz1).add(nz2);
			
				n1=n1.normalize();
				tmp[i]   = n1.x();
				tmp[i+1] = n1.y();
				tmp[i+2] = n1.z();
			}
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
		//UV are only 2D 
		float[] tmp = new float[(positions.length/3)*2];
		
		for (int i=0;i<tmp.length;i+=12){
			
			tmp[i]   = 0;
			tmp[i+1] = 0;
			
			tmp[i+2] = 0;
			tmp[i+3] = textureScaling;
			
			tmp[i+4] = textureScaling;
			tmp[i+5] = 0;
			
			tmp[i+6] = 0;
			tmp[i+7] = textureScaling;
			
			tmp[i+8] = textureScaling;
			tmp[i+9] = textureScaling;
			
			tmp[i+10] = textureScaling;
			tmp[i+11] = 0;
			
		}
		
		return tmp;
	}

	
	public float getElevation(float x, float z){
		if (debug)	return (float) (amplitude*(1+Math.sin(x*100f)) +  (amplitude*(1+Math.sin(z*100f))));
		basinX = 100;
		basinZ = 100;
		Vector2 basinDistance = new Vector2(x-basinX,z-basinZ);
		float influence =  basinDistance.length() < 100 ? 100-basinDistance.length() : 0;
	
		return (float) (amplitude*noise(x,z) + bigNoiseAmplitude*bigNoise(x, z) - influence);
	}
	
	public float getCollisionElevation(float x, float z){
		float nextX = (int) x / grid*grid;
		float nextZ = (int) z / grid*grid;
		float elev0 = getElevation(nextX, nextZ);
		float elev1 = getElevation(nextX+grid, nextZ);
		float elev2 = getElevation(nextX, nextZ+grid);
		float elev3 = getElevation(nextX+grid, nextZ+grid);
		return Math.max(elev0, Math.max(elev1, Math.max(elev2, elev3)));
	}

	private float[] createTriangleStripe(int triangles, float x, float z, int h){

		float[] tmp  = new float[triangles*9];
		
		for (int i=0;i<triangles/2;i++){
			
			int triPair    = i*18;
			
			/* 1 */
			tmp[triPair]   = x+i*h;
			tmp[triPair+1] = getElevation(x+i*h,z);
			tmp[triPair+2] = z;
			
			/* 2 */
			tmp[triPair+3] = x+i*h;
			tmp[triPair+4] = getElevation(x+i*h,z+h);
			tmp[triPair+5] = z+h;
			
			/* 3 */
			tmp[triPair+6] = x+i*h+h;
			tmp[triPair+7] = getElevation(x+i*h+h,z);
			tmp[triPair+8] = z;

			/* 4 */
			tmp[triPair+9]  = x+i*h;
			tmp[triPair+10] = tmp[triPair+4];
			tmp[triPair+11] = z+h;
			
			/* 5 */
			tmp[triPair+12] = x+i*h+h;
			tmp[triPair+13] = getElevation(x+i*h+h,z+h);
			tmp[triPair+14] = z+h;
			
			/* 6 */
			tmp[triPair+15] = x+i*h+h;
			tmp[triPair+16] = tmp[triPair+7];
			tmp[triPair+17] = z;
			
		}
		
		return tmp;
	}

	private float noise(float x, float y) {
		
		noiseMaker.noiseDetail(2,0.1f);
		return (float) noiseMaker.noise(x,y);
		
	}

	private float bigNoise(float x, float y) {
		
		noiseMaker.noiseDetail(1,0.1f);
		float n = noiseMaker.noise(x/100f,y/100f);
		float f = ((x-200)/400f)*((x-200)/400f)+
				  ((y-200)/400f)*((y-200)/400f);
		
		return n*f;
	}


	public void postPosition(Vector3 translation) {
		
		xOffset = translation.x();
		zOffset = translation.z();
		
		if (Math.abs(xOffset-oldXOffset)>=10 | Math.abs(zOffset-oldZOffset)>=10){
			oldXOffset = xOffset;
			oldZOffset = zOffset;
			
			resetTerrain();
		}
	}
	
	public void refreshShader() {
		
		// can not compile if there is no context
		if (world.renderer.ctx == null) return;
		
		try {
			
			// load texture
			textureHigh    = new Texture2D(Helper.getFileResource("textures/snow.jpg"));
			textureMiddle  = new Texture2D(Helper.getFileResource("textures/rock.jpg"));
			textureLow     = new Texture2D(Helper.getFileResource("textures/grass.jpg"));
		    
			Shader ambientVs = new Shader(Helper.getInputStreamResource("shaders/terrainambient.vs"), GL2GL3.GL_VERTEX_SHADER);
	        Shader ambientFs = new Shader(Helper.getInputStreamResource("shaders/terrainambient.fs"), GL2GL3.GL_FRAGMENT_SHADER);
	        Shader lightingVs = new Shader(Helper.getInputStreamResource("shaders/terrainlighting.vs"), GL2GL3.GL_VERTEX_SHADER);
	        Shader lightingFs = new Shader(Helper.getInputStreamResource("shaders/terrainlighting.fs"), GL2GL3.GL_FRAGMENT_SHADER);
	        
	        // Without compile the game stops if error in shader
	        ambientVs.compile(world.renderer.ctx);
	        ambientFs.compile(world.renderer.ctx);
	        lightingVs.compile(world.renderer.ctx);
	        lightingFs.compile(world.renderer.ctx);
	        
	        ShaderProgram lightingProgram = new ShaderProgram(lightingVs, lightingFs);
	        ShaderProgram ambientProgram = new ShaderProgram(ambientVs, ambientFs);
	        
	        earthMat = new ShaderMaterial();
	        
	        earthMat.setUniform("LIGHTING", "waterLevel", new UniformFloat(WATERLEVEL));
	        earthMat.setUniform("LIGHTING", "ambientFactor", new UniformFloat(1));
	        
	        earthMat.setTexture("LIGHTING", "jvr_TextureHigh", textureHigh);
	        earthMat.setTexture("LIGHTING", "jvr_TextureMiddle", textureMiddle);
	        earthMat.setTexture("LIGHTING", "jvr_TextureLow", textureLow);
			
	        earthMat.setShaderProgram("AMBIENT", ambientProgram);
	        earthMat.setShaderProgram("LIGHTING", lightingProgram);
	          
	        meshNode.setMaterial(earthMat);
	        setBrightness(1);
	        
		} catch (IOException e) {
			e.printStackTrace();
	    } catch (Exception e) {
	    	e.printStackTrace();
	    	System.out.println("Can not compile shader!");
	    }
	    
	}

	public void setBrightness(float val) {
		   earthMat.setUniform("LIGHTING", "ambientFactor", new UniformFloat(val));
	}

	class terrainMesh{
		float[] textCoords;
		float[] positions ;
		float[] normals ;
	}
	
}
