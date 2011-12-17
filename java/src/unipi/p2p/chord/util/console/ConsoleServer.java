package unipi.p2p.chord.util.console;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import com.sun.imageio.plugins.common.InputStreamAdapter;

public class ConsoleServer {
	public static void main(String[] args) {
		ServerSocket sSocket = null;
		try{
			sSocket = new ServerSocket(ConsoleProtocol.PROXY_PORT);
		}catch (IOException e0){
			e0.printStackTrace();
		}
		Socket cSocket = null;
		boolean proxyUp = true;
		while(proxyUp){
			try{
				cSocket = sSocket.accept();
				BufferedReader bin = new BufferedReader(new InputStreamReader (cSocket.getInputStream()));
				int request = Integer.parseInt(bin.readLine());
				//Just playing here
				if(request == 1){
					System.out.println("1");
				}else if(request == 2){
					System.out.println("2");
				}else {
					System.out.println("Ciao amore!");
					bin.close();
					cSocket.close();
				}
			}catch (IOException e1){
				e1.printStackTrace();
			}
			
		}
	}
}
