package gov.nih.nci.objectCart.dao.orm;

import java.sql.Timestamp;

import org.apache.log4j.Logger;
import org.hibernate.JDBCException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class CleanerDAO extends HibernateDaoSupport {
	
	private static Logger log = Logger.getLogger(CleanerDAO.class.getName());
	
	
	public void clean()
	{
		Session session = getSession();
		Transaction tx = session.beginTransaction();
		
		StringBuilder query = new StringBuilder();
		query.append("delete from Cart where");

		
		
		query.append(" EXPIRATION_DATE <= :expirationDate");	
		Query q = session.createQuery(query.toString());
		Timestamp now = new Timestamp(System.currentTimeMillis());
		q.setTimestamp("expirationDate", now );
		
		try
		{
			int results = q.executeUpdate();
			log.info("Cleaner thread ran and deleted "+results+" carts at "+now.toString());
			
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
