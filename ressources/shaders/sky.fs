uniform sampler2D jvr_Texture0;

varying vec2 texCoord;

void main (void)
{
	vec2 texC = texCoord;
	
	// remove border lines
	if(texC.x>0.999)texC.x = 0.999;
	if(texC.y>0.999)texC.y = 0.999;
	if(texC.x<0.001)texC.x = 0.001;
	if(texC.y<0.001)texC.y = 0.001;
	
	gl_FragColor = texture2D(jvr_Texture0, texCoord);
}
