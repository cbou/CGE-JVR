package cge.zeppelin;
import java.awt.event.KeyEvent;

import de.bht.jvr.core.SceneNode;
import de.bht.jvr.core.Transform;
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

    float acceleration 		= 0.5f; 	// m/s
    float rotAcceleration	= 0.05f; 	// rad/s
    
    float yRotVelocity 		= 0;
    float xRotVelocity 		= 0;
    float velocity 			= 0; 		// m/s
    
    float gravity 			= -10;
    float gas				=  25;
    float load				=  15;
    
	private float friction = 0.01f;
    
    /**
     * Create a new flyer and attach it to an existing scene node. Needs a
     * reference to the world for access to input and scene state.
     */
    Flyer(World w, SceneNode n, Vector3 start) {
        super(n, null, 0);

        translation = Transform.translate(start);
        rotation 	= Transform.rotate(new Vector3(0, 1, 0), 0);

        update();
    }

    public Flyer(World world, SceneNode node, Vector3 vector3, float  acc) {
		this(world,node,vector3);
		this.acceleration= acc;
	}

	/*
     * (non-Javadoc)
     * @see Entity#manipulate(float, World)
     */
    @Override
    void manipulate(float dt, World world) {
              
        rotation = Transform.rotate(new Vector3(0, 1, 0), yRotVelocity*dt).mul(rotation);
        rotation = rotation.mul(Transform.rotate(new Vector3(1, 0, 0), xRotVelocity * dt));

        translation = 
        	translation.mul(
        			Transform.translate(rotation.getMatrix().mulDir(new Vector3(0, 0, velocity * dt))));
    
        //Gravity, Gas and Balance
        float overAllGravity = gravity+gas-load;
        translation = translation.mul(Transform.translate(0,overAllGravity,0));
       
        
        //TODO Alle Velocities in einen Vektor
        // Friction
        velocity *= 1-(friction*Math.abs(velocity));
//        velocity *= 1-friction;

        velocity = Math.abs(velocity) < 0.01 ? 0 : velocity;
      
        yRotVelocity *= 1-friction;
        yRotVelocity = Math.abs(yRotVelocity) < 0.01 ? 0 : yRotVelocity;
      
        xRotVelocity *= 1-friction;
        xRotVelocity = Math.abs(xRotVelocity) < 0.01 ? 0 : xRotVelocity;
      
        //TODO Centrifugal force for Roll
        
        world.environnement.affect(this, dt);
        update();
    }

    private void printState() {
    	float yaw 	= (float) Math.acos(xform.extractRotation().getMatrix().get(0, 0));
    	float pitch = (float) Math.acos(xform.extractRotation().getMatrix().get(1, 1));
    	//System.out.println(String.format("Velocity %2.2f Pitch %2.2f Yaw %2.2f",velocity, Math.toDegrees(pitch),Math.toDegrees(yaw)));
    	//System.out.println(String.format("Gas %2.2f Load %2.2f",gas, load));
    }

	private void update() {
        xform = translation.mul(rotation);
        node.setTransform(xform);
        
        printState(); 
    }
    
    public void accelerate(float direction) {
    	velocity -= direction * acceleration;
    }
    
    public void turn(float direction) {
    	yRotVelocity += direction * rotAcceleration;
    }
    
    public void pitch(float direction) {
    	xRotVelocity += direction * rotAcceleration;
    }

	public void balast(int i) {
    	load -= 0.001;
    	load =  Math.max(0, load);
	}

	public void gaz(int i) {
    	gas	-= 0.001;      
    	gas =  Math.max(0, gas);
	}
}
