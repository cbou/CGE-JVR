attribute vec4 jvr_Vertex;
uniform mat4 jvr_ModelViewProjectionMatrix;

void main(void)
{
  gl_Position = jvr_ModelViewProjectionMatrix * jvr_Vertex;
}