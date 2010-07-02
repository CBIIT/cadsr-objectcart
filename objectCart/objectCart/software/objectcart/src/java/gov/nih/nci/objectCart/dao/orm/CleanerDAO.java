package gov.nih.nci.objectCart.dao.orm;

import gov.nih.nci.objectCart.util.PropertiesLoader;

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
			ex.printStackTrace();
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
		
		//Check for unexpired carts that have been active within the expiration interval and reset expiration time.
		long expirationInterval = 5760;
		long sleepTime = 600;
		try {
			long temp = Long.valueOf(PropertiesLoader.getProperty("cart.time.expiration.minutes"));
			expirationInterval = temp;
		} catch (Exception e) {}
		
		try {
			long temp = Long.valueOf(PropertiesLoader.getProperty("cart.cleaner.sleep.seconds"));
			sleepTime = temp;
		} catch (Exception e) {}
		
		Timestamp now = new Timestamp(System.currentTimeMillis());	
		Timestamp nowMinusTwiceSleep = new Timestamp(now.getTime()-sleepTime*2*1000);
		Timestamp nowPlusExpirationInterval = new Timestamp(now.getTime()+expirationInterval*60*1000);
		
		Query updateActiveCarts = session.createQuery("update Cart set expirationDate = :nowPlusExpirationInterval" +
				" where (lastWriteDate > :nowMinusTwiceSleep or lastReadDate > :nowMinusTwiceSleep) and expirationDate > :now ");
		
		updateActiveCarts.setTimestamp("nowPlusExpirationInterval", nowPlusExpirationInterval);
		updateActiveCarts.setTimestamp("nowMinusTwiceSleep", nowMinusTwiceSleep);
		updateActiveCarts.setTimestamp("now", now);
		
		/* GF 28500 */
		//Timestamp nowMinus24Hrs = new Timestamp(now.getTime() - 1440*60*1000);		
		//Timestamp nowPlusThirtyDays = new Timestamp(now.getTime() - 30*24*60*1000);
		String emptyCartSql = "UPDATE cart c left join cart_object co on c.id = co.cart_id " +
				" set expiration_Date = NOW() where" +
				" (c.user_Id like 'PublicUser%') and " +
				" (c.last_write_date < DATE_SUB(NOW(), INTERVAL 1 DAY) OR  c.last_read_date < DATE_SUB(NOW(), INTERVAL 1 DAY)) and" +
				" (c.expiration_Date is null) and (co.id is null)";
		Query expPublicCarts = session.createSQLQuery(emptyCartSql);		
		
		String nonEmptyCartSql = "UPDATE cart c left join cart_object co on c.id = co.cart_id " +
		" set expiration_Date = DATE_ADD(NOW(), INTERVAL 30 DAY) where" +
		" (c.user_Id like 'PublicUser%') and " +
		" (c.last_write_date < DATE_SUB(NOW(), INTERVAL 1 DAY) OR  c.last_read_date < DATE_SUB(NOW(), INTERVAL 1 DAY)) and" +
		" (c.expiration_Date is null) and (co.id is not null)";
		Query expNonEmptyPublicCarts = session.createSQLQuery(nonEmptyCartSql);				
		/* GF 28500 */
		
		//Now delete expired carts
		//REQUIRES ON-DELETE Cascade support in underlying database on the 
		//CartObject cart_id FK constraint
		Query cartQuery = session.createQuery("delete from Cart " +
				"where expirationDate <=:expirationDate");
		
		cartQuery.setTimestamp("expirationDate", now );		
		
		try
		{	
			/* GF 28500 */
			int expResults = expPublicCarts.executeUpdate();
			if (expResults > 0)
				log.debug("Expiration date set for "+expResults+" PublicUser carts");			
			int expNEPCResults = expNonEmptyPublicCarts.executeUpdate();
			if (expNEPCResults > 0)
				log.debug("Expiration date set for "+expNEPCResults+" PublicUser carts");			
			/* GF 28500 */
			int resetResults = updateActiveCarts.executeUpdate();
			if (resetResults > 0)
				log.debug("Reset expiration date for "+resetResults+"active carts");
			int results = cartQuery.executeUpdate();
			if (results > 0)
				log.debug("Deleted "+results+" carts at "+now.toString());
			
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
