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
				System.out.print("$>");
				String command = in.nextLine();
				String[] tokens = command.split("[ ]");
				
				//Chord node block
				if(tokens[0].equals("node")){
					if(tokens[1].equals("create")){
						pw.println(ConsoleProtocol.NODE_CREATE);
					}else if(tokens[1].equals("stop")){
						pw.println(ConsoleProtocol.NODE_STOP);
					}else if(tokens[1].equals("stat")){
						pw.println(ConsoleProtocol.NODE_STAT);
					}else if(tokens[1].equals("alive")){
						pw.println(ConsoleProtocol.NODE_ALIVE);
					}else if(tokens[1].equals("list")){
						pw.println(ConsoleProtocol.NODE_LIST);
					}else if(tokens.length < 2){
						System.out.println("Command not found!");
					}else{
						System.out.println("Command not found!");
					}
				//ScorpioFS block
				}else if(tokens[0].equals("scorpiofs")){
					if(tokens[1].equals("mount")){
						//Call ScorpioFS
					}else if(tokens[1].equals("unmount")){
						//System process (fusermount -u)
					}else if(tokens.length < 2){
						System.out.println("Command not found!");
					}else{
						System.out.println("Command not found!");
					}
				//help
				}else if(tokens[0].equals("help")){
					System.out.println("help!");
				//exit
				}else if(tokens[0].equals("terminate")){
					pw.println(ConsoleProtocol.TERMINATE);
					pw.close();
					socket.close();
					consoleUp = false;
				}else{
					System.out.println("Command not found!");
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
