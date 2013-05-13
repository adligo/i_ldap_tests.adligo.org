package org.adligo.i.ldap;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.directory.api.i18n.I18n;
import org.apache.directory.api.ldap.model.constants.MetaSchemaConstants;
import org.apache.directory.api.ldap.model.constants.SchemaConstants;
import org.apache.directory.api.ldap.model.entry.Attribute;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.entry.Value;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.api.ldap.model.ldif.LdifEntry;
import org.apache.directory.api.ldap.model.ldif.LdifReader;
import org.apache.directory.api.ldap.model.schema.AttributeType;
import org.apache.directory.api.ldap.model.schema.MatchingRule;
import org.apache.directory.api.ldap.model.schema.MutableAttributeType;
import org.apache.directory.api.ldap.model.schema.MutableObjectClass;
import org.apache.directory.api.ldap.model.schema.ObjectClass;
import org.apache.directory.api.ldap.model.schema.SchemaObject;
import org.apache.directory.api.ldap.model.schema.SchemaObjectWrapper;
import org.apache.directory.api.ldap.model.schema.parsers.AttributeTypeDescriptionSchemaParser;
import org.apache.directory.api.ldap.model.schema.parsers.ObjectClassDescriptionSchemaParser;
import org.apache.directory.api.ldap.model.schema.registries.AttributeTypeRegistry;
import org.apache.directory.api.ldap.model.schema.registries.DefaultSchema;
import org.apache.directory.api.ldap.model.schema.registries.MatchingRuleRegistry;
import org.apache.directory.api.ldap.model.schema.registries.ObjectClassRegistry;
import org.apache.directory.api.ldap.model.schema.registries.Schema;
import org.apache.directory.api.ldap.schemamanager.impl.DefaultSchemaManager;
import org.apache.directory.api.util.StringConstants;
import org.apache.log4j.Logger;


public class OpenDsToApacheDsSchemaLoader 
{
    /**
     * Filesystem path separator pattern, either forward slash or backslash.
     * java.util.regex.Pattern is immutable so only one instance is needed for all uses.
     */
    private static final String ATTRIBUTE_KEYS = "DESC EQUALITY ORDERING SUBSTR SUB SYNTAX SINGLE-VALUE";

    private static final String OBJECT_KEYS = "DESC SUP STRUCTURAL MUST MAY";
    private static final Logger log = Logger.getLogger(OpenDsToApacheDsSchemaLoader.class);
	 

	public OpenDsToApacheDsSchemaLoader() throws Exception {}
	
	  /**
	   * the class in the jar with the file
	   * @param file
	   * @param c
	   * @return
	   */
	  public Schema loadFromClasspath(String file, Class<?> c, DefaultSchemaManager dsm) throws Exception {
		  InputStream is =  c.getResourceAsStream(file);
		  LdifReader reader = new LdifReader( is );
		  LdifEntry entry = reader.next();
		  reader.close();
		  is.close();
		  Schema schema = getSchema( entry.getEntry() );
		  
		  is =  c.getResourceAsStream(file);
		  AttributeTypeDescriptionSchemaParser ap = new AttributeTypeDescriptionSchemaParser();
		  ObjectClassDescriptionSchemaParser op = new ObjectClassDescriptionSchemaParser();
		  Set<SchemaObjectWrapper> wrappers =  schema.getContent();
		 
		  InputStreamReader isr = new InputStreamReader(is);
		  BufferedReader br = new BufferedReader(isr);
		  
		  Map<String, String> attribNamesToOids = new HashMap<String,String>();
		  String line = br.readLine();
		  while (line != null) {
			 if (line.indexOf("attributeTypes: ") == 0) {
				 AttributeType at = getAttribute(line, dsm);
				 List<String> names = at.getNames();
				 String oid = at.getOid();
				 for (String name: names) {
					 attribNamesToOids.put(name.toUpperCase(), oid);
				 }
				 wrappers.add(new SchemaObjectWrapper(at));
			 } else if (line.indexOf("objectclasses: ") == 0) {
				 ObjectClass oc = getObjectClass(line, dsm, attribNamesToOids);
				 wrappers.add(new SchemaObjectWrapper(oc));
			 }
			 line = br.readLine();
		  }
		  is.close();
		  return schema;
	  }
	  
