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
		int expirationInterval = 4*24*60;  //Four days, in minutes
		int sleepTime = 60;  //One hour, in minutes
		
		//Defaults
		int publicEmptyExpirationDays = 4*24*60;
		String emptyExpirationSQL = "DATE_ADD(NOW(), INTERVAL "+publicEmptyExpirationDays+" MINUTE)";
		
		int publicFullExpirationDays = 30*24*60;  //30 days, in minutes
		String fullExpirationSQL = "DATE_ADD(NOW(), INTERVAL "+publicFullExpirationDays+" MINUTE)";
		
		
		
		try {
			int temp = Integer.valueOf(PropertiesLoader.getProperty("cart.time.expiration.minutes"));
			expirationInterval = temp;
		} catch (Exception e) { log.error(e); }
		
		try {
			int temp = Integer.valueOf(PropertiesLoader.getProperty("cart.cleaner.sleep.minutes"));
			sleepTime = temp;
		} catch (Exception e) { log.error(e); }
		
		try {
			int temp = Integer.valueOf(PropertiesLoader.getProperty("cart.public.empty.expiration.minutes"));
			publicEmptyExpirationDays = temp;
		} catch (Exception e) { log.error(e); }
		
		try {
			int temp = Integer.valueOf(PropertiesLoader.getProperty("cart.public.full.expiration.minutes"));
			publicFullExpirationDays = temp;
		} catch (Exception e) { log.error(e); }
		
		
		//Timestamps are in milliseconds
		Timestamp now = new Timestamp(System.currentTimeMillis());	
		Timestamp nowMinusTwiceSleep = new Timestamp(now.getTime()-sleepTime*60*1000*2);  //Converting minutes to milliseconds
		Timestamp nowPlusExpirationInterval = new Timestamp(now.getTime()+expirationInterval*60*1000);  //Converting minutes to milliseconds
		
		Query updateActiveCarts = session.createQuery("update Cart set expirationDate = :nowPlusExpirationInterval" +
				" where (lastWriteDate > :nowMinusTwiceSleep or lastReadDate > :nowMinusTwiceSleep) and expirationDate > :now and expirationDate < :nowPlusExpirationInterval");
		
		updateActiveCarts.setTimestamp("nowPlusExpirationInterval", nowPlusExpirationInterval);
		updateActiveCarts.setTimestamp("nowMinusTwiceSleep", nowMinusTwiceSleep);
		updateActiveCarts.setTimestamp("now", now);
		

		if (publicEmptyExpirationDays > 0 && publicEmptyExpirationDays < 365*24*60)				//Check expiration is within a year
			emptyExpirationSQL = "DATE_ADD(NOW(), INTERVAL "+ publicEmptyExpirationDays+" MINUTE)";
		else if (publicEmptyExpirationDays == 0)
			emptyExpirationSQL = "NOW()";
		
		if (publicFullExpirationDays > 0 && publicFullExpirationDays < 365)						//Check expiration is within a year
			fullExpirationSQL = "DATE_ADD(NOW(), INTERVAL "+ publicFullExpirationDays+" MINUTE)";
		else if (publicFullExpirationDays == 0)
			fullExpirationSQL = "NOW()";

		
		//Set expiration date to emptyExpirationSQL if the user starts with 'PublicUser' and the current expiration date is null
		String initializeSessionCartSql = "UPDATE cart c left join cart_object co on c.id = co.cart_id " +
				" set expiration_Date = "+emptyExpirationSQL+" where" +  
				" (c.user_Id like 'PublicUser%') and " +
				" (c.expiration_Date is null)";
		
		Query initPublicCarts = session.createSQLQuery(initializeSessionCartSql);		
		
		//Set expiration date to fullExpiration if the user starts with 'PublicUser' and the current expiration date is null and the cart has items
		String nonEmptyCartSql = "UPDATE cart c left join cart_object co on c.id = co.cart_id " +
		" set expiration_Date = "+fullExpirationSQL+" where" +
		" (c.user_Id like 'PublicUser%') and " +
		" (c.expiration_Date is null) and (co.id is not null)";
		Query expNonEmptyPublicCarts = session.createSQLQuery(nonEmptyCartSql);				
		
		
		//Now delete expired carts (carts where expiration date is in the past)
		//REQUIRES ON-DELETE Cascade support in underlying database on the 
		//CartObject cart_id FK constraint
		Query deleteCartQuery = session.createQuery("delete from Cart " +
				"where expirationDate <=:now");
		
		deleteCartQuery.setTimestamp("now", now );  
		
		try
		{	
			int resetResults = updateActiveCarts.executeUpdate();
				if (resetResults > 0)
					log.debug("Reset expiration date for "+resetResults+"active carts");
				log.debug("Reset expiration date for "+resetResults+"active carts");
			/* GF 28500 */
			int expResults = initPublicCarts.executeUpdate();
			if (expResults > 0)
				log.debug("Expiration date set for "+expResults+" PublicUser carts");			
			int expNEPCResults = expNonEmptyPublicCarts.executeUpdate();
			if (expNEPCResults > 0)
				log.debug("Expiration date set for "+expNEPCResults+" PublicUser carts");			
			/* GF 28500 */
			
			int results = deleteCartQuery.executeUpdate();
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
