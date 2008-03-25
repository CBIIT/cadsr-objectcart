package gov.nih.nci.objectCart.dao;

import java.util.Collection;

import gov.nih.nci.objectCart.domain.Cart;
import gov.nih.nci.objectCart.domain.CartObject;
import gov.nih.nci.system.dao.DAO;
import gov.nih.nci.system.dao.DAOException;

/**
 * @author Denis Avdic
 */

public interface CartDAO extends DAO {

	public Cart storeCart(Cart newCart) throws DAOException, Exception;
	public Cart updateCart(Cart newCart) throws DAOException, Exception;
}
