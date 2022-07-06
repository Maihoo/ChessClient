package states;
import java.awt.Graphics;
import api.Assets;
import api.Game;
import gameClasses.Field;

public class ViewerState extends States {
	
	int n = 0;
	Game game;
	Field[] field = new Field[64];
	
	public ViewerState(Game game) {
		super(game);
		this.game = game;
		init();
	}
	
	@Override
	public void tick() {
		if(n == 0) init();
		try {
			Thread.sleep(100);
		}catch(Exception e) {}
		update();
	}
	
	public void update() {
		try {
			this.field = game.getField();
		}catch(Exception e) { }
	}

	@Override
	public void render(Graphics g) {
		g.drawImage(Assets.bgrWhite, 0, 0, null);
		g.drawImage(Assets.sideBarBlanc, 1060, 0, null);
		
		if(n == 0) init();
		for(int i = 0; i < 64; i++) {
			field[i].render(g);
		}
	}
	
	@Override
	public void init() {
		n++;
		for(int i = 0; i < 8; i++) {
			for(int j = 0; j < 8; j++) {
				field[(8*i)+j] = new Field(game, "0", false, false, j, i);
			}
		}
	}

	@Override
	public void addField(Field[] field) {}
	
}
