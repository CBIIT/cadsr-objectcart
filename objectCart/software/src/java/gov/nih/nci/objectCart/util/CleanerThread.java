/*L
 * Copyright Ekagra Software Technologies Ltd, SAIC-F
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/cadsr-objectcart/LICENSE.txt for details.
 */

package gov.nih.nci.objectCart.util;

import gov.nih.nci.objectCart.dao.orm.CleanerDAO;
import gov.nih.nci.system.applicationservice.ApplicationException;
import org.apache.log4j.Logger;

public class CleanerThread extends Thread {
	private int sleepTime = 60;
	private static Logger log = Logger.getLogger(CleanerThread.class.getName());
	
	public void run() {
		
		CleanerDAO cleaner = new CleanerDAO();
		try {
			int temp = Integer.valueOf(PropertiesLoader.getProperty("cart.cleaner.sleep.minutes"));  //Get property value
			sleepTime = temp;  //set to sleep time
		} catch (Exception ae) {
			log.error(ae);
			ae.printStackTrace();
		}
		
		while (true) {
			try {
				log.info("Cleaner running");
				cleaner.clean();
				log.info("Cleaner stopped");
				Thread.sleep(sleepTime*60*1000);  //Put thread to sleep, converting minutes to milliseconds
			} catch (InterruptedException ie){
				log.fatal(ie.getMessage());
				ie.printStackTrace();	
			}		
		}
	}
}
