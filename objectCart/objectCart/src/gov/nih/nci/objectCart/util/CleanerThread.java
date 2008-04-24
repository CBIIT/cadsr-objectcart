package gov.nih.nci.objectCart.util;

import gov.nih.nci.objectCart.dao.orm.CleanerDAO;
import gov.nih.nci.system.applicationservice.ApplicationException;
import org.apache.log4j.Logger;

public class CleanerThread extends Thread {
	long secs = 600;
	private static Logger log = Logger.getLogger(CleanerThread.class.getName());
	
	public void run() {
		long temp = 0;
		CleanerDAO cleaner = new CleanerDAO();
		try {
			temp = Long.getLong(PropertiesLoader.getProperty("cart.cleaner.sleep.seconds"));
		} catch (ApplicationException ae) {
			log.error(ae);
		}
		
		if (temp != 0)
			secs = temp;
		
		while (true) {
			try {
				cleaner.clean();
				Thread.sleep(secs*1000);
			} catch (InterruptedException ie){
				log.fatal(ie.getMessage());
				ie.printStackTrace();	
			}		
		}
	}
}
