package water;

import java.util.jar.Attributes;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;

public class MainTextures implements ApplicationListener{
	
	private Mesh waterMesh;
	private ShaderProgram waterShaderProgram;
	private OrthographicCamera camera;
	
	private Texture waterTexture;
	private Texture heightMapTexture;
	
	long tick = 0;
	@Override
	public void create() {
		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		//init waterShaderProgram
		this.waterShaderProgram = new ShaderProgram(Gdx.files.local("assets/waterShader/vertexShader.glsl"), Gdx.files.local("assets/waterShader/fragmentShader.glsl"));
		waterShaderProgram.pedantic = false;
		waterMesh = new Mesh(false, 9, 9, new VertexAttributes(new VertexAttribute(Usage.Position, 3, ShaderProgram.POSITION_ATTRIBUTE), new VertexAttribute(Usage.TextureCoordinates, 2, ShaderProgram.TEXCOORD_ATTRIBUTE)));
		float[] waterVertices = new float[]{ 
				0,0,0, 0,0,
				300,0,0, 1,0,
				300,300,0, 1,1,
				0,0,0, 0,0,
				300,300,0, 1,1,
				0,300,0, 0,1,
		};short[] indices = new short[] {
				0,1,2, 3,4,5	
			};
		waterMesh.setVertices(waterVertices);
		waterMesh.setIndices(indices);
		
		waterTexture = new Texture(Gdx.files.local("assets/background.png"));
		heightMapTexture = new Texture(Gdx.files.local("assets/waterdisplacement.jpg"));
	}

	@Override
	public void resize(int width, int height) {
		
	}

	@Override
	public void render() {
		
		Gdx.gl.glClearColor(255, 255f, 255f, 255f);
		Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);
		Gdx.gl20.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		Gdx.gl20.glEnable(GL20.GL_BLEND);
		waterTexture.bind(1);
		heightMapTexture.bind(2);
		waterShaderProgram.begin();	
		if(!waterShaderProgram.isCompiled())
			System.err.println(waterShaderProgram.getLog());
		waterShaderProgram.setUniformMatrix("u_projMatrix", camera.combined);
		waterShaderProgram.setUniformi("u_waterImage", 1);
		waterShaderProgram.setUniformi("u_heightMap", 2);
		waterShaderProgram.setUniformf("timeDelta", tick);
		waterMesh.render(waterShaderProgram, GL20.GL_TRIANGLES, 0, 6);
		waterShaderProgram.end();
		tick++;
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		waterShaderProgram.dispose();
		waterMesh.dispose();
	}

}
