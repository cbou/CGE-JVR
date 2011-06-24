
attribute vec3 partPosition;
attribute vec3 partVelocity;

attribute float partRadius;
varying float partRadiusV;

uniform mat4 jvr_ModelViewMatrix;

void main(void)
{
  partRadiusV = partRadius;
  // transform particle position into viewer space
  gl_Position = jvr_ModelViewMatrix * vec4(partPosition, 1.0);
}