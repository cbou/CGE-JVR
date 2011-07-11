package cge.zeppelin.prototype.blur;

import java.io.File;
import java.util.Map;

import javax.media.opengl.GL2GL3;

import cge.zeppelin.util.Helper;
import de.bht.jvr.collada14.loader.ColladaLoader;
import de.bht.jvr.core.CameraNode;
import de.bht.jvr.core.Finder;
import de.bht.jvr.core.GroupNode;
import de.bht.jvr.core.PointLightNode;
import de.bht.jvr.core.Printer;
import de.bht.jvr.core.SceneNode;
import de.bht.jvr.core.Shader;
import de.bht.jvr.core.ShaderMaterial;
import de.bht.jvr.core.ShaderProgram;
import de.bht.jvr.core.ShapeNode;
import de.bht.jvr.core.Transform;
import de.bht.jvr.core.pipeline.Pipeline;
import de.bht.jvr.core.pipeline.PipelineCommandPtr;
import de.bht.jvr.core.uniforms.UniformFloat;
import de.bht.jvr.core.uniforms.UniformVector3;
import de.bht.jvr.math.Vector3;
import de.bht.jvr.renderer.AwtRenderWindow;
import de.bht.jvr.renderer.RenderWindow;
import de.bht.jvr.renderer.Viewer;
import de.bht.jvr.util.Color;
import de.bht.jvr.util.InputState;
import de.bht.jvr.util.StopWatch;

/**
 * @author Andreas Rettig
 * @author Charles Bourasseau
 */

public class BlurExample {


