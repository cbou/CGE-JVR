uniform vec3 toonColor;
uniform sampler2D jvr_Texture;
varying vec2 texture_coordinate;

void main (void)
{
  //gl_FragColor.rgb = toonColor * 0.25;
  gl_FragColor = texture2D(jvr_Texture, texture_coordinate);
  //gl_FragColor.a = 1.0;
}
