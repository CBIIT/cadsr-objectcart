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