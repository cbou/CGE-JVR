attribute vec4 jvr_Vertex;
attribute vec3 jvr_Normal;

uniform vec3 lightPosV;
uniform mat3 jvr_NormalMatrix;
uniform mat4 jvr_ModelViewMatrix;
uniform mat4 jvr_ModelViewProjectionMatrix;

varying vec3 normalV;
varying vec3 lightDirV;
varying vec3 eyeDirV;

void main(void)
{
	vec3 vertexV = (jvr_ModelViewMatrix * jvr_Vertex).xyz;
	eyeDirV = -vertexV;
	lightDirV = lightPosV - vertexV;
	normalV = normalize(jvr_NormalMatrix * jvr_Normal);

	gl_Position = jvr_ModelViewProjectionMatrix * jvr_Vertex;
}