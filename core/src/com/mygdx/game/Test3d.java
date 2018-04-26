package com.mygdx.game;

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
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.g3d.model.MeshPart;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.MeshBuilder;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Matrix4;
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
	
	final int NUM_ROWS = 10;
	final int NUM_COLS = 10;
	final int TRI_WIDTH = 2;
	int numTris;
	
	@Override
	public void create() {
		numTris = (NUM_COLS-1) * (NUM_ROWS-1);
		
		modelBatch = new ModelBatch();
		cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		cam.position.set(0, 0, 10f);
		cam.lookAt(0, 0, 0);
		cam.near = 1f;
		cam.far = 300f;
		cam.update();
		camController = new CameraInputController(cam);
		Gdx.input.setInputProcessor(camController);

		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, .4f, .4f, .4f, 1f));
		environment.add(pointLight = new PointLight().set(0.8f, 0.8f, 0.8f, 0f, 20f, 5f, 2f));
		environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
		//create water mesh
		float[] vertices = createGridVertices();
		short[] indices = createGridIndices(vertices);
		waterMesh = new Mesh(false, NUM_ROWS*NUM_COLS*3, numTris*3, new VertexAttribute(VertexAttributes.Usage.Position, 3, ShaderProgram.POSITION_ATTRIBUTE));
		waterMesh.setIndices(indices);
		waterMesh.setVertices(vertices);
		//MeshBuilder meshBuilder = new MeshBuilder();
		//meshBuilder.begin(Usage.Position, GL30.GL_TRIANGLES);
		//meshBuilder.addMesh(waterMesh);
		//waterMesh = meshBuilder.end();
		ModelBuilder modelBuilder = new ModelBuilder();
		modelBuilder.begin();
		modelBuilder.part("water", waterMesh, GL30.GL_TRIANGLES, new Material(ColorAttribute.createDiffuse(Color.RED)));
		model = modelBuilder.end();
		//model = modelBuilder.createBox(5, 5, 5, new Material(ColorAttribute.createDiffuse(Color.RED)), Usage.Position | Usage.Normal);
		instance = new ModelInstance(model);
		//instance.transform.translate(new Vector3(-5, -5, 0));
		//instance.transform.rotate(Vector3.Y, 30);
		/*model = modelBuilder.createRect(10, 10, 0, 0, 0, 10, 0, 10, 0, 0, 10, 10, 0, .5f, .5f, new Material(ColorAttribute.createDiffuse(Color.GREEN)), Usage.Position | Usage.Normal);
		instance = new ModelInstance(model);
		//================================================================
		/*MeshBuilder meshBuilder = new MeshBuilder();
		meshBuilder.begin(Usage.Position | Usage.Normal, GL30.GL_TRIANGLES);
		meshBuilder.rect(new Vector3(0, 0, 0), new Vector3(10, 0, 0), new Vector3(0, 10, 0), new Vector3(10, 10, 0), new Vector3(0, 0, 1));
		Mesh side1 = meshBuilder.end();
		
		meshBuilder.begin(new VertexAttributes(VertexAttribute.Position(), VertexAttribute.Normal()));
		meshBuilder.rect(new Vector3(0, 0, 0), new Vector3(0, 0, 10), new Vector3(0, 10, 0), new Vector3(0, 10, 10), new Vector3(1, 0, 0));
		Mesh side2 = meshBuilder.end();
		
		//Model side1 = modelBuilder.createRect(0, 0, 0, 10, 0, 0, 10, 10, 0, 0, 10, 0, 0, 0, 1, new Material(ColorAttribute.createDiffuse(Color.GREEN)), Usage.Position | Usage.Normal);
		Model side2 = modelBuilder.createRect(0, 0, 0, 10, 0, 10, 10, 10, 10, 0, 10, 10, 0, 0, 1, new Material(ColorAttribute.createDiffuse(Color.GREEN)), Usage.Position | Usage.Normal);
		Model side3 = modelBuilder.createLineGrid(5, 5, 5, 5,  new Material(ColorAttribute.createDiffuse(Color.GREEN)), Usage.Position);
		instance = new ModelInstance(side3);
		modelBuilder.begin();
		//modelBuilder.part(side1.meshParts.get(0), side1.materials.get(0));
		modelBuilder.part(side2.meshParts.get(0), side2.materials.get(0));
		model = modelBuilder.end();*/
	//	instance = new ModelInstance(model);			
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}
	int tick = 0;
	@Override
	public void render() {
		tick++;
		pointLight.setPosition((float)(10*Math.sin(tick)), 10, 20);
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
		short[] indices = new short[numTris*3];
		int idxVertex = 0;
		for(int tri = 0; tri < numTris*3 ; tri++) {
			if((idxVertex+1) % NUM_COLS != 0 || idxVertex == 0) {
				int i = 0;
				indices[tri + i++] = (short) idxVertex;
				indices[tri + i++] = (short) (idxVertex + 1);
				indices[tri + i++] = (short) (idxVertex + NUM_COLS);
				idxVertex++;
				tri+=2;
			}else {
				idxVertex++;
				tri--;
			}
			
		}
		return indices;
	}
	
	private float[] createGridVertices() {
		
		float[] vertices = new float[NUM_ROWS * NUM_COLS * 3];
		int i = 0;
		float xStart = -Gdx.graphics.getWidth()/2;
		xStart = -20;
		float yStart = -Gdx.graphics.getHeight()/2;
		yStart = -20;
		for(int row = 0; row< NUM_COLS; row++) {
			for(int col = 0; col < NUM_ROWS; col++) {
				int coordinate = 0; 
				vertices[i + coordinate++] = xStart + TRI_WIDTH * col; //x
				vertices[i + coordinate++] = yStart + TRI_WIDTH * row; //x
				vertices[i + coordinate++] = (float) (Math.random()*3-3); //x
				i += coordinate;
			}
		}
		return vertices;
	}

}
