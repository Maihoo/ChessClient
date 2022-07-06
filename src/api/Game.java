package api;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferStrategy;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import gameClasses.Clock;
import gameClasses.PointCounter;
import gameClasses.Field;
import server.ClientManager;
import states.GameState;
import states.GameOnlineState;
import states.ChooseFigureState;
import states.MenuState;
import states.ReviewState;
import states.ViewerState;
import states.States;


public class Game implements Runnable {
	
	//Bools
	public  boolean ableToRedo, waitingForRedo, willHaveToUpdate, eWantsRedo, isConnected, spReady, isMyTurn, iAmWhite, willBeViewer, clockIsRunning, isOffline, whiteWillWin, blackWillWin, running; 
	//Ints
	public int width, height, ticks, resultFPS;
	public static double FPS = 60;
	public int from = -1;
	public int to 	= -1;
	//doubles
	double ts = System.currentTimeMillis();
	double ts2 = System.currentTimeMillis();

	//Strings
	public String figureChoosen = "";
	public String title;
	//States
	public States menuState, gameState, gameOnlineState, chooseFigureState, viewerState, reviewState;
	//Input
	public KeyManager keyManager;
	public MouseManager mouseManager;
	//Others
	private Display display;
	private BufferStrategy bs;
	public  ClientManager cm = null;	
	public  Thread thread;
	public  Graphics g;
	public  Field[] field = new Field[64];
	public  Clock clock;
	public  PointCounter pc;
	
	public Game(String title, int width, int height) {
		this.width = width;
		this.height = height;
		this.title = title;
		keyManager = new KeyManager();
		mouseManager = new MouseManager();
	}
	
	public Point getMP() {
		return display.getMP();
	}
	
	private void init() {
		
		for(int i = 0; i < 8; i++) {
			for(int j = 0; j < 8; j++) {
				field[(8*i)+j] = new Field(this, "0", false, false, i, j);
			}
		}
		
		display = new Display(title, width, height);
		display.getFrame().addKeyListener(keyManager);
		display.getFrame().addMouseListener(mouseManager);
		display.getFrame().addMouseMotionListener(mouseManager);
		display.getCanvas().addMouseListener(mouseManager);
		display.getCanvas().addMouseMotionListener(mouseManager);
		Assets.init();
		
		menuState 			= new MenuState(this);
		gameState 			= new GameState(this);
		gameOnlineState 	= new GameOnlineState(this);
		chooseFigureState 	= new ChooseFigureState(this);
		viewerState 		= new ViewerState(this);
		reviewState 		= new ReviewState(this);
		
		menuState.init();
		States.setState(menuState);
	}
	
	public void giveFieldToReviewMemory(Field[] input) {
		reviewState.addField(input);
	}
	
	public void redo() {
		try {
			cm.redo();
			System.out.println("send redo to server in game. ");
		}catch (Exception e) {
			System.out.println("redo wasn't possible in game. ");
		}
	}
	
	private void tick() {
		
		ts2 = System.currentTimeMillis();
		
		if(ticks % 50 == 0) {
			resultFPS = (int) (50000/(ts2-ts));
			ts = System.currentTimeMillis();
		}
		
		ticks++;
		
		keyManager.tick();
		if(States.getState() != null) {
			States.getState().tick();
		}
		if(whiteWillWin) pc.whiteWin();
		if(blackWillWin) pc.blackWin();
	}
	
	public void render() {
		bs = display.getCanvas().getBufferStrategy();
		if(bs == null) {
			display.getCanvas().createBufferStrategy(3);
			return;
		}
		g = bs.getDrawGraphics();
		//Clear Screan
		g.clearRect(0, 0, width, height);
		//Draw Here!
		
		if(States.getState() != null) {
			States.getState().render(g);
		}
		
		if(clockIsRunning) {
			clock.render(g);
		}
		try {
			pc.render(g);
		} catch (Exception e) {}
		
		g.setFont(new Font("Arial", Font.PLAIN, 15)); 
		g.setColor(Color.WHITE);
		byte[] data = (resultFPS + " fps").getBytes();
	    g.drawBytes(data, 0, data.length, width-60, 20);

		//End Drawing
		bs.show();
		g.dispose();
	}
	
	public void run() {
		
		init();

		double fps = FPS;
		double timePerTick = 1000000000/fps;
		double delta = 0;
		long now;
		long lastTime = System.nanoTime();
		long timer = 0;
		//int ticks = 0;
		
		
		while(running) {
			timePerTick = 1000000000/(FPS);
			now = System.nanoTime();
			delta += (now - lastTime) / timePerTick;
			timer += now - lastTime;
			lastTime = now;
			
			if(delta >= 1){
			tick();
			render();
			delta--;
			}
			
			if(timer >= 1000000000) {
				//ticks = 0;
				timer = 0;
			}			
		}
		
		stop();
		
	}
	
	public void giveToPC(){
		if(!iAmWhite) {
			switch (figureChoosen) {
		   		case "turm":
		    		pc.maxwTurm++;
		    		break;
		   		case "springer":
		   			pc.maxwSpringer++;
		    		break;
		    	case "laeufer":
		    		pc.maxwLaeufer++;
					break;
		   		case "dame": 
		   			pc.maxwDame++;
		    		break;
		    	default: 
		    		break;
			}
			pc.maxwBauer--;
		}
		if(iAmWhite) {
			switch (figureChoosen) {
		   		case "turm":
		    		pc.maxsTurm++;
		    		break;
		   		case "springer":
		   			pc.maxsSpringer++;
		    		break;
		    	case "laeufer":
		    		pc.maxsLaeufer++;
					break;
		   		case "dame": 
		   			pc.maxsDame++;
		    		break;
		    	default: 
		    		break;
			}
			pc.maxsBauer--;
		}
	}
	
