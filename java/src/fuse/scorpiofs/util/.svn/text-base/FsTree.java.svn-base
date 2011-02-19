package fuse.scorpiofs.util;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;

import unipi.p2p.chord.ObjectDiskIO;

import fuse.FuseFtype;



@SuppressWarnings("serial")
public class FsTree implements Serializable {
//private FsNode rootNode = new FsNode();
//private FsNode rootNode;
public FsNode rootNode;
private int totalBlocks;
private int totalInodes;
//private Hashtable fs_table = new Hashtable();
//private Hashtable fs_table;
public FsTree() {
	
	rootNode = new FsNode(); //new
	rootNode.setDir(true);
	rootNode.setMode(FuseFtype.TYPE_DIR | 0755);
	rootNode.setParent(null);
	rootNode.setName("");
	rootNode.setPath("/");
	//fs_table = new Hashtable();
	//fs_table.put(rootNode.getPath(), rootNode);
}

public FsNode getNode(String pathname){
	      FsNode node = rootNode;
	      String[] pathParts = pathname.split("/");

	      for (int i = 0; i < pathParts.length; i++)
	      {
	         String pathPart = pathParts[i];
	         if (pathPart.equals("") || pathPart.equals("."))
	         {
	            // the same node
	         }
	         else if (pathPart.equals(".."))
	         {
	            // parent node
	            node = node.getParent();
	         }
	         else
	         {
	            node = node.getChild(pathPart);
	            if (node == null)
	               break;
	         }
	      }

	      return node;
	   }
public void calcTotalBlocksAndInodes(){
	totalBlocks = 0;
	totalInodes = 0;
	FsNode node = rootNode;
	getNodeBlocks(node);
	return;
}
private void getNodeBlocks(FsNode target_node){
	totalInodes++;
	Collection children = target_node.getChildren();
	if (children.isEmpty()){
		totalBlocks += target_node.datalist.getListSize();
		return;
	}
	Iterator iter = children.iterator();
	while (iter.hasNext()){
   		FsNode childNode = (FsNode)iter.next();
   		getNodeBlocks(childNode);
   	}
	return;
}
public int getTotalInodes(){
	return totalInodes;
}
public int getTotalBlocks(){
	return totalBlocks;
}
public void toFile(String filename){
	ObjectDiskIO objectWriter = new ObjectDiskIO();
	try {
		objectWriter.saveObject(this.rootNode, filename);
	} catch (IOException e) {
		// TODO Auto-generated catch block
		System.err.println("ERROR:\tCannot write to "+filename);
	}
	
}
}

