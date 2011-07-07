package cge.zeppelin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.media.opengl.GL2GL3;

import cge.zeppelin.util.Helper;
import de.bht.jvr.collada14.loader.ColladaLoader;
import de.bht.jvr.core.Finder;
import de.bht.jvr.core.GroupNode;
import de.bht.jvr.core.Printer;
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
		node = n;
		
		try {
			//TODO Modell
			hull 	= ColladaLoader.load(Helper.getFileResource("models/spround.dae"));
			cockpit = ColladaLoader.load(Helper.getFileResource("models/box.dae"));
			gasMeter= ColladaLoader.load(Helper.getFileResource("models/box.dae"));
			board   = ColladaLoader.load(Helper.getFileResource("models/cockpit.dae"));
			
			sn = Finder.find(board, ShapeNode.class, "shape0_Shape");
	        
			hull.setTransform(Transform.scale(1.2f, 1.2f, 6).mul(Transform.translate(0, 1f, 0)));
			cockpit.setTransform(Transform.scale(0.4f, 0.5f, 1));
		
			gasMeter.setTransform(Transform.translate(0,gasMeterHeight/2,0).mul(Transform.scale(gasMeterHeight/20, gasMeterHeight, gasMeterHeight/20)));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		GroupNode xformN 	= new GroupNode();
		GroupNode sizeN 	= new GroupNode();
	    gasNode 			= new GroupNode();
	    loadNode 			= new GroupNode();
	    boardNode 			= new GroupNode();
		
		gasNode.setTransform(Transform.translate(-0.1f, -0.1f, -0.2f));
		gasNode.addChildNode(gasMeter);
		
		boardNode.setTransform(Transform.translate(0.0f, -0.14f, -0.21f));
		//boardNode.addChildNode(board);
		boardNode.addChildNode(sn);
		
		//sn.setTransform(Transform.translate(0,gasMeterHeight/2,0).mul(Transform.scale(0.34f, 0.07f, 0.0001f)));
		sn.setTransform(Transform.translate(0,gasMeterHeight/2,0).mul(Transform.scale(0.024f, 0.022f, 0.002f)));
		
		loadNode.setTransform(Transform.translate(0.1f, -0.1f, -0.2f));
		loadNode.addChildNode(gasMeter);

		node.addChildNode(xformN);
		xformN.addChildNode(sizeN);
		sizeN.addChildNode(hull);
		sizeN.addChildNode(cockpit);

		node.addChildNode(gasNode);
		node.addChildNode(loadNode);
		node.addChildNode(boardNode);
		
		refreshShader();
	}
	
	public void refreshShader() {
		Shader lightingVs = null;
		Shader lightingFs = null;
		Shader ambientVs = null;
		Shader ambientFs = null;

		Texture2D bk = null;
		
		try {
			
			bk = new Texture2D(Helper.getFileResource("textures/wood.png"));
	        
	        lightingVs = new Shader(Helper.getInputStreamResource("shaders/zeppelinLighting.vs"), GL2GL3.GL_VERTEX_SHADER);
	        lightingFs = new Shader(Helper.getInputStreamResource("shaders/zeppelinLighting.fs"), GL2GL3.GL_FRAGMENT_SHADER);
			
	    	ambientVs = new Shader(Helper.getInputStreamResource("shaders/zeppelinAmbient.vs"), GL2GL3.GL_VERTEX_SHADER);
	        ambientFs = new Shader(Helper.getInputStreamResource("shaders/zeppelinAmbient.fs"), GL2GL3.GL_FRAGMENT_SHADER);
	        
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    
		ShaderProgram ambientProgram = new ShaderProgram(ambientVs, ambientFs);
      
        ShaderProgram lightingProgram = new ShaderProgram(lightingVs, lightingFs);
        
        ShaderMaterial boardMat = new ShaderMaterial();
        boardMat.setTexture("AMBIENT", "jvr_Texture0", bk);    
        boardMat.setShaderProgram("AMBIENT", ambientProgram);	        
        boardMat.setTexture("LIGHTING", "jvr_Texture0", bk);    
        boardMat.setShaderProgram("LIGHTING", lightingProgram);
        
        sn.setMaterial(boardMat);
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
