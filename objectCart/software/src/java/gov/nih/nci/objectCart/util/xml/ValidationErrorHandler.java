/*L
 * Copyright Ekagra Software Technologies Ltd, SAIC-F
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/cadsr-objectcart/LICENSE.txt for details.
 */

package gov.nih.nci.objectCart.util.xml;

import org.apache.log4j.Logger;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class ValidationErrorHandler implements ErrorHandler {

	//TODO Complete error handling
	
	private static Logger log = Logger.getLogger(ValidationErrorHandler.class.getName());
	
	public void warning(SAXParseException e) throws SAXException {
		log.warn(e.getMessage());
		throw new SAXException(e);
	}

	public void error(SAXParseException e) throws SAXException {
		log.error(e.getMessage());
		throw new SAXException(e);
	}

	public void fatalError(SAXParseException e) throws SAXException {
		log.fatal(e.getMessage());
		throw new SAXException(e);
	}
}
	

