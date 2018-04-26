package com.mygdx.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;


public class Test3d implements ApplicationListener {
	
	private PerspectiveCamera cam;
	private ModelBatch modelBatch;
	private Model model;
	private ModelInstance instance;
	private Environment environment;
	PointLight pointLight;
	private CameraInputController camController;
	
	private Mesh waterMesh;
	private short[] indices;
	private float[] vertices;
	private float[] waterMap1;
	private float[] waterMap2;
	
	final int NUM_ROWS = 100;
	final int NUM_COLS = 100;
	final int TRI_WIDTH = 1;
	int numTris;
	
	private final float WAVE_HEIGHT = .5f;
	
	@Override
	public void create() {
		numTris = (NUM_COLS-1) * (NUM_ROWS-1) * 2;
		
		modelBatch = new ModelBatch();
		cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		cam.position.set(0, -30, 30f);
		cam.lookAt(0, 0, 0);
		cam.near = 1f;
		cam.far = 300f;
		cam.update();
		camController = new CameraInputController(cam);
		Gdx.input.setInputProcessor(camController);

		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, .4f, .4f, .4f, 1f));
		environment.add(pointLight = new PointLight().set(.8f, .8f, .81f, -10f, 10f, 2f, 20f));
		environment.add(new DirectionalLight().set(0.4f, 0.4f, 0.4f, 0f, 0f, -2f));
		//create water mesh
		waterMap1 = new float[NUM_COLS * NUM_ROWS];
		waterMap2 = new float[NUM_COLS * NUM_ROWS];
		createWaterMap();
		vertices = createGridVertices();
		indices = createGridIndices(vertices);
		createNormals(indices, vertices);
		waterMesh = new Mesh(false, NUM_ROWS*NUM_COLS*3, numTris*3, new VertexAttributes(new VertexAttribute(VertexAttributes.Usage.Position, 3, ShaderProgram.POSITION_ATTRIBUTE), new VertexAttribute(Usage.Normal,  3, ShaderProgram.NORMAL_ATTRIBUTE)));
		waterMesh.setIndices(indices);
		waterMesh.setVertices(vertices);
		
		ModelBuilder modelBuilder = new ModelBuilder();
		modelBuilder.begin();
		modelBuilder.part("water", waterMesh, GL30.GL_TRIANGLES, new Material(ColorAttribute.createDiffuse(Color.BLUE), ColorAttribute.createSpecular(Color.YELLOW), ColorAttribute.createAmbient(Color.GREEN)));
		model = modelBuilder.end();
		//model = modelBuilder.createBox(5, 5, 5, new Material(ColorAttribute.createDiffuse(Color.RED)), Usage.Position | Usage.Normal);
		instance = new ModelInstance(model);			
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}
	int tick = 0;
	@Override
	public void render() {
		tick++;
		updateWaterMaps();
		float[] vertices = createGridVertices();
		createNormals(indices, vertices);	
		waterMesh.setVertices(vertices);
		
		pointLight.setPosition((float) (10 * Math.sin(0.01*(tick))), 0, 10);
		camController.update();
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT | GL30.GL_DEPTH_BUFFER_BIT);
		
		modelBatch.begin(cam);
		modelBatch.render(instance, environment);
		modelBatch.end();
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
		modelBatch.dispose();
		model.dispose();
	}
	
	private short[] createGridIndices(float[] vertices) {
		short[] indices = new short[numTris*3]; //3 vertices and 2 tris for quad
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
		return indices;
	}
	
	private float[] createGridVertices() {
		
		float[] vertices = new float[NUM_ROWS * NUM_COLS * 3 * 2]; //*2 for normals
		int i = 0;
		int vertex = 0;
		float xStart = -Gdx.graphics.getWidth()/2;
		xStart = -20;
		float yStart = -Gdx.graphics.getHeight()/2;
		yStart = -20;
		float multiplyer = (float) (.8*Math.sin(tick/4)+.7);
		for(int row = 0; row< NUM_COLS; row++) {
			for(int col = 0; col < NUM_ROWS; col++) {
				int coordinate = 0; 
				float x = xStart + TRI_WIDTH * col;
				float y = yStart + TRI_WIDTH * row;
				float z = waterMap1[vertex] * multiplyer;
				vertices[i + coordinate++] = x;
				vertices[i + coordinate++] = y;
				vertices[i + coordinate++] = z;
				//skip for normals
				coordinate += 3;
				i += coordinate;
				vertex++;
			}
		}
		return vertices;
	}
	
	private void updateWaterMaps() {
		Random rand = new Random();
		for(int idxVertex = 0; idxVertex < NUM_COLS * NUM_ROWS; idxVertex++) {
			waterMap1[idxVertex] += rand.nextFloat()/100 - .02;
			waterMap2[idxVertex] += rand.nextFloat()/100 - 0.02;
			if(waterMap1[idxVertex] > WAVE_HEIGHT || waterMap1[idxVertex] < -WAVE_HEIGHT) {
				waterMap1[idxVertex] = rand.nextFloat() * WAVE_HEIGHT;
			}
			if(waterMap2[idxVertex] > WAVE_HEIGHT || waterMap2[idxVertex] < -WAVE_HEIGHT) {
				waterMap2[idxVertex] = rand.nextFloat() * WAVE_HEIGHT;
			}
		}	
	}
	private void createWaterMap() {
		long seed = 10;
		Random rand = new Random(seed);
		for(int idxVertex = 0; idxVertex < NUM_COLS * NUM_ROWS; idxVertex++) {
			waterMap1[idxVertex] = rand.nextFloat() * WAVE_HEIGHT;
			waterMap2[idxVertex] = rand.nextFloat() * WAVE_HEIGHT;
		}
	}
	
	private void createNormals(short[] indices, float[] vertices) {
		//calculate face normals
		Vector3[] faceNormals = new Vector3[numTris];
		int face = 0;
		Vector3 orientationPoint = new Vector3(0, 0, 40);
		for(int row = 0; row < NUM_ROWS-1; row++) {
			for(int col = 0; col < NUM_COLS-1; col++) { 
				int leftBot = row * NUM_COLS + col;
				int leftTop = row * NUM_COLS + col + NUM_COLS;
				int rightBot = row * NUM_COLS + col + 1;
				int rightTop = row * NUM_COLS + col + NUM_COLS + 1;
				
				Vector3 vec1 = getVertexPosition(leftTop, vertices).sub(getVertexPosition(leftBot, vertices));
				Vector3 vec2 = getVertexPosition(rightBot , vertices).sub(getVertexPosition(leftBot, vertices));
				faceNormals[face++] = vec1.crs(vec2);
				faceNormals[face -1].nor();
				
				if(faceNormals[face-1].dot(new Vector3(orientationPoint.sub(getVertexPosition(leftBot, vertices)))) < 0) {
					faceNormals[face-1].scl(-1, -1, -1);
				}
				
				vec1 = getVertexPosition(leftTop, vertices).sub(getVertexPosition(rightTop, vertices));
				vec2 = getVertexPosition(rightBot, vertices).sub(getVertexPosition(rightTop, vertices));
				faceNormals[face++] = vec2.crs(vec1);
				faceNormals[face -1].nor();
				
				if(faceNormals[face-1].dot(new Vector3(orientationPoint.sub(getVertexPosition(rightTop, vertices)))) < 0) {
					faceNormals[face-1].scl(-1, -1, -1);
				}
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
		System.out.println();
	}
	
	private Vector3 getVertexPosition(int index, float[] vertices) {
		float x = vertices[index * 6]; //6 for 3 position and 3 normals
		float y = vertices[index * 6 + 1];
		float z = vertices[index * 6 + 2];
		return new Vector3(x, y, z);
	}
}
