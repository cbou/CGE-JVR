uniform vec3 toonColor;

varying vec4 position;

void main (void)
{
  vec4 P = normalize(position);
	
  gl_FragColor.r = P.y * 0.7;
  gl_FragColor.g = P.y * 0.7;
  gl_FragColor.b = P.y * 0.7;
  gl_FragColor.a = 1.0;
}
