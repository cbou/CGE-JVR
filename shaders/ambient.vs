attribute vec4 jvr_Vertex;
varying vec2 texture_coordinate;
uniform mat4 jvr_ModelViewProjectionMatrix;

void main(void)
{
  gl_Position = jvr_ModelViewProjectionMatrix * jvr_Vertex;
  	texture_coordinate = vec2(gl_MultiTexCoord0);
}