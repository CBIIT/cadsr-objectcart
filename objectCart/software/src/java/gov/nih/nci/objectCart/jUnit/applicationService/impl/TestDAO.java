/*L
 * Copyright Ekagra Software Technologies Ltd, SAIC-F
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/cadsr-objectcart/LICENSE.txt for details.
 */

package gov.nih.nci.objectCart.jUnit.applicationService.impl;



import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class TestDAO extends HibernateDaoSupport
{
	public void saveOrUpdate(Object o)
	{
		Session s = getSession();
		Transaction tx = s.beginTransaction();
		s.saveOrUpdate(o);
		//getHibernateTemplate().saveOrUpdate(o);
		tx.commit();
	}
}