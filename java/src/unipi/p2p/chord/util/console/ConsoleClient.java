package unipi.p2p.chord.util.console;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.net.Socket;

public class ConsoleClient {
	public static void main(String[] args) {
		InetAddress iAddr = null;
		try{
			iAddr = InetAddress.getByName(args[0]);
			Socket socket = new Socket(iAddr, ConsoleProtocol.PROXY_PORT);
			PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
			pw.print("1");
			System.out.println("Just sent 1");
			pw.print("2");
			System.out.println("Just sent 2");
			pw.print("3");
			System.out.println("Just sent something");
			pw.close();
			socket.close();
		}catch (UnknownHostException e0){
			e0.printStackTrace();
		}catch (IOException e1){
			e1.printStackTrace();
		}
	}

}
