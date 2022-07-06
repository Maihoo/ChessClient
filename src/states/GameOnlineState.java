package states;
import java.awt.Graphics;

import api.Assets;
import api.Game;
import gameClasses.Field;
import gameClasses.Rule;

public class GameOnlineState extends States {
	
	public Rule rule;
	
	public GameOnlineState(Game game) {
		super(game);
		rule = new Rule(game);
		init();
	}
	
	@Override
	public void tick() {
		rule.tick();
	}

	@Override
	public void render(Graphics g) {
		if(game.iAmWhite)	g.drawImage(Assets.bgrWhite, 0, 0, null);
		else 				g.drawImage(Assets.bgrBlack, 0, 0, null);
		if(game.isMyTurn || !game.ableToRedo)	g.drawImage(Assets.sideBarOnlyFF, 1060, 0, null);
		else 									g.drawImage(Assets.sideBarFFnRedo, 1060, 0, null);
		rule.render(g);
	}
	
	@Override
	public void init() {
		rule.init();
	}

	@Override
	public void addField(Field[] field) {}

	
}
