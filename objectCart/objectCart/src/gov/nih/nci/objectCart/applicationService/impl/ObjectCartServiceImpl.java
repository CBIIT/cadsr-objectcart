package gov.nih.nci.objectCart.applicationService.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Date;

import org.apache.log4j.Logger;

import gov.nih.nci.objectCart.applicationService.ObjectCartService;
import gov.nih.nci.objectCart.dao.CartDAO;
import gov.nih.nci.objectCart.domain.Cart;
import gov.nih.nci.objectCart.domain.CartObject;
import gov.nih.nci.objectCart.domain.ClassificationScheme;
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
	
	public Cart getNewCart(String userId, String cartName, String classificationSchemeType) throws ApplicationException {		
		Cart newCart = new Cart();	
		
		newCart.setUserId(userId);
		newCart.setName(cartName);
		
		ClassificationScheme cs = new ClassificationScheme();
		cs.setType(classificationSchemeType);
		cs = getClassificationScheme(cs);
		newCart.setClassificationScheme(cs);
		List<Cart> carts = getNewCarts(newCart);

		if (carts.size() > 1)
			throw new ApplicationException("More than one cart with that name/userId pairing exists (Expiration failure?)");
		else if(carts.isEmpty())
			throw new ApplicationException("No carts with that name/userId pairing exists: Create Failed (Should never get here)");
		else 
			newCart = carts.get(0);
		
		return newCart;

	}

	public List<Cart> getClassificationSchemeCarts(String classificationSchemeType) throws ApplicationException {
		Cart exampleCart = new Cart();	
		ClassificationScheme cs = new ClassificationScheme();
		
		cs.setType(classificationSchemeType);
		cs = getClassificationScheme(cs);
		exampleCart.setClassificationScheme(cs);

		List<Cart> carts = cartSearch(Cart.class, exampleCart);
		return carts;
	}
	
	public Cart getCart(Integer cartId) throws ApplicationException {
		Cart newCart = new Cart();
		newCart.setId(cartId);
		
		List<Cart> carts = cartSearch(Cart.class, newCart);
		
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

	public Cart getCart(String userId, String cartName, String classificationSchemeType) throws ApplicationException {
		Cart newCart = new Cart();	

		newCart.setUserId(userId);
		newCart.setName(cartName);
		ClassificationScheme cs = new ClassificationScheme();
		cs.setType(classificationSchemeType);
		cs = getClassificationScheme(cs);
		newCart.setClassificationScheme(cs);
		
		List<Cart> carts = getCarts(newCart);	
		
		if (carts.size() > 1)
			throw new ApplicationException("More than one cart with that userId/name pairing exists (SHOULD NOT HAPPEN)");
		else if(carts.isEmpty())
			throw new ApplicationException("No cart with that ID exist!");
		
		return carts.get(0);
	}
	
	private List<Cart> getCarts(Cart example) throws ApplicationException {
		List<Cart> carts = cartSearch(Cart.class, example);
					
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
	
	public Cart associateCart(String guestId, String userId, String cartName) throws ApplicationException {
		Cart newCart = new Cart();
		newCart.setName(cartName);
		newCart.setUserId(guestId);
		
		return associateCart(newCart, userId);
		
	}
	
	public void expireCart(Integer cartId) throws ApplicationException {
		Cart cart = getCart(cartId);
		expireCart(cart);
	}
	
	private Cart associateCart(Cart cart, String userId) throws ApplicationException {
		List<Cart> carts = cartSearch(Cart.class, cart);
		
		if (carts.size() > 1)
			throw new ApplicationException("More than one cart with that name/guestId pairing exists");
		else if(carts.isEmpty())
			throw new ApplicationException("No carts with that name/guestId pairing exists");
		else 
			cart = carts.get(0);
		
		cart.setUserId(userId);
		
		return storeCart(cart);
		
	}

	private List<Cart> getNewCarts(Cart newCart) throws ApplicationException {
		
		List<Cart> cartList = getCarts(newCart);
		if (cartList == null || cartList.isEmpty())
			cartList.add(createCart(newCart));
		
		return cartList;
	}

	private Cart createCart(Cart newCart) throws ApplicationException {
			
		Date now = now(0);
		newCart.setExpirationDate(now(24*60*60*1000));
		newCart.setCreationTime(now);
		newCart.setLastActive(now);
		
		return storeCart(newCart);
	}
	
	private Cart storeCart(Cart cart) throws ApplicationException, DAOException {
		
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
	
	private List<Cart> cartSearch(Class cl, Object obj) throws ApplicationException {
		//TODO Change class so it performs expiration date filter on DB side	
		List<Object> objects  = search(cl, obj);
		List<Cart> carts = new ArrayList<Cart>();
		
		for (Object o: objects) {
			if (o instanceof Cart){
				Cart c = (Cart) o;
				//Return only non-expired carts
				if (c.getExpirationDate().after(now(0)))
					carts.add(c);
			}
			else 
				throw new ApplicationException("Search for cart returned other than cart.  (Should not happen)");
		}
		return carts;		
	}
	
	private void expireCart(Cart cart) throws ApplicationException {
		
		cart.setExpirationDate(now(-1));
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
		return col;
		
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

	private ClassificationScheme getClassificationScheme(ClassificationScheme example) throws ApplicationException {
		
		List<Object> objects  = search(ClassificationScheme.class, example);
		
		if (objects.size() > 1) 
			throw new ApplicationException("Search for Classification Scheme by type returned more than one result.");
		else 
			return (ClassificationScheme) objects.get(0);
		
	}
	
	private Date now(long add) {
		return new Date(System.currentTimeMillis()+add);
	}
	
}
