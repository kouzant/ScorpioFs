package unipi.p2p.chord;

import java.math.BigInteger;
import java.rmi.RemoteException;
import java.util.Enumeration;
import java.util.Set;

import unipi.p2p.chord.util.Util;

public class Replicator implements Runnable {
	private int currentObj;
	private int replicationFactor = Constants.REPLICATION_FACTOR;
	private ChordNode chordObj;
	private boolean running = true;
	public Replicator(ChordNode localChord){
		chordObj = localChord;
		currentObj = 0;
		Thread replicationThread = new Thread(this, "replicationThread");
		replicationThread.start();
	}
	public void stopRunning(){
		this.running = false;
	}
	public void run(){
		while (running){
			//System.out.println("I Replicate!");
			replicateOnce(this.getObjToReplicate());
			try {
				Thread.sleep(Constants.REPLICATION_INTERVAL);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	
		}
	}
	private Storage getObjToReplicate(){
		Storage objToReplicate;
		Set hashSet = chordObj.data_hash.keySet();
		if (hashSet.size() <= currentObj){
			currentObj = 0;
		}
		if (hashSet.size() == 0){
			return null;
		}
		try {
			objToReplicate = chordObj.get((BigInteger)hashSet.toArray()[currentObj]);
			currentObj += 1;
			return objToReplicate;
			
		} catch (RemoteException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		return null;
		
		
		
	}
	private void replicateOnce (Storage obj){
			if (obj == null){
				return;
			}
			
			Finger targetFinger = findTargetNode(obj, this.replicationFactor);
			if (targetFinger != null){
				RemoteChordNode target_node;
			
			try {
				target_node = chordObj.getRemoteChordNode(targetFinger);
				target_node.put(obj);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//target_node.put(obj);
			}
	}
	private Finger findTargetNode (Storage obj, int replicationFactor){
		try {
				BigInteger target_id = obj.getID();
				Finger tempfinger = chordObj.findSuccessor(target_id);
				RemoteChordNode tempNode = chordObj.getRemoteChordNode(tempfinger);
				if (!tempNode.hasKey(obj.getID())){
					return tempfinger;
			
				}
				else{
					for (int i=0; i<replicationFactor; i++){
						target_id = Util.integerValue(target_id.toString());
						tempfinger = chordObj.findSuccessor(target_id);
						tempNode = chordObj.getRemoteChordNode(tempfinger);
						if (!tempNode.hasKey(obj.getID())){
							return tempfinger;
					
						}
					}
				}
			}
		
		catch (RemoteException e) {
			e.printStackTrace();
		}
		
		
		return null;
	}
}
