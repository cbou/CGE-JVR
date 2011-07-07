package cge.zeppelin;

import java.awt.event.KeyEvent;

import de.bht.jvr.math.Matrix4;
import de.bht.jvr.math.Vector3;

public class ControllerManager {
	World world;
	private float f;

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
        	world.flyer.balast(10);
        }
        
        if (world.input.isDown('E')){
        	world.flyer.gaz(10);
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
        
        if (world.input.isTriggered('R')){
        	world.flyer.reset();
        	world.environnement.reset();
        }

        // Weather switcher
        if (world.input.isTriggered('T')){
        	world.environnement.switchDisable();
        }

        // Refresh shader switcher
        if (world.input.isTriggered('L')){
        	world.switchRefreshShader();
        }
        
        if (world.input.isTriggered('K')){
        	world.terrain.resetTerrain();
        }
        
        if (world.input.isTriggered('J')){
        	world.environnement.rainEntity.reset();
        }
        
	}
	
}
