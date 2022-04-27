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
package it.regioneveneto.myp3.myextranet.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import it.regioneveneto.myp3.myextranet.config.WebSecurityConfig;
import it.regioneveneto.myp3.myextranet.utils.Constants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.CacheManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtTokenUtil implements Serializable {
	
	private static final Logger logger = LoggerFactory.getLogger(JwtTokenUtil.class);

  @Value("${jwt.secret}")
  private String secret;
  @Value("${jwt.validity.seconds:36000}") //default: 10 hours
  private long jwtTokenValidity;
  
  @Autowired
  private CacheManager cacheManager;

  //retrieve username from jwt token
  public String getUsernameFromToken(String token) {
    return getClaimFromToken(token, Claims::getSubject);
  }

  //retrieve expiration date from jwt token
  public Date getExpirationDateFromToken(String token) {
    return getClaimFromToken(token, Claims::getExpiration);
  }

  public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = getAllClaimsFromToken(token);
    return claimsResolver.apply(claims);
  }

  //for retrieveing any information from token we will need the secret key
  public Claims getAllClaimsFromToken(String token) {
    return Jwts.parserBuilder().setSigningKey(secret).build().parseClaimsJws(token).getBody();
  }

  //check if the token has expired
  private Boolean isTokenExpired(String token) {
    final Date expiration = getExpirationDateFromToken(token);
    return expiration.before(new Date());
  }

  //generate token for user
  public String generateToken(UserDetails userDetails) {
    Map<String, Object> claims = new HashMap<>();
    return doGenerateToken(claims, userDetails.getUsername());
  }

  //generate token for user
  public String generateToken(String username, Map<String, Object> claims) {
    return doGenerateToken(claims, username);
  }
  
  private String doGenerateToken(Map<String, Object> claims, String subject) {
    byte[] keyBytes = Decoders.BASE64.decode(secret);
    Key key = Keys.hmacShaKeyFor(keyBytes);
    return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + jwtTokenValidity * 1000))
        .signWith(key, SignatureAlgorithm.HS512).compact();
  }

  //validate token
  public Boolean validateToken(String token, UserDetails userDetails) {
    final String username = getUsernameFromToken(token);
    return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
  }

  public void storeTokenInCache(String token) {
		logger.debug("Storing token in cache");
		Cache cache = cacheManager.getCache(Constants.CACHE_NAME_VALID_TOKENS);
		if (cache != null) {
			cache.put(token, null);
			logger.debug(String.format("Token \"%s\" stored in cache ", token));
		} else {
			logger.debug(String.format("Cache \"%s\" not found", Constants.CACHE_NAME_VALID_TOKENS));
		} 
  }
  
  public boolean isTokenInCache(String token) {
		logger.debug("Looking up token in cache");
		Cache cache = cacheManager.getCache(Constants.CACHE_NAME_VALID_TOKENS);
		if (cache != null) {
			ValueWrapper t = cache.get(token);
			if (t == null) {
				logger.error(String.format("Token \"%s\" not found in cache ", token));
				return false;
			}
			logger.debug(String.format("Token \"%s\" found in cache ", token));
			return true;
		} else {
			logger.debug(String.format("Cache \"%s\" not found", Constants.CACHE_NAME_VALID_TOKENS));
			return false;
		}
  }
  
  public void evictTokenFromCache(String token) {
	logger.debug("Evicting token from cache");
	Cache cache = cacheManager.getCache(Constants.CACHE_NAME_VALID_TOKENS);
	if (cache != null) {
		cache.evict(token);
		logger.debug(String.format("Token \"%s\" evicted from cache ", token));
	} else {
		logger.debug(String.format("Cache \"%s\" not found", Constants.CACHE_NAME_VALID_TOKENS));
	}
  }
  

}