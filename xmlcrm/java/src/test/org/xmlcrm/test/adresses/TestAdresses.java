package org.xmlcrm.test.adresses;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xmlcrm.app.data.user.Statemanagement;
import org.xmlcrm.app.data.user.Addressmanagement;

public class TestAdresses extends TestCase {
	
	private static final Log log = LogFactory.getLog(TestAdresses.class);
	
	public TestAdresses (String testname) {
		super(testname);
	}
	
	public void testAddAdress(){
		
		Long states_id = Statemanagement.getInstance().addState("Deutschland");
		
		System.out.println("states_id "+states_id);
		log.error("states_id: "+states_id);
		
		long adress_id = Addressmanagement.getInstance().saveAddress("street", "zip", "town", states_id, "additionalname", "comment", "fax");
		
		System.out.println("adress_id "+adress_id);
		log.error("adress_id: "+adress_id);
		
	}

}
