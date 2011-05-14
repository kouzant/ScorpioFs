package unipi.p2p.chord;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashSet;
import java.util.LinkedList;
import java.math.BigInteger;



/**
 * According to the Chord protocol each chord node provides some operations to every other node. 
 * These operations are provided through this interface. All remote operations of Chord
 * are implemented on top of the JAVA RMI protocol. 
 * 
 * @author agapios
 *
 */
public interface RemoteChordNode extends Remote {
	
	public HashSet<String> getStoringList() throws RemoteException;
	public HashSet<String> getRetrievingList() throws RemoteException;
	
	/**
	 * 
	 * 
	 * @return The Finger of the Chord node.
	 * @throws RemoteException
	 */
	public Finger getLocalNode() throws RemoteException;
	
	/**
	 * 
	 * 
	 * @param integerValue A BigInteger denoting the key of the item we are looking for. 
	 * @return The Finger of the node responsible for the integer value.
	 * @throws RemoteException
	 */
	public Finger findSuccessor(BigInteger integerValue) throws RemoteException;
	
	/**
	 * 
	 * 
	 * @param integerValue
	 * @return The Finger of the Chord node that immediately precedes integerValue
	 * @throws RemoteException
	 */
	public Finger findPredecessor(BigInteger integerValue) throws RemoteException;
	
	/**
	 * 
	 * 
	 * @return A Finger denoting the successor of this Chord node
	 * @throws RemoteException
	 */
	public Finger successor() throws RemoteException;
	
	/**
	 * 
	 * 
	 * 
	 * 
	 * @return A Finger denoting the predecessor of this Chord node.
	 * @throws RemoteException
	 */
	public Finger predecessor() throws RemoteException;
	
	/**
	 * 
	 * 
	 * 
	 * @param finger 
	 * @throws RemoteException
	 */
	public void setPredecessor(Finger finger) throws RemoteException;
	
	/**
	 * 
	 * 
	 * 
	 * @return True if this Chord node is alive. False otherwise.
	 * @throws RemoteException
	 */
	public boolean alive() throws RemoteException;
	
	/**
	 * 
	 * 
	 * 
	 * @return The finger table of this Chord node.
	 * @throws RemoteException
	 */
	public Finger[] getFingers() throws RemoteException;
	
	public String getListOfKnownNodes() throws RemoteException;
	
	public Finger closestPrecedingNode(BigInteger integerValue) throws RemoteException;
	
	//public String get(BigInteger key) throws RemoteException; //returns data from the hashtable
	public Storage get(BigInteger key) throws RemoteException; //returns data from the hashtable
	//public void put(BigInteger key,String data) throws RemoteException; //puts data in the hashtable
	public void put(Storage storageObj) throws RemoteException; //puts data in the hashtable
	public void notifyNode(Finger f) throws RemoteException;
	public void printHashtable() throws RemoteException;
	public boolean hasKey(BigInteger key) throws RemoteException;
	/**
	 * @return A List  containing the k succussors of this node. The list is used whenever
	 * a node fails to contact its current successor.
	 * @throws RemoteException
	 */
	public LinkedList getSuccessorList() throws RemoteException;
	public void increasePopularity(BigInteger itemKey, int points) throws RemoteException;
	public void delete(BigInteger itemKey) throws RemoteException;
	public String getIPAddress() throws RemoteException;
}
