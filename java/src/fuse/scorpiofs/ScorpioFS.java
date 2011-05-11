package fuse.scorpiofs;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import unipi.p2p.chord.ChordNode;
import unipi.p2p.chord.Finger;
import unipi.p2p.chord.ObjectDiskIO;
import unipi.p2p.chord.RemoteChordNode;
import unipi.p2p.chord.util.Util;
import unipi.p2p.chord.Storage;

import com.sun.security.auth.module.UnixSystem;

import fuse.Errno;
import fuse.Filesystem3;
import fuse.FuseDirFiller;
import fuse.FuseException;
import fuse.FuseFtype;
import fuse.FuseGetattrSetter;
import fuse.FuseMount;
import fuse.FuseOpenSetter;
import fuse.FuseStatfsSetter;
import fuse.scorpiofs.util.ChunkNetwork;
import fuse.scorpiofs.util.ConfigurationParser;
import fuse.scorpiofs.util.Constants;
import fuse.scorpiofs.util.DataCache;
import fuse.scorpiofs.util.DataObject;
import fuse.scorpiofs.util.FileCrypto;
import fuse.scorpiofs.util.Initialize;
import fuse.scorpiofs.util.Finalize;
import fuse.scorpiofs.util.ZipCompress;
import fuse.scorpiofs.util.FsNode;
import fuse.scorpiofs.util.FsTree;
import fuse.scorpiofs.util.FsTreeChunks;
import fuse.scorpiofs.util.RetrieveChunks;
import fuse.scorpiofs.util.test.Tree;

public class ScorpioFS implements Filesystem3{
	private static final Log log = LogFactory.getLog(ScorpioFS.class);
	private static RemoteChordNode localChordNode;
	private static int currentUID;
	private static int currentGID;
	public FsTree my_tree = new FsTree(); //or existing fstree...
	public Tree fs_tree = new Tree();
	private static DataCache cache = new DataCache();
	public ScorpioFS(String la){

	}
	public int getattr(String path, FuseGetattrSetter getattrSetter) throws FuseException{
		log.info("called gettattr path:"+path);
		FsNode temp_node;

		temp_node = my_tree.getNode(path);

		//temp_node.datalist.setLocalChordObj(localChordNode);

		if (temp_node == null) {return 2;}
		//getattrSetter.set(1, temp_node.isDir()? FuseFtype.TYPE_DIR | 0755 : FuseFtype.TYPE_FILE | 0644, 1, (int)currentUID, (int)currentGID, 1, temp_node.getSize(), 1, 541036800, 541036800, 541036800);
		getattrSetter.set(1,temp_node.getMode(), 1, (int)currentUID, (int)currentGID, 1, temp_node.getSize(), temp_node.datalist.getListSize() * 2048, 541036800, 541036800, 541036800);
		/*
		 * for each part in datalist, increase popularity by 1.
		 */
		if (!temp_node.isDir()){
			temp_node.datalist.setLocalChordObj(localChordNode);
		//	temp_node.datalist.increasePopularityAll(1);
		}
		//getattrSetter.set(inode, mode, nlink, uid, gid, rdev, size, blocks, atime, mtime, ctime);
		return 0;
	}

	public int readlink(String path, CharBuffer link) throws FuseException{
		log.info("called readlink");
		return 1;
	}

	public int getdir(String path, FuseDirFiller dirFiller) throws FuseException{
		log.info("called getdir path:"+path);
		FsNode temp_node = my_tree.getNode(path);
		temp_node.datalist.setLocalChordObj(localChordNode);
		Collection children = temp_node.getChildren();
		Iterator iter = children.iterator();
		dirFiller.add(".", 1092, FuseFtype.TYPE_DIR | 0755);
		dirFiller.add("..", 10, FuseFtype.TYPE_DIR | 0755);
		while (iter.hasNext()){
			FsNode childNode = (FsNode)iter.next();
			dirFiller.add(childNode.getName(), 1048, childNode.isDir()? FuseFtype.TYPE_DIR | 0755 : FuseFtype.TYPE_FILE | 0644);
		}
		return 0;
	}

