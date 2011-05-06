package fuse.scorpiofs.util;

import java.util.*;
import java.util.zip.*;
import java.io.*;

public class ZipCompress {
	private static byte[] buffer=new byte[256];
	public void zip(List<String> args){
		Iterator<String> it=args.iterator();
		String zipFileName=it.next();
		try{
			FileOutputStream fos=new FileOutputStream(zipFileName);
			CheckedOutputStream csumo=new CheckedOutputStream(fos, new Adler32());
			ZipOutputStream zos=new ZipOutputStream(csumo);
			BufferedOutputStream bos=new BufferedOutputStream(zos);
			zos.setComment("ScorpioFS internal file");
			while(it.hasNext()){
				String fileName=it.next();
				System.out.println("Writing file: "+fileName);
				InputStream in=new BufferedInputStream(new FileInputStream(fileName));
				try{
					zos.putNextEntry(new ZipEntry(fileName));
					int c;
					
					buffer = new byte[256];
					
					while((c=in.read(buffer))!=-1)
						bos.write(buffer,0, c);
					in.close();
					bos.flush();
				}catch(IOException e0){
					System.err.println("ZipEntry Section Error!");
					e0.printStackTrace();
					System.exit(1);
				}
			}
			bos.close();
			System.out.println("File checksum: "+csumo.getChecksum().getValue());
		}catch(FileNotFoundException e0){
			e0.printStackTrace();
			System.exit(1);
		}catch(IOException e1){
			e1.printStackTrace();
			System.exit(1);
		}
	}
	
	public void unZip(String zipFileName){
		try{
			FileInputStream fis=new FileInputStream(zipFileName);
			CheckedInputStream csumi=new CheckedInputStream(fis, new Adler32());
			ZipInputStream zis=new ZipInputStream(csumi);
			BufferedInputStream bis=new BufferedInputStream(zis);
			ZipEntry ze;
			
			try{
				while((ze=zis.getNextEntry())!=null){
					System.out.println("Extracting file: "+ze);
					int c;
					BufferedOutputStream bos=new BufferedOutputStream(new FileOutputStream(ze.getName()));

					buffer=new byte[256];
					
					while((c=bis.read(buffer))!=-1){
						bos.write(buffer,0,c);
						bos.flush();
					}
					bos.close();
				}
				bis.close();
			}catch(IOException e){
				System.err.println("Error in extracting files");
				e.printStackTrace();
				System.exit(1);
			}
			System.out.println("Checksum: "+csumi.getChecksum().getValue());
		}catch(FileNotFoundException e0){
			System.err.println("Zip file could NOT be found");
			System.exit(1);
		}
	}
}
