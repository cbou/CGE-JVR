package cge.zeppelin;

import java.awt.event.KeyEvent;

public class ControllerManager {
	World world;

	public ControllerManager(final World world) {
		this.world = world;
	}
	
	public void update() {		
		if (world.getInput().isDown('W')){
        	world.flyer.accelerate(1);
        } else if (world.getInput().isDown('S')){
        	world.flyer.accelerate(-1);
        }
		
        if (world.getInput().isDown('A')){
        	world.flyer.turn(1);
        } else if (world.getInput().isDown('D')){
        	world.flyer.turn(-1);
        }
        
        if (world.getInput().isDown('Q')){
        	world.flyer.balast(10);
        }
        
        if (world.getInput().isDown('E')){
        	world.flyer.gaz(10);
        }
        
        if (world.getInput().isDown(KeyEvent.VK_UP)){
        	world.flyer.pitch(1);
        }
        
        if (world.getInput().isDown(KeyEvent.VK_DOWN)){
        	world.flyer.pitch(-1);
        }
   
        if (world.getInput().isTriggered('1')){
        	world.renderer.changeCamera();
        }
        
        if (world.getInput().isDown('M')){
        	world.renderer.zoomIn();
        }
        if (world.getInput().isDown('N')){
        	world.renderer.zoomOut();
        }
        
        if (world.getInput().isTriggered('R')){
        	world.flyer.reset();
        	world.environnement.reset();
        }

        // Weather switcher
        if (world.getInput().isTriggered('T')){
        	world.environnement.switchDisable();
        }

        // Refresh shader switcher
        if (world.getInput().isTriggered('L')){
        	world.switchRefreshShader();
        }
        
        if (world.getInput().isTriggered('K')){
        	world.getTerrain().resetTerrain();
        }
        
        if (world.getInput().isTriggered('J')){
        	world.environnement.rainEntity.reset();
        }
        
        if (world.getInput().isTriggered('9')){
        	world.flyer.checkPoint(c++);
        }
        
	}
	
	int c = 0;
}
