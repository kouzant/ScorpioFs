package unipi.p2p.chord;

import java.io.Serializable;
import java.math.BigInteger;

import unipi.p2p.chord.util.Util;

public class Storage implements Serializable {

	private byte[] theData;
	private BigInteger id;
	private int replicationFactor;
	//private int popularity = 0;

	public Storage (byte[] dataInput) {
		theData = dataInput;
		id = Util.integerValue(this.datatoString());
	}
	public String getShaHex(){
		return Util.shaHex(theData);
	}
	public void calculateID(){
		this.id = Util.integerValue(this.datatoString());
	}
	public BigInteger getID(){
		return id;
	}
	
	public String datatoString(){
		String value = new String(theData);
		return value;
	}
	public byte[] getData(){
		return theData;
	}
	public void setReplicationFactor (int replicationFactor){
		this.replicationFactor = replicationFactor;
	}
	public int getReplicationFactor(){
		return this.replicationFactor;
	}
/*	public int getPopularity(){
		return popularity;
	}
	public void resetPopularity(){
		popularity++;
	}
	public void increasePopularity(){
		popularity++;
		
	}*/

}
