package cge.zeppelin;
import java.awt.Dimension;

import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.JFrame;

import cge.zeppelin.util.Splashscreen;

import com.jogamp.opengl.util.FPSAnimator;

import de.bht.jvr.logger.Log;
import de.bht.jvr.logger.LogPrinter;
import de.bht.jvr.util.StopWatch;
import de.bht.jvr.util.awt.InputState;

public class Main {
	
	static Splashscreen splashscreen = new Splashscreen();
    static Renderer renderer = new Renderer();
    static InputState input = new InputState();
    static World world = new World(input, renderer);
    static ControllerManager controllerManager = new ControllerManager(world);
    static RaceManager raceManager = new RaceManager(world, renderer.sceneNode);
    static StopWatch clock = new StopWatch();
    
    /**
     * Main entry point for the application.
     */
    public static void main(String[] args) {
    	
    	Log.addLogListener(new LogPrinter(-1, 0, 0));
        GLEventListener app = new GLEventListener() {

            @Override
            public void display(GLAutoDrawable drawable) {
            	float elapsed = clock.elapsed();
                controllerManager.update();
                raceManager.update(elapsed);
                world.frame(elapsed, drawable);
            }

            @Override
            public void dispose(GLAutoDrawable drawable) {}

            @Override
            public void init(GLAutoDrawable drawable) {
                renderer.init(drawable);
            }

            @Override
            public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
                renderer.resizeWindowTo(width, height);
            }
        };

        // Setup a simple Swing frame a stablish an OpenGL context.
        JFrame gui = new JFrame("Zeppelin-Commander");

        GLCanvas glPanel = new GLCanvas();
        FPSAnimator animator = new FPSAnimator(glPanel, 60);

        glPanel.addGLEventListener(app);
        glPanel.addKeyListener(input);

        gui.getContentPane().add(glPanel);

        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gui.setMinimumSize(new Dimension(1024, 768));
        gui.setVisible(true);
        splashscreen.close();
        
        glPanel.requestFocusInWindow();
        animator.start();
        
    }
}
