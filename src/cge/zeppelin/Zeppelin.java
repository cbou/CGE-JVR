package cge.zeppelin;

import java.io.FileInputStream;

import javax.media.opengl.GL2GL3;

import cge.zeppelin.util.Helper;
import de.bht.jvr.collada14.loader.ColladaLoader;
import de.bht.jvr.core.Finder;
import de.bht.jvr.core.GroupNode;
import de.bht.jvr.core.SceneNode;
import de.bht.jvr.core.Shader;
import de.bht.jvr.core.ShaderMaterial;
import de.bht.jvr.core.ShaderProgram;
import de.bht.jvr.core.ShapeNode;
import de.bht.jvr.core.Texture2D;
import de.bht.jvr.core.Transform;

public class Zeppelin extends Entity {

	private SceneNode hull;
	private SceneNode cockpit;
	private SceneNode gasMeter;
	private float gasRot;
	private GroupNode gasNode;
	private float gasMeterHeight = 0.04f;
	private GroupNode loadNode;
	private float loadRot;
	private GroupNode boardNode;
	private SceneNode board;
	private ShapeNode sn;
	
	public Zeppelin(GroupNode n){
		try {
			node = n;
			//TODO Modell
			hull 	= ColladaLoader.load(Helper.getFileResource("models/spround.dae"));
			cockpit = ColladaLoader.load(Helper.getFileResource("models/box.dae"));
			gasMeter= ColladaLoader.load(Helper.getFileResource("models/box.dae"));
			board   = ColladaLoader.load(Helper.getFileResource("models/box.dae"));
			
			sn = Finder.find(board, ShapeNode.class, "Box01_Shape");
	        
			hull.setTransform(Transform.scale(1.2f, 1.2f, 6).mul(Transform.translate(0, 1f, 0)));
			cockpit.setTransform(Transform.scale(0.4f, 0.5f, 1));
		
			GroupNode xformN 	= new GroupNode();
			GroupNode sizeN 	= new GroupNode();
		    gasNode 			= new GroupNode();
		    loadNode 			= new GroupNode();
		    boardNode 			= new GroupNode();
		
			gasMeter.setTransform(Transform.translate(0,gasMeterHeight/2,0).mul(Transform.scale(gasMeterHeight/20, gasMeterHeight, gasMeterHeight/20)));
			
			gasNode.setTransform(Transform.translate(-0.1f, -0.1f, -0.2f));
			gasNode.addChildNode(gasMeter);
			
			boardNode.setTransform(Transform.translate(0.0f, -0.11f, -0.21f));
			boardNode.addChildNode(sn);
			sn.setTransform(Transform.translate(0,gasMeterHeight/2,0).mul(Transform.scale(0.34f, 0.07f, 0.0001f)));
			
			loadNode.setTransform(Transform.translate(0.1f, -0.1f, -0.2f));
			loadNode.addChildNode(gasMeter);

			node.addChildNode(xformN);
			xformN.addChildNode(sizeN);
			sizeN.addChildNode(hull);
			sizeN.addChildNode(cockpit);

			node.addChildNode(gasNode);
			node.addChildNode(loadNode);
			node.addChildNode(boardNode);
			
			Texture2D bk = new Texture2D(Helper.getFileResource("textures/wood.png"));
			
//			Shader ambientVs = new Shader(Helper.getInputStreamResource("shaders/null.vs"), GL2GL3.GL_VERTEX_SHADER);
//	        Shader ambientFs = new Shader(Helper.getInputStreamResource("shaders/null.fs"), GL2GL3.GL_FRAGMENT_SHADER);
	    	Shader ambientVs = new Shader(new FileInputStream("./shaders/ambient.vs"), GL2GL3.GL_VERTEX_SHADER);
	        Shader ambientFs = new Shader(new FileInputStream("./shaders/ambient.fs"), GL2GL3.GL_FRAGMENT_SHADER);
	    
	        ShaderProgram ambientProgram = new ShaderProgram(ambientVs, ambientFs);
	        
//			Shader lightingVs = new Shader(Helper.getInputStreamResource("shaders/minimal.vs"), GL2GL3.GL_VERTEX_SHADER);
//	        Shader lightingFs = new Shader(Helper.getInputStreamResource("shaders/phong.fs"), GL2GL3.GL_FRAGMENT_SHADER);
	        Shader lightingVs = new Shader(new FileInputStream("./shaders/lighting.vs"), GL2GL3.GL_VERTEX_SHADER);
	        Shader lightingFs = new Shader(new FileInputStream("./shaders/lighting.fs"), GL2GL3.GL_FRAGMENT_SHADER);
	      
	        ShaderProgram lightingProgram = new ShaderProgram(lightingVs, lightingFs);
	        
	        ShaderMaterial boardMat = new ShaderMaterial();
	        boardMat.setTexture("AMBIENT", "jvr_Texture0", bk);    
	        boardMat.setShaderProgram("AMBIENT", ambientProgram);
//	        
	        boardMat.setTexture("LIGHTING", "jvr_Texture0", bk);    
	        boardMat.setShaderProgram("LIGHTING", lightingProgram);
	        sn.setMaterial(boardMat);
	        
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void updateState(float gas, float load) {
		gasNode.setTransform(gasNode.getTransform().mul(Transform.rotateZ(-gasRot)));
		gasRot = (float) (2-(gas/25f*Math.PI));
		gasNode.setTransform(gasNode.getTransform().mul(Transform.rotateZ(gasRot)));
		
		loadNode.setTransform(loadNode.getTransform().mul(Transform.rotateZ(-loadRot)));
		loadRot = (float) (2-(load/15f*Math.PI));
		loadNode.setTransform(loadNode.getTransform().mul(Transform.rotateZ(loadRot)));
		
	}
	
}
