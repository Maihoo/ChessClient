package states;
import java.awt.Graphics;

import api.Game;
import gameClasses.Field;

public abstract class States {
	
	private static States currentState = null;
	
	public static void setState(States state) {
		currentState = state;
	}
	
	public static States getState() {
		return currentState;
	}
	
	protected Game game;
	
	public States(Game game) {
		this.game = game;
	}
	
	public abstract void tick();
	
	public abstract void render(Graphics g);
	
	public abstract void init();

	public abstract void addField(Field[] field);
}
