package gov.nih.nci.objectCart.jUnit.applicationService.impl;
import gov.nih.nci.objectCart.util.xml.ValidationErrorHandler;
import gov.nih.nci.objectCart.util.xml.Validator;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import junit.framework.TestCase;

public class GMETest extends TestCase{
	
		public void test1()
		{
			SAXParserFactory factory = SAXParserFactory.newInstance();
			factory.setValidating(false);
			factory.setNamespaceAware(true);

			SchemaFactory schemaFactory = SchemaFactory
					.newInstance("http://www.w3.org/2001/XMLSchema");
			
			String schema = Validator.getSchema("gme://caCORE.caBIG/3.0/gov.nih.nci.cadsr.domain");
			
			System.out.println(schema);
//			try {
//			factory.setSchema(
//					schemaFactory.newSchema(
//							new Source[] {new StreamSource(schema)} ));
//
//			SAXParser parser = factory.newSAXParser();
//
//			XMLReader reader = parser.getXMLReader();
//		
//			reader.setErrorHandler(new ValidationErrorHandler());
//			StringReader strReader = new StringReader("TEST DATA HERE");
//			InputSource inputSource = new InputSource(strReader);
//
//			reader.parse(inputSource);	
//			
//			} catch (SAXException se){	
//				se.printStackTrace();
//			} catch (IOException ioe){	
//				ioe.printStackTrace();
//			} catch (ParserConfigurationException pce){	
//				pce.printStackTrace();
//			}
		}
	
}
