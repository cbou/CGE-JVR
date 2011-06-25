uniform vec3 toonColor;

uniform vec4 jvr_LightSource_Diffuse;
uniform vec4 jvr_LightSource_Specular;
uniform sampler2D jvr_TextureHigh;
uniform sampler2D jvr_TextureMiddle;
uniform sampler2D jvr_TextureLow;

varying vec2 texture_coordinate;
varying vec3 normalV;
varying vec3 lightDirV;
varying vec3 eyeDirV;
varying vec4 positionV;

float high 	 = 3.0;
float middle = 2.5;
float blend  = 1.0;

void main (void)
{

  vec3 color = vec3(0, 0, 0);
  
  vec3 N = normalize(normalV);
  vec3 L = normalize(lightDirV);
  vec3 E = normalize(eyeDirV);
  
  /* diffuse intensity */
  float intensity = dot(L, N);
	 
  /* specular highlight */
  if (intensity > 0.0) {
    vec3 R = reflect(-L, N);
    float specular = max(dot(R, E), 0.0);
    if (specular > 0.99)
      color = jvr_LightSource_Specular.rgb;
  }
  
  if (positionV.y > high)  {
  	if (positionV.y < high + blend){
  		float diff = positionV.y - high;
  		gl_FragColor = intensity * (diff * texture2D(jvr_TextureHigh, texture_coordinate) + (1.0-diff) * texture2D(jvr_TextureMiddle, texture_coordinate));
  	} else {
  		gl_FragColor = intensity * texture2D(jvr_TextureHigh, texture_coordinate);
  	}
  } else if (positionV.y > middle){
   	gl_FragColor = intensity * texture2D(jvr_TextureMiddle, texture_coordinate);
  } else {
   	gl_FragColor = intensity * texture2D(jvr_TextureLow, texture_coordinate);
  }
  
  gl_FragColor.a = 1.0;
}
