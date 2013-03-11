package gov.nih.nci.objectCart.client;

import gov.nih.nci.objectCart.domain.CartObject;

public interface Serializer {

	public CartObject serializeObject(Class cl, String displayName, String nativeId, Object ob) throws ObjectCartException;
	
	public Object deserializeObject(Class cl, CartObject cob) throws ObjectCartException;
	
}
