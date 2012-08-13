package unipi.p2p.chord;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import unipi.p2p.chord.util.Statistics;
import unipi.p2p.chord.util.Util;
import unipi.p2p.chord.visualization.ChordViewer;

public class ChordNode extends UnicastRemoteObject implements RemoteChordNode, Runnable{
	private static final long serialVersionUID = -713653815979171062L;
	private static final Logger log = Logger.getRootLogger();
	private static Statistics stats = new Statistics();
	private static String outputFolder;
	private String metadataFile;
	private String storingListFilename;
	private String retrievingListFilename;
	private Date startTime;
	private boolean running = true;
	HashSet<String> storingList=new HashSet<String>();
	HashSet<String> retrievingList=new HashSet<String>();
	LinkedList successorList = new LinkedList();
	Finger[] fingerTable = new Finger[Constants.IDENTIFIER_LENGTH];
	
	//Finger workingChordNode;
	
	Finger predecessor;

	Finger localNode;
	
	public Hashtable<BigInteger, String> data_hash = new Hashtable();
	public HashMap<BigInteger, Integer> dataPopularity;
	
	public HashSet<String> getStoringList() throws RemoteException{
		return storingList;
	}
	public HashSet<String> getRetrievingList() throws RemoteException{
		return retrievingList;
	}
	public void setStoringList(String ipAddress) throws RemoteException{
		storingList.add(ipAddress);
	}
	public void setRetrievingList(String ipAddress) throws RemoteException{
		retrievingList.add(ipAddress);
	}
	public String getIPAddress() throws RemoteException{
		return (String) this.localNode.getIPAddress();
	}
	public void setStoringListFilename(String storingListFilename){
		this.storingListFilename=storingListFilename;
	}
	public void setRetrievingListFilename(String retrievingListFilename){
		this.retrievingListFilename=retrievingListFilename;
	}
	public void stopRunning(){
		this.running = false;
	}
	public void setStartTime(Date startTime){
		this.startTime = startTime;
	}
	public Date getStartTime(){
		return startTime;
	}
	public ChordNode() throws RemoteException{
		super();
	}
	public ChordNode(Finger localNode, boolean startupNew, Finger workingChordNode) throws RemoteException{
		super();
		//java.rmi.server.
		outputFolder = "/tmp/"; // Sets storage dir to /tmp
		
		log.info("Initializing chord node with ip" + localNode.getIPAddress() + " and port " + localNode.getPort());
		this.localNode = localNode;
		if (startupNew) { // create a new ChordRing
			log.info("Attempting to create a new Ring...starting node is " + localNode.toString());
			
			for (int i = 0; i < fingerTable.length; i++) {
				fingerTable[i] = localNode;
			}
			successorList.add(0, localNode);
			predecessor = null;
		} else {
			try {
				//PropertyFileBootstrapper props = new PropertyFileBootstrapper("chord.properties");
			//	if (workingChordNode == null){
				//	workingChordNode = props.getFinger();
				//}
				if (workingChordNode == null) {
					System.out.println("An error occured while bootstapping the system. See the log file for details");
					System.exit(-1);
				}
				join(workingChordNode);
		
			} catch(Exception e) {
				log.fatal("Failed to initialize the propertyFileBootstrapper:" + e.toString());
				System.exit(-1);
			}
		}

		
	}
	
	public LinkedList getSuccessorList() throws RemoteException {
		return successorList;
	}
	
	public void run() {
		while (running) {
			checkPredecessor();
			stabilizeSuccessorList();
			stabilize();
			fixFingers();
			//saveRoutingState();

			writeMetadataFileToDisk();
	
			try {
				Thread.sleep(5000);
			} catch (InterruptedException iex) { }
			
		}
		
	}

	/**
	 * 
	 * 
	 * @param chordNode
	 */
	public void join(Finger chordNode) {
		try {
			log.info("Joining the Ring...using Finger " + chordNode.toString() + "("+ chordNode.getBigIntValue() + ")");
			predecessor = null;
			RemoteChordNode remoteNode = getRemoteChordNode(chordNode);
			fingerTable[0] = remoteNode.findSuccessor(localNode.getBigIntValue());
			log.info("Success in setting successor to " + fingerTable[0].toString());
			stabilizeSuccessorList();
			stabilize();
			//long start = System.currentTimeMillis();
			//long end = System.currentTimeMillis();
			//log.info("storing peer in " + (end - start)  + " ms");
			
		} catch(Exception e) {
			log.fatal("Exception while trying to join the ring (using finger" + chordNode.toString() + ")");
		}
	}
	