	public int mknod(String path, int mode, int rdev) throws FuseException{
		log.info("called mknod path:"+path);

		FsNode temp_node = new FsNode();
		//String[] pathParts = path.split("/");
		String parentPath = path.substring(0,path.lastIndexOf("/"));
		String filename = path.substring(path.lastIndexOf("/")+1);
		log.info("Parent Path: "+ parentPath);
		log.info("Filename: "+ filename);
		FsNode parentNode = my_tree.getNode(parentPath);

		temp_node.setDir(false);
		temp_node.setSize(0);
		temp_node.setName(filename);
		temp_node.setPath(path);
		temp_node.setParent(parentNode);
		temp_node.setMode(FuseFtype.TYPE_FILE | 0644);


		//temp_node.datalist.setLocalChordObj(localChordNode);

		parentNode.addChild(temp_node);

		DataObject obj = new DataObject(0);
		obj.setFilename(path);
		obj.setDatalist(temp_node.datalist);
		temp_node.datalist.putDataObject(obj);

		// new fs tree
		
		
		fuse.scorpiofs.util.test.Node tempNode = new fuse.scorpiofs.util.test.Node();
		fuse.scorpiofs.util.test.Node parent_Node = fs_tree.getNode(parentPath);
		tempNode.isDir = false;
		tempNode.size = 0;
		tempNode.name = filename;
		tempNode.path = path;
		tempNode.parent = null;
		tempNode.mode = FuseFtype.TYPE_FILE | 0644;
		parent_Node.addChild(tempNode);
		return 0;
		
	}

	public int mkdir(String path, int mode) throws FuseException{
		log.info("called mkdir path:" +path);
		FsNode temp_node = new FsNode();
		String parentPath = path.substring(0,path.lastIndexOf("/"));
		String filename = path.substring(path.lastIndexOf("/")+1);

		FsNode parentNode = my_tree.getNode(parentPath);

		temp_node.setDir(true);
		temp_node.setSize(0);
		temp_node.setName(filename);
		temp_node.setPath(path);
		temp_node.setParent(parentNode);
		temp_node.setMode(FuseFtype.TYPE_DIR | 0755);
		parentNode.addChild(temp_node);
		
		// new fs tree
		
		fuse.scorpiofs.util.test.Node tempNode = new fuse.scorpiofs.util.test.Node();
		fuse.scorpiofs.util.test.Node parent_Node = fs_tree.getNode(parentPath);
		tempNode.isDir = true;
		tempNode.size = 0;
		tempNode.name = filename;
		tempNode.path = path;
		tempNode.parent = null;
		tempNode.mode = FuseFtype.TYPE_DIR | 0755;
		parent_Node.addChild(tempNode);
		
		return 0;
	}

	public int unlink(String path) throws FuseException{
	log.info("called unlink path:"+path);

		String parentPath = path.substring(0,path.lastIndexOf("/"));
		String filename = path.substring(path.lastIndexOf("/")+1);

		FsNode parentNode = my_tree.getNode(parentPath);
		FsNode temp_node = my_tree.getNode(path);
		temp_node.datalist.setLocalChordObj(localChordNode);
		if (!temp_node.isDir()){
			temp_node.datalist.deleteAll();
		}
		parentNode.rmChild(filename);

		return 0;
	}

	public int rmdir(String path) throws FuseException{
		log.info("called rmdir");
		String parentPath = path.substring(0,path.lastIndexOf("/"));
		String filename = path.substring(path.lastIndexOf("/")+1);

		FsNode parentNode = my_tree.getNode(parentPath);

		parentNode.rmChild(filename);
		return 0;
	}

	public int symlink(String from, String to) throws FuseException{
		log.info("called symlink");
		return 1;
	}

	public int rename(String from, String to) throws FuseException{
		log.info("called rename path:"+from+" to:"+to);
		String filename_to = to.substring(to.lastIndexOf("/")+1);
		String filename_from = from.substring(from.lastIndexOf("/")+1);
		String parentPath_to = to.substring(0,to.lastIndexOf("/"));
		String parentPath_from = from.substring(0,from.lastIndexOf("/"));



		FsNode temp_node = my_tree.getNode(from);
		temp_node.setName(filename_to);
		temp_node.setPath(to);
		temp_node.setPath(parentPath_to);
		FsNode parentNode = my_tree.getNode(parentPath_from);
		parentNode.rmChild(filename_from);
		parentNode = my_tree.getNode(parentPath_to);
		parentNode.addChild(temp_node);




		return 0;
	}

	public int link(String from, String to) throws FuseException{
		log.info("called link");
		return 1;
	}

	public int chown(String path, int uid, int gid) throws FuseException{
		log.info("called chown");
		return 0;
	}

