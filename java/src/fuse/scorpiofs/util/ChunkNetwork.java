package fuse.scorpiofs.util;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import unipi.p2p.chord.Finger;
import unipi.p2p.chord.RemoteChordNode;
import unipi.p2p.chord.Storage;
import unipi.p2p.chord.util.Util;

public class ChunkNetwork {
	private static final Log log = LogFactory.getLog(ChunkNetwork.class);
	
	public static BigInteger toNetwork(byte[] buffer, RemoteChordNode localChordNode){
		Storage storageObj=new Storage(buffer);
		storageObj.setReplicationFactor(unipi.p2p.chord.Constants.REPLICATION_FACTOR);
		BigInteger dataID=storageObj.getID();
		log.info("Chunk id: "+dataID);
		log.info("Chunk id hash: "+Util.shaHex(buffer));
		
		Finger targetFinger;
		try{
			log.info("Just before finding a successor for the fstree chunk");
			targetFinger=localChordNode.findSuccessor(dataID);
			RemoteChordNode targetNode=(RemoteChordNode) Naming.lookup("rmi://"+targetFinger.toString()+
					"/unipi.p2p.chord.ChordNode");
			if(!targetNode.hasKey(dataID)){
				log.info("Storing fstree chunk");
				targetNode.put(storageObj);
				InetAddress addr=InetAddress.getLocalHost();
				String ipAddress=new String(addr.getHostAddress());
				targetNode.getStoringList().add(ipAddress);
				log.info("Local ip address: "+ipAddress);
				log.info("Storing on node: "+targetNode.getIPAddress());
			}
			//Give a hint to garbage collector
			storageObj=null;
		}catch(RemoteException e){
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch(UnknownHostException e){
			e.printStackTrace();
		}finally{
			System.gc();
		}
		
		return dataID;
	}
	
	public static byte[] getChunks(BigInteger dataID, RemoteChordNode localChordNode){
		Storage storageObj;
		Finger targetFinger;
		byte[] buffer=null;
		try{
			if(dataID==null)
				log.info("Error!!! dataID is null");
			log.info("Chunk id: "+dataID);
			targetFinger=localChordNode.findSuccessor(dataID);
			RemoteChordNode targetNode=(RemoteChordNode) Naming.lookup("rmi://"+targetFinger.toString()+
					"/unipi.p2p.chord.ChordNode");
			if(targetNode.hasKey(dataID)){
				storageObj=targetNode.get(dataID);
				log.info("fstree chunk found first on: "+targetNode.getIPAddress());
				buffer=null;
				buffer=storageObj.getData();
			}else{
				BigInteger targetID=dataID;
				int replication=unipi.p2p.chord.Constants.REPLICATION_FACTOR;
				for(int i=0;i<replication;i++){
					targetID=unipi.p2p.chord.util.Util.integerValue(targetID.toString());
					targetFinger=localChordNode.findSuccessor(targetID);
					targetNode=(RemoteChordNode) Naming.lookup("rmi://"+targetFinger.toString()+
							"/unipi.p2p.chord.ChordNode");
					if(targetNode.hasKey(dataID)){
						storageObj=targetNode.get(dataID);
						log.info("fstree chunk found on: "+targetNode.getIPAddress());
						buffer=null;
						buffer=storageObj.getData();
						break;
					}
				}
			}
		}catch(RemoteException e){
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return buffer;
	}
}
