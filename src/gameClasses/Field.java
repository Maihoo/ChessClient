package gameClasses;
import java.awt.Graphics;

import api.Assets;
import api.Game;

public class Field{

	Game game;
	public int x;
	public int y;
	public int fieldSize = 120;
	public String character = "";
	public boolean white = false;
	public boolean black = false;
	public boolean wasMoved = false;
	public boolean enPassanteable = false;
    
    public Field(Game game, String character, boolean weis, boolean schwarz, int x, int y) {
    	this.game = game;
    	this.character = character;
    	this.white = weis;
    	this.black = schwarz;
    	this.x = x;
    	this.y = y;
    }
    
    public void render(Graphics g) {
  
    	
    	if(white) {
    		switch (character) {
	    		case "turm": 
	    			g.drawImage(Assets.turmW, 		50+fieldSize*x, 50+fieldSize*y, null);
	    			break;
	    		case "springer": 
	    			g.drawImage(Assets.springerW, 	50+fieldSize*x, 50+fieldSize*y, null);
	    			break;
	    		case "laeufer": 
	    			g.drawImage(Assets.laeuferW, 	50+fieldSize*x, 50+fieldSize*y, null);
	    			break;
	    		case "koenig": 
	    			g.drawImage(Assets.koenigW, 	50+fieldSize*x, 50+fieldSize*y, null);
	    			break;
	    		case "dame": 
	    			g.drawImage(Assets.dameW, 		50+fieldSize*x, 50+fieldSize*y, null);
	    			break;
	    		case "bauer": 
	    			g.drawImage(Assets.bauerW, 		50+fieldSize*x, 50+fieldSize*y, null);
	    			break;
	    		default: 
	    			break;
    		}
    	}
    	if(black) {
       		switch (character) {
	    		case "turm": 
	    			g.drawImage(Assets.turmS, 		50+fieldSize*x, 50+fieldSize*y, null);
	    			break;
	    		case "springer": 
	    			g.drawImage(Assets.springerS, 	50+fieldSize*x, 50+fieldSize*y, null);
	    			break;
	    		case "laeufer": 
	    			g.drawImage(Assets.laeuferS, 	50+fieldSize*x, 50+fieldSize*y, null);
	    			break;
	    		case "koenig": 
	    			g.drawImage(Assets.koenigS, 	50+fieldSize*x, 50+fieldSize*y, null);
	    			break;
	    		case "dame": 
	    			g.drawImage(Assets.dameS, 		50+fieldSize*x, 50+fieldSize*y, null);
	    			break;
	    		case "bauer": 
	    			g.drawImage(Assets.bauerS, 		50+fieldSize*x, 50+fieldSize*y, null);
	    			break;
	    		default: 
	    			break;
       		}
    	}
    }
}