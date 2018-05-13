attribute vec3 a_position;
attribute vec3 a_normal;

uniform mat4 u_projMatrix;
uniform vec3 u_lightPosition;

varying vec3 v_normal;
varying float distanceToLight;
varying vec3 v_vecToLight;

void main(){
	v_normal = a_normal;
	v_vecToLight = normalize(u_lightPosition - a_position);
	distanceToLight = distance(a_position, u_lightPosition);
	gl_Position = u_projMatrix * vec4(a_position.xyz, 1.0f);
}
