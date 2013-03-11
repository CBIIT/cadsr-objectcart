package gov.nih.nci.objectCart.dao.orm;
import org.springframework.context.*;
import org.springframework.context.support.*;


public class TestORMDAO {
	
	public static void main (){
		
		ApplicationContext context = new ClassPathXmlApplicationContext("application-config.xml");
		CartORMDAOImpl cart = (CartORMDAOImpl) context.getBean("ORMDAO");
		
	}

}
