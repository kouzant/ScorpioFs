package unipi.p2p.chord.util.console;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/*
 * Class which implements the tcp server for return messages
 */
public class ConsoleClientReceiver implements Runnable {
	private static boolean running = true;
	private volatile LinkedList<Nodes> nodesList;
	private ExecutorService exec = null;
	public ConsoleClientReceiver(LinkedList<Nodes> nodesList){
		this.nodesList = nodesList;
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
					ThreadedServer tServer = new ThreadedServer(cSocket, nodesList);
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
	BufferedReader bin = null;
	
	public ThreadedServer(Socket cSocket, LinkedList<Nodes> nodesList){
		this.cSocket = cSocket;
		this.nodesList = nodesList;
	}
	@Override
	public void run(){
		int code = -1;
		int port = -1;
		int proxyPort = -1;
		String senderIp = null;
		bin = null;
		try{
		bin = new BufferedReader(new InputStreamReader
				(cSocket.getInputStream()));
		proxyPort = Integer.parseInt(bin.readLine());
		code = Integer.parseInt(bin.readLine());
		port = Integer.parseInt(bin.readLine());
		senderIp = cSocket.getInetAddress().toString().substring(1);
		
		switch(code){
			case ConsoleProtocol.CREATED:
				System.out.println("--> Chord node on "+senderIp+" on port "+
						port+" created successfully");
				System.out.print("$>");
				Nodes newNode = new Nodes(senderIp, port, proxyPort);
				nodesList.add(newNode);
				break;
			case ConsoleProtocol.NOT_CREATED:
				System.out.println("--> Chord node on "+senderIp+" on port "+
						port+" did not created");
				System.out.print("$>");
				break;
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
			case ConsoleProtocol.NOT_STOPPED:
				System.out.println("--> Chord node on "+senderIp+" on port "+
						port+" did not stopped");
				System.out.print("$>");
				break;
		}
		bin.close();
		}catch(IOException ex){
			ex.printStackTrace();
		}
	}
}
