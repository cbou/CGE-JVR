uniform vec3 toonColor;
uniform sampler2D jvr_Texture0;
void main (void)
{

  vec2 text = vec2(1,1);
  gl_FragColor = texture2D(jvr_Texture0, text);
  //gl_FragColor.rgb = toonColor * 0.25;
  //gl_FragColor.r = jvr_Texture0; 
  //gl_FragColor.a = 1.0;
}
