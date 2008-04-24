package gov.nih.nci.objectCart.dao.orm;

import java.sql.Timestamp;

import org.apache.log4j.Logger;
import org.hibernate.JDBCException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class CleanerDAO extends HibernateDaoSupport {

	private static Logger log = Logger.getLogger(CleanerDAO.class.getName());	
	private static SessionFactory sessionFactory = null;
	
	private static void setFactory() {
		try {
			// Create the SessionFactory from hibernate.cfg.xml
			sessionFactory = new Configuration().configure().buildSessionFactory();
		} catch (Throwable ex) {
			// Make sure you log the exception, as it might be swallowed
			log.error("Initial SessionFactory creation failed." + ex);
			throw new ExceptionInInitializerError(ex);
		}
	}
	public void clean()
	{
		if (sessionFactory == null) {
			setFactory();
			this.setSessionFactory(sessionFactory);
		}
		Session session = getSession();
		Transaction tx = session.beginTransaction();

		//REQUIRES ON-DELETE Cascade support in underlying database on the 
		//CartObject cart_id FK constraint
		Query cartQuery = session.createQuery("delete from Cart " +
				"where expirationDate <=:expirationDate");
		
		Timestamp now = new Timestamp(System.currentTimeMillis());	
		cartQuery.setTimestamp("expirationDate", now );		
		
		try
		{	
			int results = cartQuery.executeUpdate();
			log.info("Deleted "+results+" carts at "+now.toString());
			
		} catch (JDBCException ex){
			log.error("JDBC Exception in ORMDAOImpl ", ex);
			ex.printStackTrace();
		} catch(org.hibernate.HibernateException hbmEx)	{
			log.error(hbmEx.getMessage());
			hbmEx.printStackTrace();
		} catch(Exception e) {
			log.error("Exception ", e);
			e.printStackTrace();
		} finally {
			try
			{						
				tx.commit();
				session.close();
			}
			catch (Exception eSession)
			{
				log.error("Could not close the session - "+ eSession.getMessage());
				eSession.printStackTrace();
			}
		}
	}

}
