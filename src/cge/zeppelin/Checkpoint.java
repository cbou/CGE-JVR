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
	private SceneNode sphereModel;
	private ShapeNode particuleShapeNode;
    private AttributeCloud cloud;
    private int count;

    private ArrayList<Vector3> position;
    private ArrayList<Float> age;
    private ArrayList<Float> startAngle;
    private ArrayList<Float> radius;

	private PApplet noiseMaker = new PApplet();
	
	final private boolean oldCheckpoint = false;
	private Vector3 startPos;
	private SceneNode arrowModel;
    
	public Checkpoint(GroupNode n, float s, Vector3 start){
		//node;
		size = s;
		startPos = start;
		count = 1000;
		node = new GroupNode();

		if (oldCheckpoint) {
			try {
				sphereModel   = ColladaLoader.load(Helper.getFileResource("models/sphere.dae"));
				arrowModel   = ColladaLoader.load(Helper.getFileResource("models/arrow.dae"));
			} catch (Exception e) { 	
				e.printStackTrace();
			}
			node.setTransform(Transform.translate(start));
			sphereModel.setTransform(Transform.scale(size, size, size));
			
			node.addChildNodes(sphereModel);
			n.addChildNode(node);
		} else {
			try {
				arrowModel   = ColladaLoader.load(Helper.getFileResource("models/arrow.dae"));
			} catch (Exception e) { 	
				e.printStackTrace();
			}
	        particuleShapeNode = new ShapeNode("Emitter");
	        cloud = new AttributeCloud(count, GL.GL_POINTS);

			node.setTransform(Transform.translate(start));
			node.addChildNode(particuleShapeNode);
			n.addChildNode(particuleShapeNode);
			initParticules();			
		}
		
	}
	
	public void deactivateArrow() {
		node.removeChildNode(arrowModel);
	}
	
	public void activateArrow() {
		node.addChildNode(arrowModel);
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
	
	void refreshShader() {
		if (!oldCheckpoint) {
	        ShaderProgram shader = null;
	        try {
	            Shader vert = new Shader(Helper.getInputStreamResource("/prototype/particule/sparks.vs"), GL3.GL_VERTEX_SHADER);
	            Shader geom = new Shader(Helper.getInputStreamResource("/prototype/particule/sparks.gs"), GL3.GL_GEOMETRY_SHADER);
	            Shader frag = new Shader(Helper.getInputStreamResource("/prototype/particule/sparks.fs"), GL3.GL_FRAGMENT_SHADER);
	
	            shader = new ShaderProgram(vert, frag, geom);            
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	
	        shader.setParameter(GL2GL3.GL_GEOMETRY_INPUT_TYPE_ARB, GL.GL_POINTS);
	        shader.setParameter(GL2GL3.GL_GEOMETRY_OUTPUT_TYPE_ARB, GL2.GL_QUADS);
	        shader.setParameter(GL2GL3.GL_GEOMETRY_VERTICES_OUT_ARB, 4);
	
	        ShaderMaterial material = new ShaderMaterial("AMBIENT", shader);
	        material.setMaterialClass("PARTICLE");
	
	        particuleShapeNode.setGeometry(cloud);
	        particuleShapeNode.setMaterial(material);
		}
	}

    public void manipulate(float elapsed) {
    	arrowModel.setTransform(Transform.rotateZDeg(-90).mul(Transform.translate(-20,-3,-1.5f)));
    	
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
	        // Set
	        cloud.setAttribute("partPosition", new AttributeVector3(position));
	        cloud.setAttribute("partRadius", new AttributeFloat(radius));
		}
    }
}
