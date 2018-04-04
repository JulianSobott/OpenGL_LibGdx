//Our Attributes
attribute vec3 a_position;
attribute vec4 a_color;

//our camera matrix
uniform mat4 u_projTrans;

//send the color out to the fragment shader
varying vec4 vColor;

void main() {
    vColor = a_color;
    gl_Position = u_projTrans * vec4(a_position.xyz, 1.0f);
}