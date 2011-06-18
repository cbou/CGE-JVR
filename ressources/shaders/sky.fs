uniform sampler2D jvr_Texture0;

varying vec2 texture_coordinate;

void main (void)
{
  gl_FragColor = vec4(1.0,0.0,0.0,1.0);
  gl_FragColor = texture2D(jvr_Texture0, texture_coordinate);
}
