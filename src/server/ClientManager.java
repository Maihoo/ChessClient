package server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;

import api.Game;


public class ClientManager implements Runnable {

	public boolean isConnected = false;
	public boolean spReady = false;
	public static final String StopCode = "STOP";
	
	public TCPClient tcpclient = null;
    Game game;
    
    public ClientManager(Game game) {
    	this.game = game;
    }
    
    public boolean askForEnemyRedo() throws IOException {
    	return tcpclient.askForEnemyRedo();
    }
    
    public void redo() throws IOException{
    	tcpclient.redo();
    }
    
    public void notReady() throws IOException{
    	tcpclient.notReady();
    }
    
    public String getField() throws IOException{
    	return tcpclient.getField();
    }
    
    public void patt() throws IOException{
    	tcpclient.sendPatt();
    }
    
	public void initiateConnection() throws IOException {
		System.out.println("initiate Connection() was run");
		
        String ip = "31.16.166.251";
        int port  = 8888;
        
        System.out.println("IP: " + ip + ", Port: " + port);
		
		tcpclient = new TCPClient(this);
		tcpclient.startConnection(ip, 8888);
		
		System.out.println("server replied with: " + tcpclient.checkConnection());
	}
	
	public void sendPatt() throws IOException {
		tcpclient.sendPatt();
	}
	
	public void sendField(String tempFeld) throws IOException{
		tcpclient.sendField(tempFeld);
	}
	
	public void quit() throws IOException{
		tcpclient.quit();
	}
	
	public boolean askIfIAmWhite() throws IOException{
		return tcpclient.askIfIAmWhite();
	}
	
	public boolean askIfWhitesTurn() throws IOException {
		return tcpclient.askIfWhitesTurn();
	}
	
	public void setConnected(boolean val) {
		isConnected = val;
	}
	
	public boolean checkSPReady() throws IOException {
		return tcpclient.checkSPReady();
	}

	@Override
	public void run() {
		while(true) {
			
		}
		
	}
}