	public int truncate(String path, long size) throws FuseException{
		log.info("called truncate path:"+path+" size="+size);

		FsNode temp_node = my_tree.getNode(path);
		temp_node.datalist.setLocalChordObj(localChordNode);
		temp_node.datalist.truncate(size);

		return 0;
	}

	public int chmod(String path, int mode) throws FuseException{
		log.info("called chmod path:"+path+" mode="+mode);
		FsNode temp_node = my_tree.getNode(path);
		temp_node.setMode(mode);
		return 0;
	}
	public int utime(String path, int atime, int mtime) throws FuseException{
		log.info("called utime");
		return 0;
	}

	public int statfs(FuseStatfsSetter statfsSetter) throws FuseException{
		log.info("called statfs");
		my_tree.calcTotalBlocksAndInodes();
		log.info("Total Blocks: "+my_tree.getTotalBlocks());
		statfsSetter.set(1048576, Integer.MAX_VALUE, Integer.MAX_VALUE - my_tree.getTotalBlocks(),Integer.MAX_VALUE - my_tree.getTotalBlocks(),my_tree.getTotalInodes(),Integer.MAX_VALUE - my_tree.getTotalInodes(), 256);
		//statfsSetter.set(blockSize, blocks, blocksFree, blocksAvail, files, filesFree, namelen)
		return 0;
	}

	// if open returns a filehandle by calling FuseOpenSetter.setFh() method, it will be passed to every method that supports 'fh' argument
	public int open(String path, int flags, FuseOpenSetter openSetter) throws FuseException{
		log.info("called open path:"+path);

		return 0;
	}

	// fh is filehandle passed from open
	public int read(String path, Object fh, ByteBuffer buf, long offset) throws FuseException{
		log.info("called read ,offset: "+offset+" length="+buf.capacity());
		boolean EOF = false;
		FsNode temp_node = my_tree.getNode(path);
		temp_node.datalist.setLocalChordObj(localChordNode);
		DataObject obj;
		long bytes;
		while (buf.remaining()>0 && !EOF){
			obj = temp_node.datalist.getDataObject(offset + buf.position());
			//   bytes=obj.getBytesFilled()+obj.getFirstByte();
			if (offset+buf.position() == temp_node.getSize()){
				break;
			}
			log.info("buffer position="+buf.position());
			if (obj != null  && obj.getBytesFilled()>0){
				log.info("reading from offset="+(buf.position()+offset));
				log.info("bytepos="+(obj.getBytesFilled()+obj.getFirstByte()));
				try{
					obj.readBytes(buf, offset+buf.position());
				}
				catch (Exception e){
					log.error("Read Error");
					return Errno.EIO;
				}

			}
			else{
				break;
			}
		}


		return 0;

	}

	// fh is filehandle passed from open,
	// isWritepage indicates that write was caused by a writepage
	public int write(String path, Object fh, boolean isWritepage, ByteBuffer buf, long offset) throws FuseException{
		//The New Write :)
		log.info("called write path:"+path+" offset="+offset + "  size="+buf.capacity());
		FsNode temp_node = my_tree.getNode(path);
		temp_node.datalist.setLocalChordObj(localChordNode);
		DataObject obj;


		obj = temp_node.datalist.getDataObject(offset);
		if (obj == null){
			obj = new DataObject(offset);
			obj.setFilename(path);
			obj.setDatalist(temp_node.datalist);
			temp_node.datalist.putDataObject(obj);
		}


		obj.writeBytes(buf, offset);
		//temp_node.datalist.putDataObject(obj);

		return 0;
	}

	// called on every filehandle close, fh is filehandle passed from open
	public int flush(String path, Object fh) throws FuseException{
		log.info("called flush path:"+path);
		return 0;
	}

	// called when last filehandle is closed, fh is filehandle passed from open
	public int release(String path, Object fh, int flags) throws FuseException{
		log.info("called release path:"+path);
		
		
		
		try{
		FsNode temp_node = my_tree.getNode(path);
		
		
		temp_node.datalist.setLocalChordObj(localChordNode);

		temp_node.setSize(temp_node.datalist.getFilesize());
		temp_node.datalist.allToNetwork();
		} catch (Exception e){e.printStackTrace();}
		
		//log.info("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!:"+path);
		return 0;
	}

	// Synchronize file contents, fh is filehandle passed from open,
	// isDatasync indicates that only the user data should be flushed, not the meta data
	public int fsync(String path, Object fh, boolean isDatasync) throws FuseException{
		log.info("called fsync");
		return 0;
	}
	
