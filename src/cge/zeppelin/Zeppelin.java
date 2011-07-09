package cge.zeppelin;

import java.io.FileNotFoundException;

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
	private SceneNode leftBoard;
	private ShapeNode leftBoardNode;
	private ShapeNode rightBoardNode;
	private SceneNode rightBoard;
	private SceneNode bottomBoard;
	private ShapeNode bottomBoardNode;
	private SceneNode wing;
	private SceneNode wing2;
	
	public Zeppelin(GroupNode n){
		node = n;
		
		try {
			//TODO Modell
			hull 	= ColladaLoader.load(Helper.getFileResource("models/spround.dae"));
			cockpit = ColladaLoader.load(Helper.getFileResource("models/box.dae"));
			gasMeter= ColladaLoader.load(Helper.getFileResource("models/box.dae"));
			wing 	= ColladaLoader.load(Helper.getFileResource("models/box.dae"));
			wing2 	= ColladaLoader.load(Helper.getFileResource("models/box.dae"));
				
			leftBoard       = ColladaLoader.load(Helper.getFileResource("models/plane.dae"));
			rightBoard      = ColladaLoader.load(Helper.getFileResource("models/plane.dae"));
			bottomBoard     = ColladaLoader.load(Helper.getFileResource("models/plane.dae"));
			
			leftBoardNode   = Finder.find(leftBoard, ShapeNode.class, "Plane01_Shape");
	        rightBoardNode  = Finder.find(rightBoard, ShapeNode.class, "Plane01_Shape");
	        bottomBoardNode = Finder.find(bottomBoard, ShapeNode.class, "Plane01_Shape");
			
	        hull.setTransform(Transform.scale(1.4f, 1.4f, 6.5f).mul(Transform.translate(0, 1f, 0)));
			cockpit.setTransform(Transform.scale(0.4f, 0.5f, 1f));
			gasMeter.setTransform(Transform.translate(0,gasMeterHeight/2,0).mul(Transform.scale(gasMeterHeight/22, gasMeterHeight/2, gasMeterHeight/42)));
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		GroupNode xformN 	= new GroupNode();
		GroupNode sizeN 	= new GroupNode();
	    gasNode 			= new GroupNode();
	    loadNode 			= new GroupNode();
	    boardNode 			= new GroupNode();
		
		gasNode.setTransform(Transform.translate(-0.15f, -0.11f, -0.2f));
		gasNode.addChildNode(gasMeter);
		
		loadNode.setTransform(Transform.translate(0.15f, -0.11f, -0.2f));
		loadNode.addChildNode(gasMeter);

		boardNode.setTransform(Transform.translate(0f, -0.14f, -0.21f));
		boardNode.addChildNode(leftBoardNode);
		boardNode.addChildNode(rightBoardNode);
		boardNode.addChildNode(bottomBoardNode);
		
		leftBoardNode.setTransform(Transform.translate(-0.17f,gasMeterHeight/2,0).mul(Transform.scale(0.12f, 0.12f, 0.12f).mul(Transform.rotate(0,0,1,(float) (Math.PI/4)))));
		rightBoardNode.setTransform(Transform.translate( 0.17f,gasMeterHeight/2,0).mul(Transform.scale(0.12f, 0.12f, 0.12f).mul(Transform.rotate(0,0,1,(float) (-Math.PI/4)))));
		bottomBoardNode.setTransform(Transform.translate( 0,-0.012f,-0.01f).mul(Transform.scale(0.4f, 0.1f, 0.12f)));
		
		node.addChildNode(xformN);
		xformN.addChildNode(sizeN);
		sizeN.addChildNode(hull);
		sizeN.addChildNode(cockpit);
		sizeN.addChildNode(wing);
		sizeN.addChildNode(wing2);

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

		Texture2D sidePanels = null;
		Texture2D bottomPanel = null;
		
		try {
			
			sidePanels = new Texture2D(Helper.getFileResource("textures/panel.jpg"));
			bottomPanel = new Texture2D(Helper.getFileResource("textures/bottom.jpg"));
		        
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
        boardMat.setTexture("AMBIENT", "jvr_Texture0", sidePanels);    
        boardMat.setShaderProgram("AMBIENT", ambientProgram);	        
        boardMat.setTexture("LIGHTING", "jvr_Texture0", sidePanels);    
        boardMat.setShaderProgram("LIGHTING", lightingProgram);
        
        ShaderMaterial bottomMat = new ShaderMaterial();
        bottomMat.setTexture("AMBIENT", "jvr_Texture0", bottomPanel);    
        bottomMat.setShaderProgram("AMBIENT", ambientProgram);	        
        bottomMat.setTexture("LIGHTING", "jvr_Texture0", bottomPanel);    
        bottomMat.setShaderProgram("LIGHTING", lightingProgram);
        
        leftBoardNode.setMaterial(boardMat);
        rightBoardNode.setMaterial(boardMat);
        bottomBoardNode.setMaterial(bottomMat);
        
	}

	public void updateState(float gas, float load) {
		
		gasNode.setTransform(gasNode.getTransform().mul(Transform.rotateZ(-gasRot)));
		gasRot = (float) (Math.PI+(gas/25f*Math.PI));
		gasNode.setTransform(gasNode.getTransform().mul(Transform.rotateZ(gasRot)));
		
		loadNode.setTransform(loadNode.getTransform().mul(Transform.rotateZ(-loadRot)));
		loadRot = (float) (Math.PI-(load/15f*Math.PI));
		loadNode.setTransform(loadNode.getTransform().mul(Transform.rotateZ(loadRot)));
		
		
		
		wing.setTransform(Transform.translate(0, 1.4f, 3.0f).mul(Transform.scale(0.01f,1.5f,1f)));
		wing2.setTransform(Transform.translate(0, 1.4f, 3.0f).mul(Transform.scale(1.5f,0.01f,1f)));
		hull.setTransform(Transform.scale(2f,2f,6.5f).mul(Transform.translate(0, 0.65f, 0)));
	}
	
}
