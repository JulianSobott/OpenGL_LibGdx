package utils;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Input.Keys;

import water.OceanSimulation;

public class InputHandler implements InputProcessor {
	
	public enum Attribute{
		WAVE_HEIGHT, SPREADINGRATE, PERIOD,
	}
	
	private OceanSimulation oceanSimulation;
	private boolean isChangingValue = false;
	private Attribute activeAttribute;
	private String valueSequence = "";
	private float value;
	
	public InputHandler(OceanSimulation oceanSimulation) {
		this.oceanSimulation = oceanSimulation;
	}

	@Override
	public boolean keyDown(int keycode) {
		if(keycode == Keys.SPACE) {
			this.oceanSimulation.togglePauseSimulation();
		}else if(keycode == Keys.ENTER) {
			if(isChangingValue) {
				if(!valueSequence.equals("")) {
					value = Float.parseFloat(valueSequence);
					this.oceanSimulation.setAttribute(activeAttribute, value);
					System.out.println(activeAttribute + "changed to: " + value);
					valueSequence = "";
				}	
			}
			isChangingValue = !isChangingValue;
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
		if(isChangingValue) {
			if(character == 'h') activeAttribute = Attribute.WAVE_HEIGHT;
			if(character == 's') activeAttribute = Attribute.SPREADINGRATE;
			if(character == 'p') activeAttribute = Attribute.PERIOD;
			if(new String("1234567890").contains(new String(""+character))) {
				valueSequence += character; 
			}
		}
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
