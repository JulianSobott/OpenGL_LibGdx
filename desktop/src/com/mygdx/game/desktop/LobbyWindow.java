package com.mygdx.game.desktop;

import org.lwjgl.opengl.Display;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Array;

import water.DisplayManager;

public class LobbyWindow{
	
	public LobbyWindow() {
		DisplayManager.createDisplay();
		
	}
	
	public void run() {
		while(!Display.isCloseRequested()) {
			
			
			DisplayManager.updateDisplay();
		}
		
		DisplayManager.closeDisplay();
	}
	
	
}
