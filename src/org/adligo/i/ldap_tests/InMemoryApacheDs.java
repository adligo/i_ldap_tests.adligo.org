package org.adligo.i.ldap_tests;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.api.ldap.model.schema.AttributeType;
import org.apache.directory.api.ldap.model.schema.ObjectClass;
import org.apache.directory.api.ldap.model.schema.SchemaObject;
import org.apache.directory.api.ldap.model.schema.SchemaObjectType;
import org.apache.directory.api.ldap.model.schema.SchemaObjectWrapper;
import org.apache.directory.api.ldap.model.schema.registries.AttributeTypeRegistry;
import org.apache.directory.api.ldap.model.schema.registries.NormalizerRegistry;
import org.apache.directory.api.ldap.model.schema.registries.ObjectClassRegistry;
import org.apache.directory.api.ldap.model.schema.registries.Registries;
import org.apache.directory.api.ldap.model.schema.registries.Schema;
import org.apache.directory.api.ldap.schemamanager.impl.DefaultSchemaManager;
import org.apache.directory.server.constants.ServerDNConstants;
import org.apache.directory.server.core.DefaultDirectoryService;
import org.apache.directory.server.core.api.CacheService;
import org.apache.directory.server.core.api.DirectoryService;
import org.apache.directory.server.core.api.InstanceLayout;
import org.apache.directory.server.core.api.schema.SchemaPartition;
import org.apache.directory.server.core.partition.impl.avl.AvlPartition;
import org.apache.directory.server.ldap.LdapServer;
import org.apache.directory.server.protocol.shared.transport.TcpTransport;
import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;

/**
 * using log4j which seemed to be used by apacheds in production
 * 
 * @author scott
 *
 * Note ATest extension just does the i_log/jse_util init for me
 */
public class InMemoryApacheDs {
		public static final String DC = "dc";
		private static final String TEST_DC = "test";
		public static final String BASE_TEST_DN = "dc=test";
		public static final int PORT = 11389;
		private static final Logger log = Logger.getLogger(InMemoryApacheDs.class);
	  private static DirectoryService directoryService;
	  private static LdapServer ldapServer;
	  private static AvlPartition testPartition;
	  private static InstanceLayout instanceLayout;
	  
	  public static DefaultSchemaManager createDefaultSchemaManager() throws Exception {


		    
		    directoryService = new DefaultDirectoryService();

		    File running = new File(".");
		    String path = running.getAbsolutePath();
		    path = path.substring(0, path.length() - 1) + "eldap_dir";
		    instanceLayout = new InstanceLayout(path);
		    directoryService.setInstanceLayout(instanceLayout);

		    DefaultSchemaManager schemaManager = new DefaultSchemaManager();
		    directoryService.setSchemaManager(schemaManager);
		    
		    List<Schema> schemas = schemaManager.getEnabled();
		    log.info("got " + schemas.size() + " schemas");
		   
		    for (Schema schema: schemas) {
		    	log.debug("got schema " + schema);
		    }


		    List<Throwable> errors = schemaManager.getErrors();

		    if (!errors.isEmpty())
		      throw new Exception("Schema load failed : " + errors);

		    AvlPartition systemPartition = new AvlPartition(schemaManager);
		    systemPartition.setId("system");
		    systemPartition.setSuffixDn(new Dn(ServerDNConstants.SYSTEM_DN));
		    directoryService.setSystemPartition(systemPartition);

		    
		    
		    return schemaManager;
	  }
	  
	  @BeforeClass
	  public static void startApacheDs(DefaultSchemaManager schemaManager) throws Exception {
	    
		  testPartition = new AvlPartition(schemaManager);
		    testPartition.setId(TEST_DC);
		    testPartition.setSuffixDn(new Dn(BASE_TEST_DN));
		    directoryService.addPartition(testPartition);
		    
		  schemaManager.loadAllEnabled();
		  AttributeTypeRegistry atr =  schemaManager.getAttributeTypeRegistry();
		  AttributeType at =  atr.lookup("1.3.6.1.4.1.33097.1.101");
		  System.out.println(at);
		  
	    directoryService.setShutdownHookEnabled(false);
	    directoryService.getChangeLog().setEnabled(false);

	    ldapServer = new LdapServer();
	    ldapServer.setTransports(new TcpTransport(PORT));
	    ldapServer.setDirectoryService(directoryService);

	    AvlPartition scheamPartition = new AvlPartition(schemaManager);
	    scheamPartition.setId("schema");
	    scheamPartition.setSchemaManager(schemaManager);
	    scheamPartition.setPartitionPath(new URI("schema"));
	   
	    SchemaPartition sp = new SchemaPartition(schemaManager);
	    sp.setWrappedPartition(scheamPartition);
	    directoryService.setSchemaPartition(sp);
	   
	    CacheService cs = new CacheService();
	    cs.initialize(instanceLayout);
	    directoryService.setCacheService(cs);
	    directoryService.startup();
	    ldapServer.start();
	    
	    createTestEntry();
	  }

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static void createTestEntry() throws NamingException {
		Hashtable env = new Hashtable();
		      env.put(Context.INITIAL_CONTEXT_FACTORY,
		         "com.sun.jndi.ldap.LdapCtxFactory");
		      env.put(Context.PROVIDER_URL,
		         "ldap://localhost:" + PORT);
	    DirContext ctx = new InitialDirContext(env);
	    BasicAttributes attribs = new BasicAttributes();
		BasicAttribute ba = new BasicAttribute(DC, TEST_DC);
		attribs.put(ba);
		ba = new BasicAttribute("objectClass", "domain");
		attribs.put(ba);
	    ctx.createSubcontext(BASE_TEST_DN, attribs);
	    ctx.close();
	}

	  @AfterClass
	  public static void stopApacheDs() throws Exception {
	    ldapServer.stop();
	    directoryService.shutdown();
	  }

	  
	  public static void addSchemas(List<Schema> customSchemas, DefaultSchemaManager schemaManager) throws Exception {
		  //schemaManager = testPartition.getSchemaManager();
		  Registries reg = schemaManager.getRegistries();
		  
		  AttributeTypeRegistry atr =  reg.getAttributeTypeRegistry();
		  
		  ObjectClassRegistry ocr = reg.getObjectClassRegistry();
		  NormalizerRegistry nr = reg.getNormalizerRegistry();
		  
				  
		  
		  for (Schema s: customSchemas) {
			  /*
		    	if (!schemaManager.enable(s)) {
		    		throw new IllegalArgumentException("didn't enable schema " + s.getSchemaName());
		    	}
		    	*/
			  List<Throwable> errors = new ArrayList<Throwable>();
			  Set<SchemaObjectWrapper> sows = s.getContent();
			  schemaManager.load(s);
			  
		    	for (SchemaObjectWrapper sow: sows) {
		    		SchemaObject so = sow.get();
		    		SchemaObjectType sot = so.getObjectType();
		    		switch (sot) {
			    		case OBJECT_CLASS:
			    				schemaManager.add(so);
			    			    ocr.register((ObjectClass) so);
			    			    break;
			    		case ATTRIBUTE_TYPE:
			    			atr.register((AttributeType) so);	
			    			break;
		    		}
		    	}
		    	reg.addSchema(s.getSchemaName());
		    	
		    	reg.schemaLoaded(s);
		    	schemaManager.enable(s);
		    }
		  
		 
	  }

}
