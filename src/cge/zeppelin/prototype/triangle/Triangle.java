package cge.zeppelin.prototype.triangle;

import de.bht.jvr.core.ShapeNode;
import de.bht.jvr.core.TriangleMesh;

public class Triangle extends ShapeNode{

	TriangleMesh vertices;
	
	public Triangle(float w, float h){
		try {
			float[] positions = new float[]{0,0,0, 0,h,0, w,h,0};
			float[] normals  = new float[]{0,0,1 ,0,0,1, 0,0,1};
			float[] textures = new float[]{0,0, 1,1, 0,1};
			vertices = new TriangleMesh(new int[]{1,2,3},positions,normals,textures,null, null);
			this.setGeometry(vertices);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
