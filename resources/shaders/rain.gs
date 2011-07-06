uniform mat4 jvr_ProjectionMatrix;

float hs = 0.002;
float zFactor = 100;

void quadVertex(float dx, float dy) {
  gl_Position = jvr_ProjectionMatrix * (gl_PositionIn[0] + vec4(dx, dy, 0, 0));
  EmitVertex();    
}

void main(void)
{
  quadVertex(-hs, zFactor*-hs);
  quadVertex( hs, zFactor*-hs);
  quadVertex(-hs,  zFactor*hs);
  quadVertex( hs,  zFactor*hs);
  EndPrimitive();

}