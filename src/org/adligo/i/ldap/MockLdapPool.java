package org.adligo.i.ldap;

import org.adligo.i.ldap.ReadWriteLdapConnection;
import org.adligo.i.ldap.ReadWriteLdapConnectionFactory;
import org.adligo.i.ldap.models.LdapConnectionFactoryConfig;
import org.adligo.i.log.shared.Log;
import org.adligo.i.log.shared.LogFactory;
import org.adligo.i.pool.I_Pool;
import org.adligo.i.pool.Pool;
import org.adligo.i.pool.PoolConfigurationMutant;
import org.adligo.models.core.shared.InvalidParameterException;

public class MockLdapPool {
	private static final Log log = LogFactory.getLog(MockLdapPool.class);
	public static final I_Pool<ReadWriteLdapConnection> POOL = getPool();
	
	public static final I_Pool<ReadWriteLdapConnection> getPool() {
		try {
		  LdapConnectionFactoryConfig config = new LdapConnectionFactoryConfig();
	      config.setPort(InMemoryApacheDs.PORT);
	      config.setHost("localhost");
	      config.setDefaultChunkSize(10);
	      PoolConfigurationMutant<ReadWriteLdapConnection> poolConfig = new PoolConfigurationMutant<ReadWriteLdapConnection>();
	      poolConfig.setFactory(new ReadWriteLdapConnectionFactory(config));
	      poolConfig.setMax(2);
	      poolConfig.setName("ldapPool");
	      
	      I_Pool<ReadWriteLdapConnection> pool = new Pool<ReadWriteLdapConnection>(poolConfig);
	      return pool;
		} catch (InvalidParameterException x) {
			throw new RuntimeException(x);
		}
	}
}
