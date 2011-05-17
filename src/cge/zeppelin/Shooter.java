package cge.zeppelin;

import java.awt.event.KeyEvent;

import de.bht.jvr.core.Printer;
import de.bht.jvr.core.Transform;
import de.bht.jvr.math.Matrix4;
import de.bht.jvr.math.Vector3;

/**
 * Setup the shooter application. Populates the world with the neccessary
 * entities.
 */
public class Shooter {

    /**
     * Populate the world with the neccessary entities.
     */
    static void populateWorld(World world, int bullets, int boxes) {
        world.renderer.spot.setTransform(Transform.translate(20, 20, 20)
                .mul(Transform.rotateY(0.8f)).mul(Transform.rotateX(-0.8f)));

        world.add(Entity.makeCube(new Vector3(100, 1, 100), 0, Matrix4.translate(0, -0.5f, 0)));

        final Entity theBigOne = Entity.makeCube(new Vector3(2, 2, 2), 0,
                Matrix4.translate(0, 10f, 0));
        world.add(theBigOne);
        world.add(Entity.makeCube(new Vector3(1, 1, 1), 0.1f, Matrix4.translate(0, 7f, 0)));

        final Flyer flyer = new Flyer(world, world.renderer.camera, new Vector3(0, 1.9f, 10));
        world.add(flyer);

        world.input.addKeyListener(KeyEvent.VK_Q, new Runnable() {

            @Override
            public void run() {
                System.exit(0);
            }
        });
        
        Printer.print(world.renderer.root);
    }
}
