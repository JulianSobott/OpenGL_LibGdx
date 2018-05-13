package water;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;


public class WaveSimulation implements ApplicationListener {
	
	private PerspectiveCamera cam;

	private CameraInputController camController;
	private ShaderProgram waterShaderProgram;
	
	private Mesh waterMesh;
	private short[] indices;
	private float[] vertices;
	private float[] waterMap1;
	
	
	
	final int MAP_SIZE = 100;
	final int NUM_ROWS = MAP_SIZE;
	final int NUM_COLS = MAP_SIZE;
	final int TRI_WIDTH = 35;
	int numTris;
	
	float[][] v = new float[NUM_COLS][NUM_ROWS];
	float[][] u = new float[NUM_COLS][NUM_ROWS];
	
	private final float WAVE_HEIGHT = 10;
	
	private Vector3 lightPosition;
	
	private boolean paused = true;
	
	private List<Vector2> waveCenters = new ArrayList<Vector2>();
	
	//DEBUGGING
	public Model axesModel;
	boolean renderNormals = false;
	public ModelInstance axesInstance;
	public Array<ModelInstance> instances = new Array<ModelInstance>();
	public ModelBatch modelBatch;
	
	@Override
	public void create() {
		numTris = (NUM_COLS-1) * (NUM_ROWS-1) * 2;
		waveCenters.add(new Vector2(10, 10));
		cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		cam.position.set(0, 0, 3000);
		cam.lookAt(0, 0, 0);
		cam.near = 1f;
		cam.far = 10000f;
		cam.update();
		camController = new CameraInputController(cam);
		
		lightPosition = new Vector3(100, 100, 900);
		
		waterShaderProgram = new ShaderProgram(Gdx.files.local("assets/waterShader/vertexShader2.glsl"), Gdx.files.local("assets/waterShader/fragmentShader2.glsl"));
		//create water mesh
		vertices = new float[NUM_ROWS * NUM_COLS * 3 * 2]; //*2 for normals
		indices = new short[numTris*3]; //3 vertices and 2 tris for quad
		waterMap1 = new float[NUM_COLS * NUM_ROWS];
		calculateHeightMap();
		positionGridVertices();
		createGridIndices();
		calculateNormals();
		waterMesh = new Mesh(false, vertices.length, indices.length, new VertexAttributes(new VertexAttribute(VertexAttributes.Usage.Position, 3, ShaderProgram.POSITION_ATTRIBUTE), new VertexAttribute(Usage.Normal,  3, ShaderProgram.NORMAL_ATTRIBUTE)));
		waterMesh.setIndices(indices);
		waterMesh.setVertices(vertices);
		
		InputMultiplexer inputMultiplexer = new InputMultiplexer(new InputProcessor() {
			
			@Override
			public boolean touchUp(int screenX, int screenY, int pointer, int button) {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public boolean touchDragged(int screenX, int screenY, int pointer) {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public boolean touchDown(int screenX, int screenY, int pointer, int button) {
				screenY = Gdx.graphics.getHeight() - screenY;
				System.out.println(new Vector2(screenY/5, screenX/5));
				waveCenters.add(new Vector2(screenY/5, screenX/5));
				tick = 0;
				return false;
			}
			
			@Override
			public boolean scrolled(int amount) {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public boolean mouseMoved(int screenX, int screenY) {
				if(Gdx.input.isKeyPressed(Keys.B)) {
					screenY = Gdx.graphics.getHeight() - screenY;
					waveCenters.add(new Vector2(screenY/5, screenX/5));
					tick = 0;
				}
				return false;
			}
			
			@Override
			public boolean keyUp(int keycode) {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public boolean keyTyped(char character) {
				if(character == 'c')
					waveCenters.clear();
				return false;
			}
			
			@Override
			public boolean keyDown(int keycode) {
				if(keycode == Keys.SPACE) {
					paused = !paused;
					return true;
				}
				return false;
			}
		});
		inputMultiplexer.addProcessor(camController);
		Gdx.input.setInputProcessor(inputMultiplexer);
		
		
		createAxes();
		modelBatch = new ModelBatch();
	}	
		
		
	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}
	
	
	int tick = 0;
	@Override
	public void render() {
		if(!paused) {
			tick++;
			calculateHeightMap();
			positionGridVertices();
			calculateNormals();	
			waterMesh.setVertices(vertices);
			if(renderNormals) createAxes();	
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
		waterMesh.render(waterShaderProgram, GL20.GL_TRIANGLES,  0, indices.length);
		waterShaderProgram.end();
		
		if(renderNormals) {
			modelBatch.begin(cam);
			modelBatch.render(axesInstance);
			modelBatch.end();
		}
		
	
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
	
	final float GRID_MIN = -100f;
	final float GRID_MAX = 100f;
	final float GRID_STEP = 10f;

	private void createAxes () {
		ModelBuilder modelBuilder = new ModelBuilder();
		modelBuilder.begin();
		MeshPartBuilder builder = modelBuilder.part("grid", GL20.GL_LINES, Usage.Position | Usage.ColorUnpacked, new Material());
		builder.setColor(Color.LIGHT_GRAY);
		for (int i = 0; i < NUM_ROWS*NUM_COLS; i++) {		
			Vector3 point = new Vector3(vertices[i * 6 + 0], vertices[i * 6 + 1], vertices[i * 6 + 2]);
			Vector3 p2 = new Vector3(point).add(new Vector3(vertices[i * 6 + 3], vertices[i * 6 + 4], vertices[i * 6 + 5]).scl(100));
			builder.line(point, p2);
		}
		axesModel = modelBuilder.end();
		axesInstance = new ModelInstance(axesModel);
}
	
	
	private void createGridIndices() {
		int idxVertex = 0;
		for(int tri = 0; tri < numTris*3; tri++) {
			if((idxVertex+1) % NUM_COLS != 0 || idxVertex == 0) {
				int i = 0;
				
				indices[tri + i++] = (short) (idxVertex+1 + NUM_COLS);
				indices[tri + i++] = (short) (idxVertex+1 + NUM_COLS - 1);
				indices[tri + i++] = (short) (idxVertex+1);
				
				
				
				indices[tri + i++] = (short) idxVertex;
				indices[tri + i++] = (short) (idxVertex + 1);
				indices[tri + i++] = (short) (idxVertex + NUM_COLS);
				
				
				
				idxVertex++;
				tri+=5;
			}else {
				idxVertex++;
				tri--;
			}
			
		}
	}
	
	private void positionGridVertices() {
		int i = 0;
		int vertex = 0;
		float xStart = -(NUM_COLS/2*TRI_WIDTH);
		//xStart = -0;
		float yStart = -(NUM_ROWS/2*TRI_WIDTH);
		//yStart = -0;
		for(int row = 0; row< NUM_COLS; row++) {
			for(int col = 0; col < NUM_ROWS; col++) {
				int coordinate = 0; 
				float x = xStart + TRI_WIDTH * col;
				float y = yStart + TRI_WIDTH * row;
				float z = waterMap1[vertex];//(float) (waterMap1[vertex] * multiplyer + waterMap2[vertex]);// * (1-multiplyer) + Math.random());				
				vertices[i + coordinate++] = x;
				vertices[i + coordinate++] = y;
				vertices[i + coordinate++] = z;
				//skip for normals
				coordinate += 3;
				i += coordinate;
				vertex++;
			}
		}
	}
	
	private void calculateNormals() {
		//calculate face normals
		Vector3[] faceNormals = new Vector3[numTris];
		int face = 0;
		Vector3 orientationPoint = new Vector3(0, 0, 4000);
		for(int row = 0; row < NUM_ROWS-1; row++) {
			for(int col = 0; col < NUM_COLS-1; col++) { 
				int leftBot = row * NUM_COLS + col;
				int leftTop = row * NUM_COLS + col + NUM_COLS;
				int rightBot = row * NUM_COLS + col + 1;
				int rightTop = row * NUM_COLS + col + NUM_COLS + 1;
				
				Vector3 vec1 = getVertexPosition(leftTop, vertices).sub(getVertexPosition(leftBot, vertices));
				Vector3 vec2 = getVertexPosition(rightBot , vertices).sub(getVertexPosition(leftBot, vertices));
				faceNormals[face] = vec1.crs(vec2);
				faceNormals[face].nor();
				
				if(faceNormals[face].dot(new Vector3(new Vector3(orientationPoint).sub(getVertexPosition(leftBot, vertices)))) < 0) {
					faceNormals[face].scl(-1, -1, -1);
				}
				face++;
				vec1 = getVertexPosition(leftTop, vertices).sub(getVertexPosition(rightTop, vertices));
				vec2 = getVertexPosition(rightBot, vertices).sub(getVertexPosition(rightTop, vertices));
				faceNormals[face] = vec2.crs(vec1);
				faceNormals[face].nor();
				
				if(faceNormals[face].dot(new Vector3(new Vector3(orientationPoint).sub(getVertexPosition(rightTop, vertices)))) < 0) {
					faceNormals[face].scl(-1, -1, -1);
				}
				face++;
			}
		}
		//calculate vertex normals
		Vector3 vertexNormal = new Vector3();
		int innerVert = 0;
		int bottomVert = 0;
		for(int idxPosition = 0; idxPosition < vertices.length/6; idxPosition++) {
			//calculate surrounding faces

			List<Integer> surroundingFaces  = new ArrayList<Integer>();
			//checking edges
			if((float)idxPosition / NUM_COLS < 1 || idxPosition == 0) { //Bottom edge
				if((float)idxPosition % NUM_COLS == 0) { //bottom Left corner
					surroundingFaces.add(0);
				}else if(((float)idxPosition+1)% NUM_COLS == 0) {//bottom right corner
					surroundingFaces.add(idxPosition - 1 + bottomVert);
					surroundingFaces.add(idxPosition + bottomVert);
				}else {
					surroundingFaces.add(idxPosition - 1 + bottomVert);
					surroundingFaces.add(idxPosition + bottomVert);
					surroundingFaces.add(idxPosition + 1 + bottomVert);
					bottomVert++;
				}
			}else if((float)idxPosition % NUM_COLS == 0 && idxPosition != 0 && NUM_COLS * NUM_ROWS - idxPosition > NUM_COLS) { //Left edge
				surroundingFaces.add((idxPosition/NUM_COLS - 1) * (NUM_COLS-1)*2);
				surroundingFaces.add((idxPosition/NUM_COLS - 1) * (NUM_COLS-1)*2 + 1);
				surroundingFaces.add((idxPosition/NUM_COLS) * (NUM_COLS-1)*2);
			}else if(((float)idxPosition+1)% NUM_COLS == 0 && NUM_COLS * NUM_ROWS - idxPosition > NUM_COLS) { //right edge
				surroundingFaces.add(((idxPosition+1)/NUM_COLS - 1) * (NUM_COLS-1)*2 - 1);				
				surroundingFaces.add(((idxPosition+1)/NUM_COLS - 1) * (NUM_COLS-1)*2 - 1 + (NUM_COLS-1)*2-1);
				surroundingFaces.add(((idxPosition+1)/NUM_COLS - 1) * (NUM_COLS-1)*2 + (NUM_COLS-1)*2-1);
			}else if(NUM_COLS * NUM_ROWS - idxPosition <= NUM_COLS) { //top edge
				if((float)idxPosition % NUM_COLS == 0) { //Top left corner
					surroundingFaces.add((idxPosition/NUM_COLS - 1) * (NUM_COLS-1)*2);
					surroundingFaces.add((idxPosition/NUM_COLS - 1) * (NUM_COLS-1)*2 + 1);
				}else if(((float)idxPosition+1)% NUM_COLS == 0) { //top right corner
					surroundingFaces.add(((idxPosition+1)/NUM_COLS - 1) * (NUM_COLS-1)*2 - 1);
				}else {
					//TODO add this INDEZEs
					int row = (int) idxPosition / NUM_COLS;
					int col = idxPosition - row * NUM_COLS;
					surroundingFaces.add((row-1) * (NUM_COLS-1)*2 + col);
					surroundingFaces.add((row-1) * (NUM_COLS-1)*2 + col + 1);
					surroundingFaces.add((row-1) * (NUM_COLS-1)*2 + col + 2);
				}
			}else {
				//non edges
				surroundingFaces.add(idxPosition - NUM_COLS + innerVert);
				surroundingFaces.add(idxPosition - NUM_COLS + innerVert + 1);
				surroundingFaces.add(idxPosition - NUM_COLS + innerVert + 2);
				
				surroundingFaces.add(idxPosition - NUM_COLS - 1 + (NUM_COLS-1)*2 + innerVert);
				surroundingFaces.add(idxPosition - NUM_COLS - 1 + (NUM_COLS-1)*2 + innerVert + 1);
				surroundingFaces.add(idxPosition - NUM_COLS - 1 + (NUM_COLS-1)*2 + innerVert + 2);
				innerVert++;
			}
			
			
			for(int idxFace : surroundingFaces) {
				vertexNormal.add(faceNormals[idxFace]);
			}
			vertexNormal.nor();
			
			vertices[idxPosition * 6 + 3] = vertexNormal.x;
			vertices[idxPosition * 6 + 1 + 3] = vertexNormal.y;
			vertices[idxPosition * 6 + 2 + 3] = vertexNormal.z;
		}
	}
	
	private Vector3 getVertexPosition(int index, float[] vertices) {
		float x = vertices[index * 6]; //6 for 3 position and 3 normals
		float y = vertices[index * 6 + 1];
		float z = vertices[index * 6 + 2];
		return new Vector3(x, y, z);
	}
	
	//Different approach
	private void calculateHeightMap() {
		final int X_LENGTH = NUM_COLS;
		final int Y_LENGHTH = NUM_ROWS;
		
		float[][] heightMap = new float[X_LENGTH][Y_LENGHTH];
		int time = tick;
		for(int x = 0; x < X_LENGTH; x++) {
			for(int y = 0; y < Y_LENGHTH; y++) {
				heightMap[x][y] = calcHeight(x, y, time);
			}
		}
		
		int i = 0;
		for(float[] row : heightMap){
			for(float d : row){
				waterMap1[i] = d;
				//waterMap2[X_LENGTH*Y_LENGHTH-i - 1] = d;
				i++;
			}
		}
	}
	
	private float calcHeight(int x, int y, int time) {
		float height = 0;
		float PERIOD = 30;
		float SPREADING_RATE = 0.2f;
		float lamda = SPREADING_RATE * PERIOD;
		final int NUM_WAVES = 1;
		List<Vector2> newCenters = new ArrayList<Vector2>();
		for(int idxWave = 1; idxWave <= NUM_WAVES; idxWave++) {
			
			PERIOD = PERIOD/idxWave;
			lamda = SPREADING_RATE * PERIOD;
			//idxWave *= 2;
			
			for(Vector2 center : waveCenters) {
				float distToStart = new Vector2(center).dst(x, y);
				if(SPREADING_RATE * time > distToStart && SPREADING_RATE * time < distToStart + lamda && distToStart < MAP_SIZE * TRI_WIDTH) {
					height += (float) (WAVE_HEIGHT * Math.sin(2 * Math.PI * ((time)/PERIOD - distToStart/lamda)));
				}
					
					
				//height += (float) ((WAVE_HEIGHT/(Math.exp(idxWave)/2)) * Math.sin(2 * Math.PI * ((time)/PERIOD - distToStart/lamda)));
			}
			
			//height = (float) (WAVE_HEIGHT*Math.sin(Math.PI*2*(time/PERIOD - y/lamda)));
			//height = 10;
			//height += (float) ((WAVE_HEIGHT/(Math.exp(idxWave)/2)) * Math.sin(2 * Math.PI * ((time)/PERIOD - x/lamda)));
			//height -= (float) ((WAVE_HEIGHT/(Math.exp(idxWave)/4)) * Math.sin(2 * Math.PI * ((time+ Math.random())/PERIOD - y/lamda)));
			//height -= (float) ((WAVE_HEIGHT/(Math.exp(idxWave)/4)) * Math.sin(2 * Math.PI * ((time + Math.random()/2)/PERIOD - x/lamda)));
			//idxWave /= 2;
		}
		//height += (float) ((WAVE_HEIGHT/2) * Math.sin(2 * Math.PI * (time/PERIOD - x/lamda)));
		//height += (float) ((WAVE_HEIGHT/2) * Math.sin(2 * Math.PI * (time/PERIOD - y/lamda)));
		return height;
	}

}
