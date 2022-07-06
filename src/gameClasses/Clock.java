package gameClasses;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.Timer;
import java.util.TimerTask;
import api.Game;

public class Clock {
	
	String myTime, enemyTime;
	
	int secMe01, secMe10, minMe01, minMe10;
	int secEn01, secEn10, minEn01, minEn10;
	Timer m1Timer = new Timer();
	int amount;
	Game game;
	
	public Clock(Game game) {
		this.game = game;
		minMe10 = 1;
		minMe01 = 0;
		secMe10 = 0;
		secMe01 = 0;
		
		minEn10 = 1;
		minEn01 = 0;
		secEn10 = 0;
		secEn01 = 0;

		m1Timer.scheduleAtFixedRate(task, 1000, 1000);
	}
	
	public String getEnemyTime() {
		String output = "";
		output += minEn10;
		output += ".";
		output += minEn01;
		output += ".";
		output += secEn10;
		output += ".";
		output += secEn01;
		output += ".";
		
		return output;
	}
	
	public void setEnemyTime(String input) {
		this.myTime = input;

		String[] temp = input.split("\\.", -1);

		minEn10 = Integer.parseInt(temp[0]);
		minEn01 = Integer.parseInt(temp[1]);
		secEn10 = Integer.parseInt(temp[2]);
		secEn01 = Integer.parseInt(temp[3]);	
	}
	
	public String getMyTime() {
		String output = "";
		output += minMe10;
		output += ".";
		output += minMe01;
		output += ".";
		output += secMe10;
		output += ".";
		output += secMe01;
		output += ".";
		
		return output;
	}
	
	public void setMyTime(String input) {
		this.myTime = input;

		String[] temp = input.split("\\.", -1);

		minMe10 = Integer.parseInt(temp[0]);
		minMe01 = Integer.parseInt(temp[1]);
		secMe10 = Integer.parseInt(temp[2]);
		secMe01 = Integer.parseInt(temp[3]);	
	}
		
	TimerTask task = new TimerTask() {	
		public void run() {
			
			if(game.isMyTurn) {
				secMe01--;
				if(secMe01 == -1) {
					secMe01 = 9;
					secMe10--;
				}
				if(secMe10 == -1) {
					secMe10 = 5;
					minMe01--;
				}
				if(minMe01 == -1) {
					minMe01 = 9;
					minMe10--;
				}
			}
			else {
				secEn01--;
				if(secEn01 == -1) {
					secEn01 = 9;
					secEn10--;
				}
				if(secEn10 == -1) {
					secEn10 = 5;
					minEn01--;
				}
				if(minEn01 == -1) {
					minEn01 = 9;
					minEn10--;
				}
			}
			
			
			if(minMe10 < 0) {
				game.clockIsRunning = false;
				if( game.iAmWhite) game.blackWillWin = true;
				if(!game.iAmWhite) game.whiteWillWin = true;
			}
			if(minEn10 < 0) {
				game.clockIsRunning = false;
				if(!game.iAmWhite) game.blackWillWin = true;
				if( game.iAmWhite) game.whiteWillWin = true;
			}
			
		}
	};
	
	public void stop() {
		m1Timer.cancel();
	}
	
	public void render(Graphics g) {
		g.setFont(new Font("Impact", Font.PLAIN, 64)); 
		g.setColor(new Color(36, 7, 7));
		byte[] myData 	 = (minMe10 + "" + minMe01 + ":" + secMe10 + "" + secMe01).getBytes();
		byte[] enemyData = (minEn10 + "" + minEn01 + ":" + secEn10 + "" + secEn01).getBytes();
	    g.drawBytes(myData, 	0, myData.length	, 1460-200, 70 );
	    g.drawBytes(enemyData,  0, enemyData.length	, 1460-200, 250);
	}
}

