uniform vec3 toonColor;

void main (void)
{
  gl_FragColor.rgb = toonColor * 0.25;
  gl_FragColor.a = 1.0;
}
