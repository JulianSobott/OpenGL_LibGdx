//Our Attributes
attribute vec3 a_position;
attribute vec4 a_color;

//our camera matrix
uniform mat4 u_projTrans;
//uniform mat4 u_rotationMatrix;
//uniform mat4 u_moveMatrix;

//send the color out to the fragment shader
varying vec4 vColor;

void main() {
    float distanceFactor = distance(a_position.x, 100)/(100.0f*sqrt(2));
    vec3 color = vec3(distanceFactor*.1f,distanceFactor* .1f, 255.0f);
	vColor = vec4(color, 1.0f);
    //gl_Position = u_moveMatrix * u_rotationMatrix * u_projTrans * vec4(a_position.xyz, 1.0f);
gl_Position = u_projTrans * vec4(a_position.xyz, 1.0f);

}