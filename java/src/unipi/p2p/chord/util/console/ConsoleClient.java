package unipi.p2p.chord.util.console;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.net.Socket;
import java.util.Scanner;

public class ConsoleClient {
	public static void main(String[] args) {
		InetAddress iAddr = null;
		boolean consoleUp = true;
		
		try{
			iAddr = InetAddress.getByName(args[0]);
			Socket socket = new Socket(iAddr, ConsoleProtocol.PROXY_PORT);
			PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
			System.out.println("Welcome to ScorpioFS administration console");
			Scanner in = new Scanner(System.in);
			while(consoleUp){
				String command = in.nextLine();
				
				if(command.equals("lala")){
					pw.println(ConsoleProtocol.NODE_STOP);
				}else if(command.equals("koko")){
					pw.println(ConsoleProtocol.NODE_STAT);
				}else if(command.equals("exit")){
					pw.println(ConsoleProtocol.NODE_ALIVE);
					pw.flush();
					pw.close();
					socket.close();
					consoleUp = false;
				}
			}
			System.out.println("GoodBye!");
			System.exit(1);
		}catch (UnknownHostException e0){
			e0.printStackTrace();
		}catch (IOException e1){
			e1.printStackTrace();
		}
	}

}
