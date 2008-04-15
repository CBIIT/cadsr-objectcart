package gov.nih.nci.objectCart.client;

import java.util.HashMap;
import java.util.Map;

public class CartManager {

	private Map<String,ObjectCartClient> clients;
	
	// Private constructor suppresses generation of a (public) default constructor
	private CartManager() {}
	
	/**
	 * SingletonHolder is loaded on the first execution of CartManager.getInstance() 
	 * or the first access to SingletonHolder.instance , not before.
	 */
	private static class SingletonHolder { 
		private final static CartManager instance = new CartManager();
	}

	public static CartManager getInstance() {
		return SingletonHolder.instance;
	}

	public void initClients(String[] classificationSchemes) throws ObjectCartException {
		Map<String, ObjectCartClient> temp = new HashMap<String, ObjectCartClient>();
		if (clients == null) {
			for (String cScheme: classificationSchemes){
				temp.put(cScheme, new ObjectCartClient(cScheme));
			}
			clients = temp;
		} else throw new ObjectCartException("Initializing manager more than once");
	}

	public ObjectCartClient getClient(String classificationScheme) {
		return clients.get(classificationScheme);
	}
}