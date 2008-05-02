package gov.nih.nci.objectCart.applicationService;

import java.util.Collection;
import java.util.List;

import gov.nih.nci.objectCart.domain.Cart;
import gov.nih.nci.objectCart.domain.CartObject;
import gov.nih.nci.objectCart.util.ValidatorException;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.ApplicationService;

/**
 * Extension to the service layer interface whose methods will be exposed to all the different tiers (webservvice, remote and web)
 * 
 * @author Denis Avdic
 */

public interface ObjectCartService extends ApplicationService {
	
	/**
	 * Creates a new Cart at the data source using the userID, the cart name and the classification scheme type. The method performs a search for an existing
	 * cart with the same parameters.  If one is found, it will be returned otherwise it will create and return a new cart.
	 * 
	 * @param userId
	 * @param cartName
	 * @param classificationSchemeType
	 * @return 
	 * @throws ApplicationException
	 */
	public Cart getNewCart(String userId, String cartName, String classificationSchemeType) throws ApplicationException;
	
	/**
	 * Retrieves a list of Carts from the data source associated with a particular ClassificationScheme. The method performs a query by
	 * example using a ClassificationScheme as an example.  It converts the result set into a List<Cart> and returns it to caller.  
	 * 
	 * @param classificationSchemeType
	 * @return 
	 * @throws ApplicationException
	 */
	public List<Cart> getClassificationSchemeCarts(String classificationSchemeType) throws ApplicationException;
		
	/**
	 * Retrieves a  Cart from the data source using the userId, cartName and the classification scheme type. The method performs a search for an existing
	 * cart with that particular identifier.  If one is found, it will be returned otherwise an exception is thrown.
	 * 
	 * @param userId
	 * @param cartName
	 * @param classificationSchemeType
	 * @return 
	 * @throws ApplicationException
	 */
	public Cart getCart(String userId, String cartName, String classificationSchemeType) throws ApplicationException;
		
	/**
	 * Retrieves a  Cart from the data source using the unique cartId. The method performs a search for an existing
	 * cart with that particular identifier.  If one is found, it will be returned otherwise an exception is thrown.
	 * 
	 * @param cartId
	 * @return 
	 * @throws ApplicationException
	 */
	public Cart getCart(Integer cartId) throws ApplicationException;
	
	/**
	 * Retrieves a list of Carts from the data source associated with a particular userId. The method performs a query by
	 * example using a Cart as an example.  It converts the result set into a List<Cart> and returns it to caller.  
	 * 
	 * @param userId
	 * @return 
	 * @throws ApplicationException
	 */
	public List<Cart> getUserCarts(String userId) throws ApplicationException;
	
	/**
	 * Retrieves a list of Carts from the data source with a particular cartName. The method performs a query by
	 * example using a Cart as an example.  It converts the result set into a List<Cart> and returns it to caller.  
	 * 
	 * @param userId
	 * @return 
	 * @throws ApplicationException
	 */
	public List<Cart> getCartsByName(String cartName) throws ApplicationException;
	
	/**
	 * Takes a cartId of an existing cart and a collection of CartObjects. The method retrieves the cart using the 
	 * provided ID and adds the contents of the collection to the cartObjectCollection of the cart.  It will merge
	 * the two collections if there is one already present in the cart.  Finally, it returns the updated cart to the requester.
	 * 
	 * @param cartId
	 * @param cartobjects
	 * @return 
	 * @throws ApplicationException
	 */
	public Cart addObjects(Integer cartId, Collection<CartObject> cartObjects) throws ApplicationException, ValidatorException;
	
	/**
	 * Takes a cartId of an existing cart and a CartObject. The method retrieves the cart using the 
	 * provided ID and adds the cartObject to the cartObjectCollection of the cart.
	 * Finally, it returns the updated cart to the requester.
	 * 
	 * @param cartId
	 * @param cartobject
	 * @return 
	 * @throws ApplicationException
	 */
	public Cart addObject(Integer cartId, CartObject cartObject) throws ApplicationException, ValidatorException;
	
	/**
	 * Convenience method to support guest carts for a particular application.  Mainly to support users that
	 * are in the process of getting an ID with the SSO or whichever authentication system the over-arching
	 * application is using.  Also supports temporary carts through the "guest" account.
	 * It takes a new UserId, the cart name and a old userID that the existing guest cart will finally be associated with.
	 * Method returns the updated cart to the requester.
	 * 
	 * @param newUserId
	 * @param userId
	 * @param cartName
	 * @return 
	 * @throws ApplicationException
	 */
	public Cart associateCart(String newUserId, String oldUserId, String cartName) throws ApplicationException;
	
	
	/**
	 * Expire cart takes an existing cartId and it sets the expiration on it to current time.
	 * Expired carts are not returned in regular searches and will be cleaned periodically using
	 * a process running in a separate thread.
	 * 
	 * @param cartId
	 * @return 
	 * @throws ApplicationException
	 */
	public void expireCart(Integer cartId) throws ApplicationException;
	
	/**
	 * Takes a cartId of an existing cart and an array of CartObject id's. The method retrieves the cart using the 
	 * provided ID and removes the contents of the cartObjectCollection based on the objectIds.  
	 * Finally, it returns the updated cart to the requester.
	 * 
	 * @param cartId
	 * @param objectIds
	 * @return 
	 * @throws ApplicationException
	 */
	public Cart removeObjects(Integer cartId, Integer[] objectIds) throws ApplicationException;
	
	/**
	 * Takes a cartId of an existing cart and a CartObject id. The method retrieves the cart using the 
	 * provided ID and removes a member of the cartObjectCollection based on the objectId.  
	 * Finally, it returns the updated cart to the requester.
	 * 
	 * @param cartId
	 * @param objectIds
	 * @return 
	 * @throws ApplicationException
	 */
	public Cart removeObject(Integer cartId, Integer objectId) throws ApplicationException;
	
	/**
	 * Takes a cartId of an existing cart. The method retrieves CartObjects associated with the 
	 * provided ID and returns them as collection to the requester.  Uses query by example.
	 * 
	 * @param cartId
	 * @return 
	 * @throws ApplicationException
	 */
	public Collection<CartObject> retrieveObjects(Integer cartId) throws ApplicationException;
	
	/**
	 * Takes a cartId of an existing cart and a type. The method retrieves CartObjects associated with the 
	 * provided ID and with the matching type and returns them as collection to the requester.  
	 * Uses query by example.
	 * 
	 * @param cartId
	 * @param type
	 * @return 
	 * @throws ApplicationException
	 */
	public Collection<CartObject> retrieveObjectsByType(Integer cartId, String type) throws ApplicationException;
	
	/**
	 * Takes a cartId of an existing cart and an objectId. The method retrieves a specific cartObject associated with the 
	 * provided cart ID and return it to the requester.  
	 * Uses query by example.
	 * 
	 * @param cartId
	 * @param objectId
	 * @return 
	 * @throws ApplicationException
	 */
	public CartObject retrieveObject(Integer cartId, Integer objectId)throws ApplicationException;

}
