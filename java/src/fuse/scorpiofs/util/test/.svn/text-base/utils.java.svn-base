package fuse.scorpiofs.util.test;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import fuse.scorpiofs.util.DataObject;
import fuse.scorpiofs.util.FsNode;

public class utils {
	public static Node CopyNode(FsNode source, Node parent){
		Node target = new Node();
		target.name = source.getName();
		target.path = source.getPath();
		target.size = source.getSize();
		target.mode = source.getMode();
		target.isDir = source.isDir();
		target.parent = parent;
		
		//chunklist
		ChunkList list = new ChunkList();
		list.Chunks = new LinkedList();
		Iterator it = source.datalist.theList.iterator();
		while (it.hasNext()){
			Chunk tmpChunk = new Chunk();
			DataObject tmpObj = (DataObject)it.next();
			tmpChunk.dataID = tmpObj.dataID;
			tmpChunk.size = tmpObj.getSize();
			tmpChunk.firstByte = tmpObj.getFirstByte();
			tmpChunk.bytesFilled = tmpObj.getBytesFilled();
			list.Chunks.add(tmpChunk);
			target.chunk_list = list;
			
		}
		Map children = source.children;
		if(children != null){
		it = children.entrySet().iterator();
		Map targetChildren = null;
		while (it.hasNext()){
			if (targetChildren == null){
				targetChildren = new HashMap();
			}
			Map.Entry pairs = (Map.Entry)it.next();
	        FsNode child = (FsNode)pairs.getValue();
	        child.getName();
	        targetChildren.put(child.getName(), CopyNode(child,target));
		}
		target.children = targetChildren;
		}
		else{
			target.children = null;//children
		}
		
		
		return target;
	}
	public static void printTree(Node root){
		System.out.println(root.path);
		if (root.chunk_list!=null)	
		
		{
			Iterator it = root.chunk_list.Chunks.iterator();
			System.out.println("parts:");
			Chunk tmpChunk;
			while (it.hasNext()){
				tmpChunk = (Chunk)it.next();
				System.out.println(tmpChunk.bytesFilled);
			}
			System.out.println("end of parts");
		}
		if (root.children!=null){
			Iterator it = root.children.entrySet().iterator();
			while (it.hasNext()){
				Map.Entry pairs = (Map.Entry)it.next();
		        Node child = (Node)pairs.getValue();
				printTree(child);
			}
		}		
	}
}
