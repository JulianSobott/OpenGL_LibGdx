attribute vec3 a_position;
attribute vec2 a_texCoord;
uniform mat4 u_projMatrix;
varying vec4 vColor;
varying vec2 v_texCoords;



void main(){
	v_texCoords = a_texCoord;
	gl_Position = u_projMatrix * vec4(a_position.xyz, 1.0f);
	vColor = vec4(100.0f, 100.0f, 100.0f, 255.0f);
}
