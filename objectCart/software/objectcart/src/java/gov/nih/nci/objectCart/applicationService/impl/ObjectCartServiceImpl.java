package gov.nih.nci.objectCart.applicationService.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Date;

import org.apache.log4j.Logger;

import gov.nih.nci.objectCart.applicationService.ObjectCartService;
import gov.nih.nci.objectCart.dao.CartDAO;
import gov.nih.nci.objectCart.domain.Cart;
import gov.nih.nci.objectCart.domain.CartObject;

import gov.nih.nci.objectCart.util.PropertiesLoader;
import gov.nih.nci.objectCart.util.ValidatorException;
import gov.nih.nci.objectCart.util.xml.Validator;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.impl.ApplicationServiceImpl;
import gov.nih.nci.system.dao.DAOException;
import gov.nih.nci.system.util.ClassCache;

public class ObjectCartServiceImpl extends ApplicationServiceImpl implements ObjectCartService{

	/**
	 * Default constructor. Cache is required and is expected to have a collection of DAO
	 * System properties is also a required parameter used to determine system specific parameters
	 * 
	 */
	
	private static Logger log = Logger.getLogger(ObjectCartServiceImpl.class.getName());
	
	public ObjectCartServiceImpl(ClassCache classCache,
			Properties systemProperties) {
		super(classCache, systemProperties);		
	}
	
	public Cart getNewCart(String userId, String cartName) throws ApplicationException {		
		Cart newCart = new Cart();	
		
		newCart.setUserId(userId);
		newCart.setName(cartName);	
		
		List<Cart> carts = getNewCarts(newCart);

		if (carts.size() > 1)
			throw new ApplicationException("More than one cart with that name/userId pairing exists (Expiration failure?)");
		else if(carts.isEmpty())
			throw new ApplicationException("No carts with that name/userId pairing exists: Create Failed (Should never get here)");
		else 
			newCart = carts.get(0);
		
		return newCart;

	}

	/*public List<Cart> getClassificationSchemeCarts(String classificationSchemeType) throws ApplicationException {
		Cart exampleCart = new Cart();	
		
		exampleCart.setType(classificationSchemeType);

		List<Cart> carts = cartSearch(exampleCart);
		return carts;
	}*/
	
	public Cart getCart(Integer cartId) throws ApplicationException {
		Cart newCart = new Cart();
		newCart.setId(cartId);
		
		List<Cart> carts = cartSearch(newCart);
		
		if (carts.size() > 1)
			throw new ApplicationException("More than one cart with that ID exists (SHOULD NOT HAPPEN)");
		else if(carts.isEmpty())
			throw new ApplicationException("No cart with that ID exist!");
		else 
			newCart = carts.get(0);	
		
		return newCart;
	}
	
	public List<Cart> getUserCarts(String userId) throws ApplicationException {
		Cart newCart = new Cart();		
		newCart.setUserId(userId);
		
		return getCarts(newCart);	
	}

	public List<Cart> getCartsByName(String cartName) throws ApplicationException {
		Cart newCart = new Cart();	
		
		newCart.setName(cartName);
		
		return getCarts(newCart);	
	}

	public Cart getCart(String userId, String cartName) throws ApplicationException {
		Cart newCart = new Cart();	

		newCart.setUserId(userId);
		newCart.setName(cartName);
		
		List<Cart> carts = getCarts(newCart);	
		
		if (carts.size() > 1)
			throw new ApplicationException("More than one cart with that userId/name pairing exists (SHOULD NOT HAPPEN)");
		else if(carts.isEmpty())
			throw new ApplicationException("No cart with that ID exist!");
		
		return carts.get(0);
	}
	
	private List<Cart> getCarts(Cart example) throws ApplicationException {
		List<Cart> carts = cartSearch(example);
					
		return carts;
	}
	
	public Cart addObjects(Integer cartId, Collection<CartObject> cartObjects) throws ApplicationException, ValidatorException {
		
		Cart cart = getCart(cartId);
		
		for (CartObject co: cartObjects){
			Validator.validateObject(co);
			if (co.getDateAdded() == null)
				co.setDateAdded(now(0));
		}
		
		cart.getCartObjectCollection().addAll(cartObjects);	
		return storeCart(cart);	
	}
	
	public Cart addObject(Integer cartId, CartObject cartObject) throws ApplicationException, ValidatorException {
		
		Cart cart = getCart(cartId);
		
		if (cartObject.getDateAdded() == null)
			cartObject.setDateAdded(now(0));
		
		Validator.validateObject(cartObject);

		cart.getCartObjectCollection().add(cartObject);
		
		return storeCart(cart);	
	}
	
