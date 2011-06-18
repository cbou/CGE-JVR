package cge.zeppelin;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.media.opengl.GL2GL3;

import cge.zeppelin.util.Helper;
import de.bht.jvr.collada14.loader.ColladaLoader;
import de.bht.jvr.core.Finder;
import de.bht.jvr.core.GroupNode;
import de.bht.jvr.core.SceneNode;
import de.bht.jvr.core.Shader;
import de.bht.jvr.core.ShaderMaterial;
import de.bht.jvr.core.ShaderProgram;
import de.bht.jvr.core.ShapeNode;
import de.bht.jvr.core.Texture2D;
import de.bht.jvr.core.Transform;

public class Skybox extends Entity {
    private Texture2D bk, dn, ft, lf, rt, up;
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

		plane.setTransform(Transform.scale(1000, 1000, 6).mul(Transform.translate(0, 0, -100f)));
		shapeNode = Finder.find(plane, ShapeNode.class, "Plane01_Shape");
		
		GroupNode groupNode = new GroupNode();
		
		groupNode.addChildNode(plane);
		node.addChildNode(groupNode);
    }
    
    private void loadFiles() throws FileNotFoundException, Exception {
    	
        plane = ColladaLoader.load(Helper.getFileResource("models/plane.dae"));

        bk = new Texture2D(Helper.getFileResource("textures/sky/mountain_ring_bk.jpg"));
        dn = new Texture2D(Helper.getFileResource("textures/sky/mountain_ring_dn.jpg"));
        ft = new Texture2D(Helper.getFileResource("textures/sky/mountain_ring_ft.jpg"));
        lf = new Texture2D(Helper.getFileResource("textures/sky/mountain_ring_lf.jpg"));
        rt = new Texture2D(Helper.getFileResource("textures/sky/mountain_ring_rt.jpg"));
        up = new Texture2D(Helper.getFileResource("textures/sky/mountain_ring_up.jpg"));
    }
    
    /**
     * just for test purpose.
     */
    public void update() {
        this.node.setTransform(Transform.translate(world.renderer.camera.getEyeWorldTransform(world.renderer.root).getMatrix().translation()));
    }
	
	public void refreshShader() {
		try {
			// load texture
			Texture2D texture = new Texture2D(Helper.getFileResource("textures/grass.jpg"));
	        texture.bind(world.renderer.ctx);
	        
			Shader skyVs = new Shader(Helper.getInputStreamResource("shaders/sky.vs"), GL2GL3.GL_VERTEX_SHADER);
	        Shader skyFs = new Shader(Helper.getInputStreamResource("shaders/sky.fs"), GL2GL3.GL_FRAGMENT_SHADER);
	        ShaderProgram ambientProgram = new ShaderProgram(skyVs, skyFs);
	        
	        skyFs.compile(world.renderer.ctx);
	        skyVs.compile(world.renderer.ctx);
	        ShaderMaterial skyMat = new ShaderMaterial();
	        skyMat.setTexture("AMBIENT", "jvr_Texture0", ft);
	        skyMat.setShaderProgram("AMBIENT", ambientProgram);
	        
	        
	        shapeNode.setMaterial(skyMat);
		} catch (IOException e) {
			e.printStackTrace();
	    } catch (Exception e) {
	    	e.printStackTrace();
	    	System.out.println("Can not compile shader!");
		}
	}
}
