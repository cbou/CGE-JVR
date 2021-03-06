package cge.zeppelin;

import java.io.File;
import java.io.IOException;

import javax.media.opengl.GL;
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

	int pov = 0;
	     
    Context ctx = null;
    GroupNode root = new GroupNode("Root");
    GroupNode zeppelinNode = new GroupNode("Zeppelin");
    GroupNode skyboxNode = new GroupNode("Skybox");
    GroupNode sceneNode = new GroupNode("Scene");
    
    Pipeline pipeline = new Pipeline(root);
    CameraNode cameraIntern = new CameraNode("Camera", 1, 60);
    CameraNode cameraExtern = new CameraNode("Camera", 1, 60);
    CameraNode cameraFixed = new CameraNode("Camera2", 1, 60);
   
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
        spot.setEnabled(true);
        zeppelinNode.addChildNodes(cameraIntern, cameraExtern);
        
        add(zeppelinNode, sceneNode, cameraFixed, spot, skyboxNode);
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
    		sp = new ShaderProgram(new File("./resources/shaders/ambient.vs"), new File("./resources/shaders/blur.fs"));
    		ShaderMaterial sm = new ShaderMaterial("DOFPass", sp);

    		/* Alles in SceneMap rendern*/
    		pipeline.createFrameBufferObject("SceneMap", true, 1, 1.0f, 0);
    		pipeline.switchFrameBufferObject("SceneMap");

    		pipeline.clearBuffers(true, true, new Color(0, 0, 0));
    		switchAmbientCamCmd = pipeline.switchCamera(cameraIntern);
    		pipeline.drawGeometry("AMBIENT", null);
    		
    		Pipeline ll = pipeline.doLightLoop(false, true);
    		ll.switchLightCamera();
    		ll.createFrameBufferObject("ShadowMap", true, 0, 2048, 2048, 0);
    		ll.switchFrameBufferObject("ShadowMap");
    		ll.clearBuffers(true, false, null);
    		ll.drawGeometry("AMBIENT", null);
    		ll.switchFrameBufferObject(null);
    		
    		switchLightCamCmd = ll.switchCamera(cameraIntern);
    		ll.bindDepthBuffer("jvr_ShadowMap", "ShadowMap");
    		ll.drawGeometry("LIGHTING", null);  

    		/* Partikel in Framebuffer*/
    		pipeline.createFrameBufferObject("Particles", true, 1, 1.0f, 0);
    		pipeline.switchFrameBufferObject("Particles");
    		pipeline.clearBuffers(true, true, new Color(0, 0, 0));
    		pipeline.drawGeometry("AMBIENT", "PARTICLE");

    		/* Auf Screen zeichnen */   
    		pipeline.switchFrameBufferObject(null);
    		pipeline.clearBuffers(true, true, new Color(0, 0, 0));
    		pipeline.setUniform("intensity", new UniformFloat(15	));      // set the particle blur intensity
    		pipeline.setUniform("dofIntensity", new UniformFloat(1f)); // set the DOFblur intensity
    		
    		pipeline.bindColorBuffer("jvr_Texture1", "SceneMap", 0); // bind color buffer from fbo to uniform
    		pipeline.bindDepthBuffer("jvr_SzeneZ", "SceneMap"); 	 // bind depth buffer from fbo to uniform
    		pipeline.bindDepthBuffer("jvr_ParticleZ", "Particles");  // bind depth buffer from fbo to uniform
//    	     
    		// render quad with dof shader
    		pipeline.drawQuad(sm, "DOFPass");
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    }
    
    /**
     * Change the position camera
     */
    public void changeCamera(){
    	pov++;
    	switch (pov) {
		case 0:
			System.out.println("change to camera intern");
	    	switchAmbientCamCmd.switchCamera(cameraIntern);
	    	switchLightCamCmd.switchCamera(cameraIntern);
			break;
		case 1:
			System.out.println("change to camera extern");
	    	switchAmbientCamCmd.switchCamera(cameraExtern);
	    	switchLightCamCmd.switchCamera(cameraExtern);
			break;
		case 2:
			System.out.println("change to camera fixed");
	    	switchAmbientCamCmd.switchCamera(cameraFixed);
	    	switchLightCamCmd.switchCamera(cameraFixed);
			pov = -1;
			break;

		default:
			pov = 0;
			break;
		}
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
        cameraIntern.setAspectRatio((float) width / (float) height);
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
		cameraFixed.setFieldOfView(cameraFixed.getFieldOfView()+1);
	}

	public void zoomOut() {
		cameraFixed.setFieldOfView(cameraFixed.getFieldOfView()-1);
	}
}