    public static void main(String[] args) throws Exception {
        GroupNode root = new GroupNode("scene root");

        SceneNode teapot = ColladaLoader.load(new File("./resources/models/teapot.dae"));
        teapot.setTransform(Transform.translate(0f, -0.6f, 0f));
        GroupNode teapotRotor = new GroupNode();
        teapotRotor.addChildNode(teapot);

        ShaderProgram sp = new ShaderProgram(new File("./resources/prototype/blur/ambient.vs"), new File("./resources/prototype/blur/blur.fs"));
        ShaderMaterial sm = new ShaderMaterial("DOFPass", sp);
        
        Shader ambientVs = new Shader(Helper.getInputStreamResource("/prototype/blur/ambient.vs"), GL2GL3.GL_VERTEX_SHADER);
        Shader ambientFs = new Shader(Helper.getInputStreamResource("/prototype/blur/ambient.fs"), GL2GL3.GL_FRAGMENT_SHADER);
        Shader lightingVs = new Shader(Helper.getInputStreamResource("/prototype/blur/lighting.vs"), GL2GL3.GL_VERTEX_SHADER);
        Shader lightingFs = new Shader(Helper.getInputStreamResource("/prototype/blur/lighting.fs"), GL2GL3.GL_FRAGMENT_SHADER);
        ShaderProgram ambientProgram = new ShaderProgram(ambientVs, ambientFs);
        ShaderProgram lightingProgram = new ShaderProgram(lightingVs, lightingFs);

        ShapeNode shape = Finder.find(teapot, ShapeNode.class, "Teapot01_Shape");

        Map<String, ShaderProgram> phong = shape.getMaterial().getShaderPrograms();

        ShaderMaterial teapotMat = new ShaderMaterial();
        teapotMat.setUniform("AMBIENT", "toonColor", new UniformVector3(new Vector3(1, 1, 1)));
        teapotMat.setUniform("LIGHTING", "toonColor", new UniformVector3(new Vector3(1, 1, 1)));
        teapotMat.setShaderProgram("AMBIENT", ambientProgram);
        teapotMat.setShaderProgram("LIGHTING", lightingProgram);

        shape.setMaterial(teapotMat);

        PointLightNode light0 = new PointLightNode("sun0");
        light0.setTransform(Transform.translate(3, 0, 3));
        light0.setAmbientColor(new Color(0.0f, 0.0f, 1.0f));
        light0.setDiffuseColor(new Color(1.0f, 0.0f, 0.0f));
        light0.setSpecularColor(new Color(1.0f, 1.0f, 1.0f));

        PointLightNode light1 = new PointLightNode("sun1");
        light1.setTransform(Transform.translate(-3, 0, 3));
        light1.setAmbientColor(new Color(0.0f, 0.0f, 1.0f));
        light1.setDiffuseColor(new Color(0.0f, 1.0f, 0.0f));
        light1.setSpecularColor(new Color(1.0f, 1.0f, 1.0f));

        CameraNode camera = new CameraNode("camera", 4f / 3f, 60);
        camera.setTransform(Transform.translate(0, 0, 3));

        root.addChildNodes(teapotRotor, light0, light1, camera);
        Printer.print(root);

        Pipeline pipeline = new Pipeline(root);
        
        /* Alles in SceneMap rendern*/
        pipeline.createFrameBufferObject("SceneMap", true, 1, 1.0f, 0);
		pipeline.switchFrameBufferObject("SceneMap");
				
        pipeline.clearBuffers(true, true, new Color(0, 0, 0));
        pipeline.switchCamera(camera);
        pipeline.drawGeometry("AMBIENT", null);
        pipeline.doLightLoop(true, true).drawGeometry("LIGHTING", null);

        pipeline.switchFrameBufferObject(null);
        pipeline.clearBuffers(true, true, new Color(0, 0, 0));
     
        /* Alles auf Screen*/
        float intensity = 4;
        PipelineCommandPtr ptr = pipeline.setUniform("intensity", new UniformFloat(intensity)); // set the blur intensity
        pipeline.bindColorBuffer("jvr_Texture1", "SceneMap", 0); // bind color buffer from fbo to uniform
        pipeline.bindDepthBuffer("jvr_Texture0", "SceneMap"); // bind depth buffer from fbo to uniform
        // render quad with dof shader
        pipeline.drawQuad(sm, "DOFPass");
         
        InputState input = new InputState();
        RenderWindow win = new AwtRenderWindow(pipeline, 800, 600);
        win.addKeyListener(input);

        StopWatch time = new StopWatch();
        Viewer v = new Viewer(win);

        float angleY = 0;
        float angleX = 0;
        float speed = 90;

        while (v.isRunning()) {
            float elapsed = time.elapsed();

            if (input.isOneDown('W', java.awt.event.KeyEvent.VK_UP))
                angleX += elapsed * speed;
            if (input.isOneDown('S', java.awt.event.KeyEvent.VK_DOWN))
                angleX -= elapsed * speed;
            if (input.isOneDown('D', java.awt.event.KeyEvent.VK_RIGHT))
                angleY += elapsed * speed;
            if (input.isOneDown('A', java.awt.event.KeyEvent.VK_LEFT))
                angleY -= elapsed * speed;

            if (input.isOneDown('N'))
                ptr.setUniform("intensity",new UniformFloat(intensity--));

            if (input.isOneDown('M'))
                ptr.setUniform("intensity",new UniformFloat(intensity++));

            
            teapotRotor.setTransform(Transform.rotateYDeg(angleY).mul(Transform.rotateXDeg(angleX)));

            if (input.isDown('Q'))
                System.exit(0);

            try {
                if (input.isDown('R')) {
                    ambientVs = new Shader(Helper.getInputStreamResource("/prototype/blur/ambient.vs"), GL2GL3.GL_VERTEX_SHADER);
                    ambientFs = new Shader(Helper.getInputStreamResource("/prototype/blur/ambient.fs"), GL2GL3.GL_FRAGMENT_SHADER);
                    lightingVs = new Shader(Helper.getInputStreamResource("/prototype/blur/lighting.vs"), GL2GL3.GL_VERTEX_SHADER);
                    lightingFs = new Shader(Helper.getInputStreamResource("/prototype/blur/lighting.fs"), GL2GL3.GL_FRAGMENT_SHADER);
                    ambientProgram = new ShaderProgram(ambientVs, ambientFs);
                    lightingProgram = new ShaderProgram(lightingVs, lightingFs);

                    teapotMat.setShaderProgram("AMBIENT", ambientProgram);
                    teapotMat.setShaderProgram("LIGHTING", lightingProgram);
                }
                v.display();
            } catch (Exception e) {
                e.printStackTrace();
                for (String ctx : phong.keySet()) {
                    teapotMat.setShaderProgram(ctx, phong.get(ctx));
                }
            }
        }

    }
}
