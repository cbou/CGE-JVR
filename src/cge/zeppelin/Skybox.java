package cge.zeppelin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.media.opengl.GL2GL3;

import cge.zeppelin.util.Helper;

import de.bht.jvr.collada14.loader.ColladaLoader;
import de.bht.jvr.core.Finder;
import de.bht.jvr.core.Geometry;
import de.bht.jvr.core.GroupNode;
import de.bht.jvr.core.SceneNode;
import de.bht.jvr.core.Shader;
import de.bht.jvr.core.ShaderMaterial;
import de.bht.jvr.core.ShaderProgram;
import de.bht.jvr.core.ShapeNode;
import de.bht.jvr.core.Texture2D;
import de.bht.jvr.core.Transform;
import de.bht.jvr.core.TriangleMesh;
import de.bht.jvr.core.uniforms.UniformVector3;
import de.bht.jvr.math.Vector3;

public class Skybox extends Entity {
    private Texture2D bk, dn, ft, lf, rt, up;
    private Geometry planeGeo;
    private ShaderProgram textureProg;
    SceneNode plane;
	private World world;
	private ShapeNode shapeNode;
   
    public Skybox(World w, GroupNode n) {
    	node = n;
    	world = w;

    	try {
			loadFiles();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		plane.setTransform(Transform.scale(1, 1, 6).mul(Transform.translate(0, 0, -1)));

		shapeNode 	 = new ShapeNode("terrain");
		
		GroupNode xformN 	= new GroupNode();
		GroupNode sizeN 	= new GroupNode();
		
		node.addChildNode(xformN);
		xformN.addChildNode(sizeN);
		
		sizeN.addChildNode(plane);
    }
    
    private void loadFiles() throws FileNotFoundException, Exception {
    	
        plane = ColladaLoader.load(Helper.getFileResource("models/plane.dae"));

        bk = new Texture2D(Helper.getFileResource("textures/sky/mountain_ring_bk.jpg"));
        dn = new Texture2D(Helper.getFileResource("textures/sky/mountain_ring_dn.jpg"));
        ft = new Texture2D(Helper.getFileResource("textures/sky/mountain_ring_ft.jpg"));
        lf = new Texture2D(Helper.getFileResource("textures/sky/mountain_ring_lf.jpg"));
        rt = new Texture2D(Helper.getFileResource("textures/sky/mountain_ring_rt.jpg"));
        up = new Texture2D(Helper.getFileResource("textures/sky/mountain_ring_up.jpg"));

        textureProg = new ShaderProgram(Helper.getFileResource("shaders/sky.vs"), Helper.getFileResource("shaders/sky.fs"));
    }
    
    /**
     * just for test purpose.
     */
    public void update() {
    }
	
	public void refreshShader() {
		try {
			// load texture
			Texture2D texture = new Texture2D(Helper.getFileResource("textures/grass.jpg"));
	        texture.bind(world.renderer.ctx);
	        
			Shader ambientVs = new Shader(Helper.getInputStreamResource("shaders/sky.vs"), GL2GL3.GL_VERTEX_SHADER);
	        Shader ambientFs = new Shader(Helper.getInputStreamResource("shaders/sky.fs"), GL2GL3.GL_FRAGMENT_SHADER);
	        ShaderProgram ambientProgram = new ShaderProgram(ambientVs, ambientFs);
	        
	        ambientFs.compile(world.renderer.ctx);
	        ShaderMaterial earthMat = new ShaderMaterial();
	        earthMat.setUniform("AMBIENT", "toonColor", new UniformVector3(new Vector3(1, 1, 1)));
	        earthMat.setUniform("LIGHTING", "toonColor", new UniformVector3(new Vector3(1, 1, 1)));
	        earthMat.setTexture("AMBIENT", "jvr_Texture0", texture);
	        earthMat.setShaderProgram("AMBIENT", ambientProgram);
	
	        shapeNode.setMaterial(earthMat);
		} catch (IOException e) {
			e.printStackTrace();
	    } catch (Exception e) {
	    	e.printStackTrace();
	    	System.out.println("Can not compile shader!");
		}
	}
}
