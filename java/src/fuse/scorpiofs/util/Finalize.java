package fuse.scorpiofs.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Finalize {
	public Finalize(){		
		List<String> zipArgs=new ArrayList<String>();
		//First	insert the zip file name
		zipArgs.add(Constants.personalDir.concat("/secretDir.zip"));
		//Next all the files we want to be included in the zip file
		zipArgs.add(Constants.interFileName);
		zipArgs.add(Constants.personalDir.concat("/.iv"));
		zipArgs.add(Constants.personalDir.concat("/.salt"));
		System.out.println("Archiving...");
		ZipCompress zc=new ZipCompress();
		zc.zip(zipArgs);
		//Remove unwanted files
		new File(Constants.fsTreeName).delete();
		new File(Constants.interFileName).delete();
		new File(Constants.fsTreeName.concat(".enc")).delete();
		new File(Constants.personalDir.concat("/.iv")).delete();
		new File(Constants.personalDir.concat("/.salt")).delete();
		//Encrypt newly created zip file
		FileCrypto fc=new FileCrypto();
		try{
			FileInputStream fis=new FileInputStream(Constants.personalDir.concat("/secretDir.zip"));
			FileOutputStream fos=new FileOutputStream(Constants.personalDir.concat("/secretDir.zip.enc"));
			fc.encrypt(fis, fos);
			try{
				fos.flush();
				fos.close();
				fis.close();
			}catch(IOException e){
				e.printStackTrace();
				System.exit(1);
			}
		}catch(FileNotFoundException e){
			System.err.println("zip file does not exist");
			System.exit(1);
		}
		new File(Constants.personalDir.concat("/secretDir.zip")).delete();
	}
}
