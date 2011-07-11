uniform sampler2D jvr_Texture0;

varying vec2 texture_coordinate;

void main (void)
{
  float gDiff = texture2D(jvr_Texture0, texture_coordinate).g - texture2D(jvr_Texture0, texture_coordinate).r;
  if (gDiff > 0.01 ){
    	gl_FragColor= 	texture2D(jvr_Texture0, texture_coordinate) * gDiff;
  } else {
  	    gl_FragColor= 	texture2D(jvr_Texture0, texture_coordinate) * 0.1;
  }
  gl_FragColor.a =  texture2D(jvr_Texture0, texture_coordinate).a;
}
