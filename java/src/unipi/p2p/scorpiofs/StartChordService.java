package unipi.p2p.scorpiofs;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import unipi.p2p.chord.ChordNode;
import unipi.p2p.chord.Finger;
import unipi.p2p.chord.HashChecker;
import unipi.p2p.chord.Replicator;
import unipi.p2p.chord.Statistics;
import unipi.p2p.chord.util.Util;
import unipi.p2p.chord.visualization.ChordViewer;
import unipi.p2p.scorpiofs.util.ConfigParser;
import unipi.p2p.scorpiofs.util.ShutDownChord;

public class StartChordService implements Runnable{
	private static int srvPort = 0;
	private static String config = null;
	
	public StartChordService(int port, String config){
		this.srvPort = port;
		this.config = config;
	}
	public void run (){
		int i = 0, j;
        String arg;
        int servicePort=0;
        String bootstrapURL = null;
        String configFile = config;
        //String configFile = "config/chord.properties";
        String outputFolder = "/tmp/";
        String realIP=null;
        String hashFile = "/tmp/chord.hashtable";
        String storingListFilename="/tmp/chord.storing";
        String retrievingListFilename="/tmp/chord.retrieving";
        boolean startNewChord = false;
        Finger bootstrapFinger = null;

        /*while (i < args.length && args[i].startsWith("-")) {
            arg = args[i++];
            
            if (arg.equals("-config")) {
                if (i < args.length){
                	configFile = args[i++];
                }
                else{
                    System.err.println("-config requires a filename");
                    System.exit(-1);                }
      
            }
        }
        
        if (args.length != i || args.length == 0){
        	System.err.println("Usage: StartChordService [-config configFile]");
        
        	System.exit(-1);
        }*/
        
        try {
        	ConfigParser conf = new ConfigParser(configFile);
        	//servicePort=conf.getPort();
        	servicePort = srvPort;
            System.out.println("Property Port: "+servicePort);
        	realIP=conf.getRealIP();
        	bootstrapURL = conf.getBootstrap();
        	outputFolder = conf.getOutputDir();
        	hashFile = conf.getHashtableFile();
        	if (bootstrapURL != null){
        		System.out.println("Bootstraping from " + bootstrapURL);
        	}
        	else{
        		System.out.println("Starting new chord ring...");
        	}
        	storingListFilename=conf.getStoringList();
        	retrievingListFilename=conf.getRetrievingList();
        } catch (IOException e) {
        	System.err.println("Cannot read from configuration file");
        	System.exit(-1);
        }
        if (bootstrapURL == null){
        	startNewChord = true;

        }
        else{
        	bootstrapFinger = new Finger(bootstrapURL);
        }
        
        InetAddress localhost;
        
        try {
        	localhost = InetAddress.getLocalHost();
        	InetAddress   in  = InetAddress.getLocalHost();
        	String localIP = localhost.getHostAddress();

        	if (realIP != null){
        		System.getProperties().put("java.rmi.server.hostname", realIP);
        	}

        	ChordNode chordobj = null;
        	
        	//chordobj = new ChordNode(new Finger(localIP + ":" + servicePort), startNewChord, bootstrapFinger);
        	chordobj = new ChordNode(new Finger(realIP + ":" + servicePort), startNewChord, bootstrapFinger);
        	Registry registry = LocateRegistry.createRegistry(servicePort);
        	chordobj.setOutputDirectory(outputFolder);
        	chordobj.setMetadataFile(hashFile);
        	chordobj.setStoringListFilename(storingListFilename);
        	chordobj.setRetrievingListFilename(retrievingListFilename);
        	chordobj.loadHashtableFromFile();
        	chordobj.loadClientsList();
        	
        	//Naming.rebind("rmi://" + localIP + ":" + servicePort + "/unipi.p2p.chord.ChordNode", chordobj);
        	Naming.rebind("rmi://" + "localhost" + ":" + servicePort + "/unipi.p2p.chord.ChordNode", chordobj);
        	Thread.currentThread();
        	Thread.sleep(2000);
        	Thread updateThread = new Thread(chordobj, "updateThread");
        	updateThread.start();
        	
        	Replicator chordReplicator = new Replicator(chordobj); //attach Replicator
        	HashChecker hashCheck = new HashChecker(chordobj); //attach HashChecker
       
        	System.out.println("Service running at "+localIP+":"+servicePort);
        	//Print statistics thread.
        	//Thread statThread=new Thread(new Statistics(chordobj),"statThread");
        	//statThread.setPriority(Thread.MIN_PRIORITY);
        	//statThread.start();
        	//System.out.println(Util.getMac());
        	Finger la;

        	la = chordobj.getLocalNode();
        	//System.out.println("IP="+la.getIPAddress());
        	//System.out.println("ID="+la.getBigIntValue());
        	Runtime.getRuntime().addShutdownHook(new ShutDownChord(chordobj)); 

        	//new ChordViewer(chordobj);


        } catch(Exception e) {
        	e.printStackTrace();
        	System.out.println("Error starting service (is port already in use?)");
        }
 

	}	
}
