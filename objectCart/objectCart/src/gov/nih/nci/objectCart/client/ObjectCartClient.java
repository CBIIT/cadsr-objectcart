package gov.nih.nci.objectCart.client;

/**
 * Wrapper around the service interface whose methods are to be used with the remote
 * client and support common Cart operations and include support for serialization. 
 * All methods throw ObjectCartException.
 * 
 * @author Denis Avdic
 */

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.client.ApplicationServiceProvider;
import gov.nih.nci.objectCart.applicationService.ObjectCartService;
import gov.nih.nci.objectCart.domain.Cart;
import gov.nih.nci.objectCart.domain.CartObject;
import gov.nih.nci.objectCart.util.ValidatorException;

public class ObjectCartClient {
	private ObjectCartService appService = null;
	private String csType = "";
	
	
	/**
	 * Constructor: Creates a new ObjectCartClient by retrieving the ObjectCartService and
	 * using it as a private member for all transactions.  Takes classificationScheme type
	 * as the sole parameter, effectively binding this client to the particular classification
	 * scheme. 
	 * 
	 * @param classificationSchemeType
	 * @throws ObjectCartException
	 */
	public ObjectCartClient(String classificationSchemeType) throws ObjectCartException {
		csType = classificationSchemeType;
		try {
			appService = (ObjectCartService) ApplicationServiceProvider
					.getApplicationService("objectCartServiceInfo");
		} catch (Exception e) {
			throw new ObjectCartException(
					"ObjectCartClient: Error retrieving application service from provider.", e);
		} 
	}

	/**
	 * Creates and returns a cart using the userId and cartName.  If a cart exists in
	 * the specified classification scheme with that userId and cartName combination, 
	 * the method returns the existing cart.
	 * 
	 * @param userId
	 * @param cartName
	 * @return Cart
	 * @throws ObjectCartException
	 */	
	public Cart createCart(String userId, String cartName) throws ObjectCartException {
		try {
			return appService.getNewCart(userId, cartName, csType);
		} catch (ApplicationException ae) {
			throw new ObjectCartException(
					"Error while trying to get new cart from ObjectCart service", ae);
		}
	}

	/**
	 * Delete cart takes an existing cart and it sets the expiration on it to current time.
	 * Expired carts are not returned in regular searches and will be cleaned periodically using
	 * a process running in a separate thread on the server.
	 * 
	 * @param cart
	 * @return 
	 * @throws ObjectCartException
	 */
	public void deleteCart(Cart cart) throws ObjectCartException {
		try {
			appService.expireCart(cart.getId());
		} catch (ApplicationException ae) {
			throw new ObjectCartException(
					"Error while trying to delete cart in ObjectCart service", ae);
		}
	}
	
	/**
	 * Retrieves a Cart from the service using the userId, cartName.
	 * If cart matching the userId and cartName is found, it will be 
	 * returned otherwise an exception is thrown.
	 * 
	 * 
	 * @param userId
	 * @param cartName
	 * @return Cart
	 * @throws ObjectCartException
	 */
	public Cart retrieveCart(String userId, String cartName) throws ObjectCartException {
		try {
			return appService.getCart(userId, cartName, csType);
		} catch (ApplicationException ae) {
			throw new ObjectCartException(
					"Error while trying to get user cart from ObjectCart service", ae);
		}
	}
	
	/**
	 * Retrieves a list of Carts from the service associated with a particular userId. 
	 * 
	 * @param userId
	 * @return List<Cart>
	 * @throws ObjectCartException
	 */
	public List<Cart> retrieveUserCarts(String userId) throws ObjectCartException {
		try {
			return appService.getUserCarts(userId);
		} catch (ApplicationException ae) {
			throw new ObjectCartException(
					"Error while trying to get user carts from ObjectCart service", ae);
		}
	}
	
