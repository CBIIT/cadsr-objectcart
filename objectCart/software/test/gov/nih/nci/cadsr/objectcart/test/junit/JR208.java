/*
 * Copyright 2002-2005 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package gov.nih.nci.cadsr.objectcart.test.junit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import gov.nih.nci.objectCart.domain.Cart;

import java.sql.Timestamp;
import java.util.Calendar;

import org.apache.log4j.Logger;
import org.hibernate.JDBCException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.classic.Session;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.orm.hibernate3.HibernateTemplate;

//@RunWith(MockitoJUnitRunner.class)
/**
 * Reference: http://hibernate-rest-interface.googlecode.com/svn/trunk/src/test/java/com/google/code/hibernate/rest/impl/EntityManagerImplTest.java
 *
 * Setup:
 * 
 * Make sure the junit 4.4 and hamcrest jar files are at the top of the classpath.
 */
@RunWith(MockitoJUnitRunner.class)
public class JR208 {

	private Logger log;
	private SQLQuery query;
	private Session session;
	private Transaction tx;
	private Cart cart;
	private String entityName;
	private String id;
	private int intId;
	
	@Mock
	private SessionFactory sessionFactory;
//	private EntityManagerImpl impl;	//just for jpa

	@Before
	public void setup() throws Exception {
		this.log = mock(Logger.class);
		this.query = mock(SQLQuery.class);
		when(query.setString(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(query);
		when(query.setComment(Mockito.anyString())).thenReturn(query);
		this.session = mock(Session.class);
	    tx = mock(Transaction.class);
		when(session.createSQLQuery(Mockito.anyString())).thenReturn(query);
		when(session.createQuery(Mockito.anyString())).thenReturn(query);
//		this.dao = new CleanerDAO(new Provider<Session>() {
//			@Override
//			public Session get() {
//				return session;
//			}
//		}, logger);
//		MockitoAnnotations.initMocks(log);
//		MockitoAnnotations.initMocks(query);
//		MockitoAnnotations.initMocks(session);
		
//		impl = new EntityManagerImpl(sessionFactory);

		id = "1234567890";
		intId = 1234567890;
		entityName = "PublicUser1234567890";
		cart = new Cart();
		cart.setId(new Integer(intId));
		cart.setName(entityName);
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -7);	//created 7 days ago
		cart.setCreationDate(cal.getTime());
	}

	@Test
	public void testCreate() {
		HibernateCleanerDAO dao = new HibernateCleanerDAO();

		HibernateTemplate mockTemplate = mock(HibernateTemplate.class);
		when(mockTemplate.save(anyObject())).thenReturn((long) 1);

		dao.setHibernateTemplate(mockTemplate);

		dao.create(cart);
		verify(mockTemplate).save(cart);
		
//		Cart persistedEntity = (Cart) mockTemplate.get("PublicUser1234567890", new Integer(1));
//		System.out.println("persistedEntity = [" + persistedEntity + "]");
	}

	@Test
	public void testGet() {
		Session session = mock(Session.class);
		when(sessionFactory.openSession()).thenReturn(session);
		when(session.get(entityName, id)).thenReturn(cart);
		
		assertThat(session.get(entityName, id), is((Object) cart));
	}

	@Test
	public void testDelete() {
		Session session = mock(Session.class);
		when(sessionFactory.openSession()).thenReturn(session);
		when(session.load(Cart.class, intId)).thenReturn(cart);

//		testCreate();

		Timestamp now = new Timestamp(System.currentTimeMillis());
		String deleteSQL = "delete from Cart " //+ "where creationDate < :now"
		;

		Query mockedQuery = mock(Query.class);
		when(session.createQuery(deleteSQL)).thenReturn(mockedQuery);
	    when(mockedQuery.setParameter("now", now)).thenReturn(mockedQuery);
	    
		Query deleteCartQuery = session.createQuery(deleteSQL);
		
		deleteCartQuery.setTimestamp("now", now );  
		
		try {	
//			int resetResults = updateActiveCarts.executeUpdate();
//				if (resetResults > 0)
//					log.debug("Reset expiration date for "+resetResults+"active carts");
//				log.debug("Reset expiration date for "+resetResults+"active carts");
//			/* GF 28500 */
//			int expResults = initPublicCarts.executeUpdate();
//			if (expResults > 0)
//				log.debug("Expiration date set for "+expResults+" PublicUser carts");			
//			int expNEPCResults = expNonEmptyPublicCarts.executeUpdate();
//			if (expNEPCResults > 0)
//				log.debug("Expiration date set for "+expNEPCResults+" PublicUser carts");			
			/* GF 28500 */
			
			int results = deleteCartQuery.executeUpdate();
			if (results > 0) {
				log.debug("Deleted "+results+" carts at "+now.toString());
				System.out.println("Deleted "+results+" carts at "+now.toString());
			} else {
				System.out.println("Nothing is deleted!");
			}
			
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
		
		Cart existingEntity = (Cart) session.load(Cart.class, intId);
		System.out.println("existingEntity = [" + existingEntity + "]");
	}

}