	public void stabilizeSuccessorList() {
		try {
			RemoteChordNode successor = getLiveSuccessor();
			LinkedList temp = successor.getSuccessorList();
			while(temp.remove(localNode)); //if the successorList contains instances of my finger delete them all
			
			Finger suc = successor.getLocalNode();
			temp.remove(suc); // if the successorList contains my successor delete it 
			temp.addFirst(suc);
		

			if (temp.size() == Constants.IDENTIFIER_LENGTH ) {
				temp.removeLast();
			}
				successorList = temp;
			//	successorList.addFirst(suc);
				//successorList.removeLast();
			//successorList = temp;
		} catch(Exception e) {
			log.error("Error while stabilizing successor list:" + e.toString());
		}
		
	}
	
	public void stabilize() {
		try {
			log.info("stabilizing......");
			//RemoteChordNode chordNode = getRemoteChordNode(fingerTable[0]);
			RemoteChordNode chordNode = getLiveSuccessor();
			Finger pred = chordNode.predecessor();
			if (pred != null) {
				if (Util.exBetween(pred.getBigIntValue(),
						localNode.getBigIntValue(), fingerTable[0].getBigIntValue())) {
					//log.info("The predecessor of my successor " + pred.toString() + " (" + pred.getBigIntValue()+ ") "+ fingerTable[0].toString() + " (" + fingerTable[0].getBigIntValue()+ ") ");
					fingerTable[0] = pred;
				}
			} 

			stats.incrStabilizeCalls();
			getRemoteChordNode(fingerTable[0]).notifyNode(localNode);
			log.info("Notified " + fingerTable[0].toString() + " that i am its predecessor");
		} catch (Exception e) {
			log.fatal(e.getMessage(), e);

		}
		
	}
	
	public void notifyNode(Finger f) throws RemoteException{
		log.info("Received notification from " + f.toString() + " that he should be my predecessor.");
		if ((predecessor == null)
				|| Util.exBetween(f.getBigIntValue(), predecessor.getBigIntValue(),
						localNode.getBigIntValue())) {
			predecessor = f;
			if (localNode.toString().equals(fingerTable[0].toString())) {
				fingerTable[0] = f;
			}
			log.info("Indeed. Setting predecessor to " + f.toString());
		}
	}

	public Finger findSuccessor(BigInteger integerValue) throws RemoteException {
	//	log.info("Received a successor request for " + integerValue.toString());
		if (localNode.equals(fingerTable[0])) {
			log.info("Returning my successor (" + fingerTable[0].toString()+ "=" + fingerTable[0].getBigIntValue() 
					+ ") since i am the only node in the system");
			return fingerTable[0];
		} else {
			Finger f = findPredecessor(integerValue);
			Finger sucF = getRemoteChordNode(f).successor();
			log.info("Finaly successor for " + integerValue.toString() + " is " + sucF.toString() + " (" + sucF.getBigIntValue() + ")");
			return sucF;
		}
	}
	
	public Finger findPredecessor(BigInteger integerValue) throws RemoteException {
		RemoteChordNode chordNode = this;
		//log.info("Attempting to find predecessor for " + integerValue.toString());
		while (!Util.inBetweenExLeft(integerValue, chordNode.getLocalNode().getBigIntValue(),
				chordNode.successor().getBigIntValue())) {
			log.info(" " + integerValue.toString() + " not in range (" + chordNode.getLocalNode().getBigIntValue() + ", " + 
					chordNode.successor().getBigIntValue() + "] nodes (" + chordNode.getLocalNode().toString() + "," + 
					chordNode.successor().toString() + "]");
			
			Finger  f = chordNode.closestPrecedingNode(integerValue);
			if (chordNode.getLocalNode().equals(f)) {
				log.info("Returning " + f.toString() + "(" + f.getBigIntValue()+ ") as predecessor for " + integerValue.toString());
				return f;
			} else {
				chordNode = getRemoteChordNode(f);
			}
		}
		
		Finger rf = chordNode.getLocalNode();
		log.info("" +
				"Returning " + rf.toString() + "(" + rf.getBigIntValue()+ ") as predecessor for " + integerValue.toString());
		
		return rf;
	}
	
