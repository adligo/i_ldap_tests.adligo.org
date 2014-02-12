package org.adligo.i.ldap.converters;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import org.adligo.i.ldap.converters.BigDecimalAttributeConverter;
import org.adligo.i.ldap.converters.BigIntegerAttributeConverter;
import org.adligo.i.ldap.converters.BooleanAttributeConverter;
import org.adligo.i.ldap.converters.DateAttributeConverter;
import org.adligo.i.ldap.converters.DoubleAttributeConverter;
import org.adligo.i.ldap.converters.FloatAttributeConverter;
import org.adligo.i.ldap.converters.IntegerAttributeConverter;
import org.adligo.i.ldap.converters.LongAttributeConverter;
import org.adligo.i.ldap.converters.ShortAttributeConverter;
import org.adligo.i.util.shared.DateTime;
import org.adligo.tests.ATest;

public class ConverterTests extends ATest {

	public void testBigDecimalToLdap() {
		BigDecimalAttributeConverter converter = new BigDecimalAttributeConverter();
		String val = converter.toLdap(new BigDecimal(Integer.MAX_VALUE));
		assertEquals("2147483647", val);
		
		val = converter.toLdap(new BigDecimal(Double.MAX_VALUE));
		assertEquals("179769313486231570814527423731704356798070567525844996598917476803157260780028538760589558632766878171540458953514382464234321326889464182768467546703537516986049910576551282076245490090389328944075868508455133942304583236903222948165808559332123348274797826204144723168738177180919299881250404026184124858368", val);
		
		val = converter.toLdap(new BigDecimal(Double.MIN_NORMAL));
		assertEquals("0.00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000002225073858507201383090232717332404064219215980462331830553327416887204434813918195854283159012511020564067339731035811005152434161553460108856012385377718821130777993532002330479610147442583636071921565046942503734208375250806650616658158948720491179968591639648500635908770118304874799780887753749949451580451605050915399856582470818645113537935804992115981085766051992433352114352390148795699609591288891602992641511063466313393663477586513029371762047325631781485664350872122828637642044846811407613911477062801689853244110024161447421618567166150540154285084716752901903161322778896729707373123334086988983175067838846926092773977972858659654941091369095406136467568702398678315290680984617210924625396728515625", val);
		
		val = converter.toLdap(null);
		assertEquals("", val);
	}
	
	public void testBigDecimalToJava() {
		BigDecimalAttributeConverter converter = new BigDecimalAttributeConverter();
		BigDecimal val = converter.toJava("2147483647");
		assertEquals(new BigDecimal(Integer.MAX_VALUE), val);
		
		val = converter.toJava("179769313486231570814527423731704356798070567525844996598917476803157260780028538760589558632766878171540458953514382464234321326889464182768467546703537516986049910576551282076245490090389328944075868508455133942304583236903222948165808559332123348274797826204144723168738177180919299881250404026184124858368");
		assertEquals(new BigDecimal(Double.MAX_VALUE), val);
		
		val = converter.toJava("0.00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000002225073858507201383090232717332404064219215980462331830553327416887204434813918195854283159012511020564067339731035811005152434161553460108856012385377718821130777993532002330479610147442583636071921565046942503734208375250806650616658158948720491179968591639648500635908770118304874799780887753749949451580451605050915399856582470818645113537935804992115981085766051992433352114352390148795699609591288891602992641511063466313393663477586513029371762047325631781485664350872122828637642044846811407613911477062801689853244110024161447421618567166150540154285084716752901903161322778896729707373123334086988983175067838846926092773977972858659654941091369095406136467568702398678315290680984617210924625396728515625");
		assertEquals(new BigDecimal(Double.MIN_NORMAL), val);
		
		val = converter.toJava(null);
		assertNull(val);
		
		assertEquals(BigDecimal.class, converter.getJavaClass());
	}
	
	public void testBigIntegerToLdap() {
		BigIntegerAttributeConverter converter = new BigIntegerAttributeConverter();
		String val = converter.toLdap(new BigInteger("" + Integer.MAX_VALUE));
		assertEquals("2147483647", val);
		
		val = converter.toLdap(new BigInteger("" + Long.MAX_VALUE));
		assertEquals("9223372036854775807", val);
		
		val = converter.toLdap(null);
		assertEquals("", val);
	}
	
	public void testBigIntegerToJava() {
		BigIntegerAttributeConverter converter = new BigIntegerAttributeConverter();
		BigInteger val = converter.toJava("2147483647");
		assertEquals(new BigInteger("" + Integer.MAX_VALUE), val);
		
		val = converter.toJava("9223372036854775807");
		assertEquals(new BigInteger("" + Long.MAX_VALUE), val);
		
		val = converter.toJava(null);
		assertNull(val);
		
		assertEquals(BigInteger.class, converter.getJavaClass());
	}
	
	public void testBooleanToLdap() {
		BooleanAttributeConverter converter = new BooleanAttributeConverter();
		String val = converter.toLdap(true);
		assertEquals("True", val);
		
		val = converter.toLdap(false);
		assertEquals("False", val);
		
		val = converter.toLdap(null);
		assertEquals("False", val);
	}
	
	public void testBooleanToJava() {
		BooleanAttributeConverter converter = new BooleanAttributeConverter();
		Boolean val = converter.toJava("True");
		assertEquals(Boolean.TRUE, val);
		
		val = converter.toJava("False");
		assertEquals(Boolean.FALSE, val);
		
		val = converter.toJava(null);
		assertEquals(Boolean.FALSE, val);
		
		assertEquals(Boolean.class, converter.getJavaClass());
	}
	
