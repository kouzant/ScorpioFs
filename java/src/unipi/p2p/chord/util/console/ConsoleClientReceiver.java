package unipi.p2p.chord.util.console;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

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
			sSocket = new ServerSocket(ConsoleProtocol.CL_REC);
			System.out.println("I'm gonna sleep now");
			TimeUnit.SECONDS.sleep(5);
			while(running){
				int code = -1;
				int port = -1;
				String senderIp = null;
				BufferedReader bin = null;
				cSocket = sSocket.accept();
				bin = new BufferedReader(new InputStreamReader
						(cSocket.getInputStream()));
				code = Integer.parseInt(bin.readLine());
				port = Integer.parseInt(bin.readLine());
				senderIp = bin.readLine();
			}
		}catch(IOException e0){
			e0.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

}