	  public AttributeType getAttribute(String p,  DefaultSchemaManager schemaManager) throws LdapException {
		  MatchingRuleRegistry reg = schemaManager.getMatchingRuleRegistry();
			 
		  char [] chars = p.toCharArray();
		  boolean inOId = false;
		  boolean afterOid = false;
		  boolean afterName = false;
		  boolean inQuote = false;
		  
		  String key = "NAME";
		  MutableAttributeType mat = null;
		  StringBuilder sb = new StringBuilder();
		  StringBuilder sbq = new StringBuilder();
		  for (int i = 15; i < chars.length; i++) {
			 char c = chars[i];
			 if (inQuote) {
				 if (c == '\'') {
					 inQuote = false;
				 } else {
					 sbq.append(c);
				 }
			 } else {
				 if (c == '(' || c == ')' || c == ' ') {
					 
					 if (!inOId) {
						 inOId = true;
					 } else if (!afterOid) {
						 String s = sb.toString();
						 if (mat == null) {
							 if (s.length() > 0) {
								 mat = new MutableAttributeType(s);
							 } 
						 } else if (s.equalsIgnoreCase("NAME")) {
							 afterOid = true;
						 }
					 } else if (!afterName) {
						 String s = sbq.toString();
						 if (s.length() > 0) {
							 mat.addName(s);
							 sbq = new StringBuilder();
						 } 
					 	 s = sb.toString();
					 	 s = s.toUpperCase();
					 	 if (s.length() > 0) {
							 if (ATTRIBUTE_KEYS.indexOf(s) != -1) {
								 key = s;
								 afterName = true;
							 }
					 	 }
					 } else  {
						 String s = sb.toString();
						 String sq = sbq.toString();
					 		
						 if (s.length() > 0) {
							 s = s.toUpperCase();
							 if (s.equals("DESC")) {
								 key = "DESC";
							 } else {
								 if (ATTRIBUTE_KEYS.indexOf(s) != -1) {
									 key = s;
									 if (s.equalsIgnoreCase("SINGLE-VALUE")) {
									 		mat.setSingleValued(true);
									 }
									 afterName = true;
								 } else if (key.equals("SYNTAX")) {
									 mat.setSyntaxOid(s);
								 } else if (key.equals("SUB")){
									 @SuppressWarnings("unchecked")
									 AttributeTypeRegistry atr = schemaManager.getAttributeTypeRegistry();
									 SchemaObject so = atr.lookup(s);
									 String superiorOid = so.getOid();
									 mat.setSuperiorOid(superiorOid);
								 } else {
									try {
										 MatchingRule rule = reg.lookup(s);
										 
										 switch (key) {
										 	case "EQUALITY":
										 		
										 		mat.setEquality(rule);
										 		break;
										 	case "ORDERING":
										 		mat.setOrdering(rule);
										 		break;
										 	case "SUBSTR":
										 		mat.setSubstring(rule);
										 		break;
										 }
									 } catch (LdapException x) {
										 log.error("Problem with attribute " + key + " " + s + "\n" + p);
										 log.error(x.getMessage(), x);
									 }
								 }
							 }
						 } else if (sq.length() > 0 ) {
							 if (key.equals("DESC")) {
								 mat.setDescription(s);
								 sbq = new StringBuilder();
							 }
						 } 
					 } 
					 sb   = new StringBuilder();
				 } else {
					 if (c == '\'') {
						 inQuote = true;
					 } else {
						 sb.append(c);
					 }
				 }
				
			 }
		}
		  return mat;
	  }
	  
	  
	  public ObjectClass getObjectClass(String p,  DefaultSchemaManager schemaManager, Map<String, String> attribNamesToOids) throws LdapException {
		  AttributeTypeRegistry reg = schemaManager.getAttributeTypeRegistry();
	
		  char [] chars = p.toCharArray();
		  boolean inOId = false;
		  boolean afterOid = false;
		  boolean afterName = false;
		  boolean inQuote = false;
		  
		  String key = "NAME";
		  MutableObjectClass mat = null;
		  StringBuilder sb = new StringBuilder();
		  StringBuilder sbq = new StringBuilder();
		  for (int i = 15; i < chars.length; i++) {
			 char c = chars[i];
			 if (inQuote) {
				 if (c == '\'') {
					 inQuote = false;
				 } else {
					 sbq.append(c);
				 }
			 } else {
				 if (c == '(' || c == ')' || c == ' ' || c == '$') {
					 
					 if (!inOId) {
						 inOId = true;
					 } else if (!afterOid) {
						 String s = sb.toString();
						 if (mat == null) {
							 if (s.length() > 0) {
								 mat = new MutableObjectClass(s);
							 } 
						 } else if (s.equalsIgnoreCase("NAME")) {
							 afterOid = true;
						 }
					 } else if (!afterName) {
						 String s = sbq.toString();
						 if (s.length() > 0) {
							 mat.addName(s);
							 sbq = new StringBuilder();
						 } 
					 	 s = sb.toString();
					 	 s = s.toUpperCase();
					 	 if (s.length() > 0) {
							 if (ATTRIBUTE_KEYS.indexOf(s) != -1) {
								 key = s;
								 afterName = true;
							 }
					 	 }
					 } else  {
						 String s = sb.toString();
						 String sq = sbq.toString();
					 		
						 if (s.length() > 0) {
							 s = s.toUpperCase();
							
							 if (OBJECT_KEYS.indexOf(s) != -1) {
								 key = s;
								 afterName = true;
							 } else if (key.equals("SUP")){
								 @SuppressWarnings("unchecked")
								 ObjectClassRegistry atr = schemaManager.getObjectClassRegistry();
								 ObjectClass so = atr.lookup(s);
								 List<String> sups = so.getSuperiorOids();
								 String superiorOid = so.getOid();
								 List<String> newSups = new ArrayList<String>(sups);
								 newSups.add(superiorOid);
								 mat.setSuperiorOids(newSups);
							 } else {
								String atOid = null;
								try {
									AttributeType at = reg.lookup(s);
									if (at != null) {
										atOid = at.getOid();
									}
								} catch (LdapException x) {
									//eat
								}
								if (atOid == null) {
									atOid = attribNamesToOids.get(s);
								}
								if (atOid == null) {
									throw new IllegalArgumentException("No Oid found for " + s);
								}
								 switch (key) {
								 	case "MUST":
								 		mat.addMustAttributeTypeOids(atOid);
								 		break;
								 	case "MAY":
								 		mat.addMayAttributeTypeOids(atOid);
								 		break;
								 }
							 }
						 } else if (sq.length() > 0 ) {
							 if (key.equals("DESC")) {
								 mat.setDescription(s);
								 sbq = new StringBuilder();
							 }
						 } 
					 } 
					 sb   = new StringBuilder();
				 } else {
					 if (c == '\'') {
						 inQuote = true;
					 } else {
						 sb.append(c);
					 }
				 }
				
			 }
		}
		  return mat;
	  }
	  
