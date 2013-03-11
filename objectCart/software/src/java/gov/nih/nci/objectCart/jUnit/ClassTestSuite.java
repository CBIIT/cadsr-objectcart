package gov.nih.nci.objectCart.jUnit;

import gov.nih.nci.objectCart.jUnit.applicationService.impl.ObjectCartServiceImplTest;

import junit.framework.Test;
import junit.framework.TestSuite;

public class ClassTestSuite {

	public static Test suite() {

        TestSuite suite = new TestSuite();
  
        suite.addTestSuite(ObjectCartServiceImplTest.class);

        return suite;
    }
}
