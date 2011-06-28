uniform vec3 toonColor;

uniform vec4 jvr_LightSource_Diffuse;
uniform vec4 jvr_LightSource_Specular;

varying vec3 normalV;
varying vec3 lightDirV;
varying vec3 eyeDirV;

void main (void)
{
  vec3 color = vec3(0, 0, 0);
  
  vec3 N = normalize(normalV);
  vec3 L = normalize(lightDirV);
  vec3 E = normalize(eyeDirV);
  
  /* diffuse intensity */
  float intensity = dot(L, N);

  if (intensity > 0.9)
    color = 0.75 * toonColor * jvr_LightSource_Diffuse.rgb;
  else if (intensity > 0.6)
    color = 0.50 * toonColor * jvr_LightSource_Diffuse.rgb;
  else if (intensity > 0.3)
    color = 0.25 * toonColor * jvr_LightSource_Diffuse.rgb;
  
  /* specular highlight */
  if (intensity > 0.0) {
    vec3 R = reflect(-L, N);
    float specular = max(dot(R, E), 0.0);
    if (specular > 0.99)
      color = jvr_LightSource_Specular.rgb;
  }
  
  //gl_FragColor.rgb = vec3(1,1,0);
  gl_FragColor.rgb = color;
  gl_FragColor.a = 1.0;
}
