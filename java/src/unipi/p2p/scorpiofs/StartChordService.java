package unipi.p2p.scorpiofs;

import java.io.IOException;
import java.net.InetAddress;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Date;
import java.util.concurrent.Callable;

import org.apache.commons.lang3.time.StopWatch;
import unipi.p2p.chord.ChordNode;
import unipi.p2p.chord.Finger;
import unipi.p2p.chord.HashChecker;
import unipi.p2p.chord.Replicator;
import unipi.p2p.chord.util.console.NodeInfo;
import unipi.p2p.scorpiofs.util.ConfigParser;

public class StartChordService implements Callable<NodeInfo>{
	private static int srvPort = 0;
	private static String config = null;
	
	public StartChordService(int port, String config){
		this.srvPort = port;
		this.config = config;
	}
	public NodeInfo call (){
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
        ChordNode chordobj = null;
        NodeInfo nodeInfo = null;
        
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
        	
        	//chordobj = new ChordNode(new Finger(localIP + ":" + servicePort), startNewChord, bootstrapFinger);
        	chordobj = new ChordNode(new Finger(realIP + ":" + servicePort), startNewChord, bootstrapFinger);
        	Registry registry = LocateRegistry.createRegistry(servicePort);
        	chordobj.setOutputDirectory(outputFolder);
        	chordobj.setMetadataFile(hashFile);
        	chordobj.setStoringListFilename(storingListFilename);
        	chordobj.setRetrievingListFilename(retrievingListFilename);
        	chordobj.loadHashtableFromFile();
        	chordobj.loadClientsList();
        	//Take time used for service uptime
        	StopWatch watch = new StopWatch();
        	watch.start();
        	chordobj.setStartTime(watch);
        	
        	//Naming.rebind("rmi://" + localIP + ":" + servicePort + "/unipi.p2p.chord.ChordNode", chordobj);
        	Naming.rebind("rmi://" + "localhost" + ":" + servicePort + "/unipi.p2p.chord.ChordNode", chordobj);
        	Thread.sleep(5000);
        	Thread updateThread = new Thread(chordobj, "updateThread");
        	updateThread.start();
        	
        	Replicator chordReplicator = new Replicator(chordobj); //attach Replicator
        	HashChecker hashCheck = new HashChecker(chordobj); //attach HashChecker
        	nodeInfo = new NodeInfo(chordReplicator, chordobj, servicePort,
        			registry);
        	System.out.println("Service running at "+localIP+":"+servicePort);
        	//System.out.println(Util.getMac());
        	Finger la;

        	la = chordobj.getLocalNode();
        	//System.out.println("IP="+la.getIPAddress());
        	//System.out.println("ID="+la.getBigIntValue());
        	//Runtime.getRuntime().addShutdownHook(new ShutDownChord(chordobj)); 

        	//new ChordViewer(chordobj);

        } catch(Exception e) {
        	e.printStackTrace();
        	System.out.println("Error starting service (is port already in use?)");
        }
 
        return nodeInfo;
	}	
}
