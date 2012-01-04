package unipi.p2p.chord.util.console;

import java.rmi.registry.Registry;

import unipi.p2p.chord.ChordNode;
import unipi.p2p.chord.Replicator;

public class NodeInfo {
	private Replicator replicator;
	private ChordNode chordobj;
	private int servicePort;
	private Registry registry;
	
	public NodeInfo(Replicator replicator, ChordNode chordobj, int servicePort,
			Registry registry){
		this.replicator = replicator;
		this.chordobj = chordobj;
		this.servicePort = servicePort;
		this.registry = registry;
	}
	
	public Replicator getReplicator(){
		return replicator;
	}
	public ChordNode getChordobj(){
		return chordobj;
	}
	public int getServicePort(){
		return servicePort;
	}
	public Registry getRegistry(){
		return registry;
	}
}
