package states;
import java.awt.Graphics;

import api.Assets;
import api.Game;

public class SettingsUI{

	Game game;
	public String type = "";
	public boolean show = false;
	int platzhalter = 25;
	int x, y;
    
    public SettingsUI(Game game, String type) {
    	this.game = game;
    	this.type = type;
    }
    
    public void render(Graphics g) {
    	switch (type) {
	    	case "cts": 
	    		g.drawImage(Assets.cts, 			128, 	(int) (0.5*128), null); 
	    		break;
	    	case "connected": 
	    		g.drawImage(Assets.connected, 		7*128, 	(int) (0.5*128), null); 
	    		break;
	    	case "notConnected": 
	    		g.drawImage(Assets.notConnected, 	7*128,	(int) (0.5*128), null); 
	    		break;
	    	case "secondPlayer":
	    		g.drawImage(Assets.secondPlayer, 	128,	(int) (2.5*128), null); 
	    		break;
	    	case "sPr": 
	    		g.drawImage(Assets.connected, 		7*128,	(int) (2.5*128), null); 
	    		break;
	    	case "sPnr": 
	    		g.drawImage(Assets.notConnected, 	7*128,	(int) (2.5*128), null); 
	    		break;
	    	case "startGame":
	    		g.drawImage(Assets.startGame, 		128,	(int) (4.5*128), null); 
	    		break;
	    	case "startOnlineGame":
	    		g.drawImage(Assets.startOnlineGame, 128,	(int) (6.5*128), null); 
	    		break;
	    	default: 
	    		break;
		}
    }
    
    public void setType(String type) {
    	this.type = type;
    }
    
    public String getType(String type) {
    	return type;
    }
}