	public Cart associateCart(String newUserId, String oldUserId, String cartName) throws ApplicationException {
		Cart oldCart = new Cart();
		oldCart.setName(cartName);
		oldCart.setUserId(oldUserId);
		
		Cart newCart = new Cart();
		newCart.setName(cartName);
		newCart.setUserId(newUserId);
		
		return associateCart(newCart, oldCart);
		
	}
	
	public Cart setExpiration(Integer cartId) throws ApplicationException {
		Cart cart = getCart(cartId);
		int expiration;	
		expiration = Integer.valueOf(PropertiesLoader.getProperty("cart.time.expiration.minutes"));
		expiration = expiration*60*1000;
		cart.setExpirationDate(now(expiration));
		return storeCart(cart);
	}
	
	public Cart setExpiration(Integer cartId, Date expirationDate) throws ApplicationException {
		Cart cart = getCart(cartId);
		cart.setExpirationDate(expirationDate);
		return storeCart(cart);
	}
	
	public void expireCart(Integer cartId) throws ApplicationException {
		Cart cart = getCart(cartId);
		expireCart(cart);
	}
	
	private Cart associateCart(Cart newCart, Cart oldCart) throws ApplicationException {
		List<Cart> newCarts = cartSearch(newCart);
		List<Cart> oldCarts = cartSearch(oldCart);
		
		if (newCarts.size() > 1 || oldCarts.size() > 1)
			throw new ApplicationException("More than one cart with that name/guestId pairing exists");
		else if (oldCarts.isEmpty())
			throw new ApplicationException("Trying to associate a non-existing cart");
		else if (newCarts.isEmpty()) {
			oldCart = oldCarts.get(0);
			oldCart.setUserId(newCart.getUserId());
			return storeCart(oldCart);
		} else {
			oldCart = oldCarts.get(0);
			newCart = newCarts.get(0);
			
			//Prevents association to itself.
			if (oldCart.getId().equals(newCart.getId()))
				return oldCart;
			
			newCart.getCartObjectCollection().addAll(
					merge(newCart.getCartObjectCollection(),
						  oldCart.getCartObjectCollection()));
			
			expireCart(oldCart);
			return storeCart(newCart);
		}

		
		
	}

	private List<Cart> getNewCarts(Cart newCart) throws ApplicationException {
		
		List<Cart> cartList = getCarts(newCart);
		if (cartList == null || cartList.isEmpty())
			cartList.add(createCart(newCart));
		
		return cartList;
	}

	private Cart createCart(Cart newCart) throws ApplicationException {
		
		Date now = now(0);		
		newCart.setExpirationDate(null);
		newCart.setCreationDate(now);
		newCart.setLastWriteDate(now);
		newCart.setLastReadDate(now);
		return storeCart(newCart);
	}
	
	private Cart storeCart(Cart cart) throws ApplicationException, DAOException {
		
		cart.setLastWriteDate(now(0));
		CartDAO dao = (CartDAO) getClassCache().getDAOForClass(cart.getClass().getCanonicalName());
		
		if(dao == null)
			throw new ApplicationException("Could not obtain DAO for: "+cart.getClass().getCanonicalName());
		
		try
		{
			return dao.storeCart(cart);
			 
		} catch(DAOException daoException) {
			log.error("Error while getting and storing Cart in DAO",daoException);
			throw daoException;
		} catch (ApplicationException e1) {
			log.fatal("Unable to locate Service Locator :",e1);
			throw new ApplicationException("Unable to locate Service Locator :",e1);
		} catch(Exception exception) {
			log.error("Exception while getting datasource information "+ exception.getMessage());
			throw new ApplicationException("Exception in Base Delegate while getting datasource information: ", exception);
		}
	}
	
	
	private void expireCart(Cart cart) throws ApplicationException {
		
		cart.setExpirationDate(now(-1000));
		storeCart(cart);
	}

	public Cart removeObjects(Integer cartId, Integer[] objectIds) throws ApplicationException {
		
		Cart realCart = getCart(cartId);
		Collection<CartObject> cartObjects = new HashSet<CartObject>();
		
		for(Integer i: objectIds){
			CartObject c = new CartObject();
			c.setId(i);
			cartObjects.add(c);
		}

		realCart.getCartObjectCollection().removeAll(cartObjects);

		return updateCart(realCart);		
	}
	
	public Cart removeObject(Integer cartId, Integer objectId) throws ApplicationException {
		
		Cart realCart = getCart(cartId);
		CartObject example = new CartObject();
		example.setId(objectId);
		
		realCart.getCartObjectCollection().remove(example);

		return updateCart(realCart);		
	}
	
	public Collection<CartObject> retrieveObjects(Integer cartId) throws ApplicationException{
		Cart example = new Cart();
		example.setId(cartId);
		List<Object> result = search(CartObject.class, example);
		Collection<CartObject> col = new HashSet<CartObject>();
		for (Object o:result){
			if (o instanceof CartObject)
				col.add((CartObject)o);
		}
		return col;
	}
	
