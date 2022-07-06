package states;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.MouseInfo;
import java.awt.Point;

import api.Assets;
import api.Game;
import gameClasses.Field;

public class MenuState extends States {
	
	Point p = MouseInfo.getPointerInfo().getLocation();
	Point b = MouseInfo.getPointerInfo().getLocation();

	SettingsUI cts, connected, notConnected, secondPlayer, sPr, sPnr, startGame, startOnlineGame;
	
	int u, n, t1, t2 = 0;
	
	public MenuState(Game game) {
		super(game);
	}
	
	@Override 
	public void tick() {
		if(n==0) init();
		n++;
		t1++;
		t2++;
		
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
		
		try {
			if(!game.isConnected && t1 == 32) {
				//t1 = 0;
				game.connectToServer();
			}
		}catch(Exception e) {}
		
		try {
			if(game.isConnected && t2 == 50) {
				t2 = 0;
				game.checkSPReady();
			}
		}catch(Exception e) {}
		
		try {
			if(game.mouseManager.leftPressed && n > 10) {
				b.y -= 31;
				if(b.x > 128 && b.y > 0.5*128 	&& b.x < 7*128 && b.y < 1.5*128 && !game.isConnected) {
					System.out.println("pressed: connect to Server");
					game.connectToServer();
				}
				if(b.x > 128 && b.y > 2.5*128 	&& b.x < 7*128 && b.y < 3.5*128 && game.isConnected) {
					System.out.println("pressed: second Player");
					game.checkSPReady();
				}
				if(b.x > 128 && b.y > 4.5*128 	&& b.x < 7*128 && b.y < 5.5*128) {
					System.out.println("pressed: start Local Game");
					game.setGame();
				}
				if(b.x > 128 && b.y > 6.5*128 	&& b.x < 7*128 && b.y < 7.5*128) {
					System.out.println("pressed: start Online Game");
					game.setOnlineGame();
				}
				
				game.resetMM();
			}
		} catch (Exception f) {
			System.out.println("perhaps there is a problem with the MouseManager. Probably not.");
		}
	}

	@Override
	public void render(Graphics g) {
		g.drawImage(Assets.bgrWhite, 0, 0, null);
		g.drawImage(Assets.sideBarBlanc, 1060, 0, null);
		g.setColor(new Color(0, 0, 0, 170));
		g.fillRect(0, 0, 1460, 1060);
		
		cts.render(g);
		secondPlayer.render(g);
		startGame.render(g);
		startOnlineGame.render(g);
		if(game.isConnected) connected.render(g);
		else notConnected.render(g);
		if(game.spReady) sPr.render(g);
		else sPnr.render(g);
	}
	
	@Override
	public void init() {
		n++;
		System.out.println("initiated Menu state");
		cts = new SettingsUI(game, "cts");
		connected = new SettingsUI(game, "connected");
		notConnected = new SettingsUI(game, "notConnected");
		secondPlayer = new SettingsUI(game, "secondPlayer");
		sPr = new SettingsUI(game, "sPr");
		sPnr = new SettingsUI(game, "sPnr");
		startGame = new SettingsUI(game, "startGame");
		startOnlineGame = new SettingsUI(game, "startOnlineGame");
	}

	@Override
	public void addField(Field[] field) {}
}
