uniform vec3 toonColor;
uniform vec4 jvr_Global_Ambient;

void main (void)
{
  gl_FragColor.rgb = toonColor * jvr_Global_Ambient.rgb;
  gl_FragColor.a = 1.0;
}
