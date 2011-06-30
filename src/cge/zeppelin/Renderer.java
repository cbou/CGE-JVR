package cge.zeppelin;

import java.io.File;
import java.io.IOException;

import javax.media.opengl.GL2GL3;
import javax.media.opengl.GLAutoDrawable;

import de.bht.jvr.core.CameraNode;
import de.bht.jvr.core.Context;
import de.bht.jvr.core.GroupNode;
import de.bht.jvr.core.SceneNode;
import de.bht.jvr.core.ShaderMaterial;
import de.bht.jvr.core.ShaderProgram;
import de.bht.jvr.core.SpotLightNode;
import de.bht.jvr.core.pipeline.Pipeline;
import de.bht.jvr.core.pipeline.PipelineCommandPtr;
import de.bht.jvr.core.uniforms.UniformFloat;
import de.bht.jvr.util.Color;

/**
 * Encapsulates the jVR render. Maintains a scene graph, a camera, a light
 * source and a jVR pipeline.
 */
public class Renderer {

	boolean pov = true;
	     
    Context ctx = null;
    GroupNode root = new GroupNode("Root");
    GroupNode zeppelinNode = new GroupNode("Zeppelin");
    GroupNode skyboxNode = new GroupNode("Skybox");
    GroupNode sceneNode = new GroupNode("Scene");
    
    Pipeline pipeline = new Pipeline(root);
    CameraNode camera = new CameraNode("Camera", 1, 60);
    CameraNode camera2 = new CameraNode("Camera2", 1, 60);
   
    SpotLightNode spot = new SpotLightNode("Spot");
    
	private PipelineCommandPtr switchAmbientCamCmd;
	private PipelineCommandPtr switchLightCamCmd;

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
        
        add(zeppelinNode, sceneNode, camera2, spot, skyboxNode);
    }

    /**
     * Initialize the renderer. This is meant to be called from the init()
     * callback on an OpenGL context.
     */
    void init(GLAutoDrawable drawable) {
    	GL2GL3 gl = drawable.getGL().getGL2GL3();
    	gl.setSwapInterval(1);
    	ctx = new Context(gl);

    	ShaderProgram sp;
    	try {
    		sp = new ShaderProgram(new File("./resources/prototype/blur/ambient.vs"), new File("./resources/prototype/blur/blur.fs"));
    		ShaderMaterial sm = new ShaderMaterial("DOFPass", sp);

    		/* Alles in SceneMap rendern*/
    		pipeline.createFrameBufferObject("SceneMap", true, 1, 1.0f, 0);
    		pipeline.switchFrameBufferObject("SceneMap");

    		pipeline.setUniform("intensity", new UniformFloat(4)); // set the blur intensity

    		pipeline.clearBuffers(true, true, new Color(0, 0, 0));
    		switchAmbientCamCmd = pipeline.switchCamera(camera);
    		pipeline.drawGeometry("AMBIENT", null);

    		Pipeline ll = pipeline.doLightLoop(false, true);
    		ll.switchLightCamera();
    		ll.createFrameBufferObject("ShadowMap", true, 0, 2048, 2048, 0);
    		ll.switchFrameBufferObject("ShadowMap");
    		ll.clearBuffers(true, false, null);
    		ll.drawGeometry("AMBIENT", null);
    		ll.switchFrameBufferObject(null);
    		switchLightCamCmd = ll.switchCamera(camera);
    		ll.bindDepthBuffer("jvr_ShadowMap", "ShadowMap");

    		ll.drawGeometry("LIGHTING", null);  

    		/* Auf Screen zeichnen */   
    		pipeline.switchFrameBufferObject(null);
    		pipeline.clearBuffers(true, true, new Color(0, 0, 0));

    		pipeline.bindColorBuffer("jvr_Texture1", "SceneMap", 0); // bind color buffer from fbo to uniform
    		pipeline.bindDepthBuffer("jvr_Texture0", "SceneMap"); // bind depth buffer from fbo to uniform
    		// render quad with dof shader
    		pipeline.drawQuad(sm, "DOFPass");
    	} catch (IOException e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	}


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
