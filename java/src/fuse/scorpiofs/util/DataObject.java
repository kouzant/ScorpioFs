package fuse.scorpiofs.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.rmi.Naming;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fuse.scorpiofs.ScorpioFS;

import unipi.p2p.chord.Constants;
import unipi.p2p.chord.Finger;
import unipi.p2p.chord.RemoteChordNode;
import unipi.p2p.chord.Storage;
import unipi.p2p.chord.util.Util;

public class DataObject implements Serializable {
	private static final Log log = LogFactory.getLog(DataObject.class);

	public byte [] theData = null;
	private DataList parentList;
	//private RemoteChordNode localChordObj;
	//private byte [] theData;
	private String tempID = null;
	public BigInteger dataID;
	//private BigInteger dataID;
	private int partSize = 1024*1024;
	private int bytesFilled;
	private long firstByte;
	private String fileOnDisk = null;
	private String filename;
	private int partNumber;
	private static String tmpPath = "/tmp/";
	private boolean onDisk = false;
	public boolean onNetwork = false;
	ReentrantReadWriteLock rwl;
	public DataObject(long byteNumber){
		theData = new byte[partSize];
	
		firstByte = byteNumber;
		bytesFilled=0;
		rwl = new ReentrantReadWriteLock();		
	}
	public void writeBytes(ByteBuffer buffer, long offset){
		log.info("called writeBytes");
		rwl.writeLock().lock();
		/*if (onDisk == true){
			fromDisk();
		}
		*/
		if (theData == null){
			if (onDisk == true){
				fromDisk();
			}
			else{
				fromNetwork();
			}
		}
		int i=0;
		while (buffer.hasRemaining() && ((int)(offset - firstByte)+i<theData.length)){
			
			theData[(int)(offset - firstByte) + i] = buffer.get();
			i++;
			}
		if (((int)(offset - firstByte) + i) > bytesFilled){
			bytesFilled = (int)(offset - firstByte) + i;
		}
		if (bytesFilled == theData.length){
			//toDisk();
			toNetwork();
		}
		rwl.writeLock().unlock();
		
	}
	public void readBytes(ByteBuffer buffer, long offset){
		rwl.readLock().lock();
		/*if (onDisk == true){
			fromDisk();
		}*/
		if (theData == null){
			if (onDisk == true){
				fromDisk();
			}
			else{
				fromNetwork();
			}
		}
		
		int i=0;
		while (buffer.remaining() >0 && ((int)(offset - firstByte + i)<bytesFilled)){
			//buffer.put(i,theData[(int)(offset - firstByte) + i]);
			buffer.put(theData[(int)(offset - firstByte) + i]);
			i++;
		}
		
		rwl.readLock().unlock();
		
		
	}
	public long getFirstByte(){
		return firstByte;
	}
	public int getSize(){
		return partSize;
	}
	public int getBytesFilled(){

		return bytesFilled;
	}
	public void setBytesFilled(int bytes){
		rwl.writeLock().lock();
		bytesFilled = bytes;
		byte[] xxx = new byte[partSize];
		for (int i= 0; i<bytesFilled; i++){
			xxx[i]=theData[i];
		}
		theData = xxx;
		rwl.writeLock().unlock();
	}
	public void toDisk(){
		rwl.writeLock().lock();
		if (onDisk){
			rwl.writeLock().unlock();
			return;
		}
		if (fileOnDisk == null)
		{
			try {
				fileOnDisk = new String (MD5sum.MD5(filename) + "." + String.valueOf(partNumber));
			} catch (Exception e) {
				e.printStackTrace();
			} 
		}
		try {
			FileOutputStream out = new FileOutputStream(tmpPath+fileOnDisk);
			out.write(theData);
			out.flush();
			out.close();
			onDisk = true;
			theData = null;
			System.gc();
			parentList.removeOnRAM(this);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		rwl.writeLock().unlock();
	}
	void fromDisk(){
		
		theData = new byte[partSize];
		try {
			FileInputStream in = new FileInputStream(tmpPath+fileOnDisk);
			in.read(theData);
			onDisk = false;
			in.close();
			parentList.addOnRAM(this);
			File partFile = new File(tmpPath+fileOnDisk);
			partFile.delete();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public void setFilename(String thepath){
		this.filename= new String(thepath);
	}
	public void setPartNumber(int partNo){
		this.partNumber=partNo;				
	}
	public boolean onDisk(){
		return onDisk;
	}
	public void setDatalist(DataList datalist){
		this.parentList = datalist;
	}
	public void toNetwork(){
		RemoteChordNode localChordObj = parentList.getLocalChordObj();
		if ((this.onNetwork) && (this.dataID != null)){
			onNetwork = true;
			theData = null;
			parentList.removeOnRAM(this);
			System.gc();
				return;
			}
		/*if (this.theData == null)
			try {
				
				if (onDisk == true){
					fromDisk();
				}
				else{
					fromNetwork();
				}
				throw new Exception("Null DATA!!!!! " + this.dataID);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}*/
		if ((onDisk == true) && (this.theData == null)){
			log.info("fromDisk()");
			fromDisk();
		}
		
		Storage stor_obj = new Storage(theData);
		stor_obj.setReplicationFactor(Constants.REPLICATION_FACTOR);
		
		dataID = stor_obj.getID();
		
		Finger target_finger;
		try {
			log.info("Just before finding a successor");
			target_finger = localChordObj.findSuccessor(dataID);
			RemoteChordNode target_node = (RemoteChordNode) Naming.lookup("rmi://" + target_finger.toString() + "/unipi.p2p.chord.ChordNode");
			if (!target_node.hasKey(dataID)){
				log.info("Storing chunk at target node");
				target_node.put(stor_obj);
			}
			onNetwork = true;
			theData = null;
			parentList.removeOnRAM(this);
			stor_obj = null;
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.gc();
	//	RemoteChordNode target_node = localChordObj.getRemoteChordNode(target_finger);
	
		/* create Storage obj
		 * get id
		 * Store id
		 * find successor
		 * Get remote Chord Object
		 * put the data
		 * theData = null;
		 * remove part file from disk */
	}
	public void fromNetwork(){
		RemoteChordNode localChordObj = parentList.getLocalChordObj();
		Storage stor_obj;
		Finger target_finger;
		try {
			if (this.dataID == null) throw new Exception("id = null!!!");
			//if (this.theData == null) throw new Exception("data = null!!!");
			
			//loop Constants.REPLICATION_FACTOR
			target_finger = localChordObj.findSuccessor(dataID);
			RemoteChordNode target_node = (RemoteChordNode) Naming.lookup("rmi://" + target_finger.toString() + "/unipi.p2p.chord.ChordNode");
			if (target_node.hasKey(dataID)) {
				stor_obj = target_node.get(dataID);
				log.info("Found first on: "+target_node.getIPAddress());
				theData = stor_obj.getData();
				parentList.addOnRAM(this);
			}
			else{
				BigInteger target_id = dataID;
				for (int i=0; i<Constants.REPLICATION_FACTOR; i++){
					target_id = Util.integerValue(target_id.toString());
					target_finger = localChordObj.findSuccessor(target_id);
					target_node = (RemoteChordNode) Naming.lookup("rmi://" + target_finger.toString() + "/unipi.p2p.chord.ChordNode");
					if (target_node.hasKey(dataID)) {
						stor_obj = target_node.get(dataID);
						log.info("Found on: "+target_node.getIPAddress());
						theData = stor_obj.getData();
						parentList.addOnRAM(this);
						break;
					}
				}
				
			}
			//System.out.println(target_node.toString());
			/*
			 * increase part popularity by let's say 50.
			 */
			
			//endloop
		} catch (Exception e) {
			// TODO Auto-generated catch block
			
		}
	}
	public boolean onNetwork(){
		return onNetwork;
	}
	public void increasePopularity(int points){
		Finger target_finger;
		RemoteChordNode localChordObj = parentList.getLocalChordObj();
		try {
			target_finger = localChordObj.findSuccessor(dataID);
			RemoteChordNode target_node = (RemoteChordNode) Naming.lookup("rmi://" + target_finger.toString() + "/unipi.p2p.chord.ChordNode");
			if (!target_node.hasKey(dataID)){
				target_node.increasePopularity(dataID, points);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	
	}

	public void delete() {
		Finger target_finger;
		RemoteChordNode localChordObj = parentList.getLocalChordObj();
		try {
			target_finger = localChordObj.findSuccessor(dataID);
			RemoteChordNode target_node = (RemoteChordNode) Naming
					.lookup("rmi://" + target_finger.toString()
							+ "/unipi.p2p.chord.ChordNode");
			if (target_node.hasKey(dataID)) {
				target_node.delete(dataID);
				theData = null;
				System.gc();
			}

		} catch (Exception e) {
			//e.printStackTrace();

		}

	}

}
