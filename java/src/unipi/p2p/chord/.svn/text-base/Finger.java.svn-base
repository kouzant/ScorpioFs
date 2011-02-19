package unipi.p2p.chord;

import java.io.Serializable;
import java.math.BigInteger;

import org.apache.log4j.Logger;

import unipi.p2p.chord.util.Util;

/**
 * 
 * 
 * Each Chord node is identified by a unique identifier. By applying a standard hash function (SHA-1) to
 * the IP address and port number  
 * 
 * 
 * 
 * 
 * @author agapios
 *
 */
public class Finger implements Serializable {
	
    private final String ipAddress;
    private final int port;
    private final BigInteger integerValue;

    /**
     *  
     */
    public Finger(final String hostNameAndPort) {
        super();
        if (!hostNameAndPort.matches(".*:.*")) {
            throw new IllegalArgumentException("Bad format.  Should be hostname:port");
        }
        final String[] info = hostNameAndPort.split(":");
        ipAddress = info[0].trim();
        port = Integer.parseInt(info[1]);
        //integerValue = Util.integerValue(toString()); //agapios id
        integerValue = Util.integerValue(unipi.p2p.chord.util.Util.getMac() + ":" + port); //alexandros id
    }


    public BigInteger getBigIntValue() {
        return integerValue;
    }
    
    public String getIPAddress() {
    	return ipAddress;
    }
    
    public int getPort() {
    	return port;
    }
    
    public String toString() {
    	return ipAddress + ":" + port;
    }

    
}
