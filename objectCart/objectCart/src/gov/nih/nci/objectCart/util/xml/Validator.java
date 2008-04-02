package gov.nih.nci.objectCart.util.xml;

import gov.nih.nci.objectCart.domain.CartObject;
import gov.nih.nci.system.applicationservice.ApplicationException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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
import org.projectmobius.client.gme.ImportInfo;
import org.projectmobius.common.GridServiceResolver;
import org.projectmobius.common.MobiusException;
import org.projectmobius.common.Namespace;
import org.projectmobius.gme.XMLDataModelService;
import org.projectmobius.gme.client.GlobusGMEXMLDataModelServiceFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public class Validator {

	private static Logger log = Logger.getLogger(Validator.class.getName());
	
	public static void validateObject(CartObject cartObject)
			throws ApplicationException {
		
		if (!cartObject.getType().startsWith(":Serialized:") &&
			!cartObject.getType().startsWith(":Test:") &&
			!cartObject.getType().startsWith("http://"))
		{
			try {
				SAXParserFactory factory = SAXParserFactory.newInstance();
				factory.setValidating(false);
				factory.setNamespaceAware(true);
	
				SchemaFactory schemaFactory = SchemaFactory
						.newInstance("http://www.w3.org/2001/XMLSchema");
				
				factory.setSchema(
						schemaFactory.newSchema(
								new Source[] {new StreamSource(getSchema(cartObject.getType()))} ));
	
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
	
	
	public static File getSchema(String name) {
		
		GridServiceResolver.getInstance().setDefaultFactory(new GlobusGMEXMLDataModelServiceFactory());  
		File schemaFile = null;
		
		try { 

			if (name.startsWith("gme://")) {
				Namespace ns = new Namespace(name);
				ImportInfo ii= new ImportInfo(ns);
				
	            String filename = ii.getFileName();
					
				XMLDataModelService handle = 
					(XMLDataModelService) GridServiceResolver.getInstance().getGridService("http://training02.cagrid.org:6080/wsrf/services/cagrid/GlobalModelExchange"); 
					
				schemaFile = new File("localcontent"+ File.separator +filename);
				
				List namespaces = null;
				if (!schemaFile.canRead()) 
					namespaces = handle.cacheSchema(ns, new File("localcontent"));
			} else {
				schemaFile = new File("localcontent"+ File.separator +name);
			}			
		} catch (MobiusException e1) {
				e1.printStackTrace(); 
		} 
		
		return schemaFile;
	}
	
    public static String readFileAsString(File file)
    throws java.io.IOException{
        StringBuilder fileData = new StringBuilder(1000);
        BufferedReader reader = new BufferedReader(
                new FileReader(file));
        char[] buf = new char[1024];
        int numRead=0;
        while((numRead=reader.read(buf)) != -1){
            String readData = String.valueOf(buf, 0, numRead);
            fileData.append(readData);
            buf = new char[1024];
        }
        reader.close();
        return fileData.toString();
    }
}


