package unipi.p2p.scorpiofs.util;

import java.rmi.RemoteException;

import unipi.p2p.chord.ChordNode;

public class ShutDownChord extends Thread{
		private ChordNode chordobj;
		public ShutDownChord(ChordNode chordobj){
			this.chordobj = chordobj;
			
		}
	
		public void run() {
			System.out.println("Control-C caught. Shutting down...");
			try {
				
				this.chordobj.setMetadataFile("/tmp/chord.hashtable");
			} catch (RemoteException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			this.chordobj.writeMetadataFileToDisk();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	
}
