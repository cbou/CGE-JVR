uniform sampler2D jvr_SzeneZ;
uniform sampler2D jvr_Texture1;
uniform sampler2D jvr_ParticleZ;
uniform float intensity;
uniform float dofIntensity;

varying vec2 texCoord;

float linearizeDepth(sampler2D buffer)
{
  float n = 0.1;
  float f = 10.0;
  float z = texture2D(buffer, texCoord).r;
  return (2.0 * n) / (f + n - z * (f - n));
}

vec4 blur()
{   	
   	int iteration = 4;
   	
   	vec4 final_color = vec4(0,0,0,1);
   	
   	for(int x=-iteration/2; x<iteration/2+1; x++)
   	{
   		for(int y=-iteration/2; y<iteration/2+1; y++)
   		{
   			//vec4 originalColor = texture2D(jvr_Texture1, texCoord);
   			
   			float particleZ    = texture2D(jvr_ParticleZ, texCoord).r;
   			float szeneZ       = texture2D(jvr_SzeneZ, texCoord).r;
   			
   			vec2 offset = intensity *  vec2(float(x)/1024.0, float(y)/1024.0);
   			vec2 texC = texCoord;
   			
   			if(!(texC.x>1.0 || texC.x<0.0 || texC.y>1.0 || texC.y<0.0))
   			{
	   			//if((originalColor.r >= 0.9) && (originalColor.g <= 0.5) && (originalColor.b <= 0.5))
	   			
	   			/* Only blur if the particle is visible */
	   			if ((particleZ <= szeneZ) && (szeneZ<1.0))
	   			{
	   				texC = texCoord+offset;
	   			} else {
	   				texC = texCoord+ (dofIntensity*vec2(float(x)/1024.0, float(y)/1024.0)*linearizeDepth(jvr_SzeneZ));
	 			}
	 			
	 			/* Fog */
	 			//final_color += linearizeDepth(jvr_SzeneZ) * vec4(1,1,1,0.2) * 0.2;
	 			
   			}
   			final_color += texture2D(jvr_Texture1, texC);
   			
   			/*Debug*/
   			//if ((particleZ <= szeneZ) && (szeneZ<1.0))final_color = vec4(0,1,0,1);
   		}
   	}

	final_color /= float(iteration + 1) * float(iteration + 1); 
   	final_color.w = 1.0;
	return final_color;
}


void main (void)
{
	gl_FragColor = blur();
}
