package unipi.p2p.chord.util.console;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class ConsoleClientReceiver implements Runnable {
	private static boolean running = true;
	
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
				String senderIp = null;
				bin = null;
				cSocket = sSocket.accept();
				bin = new BufferedReader(new InputStreamReader
						(cSocket.getInputStream()));
				code = Integer.parseInt(bin.readLine());
				port = Integer.parseInt(bin.readLine());
				senderIp = cSocket.getInetAddress().toString().substring(1);
				
				switch(code){
					case ConsoleProtocol.CREATED:
						System.out.println("Chord node on "+senderIp+" on port "+
								port+" created successfully");
						System.out.print("$>");
						break;
					case ConsoleProtocol.NOT_CREATED:
						System.out.println("Chord node on "+senderIp+" on port "+
								port+" did not created");
						System.out.print("$>");
						break;
					case ConsoleProtocol.STOPPED:
						System.out.println("Chord node on "+senderIp+" on port "+
								port+" stopped successfully");
						System.out.print("$>");
						break;
					case ConsoleProtocol.NOT_STOPPED:
						System.out.println("Chord node on "+senderIp+" on port "+
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
