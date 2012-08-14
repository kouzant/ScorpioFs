package unipi.p2p.chord.util.console;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.joda.time.DateTime;

import unipi.p2p.chord.util.Statistics;

/*
 * Class which implements the tcp server for return messages
 */
public class ConsoleClientReceiver implements Runnable {
	private static boolean running = true;
	private volatile LinkedList<Nodes> nodesList;
	private  LinkedList<Statistics> nodeStats;
	private ExecutorService exec = null;
	public ConsoleClientReceiver(LinkedList<Nodes> nodesList, 
			LinkedList<Statistics> nodeStats){
		this.nodesList = nodesList;
		this.nodeStats = nodeStats;
		exec = Executors.newCachedThreadPool();
	}
	public void stopRunning(){
		running = false;
	}
	
	
	@Override
	public void run() {
		ServerSocket sSocket = null;
		Socket cSocket = null;
		try{
			sSocket = new ServerSocket(ConsoleProtocol.CLREC_PORT);
			while(running){
				cSocket = sSocket.accept();
				if(cSocket != null){
					ThreadedServer tServer = new ThreadedServer(cSocket,
							nodesList, nodeStats);
					exec.execute(tServer);
				}
			}
			sSocket.close();
		}catch(IOException e0){
			e0.printStackTrace();
		}
	}
}

class ThreadedServer implements Runnable{
	private Socket cSocket = null;
	private volatile LinkedList<Nodes> nodesList = null;
	private  LinkedList<Statistics> nodeStats = null;
	
	public ThreadedServer(Socket cSocket, LinkedList<Nodes> nodesList,
			LinkedList<Statistics> nodeStats){
		this.cSocket = cSocket;
		this.nodesList = nodesList;
		this.nodeStats = nodeStats;
	}
	//Store every statistics object to a linked list
	public synchronized void storeStats(Statistics stats){
		nodeStats.add(stats);
		System.err.println("nodeStats size: "+nodeStats.size());
	}
	//Create Statistcs object
	private synchronized Statistics createStat(int storingListSize,
			int retrievingListSize, String ipAddr, int servicePort, 
			long totalChunkSize, DateTime startTime, DateTime currentTime){
		Statistics tmpStat = new Statistics();
		tmpStat.setStoringListSize(storingListSize);
		tmpStat.setRetrievingListSize(retrievingListSize);
		tmpStat.setIpAddr(ipAddr);
		tmpStat.setServicePort(servicePort);
		tmpStat.setTotalChunkSize(totalChunkSize);
		tmpStat.setStartTime(startTime);
		tmpStat.setCurrentTime(currentTime);
		
		return tmpStat;
	}
	@Override
	public void run(){
		BufferedReader bin = null;
		ObjectInputStream objIn = null;
		
		int code = -1;
		int port = -1;
		int proxyPort = -1;
		String senderIp = null;
		bin = null;
		try{
			InputStream inStream = cSocket.getInputStream();
		bin = new BufferedReader(new InputStreamReader(inStream));
		
		proxyPort = Integer.parseInt(bin.readLine());
		code = Integer.parseInt(bin.readLine());
		port = Integer.parseInt(bin.readLine());
		senderIp = cSocket.getInetAddress().toString().substring(1);
		
		switch(code){
			/*
			 * Chord node created
			 */
			case ConsoleProtocol.CREATED:
				System.out.println("--> Chord node on "+senderIp+" on port "+
						port+" created successfully");
				System.out.print("$>");
				Nodes newNode = new Nodes(senderIp, port, proxyPort);
				nodesList.add(newNode);
				break;
			/*
			 * Chord node not created
			 */
			case ConsoleProtocol.NOT_CREATED:
				System.out.println("--> Chord node on "+senderIp+" on port "+
						port+" did not created");
				System.out.print("$>");
				break;
			/*
			 * Chord node stopped
			 */
			case ConsoleProtocol.STOPPED:
				System.out.println("--> Chord node on "+senderIp+" on port "+
						port+" stopped successfully");
				System.out.print("$>");
				Nodes tmpNode = null;
				Nodes curNode = null;
				Iterator<Nodes> nodesIt = nodesList.iterator();
				while(nodesIt.hasNext()){
					tmpNode = nodesIt.next();
					if(tmpNode.getIpAddr().equals(senderIp) && tmpNode
							.getPort() == port){
						curNode = tmpNode;
						break;
					}
				}
				nodesList.remove(curNode);
				break;
			/*
			 * Chord node not stopped
			 */
			case ConsoleProtocol.NOT_STOPPED:
				System.out.println("--> Chord node on "+senderIp+" on port "+
						port+" did not stopped");
				System.out.print("$>");
				break;
			/*
			 * Get statistics from chord node
			 */
			case ConsoleProtocol.NODE_STAT:
				System.out.println("Statistics Received");
				int storingListSize = Integer.parseInt(bin.readLine());
				int retrievingListSize = Integer.parseInt(bin.readLine());
				int servicePort = Integer.parseInt(bin.readLine());
				long totalChunkSize = Long.parseLong(bin.readLine());
				long startTimeLong = Long.parseLong(bin.readLine());
				DateTime startTime = new DateTime(startTimeLong);
				DateTime currentTime = new DateTime();
				String ipAddr = cSocket.getInetAddress().toString().substring(1);
				Statistics tmpStat = createStat(storingListSize, 
						retrievingListSize, ipAddr, servicePort, totalChunkSize,
						startTime, currentTime);
				storeStats(tmpStat);
				
				break;
		}
		bin.close();
		}catch(IOException ex){
			ex.printStackTrace();
		}
	}
}
