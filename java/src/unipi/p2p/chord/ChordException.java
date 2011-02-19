package unipi.p2p.chord;

public class ChordException extends Exception {

	public ChordException(String ex) {
		super(ex);
	}
	
	public ChordException(Throwable t) {
		super(t);
	}
	
	public ChordException(String message, Throwable t) {
		super(message, t);
		
	}
}