	public void saveFstreeToFile(String filename){
		ObjectDiskIO objectWriter = new ObjectDiskIO();
		try {
			objectWriter.saveObject(my_tree, new File(filename));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println("ERROR:\tCannot write to "+filename);
		}
		
	}
	
	void tokenize(String fsTreeName){
		File fsTree=new File(fsTreeName);
		System.out.println("fsTree size: "+fsTree.length());
		byte[] buffer=new byte[1048576];
		FsTreeChunks fsc=new FsTreeChunks();
		try{
			FileInputStream fis=new FileInputStream(fsTree);
			try{
				log.info("fis.available: "+fis.available());
				//Encrypt fstree
				FileCrypto fc=new FileCrypto();
				File encFile=new File(fsTreeName+".enc");
				FileOutputStream encFos=new FileOutputStream(encFile);
				fc.encrypt(fis, encFos);
				fis.close();
				encFos.close();
				fis=new FileInputStream(encFile);
				//Remove existing dataID
				fsc.delIDs();
				if(fis.available()>1048576){
					while(fis.available()>0){
						fis.read(buffer);
						//Send chunks to network
						//Make use of localChordNode
						//The try block is temporary. Here will send packets to the network
						/*try{
							FileOutputStream fos=new FileOutputStream(tmpStore);
							try{
								fos.write(buffer);
								fos.flush();
								fos.close();
							}catch(IOException e0){
								e0.printStackTrace();
							}
						}catch(FileNotFoundException e0){
							e0.printStackTrace();
						}catch(SecurityException e1){
							e1.printStackTrace();
						}*/
						
						BigInteger dataID=ChunkNetwork.toNetwork(buffer, localChordNode);
						fsc.setID(dataID);
					}
				}else{
					//fstree less than 1MB
					fis.read(buffer);
					BigInteger dataID=ChunkNetwork.toNetwork(buffer, localChordNode);
					fsc.setID(dataID);
				}
				/*Iterator<BigInteger> it=fsc.getIDs();
				while(it.hasNext()){
					log.info("Chunk ID: "+it.next());
				}*/
			}catch(IOException e0){
				e0.printStackTrace();
			}
			
			ObjectDiskIO objectWriter = new ObjectDiskIO();
			try {
				objectWriter.saveObject(fsc, new File("/home/antonis/.scorpiofs/interFs"));
				FsTreeChunks c = (FsTreeChunks)objectWriter.loadObject(new File("/home/antonis/.scorpiofs/interFs"));
				log.info("Succesfully saved interFs " + c.getIDs().next().toString());
			} catch (Exception e) {
				log.error("error creating interFs");
				e.printStackTrace();
				// TODO Auto-generated catch block
				System.err.println("ERROR:\tCannot write interFs");
			}
			objectWriter=null;
		}catch(FileNotFoundException e0){
			e0.printStackTrace();
		}
	}
	
	private void setFstree(FsTree tree){
		this.my_tree = tree;
	}


