package gov.nih.nci.objectCart.jUnit.applicationService.impl;

import org.junit.After;
import org.junit.Before;

import gov.nih.nci.objectCart.client.ObjectCartClient;
import gov.nih.nci.objectCart.client.POJOSerializer;
import gov.nih.nci.objectCart.domain.Cart;
import gov.nih.nci.objectCart.domain.CartObject;
import junit.framework.TestCase;

import java.sql.Timestamp;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class SerializerTest extends TestCase{

	public ObjectCartClient cartManager = null;
	@Before
	public void setUp() throws Exception {
		super.setUp();

		try {
			cartManager = new ObjectCartClient("CDE Cart Classification: Test Classification");
		} catch (Exception e) {
			fail("Exception creating cart manager");
		}		
	}

	@After
	public void tearDown() throws Exception {	
	}
	
	
	public void test1()
	{

		String name = "testCreateCart Cart Name";
		String userId = "testCreateCart Cart User";

		String type = "CDE Cart type";
		String dispName = "This is my Name";
		String data = "Some data here";

		Cart cart = null;
		CartObject co = null;
		CartObject input = new CartObject();
		CartObject output = null;
		
		input.setDisplayText(dispName);
		input.setDateAdded(new Date());
		input.setId(234);
		input.setData(data);
		input.setType(type);
		input.setNativeId("Native id here");
		
		
		
		try {
			co = POJOSerializer.getInstance().serializeObject(input.getClass(), "displayName", input.getId().toString(), input);
		
			System.out.println(co.getData());
		} catch (Exception e){
			e.printStackTrace();
			fail(e.getMessage());
		}
		try {
			Object o = POJOSerializer.getInstance().deserializeObject(CartObject.class, co);
			if (o instanceof CartObject) 
				output = (CartObject) o;
			
			
			assertEquals(output.getDisplayText(), input.getDisplayText());
			assertEquals(output.getId(),input.getId());
			assertEquals(output.getData(),input.getData());
			assertEquals(output.getType(), input.getType());
			
		} catch (Exception e){
			e.printStackTrace();
			fail(e.getMessage());
		}
		
		
//		CDECartItemImpl citem = new CDECartItemImpl();
//		CDECartItemImpl coutput = null;
//		AdminComponentImpl aci = new AdminComponentImpl();
//		
//		aci.setIdseq("idseq here");
//		aci.setPreferredName("preferred name");		
//		
//		citem.setCreatedBy("User");
//		citem.setId("12354");
//		citem.setPersistedInd(false);
//		citem.setType("Type here");	
//		citem.setItem(aci);
//		citem.setDeletedInd(false);
//		
//		
//		try {
//			co = POJOSerializer.getInstance().serializeObject(citem.getClass(), "displayName", citem.getId(), citem);
//		} catch (Exception e){
//			e.printStackTrace();
//			fail(e.getMessage());
//		}
//		try {
//			Object o = POJOSerializer.getInstance().deserializeObject(CDECartItemImpl.class, co);
//			if (o instanceof CDECartItemImpl) 
//				coutput = (CDECartItemImpl) o;
//			
//			
//			assertEquals(coutput.getCreatedBy(),citem.getCreatedBy());
//			assertEquals(coutput.getId(),citem.getId());
//			assertEquals(coutput.getDeletedInd(),citem.getDeletedInd());
//			assertEquals(coutput.getType(), citem.getType());
//			
//			AdminComponent aoutput = coutput.getItem();
//			assertTrue(aoutput instanceof AdminComponentImpl);
//			assertEquals(aoutput.getIdseq(), aci.getIdseq());
//			assertEquals(aoutput.getPreferredName(), aci.getPreferredName());
//			
//		} catch (Exception e){
//			e.printStackTrace();
//			fail(e.getMessage());
//		}
//		
		
//		try {	
//			cart = cartManager.createCart(userId,name);
//		} catch (Exception e) {
//			e.printStackTrace();
//			fail("Exception creating cart");
//		}
//		
//		try {	
//			cart = cartManager.storePOJO(cart, citem.getClass(), "Display Name here", citem.getId(), citem);
//		} catch (Exception e) {
//			e.printStackTrace();
//			fail("Exception creating cart");
//		}
//
//		Cart secondCart = null;
//
//
//		try {
//			secondCart = cartManager.retrieveCart(cart.getUserId(), cart.getName());
//		} catch (Exception e) {
//			e.printStackTrace();
//			fail("Exception retrieving cart");
//		}
		
	}	

	public interface DataElement extends AdminComponent{
		   public String getDeIdseq();
		   public void setDeIdseq(String pDeIdseq);
		   
		   public String getVdIdseq();
		   public void setVdIdseq(String pVdIdseq);

		   public String getDecIdseq();
		   public void setDecIdseq(String pDecIdseq);

		   public String getVdName();
		   public String getContextName();
		   public String getLongCDEName();
		   public String getCDEId();
		   public String getDecName();

		   public void setLongCDEName (String pLongCDEName);
		   public void setContextName(String pConteName);
		   public void setCDEId(String pCDEId);

		   public String getUsingContexts();
		   public void setUsingContexts(String usingContexts);

		   public List getClassifications();
		   public void setClassifications(List classifications);
		   
		   public List getOtherVersions();
		   public void setOtherVersions(List deList);   
		   
		   public Object clone() throws CloneNotSupportedException ;
		}
	
	public class AdminComponentImpl implements AdminComponent {
		 
		private String preferredName;
		private String idSeq;
		public String getPreferredName() {
			 return preferredName;
		 }

		  public void setPreferredName(String pPreferredName) {
			  preferredName = pPreferredName;
		  }
		  
		  public String getIdseq() { return idSeq; }

		  public void setIdseq(String idseq) { idSeq = idseq; }
	}
	
	public interface AdminComponent {
		  
		  public String getPreferredName();

		  public void setPreferredName(String pPreferredName);

		  public String getIdseq();

		  public void setIdseq(String idseq);
		}
		
		public class CDECartItemImpl implements Serializable {
		  protected String id;
		  protected String type;
		  protected Timestamp createdDate;
		  protected String createdBy;
		  protected DataElement cde;
		  protected AdminComponent ac;
		  protected boolean deletedInd;
		  protected boolean persistedInd;

		  public CDECartItemImpl() {
		  }

		  public String getId() {
		    return id;
		  }

		  public void setId(String id) {
		    this.id = id;
		  }

		  public String getType() {
		    return type;
		  }

		  public void setType(String type) {
		    this.type = type;
		  }

		  public Timestamp getCreatedDate() {
		    return createdDate;
		  }

		  public String getCreatedBy() {
		    return createdBy;
		  }

		  public void setCreatedBy(String user) {
		    createdBy = user;
		  }

		  public boolean equals(Object obj) {
		    if (((CDECartItemImpl)obj).getId().equals(id)) {
		      return true;
		    }
		    else {
		      return false;
		    }
		  }

		  public int hashCode() {
		    return 59878489;
		  }

		  public AdminComponent getItem() {
		    return ac;
		  }

		  public void setItem(AdminComponent ac) {
		    this.ac = ac;
		    id = ac.getIdseq();
		  }

		  public boolean getDeletedInd() {
		    return deletedInd;
		  }

		  public void setDeletedInd(boolean ind) {
		    deletedInd = ind;
		  }

		  public boolean getPersistedInd() {
		    return persistedInd;
		  }

		  public void setPersistedInd(boolean ind) {
		    persistedInd = ind;
		  }
		}
}