	    /**
	     * Gets the schema.
	     *
	     * @param entry the entry
	     * @return the schema
	     * @throws Exception the exception
	     */
	    protected Schema getSchema( Entry entry ) throws Exception
	    {
	        if ( entry == null )
	        {
	            throw new IllegalArgumentException( I18n.err( I18n.ERR_04261 ) );
	        }

	        Attribute objectClasses = entry.get( SchemaConstants.OBJECT_CLASS_AT );
	        boolean isSchema = false;

	        for ( Value<?> value : objectClasses )
	        {
	            if ( "subschema".equalsIgnoreCase( value.getString() ) )
	            {
	                isSchema = true;
	                break;
	            }
	        }

	        if ( !isSchema )
	        {
	            return null;
	        }

	        String name;
	        String owner;
	        String[] dependencies = StringConstants.EMPTY_STRINGS;
	        boolean isDisabled = false;

	        if ( entry.get( SchemaConstants.CN_AT ) == null )
	        {
	            throw new IllegalArgumentException( I18n.err( I18n.ERR_04262 ) );
	        }

	        name = entry.get( SchemaConstants.CN_AT ).getString();

	        if ( entry.get( SchemaConstants.CREATORS_NAME_AT ) == null )
	        {
	            throw new IllegalArgumentException( "entry must have a valid " + SchemaConstants.CREATORS_NAME_AT
	                + " attribute" );
	        }

	        owner = entry.get( SchemaConstants.CREATORS_NAME_AT ).getString();

	        if ( entry.get( MetaSchemaConstants.M_DISABLED_AT ) != null )
	        {
	            String value = entry.get( MetaSchemaConstants.M_DISABLED_AT ).getString();
	            value = value.toUpperCase();
	            isDisabled = value.equals( "TRUE" );
	        }

	        if ( entry.get( MetaSchemaConstants.M_DEPENDENCIES_AT ) != null )
	        {
	            Set<String> depsSet = new HashSet<String>();
	            Attribute depsAttr = entry.get( MetaSchemaConstants.M_DEPENDENCIES_AT );

	            for ( Value<?> value : depsAttr )
	            {
	                depsSet.add( value.getString() );
	            }

	            dependencies = depsSet.toArray( StringConstants.EMPTY_STRINGS );
	        }

	        return new DefaultSchema( name, owner, dependencies, isDisabled );
	    }
	    


}
