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
			sSocket = new ServerSocket(ConsoleProtocol.PROXY_PORT);
		}catch (IOException e0){
			e0.printStackTrace();
		}
		Socket cSocket = null;
		boolean proxyUp = true;
		try{
			cSocket = sSocket.accept();
			BufferedReader bin = new BufferedReader(new InputStreamReader 
					(cSocket.getInputStream()));
			while(proxyUp){
				int request = Integer.parseInt(bin.readLine());
				//Just playing here
				log.info("The request is: "+request);
				if(request == ConsoleProtocol.NODE_STOP){
					log.info("Choice 1");
				}else if(request == ConsoleProtocol.NODE_STAT){
					log.info("Choice 2");
				}else {
					bin.close();
					cSocket.close();
					proxyUp = false;
				}
			}
			System.out.println("Ciao amore!");
			System.exit(1);
		}catch (IOException e1){
			e1.printStackTrace();
		}
		
	}
}
