package api;
import java.io.File;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

public class PlaySound {

    private final int BUFFER_SIZE = 128000;
    private File soundFile;
    private AudioInputStream audioStream;
    private AudioFormat audioFormat;
    private SourceDataLine sourceLine;

    public void playSound(String filename){
    	    	
        String strFilename = filename;

        new Thread(new Runnable() {
        	 public void run() {
		        try {
		            soundFile = new File(strFilename);
		            audioStream = AudioSystem.getAudioInputStream(soundFile);
		        
		            audioFormat = audioStream.getFormat();
		
		        	DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
		        
		            sourceLine = (SourceDataLine) AudioSystem.getLine(info);
		            sourceLine.open(audioFormat);
		            sourceLine.start();
		            
		            int nBytesRead = 0;
		            byte[] abData = new byte[BUFFER_SIZE];
		            while (nBytesRead != -1) {
		                
		                nBytesRead = audioStream.read(abData, 0, abData.length);
		                
		                if (nBytesRead >= 0) {
		                    @SuppressWarnings("unused")
		                    int nBytesWritten = sourceLine.write(abData, 0, nBytesRead);
		                }
		            }
		
		            sourceLine.drain();
		            sourceLine.close();
		        } catch(Exception e) { }
        	 }
        }).start();
    }
}
