package unipi.p2p.chord.util.console;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import unipi.p2p.scorpiofs.StartChordService;
import unipi.p2p.scorpiofs.util.ShutDownChord;
import unipi.p2p.chord.util.Statistics;

/*
 * Class which implements the proxy server
 */
public class ConsoleServer {
	private static final Log log = LogFactory.getLog(ConsoleServer.class);
	public static void main(String[] args) {
		ServerSocket sSocket = null;
		ThreadFactory daemonFactory = new DaemonFactory();
		ExecutorService exec = Executors.newCachedThreadPool(daemonFactory);

		try{
			if(args.length == 2){
				sSocket = new ServerSocket(Integer.parseInt(args[1]));
				System.out.println("Proxy server started on port "+args[1]);
			}else{
				sSocket = new ServerSocket(ConsoleProtocol.PROXY_PORT);
				System.out.println("Proxy server started on port "+ConsoleProtocol
						.PROXY_PORT);
			}
		}catch (IOException e0){
			e0.printStackTrace();
		}
		Socket cSocket = null;
		boolean proxyUp = true;
		
		Set<NodeInfo> nodes = new HashSet<NodeInfo>();
		try{
			while(proxyUp){
				int code = -1;
				int chPort = -1;
				String chordConfig = null;
				BufferedReader bin = null;
				cSocket = sSocket.accept();
				bin = new BufferedReader(new InputStreamReader 
						(cSocket.getInputStream()));
				
				code = Integer.parseInt(bin.readLine());
				chPort = Integer.parseInt(bin.readLine());
				chordConfig = bin.readLine();
				log.info("Code is: "+code);
				log.info("Chord Port is: "+chPort);
				log.info("Chord config: " + chordConfig);
				
				//Available operations
				switch(code){
				case ConsoleProtocol.NODE_CREATE:
					log.info("node create IP_ADDR");
					NodeInfo nodeInfo = exec.submit(new StartChordService(chPort, 
							chordConfig)).get();
					if(nodeInfo != null)
						nodes.add(nodeInfo);
					bin.close();
					cSocket.close();
					//Connect to console receiver for return messages
					Socket crSocket = new Socket(cSocket.getInetAddress(),
							ConsoleProtocol.CLREC_PORT);
					PrintWriter pwc = new PrintWriter(crSocket.getOutputStream(),
							true);
					pwc.println(sSocket.getLocalPort());
					if(nodeInfo != null){
						pwc.println(ConsoleProtocol.CREATED);
					}else{
						pwc.println(ConsoleProtocol.NOT_CREATED);
					}
					pwc.println(chPort);
					pwc.close();
					crSocket.close();
					break;
				case ConsoleProtocol.NODE_STOP:
					log.info("node stop IP_ADDR");
					//Iterate through node info list
					Iterator<NodeInfo> nodeIt = nodes.iterator();
					NodeInfo curNode = null;
					NodeInfo tmpNode = null;
					while(nodeIt.hasNext()){
						tmpNode = nodeIt.next();
						if(tmpNode.getServicePort() == chPort){
							curNode = tmpNode;
							break;
						}
					}
					//Connect to console receiver for return messages
					crSocket = new Socket(cSocket.getInetAddress(),
							ConsoleProtocol.CLREC_PORT);
					pwc = new PrintWriter(crSocket.getOutputStream(),
							true);
					pwc.println(sSocket.getLocalPort());
					if(curNode != null){
						Integer result = exec.submit(new ShutDownChord(curNode))
								.get();
						nodes.remove(curNode);
						if(result == 1)
							pwc.println(ConsoleProtocol.STOPPED);
						else
							pwc.println(ConsoleProtocol.NOT_STOPPED);
					}else{
						pwc.println(ConsoleProtocol.NOT_STOPPED);
					}
					pwc.println(chPort);
					pwc.close();
					crSocket.close();
					break;
				case ConsoleProtocol.NODE_STAT:
					//Spawn methods to receive statistics
					Iterator<NodeInfo> nodesIt = nodes.iterator();
					NodeInfo node = null;
					
					while(nodesIt.hasNext()){
						node = nodesIt.next();
						Statistics stats = node.getChordobj().getStatistics();
						//test
						System.out.println("Stats: "+stats.getSuccessorListCalls());
					}
					log.info("stats get");
					break;
				case ConsoleProtocol.NODE_ALIVE:
					log.info("node alive IP_ADDR");
					break;
				case ConsoleProtocol.TERMINATE:
					log.info("Terminate");
					bin.close();
					cSocket.close();
					proxyUp = false;
					break;
				default:
					log.info("Operation not found!");
					break;
				}
			}
			System.out.println("Ciao amore!");
			System.exit(1);
		}catch (IOException e0){
			e0.printStackTrace();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		} catch (ExecutionException e2) {
			e2.printStackTrace();
		}
		
	}
}
