/**
 *     MyExtranet, il portale per collaborare con l’ente Regione Veneto.
 *     Copyright (C) 2022  Regione Veneto
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package it.regioneveneto.myp3.myextranet.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "cache")
public class CacheConfigurationProperties {

	private String type;
	private long timeoutSeconds = 60;
	private int redisCommandTimeoutSeconds = 120;
	private RedisStandaloneConfigurationProperties standalone;
	private RedisSentinelConfigurationProperties sentinel;
	
	// Mapping of cacheNames to timeout in seconds
	private Map<String, Long> cacheExpirations = new HashMap<>();

	public long getTimeoutSeconds() {
		return timeoutSeconds;
	}

	public void setTimeoutSeconds(long timeoutSeconds) {
		this.timeoutSeconds = timeoutSeconds;
	}

	public Map<String, Long> getCacheExpirations() {
		return cacheExpirations;
	}

	public void setCacheExpirations(Map<String, Long> cacheExpirations) {
		this.cacheExpirations = cacheExpirations;
	}

	public int getRedisCommandTimeoutSeconds() {
		return redisCommandTimeoutSeconds;
	}

	public void setRedisCommandTimeoutSeconds(int redisCommandTimeoutSeconds) {
		this.redisCommandTimeoutSeconds = redisCommandTimeoutSeconds;
	}

	public RedisStandaloneConfigurationProperties getStandalone() {
		return standalone;
	}

	public void setStandalone(RedisStandaloneConfigurationProperties standalone) {
		this.standalone = standalone;
	}

	public RedisSentinelConfigurationProperties getSentinel() {
		return sentinel;
	}

	public void setSentinel(RedisSentinelConfigurationProperties sentinel) {
		this.sentinel = sentinel;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public CacheConfigurationProperties() {
		
	}
	
	public static class RedisStandaloneConfigurationProperties {
		private int redisPort;
		private String redisHost;

		public int getRedisPort() {
			return redisPort;
		}

		public void setRedisPort(int redisPort) {
			this.redisPort = redisPort;
		}

		public String getRedisHost() {
			return redisHost;
		}

		public void setRedisHost(String redisHost) {
			this.redisHost = redisHost;
		}

	}
	
	public static class RedisSentinelConfigurationProperties {
		private String master;
		private String nodes;
		private String masterPassword;
		private String sentinelPassword;
		private Integer databaseIndex;

		public String getMaster() {
			return master;
		}

		public void setMaster(String master) {
			this.master = master;
		}

		public String getNodes() {
			return nodes;
		}

		public void setNodes(String nodes) {
			this.nodes = nodes;
		}

		public String getMasterPassword() {
			return masterPassword;
		}

		public void setMasterPassword(String masterPassword) {
			this.masterPassword = masterPassword;
		}

		public String getSentinelPassword() {
			return sentinelPassword;
		}

		public void setSentinelPassword(String sentinelPassword) {
			this.sentinelPassword = sentinelPassword;
		}
		
		public Integer getDatabaseIndex() {
			return databaseIndex;
		}
		
		public void setDatabaseIndex(Integer databaseIndex) {
			this.databaseIndex = databaseIndex;
		}
	}
}
