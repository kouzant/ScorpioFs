package unipi.p2p.chord.util;

import java.io.Serializable;
import org.apache.commons.lang3.time.StopWatch;

public class Statistics implements Serializable{
	private static final long serialVersionUID = 8538684231273750632L;
	private long remoteCalls;
	private long checkPredecessorCalls;
	private long successorListCalls;
	private long stabilizeCalls;
	private long fixFingerCalls;
	private int storingListSize;
	private int retrievingListSize;
	private String ipAddr;
	//Total size in bytes
	private long totalChunkSize;
	private int servicePort;
	private StopWatch startTime;
	private StopWatch currentTime;
	private long getRequests;
	private long putRequests;
	private int totalChunks;
	private long uptime;
	
	public Statistics(){
		remoteCalls = 0L;
		checkPredecessorCalls = 0L;
		successorListCalls = 0L;
		stabilizeCalls = 0L;
		fixFingerCalls = 0L;
		storingListSize = 0;
		retrievingListSize = 0;
		ipAddr = null;
		totalChunkSize = 0L;
		servicePort = 0;
		//startTime = null;
		currentTime = null;
		getRequests = 0L;
		putRequests = 0L;
		totalChunks = 0;
		uptime = 0L;
	}
	
	public void setUptime(long uptime){
		this.uptime = uptime;
	}
	public long getUptime(){
		return uptime;
	}
	public void setTotalChunks(int totalChunks){
		this.totalChunks = totalChunks;
	}
	public int getTotalChunks(){
		return totalChunks;
	}
	public void setGetRequests(long getRequests){
		this.getRequests = getRequests;
	}
	public long getGetRequests(){
		return getRequests;
	}
	public void setPutRequests(long putRequests){
		this.putRequests = putRequests;
	}
	public long getPutRequests(){
		return putRequests;
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
	public void setIpAddr(String ipAddr){
		this.ipAddr = ipAddr;
	}
	public String getIpAddr(){
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
	public void setStartTime(StopWatch startTime){
		this.startTime = startTime;
	}
	public StopWatch getStartTime(){
		return startTime;
	}
	public void setCurrentTime(StopWatch currentTime){
		this.currentTime = currentTime;
	}
	public StopWatch getCurrentTime(){
		return currentTime;
	}
}
