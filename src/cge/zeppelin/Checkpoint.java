package cge.zeppelin;

import java.io.IOException;
import java.util.ArrayList;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GL2GL3;
import javax.media.opengl.GL3;

import processing.core.PApplet;
import cge.zeppelin.util.Helper;
import de.bht.jvr.collada14.loader.ColladaLoader;
import de.bht.jvr.core.AttributeCloud;
import de.bht.jvr.core.Finder;
import de.bht.jvr.core.Geometry;
import de.bht.jvr.core.GroupNode;
import de.bht.jvr.core.SceneNode;
import de.bht.jvr.core.Shader;
import de.bht.jvr.core.ShaderMaterial;
import de.bht.jvr.core.ShaderProgram;
import de.bht.jvr.core.ShapeNode;
import de.bht.jvr.core.Transform;
import de.bht.jvr.core.attributes.AttributeFloat;
import de.bht.jvr.core.attributes.AttributeVector3;
import de.bht.jvr.math.Vector3;

public class Checkpoint extends Entity {
	
	float size;
	
	SceneNode sphereModel;
	ShapeNode particuleShapeNode;
    AttributeCloud cloud;
    int count;

    ArrayList<Vector3> position;
    ArrayList<Float> age;
    ArrayList<Float> startAngle;
    ArrayList<Float> radius;

	PApplet noiseMaker = new PApplet();
	
	Vector3 startPos;
	SceneNode arrowModel;
	ShapeNode arrowShapeNode;
	
	final boolean oldCheckpoint = false;

	private World world;

	public Checkpoint(GroupNode n, float s, Vector3 start, World w){
		size = s;
		startPos = start;
		count = 1000;
		world = w;
		node = new GroupNode();

		try {
			arrowModel   = ColladaLoader.load(Helper.getFileResource("models/arrow.dae"));
		} catch (Exception e) { 	
			e.printStackTrace();
		}

		arrowShapeNode = new ShapeNode("Arrow");
        Geometry arrowGeom = Finder.findGeometry(arrowModel, null);
        arrowShapeNode.setGeometry(arrowGeom);
		
		if (oldCheckpoint) {
			
			try {
				sphereModel   = ColladaLoader.load(Helper.getFileResource("models/sphere.dae"));
			} catch (Exception e) { 	
				e.printStackTrace();
			}
			
			node.setTransform(Transform.translate(start));
			sphereModel.setTransform(Transform.scale(size, size, size));
			
			node.addChildNodes(sphereModel);
			n.addChildNode(node);
			
		} else {

	        particuleShapeNode = new ShapeNode("Emitter");
	        cloud = new AttributeCloud(count, GL.GL_POINTS);

			node.setTransform(Transform.translate(start));
			node.addChildNode(particuleShapeNode);
			initParticules();
			
		}
		
		refreshShader();
	}

	protected void initParticules() {

        position = new ArrayList<Vector3>(count);
        for (int i = 0; i != count; i++)
            position.add(new Vector3(0, 0, 0));

        age = new ArrayList<Float>(count);
        for (int i = 0; i != count; i++)
            age.add(Float.POSITIVE_INFINITY);

        startAngle = new ArrayList<Float>(count);
		for (int i = 0; i != count; i++) {
			float p = PApplet.map(i, 0, count, 0, 360);
        	startAngle.add(p);
		}

        radius = new ArrayList<Float>(count);
        for (int i = 0; i != count; i++)
        	radius.add(noiseMaker.random(1,2));
        
        cloud.setAttribute("partPosition", new AttributeVector3(position));
        
	}
	
	public void refreshShader() {
		
		// can not compile if there is no context
		if (world.renderer.ctx == null) return;
		
		Shader vert = null;
        Shader frag = null;
        Shader geom = null;
        
		ShaderProgram shader = null;
		ShaderMaterial material = null;
        
		if (!oldCheckpoint) {
	        shader = null;
	        try {
	            vert = new Shader(Helper.getInputStreamResource("shaders/checkpointParticule.vs"), GL3.GL_VERTEX_SHADER);
	            geom = new Shader(Helper.getInputStreamResource("shaders/checkpointParticule.gs"), GL3.GL_GEOMETRY_SHADER);
	            frag = new Shader(Helper.getInputStreamResource("shaders/checkpointParticule.fs"), GL3.GL_FRAGMENT_SHADER); 
		        
		        // Without compile the game stops if error in shader
	            vert.compile(world.renderer.ctx);
		        geom.compile(world.renderer.ctx);
		        frag.compile(world.renderer.ctx);
		        
	        } catch (IOException e) {
	            e.printStackTrace();
	        } catch (Exception e) {
				e.printStackTrace();
		    	System.out.println("Can not compile shader!");
			}
	        
            shader = new ShaderProgram(vert, frag, geom);
	
	        shader.setParameter(GL2GL3.GL_GEOMETRY_INPUT_TYPE_ARB, GL.GL_POINTS);
	        shader.setParameter(GL2GL3.GL_GEOMETRY_OUTPUT_TYPE_ARB, GL2.GL_QUADS);
	        shader.setParameter(GL2GL3.GL_GEOMETRY_VERTICES_OUT_ARB, 4);
	
	        material = new ShaderMaterial("AMBIENT", shader);
	        material.setMaterialClass("PARTICLE");
	
	        particuleShapeNode.setGeometry(cloud);
	        particuleShapeNode.setMaterial(material);
		}
		
		shader = null;
		
        try {
            vert = new Shader(Helper.getInputStreamResource("shaders/arrow.vs"), GL3.GL_VERTEX_SHADER);
            frag = new Shader(Helper.getInputStreamResource("shaders/arrow.fs"), GL3.GL_FRAGMENT_SHADER);
        } catch (IOException e) {
            e.printStackTrace();
        }

        shader = new ShaderProgram(vert, frag);  
        material = new ShaderMaterial("AMBIENT", shader);
        arrowShapeNode.setMaterial(material);
	}

    public void manipulate(float elapsed) {

    	arrowShapeNode.setTransform(Transform.rotateZDeg(-90).mul(Transform.translate(-17,-3,-1.5f)).mul(Transform.scale(0.2f)));
    	
		if (!oldCheckpoint) {
			
	        for (int i = 0; i != count; i++) {
	        	
	            age.set(i, age.get(i) + elapsed*50);
	
	            if (age.get(i) > 360) {
	            	age.set(i, (float) 0);
	            }
	
	            float n = (float) noiseMaker.noise(age.get(i));
	            
	        	float r = age.get(i);
	            float x1 = (float) (1 + Math.cos(PApplet.radians(r + startAngle.get(i))) * (radius.get(i)+n/30));
	            float y1 = (float) (1 + Math.sin(PApplet.radians(r + startAngle.get(i))) * (radius.get(i)+n/30));
	            position.set(i, new Vector3(x1,y1,1));
	            
	        }
	
	        cloud.setAttribute("partPosition", new AttributeVector3(position));
	        cloud.setAttribute("partRadius", new AttributeFloat(radius));
		}
    }
	
    /**
     * displays arrow above checkpoint
     */
	public void deactivateArrow() {
		
		node.removeChildNode(arrowShapeNode);
		
	}

	
    /**
     * removes arrow above checkpoint
     */
	public void activateArrow() {
		
		node.addChildNode(arrowShapeNode);
		
	}
}
