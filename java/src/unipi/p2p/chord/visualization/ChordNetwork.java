package unipi.p2p.chord.visualization;

import java.rmi.RemoteException;
import java.util.Hashtable;
import java.util.Vector;

import org.apache.log4j.Logger;

import unipi.p2p.chord.ChordNode;
import unipi.p2p.chord.Finger;
import unipi.p2p.chord.RemoteChordNode;
import unipi.p2p.chord.util.Util;

/**
 * Each Chord node can visualize its current view of the chord ring. This class provides all the necessary information
 * for the visualization (e.g. all the fingers in the chord ring, routing table of each chord node, successors and predecessor).
 * Starting from a Chord node we obtain all the nodes in the system by following the successor pointers of each node. Each node is stored
 * in a vector and procedure is terminated when the successor pointer is equal to the starting chord node. At that time we have a snapsot
 * of the Chord ring.  
 * 
 * 
 * @author agapios
 *
 */
public class ChordNetwork {
	private static Logger log = Logger.getRootLogger();
	private Hashtable chordNetwork = new Hashtable();
	private RemoteChordNode startingNode = null;
	
	public ChordNetwork(RemoteChordNode cn) throws RemoteException{
		startingNode = cn;
		RemoteChordNode tempNode = startingNode;
		
		long start = System.currentTimeMillis();
		int i = 0;
		while(tempNode.successor() != null && !tempNode.successor().toString().
				equals(startingNode.getLocalNode().toString())) {
			//log.info("chordnet " + i++);
			chordNetwork.put(tempNode.successor().getBigIntValue(), tempNode.successor());
			tempNode = Util.getRemoteChordNode(tempNode.successor());
		}
		long end = System.currentTimeMillis();
		log.info("ChordNetwork created in " + (end - start) + " ms");
	}
	

	
	/**
	 * 
	 * 
	 * @return A Hashtable containing all the chord nodes(Fingers) in the Chord Ring
	 */
	public Hashtable getNodes() {
		return chordNetwork;
	}
	
	/**
	 * 
	 * 
	 * @param f A Chord node identified by Finger f
	 * @return The fingerTable of finger f
	 */
	public Finger[] getFingerTable(Finger f) throws RemoteException{
		return Util.getRemoteChordNode(f).getFingers();
	}
	
	/**
	 * 
	 * 
	 * 
	 * @param f A Chord node identified by Finger f
	 * @return The successor of finger f
	 * @throws RemoteException
	 */
	public Finger getSuccessor(Finger f) throws RemoteException {
		return startingNode.findSuccessor(f.getBigIntValue());
	}
	

	/**
	 * 
	 * 
	 * 
	 * @param f A Chord node identified by Finger f
	 * @return The predecessor of finger f
	 * @throws RemoteException
	 */
	public Finger getPredecessor(Finger f) throws RemoteException {
		return startingNode.findPredecessor(f.getBigIntValue());
	}

	
}
