package cge.zeppelin;
import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.media.opengl.GL2GL3;
import javax.media.opengl.GLAutoDrawable;
import javax.vecmath.TexCoord2f;

import cge.zeppelin.util.Helper;

import de.bht.jvr.core.CameraNode;
import de.bht.jvr.core.Context;
import de.bht.jvr.core.Finder;
import de.bht.jvr.core.GroupNode;
import de.bht.jvr.core.SceneNode;
import de.bht.jvr.core.Shader;
import de.bht.jvr.core.ShaderMaterial;
import de.bht.jvr.core.ShaderProgram;
import de.bht.jvr.core.ShapeNode;
import de.bht.jvr.core.SpotLightNode;
import de.bht.jvr.core.Texture2D;
import de.bht.jvr.core.pipeline.Pipeline;
import de.bht.jvr.core.pipeline.PipelineCommandPtr;
import de.bht.jvr.core.uniforms.UniformBool;
import de.bht.jvr.core.uniforms.UniformVector3;
import de.bht.jvr.math.Vector3;

/**
 * Encapsulates the jVR render. Maintains a scene graph, a camera, a light
 * source and a jVR pipeline.
 */
public class Renderer {

	boolean pov = true;
	     
    Context ctx = null;
    GroupNode root = new GroupNode("Root");
    GroupNode zeppelinNode = new GroupNode("Zeppelin");
    GroupNode sceneNode = new GroupNode("Scene");
    
    Pipeline pipeline = new Pipeline(root);
    CameraNode camera = new CameraNode("Camera", 1, 60);
    CameraNode camera2 = new CameraNode("Camera2", 1, 60);
   
    SpotLightNode spot = new SpotLightNode("Spot");
    
	private PipelineCommandPtr switchAmbientCamCmd;
	private PipelineCommandPtr switchLightCamCmd;

	private Texture2D texture;

    /**
     * Create a new renderer.
     */
    Renderer() {
        spot.setCastShadow(true);
        spot.setSpotCutOff(40);
        spot.setShadowBias(0.3f);
        spot.setIntensity(1f);
        spot.setSpecularColor(new Color(0.8f, 0.8f, 0.8f));
        spot.setDiffuseColor(new Color(0.8f, 0.8f, 0.8f));
        
        zeppelinNode.addChildNode(camera);
        
        add(zeppelinNode, sceneNode, camera2, spot);
    }

    /**
     * Initialize the renderer. This is meant to be called from the init()
     * callback on an OpenGL context.
     */
    void init(GLAutoDrawable drawable) {
    	GL2GL3 gl = drawable.getGL().getGL2GL3();
        gl.setSwapInterval(1);
        ctx = new Context(gl);

        
        pipeline.clearBuffers(true, true, new Color(0, 0, 0));
        switchAmbientCamCmd = pipeline.switchCamera(camera);
        pipeline.drawGeometry("AMBIENT", null);
       
        Pipeline ll = pipeline.doLightLoop(false, true);
        ll.switchLightCamera();
        ll.createFrameBufferObject("ShadowMap", true, 0, 2048, 2048, 0);
        ll.switchFrameBufferObject("ShadowMap");
        ll.drawGeometry("SKY2", null); // draw sky box
        ll.clearBuffers(true, false, null);
        ll.drawGeometry("AMBIENT", null);
        ll.switchFrameBufferObject(null);
        switchLightCamCmd = ll.switchCamera(camera);
        ll.bindDepthBuffer("jvr_ShadowMap", "ShadowMap");
        ll.drawGeometry("LIGHTING", null);        
        
    }
    
    
    public void switchCamera(){
    	pov = !pov;
    	switchAmbientCamCmd.switchCamera(pov ? camera:camera2);
    	switchLightCamCmd.switchCamera(pov ? camera:camera2);
    }

    /**
     * Render one frame. This is meant to be called from the display() callback
     * on an OpenGL context.
     */
    void render(GLAutoDrawable drawable) {
        try {
            pipeline.update();
            pipeline.render(ctx);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    /**
     * Call whenever the window size changes.
     */
    void resizeWindowTo(int width, int height) {
        camera.setAspectRatio((float) width / (float) height);
    }

    /**
     * Add nodes to the scene root.
     */
    void add(SceneNode... nodes) {
        for (SceneNode node : nodes)
            root.addChildNode(node);
    }

    /**
     * Remove nodes from the scene root.
     */
    void remove(SceneNode... nodes) {
        for (SceneNode node : nodes)
            root.removeChildNode(node);
    }

	public void zoomIn() {
		camera2.setFieldOfView(camera2.getFieldOfView()+1);
	}

	public void zoomOut() {
		camera2.setFieldOfView(camera2.getFieldOfView()-1);
	}
}
