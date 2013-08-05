/*L
 * Copyright Ekagra Software Technologies Ltd, SAIC-F
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/cadsr-objectcart/LICENSE.txt for details.
 */

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
