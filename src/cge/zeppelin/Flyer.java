package cge.zeppelin;
import java.awt.event.KeyEvent;

import de.bht.jvr.core.SceneNode;
import de.bht.jvr.core.Transform;
import de.bht.jvr.math.Matrix4;
import de.bht.jvr.math.Vector3;
import de.bht.jvr.util.awt.InputState;

/**
 * An entity that responds to keyboard input and implements a very simple
 * interactive model. The flyer can shoot bullets into the viewing direction.
 * The total number of bullets is fixed. Bullet entities are reused.
 */
class Flyer extends Entity {

    Transform translation;
    Transform rotation;
    Transform xform;

    float speed = 5; // m/s
    float speedA = 1; // rad/s

    /**
     * Create a new flyer and attach it to an existing scene node. Needs a
     * reference to the world for access to input and scene state.
     */
    Flyer(World w, SceneNode n, Vector3 start) {
        super(n, null, 0);

        translation = Transform.translate(start);
        rotation = Transform.rotate(new Vector3(0, 1, 0), 0);

        update();
    }

    /*
     * (non-Javadoc)
     * @see Entity#manipulate(float, World)
     */
    @Override
    void manipulate(float dt, World world) {
        InputState input = world.input;

        if (input.isDown('W'))
            translation = translation.mul(Transform.translate(rotation.getMatrix().mulDir(
                    new Vector3(0, 0, -speed * dt))));
        else if (input.isDown('S'))
            translation = translation.mul(Transform.translate(rotation.getMatrix().mulDir(
                    new Vector3(0, 0, speed * dt))));

        if (input.isDown('A'))
            translation = translation.mul(Transform.translate(rotation.getMatrix().mulDir(
                    new Vector3(-speed * dt, 0, 0))));
        else if (input.isDown('D'))
            translation = translation.mul(Transform.translate(rotation.getMatrix().mulDir(
                    new Vector3(speed * dt, 0, 0))));

        if (input.isOneDown('J', KeyEvent.VK_LEFT))
            rotation = Transform.rotate(new Vector3(0, 1, 0), speedA * dt).mul(rotation);
        else if (input.isOneDown('L', KeyEvent.VK_RIGHT))
            rotation = Transform.rotate(new Vector3(0, 1, 0), -speedA * dt).mul(rotation);

        if (input.isOneDown('I', KeyEvent.VK_UP))
            rotation = rotation.mul(Transform.rotate(new Vector3(1, 0, 0), speedA * dt));
        else if (input.isOneDown('K', KeyEvent.VK_DOWN))
            rotation = rotation.mul(Transform.rotate(new Vector3(1, 0, 0), -speedA * dt));

        update();
    }

    private void update() {
        xform = translation.mul(rotation);
        node.setTransform(xform);
    }
}
