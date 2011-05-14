package unipi.p2p.chord;

import java.io.File;
import java.math.BigInteger;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;


public class HashChecker implements Runnable {
	int numOfChunks;
	int interval;
	ChordNode chordobj;
	
	public HashChecker(ChordNode chordObject){
		this.numOfChunks = 10;
		this.interval = 1;
		this.chordobj = chordObject;
		Thread hashCheckerThread = new Thread(this, "hashCheckerThread");
		hashCheckerThread.setPriority(1);
		System.out.println("New HashChecker Thread: "+hashCheckerThread.getId());
		hashCheckerThread.start();
		
	}

	public void run() {
		//System.out.println("HashChecker thread started");
		long startTime = System.currentTimeMillis();
		// get a copy of the hashtable.
		Hashtable<BigInteger, String> tmpHash = (Hashtable<BigInteger, String>) this.chordobj.data_hash.clone();
		Object currentElement;
		String filename;
		BigInteger hash;
		Set hashSet = chordobj.data_hash.keySet();
		Iterator it = hashSet.iterator();
		for (int i = 0; i<hashSet.size(); i++) {
			
			filename = tmpHash.get(hashSet.toArray()[i]);
			//System.out.println("Checking " + filename);
			ObjectDiskIO objectReader = new ObjectDiskIO();
			try {
				Storage storageObj = (Storage) objectReader
						.loadObject(new File(filename));
				storageObj.calculateID();
				if (!(storageObj.getID().equals(hashSet.toArray()[i]))) {
					System.out.println("incorrect hash.");
					System.out.println(storageObj.getID());
					System.out.println(hashSet.toArray()[i] + "\n");
					//remove from hashtable
					chordobj.data_hash.remove(hashSet.toArray()[i]);
					//delete from disk
					File f = new File(filename);
					f.delete();
					
				}

			} catch (Exception e1) {
				// TODO Auto-generated catch block
				
				//e1.printStackTrace();
				System.out.println("HashChecker Exception!");
				//remove from hashtable
				chordobj.data_hash.remove(hashSet.toArray()[i]);
				//delete from disk (???)
				File f = new File(filename);
				f.delete();
			}
			try {
				Thread.sleep(interval);
			} catch (InterruptedException e2) {
				// TODO Auto-generated catch block
				System.out.println("HashChecker thread interrupted!");
				e2.printStackTrace();
			}

		}
		//System.out.println("HashChecker thread stopped, time = " + (System.currentTimeMillis() - startTime) + "ms");

	}
	
}
