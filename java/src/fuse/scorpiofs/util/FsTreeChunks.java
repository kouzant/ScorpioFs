package fuse.scorpiofs.util;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.*;

public class FsTreeChunks implements Serializable{
	private static final long serialVersionUID = 1L;
	List<BigInteger> chunkIDList=new ArrayList<BigInteger>();
	
	public void setID(BigInteger chunkID){
		try{
			chunkIDList.add(chunkID);
		}catch(UnsupportedOperationException e0){
			e0.printStackTrace();
		}catch(ClassCastException e1){
			e1.printStackTrace();
		}catch(NullPointerException e2){
			e2.printStackTrace();
		}catch(IllegalArgumentException e3){
			e3.printStackTrace();
		}
	}
	
	public Iterator<BigInteger> getIDs(){
		Iterator<BigInteger> it=chunkIDList.iterator();
		return it;
	}
	
	public void delIDs(){
		chunkIDList.removeAll(chunkIDList);
	}
}
