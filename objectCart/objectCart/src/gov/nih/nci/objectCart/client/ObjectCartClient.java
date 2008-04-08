package gov.nih.nci.objectCart.client;


import java.util.Collection;
import java.util.List;

import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.client.ApplicationServiceProvider;
import gov.nih.nci.objectCart.applicationService.ObjectCartService;
import gov.nih.nci.objectCart.domain.Cart;
import gov.nih.nci.objectCart.domain.CartObject;
import gov.nih.nci.objectCart.domain.ClassificationScheme;
import gov.nih.nci.objectCart.util.ValidatorException;

public class ObjectCartClient {
	private ObjectCartService appService = null;
	private String csType = "";
	
	public ObjectCartClient(String classificationSchemeType) throws ObjectCartException {
		csType = classificationSchemeType;
		try {
			appService = (ObjectCartService) ApplicationServiceProvider
					.getApplicationService();
		} catch (Exception e) {
			throw new ObjectCartException(
					"ObjectCartClient: Error retrieving application service from provider.", e);
		} 
	}

	public Cart createCart(String userId, String cartName) throws ObjectCartException {
		try {
			return appService.getNewCart(userId, cartName, csType);
		} catch (ApplicationException ae) {
			throw new ObjectCartException(
					"Error while trying to get new cart from ObjectCart service", ae);
		}
	}

	public void deleteCart(Cart cart) throws ObjectCartException {
		try {
			appService.expireCart(cart.getId());
		} catch (ApplicationException ae) {
			throw new ObjectCartException(
					"Error while trying to delete cart in ObjectCart service", ae);
		}
	}
	
	public Cart retrieveCart(String userId, String cartName) throws ObjectCartException {
		try {
			return appService.getCart(userId, cartName, csType);
		} catch (ApplicationException ae) {
			throw new ObjectCartException(
					"Error while trying to get user cart from ObjectCart service", ae);
		}
	}
	
	public List<Cart> retrieveUserCarts(String userId) throws ObjectCartException {
		try {
			return appService.getUserCarts(userId);
		} catch (ApplicationException ae) {
			throw new ObjectCartException(
					"Error while trying to get user carts from ObjectCart service", ae);
		}
	}
	
	public Cart refreshCart(Cart cart) throws ObjectCartException {
		try {
			return appService.getCart(cart.getId());
		} catch (ApplicationException ae) {
			throw new ObjectCartException(
					"Error while trying to refresh cart from ObjectCart service", ae);
		}
	}

	public Cart storeObject(Cart cart, CartObject ob) throws ObjectCartException{
		try {
			return appService.addObject(cart.getId(),ob);
		} catch (ApplicationException ae) {
			throw new ObjectCartException("Error while trying to store object to Cart using ObjectCart service", ae);
		} catch (ValidatorException ae) {
			throw new ObjectCartException("Validation Error while trying to store object to Cart using ObjectCart service", ae);
		}
	}
	
	public Cart storeObjectCollection(Cart cart, Collection<CartObject> col) throws ObjectCartException{
		try {
			return appService.addObjects(cart.getId(),col);
		} catch (ApplicationException ae) {
			throw new ObjectCartException("Error while trying to store object to Cart using ObjectCart service", ae);
		} catch (ValidatorException ae) {
			throw new ObjectCartException("Validation Error while trying to store object to Cart using ObjectCart service", ae);
		}
	}
	
	public Cart storePOJO(Cart cart, Class cl, String objectDisplayName, String nativeId, Object pOb) throws ObjectCartException {
		CartObject cartObject = POJOSerializer.getInstance().serializeObject(cl, objectDisplayName, nativeId, pOb);
		return storeObject(cart, cartObject);
	}
	
	public Cart storeCustomObject(Cart cart, Class cl, String objectDisplayName, String nativeId, Object pOb, Serializer customSerializer) throws ObjectCartException {
		CartObject cartObject = customSerializer.serializeObject(cl, objectDisplayName, nativeId, pOb);
		return storeObject(cart, cartObject);
	}
	
	public Cart removeObject(Cart cart, CartObject ob) throws ObjectCartException {
		try {
			return appService.removeObject(cart.getId(),ob.getId());
		} catch (ApplicationException ae) {
			throw new ObjectCartException("Error while trying to remove an object from Cart using ObjectCart service", ae);
		}
	}
	
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
	
	public Collection<CartObject> getObjectsByType(Cart cart, String type) throws ObjectCartException {
		try {
			return appService.retrieveObjectsByType(cart.getId(), type);
		} catch (ApplicationException ae) {
			throw new ObjectCartException("Error while trying to remove an object to Cart using ObjectCart service", ae);
		}
		
	}
}