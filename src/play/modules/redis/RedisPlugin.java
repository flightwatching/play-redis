package play.modules.redis;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import play.Logger;
import play.Play;
import play.PlayPlugin;
import play.cache.Cache;
import play.exceptions.ConfigurationException;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.Protocol;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;
import redis.clients.util.Pool;


/**
 * Play plugin for Redis.
 * 
 * @author Tim Kral
 */
public class RedisPlugin extends PlayPlugin {

	private boolean createdRedisCache;
	private boolean createdRedis;
	
	public static boolean isRedisCacheEnabled() {
		return Play.configuration.getProperty("redis.cache", "disabled").equals("enabled");
	}

	/**
	 * Masks the credentials of a redis URL, whose password sits in the userinfo part
	 * (<code>redis://:secret@host:port</code>).
	 *
	 * Logging such a URL verbatim - or echoing it back inside a parse error - writes the password in clear to
	 * every pod's stdout, and from there into whatever collects those logs. Anything that puts a redis URL in a
	 * message must go through this first.
	 *
	 * Deliberately not URI-based: it must also work on the malformed URLs reported by the parse error in
	 * RedisConnectionInfo, which are precisely the ones java.net.URI refuses. Everything between the scheme and
	 * the "@" is dropped, username included: keeping the supposedly harmless half would partly preserve a
	 * password that happens to contain a ":".
	 *
	 * The match is greedy up to the LAST "@", not the first: java.net.URI ends the userinfo there, and so does
	 * RedisConnectionInfo below, so a password containing an "@" is valid here and a lazy match would leak its
	 * tail. A redis URL has no path, so there is no later "@" to over-match; and over-redacting a log line is the
	 * harmless failure, printing a password is not.
	 */
	static String redactUrl(String redisUrl) {
		if (redisUrl == null) {
			return null;
		}
		return redisUrl.replaceFirst("^([a-zA-Z][a-zA-Z0-9+.-]*://).*@", "$1***@");
	}
	
	@Override
	public void onConfigurationRead() {
		if (isRedisCacheEnabled()) {
	    	if (Play.configuration.containsKey("redis.cache.url")) {
	    	    String redisCacheUrl = Play.configuration.getProperty("redis.cache.url");
	    	    Logger.info("Connecting to redis cache with %s", redactUrl(redisCacheUrl));
	    	    RedisConnectionInfo redisConnInfo = new RedisConnectionInfo(redisCacheUrl, Play.configuration.getProperty("redis.cache.timeout"));

	    	    // Separate property, not parsed out of the URL (see RedisConnectionInfo below); empty/unresolved => pre-ACL behavior, connect as "default".
	    	    String redisCacheUser = Play.configuration.getProperty("redis.cache.user");
	    	    if (redisCacheUser != null && redisCacheUser.length() > 0 && !redisCacheUser.startsWith("${")) {
	    	    	Logger.info("Connecting to redis cache as ACL user %s", redisCacheUser);
	    	    	RedisCacheImpl.connectionPool = redisConnInfo.getAclConnectionPool(redisCacheUser);
	    	    } else {
	    	    	RedisCacheImpl.connectionPool = redisConnInfo.getConnectionPool();
	    	    }
	    	    RedisCacheImpl.configureKeyPrefix(Play.configuration.getProperty("redis.cache.prefix"));
	    	    Cache.forcedCacheImpl = RedisCacheImpl.getInstance();
	    	    createdRedisCache = true;
	    	} else {
	    		throw new ConfigurationException("Bad configuration for redis cache: missing redis.cache.url");
	    	}
		}
	}
	
	@Override
	public void onApplicationStart() {
    	if (Play.configuration.containsKey("redis.url")) {
    	    String redisUrl = Play.configuration.getProperty("redis.url");
    	    Logger.info("Connecting to redis with %s", redactUrl(redisUrl));
    	    RedisConnectionInfo redisConnInfo = new RedisConnectionInfo(redisUrl, Play.configuration.getProperty("redis.timeout"));
    	    
        	RedisConnectionManager.connectionPool = redisConnInfo.getConnectionPool();
        	createdRedis = true;
    	} else if(Play.configuration.containsKey("redis.1.url")) {
    		int nb = 1;
    		
    		List<JedisShardInfo> shards = new ArrayList<JedisShardInfo>();
            while (Play.configuration.containsKey("redis." + nb + ".url")) {
            	RedisConnectionInfo redisConnInfo = new RedisConnectionInfo(Play.configuration.getProperty("redis." + nb + ".url"), Play.configuration.getProperty("redis.timeout"));
            	shards.add(redisConnInfo.getShardInfo());
                nb++;
            }
            
            RedisConnectionManager.shardedConnectionPool = new ShardedJedisPool(new JedisPoolConfig(), shards, ShardedJedis.DEFAULT_KEY_TAG_PATTERN);
            createdRedis = true;
    	} else {
    		if (!createdRedisCache) Logger.warn("No redis.url found in configuration. Redis will not be available.");
    	}
    	
	}
	
	@Override
	public void onApplicationStop() {
		// Redis cache is destroyed in Cache.stop (see Play.stop)
		if (createdRedis) RedisConnectionManager.destroy();
	}
	
    @Override
    public void invocationFinally() {
    	if (createdRedisCache) RedisCacheImpl.closeCacheConnection();
    	if (createdRedis) RedisConnectionManager.closeConnection();
    }
    
    private static class RedisConnectionInfo {
    	private final String host;
    	private final int port;
    	private final String password;
    	private final int timeout;
    	
    	RedisConnectionInfo(String redisUrl, String timeoutStr) {
    	    URI redisUri;
    		try {
    	        redisUri = new URI(redisUrl);
    	    } catch (URISyntaxException e) {
    	        throw new ConfigurationException("Bad configuration for redis: unable to parse redis url (" + redactUrl(redisUrl) + ")");
    	    }
    		
    	    host = redisUri.getHost();
    	    
        	if (redisUri.getPort() > 0) {
        		port = redisUri.getPort();
        	} else {
        	    port = Protocol.DEFAULT_PORT;
        	}
        	
        	String userInfo = redisUri.getUserInfo();
        	if (userInfo != null) {
        	    String[] parsedUserInfo = userInfo.split(":");
        	    password = parsedUserInfo[parsedUserInfo.length - 1];
        	} else {
        		password = null;
        	}
        	
        	if (timeoutStr == null) {
        		timeout = Protocol.DEFAULT_TIMEOUT;
        	} else {
        		timeout = Integer.parseInt(timeoutStr);
        	}
    	}
    	
    	JedisPool getConnectionPool() {
    		if (password == null) {
    			return new JedisPool(new JedisPoolConfig(), host, port, timeout);
    		}

    		return new JedisPool(new JedisPoolConfig(), host, port, timeout, password);
    	}

    	Pool<Jedis> getAclConnectionPool(String username) {
    		return new AclJedisPool(new JedisPoolConfig(), host, port, timeout, username, password);
    	}

    	JedisShardInfo getShardInfo() {
    		JedisShardInfo si = new JedisShardInfo(host, port, timeout);
    		si.setPassword(password);
    		return si;
    	}
    }
}