	/**
	 * Retrieves an existing Cart from the data source. 
	 * If the cart still exists it will be returned otherwise an exception is thrown.
	 * 
	 * @param cart
	 * @return Cart
	 * @throws ObjectCartException
	 */	
	public Cart refreshCart(Cart cart) throws ObjectCartException {
		try {
			return appService.getCart(cart.getId());
		} catch (ApplicationException ae) {
			throw new ObjectCartException(
					"Error while trying to refresh cart from ObjectCart service", ae);
		}
	}

	/**
	 * Takes an existing cart and a CartObject. The method sends the cartId and the CartObject
	 * to the service and returns the updated Cart.
	 * 
	 * @param cart
	 * @param cObject
	 * @return Cart
	 * @throws ObjectCartException
	 */	
	public Cart storeObject(Cart cart, CartObject cObject) throws ObjectCartException{
		try {
			return appService.addObject(cart.getId(),cObject);
		} catch (ApplicationException ae) {
			throw new ObjectCartException("Error while trying to store object to Cart using ObjectCart service", ae);
		} catch (ValidatorException ae) {
			throw new ObjectCartException("Validation Error while trying to store object to Cart using ObjectCart service", ae);
		}
	}
	
	/**
	 * Takes an existing cart and a Collection of CartObjects. 
	 * The method sends the cartId and the collection to the service and returns the 
	 * updated Cart.
	 * 
	 * @param cart
	 * @param col
	 * @return Cart
	 * @throws ObjectCartException
	 */
	public Cart storeObjectCollection(Cart cart, Collection<CartObject> col) throws ObjectCartException{
		try {
			return appService.addObjects(cart.getId(),col);
		} catch (ApplicationException ae) {
			throw new ObjectCartException("Error while trying to store object to Cart using ObjectCart service", ae);
		} catch (ValidatorException ae) {
			throw new ObjectCartException("Validation Error while trying to store object to Cart using ObjectCart service", ae);
		}
	}
	
	/**
	 * Takes an existing cart, an Object plainObject of type classToSerialize, the objectDisplayName and the nativeId of the object. 
	 * The method uses the included POJOSerializer.serializeObject method to serialize the contents of the plainObject
	 * and store the resulting CartObject with the storeObject method.
	 * 
	 * @param cart
	 * @param classToSerialize
	 * @param objectDisplayName
	 * @param nativeId
	 * @param plainObject
	 * @return Cart
	 * @throws ObjectCartException
	 */
	public Cart storePOJO(Cart cart, Class classToSerialize, String objectDisplayName, String nativeId, Object plainObject) throws ObjectCartException {
		CartObject cartObject = POJOSerializer.getInstance().serializeObject(classToSerialize, objectDisplayName, nativeId, plainObject);
		return storeObject(cart, cartObject);
	}
	
	/**
	 * Takes an existing cart, a Map<String,Object> of objects of type classToSerialize, 
	 * and a Map<String,String> of objectDisplayNames. The keys in both Maps are nativeId's of objects to be serialized.
	 * The method uses the included POJOSerializer.serializeObject method to serialize the contents of the object Map
	 * and store the resulting CartObject collection with the storeObjectCollection method.
	 * 
	 * @param cart
	 * @param cl
	 * @param objectDisplayNames
	 * @param objects
	 * @return Cart
	 * @throws ObjectCartException
	 */
	public Cart storePOJOCollection (Cart cart, Class classToSerialize, Map<String,String> objectDisplayNames, Map<String,Object> objects) throws ObjectCartException {
		Collection<CartObject> clist = new ArrayList<CartObject>();
		for (Object key: objectDisplayNames.keySet().toArray()){
			String id = (String) key;
			clist.add(POJOSerializer.getInstance().serializeObject(classToSerialize, objectDisplayNames.get(id), id, objects.get(id)));
		}
		
		return storeObjectCollection(cart, clist);
	}
	
	/**
	 * Method takes a cObject and a Class classToDeserialize.  Using the deserializeObject method
	 * of the POJOSerializer the method returns a deserialized object of type classToDeserialize based
	 * on the data field of cObject.
	 * 
	 * @param classToDeserialize
	 * @param cObject
	 * @return Object
	 * @throws ObjectCartException
	 */
	public Object getPOJO(Class classToDeserialize, CartObject cObject) throws ObjectCartException {
		return POJOSerializer.getInstance().deserializeObject(classToDeserialize, cObject);
	}
	
