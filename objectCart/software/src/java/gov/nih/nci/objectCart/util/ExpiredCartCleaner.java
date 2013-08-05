/*L
 * Copyright Ekagra Software Technologies Ltd, SAIC-F
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/cadsr-objectcart/LICENSE.txt for details.
 */

package gov.nih.nci.objectCart.util;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;

public class ExpiredCartCleaner implements ServletContextListener {
	CleanerThread cleanerThread = new CleanerThread();
	private static Logger log = Logger.getLogger(ExpiredCartCleaner.class.getName());
	
	public void contextInitialized(ServletContextEvent event)
	{
		log.info("Starting cleaner thread");
		cleanerThread.start(); 
	}
	public void contextDestroyed(ServletContextEvent event)
	{
		cleanerThread.interrupt();
		log.info("Cleaner thread stopped");
	}
}
