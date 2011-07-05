uniform vec3 toonColor;
uniform sampler2D jvr_Texture0;
varying vec2 texture_coordinate;

void main (void)
{
  gl_FragColor.rgb = vec3(1,1,1) * 0.0;
  gl_FragColor.a = 1.0;
}
