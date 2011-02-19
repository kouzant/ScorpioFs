package unipi.p2p.chord;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class ObjectDiskIO {
	 public void saveObject(Serializable object, String filename) throws IOException {
	       ObjectOutputStream objstream = new ObjectOutputStream(new FileOutputStream(filename));
	       objstream.writeObject(object);
	       objstream.close();
	    }
	 
	    public Object loadObject(String filename) throws Exception {
	       ObjectInputStream objstream = new ObjectInputStream(new FileInputStream(filename));
	       Object object = objstream.readObject();
	       objstream.close();
	       return object;
	    }


}
