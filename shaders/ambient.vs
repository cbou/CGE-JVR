attribute vec4 jvr_Vertex;

uniform mat4 jvr_ModelViewProjectionMatrix;

varying vec4 position;

void main(void)
{
  gl_Position = jvr_ModelViewProjectionMatrix * jvr_Vertex;
  position = jvr_Vertex;
}