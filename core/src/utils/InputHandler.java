package utils;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Input.Keys;

import water.OceanSimulation;

public class InputHandler implements InputProcessor {

	private OceanSimulation oceanSimulation;
	
	public InputHandler(OceanSimulation oceanSimulation) {
		this.oceanSimulation = oceanSimulation;
	}

	@Override
	public boolean keyDown(int keycode) {
		if(keycode == Keys.SPACE) {
			this.oceanSimulation.togglePauseSimulation();
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
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

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
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}

}
