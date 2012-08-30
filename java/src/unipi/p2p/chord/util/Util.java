package unipi.p2p.chord.util;

import java.math.BigInteger;
import java.net.NetworkInterface;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.security.*;
import java.util.BitSet;
import java.util.Enumeration;
import org.apache.log4j.Logger;

import unipi.p2p.chord.Finger;
import unipi.p2p.chord.RemoteChordNode;


public abstract class Util {

	private static final Logger log = Logger.getRootLogger();
	private Util() {
		super();
	}
	
	
	

	private static String hexEncode(byte[] aInput) {
		StringBuffer result = new StringBuffer();
		char[] digits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'a', 'b', 'c', 'd', 'e', 'f' };
		for (int idx = 0; idx < aInput.length; ++idx) {
			byte b = aInput[idx];
			result.append(digits[(b & 0xf0) >> 4]);
			result.append(digits[b & 0x0f]);
		}
		return result.toString();
	}

	
	public static byte[] sha(final String msg) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			byte[] xxx = md.digest(msg.getBytes());
			return xxx;
		} catch(Exception e) {
			return null;
		}
		
	}
	public static byte[] sha(final byte [] data) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			byte[] xxx = md.digest(data);
			return xxx;
		} catch(Exception e) {
			return null;
		}
		
	}
	

	public static String shaHex(final String msg) {
		return hexEncode(sha(msg));
	}
	
	public static String shaHex(final byte [] data) {
		return hexEncode(sha(data));
	}


	 public static BitSet fromByteArray(byte[] bytes) {
	        BitSet bits = new BitSet();
	        for (int i=0; i<bytes.length*8; i++) {
	            if ((bytes[bytes.length-i/8-1]&(1<<(i%8))) > 0) {
	                bits.set(i);
	            }
	        }
	        return bits;
	    }
	
	 
	/*public static BigInteger integerValue(final String msg) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			byte[] xxx = md.digest(msg.getBytes());
			Random rand = new Random(System.currentTimeMillis());
			BitSet bigBS = fromByteArray(xxx);
			for(int i = 0; i < bigBS.length();i++) {
				
				log.info("agapios + " + bigBS.get(i));
			}
			BitSet bs = new BitSet(Constants.IDENTIFIER_LENGTH);
			bs.clear();
			for(int i = 0; i < bs.length(); i++) {
				int temp = rand.nextInt(bigBS.length());
				if (bigBS.get(temp)) {
					bs.set(i);
				}
			}
			BigInteger bi = BigInteger.ZERO;
			while(bi.compareTo(BigInteger.ZERO) == 0) {
			for (int i = bs.length() - 1; i >=0 ;i--) {
				if (bs.get(i)) {
					bi.add(Constants.TWO.pow(i));
				}
			}
			}
			log.info("aaaa " + bi.toString());
			return bi;
		} catch(Exception e) {
			return null;
		}
	}*/

	 

		public static BigInteger integerValue2(byte[] data) {
			try {
				if (data == null) return BigInteger.ONE;
				
				MessageDigest md = MessageDigest.getInstance("SHA-1");
				byte[] xxx = md.digest(data);
				
				BigInteger bi = new BigInteger(1, xxx);
				//log.info("aaaa " + bi.toString());
				return bi;
			} catch(Exception e) {
				e.printStackTrace();
				log.fatal("teeeeeeeeeeeeeeeeeeee " + e.toString());
				return BigInteger.ONE;
			}
			
		}

	public static BigInteger integerValue(final String msg) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			byte[] xxx = md.digest(msg.getBytes());
			
	/*		byte[] yyy = new byte[Constants.IDENTIFIER_LENGTH / 8];
			
			for (int i = 0; i < Constants.IDENTIFIER_LENGTH / 8;i++) {
				yyy[i] = xxx[xxx.length - 1];
			}
		*/	
			//BigInteger bi = new BigInteger(1, yyy);
			BigInteger bi = new BigInteger(1, xxx);
			//log.info("aaaa " + bi.toString());
			return bi;
		} catch(Exception e) {
			log.fatal("teeeeeeeeeeeeeeeeeeee " + e.toString());
			return null;
		}
		
	}

	/**
	 * Returns true if b1 < candidate < b2.
	 * 
	 * @param candidate
	 * @param b1
	 * @param b2
	 * @return
	 */
	public static boolean between(final BigInteger candidate,
			final BigInteger b1, final BigInteger b2, final boolean inclusive) {
		if (inclusive && (candidate.equals(b1) || candidate.equals(b2)))
			return true;

		return candidate.compareTo(b2) == -1
				&& candidate.compareTo(b1) == 1;
	}

	public static boolean inBetween(BigInteger candidate, BigInteger b1,
			BigInteger b2) {
		if (b1.compareTo(b2) < 0) {
			return (candidate.compareTo(b1) >= 0 && candidate.compareTo(b2) <= 0);
		} else {
			return (candidate.compareTo(b1) >= 0 || candidate.compareTo(b2) <= 0);

		}

	}

	public static boolean exBetween(BigInteger candidate, BigInteger b1,
			BigInteger b2) {
		if (b1.compareTo(b2) < 0) {
			return (candidate.compareTo(b1) > 0 && candidate.compareTo(b2) < 0);
		} else {
			return (candidate.compareTo(b1) > 0 || candidate.compareTo(b2) < 0);

		}

	}

	public static boolean inBetweenExRight(BigInteger candidate, BigInteger b1,
			BigInteger b2) {
		if (b1.compareTo(b2) < 0) {
			return (candidate.compareTo(b1) >= 0 && candidate.compareTo(b2) < 0);
		} else {
			return (candidate.compareTo(b1) >= 0 || candidate.compareTo(b2) < 0);

		}

	}

	public static boolean inBetweenExLeft(BigInteger candidate, BigInteger b1,
			BigInteger b2) {
		if (b1.compareTo(b2) < 0) {
			return (candidate.compareTo(b1) > 0 && candidate.compareTo(b2) <= 0);
		} else {
			return (candidate.compareTo(b1) > 0 || candidate.compareTo(b2) <= 0);

		}

	}

	/**
	 * 2 Returns the base 10 log of a BigInteger value.
	 * 
	 * @param bi
	 * @return
	 */
	public static double log2(final BigInteger bi) {
		return bi.bitLength();
	}
	
	public static RemoteChordNode getRemoteChordNode(Finger f) throws RemoteException {
		try {
			RemoteChordNode stub = (RemoteChordNode) Naming.lookup("rmi://"
					+ f.toString() + "/unipi.p2p.chord.ChordNode");
			stub.alive(); //throws exception if the RemoteChordNode is not alive
			return stub;
		} catch (Exception e) {
			throw new RemoteException(e.toString());
		}
	}
	public static String getMac(){

		Enumeration<NetworkInterface> interfaces;
		try {
			interfaces = java.net.NetworkInterface.getNetworkInterfaces();
			for (int i = 0; interfaces.hasMoreElements(); i++) {
				byte[] addr = interfaces.nextElement().getHardwareAddress();
				String str_mac = "";
				if (addr != null) {
					for (int j = 0; j < addr.length; j++) {
						str_mac = str_mac
								+ String.format("%02x%s", addr[j],
										(j < addr.length - 1) ? ":" : "");
					}
					return str_mac;

				}
			}
			// System.out.println();
		} catch (Exception e) {
			//do nothing...
		}
		return "00:00:00:00:00:00";

	}

}
