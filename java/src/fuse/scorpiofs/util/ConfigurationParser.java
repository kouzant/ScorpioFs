package fuse.scorpiofs.util;

import java.util.Properties;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ConfigurationParser {
	private static Properties properties=new Properties();
	
	public ConfigurationParser(String filename){
		try{
			FileInputStream fis=new FileInputStream(filename);
			properties.load(fis);
		}catch(FileNotFoundException e0){
			System.err.println("Could not load ScorpioFS configuration file");
			System.exit(1);
		}catch(IOException e1){
			System.err.println("Could not load ScorpioFS configuration file");
			System.exit(1);
		}
	}
	
	public String getPersonalDir(){
		String defaultDir=Constants.USER_HOME+"/.scorpiofs";
		return properties.getProperty("personalDir",defaultDir);
	}
	
	public String getInterFilename(){
		String defaultInterFile=Constants.USER_HOME+"/.scorpiofs/interFile";
		return properties.getProperty("interFileName",defaultInterFile);
	}
	
	public String getfsTreeName(){
		String defaultFsTreeName=Constants.USER_HOME+"/.scorpiofs/fsTree";
		return properties.getProperty("fsTreeName",defaultFsTreeName);
	}
}
