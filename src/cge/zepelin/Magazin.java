package cge.zepelin;
import java.util.LinkedList;
import java.util.Queue;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import com.bulletphysics.collision.dispatch.CollisionObject;

import de.bht.jvr.core.Transform;
import de.bht.jvr.math.Matrix4;
import de.bht.jvr.math.Vector3;

/**
 * A bullet clip. Manages a finite number of entities that can be recycled.
 */
public class Magazin {

    private Queue<Entity> bullets = new LinkedList<Entity>();
    final int count;
    private final BulletMaker maker;
    final World world;

    /**
     * Funktion object that creates a new Entity for the bullet clip.
     */
    interface BulletMaker {
        /**
         * Override to create one new entity.
         */
        Entity makeOne();
    }

    /**
     * Create a new bullet clip and .
     */
    Magazin(World w, BulletMaker m, int c) {
        world = w;
        maker = m;
        count = c;
    }

    /**
     * Reposition the next bullet. Bullets are sorted LRU.
     */
    void next(Vector3 position) {
        next(position, new Vector3(0, 0, 0));
    }

    /**
     * Reposition the next bullet and fire with an initial velocity. Bullets are
     * sorted LRU.
     */
    void next(Vector3 position, Vector3 velocity) {
        Entity bullet = null;
        if (bullets.size() < count) {
            bullet = maker.makeOne();
        } else {
            bullet = bullets.remove();
            world.remove(bullet);
        }

        bullet.node.setTransform(Transform.translate(position));
        bullet.body.proceedToTransform(new com.bulletphysics.linearmath.Transform(new Matrix4f(
                Matrix4.translate(position).getData())));
        bullet.body.setLinearVelocity(new Vector3f(velocity.x(), velocity.y(), velocity.z()));
        bullet.body.setActivationState(CollisionObject.DISABLE_DEACTIVATION);

        world.add(bullet);
        bullets.add(bullet);
    }
}
