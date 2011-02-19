package fuse.scorpiofs.util;

import java.io.FileOutputStream;
import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import unipi.p2p.chord.RemoteChordNode;


import fuse.scorpiofs.ScorpioFS;

public class DataList implements Serializable {
// A list of Data Objects
	// Some methods on the Data Objects
	//private static final Log log = LogFactory.getLog(ScorpioFS.class);
	private  RemoteChordNode localChordObj;
	public LinkedList<DataObject> theList;
	private LinkedList<DataObject> onRAM;
	private int bytesOnRAM = 0;
	private int RAMLimit = 41943040;
	ReentrantReadWriteLock rwl;
	public DataList(){
		rwl = new ReentrantReadWriteLock();
		theList = new LinkedList<DataObject>();
		onRAM = new LinkedList<DataObject>();
	}
	public DataObject getDataObject(long offset){
		checkRAM();
		DataObject obj = null;
		for (int i=0; i<theList.size(); i++){
			obj = theList.get(i);
			if ( ( (int)offset >= obj.getFirstByte() ) && ( (int)offset < obj.getSize() + obj.getFirstByte() ) ){
				return obj;
			}
		}
		
		return null;
	}
	public void putDataObject(DataObject dataobj){
		checkRAM();
		DataObject obj;
		for (int i = 0; i<theList.size(); i++){
			obj = theList.get(i);
			if (obj.getFirstByte() == dataobj.getFirstByte()){
				theList.remove(i);
				break;
			}
		}
		
		theList.add(dataobj);
		dataobj.setPartNumber(theList.lastIndexOf(dataobj));
	}
	public int getFilesize(){
		int size = 0;
		DataObject obj;
		for (int i=0; i<theList.size(); i++){
			obj = theList.get(i);
			size = size + obj.getBytesFilled();
		}
		
		
		return size;
		
	}
	public void allToDisk(){
		DataObject obj;
		for (int i=0; i<theList.size(); i++){
			obj = theList.get(i);
			if (!obj.onDisk()){
				obj.toDisk();
			}
		}
	}
	public void allToDiskExcept(int except){
		DataObject obj;
		for (int i=0; i<theList.size(); i++){
			if (i == except){
				continue;
			}
			obj = theList.get(i);
			if (!obj.onDisk()){
				obj.toDisk();
			}
		}
	}
	
	public void truncate(long size){
		DataObject obj;
		for (int i=0; i<theList.size(); i++){
			obj = theList.get(i);
			if (size >= obj.getFirstByte()){
				theList.remove(i);
			}
		/*	if (size == 0){
				obj = new DataObject2(0);
				
				theList.add(obj);
			}*/
			obj = getDataObject(size);
			
			if (obj != null){
				obj.setBytesFilled((int)(size -obj.getFirstByte()));
			}
		}
	}
	public void addOnRAM(DataObject obj){
		onRAM.add(obj);
		this.bytesOnRAM += obj.getSize();
	}
	public void removeOnRAM(DataObject obj){
		if (onRAM.contains(obj)){
			onRAM.remove(obj);
			this.bytesOnRAM -= obj.getSize();
		}
	}
	void checkRAM(){
		if (bytesOnRAM > RAMLimit){
			DataObject obj = onRAM.getFirst();
			obj.toDisk();
		}
	}
	public void setLocalChordObj(RemoteChordNode obj){
		localChordObj = obj;
	}

	public RemoteChordNode getLocalChordObj(){
		return localChordObj;
	}
	public int getListSize(){
		return theList.size();
	}
	public void allToNetwork(){
		DataObject obj;
		Iterator<DataObject> it = theList.iterator();
		while (it.hasNext()){
			obj = it.next();
			obj.toNetwork();
		}
		/*for (int i=0; i<theList.size(); i++){
			obj = theList.get(i);
			obj.toNetwork();
		}*/
	}
	public void increasePopularityAll(int points){
		DataObject obj;
		for (int i=0; i<theList.size(); i++){
			obj = theList.get(i);
			obj.increasePopularity(points);
		}
	}
	public void deleteAll(){
		DataObject obj;
		for (int i=0; i<theList.size(); i++){
			obj = theList.get(i);
			obj.delete();
		}
	}
	public void printStatus(){
		DataObject obj;
		try{
		FileOutputStream fos = new FileOutputStream("/tmp/printstatus.txt", true);
		fos.write("printStatus:\n".getBytes());
		for (int i=0; i<theList.size(); i++){
			obj = theList.get(i);
			
			if (obj.theData != null){
				fos.write(String.valueOf("data not null, \t" + obj.getBytesFilled() + "\n").getBytes());
				
			}
			if (obj.dataID == null)
				fos.write(("null id, \tBytesFilled=" + obj.getBytesFilled() + "\n").getBytes());
				if (!obj.onNetwork){
					fos.write("onNetwork=false\n".getBytes());
				}
		}
		fos.flush();
		fos.close();
		}catch(Exception e){e.printStackTrace();}
	}
}
