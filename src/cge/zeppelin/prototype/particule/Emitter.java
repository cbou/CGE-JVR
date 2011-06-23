package cge.zeppelin.prototype.particule;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GL2GL3;
import javax.media.opengl.GL3;

import cge.zeppelin.util.Helper;

import de.bht.jvr.core.AttributeCloud;
import de.bht.jvr.core.GroupNode;
import de.bht.jvr.core.Shader;
import de.bht.jvr.core.ShaderMaterial;
import de.bht.jvr.core.ShaderProgram;
import de.bht.jvr.core.ShapeNode;
import de.bht.jvr.core.Transform;
import de.bht.jvr.core.attributes.AttributeFloat;
import de.bht.jvr.core.attributes.AttributeVector3;
import de.bht.jvr.core.uniforms.UniformFloat;
import de.bht.jvr.math.Vector3;

public class Emitter {

    private GroupNode parent;
    private AttributeCloud cloud;
    private int count;

    private ArrayList<Vector3> position;
    private ArrayList<Vector3> velocity;
    private ArrayList<Float> energy;
    private ArrayList<Float> age;

    private Vector3 gravity = new Vector3(0, -10, 0);
    private float damping = 0.9f;

    private Vector3 minVel = new Vector3(-2, 5, -2);
    private Vector3 maxVel = new Vector3(2, 10, 2);

    private float minEnergy = 2.0f;
    private float maxEnergy = 4.0f;

    private float emmitRate = 3000;

    private float halfLife = 0.5f;
    private float initEnergy = 1f;

    public Emitter(GroupNode p, int c) {
        parent = p;
        count = c;

        ShapeNode emitter = new ShapeNode("Emitter");
        
        cloud = new AttributeCloud(count, GL.GL_POINTS);
        ShaderProgram shader = null;
        try {
            Resource loader = new Resource(getClass());
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

        emitter.setGeometry(cloud);
        emitter.setMaterial(material);

        parent.addChildNode(emitter);

        position = new ArrayList<Vector3>(count);
        for (int i = 0; i != count; i++)
            position.add(new Vector3(0, 0, 0));

        velocity = new ArrayList<Vector3>(count);
        for (int i = 0; i != count; i++)
            velocity.add(new Vector3(0, 0, 0));

        energy = new ArrayList<Float>(count);
        for (int i = 0; i != count; i++)
            energy.add(0.0f);

        age = new ArrayList<Float>(count);
        for (int i = 0; i != count; i++)
            age.add(Float.POSITIVE_INFINITY);

        cloud.setAttribute("partPosition", new AttributeVector3(position));
        cloud.setAttribute("partVelocity", new AttributeVector3(velocity));
        cloud.setAttribute("partEnergy", new AttributeFloat(energy));

        material.setUniform("AMBIENT", "maxEnergy", new UniformFloat(maxEnergy));
    }

    public void simulate(float elapsed) {
        
        float toEmmit = (int) (emmitRate * elapsed);

        for (int i = 0; i != count; i++) {

            // Emmit if neccessary and possible
            if (energy.get(i) <= 0.1f && toEmmit != 0) {
                Transform t = parent.getWorldTransform(null);
                position.set(i, t.getMatrix().mulPoint(new Vector3(0, 0, 0)));
                velocity.set(i, t.getMatrix().mulDir(randomVector3(minVel, maxVel)));
                energy.set(i, (float) 1);
                age.set(i, (float) 0);
                toEmmit -= 1;
            }

            // Simulate
            age.set(i, age.get(i) + elapsed);
            velocity.set(i, velocity.get(i).add(gravity.mul(elapsed)));
            position.set(i, position.get(i).add(velocity.get(i).mul(elapsed)));
            energy.set(i, (float) (initEnergy * Math.pow(0.5f, age.get(i) / halfLife)));
        }
        // Set
        cloud.setAttribute("partPosition", new AttributeVector3(position));
        cloud.setAttribute("partEnergy", new AttributeFloat(energy));
    }

    private static Random random = new Random();

    private float randomValue(float min, float max) {
        return min + random.nextFloat() * (max - min);
    }

    private Vector3 randomVector3(Vector3 min, Vector3 max) {
        return new Vector3(randomValue(min.x(), max.x()), randomValue(min.y(), max.y()),
                randomValue(min.z(), max.z()));
    }

}
