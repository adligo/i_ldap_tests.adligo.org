package org.adligo.i.ldap_tests;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.adligo.i.adi.shared.InvokerNames;
import org.adligo.i.adig.shared.GRegistry;
import org.adligo.i.adig_tests.shared.MockGClock;
import org.adligo.i.ldap.ReadWriteLdapConnection;
import org.adligo.i.ldap.models.DomainAttributes;
import org.adligo.i.ldap.models.I_LdapEntry;
import org.adligo.i.ldap.models.LargeFileAttributes;
import org.adligo.i.ldap.models.LargeFileChunkAttributes;
import org.adligo.i.ldap.models.LargeFileCreationToken;
import org.adligo.i.ldap.models.LdapEntryMutant;
import org.adligo.i.ldap.models.TopAttributes;
import org.adligo.i.log.shared.Log;
import org.adligo.i.log.shared.LogFactory;
import org.adligo.i.pool.Pool;
import org.adligo.tests.ATest;
import org.apache.directory.api.ldap.model.schema.registries.Schema;
import org.apache.directory.api.ldap.schemamanager.impl.DefaultSchemaManager;


public class LdapConnectionsTest extends ATest {
	private static final Log log = LogFactory.getLog(LdapConnectionsTest.class);
	private static int tests = 3;
	private static int which_test = 0;
	ReadWriteLdapConnection ldapCon;
    
	static {
		try {
			GRegistry.addOrReplaceInvoker(InvokerNames.CLOCK,  MockGClock.INSTANCE);
			List<Schema> schemaFiles = new ArrayList<Schema>();
			
			
			DefaultSchemaManager dsm = InMemoryApacheDs.createDefaultSchemaManager();
			OpenDsToApacheDsSchemaLoader loader = new OpenDsToApacheDsSchemaLoader();
			Schema schema = loader.loadFromClasspath("/org/adligo/i/ldap/schemas/adligo_large_file.ldif", Pool.class, dsm);
			schemaFiles.add(schema);
			InMemoryApacheDs.addSchemas(schemaFiles, dsm);
			InMemoryApacheDs.startApacheDs(dsm);
		} catch (Exception x) {
			log.error(x.getMessage(), x);
		}
	}
	
	public LdapConnectionsTest() {}
	
	
	@Override
	public void setUp() throws Exception {
		ldapCon = MockLdapPool.POOL.getConnection();
	}
	@Override
	public void tearDown() throws Exception {
		ldapCon.returnToPool();
		if (which_test == tests) {
			InMemoryApacheDs.stopApacheDs();
		} else {
			tests++;
		}
	}
	
	public void testGet() throws Exception {
			
	      I_LdapEntry entry = ldapCon.get(InMemoryApacheDs.BASE_TEST_DN);

	      assertNotNull(InMemoryApacheDs.BASE_TEST_DN);
	      assertEquals(InMemoryApacheDs.BASE_TEST_DN, entry.getDistinguishedName());
	      assertEquals("test", entry.getAttribute(DomainAttributes.DOMAIN_COMPONENT));
	      List<String> attribs = entry.getStringAttributes(DomainAttributes.OBJECT_CLASS);
	      assertTrue(attribs.contains("top"));
	      assertTrue(attribs.contains("domain"));
	}
	
	public void testCreate() throws Exception {
	      String dn = "dc=testCreate," + InMemoryApacheDs.BASE_TEST_DN;
	      
	      LdapEntryMutant lem = new LdapEntryMutant();
	      lem.setDistinguishedName(dn);
	      lem.setAttribute(DomainAttributes.DOMAIN_COMPONENT, "testCreate");
	      lem.setAttribute(DomainAttributes.OBJECT_CLASS, "domain");
	      ldapCon.create(lem);
	      
	      I_LdapEntry entry = ldapCon.get(dn);

	      assertNotNull(InMemoryApacheDs.BASE_TEST_DN);
	      assertEquals(dn, entry.getDistinguishedName());
	      assertEquals("testCreate", entry.getAttribute(DomainAttributes.DOMAIN_COMPONENT));
	      List<String> attribs = entry.getStringAttributes(DomainAttributes.OBJECT_CLASS);
	      assertTrue(attribs.contains("top"));
	      assertTrue(attribs.contains("domain"));
	      
	}
	
