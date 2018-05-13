#ifdef GL_ES
precision mediump float;
#endif

varying vec3 v_normal;
varying float distanceToLight;
varying vec3 v_vecToLight;

void main(){
	float cosTheta = clamp( dot(v_normal, v_vecToLight), 0,1);
	vec4 waterColor = vec4(30f/255f, 80f/255f, 190f/255f, 255f/255f);
	vec4 lightColor = vec4(250f/255f, 255f/255f, 250f/255f, 255f/255f);
	float lightPower = 800;
	float ambientLightIntensity = 0.2f;
	vec4 ambientLight = vec4(ambientLightIntensity, ambientLightIntensity, ambientLightIntensity, 1f);
	vec4 waterAmbientColor = waterColor * ambientLight;
	gl_FragColor = waterAmbientColor +
			 waterColor * lightColor * lightPower * cosTheta/(distanceToLight);
}
