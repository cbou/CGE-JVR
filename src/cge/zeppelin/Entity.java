package cge.zeppelin;

import javax.vecmath.Matrix4f;

import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.MotionState;

import de.bht.jvr.core.GroupNode;
import de.bht.jvr.core.Transform;
import de.bht.jvr.math.Matrix4;

/**
 * Combines a jVR scene graph node and a jBullet rigid body into a game entity.
 * Contains static factory methods for the creation of simple entities. Entities
 * can be tagged. Tags are supposed to be cheap.
 */
abstract public class Entity extends MotionState {
	
    public Transform translation;
    Transform rotation;
    Transform xform = Transform.identity();
	    
	GroupNode node;
    CollisionShape collisionShape;
    RigidBody body;
    
    float mass;

    /**
     * Override for entities that need to respond to changes in the world.
     */
    public void manipulate(float elapsed) {}

    public void refreshShader() {}

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

}
