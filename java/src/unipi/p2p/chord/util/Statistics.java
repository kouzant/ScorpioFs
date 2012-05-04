package unipi.p2p.chord.util;

import java.io.Serializable;
import java.net.InetAddress;

public class Statistics implements Serializable{
	private static final long serialVersionUID = 8538684231273750632L;
	private long remoteCalls;
	private long checkPredecessorCalls;
	private long successorListCalls;
	private long stabilizeCalls;
	private long fixFingerCalls;
	private int storingListSize;
	private int retrievingListSize;
	private InetAddress ipAddr;
	//Total size in bytes
	private long totalChunkSize;
	private int servicePort;
	
	public Statistics(){
		remoteCalls = 0;
		checkPredecessorCalls = 0;
		successorListCalls = 0;
		stabilizeCalls = 0;
		fixFingerCalls = 0;
		storingListSize = 0;
		retrievingListSize = 0;
		ipAddr = null;
		totalChunkSize = 0L;
		servicePort = 0;
	}
	
	public void setServicePort(int servicePort){
		this.servicePort = servicePort;
	}
	public int getServicePort(){
		return servicePort;
	}
	public void setTotalChunkSize(long totalChunkSize){
		this.totalChunkSize = totalChunkSize;
	}
	public long getTotalChunkSize(){
		return totalChunkSize;
	}
	public void setIpAddr(InetAddress ipAddr){
		this.ipAddr = ipAddr;
	}
	public InetAddress getIpAddr(){
		return ipAddr;
	}
	public void setStoringListSize(int storingListSize){
		this.storingListSize = storingListSize;
	}
	public int getStoringListSize(){
		return storingListSize;
	}
	public void setRetrievingListSize(int retrievingListSize){
		this.retrievingListSize = retrievingListSize;
	}
	public int getRetrievingListSize(){
		return retrievingListSize;
	}
	public long getCheckPredecessorCalls() {
		return checkPredecessorCalls;
	}
	public void incrCheckPredecessorCalls() {
		this.checkPredecessorCalls++;
	}
	public long getFixFingerCalls() {
		return fixFingerCalls;
	}
	public void incrFixFingerCalls() {
		this.fixFingerCalls++;
	}
	public long getRemoteCalls() {
		return remoteCalls;
	}
	public void incrRemoteCalls() {
		this.remoteCalls++;
	}
	public long getStabilizeCalls() {
		return stabilizeCalls;
	}
	public void incrStabilizeCalls() {
		this.stabilizeCalls++;
	}
	public long getSuccessorListCalls() {
		return successorListCalls;
	}
	public void incrSuccessorListCalls() {
		this.successorListCalls++;
	}
	
	
	
		
}
