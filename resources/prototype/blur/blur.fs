uniform sampler2D jvr_Texture0;
uniform sampler2D jvr_Texture1;
uniform float intensity;
varying vec2 texCoord;


vec4 blur()
{   	
   	int iteration = 4;
   	
   	vec4 final_color = vec4(0,0,0,1);
   	
   	for(int x=-iteration/2; x<iteration/2+1; x++)
   	{
   		for(int y=-iteration/2; y<iteration/2+1; y++)
   		{
   			vec3 yellow = vec3(1,0,0);
   			vec4 originalColor = texture2D(jvr_Texture1, texCoord);
   		
   			float simil = dot(yellow, originalColor.rgb);
   			
   			vec2 offset = intensity *  vec2(float(x)/1024.0, float(y)/1024.0);
   			vec2 texC = texCoord;
   			
   			if(!(texC.x>1.0 || texC.x<0.0 || texC.y>1.0 || texC.y<0.0))
   			{
	   			if((originalColor.r >= 0.9) && (originalColor.g <= 0.5) && (originalColor.b <= 0.5))
	   			{
	   				texC = texCoord+offset;
	   			}
   			}
   			final_color += texture2D(jvr_Texture1, texC);
   			//final_color = (float(y)+2.0)* texture2D(jvr_Texture1, texCoord) + (float(iteration - y)+2.0) * texture2D(jvr_Texture1, texC);
   		}
   	}

	final_color /= float(iteration + 1) * float(iteration + 1); 
  // 	final_color = texture2D(jvr_Texture0, texCoord);
   	
   	final_color.w = 1.0;
	return final_color;
}


void main (void)
{
	gl_FragColor = blur();
}
