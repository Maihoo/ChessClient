package states;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.MouseInfo;
import java.awt.Point;
import java.util.ArrayList;

import api.Assets;
import api.Game;
import api.PlaySound;
import gameClasses.Extra;
import gameClasses.Field;

public class ReviewState extends States {
	
	Game game;
	int timePoint, u, n, x, y;
	int diff = 1;
	public int f = -1;
	public int t 	= -1;
	public int activeFeld = 0;
	Point p = MouseInfo.getPointerInfo().getLocation();
	Point b = MouseInfo.getPointerInfo().getLocation();
	Extra from, to;
	Extra rahmen = new Extra(game, Assets.rahmen, p.x, p.y, true);
	Field[] field = new Field[64];
	ArrayList<String> boards = new ArrayList<String>();
	PlaySound ps = new PlaySound();
	
	public ReviewState(Game game) {
		super(game);
		this.game = game;
		init();
		System.out.println("Choose Figure State created.");
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
		
		if(b.x > 1460) b.x = 1460;
		if(b.x < 0) b.x = 0;
		if(b.y > 1460) b.y = 1460;
		if(b.y < 0) b.y = 0;
		
		if(game.mouseManager.leftPressed) {
			if(b.x > 1060 && b.x < 1160 && b.y > 1060-100 && b.y < 1060 ) {
				timePoint = 0;
			}
			if(b.x > 1160 && b.x < 1260 && b.y > 1060-100 && b.y < 1060 ) {
				if(timePoint > 0) 					timePoint--;
			}
			if(b.x > 1260 && b.x < 1360 && b.y > 1060-100 && b.y < 1060 ) {
				if(timePoint < boards.size()-diff) 	timePoint++;
			}
			if(b.x > 1360 && b.x < 1460 && b.y > 1060-100 && b.y < 1060 ) {
				timePoint = boards.size()-diff;
			}
			
			game.resetMM();
		}
		
		rahmen.x = x;
		rahmen.y = y;
	}

	@Override
	public void render(Graphics g) {
		if(game.iAmWhite) g.drawImage(Assets.bgrWhite, 0, 0, null);
		else 			  g.drawImage(Assets.bgrBlack, 0, 0, null);
		
		g.drawImage(Assets.sideBarReview, 1060, 0, null);
		
		if(n == 0) init();
		
		field = stringToField(boards.get(timePoint));
		
		from.x = f%8;
		from.y = f/8;
		if(!game.isOffline && !game.iAmWhite) {from.x = 7 - from.x	; from.y = 7 - from.y;}
		if(timePoint != 0) from.render(g);
		
		to.x = t%8;
		to.y = t/8;
		if(!game.isOffline && !game.iAmWhite) {to.x 	 = 7 - to.x		; to.y = 7 - to.y;}
		if(timePoint != 0) to.render(g);
		
		g.setFont(new Font("Impact", Font.PLAIN, 64)); 
		g.setColor(new Color(36, 7, 7));
		
		byte[] data = ("Step " + timePoint + "/" + (boards.size()-diff)).getBytes();
	    g.drawBytes(data, 0, data.length, 1100, 932);
	  
		
		for(int i = 0; i < 64; i++) {
			field[i].render(g);
		}
		
		rahmen.render(g);
	}
	
	@Override
	public void init() {
		n++;
		from 		= new Extra(game, Assets.yellow, 0, 0, true);
		to 			= new Extra(game, Assets.yellow, 0, 0, true);
	}
		
	public void addField(Field[] input) {
		timePoint = boards.size();
		
		Field[] output = new Field[64];
		
		for(int i = 0; i < 64; i++) {
			output[i] = new Field(game, input[i].character, input[i].white, input[i].black, input[i].x, input[i].y);
			output[i].wasMoved		= input[i].wasMoved;
			output[i].enPassanteable	= input[i].enPassanteable;
		}
		
		if(!game.iAmWhite && !game.isOffline) {
		
			Field[] tempField = new Field[64];

			for(int i = 0; i < 64; i++) {
				tempField[i] = new Field(game, output[i].character, output[i].white, output[i].black, output[i].x, output[i].y);
				tempField[i].wasMoved		= output[i].wasMoved;
				tempField[i].enPassanteable	= output[i].enPassanteable;
			}
			
			for(int i = 0; i < 64; i++) {
				output[i].character 		= tempField[63-i].character;
				output[i].white 			= tempField[63-i].white;
				output[i].black 			= tempField[63-i].black;
				output[i].wasMoved			= tempField[63-i].wasMoved;
				output[i].enPassanteable	= tempField[63-i].enPassanteable;
			}
		}
		boards.add(fieldToString(output));
	}
	
	public Field[] stringToField(String input) {
		Field[] tempField = new Field[64];
		for(int i = 0; i < 8; i++) {
			for(int j = 0; j < 8; j++) {
				tempField[(8*i)+j] = new Field(game, "0", false, false, j, i);
			}
		}
		
		String[] tempString = input.split(":");
		
		for(int i = 0; i < 64; i++) {		
	
			String[] tempSubString = tempString[i].split("\\.", -1);
			
				tempField[i].character = tempSubString[0];
				if(tempSubString[1].equalsIgnoreCase("1")) tempField[i].white = true;
				else tempField[i].white = false;
				if(tempSubString[2].equalsIgnoreCase("1")) tempField[i].black = true;
				else tempField[i].black = false;
				if(tempSubString[3].equalsIgnoreCase("1")) tempField[i].wasMoved = true;
				else tempField[i].wasMoved = false;
				if(tempSubString[4].equalsIgnoreCase("1")) tempField[i].enPassanteable = true;
				else tempField[i].enPassanteable = false;
		}
		game.clock.setEnemyTime(tempString[64]);
		f = Integer.parseInt(tempString[65]);
		t = Integer.parseInt(tempString[66]);
		
		return tempField;
	}
	
	public String fieldToString(Field[] input) {
		String result = "";
		
		for(int i = 0; i< input.length; i++) {
			
			if(!(input[i].character.equalsIgnoreCase(""))) result += input[i].character;
			else result += "0";
			
			result += ".";
			if(input[i].white) result += "1";
			else result += "0";
			result += ".";
				
			if(input[i].black) result += "1";
			else result += "0";
			result += ".";
			
			if(input[i].wasMoved) result += "1";
			else result+= "0";
			result += ".";
			
			if(input[i].enPassanteable) result += "1";
			else result+= "0";
			
			result += ":";
		}	
		
		if(game.clockIsRunning) result += game.clock.getMyTime();
		else result += "1.0.0.0";
		
		result += ":";
		result += game.from;
		result += ":";
		result += game.to;
		
		return result;
	}
}
