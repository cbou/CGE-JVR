package cge.zeppelin;

import java.io.File;
import java.io.FileNotFoundException;

import cge.zeppelin.util.Helper;

import de.bht.jvr.collada14.loader.ColladaLoader;
import de.bht.jvr.core.Finder;
import de.bht.jvr.core.Geometry;
import de.bht.jvr.core.GroupNode;
import de.bht.jvr.core.SceneNode;
import de.bht.jvr.core.ShaderMaterial;
import de.bht.jvr.core.ShaderProgram;
import de.bht.jvr.core.ShapeNode;
import de.bht.jvr.core.Texture2D;
import de.bht.jvr.core.Transform;

public class Skybox extends Entity {
    private Texture2D bk, dn, ft, lf, rt, up;
    private Geometry planeGeo;
    private ShaderProgram textureProg;
    SceneNode plane;
   
    public Skybox(GroupNode n) {
    	node = n;

    	try {
			loadFiles();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		plane.setTransform(Transform.scale(1, 1, 6).mul(Transform.translate(0, 0, -1)));

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
}
