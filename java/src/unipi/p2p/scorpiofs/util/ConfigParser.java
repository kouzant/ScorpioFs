package unipi.p2p.scorpiofs.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigParser {
	private static Properties props = new Properties();
	
	public ConfigParser(String configFile) throws IOException{
			InputStream is = new FileInputStream (configFile);
			props.load(is);
		
		}
	
	public int getPort(){
		return Integer.parseInt(props.getProperty("port", "6789"));
	}
	public String getBootstrap(){
		String bootstrapURL;
		bootstrapURL=props.getProperty("bootstrap");
		if (bootstrapURL != null){
			if (bootstrapURL.equals("")){
				return null;
			}
			else{
				return bootstrapURL;
			}
		}
		else{
			return bootstrapURL;
		}
	}
	public String getOutputDir(){
		String outputDir = props.getProperty("outputdir");
		if (outputDir == null || outputDir.equals("")) {
			return props.getProperty("outputdir", "/tmp/");
		} else {
			return outputDir;
		}
	}
	public String getHashtableFile(){
		String hashtable = props.getProperty("hashtable");
		if (hashtable == null || hashtable.equals("")) {
			return props.getProperty("hashtable", "/tmp/chord.hashtable");
		} else {
			return hashtable;
		}
	
	}
	
	public String getRealIP(){
		String realIP;
		realIP=props.getProperty("external_ip",null);
		if (realIP != null){
			if (realIP.equals("")){
				return null;
			}
			else {
				return realIP;
			}
		}
		else{
			return realIP;
		}
	}
}
