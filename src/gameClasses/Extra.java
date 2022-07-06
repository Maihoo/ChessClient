package gameClasses;
import java.awt.Graphics;
import java.awt.Image;

import api.Game;

public class Extra{

	Game game;
	public int x;
	public int y;
	public int fieldSize = 120;
    
    public Image pic;
    public boolean render = true;
    
    public Extra(Game game, Image pic, int x, int y, boolean render) {
    	this.game = game;
    	this.pic = pic;
    	this.x = x;
    	this.y = y;
    	this.render = render;
    }

    public void render(Graphics g) {
    	if(render) g.drawImage(pic, 50+fieldSize*x, 50+fieldSize*y, null);
    } 
}