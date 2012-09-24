package sound;

import java.io.File;

import java.io.IOException;

import javax.sound.sampled.*;

public class Sound {

	private static Sound singleton = new Sound();
	
	
	private Sound(){}
	
	public static Sound instanceOf(){
		return singleton;
	}
	
	public void playBattlefieldSound(){
		new PlaySound(new File("audio/battlefield1942.wav"));
	}
	
	public void playShotSound(){
		new PlaySound(new File("audio/boom.wav"));
	}
	
	private class PlaySound extends Thread{
		
		private File file = null;
		
		public PlaySound(File file){
			this.file = file;
			this.start();
		}
		
		public void run(){
			AudioInputStream din = null;
			try {
				AudioInputStream in = AudioSystem.getAudioInputStream(file);
				AudioFormat baseFormat = in.getFormat();
				AudioFormat decodedFormat = new AudioFormat(
						AudioFormat.Encoding.PCM_SIGNED,
						baseFormat.getSampleRate(), 16, baseFormat.getChannels(),
						baseFormat.getChannels() * 2, baseFormat.getSampleRate(),
						false);
				din = AudioSystem.getAudioInputStream(decodedFormat, in);
				DataLine.Info info = new DataLine.Info(SourceDataLine.class, decodedFormat);
				SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
				if(line != null) {
					line.open(decodedFormat);
					byte[] data = new byte[4096];
					// Start
					line.start();
					
					int nBytesRead;
					while ((nBytesRead = din.read(data, 0, data.length)) != -1) {	
						line.write(data, 0, nBytesRead);
					}
					// Stop
					line.drain();
					line.stop();
					line.close();
					din.close();
				}
				
			}
			catch(Exception e) {
				e.printStackTrace();
			}
			finally {
				if(din != null) {
					try { din.close(); } catch(IOException e) { }
				}
			}
		}
	}
}
