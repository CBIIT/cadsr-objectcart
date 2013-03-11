package gov.nih.nci.objectCart.jUnit.applicationService.impl;

import gov.nih.nci.objectCart.dao.orm.CartORMDAOImpl;

import org.springframework.context.*;
import org.springframework.context.support.*;


public class TestDAO {
	
	public static void main (){
		
		ApplicationContext context = new ClassPathXmlApplicationContext("application-config.xml");
		CartORMDAOImpl cart = (CartORMDAOImpl) context.getBean("ORMDAO");
		
	}

}
