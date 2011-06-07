

varying vec4 position;

void main (void)
{
  vec4 P = normalize(position);
  P = position * 0.2;
  gl_FragColor.r = P.y;
  gl_FragColor.g = P.y;
  gl_FragColor.b = P.y;
  gl_FragColor.a = 1.0;
}
