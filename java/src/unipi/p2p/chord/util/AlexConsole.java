package unipi.p2p.chord.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Hashtable;

import org.apache.log4j.Logger;

import unipi.p2p.chord.ChordNode;
import unipi.p2p.chord.Finger;
import unipi.p2p.chord.RemoteChordNode;
import unipi.p2p.chord.Replicator;
import unipi.p2p.chord.Storage;

public class AlexConsole {
	
	//private static final Logger log = Logger.getRootLogger();
	private static ChordNode chordobj = null;
	private static Replicator chordReplicator = null;	
	private static String outputDir = "/tmp/";
	private static Finger bootstrapFinger;
	private static Hashtable<String, BigInteger> filelist = new Hashtable(10,10);
	public static void main(String [] args) throws RemoteException, MalformedURLException, UnknownHostException {
		String InputCommand = null;
		String [] splitCommand;
		
		System.out.println("ScorpioConsole\ntype 'help' for available commands");
		
		while (true) {
			InputCommand = inputStr("> ");
			splitCommand = InputCommand.split(" ");
			runCommand(splitCommand);
		}
	}
	public static void runCommand(String [] Command) throws MalformedURLException, UnknownHostException{
		if (Command[0].equals("help")){
			System.out.println("Available commands are:\nhelp create join id successor put get outputdir printhash replicate exit");
		}
		else if (Command[0].equals("exit")){
			System.out.println("Bye!"); System.exit(0);
		}
		else if (Command[0].equals("create")){
			System.out.println("Starting chord network...");
			
			String ip = Command[1];
			int port = Integer.parseInt(Command[2]);
			try {
				chordobj = new ChordNode(new Finger(ip + ":" + port), true, null);
				Registry registry = LocateRegistry.createRegistry(port);
				Naming.rebind("rmi://" + ip + ":" + port+ "/unipi.p2p.chord.ChordNode", chordobj);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//log.info("ChordNode at " + InetAddress.getLocalHost() + "ready");
			//System.out.println(chordobj.getFingers()[5]);
			Thread updateThread = new Thread(chordobj, "updateThread");
			updateThread.start();
			chordReplicator = new Replicator(chordobj);
			System.out.println("Done!");
			
			
			
		}
		else if (Command[0].equals("join")){
			System.out.println("Joining chord network...");
			
			String ip = Command[1];
			int port = Integer.parseInt(Command[2]);
			try {
				chordobj = new ChordNode(new Finger(ip + ":" + port), false, bootstrapFinger);
				Registry registry = LocateRegistry.createRegistry(port);
				Naming.rebind("rmi://" + ip + ":" + port+ "/unipi.p2p.chord.ChordNode", chordobj);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//log.info("ChordNode at " + InetAddress.getLocalHost() + "ready");
			try {
				System.out.println(chordobj.getFingers()[0]);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			Thread updateThread = new Thread(chordobj, "updateThread");
			updateThread.start();
			
			chordReplicator = new Replicator(chordobj);
			
			try {
				chordobj.setOutputDirectory(outputDir);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("Done!");
			
		}
		else if (Command[0].equals("id")){
			Finger tempfinger;
			try {
				tempfinger = chordobj.getLocalNode();
				System.out.println(tempfinger.getBigIntValue().toString());
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		}
		else if (Command[0].equals("successor")){
			Finger tempfinger;
			try {
				tempfinger = chordobj.successor();
				System.out.println(tempfinger.getBigIntValue().toString());
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
		else if (Command[0].equals("get")){
			BigInteger b = new BigInteger(Command[1]);
			Finger tempfinger;
			try {
				tempfinger = chordobj.findSuccessor(b);
				RemoteChordNode la = chordobj.getRemoteChordNode(tempfinger);
				//System.out.println("Getting from node " + tempfinger.getBigIntValue());
				Storage tmpStor = la.get(b);
				if (tmpStor != null){
					System.out.println(la.get(b).datatoString());
				}
				else {
					System.out.println("Not found.");
				}
			}
			 catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		/*else if (Command[0].equals("put")){
			String word = Command[1];
			saveToDHT(word);
						
			}*/
		else if (Command[0].equals("put")){
			String filename = Command[1];
			saveFileToDHT(filename);
						
			}
		else if (Command[0].equals("ls")){
			System.out.println(filelist.toString());			
		}
		else if (Command[0].equals("outputdir")){
			outputDir = Command[1];
			if (chordobj != null){
				try {
					chordobj.setOutputDirectory(outputDir);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
						
			}
		else if (Command[0].equals("printhash")){
			try {
				chordobj.printHashtable();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			}
		else if (Command[0].equals("replicate")){
			chordReplicator.run();
			}
		else if (Command[0].equals("bootstrap")){
			bootstrapFinger = new Finger(Command[1]);
			}
		else{
			System.out.println("Unknown command");
		}
	}

	public static String inputStr(String s) {
        String aLine = "";
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        System.out.print(s);
        try {
          aLine = input.readLine();
        }
        catch (Exception e) {
          e.printStackTrace();
        }
        return aLine;
}
	
	public static void saveToDHT(String data){
		BigInteger data_key = Util.integerValue(data);
		try {
			Finger target_finger = chordobj.findSuccessor(data_key);
			RemoteChordNode target_node = chordobj.getRemoteChordNode(target_finger);
			Storage stor_obj = new Storage(data.getBytes());
			target_node.put(stor_obj);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
		
	}
	public static void saveFileToDHT(String filename){
		File fileObj = new File(filename);
		try {
			Storage stor_obj = new Storage(getBytesFromFile(fileObj));
			Finger target_finger = chordobj.findSuccessor(stor_obj.getID());
			RemoteChordNode target_node = chordobj.getRemoteChordNode(target_finger);
			target_node.put(stor_obj);
			filelist.put(fileObj.getName(), stor_obj.getID());
		} catch (IOException e) {
			
			System.out.println("Cannot open file");
		}
		
		
	}
	public static byte[] getBytesFromFile(File file) throws IOException {
        InputStream is = new FileInputStream(file);
    
        // Get the size of the file
        long length = file.length();
    
        if (length > Integer.MAX_VALUE) {
            // File is too large
        }
    
        // Create the byte array to hold the data
        byte[] bytes = new byte[(int)length];
    
        // Read in the bytes
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
               && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
            offset += numRead;
        }
    
        // Ensure all the bytes have been read in
        if (offset < bytes.length) {
            throw new IOException("Could not completely read file "+file.getName());
        }
    
        // Close the input stream and return bytes
        is.close();
        return bytes;
    }




}