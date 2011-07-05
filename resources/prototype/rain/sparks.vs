attribute vec3 partPosition;
attribute vec3 partVelocity;

uniform mat4 jvr_ModelViewMatrix;

void main(void)
{
  // transform particle position into viewer space
  gl_Position = jvr_ModelViewMatrix * vec4(partPosition, 1.0);
}