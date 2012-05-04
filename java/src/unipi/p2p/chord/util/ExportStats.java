package unipi.p2p.chord.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;

import unipi.p2p.chord.util.Statistics;

public class ExportStats {
	private LinkedList<Statistics> nodeStats = null;
	
	public ExportStats(LinkedList<Statistics> nodeStats){
		this.nodeStats = nodeStats;
		File dir = new File("stats");
		
		if(!dir.exists()){
			dir.mkdir();				
		}
	}

	private String makeTitle(String ipAddr){
		Date time = new Date();
		DateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
		String title = ipAddr+"_"+dFormat.format(time);
		
		return title;
	}
	
	private void write(String title, String ipAddr, long storingSize, 
			long retrievingSize, float chSizeMB){
		String path = "stats/"+title;
		DecimalFormat df = new DecimalFormat("##.#");
		try{
			FileWriter fwrite = new FileWriter(path);
			BufferedWriter bout = new BufferedWriter(fwrite);
			
			bout.write("\tScorpioFS Statistics");
			bout.newLine();
			bout.write("IP Address: "+ipAddr);
			bout.newLine();
			bout.write("Storing List Size: "+storingSize);
			bout.newLine();
			bout.write("Retrieving List Size: "+retrievingSize);
			bout.newLine();
			bout.write("Total Disk Usage: "+df.format(chSizeMB)+"MB");
			bout.newLine();
			
			bout.close();
		}catch(IOException ex){
			ex.printStackTrace();
		}
	}
	
	public void exportStat(){
		Iterator<Statistics> statsIt = nodeStats.iterator();
		Statistics tmpNode = null;
		while(statsIt.hasNext()){
			tmpNode = statsIt.next();
			String ipAddr = tmpNode.getIpAddr().getHostAddress();
			long storingSize = tmpNode.getStoringListSize();
			long retrievingSize = tmpNode.getRetrievingListSize();
			long totalChunkSize = tmpNode.getTotalChunkSize();
			float chSizeMB = totalChunkSize / 1048576;
			String title = makeTitle(ipAddr);
			
			write(title, ipAddr, storingSize, retrievingSize, chSizeMB);
		}
	}
}
