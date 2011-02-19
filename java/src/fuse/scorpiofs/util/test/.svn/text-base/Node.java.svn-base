package fuse.scorpiofs.util.test;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import fuse.scorpiofs.util.FsNode;

public class Node implements Serializable {
	public String name;
	public String path;
	public Node parent;
	public Map children;
	public boolean isDir;
	public int size;
	public int mode;
	public long atime;
	public long mtime;
	public long ctime;
	public ChunkList chunk_list;

	public Node() {
		//this.chunk_list = new ChunkList();
		long currentUnixTime = System.currentTimeMillis() / 1000L;
		this.atime = currentUnixTime;
		this.mtime = currentUnixTime;
		this.ctime = currentUnixTime;
	}

	public void addChild(Node node) {
		if (children == null)
			children = new HashMap();

		children.put(node.name, node);
	}

	public void rmChild(String name) {
		children.remove(name);
	}

	public Node getChild(String name) {
		return (children == null) ? null : (Node) children.get(name);
	}

}
