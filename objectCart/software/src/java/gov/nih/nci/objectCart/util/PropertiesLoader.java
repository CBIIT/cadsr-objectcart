package gov.nih.nci.objectCart.util;

import gov.nih.nci.system.applicationservice.ApplicationException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;


public class PropertiesLoader {

	private static ResourceBundle props;
	private static class SingletonHolder { 
		private final static PropertiesLoader inst = new PropertiesLoader();
	}

	public static PropertiesLoader getInstance() {
		return SingletonHolder.inst;
	}

	private static void loadProperties() throws ApplicationException {
			
		try {
			props = ResourceBundle.getBundle("objectCart");
		} catch (MissingResourceException mre) {
			mre.printStackTrace();
			throw new ApplicationException("Error loading properties", mre);
		}		
	}
	
	public static String getProperty(String propName) throws ApplicationException {
		if (props == null) 	
			loadProperties();
		
		return props.getString(propName);
	}
}
