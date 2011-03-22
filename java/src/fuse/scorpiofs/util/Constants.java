package fuse.scorpiofs.util;

import unipi.p2p.chord.RemoteChordNode;

public class Constants {
	public static final String USER_HOME=System.getProperty("user.home");
	public static int replicationFactor = 3; 
	//public static RemoteChordNode x;
	public static String personalDir=USER_HOME+"/.scorpiofs";
	public static String interFileName=personalDir+"interFile";
	public static String fsTreeName=personalDir+"fsTree";
	public static char[] password=null;

	public static void setReplicationFactor(int x) {
		replicationFactor = x;
	}
	
	public static void setPersonalDir(String pd){
		personalDir=pd;
	}
	
	public static void setInterFileName(String ifn){
		interFileName=ifn;
	}
	
	public static void setFsTreeName(String fstn){
		fsTreeName=fstn;
	}
	
	public static void setPassword(char[] pass){
		password=pass;
	}
}
