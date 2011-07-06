uniform sampler2D image;

varying vec2 texCoord;
varying vec3 normalV;
varying vec3 lightDirV;
varying vec3 eyeDirV;

void main (void)
{

	vec3 lightIntensity = vec3(0.5,0.5,0.5);
	vec3 ka = vec3(0.0,0.0,0.0);
	vec3 kd = vec3(0.3,0.3,0.3);
	vec3 ks = vec3(0.2,0.2,0.2);
	
	float ke = 50.0;
	
	vec3 N = normalize(normalV);
	vec3 L = normalize(lightDirV);
	vec3 E = normalize(eyeDirV);
	
	/* diffuse intensity */
	float intensity = dot(L, N);
	vec3 color = vec3(texture2D(image, texCoord))*lightIntensity;
	//vec3 color = ka * lightIntensity;
	if (intensity > 0.0) {
		/* diffuse reflectance */
		color += kd * lightIntensity * intensity;
		/* specular highlight */
		vec3 R = reflect(-L, N);
		color += ks * lightIntensity * pow(max(dot(R, E), 0.0), ke );
	}
	gl_FragColor	=	vec4(color ,	1);
}
