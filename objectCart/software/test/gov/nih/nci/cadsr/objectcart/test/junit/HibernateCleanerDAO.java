package gov.nih.nci.cadsr.objectcart.test.junit;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import gov.nih.nci.objectCart.domain.Cart;

public class HibernateCleanerDAO extends HibernateDaoSupport /*implements CleanerDAO*/ {

	public void insert(Cart cart) {
		getHibernateTemplate().save(cart);
	}

	public void create(Cart newEntity) {
		insert(newEntity);
	}

}