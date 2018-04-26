package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.GLTexture;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.GLFrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;

public class SimpleTests extends ApplicationAdapter{
	
	ShaderProgram shader;
	Mesh mesh;
	OrthographicCamera cam;
	Texture texture;
	Texture Region;
	FrameBuffer fbo;

	
	@Override
	public void create() {
		//super.create();
		cam = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		fbo = new FrameBuffer(Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getWidth(), false);
		Array<Texture> test2 = fbo.getTextureAttachments();
		int test = fbo.getFramebufferHandle();
		shader = new ShaderProgram(Gdx.files.absolute("E:\\Programmieren\\Java\\OpenGL_LibGdx\\core\\src\\shaders\\simpleVertex.glsl").readString(), Gdx.files.absolute("E:\\Programmieren\\Java\\OpenGL_LibGdx\\core\\src\\shaders\\simpleFragment.glsl").readString());
		mesh = new Mesh(false, 6, 6, new VertexAttribute(VertexAttributes.Usage.Position, 3, ShaderProgram.POSITION_ATTRIBUTE));		
		
		float xStart = -Gdx.graphics.getWidth()/2;
		float yStart = -Gdx.graphics.getWidth()/2;
		mesh.setVertices(new float[] {
			xStart, yStart, 0f,
			-xStart, yStart, 0f,
			xStart, 0f, 0f,
			xStart, 0, 0f,
			-xStart, 0, 0f,
			-xStart, yStart, 0f,
		});
		mesh.setIndices(new short[] {0,1,2, 3, 4,5});
		
		fbo.begin();
		Gdx.gl.glClearColor(255f, 0f, 0f, 0f);
		Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);
		shader.begin();
		if(!shader.isCompiled())
			System.out.println(shader.getLog());
		shader.setUniformMatrix("u_projMatrix", cam.combined);
		mesh.render(shader, GL30.GL_TRIANGLES, 0, 6);
		shader.end();
		fbo.end();
		
	}
	
	@Override
	public void render() {	
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		Gdx.gl.glClearColor(0, 0, 0, 255);
		
		shader.begin();
		if(!shader.isCompiled())
			System.out.println(shader.getLog());
		shader.setUniformMatrix("u_projMatrix", cam.combined);
		mesh.render(shader, GL30.GL_TRIANGLES, 0, 6);
		shader.end();
	}
	
	
}
