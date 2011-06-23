attribute vec4 jvr_Vertex;
attribute vec2 jvr_TexCoord;

uniform mat4 jvr_ModelViewProjectionMatrix;

varying vec2 texture_coordinate;

void main(void)
{
  gl_Position = jvr_ModelViewProjectionMatrix * jvr_Vertex;
  texture_coordinate = jvr_TexCoord;
}