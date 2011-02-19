package fuse.scorpiofs.util.test;

import java.io.Serializable;
import java.math.BigInteger;

public class Chunk implements Serializable{
	public BigInteger dataID;
	public int size = 1024*1024;
	public long firstByte;
	public int bytesFilled;

}
