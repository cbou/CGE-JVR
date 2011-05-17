package cge.zepelin;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.SphereShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.MotionState;

import de.bht.jvr.collada14.loader.ColladaLoader;
import de.bht.jvr.core.GroupNode;
import de.bht.jvr.core.SceneNode;
import de.bht.jvr.core.Transform;
import de.bht.jvr.math.Matrix4;
import de.bht.jvr.math.Vector3;

/**
 * Combines a jVR scene graph node and a jBullet rigid body into a game entity.
 * Contains static factory methods for the creation of simple entities. Entities
 * can be tagged. Tags are supposed to be cheap.
 */
class Entity extends MotionState {

    final SceneNode node;

    final CollisionShape shape;
    final RigidBody body;
    final float mass;

    /**
     * Create a new entity.
     */
    Entity(SceneNode n, CollisionShape s, float m) {
        shape = s;
        node = n;
        mass = m;

        if (shape != null) {
            Vector3f inertia = new Vector3f(0, 0, 0);
            shape.calculateLocalInertia(mass, inertia);

            RigidBodyConstructionInfo info = new RigidBodyConstructionInfo(mass, this, shape,
                    inertia);
            info.restitution = 0.4f;
            info.linearDamping = 0.2f;
            info.angularDamping = 0.2f;

            body = new RigidBody(info);
            body.setUserPointer(this);

            if (mass == 0.0f)
                body.setActivationState(CollisionObject.DISABLE_DEACTIVATION);
        } else {
            body = null;
        }
    }

    /**
     * Override for entities that need to respond to changes in the world.
     */
    void manipulate(float elapsed, World world) {}

    /*
     * (non-Javadoc)
     * @see
     * com.bulletphysics.linearmath.MotionState#getWorldTransform(com.bulletphysics
     * .linearmath.Transform)
     */
    @Override
    public com.bulletphysics.linearmath.Transform getWorldTransform(
            com.bulletphysics.linearmath.Transform out) {
        if (node != null)
            out.set(new Matrix4f(node.getTransform().getMatrix().getData()));
        return out;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.bulletphysics.linearmath.MotionState#setWorldTransform(com.bulletphysics
     * .linearmath.Transform)
     */
    @Override
    public void setWorldTransform(com.bulletphysics.linearmath.Transform in) {
        if (node != null) {
            Matrix4f m = new Matrix4f();
            in.getMatrix(m);
            float[] newTrans = { m.m00, m.m01, m.m02, m.m03, m.m10, m.m11, m.m12, m.m13, m.m20,
                    m.m21, m.m22, m.m23, m.m30, m.m31, m.m32, m.m33 };
            node.setTransform(new Transform(new Matrix4(newTrans)));
        }
    }

    /**
     * Create a new cube entity with a given size, density and initial
     * transformation.
     */
    static Entity makeCube(Vector3 size, float density, Matrix4 initialXform) {
        loadOnce();

        CollisionShape bs = new BoxShape(new Vector3f(0.5f * size.x(), 0.5f * size.y(),
                0.5f * size.z()));
        float mass = density * size.x() * size.y() * size.z();

        GroupNode xformN = new GroupNode();
        GroupNode sizeN = new GroupNode();
        xformN.addChildNode(sizeN);
        sizeN.addChildNode(box);

        xformN.setTransform(new Transform(initialXform));
        sizeN.setTransform(Transform.scale(size.x(), size.y(), size.z()));

        return new Entity(xformN, bs, mass);
    }

    /**
     * Create a new sphere entity with a given size, density and initial
     * transformation.
     */
    static Entity makeSphere(float radius, float density, Matrix4 initialXform) {
        loadOnce();

        CollisionShape ss = new SphereShape(radius);
        float mass = density * 4.0f / 3.0f * (float) Math.PI * radius * radius * radius;

        GroupNode xformN = new GroupNode();
        GroupNode sizeN = new GroupNode();
        xformN.addChildNode(sizeN);
        sizeN.addChildNode(sphere);

        xformN.setTransform(new Transform(initialXform));
        sizeN.setTransform(Transform.scale(radius * 2, radius * 2, radius * 2));

        return new Entity(xformN, ss, mass);
    }

    /**
     * All the tags associated with this entity.
     */
    public Set<String> tags = null;

    /**
     * Add a tag to this entity.
     */
    public Entity tag(String... ts) {
        if (tags == null)
            tags = new HashSet<String>();
        for (String s : ts) {
            tags.add(s.intern());
        }
        return this;
    }

    /**
     * Returns true if the entity is taged tagged with all of the specified
     * tags.
     */
    public boolean isTagged(String... ts) {
        for (String t : ts)
            if (!tags.contains(t))
                return false;
        return true;
    }

    /**
     * Returns true if the entity is taged tagged with at least one of the
     * specified tags.
     */
    public boolean isTaggedWithSome(String... ts) {
        for (String t : ts)
            if (tags.contains(t))
                return true;
        return false;
    }

    private static SceneNode box = null;
    private static SceneNode sphere = null;

    private static void loadOnce() {
        try {
            if (box == null)
                box = ColladaLoader.load(new File("models/box.dae"));
            if (sphere == null)
                sphere = ColladaLoader.load(new File("models/sphere.dae"));
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

}
