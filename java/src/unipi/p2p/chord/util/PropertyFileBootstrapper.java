package unipi.p2p.chord.util;



import org.apache.log4j.Logger;

import unipi.p2p.chord.Bootstrapper;
import unipi.p2p.chord.Finger;
import unipi.p2p.chord.ChordNode;
import unipi.p2p.chord.RemoteChordNode;

import java.io.InputStream;
import java.io.FileInputStream;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.Properties;

public class PropertyFileBootstrapper implements Bootstrapper {

	private static final Logger log = Logger.getRootLogger();
	private  String[] hosts;
	             
	public PropertyFileBootstrapper(String fileName) throws Exception{
		
		
		try {
	
			InputStream is = new FileInputStream(fileName);
			Properties props = new Properties();
			props.load(is);
			String hostList = props.getProperty("chord.seedList");
			hosts = hostList.split(",");
			if (hosts.length == 0) {
				throw new Exception("You should have at least on Chord Node in your seed list");
			}
			is.close();
		} catch(Exception e) {
			log.fatal(e.getMessage(), e);
			throw e;
		}
		
	}
	public Finger getFinger() {
		
		for (int i = 0; i < hosts.length; i++) {
			Finger f = new Finger(hosts[i]);
			try {
				if (getRemoteChordNode(f).alive()) {
					return f;
				}
			} catch(Exception e) {
				log.info("Failed to communicate with node " + f.toString());
			}		
		}
		return null;
	}
	
	private RemoteChordNode getRemoteChordNode(Finger f) throws RemoteException {
		try {
			RemoteChordNode stub = (RemoteChordNode) Naming.lookup("rmi://"
					+ f.toString() + "/unipi.p2p.chord.ChordNode");
			return stub;
		} catch (Exception e) {
			throw new RemoteException(e.toString());
		}
	}
}