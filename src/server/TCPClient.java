package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.UnknownHostException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


public class TCPClient {
	private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    ClientManager cm;
    
    public TCPClient(ClientManager cm) {
    	this.cm = cm;
    }
    
    public void startConnectionOld(String adress, int port) throws UnknownHostException, IOException {
    	System.out.println("try taking connection");
    	clientSocket = new Socket(adress, port);
    	System.out.println("uhh1");
    	out = new PrintWriter(clientSocket.getOutputStream(), true);
    	System.out.println("uhh2");
    	in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        System.out.println("uhh3");
    }
    
    public boolean askForEnemyRedo() throws IOException {
    	out.println("eRedo?");
    	String resp = in.readLine();
        if(resp.equalsIgnoreCase("")) {
        	resp = in.readLine();
        }
        if(resp.equalsIgnoreCase("1")) return true;
        else cm.game.eWantsRedo = false;
        return false;
    }
    
    public void redo() throws IOException {
    	cm.game.waitingForRedo = true;
    	out.println("redo");
    	String resp = in.readLine();
        if(resp.equalsIgnoreCase("")) {
        	resp = in.readLine();
        }
        //if(resp.equalsIgnoreCase("got it"))
        if(resp.equalsIgnoreCase("do it") && cm.game.isMyTurn) {
        	cm.game.waitingForRedo = false;
        	cm.game.field = cm.game.getField();
        	cm.game.askIfIsMyTurn();
        	cm.game.willHaveToUpdate = true;
        	cm.game.isMyTurn = false;
        	cm.game.ableToRedo = false;
        }
        
        
        
        System.out.println("told Server want redo. Answer: " + resp);
    }
    
    public void notReady() throws IOException {
        out.println("notReady");
        String resp = in.readLine();
        if(resp.equalsIgnoreCase("")) {
        	resp = in.readLine();
        }
        System.out.println("told Server I am not ready. Answer: " + resp);
    }
    
    public void startConnection(String adress, int port) {
    	Thread socketThread=new Thread() {
    		  public void run() {
    		    try {
    		      clientSocket = new Socket(adress, port);
    		      out = new PrintWriter(clientSocket.getOutputStream(), true);
    		      in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    		    }
    		    catch (Exception e) {
    		      System.out.println("Socket couldn't be created");
    		    }
    		  }
    		};
    		socketThread.start();

    		try {
    		  socketThread.join(2000); // two seconds socket timeout in construction
    		} catch (Exception e) {
    		  e.printStackTrace();
    		}

    		// At this point either two seconds has passed,// or the socketThread died a normal death,// or current thread was interrupted.// Either way, it's ok to continue.
    		
    }
    
    public void sendPatt() throws IOException {
    	out.println("patt");
    	String resp = in.readLine();
    	if(resp.equalsIgnoreCase("")) {
    		resp = in.readLine();
    	}
    }

    public void quit() throws IOException {
    	System.out.println("QUIT");
        out.println("QUIT");
    }
    
    public String checkConnection() throws IOException {
    	System.out.println("checkConnection() was run in TCPClient");
    	out.println("connection running");
    	String resp = in.readLine();
    	System.out.println("respone: " + resp);
    	cm.setConnected(true);
    	if(resp.equalsIgnoreCase("connection running. 1")) {
    		System.out.println("i am Client 1");
    	} else {
	    	if(resp.equalsIgnoreCase("connection running. 2")) {
	    		System.out.println("i am Client 2");
	    	}
	    	else {
	    		System.out.println("i am a VIEWER");
	    		cm.game.willBeViewer = true;
	    	}
    	}
    	
    	return resp;
    }

    public void stopConnection() throws IOException {
    	out.println(ClientManager.StopCode);
        in.close();
        out.close();
        clientSocket.close();
    }
    
    public boolean askIfIAmWhite() throws IOException {
    	out.println("color");
    	String resp = in.readLine();
    	if(resp.equalsIgnoreCase("")) {
    		resp = in.readLine();
    	}
    	if(resp.equalsIgnoreCase("youAreWhite")) {
    		System.out.println("youAreWhite got pasted"); 
    		cm.game.iAmWhite = true; 
    		return true;
    	}
    	else {
	    	System.out.println("youAreBlack got pasted");
	    	cm.game.iAmWhite = false;
	    	return false;
    	}
    }
    
    public boolean askIfWhitesTurn() throws IOException {
    	out.println("turn");
    	String resp = in.readLine();
    	if(resp.equalsIgnoreCase("")) {
    		resp = in.readLine();
    	}
    	if(resp.equalsIgnoreCase("patt")) cm.game.patt();
    	if(resp.equalsIgnoreCase("white")) return true;
    	return false;
    }
    
    public boolean checkSPReady() throws IOException{
		out.println("sp");
		System.out.println("checkedIfSPReady");
		String resp = in.readLine();
    	if(resp.equalsIgnoreCase("")) {
    		resp = in.readLine();
    	}
		if(resp.equals("1")) return true;
		System.out.println("wasn't ready. Answer from Server was: " + resp);
		return false;
	}
    
    public String getField() throws IOException{
    	out.println("field");
    	System.out.println("asked Server for field");
    	String resp = in.readLine();
    	if(resp.equalsIgnoreCase("")) {
    		resp = in.readLine();
    	}
    	if(resp.equals("patt")) cm.game.patt();
    	
    	return resp;
    }
    
	public void sendField(String tempFeld) throws IOException{
		out.println("setField");
		System.out.print("told Server setField is coming. ");
    	String resp = in.readLine();
    	if(resp.equalsIgnoreCase("")) {
    		resp = in.readLine();
    	}
    	System.out.println("Response: " + resp);
    	
    	if(resp.equalsIgnoreCase("waiting")) {
    		System.out.println("sending tempField...");
    		out.println(tempFeld);
    		resp = in.readLine();
    		if(resp.equalsIgnoreCase("")) {
        		resp = in.readLine();
        	}
    		System.out.println("Server responded with:" + resp);
    	}
    	System.out.println("sent field to Server");
    	giveUpTurn();
	}
	
	public void giveUpTurn() throws IOException {
		out.println("giveUpTurn");
		System.out.print("told Server to Change turn. ");
    	String resp = in.readLine();
    	if(resp.equalsIgnoreCase("")) {
    		resp = in.readLine();
    	}
    	System.out.println("Response: " + resp);
	}
	
	
}
