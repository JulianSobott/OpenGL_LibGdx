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
	private CameraInputController camController;
	
	private ShapeRenderer sr;
	
	@Override
	public void create() {
		modelBatch = new ModelBatch();
		cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		cam.position.set(0, 0, 20f);
		cam.lookAt(0, 0, 0);
		cam.near = 1f;
		cam.far = 300f;
		cam.update();
		camController = new CameraInputController(cam);
		Gdx.input.setInputProcessor(camController);

		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, .1f, .1f, .1f, 1f));
		environment.add(new PointLight().set(0.8f, 0.8f, 0.8f, 5f, 2f, 2f, 20f));
		ModelBuilder modelBuilder = new ModelBuilder();
		model = modelBuilder.createRect(0, 0, 0, 5, 0, 0, 5, 5, 0, 0, 5, 0, 0, 1, 1, new Material(ColorAttribute.createDiffuse(Color.GREEN)), Usage.Position | Usage.Normal);
		//model = modelBuilder.createSphere(1f, 1f, 1f, 20, 10, new Material(ColorAttribute.createDiffuse(Color.GREEN)), Usage.Position | Usage.Normal);
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

	@Override
	public void render() {
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
	

}
