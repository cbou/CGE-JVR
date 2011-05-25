package cge.zeppelin;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;

import de.bht.jvr.core.GroupNode;
import de.bht.jvr.core.Transform;
import de.bht.jvr.math.Vector3;

/**
 * An entity that responds to keyboard input and implements a very simple
 * interactive model. The flyer can shoot bullets into the viewing direction.
 * The total number of bullets is fixed. Bullet entities are reused.
 */
class Flyer extends Entity {

    float acceleration 		= 0.5f; 	// m/s
    float rotAcceleration	= 0.05f; 	// rad/s
    
    float yRotVelocity 		= 0;
    float xRotVelocity 		= 0;
    float velocity 			= 0; 		// m/s
    
    float gravity 			= -10;
    float gas				=  25;
    float load				=  15;
    
	float friction = 0.01f;
	private float lastRot = 0;
	private Zeppelin zeppelin;
    
    /**
     * Create a new flyer and attach it to an existing scene node. Needs a
     * reference to the world for access to input and scene state.
     */
    Flyer(GroupNode n, Vector3 start) {
        node = n;

		translation = Transform.translate(start);
		rotation 	= Transform.rotate(new Vector3(0, 1, 0), 0);
		zeppelin = new Zeppelin(node); 
		     

        mass = 0.1f;
        Vector3 size = new Vector3(1,1,1);
        shape = new BoxShape(new Vector3f(0.5f * size.x(), 0.5f * size.y(),
                0.5f * size.z()));

        Vector3f inertia = new Vector3f(0, 0, 0);
        //shape.calculateLocalInertia(0, inertia);

        RigidBodyConstructionInfo info = new RigidBodyConstructionInfo(0, this, shape, inertia);
        
        body = new RigidBody(info);
        body.setUserPointer(this); 
		
        manipulate(0.01f);
	}

	/*
	 * (non-Javadoc)
	 * @see Entity#manipulate(float, World)
	 */
	@Override
	void manipulate(float dt) {              
		rotation = Transform.rotate(new Vector3(0, 1, 0), yRotVelocity*dt).mul(rotation);
		rotation = rotation.mul(Transform.rotate(new Vector3(1, 0, 0), xRotVelocity * dt));
		rotation = rotation.mul(Transform.rotate(new Vector3(0, 0, 1), -lastRot ));
		float newRot = lastRot  = -yRotVelocity*velocity/80f;
		rotation = rotation.mul(Transform.rotate(new Vector3(0, 0, 1), newRot));

		translation = 
			translation.mul(
					Transform.translate(rotation.getMatrix().mulDir(new Vector3(0, 0, velocity * dt))));

		//Gravity, Gas and Balance
		float overAllGravity = gravity+gas-load;
		translation = translation.mul(Transform.translate(0,overAllGravity,0));

		//TODO Alle Velocities in einen Vektor
		// Friction
		velocity *= 1-(friction*Math.abs(velocity));
		velocity = Math.abs(velocity) < 0.01 ? 0 : velocity;

		yRotVelocity *= 1-friction;
		yRotVelocity = Math.abs(yRotVelocity) < 0.01 ? 0 : yRotVelocity;

		xRotVelocity *= 1-friction;
		xRotVelocity = Math.abs(xRotVelocity) < 0.01 ? 0 : xRotVelocity;

        update();
        
       // body.getWorldTransform(xform2)
       // System.out.println(dt);
        //System.out.println(body.getMotionState() != null);
        body.saveKinematicState(dt);
       
        //body.
    }

	
	public void update() {
		xform = translation.mul(rotation);
		node.setTransform(xform);
		
		/*com.bulletphysics.linearmath.Transform xform2 = new com.bulletphysics.linearmath.Transform();
		xform2.set(new Matrix4f(node.getTransform().getMatrix().getData()));*/
		//this.setWorldTransform(xform2);
		//body.getWorldTransform(xform2);
		//body.getWorldTransform(xform2).transform(new Vector3f(xform.getMatrix().translation().x(), xform.getMatrix().translation().y(), xform.getMatrix().translation().z()));
		//body.setWorldTransform(xform2);
		//this.getWorldTransform(n);
		//System.out.println(body.hasContactResponse());
		//body.translate(new Vector3f(0,0,0));
		//com.bulletphysics.linearmath.Transform j = new com.bulletphysics.linearmath.Transform(xform.getMatrix());
        //body.getWorldTransform(j);
		//body.translate(new Vector3f(xform.getMatrix().translation().x(), xform.getMatrix().translation().y(), xform.getMatrix().translation().z()));
		
		printState(); 
	}

	public void accelerate(float direction) {
		velocity -= direction * acceleration;
	}

	public void turn(float direction) {
		yRotVelocity += direction * rotAcceleration * Math.min(Math.abs(velocity),1);
	}

	public void pitch(float direction) {
		xRotVelocity += direction * rotAcceleration * Math.min(Math.abs(velocity),1);
	}

	public void balast(int i) {
		load -= 0.001;
		load =  Math.max(0, load);
	}

	public void gaz(int i) {
		gas	-= 0.001;      
		gas =  Math.max(0, gas);
	}

	private void printState() {
		float yaw 	= (float) Math.acos(xform.extractRotation().getMatrix().get(0, 0));
		float pitch = (float) Math.acos(xform.extractRotation().getMatrix().get(1, 1));
		//System.out.println(String.format("Velocity %2.2f Pitch %2.2f Yaw %2.2f",velocity, Math.toDegrees(pitch),Math.toDegrees(yaw)));
		//System.out.println(String.format("Gas %2.2f Load %2.2f",gas, load));
	}
}
