package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

public class OpenGL extends ApplicationAdapter {
	
	ShaderProgram shader;
	Mesh mesh;
	OrthographicCamera cam;
	PerspectiveCamera cam2;
	
	final int NUM_ROWS = 100;
	final int NUM_COLS = 100;
	final int TRI_WIDTH = 2;
	int numTris;
	
	long tick = 0;
	
	float[] stillWaterVertices;
	
	@Override
	public void create() {
		super.create();
		numTris = (NUM_COLS-1) * (NUM_ROWS-1);
		cam = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		cam2 = new PerspectiveCamera(64,Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		cam2.position.set(-220, -100, 100f);
		//cam2.lookAt(-20, 5, 10);
		cam2.up.set(0, 1, 0);
		cam2.update();
		shader = new ShaderProgram(Gdx.files.local("assets/vertex.glsl").readString(), Gdx.files.local("assets/fragment.glsl").readString());
		//Vertices - indices
		
		mesh = new Mesh(false, NUM_ROWS*NUM_COLS*3, numTris*3, new VertexAttribute(VertexAttributes.Usage.Position, 3, ShaderProgram.POSITION_ATTRIBUTE));
		float[] vertices = createGridVertices();
		mesh.setVertices(vertices);
		mesh.setIndices(createGridIndices(vertices));
		
		mesh = new Mesh(false, NUM_ROWS*NUM_COLS*3, numTris*3, new VertexAttribute(VertexAttributes.Usage.Position, 3, ShaderProgram.POSITION_ATTRIBUTE));
		vertices = createGridVertices();
		stillWaterVertices = vertices;
		mesh.setVertices(vertices);
		mesh.setIndices(createGridIndices(vertices));
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
		float yStart = -Gdx.graphics.getHeight()/2;
		for(int row = 0; row< NUM_COLS; row++) {
			for(int col = 0; col < NUM_ROWS; col++) {
				int coordinate = 0; 
				vertices[i + coordinate++] = xStart + TRI_WIDTH * col; //x
				vertices[i + coordinate++] = yStart + TRI_WIDTH * row; //x
				vertices[i + coordinate++] = 0; //x
				i += coordinate;
			}
		}
		return vertices;
	}

	@Override
	public void render() {
		super.render();
		
		Matrix4 translationMatrix = new Matrix4(new float[] {
				1f, 0f, 0f, 0f,
				0f, 1f, 0f, 0f,
				0f, 0f, 1f, 0f,
				0f, 0f, 0f, 1f,
		});
		Matrix4 rotationMatrix = new Matrix4(new float[] {
				1f, 0f, 0f, 0f,
                0f, (float) Math.cos(tick * 0.00005), -(float) Math.sin(tick * 0.00005), 0f,
                0f, (float) Math.sin(tick * 0.00005), (float) Math.cos(tick * 0.00005), 0f,
                0f, 0f, 0f, 1f
		});
		 Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		 Gdx.gl.glClearColor(0, 0, 255, 255);
	     shader.begin();
	     if(!shader.isCompiled())
	    	 System.out.println(shader.getLog());
		     
	     shader.setUniformMatrix("u_projTrans", cam2.combined);
	    // shader.setUniformMatrix("u_rotationMatrix", rotationMatrix);
	     //shader.setUniformMatrix("u_moveMatrix", translationMatrix);
	     mesh.render(shader, GL30.GL_TRIANGLES, 0, numTris * 3);
	     shader.end();
	     tick++;
		updateMesh(mesh);
	}

	private void updateMesh(Mesh mesh) {
		float y = (float) (-100 * Math.sin((Math.PI*2)/Gdx.graphics.getHeight()/2*(tick - Gdx.graphics.getHeight()/4)));
		//y = (float) (-1f - Math.random()*3);
		float[] vertices = new float[NUM_ROWS*NUM_COLS*3];
		vertices = mesh.getVertices(vertices);
		int direction = 1;
		for(int i = 1; i < vertices.length; i += 3) {
			if(vertices[i] - stillWaterVertices[i] > 10) {
				direction = 0;
			}else if(vertices[i] - stillWaterVertices[i] < 10) {
				direction = -1;
			}
			if(Math.random() > 0.9)
				vertices[i] = (float) ((Math.random()-0.5)*3 * Math.sin(tick* 0.0005)) + vertices[i];
			if(Math.abs(vertices[i] - stillWaterVertices[i]) > 10) {
				//vertices[i] -= vertices[i] - (float) (0.001 * Math.sin(tick* 0.005));
			}
				
		}
		//vertices[1 + 3* 20] = (float) (0.5 * Math.sin(tick* 0.05)) + vertices[1 + 3* 20];
		//vertices[1 + 3* 15] = (float) (0.5 * Math.sin(tick* 0.05)) + vertices[1 + 3* 15];
		mesh.setVertices(vertices);
	}
	
	@Override
	public void dispose () {
		
	}
}
