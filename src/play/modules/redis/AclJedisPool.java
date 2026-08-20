package play.modules.redis;

import org.apache.commons.pool.BasePoolableObjectFactory;
import org.apache.commons.pool.impl.GenericObjectPool;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisAclAuth;
import redis.clients.util.Pool;

/** JedisPool equivalent using two-argument ACL AUTH (JedisAclAuth) instead of JedisPool's single-argument AUTH — needed since JedisPool's own connection factory is a private inner class and can't be overridden. */
class AclJedisPool extends Pool<Jedis> {

    AclJedisPool(GenericObjectPool.Config poolConfig, String host, int port, int timeout,
                 String username, String password) {
        super(poolConfig, new AclJedisFactory(host, port, timeout, username, password));
    }

    private static class AclJedisFactory extends BasePoolableObjectFactory {
        private final String host;
        private final String username;
        private final String password;
        private final int port;
        private final int timeout;

        AclJedisFactory(String host, int port, int timeout, String username, String password) {
            this.host = host;
            this.port = port;
            this.timeout = timeout;
            this.username = username;
            this.password = password;
        }

        public Object makeObject() {
            Jedis jedis = (timeout > 0) ? new Jedis(host, port, timeout) : new Jedis(host, port);
            jedis.connect();
            JedisAclAuth.authAcl(jedis, username, password);
            return jedis;
        }

        public void destroyObject(Object obj) {
            Jedis jedis = (Jedis) obj;
            if (jedis.isConnected()) {
                try {
                    jedis.quit();
                } catch (Exception ignored) {
                }
                jedis.disconnect();
            }
        }

        public boolean validateObject(Object obj) {
            Jedis jedis = (Jedis) obj;
            try {
                return jedis.isConnected() && "PONG".equals(jedis.ping());
            } catch (Exception e) {
                return false;
            }
        }
    }
}
