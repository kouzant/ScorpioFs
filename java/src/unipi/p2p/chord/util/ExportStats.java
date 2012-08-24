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
import java.util.concurrent.TimeUnit;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

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

	private String makeTitle(String ipAddr, int srvPort){
		Date time = new Date();
		DateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
		String title = ipAddr+":"+srvPort+"_"+dFormat.format(time);
		
		return title;
	}
	
	private void write(String title, String ipAddr, long storingSize, 
			long retrievingSize, float chSizeMB, int srvPort, String uptime, 
			long putRequests, long getRequests, int totalChunks){
		String path = "stats/"+title;
		DecimalFormat df = new DecimalFormat("##.#");
		try{
			FileWriter fwrite = new FileWriter(path);
			BufferedWriter bout = new BufferedWriter(fwrite);
			
			bout.write("\tScorpioFS Statistics");
			bout.newLine();
			bout.write("IP Address: "+ipAddr);
			bout.newLine();
			bout.write("Service Port: "+srvPort);
			bout.newLine();
			bout.write("Uptime: "+uptime);
			bout.newLine();
			bout.write("Storing List Size: "+storingSize);
			bout.newLine();
			bout.write("Retrieving List Size: "+retrievingSize);
			bout.newLine();
			bout.write("Put Requests: "+putRequests);
			bout.newLine();
			bout.write("Get Requests: "+getRequests);
			bout.newLine();
			bout.write("Total Chunks: "+totalChunks);
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
			String ipAddr = tmpNode.getIpAddr();
			long storingSize = tmpNode.getStoringListSize();
			long retrievingSize = tmpNode.getRetrievingListSize();
			long totalChunkSize = tmpNode.getTotalChunkSize();
			float chSizeMB = totalChunkSize / 1048576;
			int srvPort = tmpNode.getServicePort();
			String title = makeTitle(ipAddr, srvPort);
			DateTime startTime = tmpNode.getStartTime();
			DateTime currentTime = tmpNode.getCurrentTime();
			long putRequests = tmpNode.getPutRequests();
			long getRequests = tmpNode.getGetRequests();
			int totalChunks = tmpNode.getTotalChunks();
			Interval interval = new Interval(startTime, currentTime);
			PeriodFormatter formater = new PeriodFormatterBuilder()
		    .appendDays()
		    .appendSuffix(" day", " days")
		    .appendSeparator(" : ")
		    .appendMinutes()
		    .appendSuffix(" minute", " minutes")
		    .appendSeparator(" : ")
		    .appendSeconds()
		    .appendSuffix(" second", " seconds")
		    .toFormatter();
			
			Period period = interval.toPeriod();
			String uptime = formater.print(period.normalizedStandard());
			write(title, ipAddr, storingSize, retrievingSize, chSizeMB, srvPort, 
					uptime, putRequests, getRequests, totalChunks);
		}
	}
}
