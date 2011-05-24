package cge.zeppelin;

import java.awt.event.KeyEvent;

import de.bht.jvr.core.GroupNode;
import de.bht.jvr.core.Printer;
import de.bht.jvr.core.Transform;
import de.bht.jvr.math.Matrix4;
import de.bht.jvr.math.Vector3;

/**
 * Setup the shooter application. Populates the world with the neccessary
 * entities.
 */
public class Shooter {
	static Entity zeppelin;

    /**
     * Populate the world with the neccessary entities.
     */
    static void populateWorld( World world, int bullets, int boxes) {
    	
        world.renderer.spot.setTransform(Transform.translate(20, 20, 20)
                .mul(Transform.rotateY(0.8f)).mul(Transform.rotateX(-0.8f)));

        world.add(Entity.makeCube(new Vector3(100, 1, 100), 0, Matrix4.translate(0, -0.5f, 0)));

        final Entity theBigOne = Entity.makeCube(new Vector3(2, 2, 2), 0,
                Matrix4.translate(0, 10f, 0));

        zeppelin = Entity.makeCube(new Vector3(1, 1, 3), 0, Matrix4.translate(0, 0, 0));
        Entity zeppelin2 = Entity.makeCube(new Vector3(1, 1, 1), 0, Matrix4.translate(0, -1, 0));
        
        world.add(theBigOne);
        world.add(Entity.makeCube(new Vector3(1, 1, 1), 0.1f, Matrix4.translate(0, 7f, 0)));

        world.renderer.zeppelin.addChildNode(zeppelin.node);
        world.renderer.zeppelin.addChildNode(zeppelin2.node);
     	world.renderer.camera.setTransform(Transform.translate(new Vector3(0,2,5)));
     	world.renderer.camera2.setTransform(Transform.translate(new Vector3(0,2,25)));
                
        final Flyer flyer = new Flyer(world, world.renderer.zeppelin, new Vector3(0, 1.9f, 10)); 
        
        world.add(flyer);
        
        world.input.addKeyListener(KeyEvent.VK_Q, new Runnable() {

            @Override
            public void run() {
       
            }
        });
        
        Printer.print(world.renderer.root);
    }
}
