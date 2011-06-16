package cge.zeppelin;
import java.awt.Dimension;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.JFrame;

import com.jogamp.opengl.util.FPSAnimator;

import de.bht.jvr.logger.Log;
import de.bht.jvr.logger.LogPrinter;
import de.bht.jvr.math.Vector3;
import de.bht.jvr.util.awt.InputState;
import de.bht.jvr.util.StopWatch;

/**
 * A medium complex example showing integration of the jVR renderer and the
 * jBullet library. Use an AWT widget for OpenGL context creation.
 */
public class Main {

    static Renderer renderer = new Renderer();
    static Simulator simulator = new Simulator(new Vector3(0, -10, 0));
    static InputState input = new InputState();
    static World world = new World(input, simulator, renderer);
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
        JFrame gui = new JFrame("Physics Integration Example");

        GLCanvas glPanel = new GLCanvas();
        FPSAnimator animator = new FPSAnimator(glPanel, 60);

        glPanel.addGLEventListener(app);
        glPanel.addKeyListener(input);

        gui.getContentPane().add(glPanel);

        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gui.setMinimumSize(new Dimension(400, 400));
        gui.setVisible(true);

        glPanel.requestFocusInWindow();
        animator.start();
        
    }
}
