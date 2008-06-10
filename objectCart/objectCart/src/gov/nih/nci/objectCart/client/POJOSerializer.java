package gov.nih.nci.objectCart.client;

import java.io.StringReader;
import java.io.StringWriter;

import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.ValidationException;

import gov.nih.nci.objectCart.domain.CartObject;

public class POJOSerializer implements Serializer {

	private POJOSerializer() {}

	/**
	 * SingletonHolder is loaded on the first execution of Singleton.getInstance() 
	 * or the first access to SingletonHolder.instance , not before.
	 */
	private static class SingletonHolder { 
		private final static POJOSerializer instance = new POJOSerializer();
	}

	public static POJOSerializer getInstance() {
		return SingletonHolder.instance;
	}

	public CartObject serializeObject(Class cl, String displayName, String nativeId, Object ob) throws ObjectCartException {
		CartObject cob = null;
		try {
			cob = marshalObject(cl, displayName, nativeId, ob);
			return cob;
		} catch (MarshalException me) {
			throw new ObjectCartException("Marshaling Error while trying to convert object to CartObject", me);			
		} catch (ValidationException ve) {
			throw new ObjectCartException("Validation Error while trying to convert object to CartObject", ve);	
		}
	}
	
	public Object deserializeObject(Class cl, CartObject cob) throws ObjectCartException {
		Object pob = null;
		try {
			pob = unmarshalObject(cl, cob);
			return pob;

		} catch (MarshalException me) {
			throw new ObjectCartException("Marshaling Error while trying to convert CartObject to " + cl.getName(), me);			
		} catch (ValidationException ve) {
			throw new ObjectCartException("Validation Error while trying to convert CartObject to " + cl.getName(), ve);	
		}
	}
	
	private CartObject marshalObject(Class cl, String objectDisplayName, String nativeId, Object pOb) throws MarshalException, ValidationException{
		
		CartObject ob = new CartObject();
		ob.setType(":Serialized:"+pOb.getClass());
		ob.setDisplayText(objectDisplayName);
		ob.setNativeId(nativeId);
		StringWriter writer = new StringWriter();
		Marshaller.marshal(pOb, writer);
		ob.setData(writer.toString());
		return ob;
	}
	
	private Object unmarshalObject(Class cl, CartObject ob) throws MarshalException, ValidationException {		
		Object pOb = new Object();
		StringReader reader = new StringReader(ob.getData());
		pOb = Unmarshaller.unmarshal(cl, reader);
		return pOb;
	}
}
