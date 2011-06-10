package org.spring.test;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openliberty.openaz.azapi.pep.PepRequest;
import org.openliberty.openaz.azapi.pep.PepRequestFactory;
import org.openliberty.openaz.azapi.pep.PepResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * This is an example that shows how to use Spring to configure a PEP
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class SpringPEPTest extends TestCase {

	@Autowired
	private PepRequestFactory pep;

	@Test
	public void testSpringPEP() throws Exception {

		PepRequest req = pep.newPepRequest("josh", "read", "foo");

		PepResponse resp = req.decide();

		System.out.println(resp.allowed());

		assertTrue(resp.allowed());

	}

}