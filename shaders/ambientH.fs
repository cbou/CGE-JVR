
varying vec3 normal;
varying vec4 position;

void main (void)
{
  vec4 P = position * 0.05;
  vec3 N = normal*0.5;
  gl_FragColor.r = P.y;
  gl_FragColor.g = P.y;
  gl_FragColor.b = P.y;
  gl_FragColor.a = 1.0;
}
