package org.adligo.i.ldap_tests.models;

import org.adligo.i.ldap.models.CoreAttributes;
import org.adligo.tests.ATest;

public class LdapAttributeNameTests extends ATest {

	public void testEqualsHash() {
		assertEquals(CoreAttributes.ASSOCIATED_NAME.hashCode(), CoreAttributes.ASSOCIATED_NAME.hashCode());
		assertEquals(CoreAttributes.ASSOCIATED_NAME, CoreAttributes.ASSOCIATED_NAME);
		
		assertEquals(CoreAttributes.BUSINESS_CATEGORY.hashCode(), CoreAttributes.BUSINESS_CATEGORY.hashCode());
		assertEquals(CoreAttributes.BUSINESS_CATEGORY, CoreAttributes.BUSINESS_CATEGORY);
		

		assertEquals(CoreAttributes.DOMAIN_COMPONENT.hashCode(), CoreAttributes.DOMAIN_COMPONENT.hashCode());
		assertEquals(CoreAttributes.DOMAIN_COMPONENT, CoreAttributes.DOMAIN_COMPONENT);
	}
}