	public static void main(String[] args){

		// String fuseArgs[] = new String[args.length];
		// System.arraycopy(args, 0, fuseArgs, 0, fuseArgs.length);
		UnixSystem unixobj = new UnixSystem();
		currentUID = (int) unixobj.getUid();
		currentGID = (int) unixobj.getGid();

		// parse cmd args

		String argument;
		String port = null;
		String fstree = null;
		String mountpoint = null;
		String configFile=null;
		Boolean init=false;
		int argCount = 0;
		int i;
		
		for (i = 0; i < args.length; i++) {
			if (args[i].startsWith("-")) {
				argument = args[i];
				i++;
				if (argument.equals("-port")) {
					argCount++;
					if (i < args.length) {
						argCount++;
						port = args[i];
					}
				}

				if (argument.equals("-fstree")) {
					argCount++;
					if (i < args.length) {
						argCount++;
						fstree = args[i];
					}
				}
				
				if(argument.equals("-init")){
					init=true;
					argCount++;
					i--;
				}
				
				if(argument.equals("-config")){
					argCount++;
					if(i<args.length){
						argCount++;
						configFile=args[i];
					}
				}

			} else {
				if (i == (args.length - 1)) {
					argCount++;
					mountpoint = args[i];

				}
			}
		}
		if ((i != argCount) || (i < 5)) {
			System.out.println("Invalid number of arguments");
			System.exit(-1);
		}

		String fuseArgs[] = new String[2];
		fuseArgs[0] = "-f";
		fuseArgs[1] = mountpoint;

		/*
		 * check if fstree file exists true - load fstree false - create file
		 */
		
		

		/*
		 * Chord Stuff
		 */
		Constants.setReplicationFactor(3);

		try {
			localChordNode = (RemoteChordNode) Naming.lookup("rmi://"
					+ "localhost:" + port + "/unipi.p2p.chord.ChordNode");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			System.err.println("ERROR:\tCan't connect to local chord node");
			System.exit(-1);
		}
		
		ScorpioFS fs = new ScorpioFS(mountpoint);
		ConfigurationParser cp=new ConfigurationParser(configFile);
		Constants.setPersonalDir(cp.getPersonalDir());
		Constants.setInterFileName(cp.getInterFilename());
		Constants.setFsTreeName(cp.getfsTreeName());
		
		if(init){
			new Initialize();
		}else{
			String zipFileName=Constants.personalDir.concat("/secretDir.zip");
			FileCrypto fc=new FileCrypto();
			try{
				FileInputStream fis=new FileInputStream(zipFileName.concat(".enc"));
				FileOutputStream fos=new FileOutputStream(zipFileName);
				fc.decrypt(fis, fos);
				try{
					fos.flush();
					fos.close();
					fis.close();
				}catch(IOException e){
					e.printStackTrace();
					System.exit(1);
				}
			}catch(FileNotFoundException e){
				e.printStackTrace();
				System.exit(1);
			}
			ZipCompress zc=new ZipCompress();
			zc.unZip(zipFileName);
			
			//new method
			File ifn=new File(Constants.interFileName);
			if(ifn.exists()){
				log.info("Just before revoking fstree chunks");
				RetrieveChunks lala=new RetrieveChunks();
				File newFsTree=lala.koko(localChordNode,ifn);
				ObjectDiskIO objectReader=new ObjectDiskIO();
				if(newFsTree==null){
					log.info("NEWFSTREE IS NULL!");
				}
				try{
					fs.my_tree=(FsTree)objectReader.loadObject(newFsTree);
				}catch(Exception e){
					log.info("Error in deserialization of newFsTree");
					e.printStackTrace();
				}
			}
		}
		
		
		
		//TO BE FIXED
		fstree=Constants.fsTreeName;
		
		
		
		/*File fsTree=new File(fstree);
		if(fsTree.exists()){
			ObjectDiskIO objectReader = new ObjectDiskIO();
			try {
				fs.my_tree = (FsTree)objectReader.loadObject(fsTree);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			System.out.println("total inodes=" + fs.my_tree.getTotalInodes());

			File interFile=new File(Constants.interFileName);
			FsTreeChunks ftc=new FsTreeChunks();
			if(interFile.exists()){
				try{
					ftc=(FsTreeChunks)objectReader.loadObject(interFile);
					Iterator<String> it=ftc.getIDs();
					while(it.hasNext()){
						System.out.println(it.next());
					}
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}else{
			System.out.println("Fetch fstree from network");
		}*/
		
		try {
			FuseMount.mount(fuseArgs,fs, null);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("ERROR:\tmounting scorpiofs failed");
		}
		
		
		System.gc();
		fs.my_tree.calcTotalBlocksAndInodes();
		fs.my_tree.rootNode.printStatus();
		
		fs.saveFstreeToFile(Constants.fsTreeName);
		try{
			TimeUnit.SECONDS.sleep(1);
		}catch(InterruptedException e){
			e.printStackTrace();
		}
		fs.tokenize(Constants.fsTreeName);
		try{
			TimeUnit.SECONDS.sleep(1);
		}catch(InterruptedException e){
			e.printStackTrace();
		}
		
		new Finalize();
		
		//Tree tmptree = new Tree();
		//tmptree.root = fuse.scorpiofs.util.test.utils.CopyNode(fs.my_tree.rootNode, null);
		//fuse.scorpiofs.util.test.utils.printTree(tmptree.root);
		/*/fuse.scorpiofs.util.test.utils.printTree(fs.fs_tree.root);
		/ObjectDiskIO objectWriter = new ObjectDiskIO();
		try {
			objectWriter.saveObject(fs.fs_tree, "/tmp/tmptree");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println("ERROR:\tCannot write to "+ "/tmp/tmptree");
		}
		*/
		
		//System.out.println("total inodes=" + String.valueOf(fs.my_tree.getTotalInodes()));
		System.out.println("Exit...");

	}

}