	public Finger closestPrecedingNode(BigInteger integerValue) throws RemoteException{
		try {
			for (int i = fingerTable.length - 1; i >= 0; i--) {
				if (fingerTable[i] != null) {
					if (Util.inBetweenExLeft(fingerTable[i].getBigIntValue(), 
							localNode.getBigIntValue(), integerValue)) {
						log.info("Closest preceding node for " + integerValue.toString() + " is " + fingerTable[i].getBigIntValue().toString() + ")");
						return fingerTable[i];
					}
				}
			}
			log.info("Closest preceding node for " + integerValue.toString() + " from node " + getLocalNode().toString() + 
					"(" + getLocalNode().getBigIntValue() + ")");
			//return fingerTable[0];
			return getLocalNode();
		} catch (Exception e) {
			log.error("An exception occured while searching for closest preceding node:" + e.toString());
			return localNode;
		}
	}

	public Finger getLocalNode() throws RemoteException {
		return localNode;
	} 

	public Finger successor() throws RemoteException {
		return getLiveSuccessor().getLocalNode();
	}

	public Finger predecessor() throws RemoteException {
		return predecessor;
	}

	public void setPredecessor(Finger f) throws RemoteException {
		this.predecessor = f;

	}
	public void setOutputDirectory(String directory) throws RemoteException {
		outputFolder = directory;
		File f = new File(directory);
		if (!f.exists()) {
			f.mkdirs();
		}
		// here, i have to recreate the data_hash. data filenames are 40 hex chars long.
		//this.createDatahashFromDirectory(directory);

	}
	public void setMetadataFile(String filename) throws RemoteException {
		this.metadataFile = filename;
		File f = new File(metadataFile);
		if (!f.exists()) {
			File parent = null;
			parent = f.getParentFile();
			if (parent != null && !parent.exists()) {
				parent.mkdirs();
			}			
		}
		
	}
	public void writeMetadataFileToDisk(){
		ObjectDiskIO objectWriter = new ObjectDiskIO();
		try {
			objectWriter.saveObject(this.data_hash, new File(this.metadataFile));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void writeClientsList(){
		ObjectDiskIO objectWriter=new ObjectDiskIO();
		try{
			objectWriter.saveObject(storingList, new File(storingListFilename));
			objectWriter.saveObject(retrievingList, new File(retrievingListFilename));
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	public boolean alive() throws RemoteException {
		return true;
	}

	public Finger[] getFingers() throws RemoteException {
		return fingerTable;

	}

	public String getListOfKnownNodes() throws RemoteException {
		return null;
	}
	
	public RemoteChordNode getLiveSuccessor() throws RemoteException {
		stats.incrSuccessorListCalls();
		try {
			return getRemoteChordNode(fingerTable[0]);
		} catch(Exception e) {
			
		}
		
		for (int i = 0; i < successorList.size(); i++) {
			try {
				Finger f = (Finger)successorList.get(i);
				stats.incrSuccessorListCalls();
				RemoteChordNode stub = getRemoteChordNode(f);
				fingerTable[0] = f;
				
				return stub;
			} catch(Exception e) {
				//successorList.remove(i);
				log.info("trying to obtain a live successor");
			}
		}
		throw new RemoteException("Failed to find a live successor in the successor list");
	}

	public RemoteChordNode getRemoteChordNode(Finger f) throws RemoteException {
		stats.incrRemoteCalls();
		try {
			RemoteChordNode stub = (RemoteChordNode) Naming.lookup("rmi://"
					+ f.toString() + "/unipi.p2p.chord.ChordNode");
			stub.alive(); //throws exception if the RemoteChordNode is not alive
			return stub;
		} catch (Exception e) {
			throw new RemoteException(e.toString());
			//log.info("Cannot get remote object");
			
		}
	}
	
	
	public Statistics getStatistics() {
		stats.setRetrievingListSize(retrievingList.size());
		stats.setStoringListSize(storingList.size());
		try{
			stats.setIpAddr(InetAddress.getLocalHost().getHostAddress().toString());
		}catch(UnknownHostException ex){
			ex.printStackTrace();
		}
		Iterator<String> hashChunk = getHashChunks();
		File tmpFile = null;
		long totalSize = 0L;
		
		while(hashChunk.hasNext()){
			tmpFile = new File(hashChunk.next());
			totalSize += tmpFile.length();
			tmpFile = null;
		}
		stats.setTotalChunkSize(totalSize);
		stats.setStartTime(startTime);
		
		return stats;
	}
	
	public Iterator<String> getHashChunks(){
		Collection<String> chunkHash=data_hash.values();
		Iterator<String> itChunkHash=chunkHash.iterator();
		
		return itChunkHash;
	}
	/**
	 * Called periodically to check whether predecessor has failed
	 * 
	 * 
	 * 
	 *
	 */
	private void checkPredecessor() {
		if (predecessor != null) {
			try {
				stats.incrCheckPredecessorCalls();
				predecessor = getRemoteChordNode(predecessor).getLocalNode();
			} catch(Exception e) {
				log.debug("Could not reach predecessor:" + predecessor.toString() + ":" + e.toString());
				log.debug("Setting predecessor to null");
				predecessor = null;
			}
		}
	}
	


	public void fixFingers() {
		long start = System.currentTimeMillis();
		try {
			for (int i = 1; i < fingerTable.length; i++) {
				BigInteger temp = ((Constants.TWO.pow(i - 1)).add(localNode.getBigIntValue()))
						.mod(Constants.TWOPOWERM);
				stats.incrFixFingerCalls();
				Finger f = findSuccessor(temp);
				log.info(" " + i + " entry is " + f.toString() + "(" + f.getBigIntValue()+ ")");
				
				fingerTable[i] = f;
			}
			

		} catch (Exception e) {
			log.fatal(e.getMessage(), e);
		}
		long end = System.currentTimeMillis();
		log.info("Finished updating fingers in " + (end - start) + "ms");

	}

	public Storage get(BigInteger key) throws RemoteException{
		
		String filename = data_hash.get(key);
		ObjectDiskIO objectReader = new ObjectDiskIO();
		try {
			/*if (dataPopularity.containsKey(key)){
				int currentValue = dataPopularity.get(key);
				dataPopularity.put(key, currentValue + 50); //each request gets 50 points
			}*/
			Storage storageObj = (Storage)objectReader.loadObject(new File(filename));
			return storageObj;
		} catch (Exception e) {
			log.error("Could not retrieve object no. " + key);
		}
		return null;
		
	}

		public void put(Storage storageObj) throws RemoteException{ // isws na prepei na thn kanw boolean...
			
			//dataPopularity.put(storageObj.getID(), 0);
			// check if disk limit is reached
			ObjectDiskIO objectWriter = new ObjectDiskIO();
			try {
				String output=outputFolder+storageObj.getShaHex();
				objectWriter.saveObject(storageObj, new File(output));
				data_hash.put(storageObj.getID(), output);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		public void increasePopularity(BigInteger itemKey, int points) throws RemoteException{
			if (dataPopularity.containsKey(itemKey)){
				int currentValue = dataPopularity.get(itemKey);
				dataPopularity.put(itemKey, currentValue + points);
			}
		}
		public void delete(BigInteger itemKey) throws RemoteException{
			/*if (dataPopularity.containsKey(itemKey)){
				dataPopularity.put(itemKey, -1);
			}*/
			
			
			
			//check if hash contains itemKey, else return, notify next node???
			//get associated filename
			//delete associated file from disk
			//remove itemKey from hash
			//notify next node???
			//return
			log.info("Called delete");
			
			String filename = this.data_hash.get(itemKey);
			if (filename == null){
				return;
			}
			File f = new File(filename);
			f.delete();
			this.data_hash.remove(itemKey);
			
		}

	/*public void printHashtable() throws RemoteException{
		for (Enumeration e = data_hash.keys(); e.hasMoreElements();)
	    {
		System.out.println (data_hash.get(e.nextElement()));
	    }
	}*/
	
	public boolean hasKey(BigInteger key){
		return data_hash.containsKey(key);
		
	}
	public void printHashtable() throws RemoteException{
		Enumeration e = data_hash.keys();
		
		while( e. hasMoreElements() ){
			System.out.println( e.nextElement() );
		}
		System.out.println(data_hash.size());
	}
	
	private void replicate(Storage obj){
		try {
			RemoteChordNode target_node = getRemoteChordNode(successor());
			target_node.put(obj);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
	}
	private void createDatahashFromDirectory(String directory){ // it works, really!
		//pattern --> [0-9 a-f]{40}
		//open directory
		File contents[];
		File f = new File(directory);
		contents = f.listFiles();
		
		// [A-Fa-f0-9]{40}
		Pattern hexdigits = Pattern.compile("[A-Fa-f0-9]{40}");
		
		
		for (int i = 0; i < contents.length; i++){ //check every file against the pattern
			
			if (contents[i].isFile()){
				String filename = contents[i].toString().substring(contents[i].toString().lastIndexOf("/")+1);
				Matcher matcher = hexdigits.matcher(filename);
				if (matcher.matches()){
					
					//for each file that matches the pattern, create a dataobject and add it to the data_hash
					ObjectDiskIO objectReader = new ObjectDiskIO();
					try {
						Storage storageObj = (Storage)objectReader.loadObject(contents[i]);
						data_hash.put(storageObj.getID(), contents[i].toString());
						
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						
					}
				}
				
			}
			
		}
	}
	public void loadHashtableFromFile(){
		//if file exists, load it
		File metaDataFile=new File(this.metadataFile);
		if(metaDataFile.exists()){
			ObjectDiskIO objectReader = new ObjectDiskIO();
			try {
				this.data_hash = (Hashtable<BigInteger, String>) objectReader.loadObject(metaDataFile);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
			//check if files exist
			File f;
			for (Enumeration e = data_hash.keys(); e.hasMoreElements();){
				Object currentElement = e.nextElement();
				f = new File(data_hash.get(currentElement));
				if (!f.exists()){
					//System.out.println("Removing " + data_hash.get(currentElement));
					data_hash.remove(currentElement);
				}
			}
		}else{
			System.out.println("Could not find metadata file. Creating a new one.");
		}
	}
	
	public void loadClientsList(){
		File storingListFile=new File(storingListFilename);
		File retrievingListFile=new File(retrievingListFilename);
		if(storingListFile.exists() && retrievingListFile.exists()){
			ObjectDiskIO objectReader=new ObjectDiskIO();
			try{
				storingList=(HashSet<String>) objectReader.loadObject(storingListFile);
				retrievingList=(HashSet<String>) objectReader.loadObject(retrievingListFile);
			}catch(Exception e){
				e.printStackTrace();
			}
		}else{
			System.err.println("Could not find storingList file or retrievingList" +
					"file. Creating new ones.");
		}
	}
	public void saveRoutingState() {
		try {
			FileOutputStream fos = new FileOutputStream("RoutingState_" + Calendar.getInstance().getTime().toString() + ".txt");
			PrintWriter pw = new PrintWriter(fos);
			pw.println("ChordNode: " + this.getLocalNode().toString());
			pw.println("ChordID: " + this.getLocalNode().getBigIntValue());
			if (this.predecessor != null) {
				pw.println("Predecessor: " + predecessor.toString() + " " + predecessor.getBigIntValue());
			} else {
				pw.println("Predecessor: NULL");
			}
			if (successor() != null) {
				pw.println("Successor: " + successor().toString() + " " + successor().getBigIntValue());
			} else {
				pw.println("Successor: NULL");
			}
			
			pw.println("List of Successors (" + successorList.size() + " elements)");
			for (int i = 0; i < successorList.size(); i++) {
				Finger f = (Finger)successorList.get(i);
				pw.println(" " + i + ": " + f.getBigIntValue());
			}
			
			pw.println("------------------------------------------------------");
			
			pw.println("Routing Table");
			for(int i = 0; i < fingerTable.length; i++) {
				Finger f = fingerTable[i];
				if (f != null) {
					pw.println("Index: " + i + " Node: " + f.toString() + " ID: " + f.getBigIntValue());
				} else {
					break;
				}
			}
			pw.flush();
			pw.close();
			fos.close();
			
		} catch(Exception e) {
			log.error("An error occured while attempting to save the routing state of this ChordNode:" + e.toString());
		}
	}
	
	/**
	 * @param args
	 */
	

	
	
	public static void main(String[] args) {
		try {
			boolean flag = Boolean.parseBoolean(args[0]);
			String ip = args[1];
			int port = Integer.parseInt(args[2]);
			ChordNode obj = new ChordNode(new Finger(ip + ":" + port), flag, null);
			Registry registry = LocateRegistry.createRegistry(port);
			//.getRegistry("195.251.230.112");
			// Registry registry = LocateRegistry.createRegistry(1099);
			Naming.rebind("rmi://" + ip + ":" + port+ "/unipi.p2p.chord.ChordNode", obj);
			log.info("ChordNode at " + InetAddress.getLocalHost()
					+ "ready");
			Thread.currentThread().sleep(2000);
			Thread updateThread = new Thread(obj, "updateThread");
			updateThread.start();
			
			if (flag) {
				new ChordViewer(obj);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
