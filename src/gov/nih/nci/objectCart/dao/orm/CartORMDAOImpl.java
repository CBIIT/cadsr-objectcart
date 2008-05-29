package gov.nih.nci.objectCart.dao.orm;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.JDBCException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import gov.nih.nci.objectCart.dao.CartDAO;
import gov.nih.nci.objectCart.domain.Cart;
import gov.nih.nci.objectCart.util.PropertiesLoader;
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
			newCart.setLastWriteDate(new Date(System.currentTimeMillis()));
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

	public List<Cart> cartSearch(Cart exampleCart) throws DAOException, Exception {
		List<Cart> results = new ArrayList<Cart>();
		Session session = getSession();	

		Transaction t = session.beginTransaction();
		StringBuilder query = new StringBuilder();
		query.append("from Cart where");

		if (exampleCart.getId() != null)
			query.append(" id = :cartId");
		else {
			int andCntr = 0;
			if (exampleCart.getUserId() != null && exampleCart.getUserId().length() > 0){
				query.append(" userId = :userId");
				andCntr++;
			}
			if (exampleCart.getName() != null && exampleCart.getName().length() > 0){
				if (andCntr >0)
					query.append(" and");
				query.append(" name = :name");
				andCntr++;
			}
			if (exampleCart.getType() != null && exampleCart.getType().length() > 0){
				if (andCntr >0)
					query.append(" and");
				query.append(" type = :type");
			}
		}
			query.append(" and (expirationDate > :expirationDate or expirationDate is null)");
		
		
		Query q = session.createQuery(query.toString());
		String[] params = q.getNamedParameters();

		for (String param: params){
			
			if ("cartId".equals(param))
				q.setInteger(param, exampleCart.getId());
			else {
				if ("userId".equals(param)) 
					q.setString(param, exampleCart.getUserId());
				if ("name".equals(param))
					q.setString(param, exampleCart.getName());
				if ("type".equals(param))
					q.setString(param, exampleCart.getType());
			}
		}
		
		q.setTimestamp("expirationDate", new Timestamp(System.currentTimeMillis()));

		try
		{
			results = (List<Cart>)q.list();
			
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
		return results;
	}
}
