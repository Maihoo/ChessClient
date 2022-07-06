package states;
import java.awt.Graphics;
import java.awt.MouseInfo;
import java.awt.Point;
import api.Assets;
import api.Game;
import gameClasses.Extra;
import gameClasses.Field;

public class ChooseFigureState extends States {
	
	Game game;
	int n = 0;
	int x = 0;
	int y = 0;
	Point p = MouseInfo.getPointerInfo().getLocation();
	Point b = MouseInfo.getPointerInfo().getLocation();
	Extra rahmen = new Extra(game, Assets.rahmen, p.x, p.y, true);
	public int activeFeld = 0;
	Field[] feld = new Field[64];
	
	
	public ChooseFigureState(Game game) {
		super(game);
		this.game = game;
	}
	
	@Override
	public void tick() {
		if(n == 0) init();
		
		try {
			p = game.getMP();
			if(p != null) {
				b.x = game.getMP().x;
				b.y = game.getMP().y;
			}
		} catch (Exception e) {
			b.x = 0;
			b.y = 0;
		}
		
		x = (b.x-55)/120;
		y = (b.y-75)/120;
		
		if(x > 7) x = 7;
		if(x < 0) x = 0;
		if(y > 7) y = 7;
		if(y < 0) y = 0;
		
		feldscan();
		
		if(game.mouseManager.leftPressed) {
			if(x == 2 && y == 4 ) game.figureChoosen = "springer";
			if(x == 3 && y == 4 ) game.figureChoosen = "laeufer";
			if(x == 4 && y == 4 ) game.figureChoosen = "turm";
			if(x == 5 && y == 4 ) game.figureChoosen = "dame";
			
			game.resetMM();
			game.returnToGame();
		}
		
		rahmen.x = x;
		rahmen.y = y;
	}
	
	public void feldscan(){
		activeFeld = 8*y+x;
		if(activeFeld < 0 || activeFeld > 63) return;
	}

	@Override
	public void render(Graphics g) {
		g.drawImage(Assets.bgrCF, 0, 0, null);
		g.drawImage(Assets.sideBarBlanc, 1060, 0, null);
		
		if(n == 0) init();
		
		for(int i = 0; i < 64; i++) {
			feld[i].render(g);
		}
		
		rahmen.render(g);
	}
	
	@Override
	public void init() {
		n++;
		System.out.println("Choose Figure initiated.");
		
		b.x = 0;
		b.y = 0;		
		
		for(int i = 0; i < 8; i++) {
			for(int j = 0; j < 8; j++) {
				feld[(8*i)+j] = new Field(game, "0", false, false, j, i);
			}
		}
		
		if(game.iAmWhite) {
			feld[26]  = new Field(game, "springer", true, false, 2, 4);
			feld[27]  = new Field(game, "laeufer", true, false, 3, 4);
			feld[28]  = new Field(game, "turm", true, false, 4, 4);
			feld[29]  = new Field(game, "dame", true, false, 5, 4);
		}
		else {
			feld[26]  = new Field(game, "springer", false, true, 2, 4);
			feld[27]  = new Field(game, "laeufer", false, true, 3, 4);
			feld[28]  = new Field(game, "turm", false, true, 4, 4);
			feld[29]  = new Field(game, "dame", false, true, 5, 4);
		}
	}

	@Override
	public void addField(Field[] field) { }
	
}
