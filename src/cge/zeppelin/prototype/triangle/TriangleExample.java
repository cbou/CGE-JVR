package cge.zeppelin.prototype.triangle;

import javax.media.opengl.GL2GL3;

import cge.zeppelin.util.Helper;
import de.bht.jvr.collada14.loader.ColladaLoader;
import de.bht.jvr.core.CameraNode;
import de.bht.jvr.core.Finder;
import de.bht.jvr.core.GroupNode;
import de.bht.jvr.core.PointLightNode;
import de.bht.jvr.core.Printer;
import de.bht.jvr.core.Shader;
import de.bht.jvr.core.ShaderMaterial;
import de.bht.jvr.core.ShaderProgram;
import de.bht.jvr.core.ShapeNode;
import de.bht.jvr.core.Texture2D;
import de.bht.jvr.core.Transform;
import de.bht.jvr.core.pipeline.Pipeline;
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

public class TriangleExample {

    public static void main(String[] args) throws Exception {
        GroupNode root = new GroupNode("scene root");
        GroupNode t = new GroupNode("t");
        t.addChildNode(ColladaLoader.load(Helper.getFileResource("models/plane.dae")));
        
        PointLightNode light = new PointLightNode("sun");
        light.setEnabled(true);
        light.setAmbientColor(new Color(1,0,1));
        light.setColor(new Color(1,1,1));
        light.setDiffuseColor(new Color(1,1,1));
        light.setIntensity(1);
        light.setTransform(Transform.translate(3, 0, 3));

        CameraNode camera = new CameraNode("camera", 4f / 3f, 60);
        camera.setTransform(Transform.translate(0, 0, 3));
        root.addChildNodes(light, camera,t);
        Printer.print(root);

        Texture2D bk = new Texture2D(Helper.getInputStreamResource("textures/checkered.jpg"));
		
    	Shader ambientVs = new Shader(Helper.getInputStreamResource("shaders/ambient.vs"), GL2GL3.GL_VERTEX_SHADER);
        Shader ambientFs = new Shader(Helper.getInputStreamResource("shaders/ambient.fs"), GL2GL3.GL_FRAGMENT_SHADER);
    
        ShaderProgram ambientProgram = new ShaderProgram(ambientVs, ambientFs);
        
        Shader lightingVs = new Shader(Helper.getInputStreamResource("shaders/zeppelinLighting.vs"), GL2GL3.GL_VERTEX_SHADER);
        Shader lightingFs = new Shader(Helper.getInputStreamResource("shaders/zeppelinLighting.fs"), GL2GL3.GL_FRAGMENT_SHADER);
      
        ShaderProgram lightingProgram = new ShaderProgram(lightingVs, lightingFs);
        
        ShaderMaterial boardMat = new ShaderMaterial();
        boardMat.setTexture("AMBIENT", "jvr_Texture0", bk);    
        boardMat.setShaderProgram("AMBIENT", ambientProgram);	        
        boardMat.setTexture("LIGHTING", "jvr_Texture0", bk);    
        boardMat.setShaderProgram("LIGHTING", lightingProgram);
        
       
        t.setTransform(Transform.translate(0, 0, 10));
        ShapeNode s = Finder.find(t, ShapeNode.class, "Plane01_Shape");
        s.setMaterial(boardMat);
        //t.setTransform(Transform.scale(0.5f));
        
        Pipeline pipeline = new Pipeline(root);
        pipeline.clearBuffers(true, true, new Color(0, 0, 0));
        pipeline.switchCamera(camera);
        pipeline.drawGeometry("AMBIENT", null);
        pipeline.doLightLoop(true, true).drawGeometry("LIGHTING", null);

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

            t.setTransform(Transform.rotateYDeg(angleY).mul(Transform.rotateXDeg(angleX)));

            if (input.isDown('Q'))
                System.exit(0);

            v.display();
        }
    }
}
