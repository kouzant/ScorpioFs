package fuse.scorpiofs.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

import fuse.scorpiofs.ScorpioFS;

import unipi.p2p.chord.ObjectDiskIO;

public class RevokeChunks {
	public RevokeChunks(ScorpioFS fs){
		String interFileLoc="/home/antonis/.scorpiofs/interFs";
		File interFile=new File(interFileLoc);
		if(interFile.exists()){
			ObjectDiskIO objectReader=new ObjectDiskIO();
			byte[] buffer=new byte[1048576];
			try{
				FsTreeChunks ftc=(FsTreeChunks)objectReader.loadObject(interFile);
				Iterator<String> it1=ftc.getIDs();
				Iterator<String> it=ftc.getIDs();
				if(it1.hasNext()){
					System.out.println("ID: "+it1.next());
				}else{
					System.out.println("interFile Error!!!");
				}
				FileInputStream fis;
				FileOutputStream fos=new FileOutputStream("/home/antonis/.scorpiofs/fsTree.enc");
				//write chunks to one file
				while(it.hasNext()){
					fis=new FileInputStream("/tmp/"+it.next());
					System.out.println("RevokeChunks.chunks: "+fis);
					try{
						fis.read(buffer);
						fos.write(buffer);
						fos.flush();
					}catch(IOException e0){
						e0.printStackTrace();
					}
					fis.close();
				}
				fos.close();

				fis=new FileInputStream("/home/antonis/.scorpiofs/fsTree.enc");
				fos=new FileOutputStream("/home/antonis/.scorpiofs/fsTree");
				FileCrypto fc=new FileCrypto();
				fc.decrypt(fis, fos);
				fis.close();
				fos.close();
				//finally deserialize fstree
				try{
					File newFsTree=new File("/home/antonis/.scorpiofs/fsTree");
					fs.my_tree=(FsTree)objectReader.loadObject(newFsTree);
				}catch(Exception e){
					System.out.println("Could NOT read new fstree");
				}
			}catch(Exception e1){
				e1.printStackTrace();
			}
		}else{
			System.out.println("Could NOT find interFs");
		}
	}
}
