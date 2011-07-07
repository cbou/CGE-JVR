package cge.zeppelin.environnement;

import java.io.IOException;
import java.util.ArrayList;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GL2GL3;
import javax.media.opengl.GL3;

import processing.core.PApplet;

import cge.zeppelin.Entity;
import cge.zeppelin.util.Helper;
import de.bht.jvr.core.AttributeCloud;
import de.bht.jvr.core.GroupNode;
import de.bht.jvr.core.Shader;
import de.bht.jvr.core.ShaderMaterial;
import de.bht.jvr.core.ShaderProgram;
import de.bht.jvr.core.ShapeNode;
import de.bht.jvr.core.attributes.AttributeVector3;
import de.bht.jvr.math.Vector3;

public class RainEntity extends Entity {
	
	GroupNode node;

    private AttributeCloud cloud;
    private int count;

    private ArrayList<Vector3> position;
    private ArrayList<Float> age;
    private ArrayList<Float> deep;
    
    final private float speed = 8;

	private PApplet noiseMaker = new PApplet();
	private ShapeNode emitter;
	
	private boolean isRunning = false;
	
	public RainEntity(GroupNode n) {
		node = n;

        count = 1000;
        emitter = new ShapeNode("Emitter");
        
        ShaderProgram shader = null;
        try {
            Shader vert = new Shader(Helper.getInputStreamResource("shaders/rain.vs"), GL3.GL_VERTEX_SHADER);
            Shader geom = new Shader(Helper.getInputStreamResource("shaders/rain.gs"), GL3.GL_GEOMETRY_SHADER);
            Shader frag = new Shader(Helper.getInputStreamResource("shaders/rain.fs"), GL3.GL_FRAGMENT_SHADER);
            
            shader = new ShaderProgram(vert, frag, geom);            
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
			e.printStackTrace();
		}

        shader.setParameter(GL2GL3.GL_GEOMETRY_INPUT_TYPE_ARB, GL.GL_POINTS);
        shader.setParameter(GL2GL3.GL_GEOMETRY_OUTPUT_TYPE_ARB, GL2.GL_QUADS);
        shader.setParameter(GL2GL3.GL_GEOMETRY_VERTICES_OUT_ARB, 4);

        ShaderMaterial material = new ShaderMaterial("AMBIENT", shader);
        material.setMaterialClass("PARTICLE");

        emitter.setMaterial(material);

        reset();
	}
	
	public void reset() {
		System.out.println("Reset rain effect");
		
		int rainWidth = 8;
		
        count = 20000;
        
        position = new ArrayList<Vector3>(count);
        for (int i = 0; i != count; i++)
            position.add(new Vector3(noiseMaker.random(-1 * rainWidth, 1 * rainWidth), 0, noiseMaker.random(-10, +10)));

        deep = new ArrayList<Float>(count);
        for (int i = 0; i != count; i++)
        	deep.add((float) noiseMaker.random(7, 9));

        age = new ArrayList<Float>(count);
        for (int i = 0; i != count; i++)
            age.add((float) noiseMaker.random(-1 * deep.get(i) * 4, 0));

        cloud = new AttributeCloud(count, GL.GL_POINTS);
        cloud.setAttribute("partPosition", new AttributeVector3(position));
        emitter.setGeometry(cloud);
	}

    public void manipulate(float elapsed) {
	        for (int i = 0; i != count; i++) {
	            age.set(i, age.get(i) + elapsed* speed);
	            if (age.get(i) > deep.get(i)) {
	            	if (isRunning) {
	            		age.set(i, (float) noiseMaker.random(-1 * deep.get(i) / 2, 0));
	            	}
	            }
	
	            float y = PApplet.map(age.get(i), 0, 3, 3, 0);
	            
	            position.set(i, new Vector3(position.get(i).x(),y,position.get(i).z()));
	        }
	        
	        cloud.setAttribute("partPosition", new AttributeVector3(position));
    }

     
    public void refreshShader(){
    	ShaderProgram shader = null;
    	     
    	try {
            Shader vert = new Shader(Helper.getInputStreamResource("shaders/rain.vs"), GL3.GL_VERTEX_SHADER);
            Shader geom = new Shader(Helper.getInputStreamResource("shaders/rain.gs"), GL3.GL_GEOMETRY_SHADER);
            Shader frag = new Shader(Helper.getInputStreamResource("shaders/rain.fs"), GL3.GL_FRAGMENT_SHADER);

            shader = new ShaderProgram(vert, frag, geom);            
        } catch (IOException e) {
            e.printStackTrace();
        }

        shader.setParameter(GL2GL3.GL_GEOMETRY_INPUT_TYPE_ARB, GL.GL_POINTS);
        shader.setParameter(GL2GL3.GL_GEOMETRY_OUTPUT_TYPE_ARB, GL2.GL_QUADS);
        shader.setParameter(GL2GL3.GL_GEOMETRY_VERTICES_OUT_ARB, 4);

        ShaderMaterial material = new ShaderMaterial("AMBIENT", shader);
        material.setMaterialClass("PARTICLE");

        emitter.setMaterial(material);

    }
    
    public void start() {
    	isRunning = true;
		node.addChildNode(emitter);
        reset();
    }
    
    public void stop() {
    	System.out.println("STOP");
    	isRunning = false;
		//node.removeChildNode(emitter);
    }
}
