package unipi.p2p.scorpiofs.util;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import unipi.p2p.chord.ChordNode;
import unipi.p2p.chord.Replicator;
import unipi.p2p.chord.util.console.NodeInfo;
import unipi.p2p.chord.Constants;

public class ShutDownChord implements Callable<Integer>{
		private Replicator replicator;
		private ChordNode chordobj;
		private int servicePort;
		private Registry registry;
		
		public ShutDownChord(NodeInfo curNode){
			this.replicator = curNode.getReplicator();
			this.chordobj = curNode.getChordobj();
			this.servicePort = curNode.getServicePort();
			this.registry = curNode.getRegistry();
		}
	
		public Integer call() {
			try {
				
				this.chordobj.setMetadataFile("/tmp/chord.hashtable");
			} catch (RemoteException e1) {
				e1.printStackTrace();
			}
			this.chordobj.writeMetadataFileToDisk();
			this.chordobj.writeClientsList();
			try {
				Constants.statsThread = false;
				TimeUnit.SECONDS.sleep(1);
				this.chordobj.stopRunning();
				this.replicator.stopRunning();
				TimeUnit.SECONDS.sleep(5);
				Naming.unbind("rmi://" + "localhost" + ":" + servicePort + 
						"/unipi.p2p.chord.ChordNode");
	        	UnicastRemoteObject.unexportObject(registry, true);
	        	System.out.println("Chord node on port "+servicePort+" terminated");
			} catch (InterruptedException e0) {
				e0.printStackTrace();
			} catch (RemoteException e1) {
				e1.printStackTrace();
			} catch (MalformedURLException e2) {
				e2.printStackTrace();
			} catch (NotBoundException e3) {
				e3.printStackTrace();
			}
			
			return 1;
		}
}
