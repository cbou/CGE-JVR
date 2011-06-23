uniform mat4 jvr_ProjectionMatrix;

float hs = 0.05;

varying in float partEnergyV[];
varying out float partEnergyG;

void quadVertex(float dx, float dy) {
	gl_Position = jvr_ProjectionMatrix * (gl_PositionIn[0] + vec4(dx, dy, 0, 0));
	partEnergyG = partEnergyV[0];
	EmitVertex();    
}

void main(void)
{
	if (partEnergyV[0] > 0.01) {
		quadVertex(-hs, -hs);
		quadVertex( hs, -hs);
		quadVertex(-hs,  hs);
		quadVertex( hs,  hs);
  	EndPrimitive();
	}
}