package water;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

/**
 * LibGDX port of ShaderLesson6, i.e. normal mapping in 2D games.
 * @author davedes
 */
public class MapTutorial implements ApplicationListener {
  
	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.width = 640;
		cfg.height = 480;
		cfg.resizable = false;
		new LwjglApplication(new MapTutorial(), cfg);
	}
	
	private Mesh mesh;
	
	private Mesh mesh2;
	private ShaderProgram shaderProgram;
	private OrthographicCamera cam;
	
	private final int NUM_VERTICES = 20;
	private int tick = 0;
	
	final String VERT =  
			"attribute vec4 "+ShaderProgram.POSITION_ATTRIBUTE+";\n" +
			"attribute vec4 "+ShaderProgram.COLOR_ATTRIBUTE+";\n" +
			"attribute vec2 "+ShaderProgram.TEXCOORD_ATTRIBUTE+"0;\n" +
			
			"uniform mat4 u_projTrans;\n" + 
			" \n" + 
			"varying vec4 vColor;\n" +
			"varying vec2 vTexCoord;\n" +
			"varying float zCoord;\n" +
			
			"void main() {\n" +  
			"	vColor = "+ShaderProgram.COLOR_ATTRIBUTE+";\n" +
			"	vTexCoord = "+ShaderProgram.TEXCOORD_ATTRIBUTE+"0;\n" +
			"   zCoord = "+ ShaderProgram.POSITION_ATTRIBUTE + ".z;\n" + 
			"	gl_Position =  u_projTrans * " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" +
			"}";
	
	//no changes except for LOWP for color values
	//we would store this in a file for increased readability
	final String FRAG = 
			//GL ES specific stuff
			  "#ifdef GL_ES\n" //
			+ "#define LOWP lowp\n" //
			+ "precision mediump float;\n" //
			+ "#else\n" //
			+ "#define LOWP \n" //
			+ "#endif\n" + //
			"//attributes from vertex shader\n" + 
			"varying LOWP vec4 vColor;\n" + 
			"varying vec2 vTexCoord;\n" + 
			"varying float zCoord;\n" +
			"\n" + 
			"//our texture samplers\n" + 
			"//uniform sampler2D u_texture;   //diffuse map\n" + 
			"//uniform sampler2D u_normals;   //normal map\n" + 
			"\n" + 
			"//values used for shading algorithm...\n" + 
			"//uniform vec2 Resolution;         //resolution of screen\n" + 
			"//uniform vec3 LightPos;           //light position, normalized\n" + 
			"//uniform LOWP vec4 LightColor;    //light RGBA -- alpha is intensity\n" + 
			"//uniform LOWP vec4 AmbientColor;  //ambient RGBA -- alpha is intensity \n" + 
			"//uniform vec3 Falloff;            //attenuation coefficients\n" + 
			"\n" + 
			"void main() {\n" + 
			"	//RGBA of our diffuse color\n" + 
			"	//vec4 DiffuseColor = texture2D(u_texture, vTexCoord);\n" + 
			"	\n" + 
			"	//RGB of our normal map\n" + 
			"	//vec3 NormalMap = texture2D(u_normals, vTexCoord).rgb;\n" + 
			"	\n" + 
			"	//The delta position of light\n" + 
			"	//vec3 LightDir = vec3(LightPos.xy - (gl_FragCoord.xy / Resolution.xy), LightPos.z);\n" + 
			"	\n" + 
			"	//Correct for aspect ratio\n" + 
			"	//LightDir.x *= Resolution.x / Resolution.y;\n" + 
			"	\n" + 
			"	//Determine distance (used for attenuation) BEFORE we normalize our LightDir\n" + 
			"	//float D = length(LightDir);\n" + 
			"	\n" + 
			"	//normalize our vectors\n" + 
			"	//vec3 N = normalize(NormalMap * 2.0 - 1.0);\n" + 
			"	//vec3 L = normalize(LightDir);\n" + 
			"	\n" + 
			"	//Pre-multiply light color with intensity\n" + 
			"	//Then perform \"N dot L\" to determine our diffuse term\n" + 
			"	//vec3 Diffuse = (LightColor.rgb * LightColor.a) * max(dot(N, L), 0.0);\n" + 
			"\n" + 
			"	//pre-multiply ambient color with intensity\n" + 
			"	//vec3 Ambient = AmbientColor.rgb * AmbientColor.a;\n" + 
			"	\n" + 
			"	//calculate attenuation\n" + 
			"	//float Attenuation = 1.0 / ( Falloff.x + (Falloff.y*D) + (Falloff.z*D*D) );\n" + 
			"	\n" + 
			"	//the calculation which brings it all together\n" + 
			"	//vec3 Intensity = Ambient + Diffuse * Attenuation;\n" + 
			"	//vec3 FinalColor = DiffuseColor.rgb * Intensity;\n" + 
		//vec4(float(gl_FragCoord/255), float(gl_FragCoord/255), zCoord*(-1), 1f);
			"	gl_FragColor = vec4(float(zCoord*(-.1)),float(zCoord*(-.01)), float(zCoord*(-1)), 1); //* vec4(FinalColor, DiffuseColor.a);\n" + 
			"}";
	
	@Override
	public void create() {
		shaderProgram = new ShaderProgram(VERT, FRAG);
		mesh = new Mesh(false, NUM_VERTICES, 30, new VertexAttribute(VertexAttributes.Usage.Position, 3, ShaderProgram.POSITION_ATTRIBUTE));
		float leftBot = (float) -(0.5*Math.sin(.005*(tick+1.321)) + .9);
		float leftTop = (float) -(0.5*Math.cos(.005*(tick-0.01)) + .8);
		float rightBot = (float) -(0.5*Math.sin(.005*(tick)) + .9);
		float rightTop = (float) -(0.5*Math.cos(.005*(tick+40)) + .9);
		mesh.setVertices(new float[] {
				0,0,leftBot,
				0, 200, leftTop,
				200, 0, rightBot,
				200,200, rightTop,
				
				400, 0, rightBot,
				400, 200, rightTop,
				
				600, 0, leftBot,
				600, 200, leftTop,
				
		});
		mesh.setIndices(new short[] {
				0,1,2, 1,2,3, 2,3,4, 3,4,5, 4,5,6, 5,6,7
		});
		
		mesh2 = new Mesh(false, NUM_VERTICES, 30, new VertexAttribute(VertexAttributes.Usage.Position, 3, ShaderProgram.POSITION_ATTRIBUTE));
		leftBot = (float) -(0.5*Math.cos(.005*(tick+1.321)) + .9);
		leftTop = (float) -(0.5*Math.sin(.005*(tick-0.01)) + .8);
		rightBot = (float) -(0.5*Math.cos(.005*(tick)) + .9);
		rightTop = (float) -(0.5*Math.sin(.005*(tick+40)) + .9);
		mesh2.setVertices(new float[] {
				0,0,leftBot,
				0, 200, leftTop,
				200, 0, rightBot,
				200,200, rightTop,
				
				400, 0, rightBot,
				400, 200, rightTop,
				
				600, 0, leftBot,
				600, 200, leftTop,
				
		});
		mesh2.setIndices(new short[] {
				0,1,2, 1,2,3, 2,3,4, 3,4,5, 4,5,6, 5,6,7
		});
		cam = new OrthographicCamera(Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
		cam.setToOrtho(false);
	}

	@Override
	public void resize(int width, int height) {
		
	}

	@Override
	public void render() {
		float leftBot = (float) -(0.5*Math.sin(.05*tick) + .9);
		float leftTop = (float) -(0.5*Math.cos(.05*(tick-0.01)) + .8);
		float rightBot = (float) -(0.5*Math.sin(.05*(tick)) + .9);
		float rightTop = (float) -(0.5*Math.cos(.05*(tick+40)) + .9);
		mesh.setVertices(new float[] 
				{
						0,0,leftBot,
						0, 200, leftTop,
						200, 0, rightBot,
						200,200, rightTop,
						
						300, 0, rightBot,
						300, 200, rightTop,
						
						600, 0, rightBot,
						600, 200, rightTop,
				}
		);
		
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glEnable(GL30.GL_BLEND);
		Gdx.gl.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);
		Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);
		shaderProgram.begin();
		if(!shaderProgram.isCompiled())
			System.out.println(shaderProgram.getLog());
		shaderProgram.setUniformMatrix("u_projTrans", cam.combined);
		mesh.render(shaderProgram, GL30.GL_TRIANGLES, 0, NUM_VERTICES);
		mesh2.render(shaderProgram, GL30.GL_TRIANGLES, 0, NUM_VERTICES);
		shaderProgram.end();
		tick++;
	}

	@Override
	public void pause() {
		
	}

	@Override
	public void resume() {
		
	}

	@Override
	public void dispose() {
		
	}	
}