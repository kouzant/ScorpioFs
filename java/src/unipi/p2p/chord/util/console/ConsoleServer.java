package unipi.p2p.chord.util.console;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ConsoleServer {
	private static final Log log = LogFactory.getLog(ConsoleServer.class);
	public static void main(String[] args) {
		ServerSocket sSocket = null;
		try{
			if(args.length == 2){
				sSocket = new ServerSocket(Integer.parseInt(args[1]));
			}else{
				sSocket = new ServerSocket(ConsoleProtocol.PROXY_PORT);
			}
		}catch (IOException e0){
			e0.printStackTrace();
		}
		Socket cSocket = null;
		boolean proxyUp = true;
		int code = -1;
		int chPort = -1;
		BufferedReader bin = null;
		try{
			
			while(proxyUp){
				cSocket = sSocket.accept();
				bin = new BufferedReader(new InputStreamReader 
						(cSocket.getInputStream()));
				
				code = Integer.parseInt(bin.readLine());
				chPort = Integer.parseInt(bin.readLine());
				log.info("Code is: "+code);
				log.info("Chord Port is: "+chPort);
				
				//Available operations
				switch(code){
				case ConsoleProtocol.NODE_CREATE:
					log.info("node create IP_ADDR");
					bin.close();
					cSocket.close();
					break;
				case ConsoleProtocol.NODE_STOP:
					log.info("node stop IP_ADDR");
					break;
				case ConsoleProtocol.NODE_STAT:
					log.info("node stat IP_ADDR");
					break;
				case ConsoleProtocol.NODE_ALIVE:
					log.info("node alive IP_ADDR");
					break;
				case ConsoleProtocol.NODE_LIST:
					log.info("node list");
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
		}catch (IOException e1){
			e1.printStackTrace();
		}
		
	}
}
