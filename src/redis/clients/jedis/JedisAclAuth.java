package redis.clients.jedis;

import redis.clients.jedis.exceptions.JedisConnectionException;

/** Sends a real two-argument `AUTH user pass` (Jedis 2.0.0 only exposes single-arg AUTH); same-package placement gives legal access to protected Connection/BinaryJedis members without reflection. */
public final class JedisAclAuth {

    private JedisAclAuth() {
    }

    public static void authAcl(BinaryJedis jedis, String username, String password) {
        jedis.client.sendCommand(Protocol.Command.AUTH, username, password);
        String status = jedis.client.getStatusCodeReply();
        if (!"OK".equals(status)) {
            throw new JedisConnectionException("ACL AUTH failed for user " + username + ": " + status);
        }
    }
}
