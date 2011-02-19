package unipi.p2p.ref.service.dht;


/**
 * This service provides the basic methods for storing and retrieving  
 * data from the Overlay. All applications or services wishing to store and retrieve data
 * from the overlay must use this interface.
 * 
 * @author agapios
 *
 */
public interface DHT {
	
	/**
	 * Attempts to retrieve a DataItem that was stored in the structured overlay network
	 * using the put <DataKey> <DataItem> method. 
	 * 
	 * @param key The key of the DataItem
	 * @return The DataItem that corresponds to key
	 * @throws KeyNotFoundException In case that no live node contains the specified Key (and the 
	 * @throws CommunicationException In case a communication error occurs between the LocalNode and the set of its neighbors
	 */
	public DataItem get(DataKey key) throws KeyNotFoundException, CommunicationException;
	
	
	
	/**
	 * Attempts to store the <key, data> pair into a node in the overlay. 
	 * 
	 * 
	 * @param key The key of the data.
	 * @param data A DataItem containing the actual data to be stored
	 * @throws StorageUnavailableException In case that no live node has available storage space.
	 * @throws CommunicationException In case a communication error occurs between the LocalNode and the set of its neighbors
	 */
	public void put(DataKey key, DataItem data) throws StorageUnavailableException, CommunicationException;

}
