uniform float waterLevel;
uniform float ambientFactor;

uniform vec4 jvr_LightSource_Diffuse;
uniform vec4 jvr_LightSource_Specular;
uniform sampler2D jvr_TextureHigh;
uniform sampler2D jvr_TextureMiddle;
uniform sampler2D jvr_TextureLow;

varying vec2 texture_coordinate;
varying vec3 normalV;
varying vec3 objNormalV;
varying vec3 lightDirV;
varying vec3 eyeDirV;
varying vec4 positionV;

float high 	 = 75.8;
float middle = 25.2;
float blend  = 25.7;
bool debug = false;

void main (void)
{
  vec3 color = vec3(0, 0, 0);
  float diffFact =  1.0/blend;
  
  vec3 N = normalize(normalV);
  vec3 L = normalize(lightDirV);
  vec3 E = normalize(eyeDirV);
  vec3 oN = normalize(objNormalV);
  
  /* diffuse intensity */
  float intensity = dot(L, N);
  
  /* Vary terrain borders dependent on normal */
  high 	 = high   + oN.x * 15.0;
  middle = middle + oN.z * 15.0;
  
  if (positionV.y > high)  {
	  	if (positionV.y < high + blend){
	  		float diff = (positionV.y - high)*diffFact;
	  		gl_FragColor = ( diff * texture2D(jvr_TextureHigh, texture_coordinate) + (1.0-diff) * texture2D(jvr_TextureMiddle, texture_coordinate));
	  	} else {
	  		gl_FragColor = texture2D(jvr_TextureHigh, texture_coordinate);
	  	}
  } else if (positionV.y > middle){
	   if (positionV.y < middle + blend){
	  		float diff = (positionV.y - middle)*diffFact;
	  		gl_FragColor = (diff * texture2D(jvr_TextureMiddle, texture_coordinate) +  (1.0-diff) * texture2D(jvr_TextureLow, texture_coordinate));
	    } else {
	  		gl_FragColor = texture2D(jvr_TextureMiddle, texture_coordinate);
	  	}
  } else {
   		gl_FragColor = texture2D(jvr_TextureLow, texture_coordinate);
  }
  
  gl_FragColor = ambientFactor*gl_FragColor + 0.3*intensity*gl_FragColor ;
 
  /* Normal Shader*/
  if (debug) gl_FragColor.rgb = objNormalV;
  
   /* Water */
  if (intensity > 0.0) {
    vec3 R = reflect(-L, N);
    float specular = max(dot(R, E), 0.0);
    if (positionV.y <= waterLevel){
       	gl_FragColor = gl_FragColor + specular * jvr_LightSource_Specular + vec4(0,0.1,0.5,0.5);
    }
  }
  
  gl_FragColor.a = 1.0;
}