	/**
	 * Method takes a Collection of CartObjects col and a Class classToDeserialize.  Using the deserializeObject method
	 * of the POJOSerializer the method returns a Collection (ArrayList) of deserialized objects of type 
	 * classToDeserialize based on the data field of cObject.
	 * 
	 * @param cl
	 * @param col
	 * @return Collection<Object>
	 * @throws ObjectCartException
	 */	
	public Collection<Object> getPOJOCollection(Class cl, Collection<CartObject> col) throws ObjectCartException {
		Collection<Object> clist = new ArrayList<Object>();
		for (CartObject cob: col){
			clist.add(POJOSerializer.getInstance().deserializeObject(cl, cob));
		}
		return clist;
	}
	
	/**
	 * Takes an existing cart, an Object plainObject of type classToSerialize, the objectDisplayName and the nativeId of the object. 
	 * The method also takes a custom made serializer implementing the Serializer interface.
	 * The method then uses the  Serializer.serializeObject method to serialize the contents of the plainObject
	 * and store the resulting CartObject with the storeObject method.
	 * 
	 * @param cart
	 * @param cl
	 * @param objectDisplayName
	 * @param nativeId
	 * @param pOb
	 * @param customSerializer
	 * @return Cart
	 * @throws ObjectCartException
	 */
	public Cart storeCustomObject(Cart cart, Class cl, String objectDisplayName, String nativeId, Object pOb, Serializer customSerializer) throws ObjectCartException {
		CartObject cartObject = customSerializer.serializeObject(cl, objectDisplayName, nativeId, pOb);
		return storeObject(cart, cartObject);
	}
	
	/**
	 * The method takes a Cart and a CartObject and passes them to the service for removal 
	 * of CartObject from the CartObjectCollection associated with that Cart.
	 * The service returns the updated cart.
	 * 
	 * @param cart
	 * @param ob
	 * @return Cart
	 * @throws ObjectCartException
	 */
	public Cart removeObject(Cart cart, CartObject ob) throws ObjectCartException {
		try {
			return appService.removeObject(cart.getId(),ob.getId());
		} catch (ApplicationException ae) {
			throw new ObjectCartException("Error while trying to remove an object from Cart using ObjectCart service", ae);
		}
	}
	
	/**
	 * The method takes a Cart and a Collection of CartObject and passes them to the service for removal 
	 * of Collection of CartObject from the CartObjectCollection associated with that Cart.
	 * The Collection to be deleted can be a subset of the CartObjectCollection.
	 * The service returns the updated cart.
	 * 
	 * @param cart
	 * @param col
	 * @return Cart
	 * @throws ObjectCartException
	 */	
	public Cart removeObjectCollection(Cart cart, Collection<CartObject> col) throws ObjectCartException {
		try {
			Integer[] arr = new Integer[col.size()];
			int counter = 0;
			for (CartObject c: col)
				arr[counter++] = c.getId();
			
			return appService.removeObjects(cart.getId(),arr);
		} catch (ApplicationException ae) {
			throw new ObjectCartException("Error while trying to remove collection of objects from Cart using ObjectCart service", ae);
		}
	}
	
	/**
	 * Takes an existing cart and a type. The method uses the service to retrieve CartObjects associated with the 
	 * provided cart and with the matching type and returns them as collection to the requester.  
	 * 
	 * @param cart
	 * @param type
	 * @return Collection<CartObject>
	 * @throws ObjectCartException
	 */	
	public Collection<CartObject> getObjectsByType(Cart cart, String type) throws ObjectCartException {
		try {
			return appService.retrieveObjectsByType(cart.getId(), type);
		} catch (ApplicationException ae) {
			throw new ObjectCartException("Error while trying to remove an object to Cart using ObjectCart service", ae);
		}
		
	}
}