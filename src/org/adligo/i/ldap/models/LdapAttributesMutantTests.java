package org.adligo.i.ldap.models;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import org.adligo.i.ldap.models.LdapAttribute;
import org.adligo.i.ldap.models.LdapAttributesMutant;
import org.adligo.tests.ATest;

public class LdapAttributesMutantTests extends ATest {
	LdapAttributesMutant mutant;
	
	public void setUp() {
		mutant = new LdapAttributesMutant();
	}
	
	public void testString() {
		LdapAttribute attrib = new LdapAttribute("string");
		mutant.setAttribute(attrib, "sVal");
		assertEquals("sVal",mutant.getAttribute(attrib));
		assertEquals("sVal",mutant.getStringAttribute(attrib));
		
		List<String> vals = mutant.getStringAttributes(attrib);
		assertNotNull(vals);
		assertTrue(vals.contains("sVal"));
		
		List<Object> oVals = mutant.getAttributes(attrib);
		assertNotNull(oVals);
		assertTrue(oVals.contains("sVal"));
		
		vals.add("sOV");
		mutant.setAttributes(attrib, vals);
		vals = mutant.getStringAttributes(attrib);
		assertNotNull(vals);
		assertTrue(vals.contains("sVal"));
		assertTrue(vals.contains("sOV"));
	}
	
	public void testByteArray() {
		LdapAttribute attrib = new LdapAttribute("string");
		byte [] bytes = "sVal".getBytes();
		mutant.setAttribute(attrib, bytes);
		assertEquals(bytes,mutant.getAttribute(attrib));
		
		
		List<Object> oVals = mutant.getAttributes(attrib);
		assertNotNull(oVals);
		assertTrue(oVals.contains(bytes));
		
		byte [] oBytes = "sOV".getBytes();
		oVals.add(oBytes);
		mutant.setAttributes(attrib, oVals);
		List<Object> vals = mutant.getAttributes(attrib);
		assertNotNull(vals);
		assertTrue(vals.contains(bytes));
		assertTrue(vals.contains(oBytes));
	}
	
	public void testBigDecimal() {
		LdapAttribute attrib = new LdapAttribute("bd");
		BigDecimal d = new BigDecimal(123);
		mutant.setAttribute(attrib, d);
		assertEquals(d,mutant.getAttribute(attrib));
		assertEquals(d,mutant.getBigDecimalAttribute(attrib));
		
		List<BigDecimal> vals = mutant.getBigDecimalAttributes(attrib);
		assertNotNull(vals);
		assertTrue(vals.contains(d));
		
		List<Object> oVals = mutant.getAttributes(attrib);
		assertNotNull(oVals);
		assertTrue(oVals.contains(d));
		
		BigDecimal db = new BigDecimal(1.1);
		vals.add(db);
		mutant.setAttributes(attrib, vals);
		vals = mutant.getBigDecimalAttributes(attrib);
		assertNotNull(vals);
		assertTrue(vals.contains(d));
		assertTrue(vals.contains(db));
	}
	
	public void testBigInteger() {
		LdapAttribute attrib = new LdapAttribute("bi");
		BigInteger d = BigInteger.valueOf(3333L);
		mutant.setAttribute(attrib, d);
		assertEquals(d,mutant.getAttribute(attrib));
		assertEquals(d,mutant.getBigIntegerAttribute(attrib));
		
		List<BigInteger> vals = mutant.getBigIntegerAttributes(attrib);
		assertNotNull(vals);
		assertTrue(vals.contains(d));
		
		List<Object> oVals = mutant.getAttributes(attrib);
		assertNotNull(oVals);
		assertTrue(oVals.contains(d));
		
		BigInteger db = BigInteger.valueOf(11L);
		vals.add(db);
		mutant.setAttributes(attrib, vals);
		vals = mutant.getBigIntegerAttributes(attrib);
		assertNotNull(vals);
		assertTrue(vals.contains(d));
		assertTrue(vals.contains(db));
	}
	
	public void testBoolean() {
		LdapAttribute attrib = new LdapAttribute("bool");
		BigInteger d = BigInteger.valueOf(3333L);
		mutant.setAttribute(attrib, d);
		assertEquals(d,mutant.getAttribute(attrib));
		assertEquals(d,mutant.getBigIntegerAttribute(attrib));
		
		List<BigInteger> vals = mutant.getBigIntegerAttributes(attrib);
		assertNotNull(vals);
		assertTrue(vals.contains(d));
		
		List<Object> oVals = mutant.getAttributes(attrib);
		assertNotNull(oVals);
		assertTrue(oVals.contains(d));
	}
	
