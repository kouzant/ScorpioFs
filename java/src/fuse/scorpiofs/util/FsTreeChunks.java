package fuse.scorpiofs.util;

import java.io.Serializable;
import java.util.*;

public class FsTreeChunks implements Serializable{
	List<String> chunkIDList=new ArrayList<String>();
	
	public void setID(String chunkID){
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
	
	public Iterator<String> getIDs(){
		Iterator<String> it=chunkIDList.iterator();
		return it;
	}
}
