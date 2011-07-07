uniform vec3 toonColor;
uniform vec3 lightIntensity;

uniform vec3 ka, kd, ks;
uniform float ke;

varying vec3 normalV;
varying vec3 lightDirV;
varying vec3 eyeDirV;

void main (void)
{
	vec3 N = normalize(normalV);
	vec3 L = normalize(lightDirV);
	vec3 E = normalize(eyeDirV);
	
	/* diffuse intensity */
	float intensity = dot(L, N);
	//intensity = intensity > 0.1 ? 1.0 : 0.3;
	
	if (intensity < 0.1){
		intensity = 0.1;
	} else if (intensity < 0.2){
		intensity = 0.2;
	} else if (intensity < 0.5){
		intensity = 0.5;
	} else {
		intensity = 1.0;
	} 
	
	vec3 color = ka * lightIntensity;
	if (intensity > 0.0) {
		
		/* diffuse reflectance */
		color += kd * lightIntensity * intensity;
		
		/* specular highlight */
		vec3 R = reflect(-L, N);
		color += ks * lightIntensity * pow(max(dot(R, E), 0.0), ke );
		if (length(color) > 1.0) color = vec3(1.0,1.0,1.0);
	}
	gl_FragColor	=	vec4(color ,	1);	
}
