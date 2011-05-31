package cge.zeppelin;

import java.awt.event.KeyEvent;

import de.bht.jvr.core.Transform;
import de.bht.jvr.math.Matrix4;
import de.bht.jvr.math.Vector3;

public class ControllerManager {
	World world;

	public ControllerManager(final World world) {
		this.world = world;
	}
	
	public void update() {		
		if (world.input.isDown('W')){
        	world.flyer.accelerate(1);
        } else if (world.input.isDown('S')){
        	world.flyer.accelerate(-1);
        }
		
        if (world.input.isDown('A')){
        	world.flyer.turn(1);
        } else if (world.input.isDown('D')){
        	world.flyer.turn(-1);
        }
        
        if (world.input.isDown('Q')){
        	world.flyer.balast(1);
        }
        
        if (world.input.isDown('E')){
        	world.flyer.gaz(100);
        }
        
        if (world.input.isDown(KeyEvent.VK_UP)){
        	world.flyer.pitch(1);
        }
        
        if (world.input.isDown(KeyEvent.VK_DOWN)){
        	world.flyer.pitch(-1);
        }
   
        if (world.input.isTriggered('1')){
        	world.renderer.switchCamera();
        }
        
        if (world.input.isDown('M')){
        	world.renderer.zoomIn();
        }
        if (world.input.isDown('N')){
        	world.renderer.zoomOut();
        }

        if (world.input.isDown('J')){
            world.add(Entity.makeCube(new Vector3(1, 1, 1), 0.1f, Matrix4.translate(3, 16, 0)));
        }
        
        if (world.input.isDown('I')){
        	// Back to start
        	world.flyer.translation = Transform.translate(new Vector3(3, 10, 0));
        	world.flyer.rotation = Transform.rotate(new Vector3(0, 1, 0), 0);
            world.flyer.update();
        }

        
	}
	
}
