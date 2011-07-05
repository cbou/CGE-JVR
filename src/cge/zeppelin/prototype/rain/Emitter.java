package cge.zeppelin.prototype.rain;

import java.io.IOException;
import java.util.ArrayList;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GL2GL3;
import javax.media.opengl.GL3;

import processing.core.PApplet;
import cge.zeppelin.util.Helper;
import de.bht.jvr.core.AttributeCloud;
import de.bht.jvr.core.GroupNode;
import de.bht.jvr.core.Shader;
import de.bht.jvr.core.ShaderMaterial;
import de.bht.jvr.core.ShaderProgram;
import de.bht.jvr.core.ShapeNode;
import de.bht.jvr.core.attributes.AttributeVector3;
import de.bht.jvr.math.Vector3;

public class Emitter {

    private GroupNode parent;
    private AttributeCloud cloud;
    private int count;

    private ArrayList<Vector3> position;
    private ArrayList<Float> age;
    
    private float speed = 6;

	private PApplet noiseMaker = new PApplet();
	private ShapeNode emitter;

    public Emitter(GroupNode p, int c) {
        parent = p;
        count = c;

        emitter = new ShapeNode("Emitter");
        
        cloud = new AttributeCloud(count, GL.GL_POINTS);
        ShaderProgram shader = null;
        try {
            Shader vert = new Shader(Helper.getInputStreamResource("prototype/rain/sparks.vs"), GL3.GL_VERTEX_SHADER);
            Shader geom = new Shader(Helper.getInputStreamResource("prototype/rain/sparks.gs"), GL3.GL_GEOMETRY_SHADER);
            Shader frag = new Shader(Helper.getInputStreamResource("prototype/rain/sparks.fs"), GL3.GL_FRAGMENT_SHADER);
            
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

        emitter.setGeometry(cloud);
        emitter.setMaterial(material);

        parent.addChildNode(emitter);

        position = new ArrayList<Vector3>(count);
        for (int i = 0; i != count; i++)
            position.add(new Vector3(noiseMaker.random(-2, 2), 0, noiseMaker.random(-2, 2)));

        age = new ArrayList<Float>(count);
        for (int i = 0; i != count; i++)
            age.add(noiseMaker.random(0, 3));
        
        cloud.setAttribute("partPosition", new AttributeVector3(position));
    }

    public void simulate(float elapsed) {
        for (int i = 0; i != count; i++) {
            age.set(i, age.get(i) + elapsed* speed);
            if (age.get(i) > 3) {
            	age.set(i, (float) noiseMaker.random(-1, 0));
            }

            float y = PApplet.map(age.get(i), 0, 3, 3, 0);
            
            position.set(i, new Vector3(position.get(i).x(),y,position.get(i).z()));
        }
        
        cloud.setAttribute("partPosition", new AttributeVector3(position));
    }

     
    public void refreshShader(){
    	ShaderProgram shader = null;
    	     
    	try {
            Shader vert = new Shader(Helper.getInputStreamResource("prototype/rain/sparks.vs"), GL3.GL_VERTEX_SHADER);
            Shader geom = new Shader(Helper.getInputStreamResource("prototype/rain/sparks.gs"), GL3.GL_GEOMETRY_SHADER);
            Shader frag = new Shader(Helper.getInputStreamResource("prototype/rain/sparks.fs"), GL3.GL_FRAGMENT_SHADER);

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
    
}
