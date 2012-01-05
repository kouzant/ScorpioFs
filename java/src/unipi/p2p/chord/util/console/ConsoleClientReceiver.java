package unipi.p2p.chord.util.console;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.LinkedList;

public class ConsoleClientReceiver implements Runnable {
	private static boolean running = true;
	private LinkedList<Nodes> nodesList;
	public ConsoleClientReceiver(LinkedList<Nodes> nodesList){
		this.nodesList = nodesList;
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
			BufferedReader bin = null;
			while(running){
				int code = -1;
				int port = -1;
				int proxyPort = -1;
				String senderIp = null;
				bin = null;
				cSocket = sSocket.accept();
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
			}
			bin.close();
			sSocket.close();
		}catch(IOException e0){
			e0.printStackTrace();
		}
	}
}
