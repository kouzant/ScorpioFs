package fuse.scorpiofs.util;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class FsNode implements Serializable {
	private String real_file;
	private String name;
	private String path;
	private FsNode parent;
	// private Object value;
	public Map children;
	private int size;
	int mode;
	private boolean isDir;
	public DataList datalist = new DataList();

	public void setName(String filename) {
		this.name = filename;
	}

	public void setPath(String pathname) {
		this.path = pathname;
	}

	public String getPath() {
		return this.path;
	}

	public String getName() {
		return this.name;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getSize() {
		return this.size;
	}

	public void setParent(FsNode parent) {
		this.parent = parent;
	}

	public FsNode getParent() {
		return this.parent;
	}

	public void setDir(boolean isDirectory) {
		this.isDir = isDirectory;
	}

	public boolean isDir() {
		return this.isDir;
	}

	public FsNode getChild(String name) {
		return (children == null) ? null : (FsNode) children.get(name);
	}

	public void addChild(FsNode node) {
		if (children == null)
			children = new HashMap();

		children.put(node.getName(), node);
	}

	public void rmChild(String name) {
		children.remove(name);

	}

	public Collection getChildren() {
		return (children == null) ? Collections.EMPTY_LIST : children.values();
	}

	public void setRealFile(String path) {
		this.real_file = path;
	}

	public String getRealFile() {
		return this.real_file;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

	public int getMode() {
		return mode;
	}

	public void printStatus() {
		//this.datalist.printStatus();
		if (children == null) return;
		Iterator it = children.entrySet().iterator();
		
		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry) it.next();
			FsNode child = (FsNode) pairs.getValue();
			if (child != null){
				child.datalist.printStatus();
				child.printStatus();
		}
		}
	}

}
