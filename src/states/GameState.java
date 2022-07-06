package states;
import java.awt.Graphics;

import api.Assets;
import api.Game;
import gameClasses.Field;
import gameClasses.Rule;

public class GameState extends States {
	
	private Rule rule;
	
	public GameState(Game game) {
		super(game);
		rule = new Rule(game);
	}
	
	@Override
	public void tick() {
		rule.tick();
	}

	@Override
	public void render(Graphics g) {
		if(game.iAmWhite) g.drawImage(Assets.bgrWhite, 0, 0, null);
		else 			  g.drawImage(Assets.bgrBlack, 0, 0, null);
		
		g.drawImage(Assets.sideBarBlanc, 1060, 0, null);
		rule.render(g);
	}
	
	@Override
	public void init() {
		rule.init();
	}

	@Override
	public void addField(Field[] field) {}
	
}
