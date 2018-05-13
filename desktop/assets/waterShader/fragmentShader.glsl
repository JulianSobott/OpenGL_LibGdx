#ifdef GL_ES
precision mediump float;
#endif

varying vec4 color;
varying vec2 v_texCoords;

uniform sampler2D u_waterImage;
uniform sampler2D u_heightMap;
uniform float timeDelta;

void main(){
	float displacement = texture2D(u_heightMap, v_texCoords).s;
	float t=texture2D(u_heightMap, v_texCoords).s * sin((6.14/v_texCoords.x*60) * timeDelta) * 255f;
	t = 10+ 100*sin(2 * 3.142 * ((timeDelta + displacement)/500 - v_texCoords.x/500));
	t=  100f + 250f * sin((6/1) * timeDelta + displacement); 
	gl_FragColor = vec4(texture2D(u_heightMap, v_texCoords).s, 100f, 250f, 255f);
}
