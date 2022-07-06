package gameClasses;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.util.Timer;
import api.Assets;
import api.Game;
import api.PlaySound;
import gameClasses.Field;

public class PointCounter {
	
	public boolean patt = false;
	boolean soundPlayed = false;
	int points;
	int digitX1, digitX2;
	int amount = 0;
	public int maxwTurm, maxwSpringer, maxwLaeufer, maxwKoenig, maxwDame, maxwBauer;
	public int wTurm, wSpringer, wLaeufer, wKoenig, wDame, wBauer;
	public int maxsTurm, maxsSpringer, maxsLaeufer, maxsKoenig, maxsDame, maxsBauer;
	public int sTurm, sSpringer, sLaeufer, sKoenig, sDame, sBauer;
	Timer m1Timer = new Timer();
	Game game;
	PlaySound ps = new PlaySound();
	long ts = 100;
	
	
	public PointCounter(Game game) {
		this.game = game;
		this.amount = 0;
		digitX2 = 0;
		digitX1 = 0;
		
		maxsTurm = 2; maxsSpringer = 2; maxsLaeufer = 2; maxsKoenig = 1; maxsDame = 1; maxsBauer = 8;
		maxwTurm = 2; maxwSpringer = 2; maxwLaeufer = 2; maxwKoenig = 1; maxwDame = 1; maxwBauer = 8;
		wTurm = 2; wSpringer = 2; wLaeufer = 2; wKoenig = 1; wDame = 1; wBauer = 8;
		sTurm = 2; sSpringer = 2; sLaeufer = 2; sKoenig = 1; sDame = 1; sBauer = 8;
		
		ts = System.currentTimeMillis();
	}
	
	public void setPoints(Field[] field) {
		int result = 0;
		wTurm = 0; wSpringer = 0; wLaeufer = 0; wKoenig = 0; wDame = 0; wBauer = 0;
		sTurm = 0; sSpringer = 0; sLaeufer = 0; sKoenig = 0; sDame = 0; sBauer = 0;
		
		for(int i = 0; i < 64; i++) {
			if(field[i].white) {
				switch (field[i].character) {
		    		case "turm":
		    			wTurm++;
		    			break;
		    		case "springer":
		    			wSpringer++;
		    			break;
		    		case "laeufer":
		    			wLaeufer++;
		    			break;
		    		case "koenig": 
		    			wKoenig++;
		    			break;
		    		case "dame": 
		    			wDame++;
		    			break;
		    		case "bauer": 
		    			wBauer++;
		    			break;
		    		default: 
		    			break;
				}
			}
			if(field[i].black) {
				switch (field[i].character) {
			   		case "turm":
			    		sTurm++;
			    		break;
			   		case "springer":
			   			sSpringer++;
			    		break;
			    	case "laeufer":
			    		sLaeufer++;
		    			break;
		    		case "koenig": 
			   			sKoenig++;
			   			break;
			   		case "dame": 
			   			sDame++;
			    		break;
			    	case "bauer": 
			    		sBauer++;
			    		break;
			    	default: 
			    		break;
				}	
			}
		}
		
		result = result + (5*wTurm + 3*wSpringer + 3*wLaeufer + 1000*wKoenig + 9*wDame + 1*wBauer);
		result = result - (5*sTurm + 3*sSpringer + 3*sLaeufer + 1000*sKoenig + 9*sDame + 1*sBauer);
		
		if(!game.iAmWhite) result *= -1;
		
		this.points = result;
		digitX2 = Math.abs(points)/10;
		digitX1 = Math.abs(points)%10;
	}
	
	public void whiteWin() {
		if(game.iAmWhite) 	points =  200;
		else 				points = -200;
	}
	
	public void blackWin() {
		if(!game.iAmWhite)	points =  200;
		else 				points = -200;
	}
	
	
	public void render(Graphics g) {
	
		//actually drawing score
		g.setFont(new Font("Impact", Font.PLAIN, 64)); 
		g.setColor(new Color(36, 7, 7));
		byte[] pointsBytes 	 = ("" + points).getBytes();
		g.drawBytes(pointsBytes, 0, pointsBytes.length, 1460-200, 370 );
		
		//draws win screens
		if(points > 100  &&  game.iAmWhite) {
			g.drawImage(Assets.whiteWon, 	0, 0, null); 
			game.clock.stop();
		}
		if(points < -100 &&  game.iAmWhite) {
			g.drawImage(Assets.blackWon, 	0, 0, null); 
			game.clock.stop();
		}
		if(points > 100  && !game.iAmWhite) {
			g.drawImage(Assets.blackWon, 	0, 0, null);
			game.clock.stop();
		}
		if(points < -100 && !game.iAmWhite) {
			g.drawImage(Assets.whiteWon, 	0, 0, null); 
			game.clock.stop();
		}

		if(patt) {
			g.drawImage(Assets.patt, 		0, 0, null); 
			game.clock.stop();
		}
		
		//draws slayn pieces
		drawGFX(g, Assets.bauerW,		80, 1075, 430, maxwBauer-wBauer);
		drawGFX(g, Assets.bauerSC,		80, 1275, 430, maxsBauer-sBauer);
		drawGFX(g, Assets.springerW,	80, 1075, 500, maxwSpringer-wSpringer);
		drawGFX(g, Assets.springerSC,	80, 1275, 500, maxsSpringer-sSpringer);
		drawGFX(g, Assets.laeuferW,		80, 1075, 570, maxwLaeufer-wLaeufer);
		drawGFX(g, Assets.laeuferSC,	80, 1275, 570, maxsLaeufer-sLaeufer);
		drawGFX(g, Assets.turmW,		80, 1075, 640, maxwTurm-wTurm);
		drawGFX(g, Assets.turmSC,		80, 1275, 640, maxsTurm-sTurm);
		drawGFX(g, Assets.dameW,		80, 1075, 710, maxwDame-wDame);
		drawGFX(g, Assets.dameSC,		80, 1275, 710, maxsDame-sDame);
		
		//loss/win/patt-sounds
		if(points >  100 && !soundPlayed) { ps.playSound("res/sounds/win2.wav" ); soundPlayed = true;}
		if(points < -100 && !soundPlayed) { ps.playSound("res/sounds/lose2.wav"); soundPlayed = true;}
		if(patt 		 && !soundPlayed) { ps.playSound("res/sounds/patt3.wav"); soundPlayed = true;}
		
		if(!soundPlayed) ts = System.currentTimeMillis();
		double ts2 = System.currentTimeMillis();
		
		//puts State into Review after 2s
		if(ts2-ts > 2000) game.setReview();		
	}
	
	public void drawGFX(Graphics g, Image img, int scale, int x, int y, int amount) {
		img = img.getScaledInstance(scale, scale, scale);
		
		for(int i = 0; i < amount; i++) {
			g.drawImage(img, 10+x+ (100/amount)*(i) , y, null);
			//                  
		}
	}
}

