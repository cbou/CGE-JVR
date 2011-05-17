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

        final Magazin spheres = new Magazin(world, new Magazin.BulletMaker() {

            @Override
            public Entity makeOne() {
                return Entity.makeSphere(0.5f, 0.1f, Matrix4.translate(0, 8f, 0)).tag("sphere");
            }
        }, 200);

        final Magazin cubes = new Magazin(world, new Magazin.BulletMaker() {

            @Override
            public Entity makeOne() {
                return Entity.makeCube(new Vector3(1, 1, 1), 0.1f, Matrix4.translate(0, 8f, 0))
                        .tag("cube");
            }
        }, 200);

        world.input.addKeyListener(KeyEvent.VK_B, new Runnable() {
            @Override
            public void run() {
                cubes.next(new Vector3(0, 8f, 0));
            }
        });

        world.input.addKeyListener(KeyEvent.VK_N, new Runnable() {
            @Override
            public void run() {
                spheres.next(new Vector3(0, 8f, 0));
            }
        });

        world.input.addKeyListener(KeyEvent.VK_SPACE, new Runnable() {
            @Override
            public void run() {
                flyer.fire();
            }
        });

        world.simulator.addCollisionListener(theBigOne, new CollisionListener() {
            @Override
            public void response(Entity e0, Entity e1) {
                if (e1.isTagged("bullet"))
                    cubes.next(new Vector3(0, 8f, 0));
            }
        });

        Printer.print(world.renderer.root);
    }
}
