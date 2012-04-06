package unipi.p2p.chord.util;

import java.io.Serializable;

public class Statistics implements Serializable{
	private static final long serialVersionUID = 1L;
	private long remoteCalls = 0;
	private long checkPredecessorCalls = 0;
	private long successorListCalls = 0;
	private long stabilizeCalls = 0;
	private long fixFingerCalls = 0;
	
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