	public void testDateToLdap() {
		DateAttributeConverter converter = new DateAttributeConverter();
		String val = converter.toLdap(new Date(0));
		assertEquals("12/31/1969 06:00 PM 000", val);
		
		val = converter.toLdap(new Date(1));
		assertEquals("12/31/1969 06:00 PM 001", val);
		
		val = converter.toLdap(null);
		assertEquals("", val);
	}
	
	public void testDateToJava() {
		DateAttributeConverter converter = new DateAttributeConverter();
		DateTime dt = new DateTime(0);
		String dtVal = dt.toString();
		Date val = converter.toJava(dtVal);
		assertEquals(new Date(0), val);
		
		dt = new DateTime(1);
		dtVal = dt.toString();
		val = converter.toJava(dtVal);
		assertEquals(new Date(1), val);
		
		val = converter.toJava(null);
		assertNull(val);
		
		assertEquals(Date.class, converter.getJavaClass());
	}
	
	
	
	public void testDoubleToLdap() {
		DoubleAttributeConverter converter = new DoubleAttributeConverter();
		String val = converter.toLdap(new Double(0));
		assertEquals("0", val);
		
		val = converter.toLdap(Double.MIN_NORMAL);
		assertEquals("0.00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000002225073858507201383090232717332404064219215980462331830553327416887204434813918195854283159012511020564067339731035811005152434161553460108856012385377718821130777993532002330479610147442583636071921565046942503734208375250806650616658158948720491179968591639648500635908770118304874799780887753749949451580451605050915399856582470818645113537935804992115981085766051992433352114352390148795699609591288891602992641511063466313393663477586513029371762047325631781485664350872122828637642044846811407613911477062801689853244110024161447421618567166150540154285084716752901903161322778896729707373123334086988983175067838846926092773977972858659654941091369095406136467568702398678315290680984617210924625396728515625", val);
		
		val = converter.toLdap(null);
		assertEquals("", val);
	}
	
	public void testDoubleToJava() {
		DoubleAttributeConverter converter = new DoubleAttributeConverter();
		Double val = converter.toJava("0");
		assertEquals(new Double(0), val);
		
		val = converter.toJava("" + Double.MAX_VALUE);
		assertEquals(Double.MAX_VALUE, val);
		
		val = converter.toJava(null);
		assertNull(val);
		
		assertEquals(Double.class, converter.getJavaClass());
	}
	
	public void testFloatToLdap() {
		FloatAttributeConverter converter = new FloatAttributeConverter();
		String val = converter.toLdap(new Float(0));
		assertEquals("0", val);
		
		val = converter.toLdap(Float.MIN_NORMAL);
		assertEquals("0.000000000000000000000000000000000000011754943508222875079687365372222456778186655567720875215087517062784172594547271728515625", val);
		
		val = converter.toLdap(null);
		assertEquals("", val);
	}
	
	public void testFloatToJava() {
		FloatAttributeConverter converter = new FloatAttributeConverter();
		Float val = converter.toJava("0");
		assertEquals(new Float(0), val);
		
		val = converter.toJava("" + Float.MAX_VALUE);
		assertEquals(Float.MAX_VALUE, val);
		
		val = converter.toJava(null);
		assertNull(val);
		
		assertEquals(Float.class, converter.getJavaClass());
	}
	
	
	public void testIntegerToLdap() {
		IntegerAttributeConverter converter = new IntegerAttributeConverter();
		String val = converter.toLdap(new Integer(0));
		assertEquals("0", val);
		
		val = converter.toLdap(Integer.MIN_VALUE);
		assertEquals("-2147483648", val);
		
		val = converter.toLdap(null);
		assertEquals("", val);
	}
	
	public void testIntegerToJava() {
		IntegerAttributeConverter converter = new IntegerAttributeConverter();
		Integer val = converter.toJava("0");
		assertEquals(new Integer(0), val);
		
		val = converter.toJava("" + Integer.MAX_VALUE);
		assertEquals(new Integer(Integer.MAX_VALUE), val);
		
		val = converter.toJava(null);
		assertNull(val);
		
		assertEquals(Integer.class, converter.getJavaClass());
	}
	
	public void testLongToLdap() {
		LongAttributeConverter converter = new LongAttributeConverter();
		String val = converter.toLdap(new Long(0));
		assertEquals("0", val);
		
		val = converter.toLdap(Long.MIN_VALUE);
		assertEquals("-9223372036854775808", val);
		
		val = converter.toLdap(null);
		assertEquals("", val);
	}
	
	public void testLongToJava() {
		LongAttributeConverter converter = new LongAttributeConverter();
		Long val = converter.toJava("0");
		assertEquals(new Long(0), val);
		
		val = converter.toJava("" + Long.MAX_VALUE);
		assertEquals(new Long(Long.MAX_VALUE), val);
		
		val = converter.toJava(null);
		assertNull(val);
		
		assertEquals(Long.class, converter.getJavaClass());
	}
	
	
	public void testShortToLdap() {
		ShortAttributeConverter converter = new ShortAttributeConverter();
		String val = converter.toLdap(new Short((short) 0));
		assertEquals("0", val);
		
		val = converter.toLdap(Short.MIN_VALUE);
		assertEquals("-32768", val);
		
		val = converter.toLdap(null);
		assertEquals("", val);
	}
	
	public void testShortToJava() {
		ShortAttributeConverter converter = new ShortAttributeConverter();
		Short val = converter.toJava("0");
		assertEquals(new Short((short) 0), val);
		
		val = converter.toJava("" + Short.MAX_VALUE);
		assertEquals(new Short(Short.MAX_VALUE), val);
		
		val = converter.toJava(null);
		assertNull(val);
		
		assertEquals(Short.class, converter.getJavaClass());
	}
}
