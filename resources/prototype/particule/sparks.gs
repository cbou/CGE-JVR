uniform mat4 jvr_ProjectionMatrix;

float hs = 0.025;

varying in float partRadiusV[];
varying out float partRadiusG;

void quadVertex(float dx, float dy) {
  gl_Position = jvr_ProjectionMatrix * (gl_PositionIn[0] + vec4(dx, dy, 0, 0));
  partRadiusG = partRadiusV[0];
  EmitVertex();    
}

void main(void)
{
  quadVertex(-hs, -hs);
  quadVertex( hs, -hs);
  quadVertex(-hs,  hs);
  quadVertex( hs,  hs);
  EndPrimitive();

}