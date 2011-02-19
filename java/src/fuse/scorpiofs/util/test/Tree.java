package fuse.scorpiofs.util.test;

import java.io.Serializable;

import fuse.FuseFtype;
import fuse.scorpiofs.util.FsNode;


public class Tree implements Serializable {
	public Node root;
	
	public Tree(){
		long currentUnixTime = System.currentTimeMillis() / 1000L;
		this.root = new Node();
		this.root.name = "";
		this.root.path = "/";
		this.root.isDir = true;
		this.root.mode = FuseFtype.TYPE_DIR | 0755;
		this.root.parent = null;
		this.root.atime = currentUnixTime;
		this.root.mtime = currentUnixTime;
		this.root.ctime = currentUnixTime;
	}
	
	public Node getNode(String pathname){
	      Node node = root;
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
	            node = node.parent;
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
	
}
