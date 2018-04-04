package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;

public class OpenGL extends ApplicationAdapter {
	
	ShaderProgram shader;
	Mesh mesh;
	OrthographicCamera cam;
	
	final int NUM_ROWS = 4;
	final int NUM_COLS = 4;
	int numTris;
	
	long tick = 0;
	
	@Override
	public void create() {
		super.create();
		numTris = (NUM_COLS-1) * (NUM_ROWS-1);
		cam = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		shader = new ShaderProgram(Gdx.files.local("assets/vertex.glsl").readString(), Gdx.files.local("assets/fragment.glsl").readString());
		
		mesh = new Mesh(false, 600, 600, new VertexAttribute(VertexAttributes.Usage.Position, 3, ShaderProgram.POSITION_ATTRIBUTE));
		/*mesh.setVertices(new float[] 
				{
						0, 0 , 0,
						-100, 100, 0,
						200, 200, 0,
						
						100, 100 , 0,
						-200, 200, 0,
						300, 300, 0,
						20, 200, 0
				}
		);*/
		float[] vertices = createGridVertices();
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
		//new short[] {0,1,3,1,2,5,3,6,7,7,8,5}
		return indices;
	}

	private float[] createGridVertices() {
		
		float[] vertices = new float[NUM_ROWS * NUM_COLS * 3];
		
		int i = 0;
		for(int row = 0; row< NUM_COLS; row++) {
			for(int col = 0; col < NUM_ROWS; col++) {
				int coordinate = 0; 
				vertices[i + coordinate++] = -200 + 100 * col; //x
				vertices[i + coordinate++] = -200 + 100 * row; //x
				vertices[i + coordinate++] = 0; //x
				i += coordinate;
			}
		}
		
		/*for(int tri = 0; tri < NUM_TRIANGLES; tri++) {
			for(int vertex = 0; vertex < 3; vertex++) {
				int coordinate = 0; 
				vertices[tri + vertex + coordinate++] = 100f * vertex; //x
				vertices[tri + vertex + coordinate++] = 0f * 0.5f * vertex;	//y
				vertices[tri + vertex + coordinate++] = 1f;	//z
			}		
		}*/
		return vertices;
	}

	@Override
	public void render() {
		super.render();
		 Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	     shader.begin();
	     if(!shader.isCompiled())
	    	 System.out.println(shader.getLog());
	     shader.setUniformMatrix("u_projTrans", cam.combined);
	     mesh.render(shader, GL30.GL_TRIANGLES, 0, 100);
	     shader.end();
	     tick++;
		//updateMesh();
	}

	private void updateMesh() {
		float y = (float) (-100 * Math.sin((Math.PI*2)/Gdx.graphics.getHeight()/2*(tick - Gdx.graphics.getHeight()/4)));
		mesh.setVertices(new float[] 
				{
						0, 0 , 0,
						-100, y, 0,
						-100, 0, 0
				}
		);
	}
	
	@Override
	public void dispose () {
		
	}
}
