uniform mat4 jvr_ProjectionMatrix;

float hs = 0.005;

void quadVertex(float dx, float dy) {
  gl_Position = jvr_ProjectionMatrix * (gl_PositionIn[0] + vec4(dx, dy, 0, 0));
  EmitVertex();    
}

void main(void)
{
  quadVertex(-hs, 8.0*-hs);
  quadVertex( hs, 8.0*-hs);
  quadVertex(-hs,  8.0*hs);
  quadVertex( hs,  8.0*hs);
  EndPrimitive();

}