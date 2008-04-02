package gov.nih.nci.objectCart.util;

import gov.nih.nci.system.applicationservice.ApplicationException;

import java.io.IOException;
import java.util.Properties;


public class PropertiesLoader {

	private static Properties props = null;
	
	private static class SingletonHolder { 
		private final static PropertiesLoader inst = new PropertiesLoader();
	}

	public static PropertiesLoader getInstance() {
		return SingletonHolder.inst;
	}

	private static void loadProperties() throws ApplicationException {
		
		props = new Properties();
		java.net.URL url = ClassLoader.getSystemResource("objectCart.properties");
		try {
			props.load(url.openStream());
		} catch (IOException ioe) {
			throw new ApplicationException("Error loading properties", ioe);
		}		
	}
	
	public static String getProperty(String propName) throws ApplicationException {
		if (props == null) 	
			loadProperties();
		
		return props.getProperty(propName);
	}
}
