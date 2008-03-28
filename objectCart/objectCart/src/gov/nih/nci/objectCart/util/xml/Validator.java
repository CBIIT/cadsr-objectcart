package gov.nih.nci.objectCart.util.xml;

import gov.nih.nci.objectCart.domain.CartObject;
import gov.nih.nci.system.applicationservice.ApplicationException;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;

import org.apache.log4j.Logger;
import org.projectmobius.common.GridServiceResolver;
import org.projectmobius.common.MobiusException;
import org.projectmobius.common.Namespace;
import org.projectmobius.gme.XMLDataModelService;
import org.projectmobius.gme.client.GlobusGMEXMLDataModelServiceFactory;
import org.projectmobius.protocol.gme.SchemaNode;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public class Validator {

	private static Logger log = Logger.getLogger(Validator.class.getName());
	
	public static void validateObject(CartObject cartObject)
			throws ApplicationException {
		
		if (!cartObject.getType().startsWith(":Serialized:") &&
			!cartObject.getType().startsWith(":Test:"))
		{
			try {
				SAXParserFactory factory = SAXParserFactory.newInstance();
				factory.setValidating(false);
				factory.setNamespaceAware(true);
	
				SchemaFactory schemaFactory = SchemaFactory
						.newInstance("http://www.w3.org/2001/XMLSchema");
				
				String schema = getSchema(cartObject.getType());
				
				factory.setSchema(
						schemaFactory.newSchema(
								new Source[] {new StreamSource(schema)} ));
	
				SAXParser parser = factory.newSAXParser();
	
				XMLReader reader = parser.getXMLReader();
	
				reader.setErrorHandler(new ValidationErrorHandler());
				StringReader strReader = new StringReader(cartObject.getData());
				InputSource inputSource = new InputSource(strReader);
	
				reader.parse(inputSource);
	
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
				log.error("Parser Configuration Error");
				throw new ApplicationException("Parser Configuration Error", e);
			} catch (SAXException e) {
				e.printStackTrace();
				log.error("Error during validation");
				throw new ApplicationException("Error During Validation", e);
	        } catch (IOException e) {
	            e.printStackTrace();
	            log.error("IOException in Validation");
	            throw new ApplicationException("IO Exception in Validation", e);
	        }	
		}
	}
	
	
	public static String getSchema(String name) {
		
		GridServiceResolver.getInstance().setDefaultFactory(new GlobusGMEXMLDataModelServiceFactory());  
		
		try { 
			Namespace ns = new Namespace(name);
			File f = new File("localcontent/"+name);
		
			XMLDataModelService handle = 
				(XMLDataModelService) GridServiceResolver.getInstance().getGridService("http://dc04.bmi.ohio-state.edu:8080/ogsa/services/cagrid/gme"); 
			List nodes = handle.cacheSchema(ns, f);
			
			String schema = constructSchema(nodes);
			return schema;
			
			
		} catch (MobiusException e1) {
				e1.printStackTrace(); 
		}
		return null;
	}
		
	private static String constructSchema(List nodes) {
		
		for (Object o: nodes){
			SchemaNode sn = (SchemaNode) o;
			System.out.println("+++++++++++++++++++++");
			System.out.println(sn.getSchemaContents());
		}
		return "";
	}
}


