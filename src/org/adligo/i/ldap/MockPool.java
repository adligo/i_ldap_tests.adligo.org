package org.adligo.i.ldap;

import org.adligo.i.ldap.ReadWriteLdapConnection;
import org.adligo.i.ldap.ReadWriteLdapConnectionFactory;
import org.adligo.i.ldap.models.LdapConnectionFactoryConfig;
import org.adligo.i.pool.Pool;
import org.adligo.i.pool.PoolConfiguration;

public class MockPool {
	public static final Pool<ReadWriteLdapConnection> POOL = getPool();
	
	public static final Pool<ReadWriteLdapConnection> getPool() {
		 LdapConnectionFactoryConfig config = new LdapConnectionFactoryConfig();
	      config.setPort(InMemoryApacheDs.PORT);
	      config.setHost("localhost");
	      config.setDefaultChunkSize(10);
	      PoolConfiguration<ReadWriteLdapConnection> poolConfig = new PoolConfiguration<ReadWriteLdapConnection>();
	      poolConfig.setFactory(new ReadWriteLdapConnectionFactory(config));
	      poolConfig.setMax(2);
	      poolConfig.setName("ldapPool");
	      
	      Pool<ReadWriteLdapConnection> pool = new Pool<ReadWriteLdapConnection>(poolConfig);
	      return pool;
	}
}
