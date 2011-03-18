package fuse.scorpiofs.util;

import java.io.File;
import java.io.IOException;

import fuse.scorpiofs.util.Constants;

public class Initialize {
	public Initialize(){
		System.out.println("Personal Dir: "+Constants.personalDir);
		System.out.println("interFileName: "+Constants.interFileName);
		System.out.println("fsTreeName: "+Constants.fsTreeName);
		
		
		try{
			new File(Constants.personalDir).mkdir();
			System.out.println("Directory "+Constants.personalDir+" just created");
			
			new File(Constants.interFileName).createNewFile();
			System.out.println("File "+Constants.interFileName+" just created");

			new File(Constants.fsTreeName).createNewFile();
			System.out.println("File "+Constants.fsTreeName+" just created");
		}catch(IOException e){
			System.err.println("Could NOT create files");
			System.exit(1);
		}
	}
}
