package cge.zeppelin;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.vecmath.Vector3f;

import com.bulletphysics.collision.broadphase.AxisSweep3;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.collision.narrowphase.PersistentManifold;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.constraintsolver.ContactSolverInfo;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;

import de.bht.jvr.math.Vector3;

/**
 * Encapsulate the jBullet physics engine.
 */
public class Simulator {

    DefaultCollisionConfiguration collisionConfiguration = new DefaultCollisionConfiguration();
    CollisionDispatcher dispatcher = new CollisionDispatcher(collisionConfiguration);
    Vector3f worldAabbMin = new Vector3f(-1000, -1000, -1000);
    Vector3f worldAabbMax = new Vector3f(1000, 1000, 1000);
    int maxProxies = 4096;

    AxisSweep3 overlappingPairCache = new AxisSweep3(worldAabbMin, worldAabbMax, maxProxies);
    SequentialImpulseConstraintSolver solver = new SequentialImpulseConstraintSolver();
    DiscreteDynamicsWorld dynamicsWorld = new DiscreteDynamicsWorld(dispatcher,
            overlappingPairCache, solver, collisionConfiguration);

    ContactSolverInfo info = dynamicsWorld.getSolverInfo();

    Set<CollisionListener> collisionListeners = new HashSet<CollisionListener>();
    Map<Entity, CollisionListener> collisionEntityListeners = new HashMap<Entity, CollisionListener>();
    Map<CollisionPair, CollisionListener> collisionPairListeners = new HashMap<CollisionPair, CollisionListener>();
    Set<CollisionPair> lastCollisions = new HashSet<CollisionPair>();

    /**
     * Create a new simulator and set the gravity vector for the world.
     */
    Simulator(Vector3 gravity) {
        dynamicsWorld.setGravity(new Vector3f(gravity.x(), gravity.y(), gravity.z()));
    }

    /**
     * Add a rigid body.
     */
    void add(RigidBody... bs) {
        for (RigidBody b : bs)
            dynamicsWorld.addRigidBody(b);
    }

    /**
     * Remove a rigid body.
     */
    void remove(RigidBody... bs) {
        for (RigidBody b : bs)
            dynamicsWorld.removeRigidBody(b);
    }

    /**
     * Add a listener for all entity collisions.
     */
    void addCollisionListener(CollisionListener cl) {
        collisionListeners.add(cl);
    }

    /**
     * Add a listener for collisions in which the specified entity is involved.
     */
    void addCollisionListener(Entity e, CollisionListener cl) {
        collisionEntityListeners.put(e, cl);
    }

    /**
     * Add a listener for collisions in which the specified entities are
     * involved.
     */
    void addCollisionListener(Entity e0, Entity e1, CollisionListener cl) {
        collisionPairListeners.put(new CollisionPair(e0, e1), cl);
    }

    /**
     * Simulate the world for the specified amount of time.
     */
    void simulate(float elapsed) {
        dynamicsWorld.stepSimulation(elapsed, 10);

        Set<CollisionPair> collisions = new HashSet<CollisionPair>();
        int numManifolds = dynamicsWorld.getDispatcher().getNumManifolds();
        for (int i = 0; i < numManifolds; i++) {
            PersistentManifold contactManifold = dynamicsWorld.getDispatcher()
                    .getManifoldByIndexInternal(i);
            RigidBody body0 = (RigidBody) contactManifold.getBody0();
            RigidBody body1 = (RigidBody) contactManifold.getBody1();
            Entity e0 = (Entity) body0.getUserPointer();
            Entity e1 = (Entity) body1.getUserPointer();
            collisions.add(new CollisionPair(e0, e1));
        }

        Set<CollisionPair> firstCollisions = new HashSet<CollisionPair>(collisions);
        firstCollisions.removeAll(lastCollisions);
        lastCollisions = collisions;

        for (CollisionListener cl : collisionListeners)
            for (CollisionPair fc : firstCollisions)
                cl.response(fc.e0, fc.e1);

        for (CollisionPair fc : firstCollisions) {
            CollisionListener cl0 = collisionEntityListeners.get(fc.e0);
            if (cl0 != null)
                cl0.response(fc.e0, fc.e1);

            CollisionListener cl1 = collisionEntityListeners.get(fc.e1);
            if (cl1 != null)
                cl1.response(fc.e1, fc.e0);

            CollisionListener cl = collisionPairListeners.get(fc);
            if (cl != null)
                cl.response(fc.e1, fc.e0);
        }
    }
}
