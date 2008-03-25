package gov.nih.nci.objectCart.dao.orm;

import java.util.Date;

import org.apache.log4j.Logger;
import org.hibernate.JDBCException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import gov.nih.nci.objectCart.dao.CartDAO;
import gov.nih.nci.objectCart.domain.Cart;
import gov.nih.nci.security.acegi.authentication.CSMAuthenticationProvider;
import gov.nih.nci.system.dao.DAOException;
import gov.nih.nci.system.dao.orm.ORMDAOImpl;

public class CartORMDAOImpl extends ORMDAOImpl implements CartDAO {

	private static Logger log = Logger.getLogger(CartORMDAOImpl.class.getName());
	
	public CartORMDAOImpl(SessionFactory sessionFactory, Configuration config,
			boolean caseSensitive, int resultCountPerQuery,
			boolean instanceLevelSecurity, boolean attributeLevelSecurity,
			CSMAuthenticationProvider authenticationProvider) {
		super(sessionFactory, config, caseSensitive, resultCountPerQuery,
				instanceLevelSecurity, attributeLevelSecurity,
				authenticationProvider);
		// TODO Auto-generated constructor stub
	}

	public Cart storeCart(Cart newCart) throws DAOException, Exception {
		
		Session session = getSession();	
		Transaction t = session.beginTransaction();
		try
		{
			newCart.setLastActive(new Date(System.currentTimeMillis()));
			session.saveOrUpdate(newCart);

		} catch (JDBCException ex){
			log.error("JDBC Exception in ORMDAOImpl ", ex);
			throw new DAOException("JDBC Exception in ORMDAOImpl ", ex);
		} catch(org.hibernate.HibernateException hbmEx)	{
			log.error(hbmEx.getMessage());
			throw new DAOException("DAO:Hibernate problem ", hbmEx);
		} catch(Exception e) {
			log.error("Exception ", e);
			throw new DAOException("Exception in ORMDAOImpl ", e);
		} finally {
			try
			{						
				t.commit();
				session.close();
			}
			catch (Exception eSession)
			{
				log.error("Could not close the session - "+ eSession.getMessage());
				throw new DAOException("Could not close the session  " + eSession);
			}
		}
		
		return newCart;
	}
	
	public Cart updateCart(Cart newCart) throws DAOException, Exception {
		
		Session session = getSession();	
		Transaction t = session.beginTransaction();
		try
		{
			newCart.setLastActive(new Date(System.currentTimeMillis()));
			session.update(newCart);

		} catch (JDBCException ex){
			log.error("JDBC Exception in ORMDAOImpl ", ex);
			throw new DAOException("JDBC Exception in ORMDAOImpl ", ex);
		} catch(org.hibernate.HibernateException hbmEx)	{
			log.error(hbmEx.getMessage());
			throw new DAOException("DAO:Hibernate problem ", hbmEx);
		} catch(Exception e) {
			log.error("Exception ", e);
			throw new DAOException("Exception in ORMDAOImpl ", e);
		} finally {
			try
			{						
				t.commit();
				session.close();
			}
			catch (Exception eSession)
			{
				log.error("Could not close the session - "+ eSession.getMessage());
				throw new DAOException("Could not close the session  " + eSession);
			}
		}
		
		return newCart;
	}
}
