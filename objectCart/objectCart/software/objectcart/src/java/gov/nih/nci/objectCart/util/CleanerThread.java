package gov.nih.nci.objectCart.util;

import gov.nih.nci.objectCart.dao.orm.CleanerDAO;
import gov.nih.nci.system.applicationservice.ApplicationException;
import org.apache.log4j.Logger;

public class CleanerThread extends Thread {
	private long secs = 600;
	private static Logger log = Logger.getLogger(CleanerThread.class.getName());
	
	public void run() {
		long temp = 0;
		CleanerDAO cleaner = new CleanerDAO();
		try {
			temp = Long.valueOf(PropertiesLoader.getProperty("cart.cleaner.sleep.seconds"));
		} catch (Exception ae) {
			log.error(ae);
			ae.printStackTrace();
		}
		
		if (temp != 0)
			secs = temp;
		
		while (true) {
			try {
				log.info("Cleaner running");
				cleaner.clean();
				log.info("Cleaner stopped");
				Thread.sleep(secs*1000);
			} catch (InterruptedException ie){
				log.fatal(ie.getMessage());
				ie.printStackTrace();	
			}		
		}
	}
}
