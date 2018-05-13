package com.mygdx.game.desktop;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.OpenGL;
import com.mygdx.game.SimpleTests;
import com.mygdx.game.Test3d;

import water.WaveSimulation;


public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.fullscreen = false;
		config.title = "Catan";
		config.width = 500;
		config.height = 500;
		config.samples = 8;
		new LwjglApplication(new WaveSimulation(), config);
	}
}