	public void testDate() {
		LdapAttribute attrib = new LdapAttribute("bi");
		Date d = new Date(3333L);
		mutant.setAttribute(attrib, d);
		assertEquals(d,mutant.getAttribute(attrib));
		assertEquals(d,mutant.getDateAttribute(attrib));
		
		List<Date> vals = mutant.getDateAttributes(attrib);
		assertNotNull(vals);
		assertTrue(vals.contains(d));
		
		List<Object> oVals = mutant.getAttributes(attrib);
		assertNotNull(oVals);
		assertTrue(oVals.contains(d));
		
		Date db = new Date(11L);
		vals.add(db);
		mutant.setAttributes(attrib, vals);
		vals = mutant.getDateAttributes(attrib);
		assertNotNull(vals);
		assertTrue(vals.contains(d));
		assertTrue(vals.contains(db));
	}
	
	public void testDouble() {
		LdapAttribute attrib = new LdapAttribute("bi");
		Double d = new Double(33.33);
		mutant.setAttribute(attrib, d);
		assertEquals(d,mutant.getAttribute(attrib));
		assertEquals(d,mutant.getDoubleAttribute(attrib));
		
		List<Double> vals = mutant.getDoubleAttributes(attrib);
		assertNotNull(vals);
		assertTrue(vals.contains(d));
		
		List<Object> oVals = mutant.getAttributes(attrib);
		assertNotNull(oVals);
		assertTrue(oVals.contains(d));
		
		Double db = new Double(1.1);
		vals.add(db);
		mutant.setAttributes(attrib, vals);
		vals = mutant.getDoubleAttributes(attrib);
		assertNotNull(vals);
		assertTrue(vals.contains(d));
		assertTrue(vals.contains(db));
	}
	
	public void testFloat() {
		LdapAttribute attrib = new LdapAttribute("bi");
		Float d = new Float(33.33);
		mutant.setAttribute(attrib, d);
		assertEquals(d,mutant.getAttribute(attrib));
		assertEquals(d,mutant.getFloatAttribute(attrib));
		
		List<Float> vals = mutant.getFloatAttributes(attrib);
		assertNotNull(vals);
		assertTrue(vals.contains(d));
		
		List<Object> oVals = mutant.getAttributes(attrib);
		assertNotNull(oVals);
		assertTrue(oVals.contains(d));
		
		Float db = new Float(1.1);
		vals.add(db);
		mutant.setAttributes(attrib, vals);
		vals = mutant.getFloatAttributes(attrib);
		assertNotNull(vals);
		assertTrue(vals.contains(d));
		assertTrue(vals.contains(db));
	}
	
	public void testInteger() {
		LdapAttribute attrib = new LdapAttribute("bi");
		Integer d = new Integer(3333);
		mutant.setAttribute(attrib, d);
		assertEquals(d,mutant.getAttribute(attrib));
		assertEquals(d,mutant.getIntegerAttribute(attrib));
		
		List<Integer> vals = mutant.getIntegerAttributes(attrib);
		assertNotNull(vals);
		assertTrue(vals.contains(d));
		
		List<Object> oVals = mutant.getAttributes(attrib);
		assertNotNull(oVals);
		assertTrue(oVals.contains(d));
		
		Integer db = new Integer(11);
		vals.add(db);
		mutant.setAttributes(attrib, vals);
		vals = mutant.getIntegerAttributes(attrib);
		assertNotNull(vals);
		assertTrue(vals.contains(d));
		assertTrue(vals.contains(db));
	}
	
	public void testLong() {
		LdapAttribute attrib = new LdapAttribute("bi");
		Long d = new Long(3333);
		mutant.setAttribute(attrib, d);
		assertEquals(d,mutant.getAttribute(attrib));
		assertEquals(d,mutant.getLongAttribute(attrib));
		
		List<Long> vals = mutant.getLongAttributes(attrib);
		assertNotNull(vals);
		assertTrue(vals.contains(d));
		
		List<Object> oVals = mutant.getAttributes(attrib);
		assertNotNull(oVals);
		assertTrue(oVals.contains(d));
		
		Long db = new Long(11);
		vals.add(db);
		mutant.setAttributes(attrib, vals);
		vals = mutant.getLongAttributes(attrib);
		assertNotNull(vals);
		assertTrue(vals.contains(d));
		assertTrue(vals.contains(db));
	}
	
	public void testShort() {
		LdapAttribute attrib = new LdapAttribute("bi");
		Short d = new Short((short) 33);
		mutant.setAttribute(attrib, d);
		assertEquals(d,mutant.getAttribute(attrib));
		assertEquals(d,mutant.getShortAttribute(attrib));
		
		List<Short> vals = mutant.getShortAttributes(attrib);
		assertNotNull(vals);
		assertTrue(vals.contains(d));
		
		List<Object> oVals = mutant.getAttributes(attrib);
		assertNotNull(oVals);
		assertTrue(oVals.contains(d));
		
		Short db = new Short((short) 11);
		vals.add(db);
		mutant.setAttributes(attrib, vals);
		vals = mutant.getShortAttributes(attrib);
		assertNotNull(vals);
		assertTrue(vals.contains(d));
		assertTrue(vals.contains(db));
	}
}