	public void testCreateAndReadLargeFile() throws Exception {
	      InputStream in = getClass().getResourceAsStream("/org/adligo/i/ldap_tests/test.file");
	      
	      MockGClock.INSTANCE.setTime(111);
	      LargeFileCreationToken token = new LargeFileCreationToken();
	      token.setBaseDn(InMemoryApacheDs.BASE_TEST_DN);
	      token.setFileName("test.file");
	      token.setContentStream(in);
	      token.setSize(37);
	      token.setServerCheckedOn("inMemoryServer");
	     assertTrue(ldapCon.createLargeFile(token));

	      I_LdapEntry largeFile = ldapCon.get("fn=test.file," + InMemoryApacheDs.BASE_TEST_DN);
	      assertNotNull(largeFile);
	      
	      assertEquals( "fn=test.file," + InMemoryApacheDs.BASE_TEST_DN, largeFile.getDistinguishedName());
	      assertEquals("test.file", largeFile.getAttribute(LargeFileAttributes.FILE_NAME));
	      assertNull(largeFile.getLongAttribute(LargeFileAttributes.DELETING));
	      assertEquals(Boolean.FALSE, largeFile.getBooleanAttribute(LargeFileAttributes.WRITING));
	      assertEquals("inMemoryServer", largeFile.getStringAttribute(LargeFileAttributes.CHECKED_ON_SERVER));
	      assertEquals(new Long(37), largeFile.getLongAttribute(LargeFileAttributes.SIZE));
	      List<String> classes = largeFile.getStringAttributes(LargeFileAttributes.OBJECT_CLASS);
	      assertNotNull(classes);
	      assertTrue( classes.contains(TopAttributes.OBJECT_CLASS_NAME));
	      assertTrue( classes.contains(LargeFileAttributes.OBJECT_CLASS_NAME));

	      I_LdapEntry chunkOne = ldapCon.get("nbr=1,fn=test.file," + InMemoryApacheDs.BASE_TEST_DN);
	      assertNotNull(chunkOne);
	      
	      assertEquals( "nbr=1,fn=test.file," + InMemoryApacheDs.BASE_TEST_DN, chunkOne.getDistinguishedName());
	      assertEquals(new Integer(1), chunkOne.getIntegerAttribute(LargeFileChunkAttributes.SEQUENCED_NUMBER));
	      assertEquals("1234567890", new String((byte []) chunkOne.getAttribute(LargeFileChunkAttributes.BINARY), "ASCII"));
	      assertEquals(new Long(10), chunkOne.getLongAttribute(LargeFileChunkAttributes.SIZE));
		     
	      classes = chunkOne.getStringAttributes(LargeFileAttributes.OBJECT_CLASS);
	      assertNotNull(classes);
	      assertTrue( classes.contains(TopAttributes.OBJECT_CLASS_NAME));
	      assertTrue( classes.contains(LargeFileChunkAttributes.OBJECT_CLASS_NAME));
	      
	      I_LdapEntry chunkTwo = ldapCon.get("nbr=2,fn=test.file," + InMemoryApacheDs.BASE_TEST_DN);
	      assertNotNull(chunkTwo);
	      
	      assertEquals( "nbr=2,fn=test.file," + InMemoryApacheDs.BASE_TEST_DN, chunkTwo.getDistinguishedName());
	      assertEquals(new Integer(2), chunkTwo.getIntegerAttribute(LargeFileChunkAttributes.SEQUENCED_NUMBER));
	      assertEquals("qwertyuiop", new String((byte []) chunkTwo.getAttribute(LargeFileChunkAttributes.BINARY), "ASCII"));
	      assertEquals(new Long(10), chunkTwo.getLongAttribute(LargeFileChunkAttributes.SIZE));
		     
	      classes = chunkTwo.getStringAttributes(LargeFileAttributes.OBJECT_CLASS);
	      assertNotNull(classes);
	      assertTrue( classes.contains(TopAttributes.OBJECT_CLASS_NAME));
	      assertTrue( classes.contains(LargeFileChunkAttributes.OBJECT_CLASS_NAME));
	      
	      I_LdapEntry chunkThree = ldapCon.get("nbr=3,fn=test.file," + InMemoryApacheDs.BASE_TEST_DN);
	      assertNotNull(chunkThree);
	      
	      assertEquals( "nbr=3,fn=test.file," + InMemoryApacheDs.BASE_TEST_DN, chunkThree.getDistinguishedName());
	      assertEquals(new Integer(3), chunkThree.getIntegerAttribute(LargeFileChunkAttributes.SEQUENCED_NUMBER));
	      assertEquals("asdfghjklz", new String((byte []) chunkThree.getAttribute(LargeFileChunkAttributes.BINARY), "ASCII"));
	      assertEquals(new Long(10), chunkThree.getLongAttribute(LargeFileChunkAttributes.SIZE));
		     
	      classes = chunkThree.getStringAttributes(LargeFileAttributes.OBJECT_CLASS);
	      assertNotNull(classes);
	      assertTrue( classes.contains(TopAttributes.OBJECT_CLASS_NAME));
	      assertTrue( classes.contains(LargeFileChunkAttributes.OBJECT_CLASS_NAME));
	      
	      I_LdapEntry chunkFour = ldapCon.get("nbr=4,fn=test.file," + InMemoryApacheDs.BASE_TEST_DN);
	      assertNotNull(chunkFour);
	      
	      assertEquals( "nbr=4,fn=test.file," + InMemoryApacheDs.BASE_TEST_DN, chunkFour.getDistinguishedName());
	      assertEquals(new Integer(4), chunkFour.getIntegerAttribute(LargeFileChunkAttributes.SEQUENCED_NUMBER));
	      assertEquals("xcvbnm ", new String((byte []) chunkFour.getAttribute(LargeFileChunkAttributes.BINARY), "ASCII"));
	      assertEquals(new Long(7), chunkFour.getLongAttribute(LargeFileChunkAttributes.SIZE));
		     
	      classes = chunkFour.getStringAttributes(LargeFileAttributes.OBJECT_CLASS);
	      assertNotNull(classes);
	      assertTrue( classes.contains(TopAttributes.OBJECT_CLASS_NAME));
	      assertTrue( classes.contains(LargeFileChunkAttributes.OBJECT_CLASS_NAME));
	      
	      ByteArrayOutputStream baos = new ByteArrayOutputStream(37);
	      ldapCon.getLargeFile("fn=test.file," + InMemoryApacheDs.BASE_TEST_DN, "inMemoryServer" , baos);
	      byte [] bytes = baos.toByteArray();
	      assertEquals("1234567890qwertyuiopasdfghjklzxcvbnm ", new String(bytes, "ASCII"));
	    		  
	}
}
