package cge.zepelin;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.media.opengl.GLAutoDrawable;

import de.bht.jvr.util.awt.*;

/**
 * Represent a world of entites that can be rendered and simulated.
 */
public class World {

    final Set<Entity> entities = Collections.synchronizedSet(new HashSet<Entity>());
    final InputState input;
    final Simulator simulator;
    final Renderer renderer;

    /**
     * Create a new world.
     */
    public World(InputState i, Simulator s, Renderer r) {
        input = i;
        simulator = s;
        renderer = r;
    }

    /**
     * Add an entity.
     */
    void add(Entity... es) {
        for (Entity e : es) {
            entities.add(e);
            if (e.node != null)
                renderer.add(e.node);
            if (e.body != null)
                simulator.add(e.body);
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
            if (e.body != null)
                simulator.remove(e.body);
        }
    }

    void frame(float elapsed, GLAutoDrawable drawable) {
        for (Entity e : entities)
            e.manipulate(elapsed, this);
        input.frame(elapsed);
        simulator.simulate(elapsed);
        renderer.render(drawable);
    }
}
