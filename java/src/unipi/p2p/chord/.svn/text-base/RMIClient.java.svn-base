package unipi.p2p.chord;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RMIClient {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String host = (args.length < 1) ? null : args[0];
        try {
            //Registry registry = LocateRegistry.getRegistry(host);
            RemoteChordNode stub = (RemoteChordNode) Naming.lookup("rmi://" + host+ "/RemoteChordNode");
            long start = System.currentTimeMillis();
            Finger[] fingTable = stub.getFingers();
            long end = System.currentTimeMillis();
            System.out.println("got it in " + (end - start) + " size " + fingTable.length);
            
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }


	}

}
