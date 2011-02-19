package fuse.scorpiofs.util;

import java.util.Hashtable;

public class DataCache {
	private static Hashtable<String, DataObject> dataCache;
	public DataCache(){
		 dataCache = new Hashtable<String, DataObject>();
		
	}
	public void put(String path, DataObject dataObj){
		if (dataCache.get(path)!=null)
		{
			dataCache.remove(path);
		}
		dataCache.put(path, dataObj);
	}
	public DataObject get(String path){
		return dataCache.get(path);
		
	}
	public void remove(String path){
		dataCache.remove(path);		
	}

}
