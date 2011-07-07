package cge.zeppelin;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.media.opengl.GLAutoDrawable;

import cge.zeppelin.environnement.EnvironnementManager;
import de.bht.jvr.core.Transform;
import de.bht.jvr.math.Vector3;
import de.bht.jvr.util.awt.InputState;

/**
 * Represent a world of entites that can be rendered and simulated.
 */
public class World {

    final Set<Entity> entities = Collections.synchronizedSet(new HashSet<Entity>());
    final InputState input;
    final Renderer renderer;
    final EnvironnementManager environnement;
    final Flyer flyer;
    final Skybox skybox;
	final Terrain terrain;
	
	private boolean refreshShader = true;

    /**
     * Create a new world.
     */
    public World(InputState i, Renderer r) {
    	
        input = i;
        renderer = r;
        environnement = new EnvironnementManager(this, renderer.zeppelinNode);

        terrain = new Terrain(this);
        flyer = new Flyer(renderer.zeppelinNode, new Vector3(3, 10, 0), getTerrain()); 
        skybox = new Skybox(this, renderer.skyboxNode); 
        
        populateWorld(50, 200);
        
    }
    

    /**
     * Add an entity.
     */
    public void add(Entity... es) {
        for (Entity e : es) {
            entities.add(e);
            if (e.node != null)
                renderer.add(e.node);
        }
    }

    /**
     * Remove an entity.
     */
    void remove(Entity... es) {
        for (Entity e : es) {
            entities.remove(e);
            if (e.node != null)
                renderer.remove(e.node);
        }
    }

    void frame(float elapsed, GLAutoDrawable drawable) {
    	
        renderer.cameraExtern.setTransform(Transform.translate(new Vector3(0,3.5f,+9)));
    	getSkybox().update();
    	environnement.update();
    	
        for (Entity e : entities) {
            e.manipulate(elapsed);

            if (refreshShader) {
            	e.refreshShader();
            }
        }
        
        environnement.affect(flyer, elapsed);
        getInput().frame(elapsed);

        renderer.render(drawable);
    }
    
    /**
     * Populate the world with the neccessary entities.
     */
    void populateWorld( int bullets, int boxes) {
    	
        renderer.spot.setTransform(Transform.translate(20, 120, 20)
                .mul(Transform.rotateY(0.8f)).mul(Transform.rotateX(-0.8f)));

        getTerrain().node.setTransform(Transform.translate(-300,0,-300));
        
        renderer.cameraFixed.setTransform(Transform.translate(new Vector3(0,0,0)));
        renderer.cameraExtern.setTransform(Transform.translate(new Vector3(0,0,0)));

        add(getTerrain());
        add(flyer);
        add(getSkybox());
        
        
//    	SpotLightNode zepSpot = new SpotLightNode();
//    	zepSpot.setCastShadow(true);
//    	zepSpot.setSpotCutOff(30);
//    	zepSpot.setShadowBias(0.3f);
//    	zepSpot.setIntensity(0.8f);
//    	zepSpot.setSpecularColor(new Color(0.8f, 0.5f, 0.8f));
//    	zepSpot.setDiffuseColor(new Color(0.8f, 0.5f, 0.8f));
//    	flyer.node.addChildNode(zepSpot);
/*
        simulator.addCollisionListener(flyer, new CollisionListener() {
            @Override
            public void response(Entity e0, Entity e1) {
                System.out.println("Collision detected");
            }
        });
        */
    }
	
	public void switchRefreshShader() {
		refreshShader = !refreshShader;
		if (refreshShader) {
			System.out.println("START REFRESH SHADER");
		} else {
			System.out.println("STOP REFRESH SHADER");
		}
	}


	public InputState getInput() {
		return input;
	}


	public Skybox getSkybox() {
		return skybox;
	}


	public Terrain getTerrain() {
		return terrain;
	} 
}
