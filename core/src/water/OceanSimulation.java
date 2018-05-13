package water;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;

import utils.InputHandler;
import utils.InputHandler.Attribute;

public class OceanSimulation implements ApplicationListener{

	private WaterMesh waterMesh;
	private final int MAP_SIZE = 200;
	
	private Vector3 lightPosition;
	
	private ShaderProgram waterShaderProgram;
	private PerspectiveCamera cam;
	private CameraInputController camController;
	
	private boolean paused = false;
	private int time = 0;
	
	@Override
	public void create() {
		cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		cam.position.set(0, 0, 4000);
		cam.lookAt(0, 0, 0);
		cam.near = 1f;
		cam.far = 100000f;
		cam.update();
		camController = new CameraInputController(cam);
		
		lightPosition = new Vector3(100, 100, 900);
		
		waterShaderProgram = new ShaderProgram(Gdx.files.local("assets/waterShader/vertexShader2.glsl"), Gdx.files.local("assets/waterShader/fragmentShader2.glsl"));
		
		InputMultiplexer inputMultiplexer = new InputMultiplexer(new InputHandler(this));
		inputMultiplexer.addProcessor(camController);
		Gdx.input.setInputProcessor(inputMultiplexer);
		
		waterMesh = new WaterMesh(MAP_SIZE);
		waterMesh.init();
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void render() {
		//System.out.println(Gdx.graphics.getFramesPerSecond());
		if(!paused) {
			time++;
			waterMesh.update(time);
		}
		
		Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT | GL30.GL_DEPTH_BUFFER_BIT);
		Gdx.gl20.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		Gdx.gl20.glEnable(GL20.GL_BLEND);
		
		//pointLight.setPosition((float) (10 * Math.sin(0.01*(tick))), 0, 10);
		//camController.update();
		
		
		waterShaderProgram.begin();
		if(!waterShaderProgram.isCompiled())
			System.err.println(waterShaderProgram.getLog());
		waterShaderProgram.setUniformMatrix("u_projMatrix", cam.combined);
		waterShaderProgram.setUniformf("u_lightPosition", lightPosition);
		waterMesh.render(waterShaderProgram, GL20.GL_TRIANGLES,  0,  (MAP_SIZE-1) * (MAP_SIZE-1) * 6);
		waterShaderProgram.end();
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
		// TODO Auto-generated method stub
		
	}
	
	public void togglePauseSimulation() {
		this.paused = !this.paused;
	}

	public void setAttribute(Attribute activeAttribute, float value) {
		switch(activeAttribute) {
		case WAVE_HEIGHT: this.waterMesh.setWaveHeight(value); break;
		case PERIOD: this.waterMesh.setPeriod(value); break;
		case SPREADINGRATE: this.waterMesh.setSpreadingrate(value/10); break;
		}
	}
}