	public void chooseFigure() {
		figureChoosen = "";
		States.setState(chooseFigureState);
		chooseFigureState.init();
		while(!(figureChoosen.length() > 2)) {tick(); render();}
		//return figureChoosen;
	}
	
	public boolean askIfIsMyTurn() {
		if(isOffline) return true;
		try {
			if(cm.askIfWhitesTurn() == iAmWhite) { 
				System.out.println("YES, IT'S MY TURN!!!");
				this.isMyTurn = true;
				return true;
			}
			//System.out.println("NO, IT'S NOT MY TURN!!!");
			this.isMyTurn = false;
			return false;
		}
		catch (Exception e) {
			System.out.println("askIfIsMyTurn didnt manage to call cm.askIfWhitesTurn()");
			return false;
		}
		
	}
	
	public void patt() {
		pc.patt = true;
		if(isOffline) return;
		try {
			cm.patt();
		}
		catch(Exception e) {}
	}
	
	public void setGame()  {
		if(!clockIsRunning) {iAmWhite  = true; isMyTurn  = true;}
		
		isOffline = true;
		
		try {
			if(isConnected) cm.notReady();
		} catch (Exception e) {}
		
		if(!clockIsRunning) {
			clock = new Clock(this);
			pc = new PointCounter(this);
		}
		clockIsRunning = true;
		States.setState(gameState);
	}
	
	public void setOnlineGame() {
		if(willBeViewer) setViewer();
		else {
			if(isConnected && spReady) {
				if(!clockIsRunning) {
					clock = new Clock(this);
					pc = new PointCounter(this);
				}
				States.setState(gameOnlineState);
				clockIsRunning = true;}
		}
	}
	
	public void setReview() {
		System.out.println("setReview set");
		States.setState(reviewState);
		this.pc = null;
	}
	
	public void setViewer() {
		States.setState(viewerState);
	}
	
	public void connectToServer() {
		if(isOffline) return;
		
		System.out.println("cts called");
		cm = new ClientManager(this);
		try {
			cm.initiateConnection();
			isConnected = cm.isConnected;
			iAmWhite = cm.askIfIAmWhite();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("initation of connection failed in Game");
		}
		System.out.println("isConnected got set to: " + cm.isConnected);
	}
	
	public void checkSPReady() throws IOException {
		if(isOffline) return;
		spReady = cm.checkSPReady();
	}
	
	public void resetKM() {
		keyManager.reset();
	}
	
	public void resetMM() {
		mouseManager.reset();
	}
	
	public static Object[] reverse(Object[] arr) {
        List<Object> list = Arrays.asList(arr);
        Collections.reverse(list);
        return list.toArray();
    }
	
	public Field[] getField() {
		if(isOffline) {
			if(!iAmWhite) {
				Field[] tempField = new Field[64];

				for(int i = 0; i < 64; i++) {
					tempField[i] = new Field(this, field[i].character, field[i].white, field[i].black, field[i].x, field[i].y);
					tempField[i].wasMoved		= field[i].wasMoved;
					tempField[i].enPassanteable	= field[i].enPassanteable;					//tempField = field KLAPPT SAFE
				}
			
				for(int i = 0; i < 64; i++) {
					field[i].character 		= tempField[63-i].character;
					field[i].white 			= tempField[63-i].white;
					field[i].black 			= tempField[63-i].black;
					field[i].wasMoved		= tempField[63-i].wasMoved;
					field[i].enPassanteable	= tempField[63-i].enPassanteable;	
				}
			}
			return this.field;
		}
		ableToRedo = true;
		
		System.out.println("GETFIELD was run.");
		try {
			this.field = stringToField(cm.getField());
		}
		catch(Exception e) {
			System.out.println("MEEEEEEEEEEEEEEEE");
		}
		return field;
	}
	
	public void setField(Field[] field) throws IOException {
		this.field = field;
		if(isOffline) {
			iAmWhite = !iAmWhite;
			pc.setPoints(field);
			String temp = clock.getEnemyTime();
			clock.setEnemyTime(clock.getMyTime());
			clock.setMyTime(temp);
			return;
		}
		cm.sendField(fieldToString(field));
	}
	
	public void initField(Field[] field) {
		this.field = field;
	}
	
	public Field[] stringToField(String input) {
		Field[] tempField = new Field[64];
		for(int i = 0; i < 8; i++) {
			for(int j = 0; j < 8; j++) {
				tempField[(8*i)+j] = new Field(this, "0", false, false, j, i);
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
		if(clockIsRunning) clock.setEnemyTime(tempString[64]);
		from = Integer.parseInt(tempString[65]);
		to 	 = Integer.parseInt(tempString[66]);
		
		return tempField;
	}
	
	public void calcPoints() {
		pc.setPoints(field);
		if(whiteWillWin) pc.whiteWin();
		if(blackWillWin) pc.blackWin();
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
		
		if(clockIsRunning) result += clock.getMyTime();
		else result += "1.0.0.0";
		
		result += ":";
		result += from;
		result += ":";
		result += to;
		
		return result;
	}
	
	public void returnToGame() {
		if(isOffline) setGame();
		else setOnlineGame();
	}
	
	public synchronized void start() {
		if(running)
			return;
		running = true;
		thread = new Thread(this);
		thread.start();
	}
	
	public synchronized void stop() {
		if(!running)
			return;
		try {
			thread.join();
			try {
				cm.quit();
			}catch (Exception e) {}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}