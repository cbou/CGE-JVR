uniform vec3 toonColor;
uniform sampler2D 0;
varying vec2 texture_coordinate;

void main (void)
{
  //gl_FragColor.rgb = toonColor * 0.25;
  gl_FragColor = texture2D(jvr_Texture0, texture_coordinate);
  //gl_FragColor.a = 1.0;
}
