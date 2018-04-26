attribute vec3 a_position;

uniform mat4 u_projMatrix;
varying vec4 vColor;



void main(){
	gl_Position = u_projMatrix * vec4(a_position.xyz, 1.0f);
	vColor = vec4(100.0f, 100.0f, 100.0f, 255.0f);
}
