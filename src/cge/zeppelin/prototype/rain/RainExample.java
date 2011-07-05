package cge.zeppelin.prototype.rain;

import cge.zeppelin.util.Helper;
import de.bht.jvr.collada14.loader.ColladaLoader;
import de.bht.jvr.core.CameraNode;
import de.bht.jvr.core.GroupNode;
import de.bht.jvr.core.PointLightNode;
import de.bht.jvr.core.Printer;
import de.bht.jvr.core.SceneNode;
import de.bht.jvr.core.Transform;
import de.bht.jvr.core.pipeline.Pipeline;
import de.bht.jvr.math.Vector3;
import de.bht.jvr.renderer.AwtRenderWindow;
import de.bht.jvr.renderer.RenderWindow;
import de.bht.jvr.renderer.Viewer;
import de.bht.jvr.util.Color;
import de.bht.jvr.util.InputState;
import de.bht.jvr.util.StopWatch;

/**
 * This basic sample demonstrates how to setup a very simple jVR application
 * that uses a geometry shader.
 * 
 * @author Marc Roßbach
 * @author Henrik Tramberend
 */

public class RainExample {

    public static void main(String[] args) throws Exception {
        GroupNode root = new GroupNode("scene root");

        SceneNode ground = ColladaLoader.load(Helper.getFileResource("/prototype/rain/box.dae"));
        ground.setTransform(Transform.translate(0f, -1f, 0f).mul(Transform.scale(10f, 0.1f, 10f)));
        
        GroupNode emitterPos = new GroupNode();
        emitterPos.setTransform(Transform.translate(0, -0.5f, 0));
        GroupNode emitterDir = new GroupNode();
        emitterPos.addChildNode(emitterDir);
        
        Emitter emitter = new Emitter(emitterDir, 1000);
        
        PointLightNode light = new PointLightNode("sun");
        light.setTransform(Transform.translate(3, 3, 3));

        CameraNode camera = new CameraNode("camera", 4f / 3f, 60);
        camera.setTransform(Transform.translate(0, 0, 5));

        root.addChildNodes(ground, light, camera, emitterPos);
        Printer.print(root);

        Pipeline pipeline = new Pipeline(root);
        pipeline.clearBuffers(true, true, new Color(0, 0, 0));
        pipeline.switchCamera(camera);
        pipeline.drawGeometry("AMBIENT", null);
        pipeline.doLightLoop(true, true).drawGeometry("LIGHTING", null);
        // Render the particles unlit and last, because of transparency.
        pipeline.drawGeometry("AMBIENT", "PARTICLE");

        InputState input = new InputState();
        RenderWindow win = new AwtRenderWindow(pipeline, 800, 600);
        win.addKeyListener(input);

        StopWatch time = new StopWatch();
        Viewer v = new Viewer(win);

        float angleY = 0;
        float angleX = 0;
        float speed = 2;

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
            if (input.isOneDown('R'))
            	emitter.refreshShader();
            if (input.isDown('Q'))
                System.exit(0);

            emitterDir.setTransform(Transform.rotate(Vector3.X, angleX).mul(Transform.rotate(Vector3.Y, angleY)));
            emitter.simulate(elapsed);
            
            v.display();
        }
    }
}
