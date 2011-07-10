uniform sampler2D jvr_SzeneZ;
uniform sampler2D jvr_Texture1;
uniform sampler2D jvr_ParticleZ;
uniform float intensity;
uniform float dofIntensity;

varying vec2 texture_coordinate;

float linearizeDepth(sampler2D buffer)
{
  float n = 0.1;
  float f = 10.0;
  float z = texture2D(buffer, texture_coordinate).r;
  return (2.0 * n) / (f + n - z * (f - n));
}

vec4 blur()
{   	
   	int iteration = 4;
   	
   	vec4 final_color = vec4(0,0,0,1);
   	float particleZ  = texture2D(jvr_ParticleZ, texture_coordinate).r;
   	float szeneZ     = texture2D(jvr_SzeneZ, texture_coordinate).r;
   		
   	for(int x=-iteration/2; x<iteration/2+1; x++)
   	{
   		for(int y=-iteration/2; y<iteration/2+1; y++)
   		{
   			vec2 texC = texture_coordinate;
   			vec2 offset = vec2(0,0);
   			
   			if(!(texC.x>1.0 || texC.x<0.0 || texC.y>1.0 || texC.y<0.0))
   			{
	   			/* Only blur if the particle is visible */
	   			if ((particleZ <= szeneZ) && (szeneZ<1.0))
	   			{
	   				offset = intensity *  vec2(float(x)/1024.0, float(y)/1024.0);
	   			} else {
	   			    offset = dofIntensity*vec2(float(x)/1024.0, float(y)/1024.0)*linearizeDepth(jvr_SzeneZ);
	   			}
	 			texC = texture_coordinate + offset;
   			}
   			final_color += texture2D(jvr_Texture1, texC);
   			
   			/*Debug*/
   			//if ((particleZ <= szeneZ) && (szeneZ<1.0))final_color = vec4(0,1,0,1);
   		}
   	}

	final_color /= float(iteration + 1) * float(iteration + 1); 
   	final_color.w = 1.0;
   	
   	/* Fog */
	//final_color += linearizeDepth(jvr_SzeneZ) * vec4(1,1,1,1) * 0.2;
	return final_color;
}

void main (void)
{
	gl_FragColor = blur();
}
