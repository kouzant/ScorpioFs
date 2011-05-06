package unipi.p2p.chord;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.File;

public class ObjectDiskIO {
	 public void saveObject(Serializable inObject, File filename) throws IOException {
	       ObjectOutputStream objOutStream = new ObjectOutputStream(new FileOutputStream(filename));
	       objOutStream.writeObject(inObject);
	       objOutStream.close();
	    }
	 
	    public Object loadObject(File filename) throws Exception {
	       ObjectInputStream objInStream = new ObjectInputStream(new FileInputStream(filename));
	       Object outObject = objInStream.readObject();
	       objInStream.close();
	       return outObject;
	    }


}
