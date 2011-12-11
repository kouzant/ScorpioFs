package unipi.p2p.chord;

import java.io.File;
import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

public class Statistics implements Runnable {
	private ChordNode chordNode;
	
	public Statistics(ChordNode chordNode){
		this.chordNode=chordNode;
	}
	@Override
	public void run() {
		Iterator<String> hashChunks;
		int chunkCounter=0;
		File tmpFile;
		long totalSize=0;
		
		try {
			while(true){
				hashChunks=chordNode.getHashChunks();
				while(hashChunks.hasNext()){
					chunkCounter++;
					tmpFile=new File(hashChunks.next());
					totalSize=totalSize+tmpFile.length();
					tmpFile=null;
				}
				hashChunks=null;
				//Convert from bytes to megabytes
				totalSize=totalSize/1048576;
				System.out.println("Total number of chunks: "+chunkCounter);
				System.out.println("Total disk usage: "+totalSize+"MB");
				totalSize=0;
				chunkCounter=0;
				System.out.println("Clients who have stored data: "+chordNode
						.getStoringList().size());
				System.out.println(chordNode.getStoringList());
				System.out.println("Clients who have retrieved data: "+chordNode
						.getRetrievingList().size());
				System.out.println(chordNode.getRetrievingList());
				Thread.yield();
				//Every ten minutes display the statistics
				//TimeUnit.MINUTES.sleep(10);
				TimeUnit.SECONDS.sleep(10);
			}
		} catch(InterruptedException e){
			e.printStackTrace();
		} catch(RemoteException e){
			e.printStackTrace();
		}
	}
}