	public Collection<CartObject> retrieveObjectsByType(Integer cartId, String type) throws ApplicationException {
			
		Cart cart = new Cart();
		cart.setId(cartId);
		cart = cartSearch(cart).get(0);
		
		return cartObjectSearchByType(cart, type);
		
		/*
		Cart example = new Cart();
		example.setId(cartId);
		CartObject objectExample = new CartObject();
		objectExample.setType(type);
		
		List<Object> examples = new ArrayList<Object>();
		examples.add(example);
		examples.add(objectExample);
		
		List<Object> result = search(CartObject.class, examples);
		Collection<CartObject> col = new HashSet<CartObject>();
		for (Object o:result){
			if (o instanceof CartObject)
				col.add((CartObject)o);
		}
		return col;*/
		
	}
	public CartObject retrieveObject(Integer cartId, Integer objectId)throws ApplicationException {

		Cart example = new Cart();
		example.setId(cartId);
		CartObject objectExample = new CartObject();
		objectExample.setId(objectId);
		
		List<Object> examples = new ArrayList<Object>();
		examples.add(example);
		examples.add(objectExample);
		
		List<Object> result = search(CartObject.class, examples);
		if (result.size() > 1)
			throw new ApplicationException("Too many results returned");
		else if (result.size() == 0)
			throw new ApplicationException("No such object exists");
		
		CartObject ret = null;
		for (Object o:result){
			if (o instanceof CartObject)
				ret = (CartObject)o;
		}
		return ret;
	}
		
	private Cart updateCart(Cart cart) throws ApplicationException, DAOException {
		
		cart.setLastWriteDate(now(0));
		CartDAO dao = (CartDAO) getClassCache().getDAOForClass(cart.getClass().getCanonicalName());
	
		if(dao == null)
			throw new ApplicationException("Could not obtain DAO for: "+cart.getClass().getCanonicalName());
		
		try
		{
			return dao.updateCart(cart);
			 
		} catch(DAOException daoException) {
			log.error("Error while getting and storing Cart in DAO",daoException);
			throw daoException;
		} catch (ApplicationException e1) {
			log.fatal("Unable to locate Service Locator :",e1);
			throw new ApplicationException("Unable to locate Service Locator :",e1);
		} catch(Exception exception) {
			log.error("Exception while getting datasource information "+ exception.getMessage());
			throw new ApplicationException("Exception in Base Delegate while getting datasource information: ", exception);
		}
	}
	
	private List<Cart> cartSearch(Cart cart) throws ApplicationException, DAOException {
		
		CartDAO dao = (CartDAO) getClassCache().getDAOForClass(cart.getClass().getCanonicalName());
	
		if(dao == null)
			throw new ApplicationException("Could not obtain DAO for: "+cart.getClass().getCanonicalName());
		
		try
		{
			List<Cart> carts = dao.cartSearch(cart);
			
			for (Cart c: carts){
				c.setLastReadDate(now(0));
				updateCart(c);
			}
			
			return carts;
			
		} catch(DAOException daoException) {
			log.error("Error while getting and storing Cart in DAO",daoException);
			throw daoException;
		} catch (ApplicationException e1) {
			log.fatal("Unable to locate Service Locator :",e1);
			throw new ApplicationException("Unable to locate Service Locator :",e1);
		} catch(Exception exception) {
			log.error("Exception while getting datasource information "+ exception.getMessage());
			throw new ApplicationException("Exception in Base Delegate while getting datasource information: ", exception);
		}
	}

	private List<CartObject> cartObjectSearchByType(Cart cart, String type) throws ApplicationException, DAOException {
		
		CartDAO dao = (CartDAO) getClassCache().getDAOForClass(cart.getClass().getCanonicalName());
	
		if(dao == null)
			throw new ApplicationException("Could not obtain DAO for: "+cart.getClass().getCanonicalName());
		
		try
		{
			List<CartObject> cartObjects = dao.cartObjectSearchByType(cart, type);
			return cartObjects;
			
		} catch(DAOException daoException) {
			log.error("Error while getting and storing Cart in DAO",daoException);
			throw daoException;
		} catch (ApplicationException e1) {
			log.fatal("Unable to locate Service Locator :",e1);
			throw new ApplicationException("Unable to locate Service Locator :",e1);
		} catch(Exception exception) {
			log.error("Exception while getting datasource information "+ exception.getMessage());
			throw new ApplicationException("Exception in Base Delegate while getting datasource information: ", exception);
		}
		
	}
	
	public Collection<CartObject> merge(Collection<CartObject> current, Collection<CartObject> incoming) {	
		
		for (CartObject c: current)
			incoming.remove(c);

		return incoming;
		
	}
	
	private Date now(long add) {
		return new Date(System.currentTimeMillis()+add);
	}
	
}
