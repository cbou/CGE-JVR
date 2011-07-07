attribute vec4 jvr_Vertex;
attribute vec3 jvr_Normal;

uniform mat4 jvr_ModelViewProjectionMatrix;

varying vec4 position;
varying vec3 normal;

void main(void)
{
  gl_Position = jvr_ModelViewProjectionMatrix * jvr_Vertex;
  position = jvr_Vertex;
  normal = jvr_Normal;
}