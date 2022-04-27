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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import it.regioneveneto.myp3.myextranet.exception.InvalidTokenException;
import it.regioneveneto.myp3.myextranet.service.MyProfileService;
import it.regioneveneto.myp3.myextranet.utils.Constants;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

  @Value("${static.serve.enabled:false}")
  String staticContentEnabled;
  @Value("${static.serve.path:/staticContent}")
  String staticContentPath;
  
  @Value("${myportal.ipa}")
  String ipa;
  
  @Value("${myprofile.application}")
  String applicationCode;

  @Autowired
  private JwtTokenUtil jwtTokenUtil;
  
  @Autowired
  private MyProfileService myProfileService;

  private AntPathRequestMatcher antPathRequestMatcher;

  @Override
  protected void initFilterBean() throws ServletException {
    super.initFilterBean();
    if("true".equalsIgnoreCase(staticContentEnabled)) {
      antPathRequestMatcher = new AntPathRequestMatcher(staticContentPath+"/**");
    }
  }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    return antPathRequestMatcher != null && antPathRequestMatcher.matches(request);
  }

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {
		// read token from cookie
		String jwtToken = getAccessTokenFromCookie(request);
		
		if (jwtToken != null) {
			
			try {
				// check if is still active
				if (!jwtTokenUtil.isTokenInCache(jwtToken)) {
					throw new InvalidTokenException("The token is not valid");
				}
				
				// parse JWT token
				Claims claims = jwtTokenUtil.getAllClaimsFromToken(jwtToken); //retrieve user info from token
				UserWithAdditionalInfo user = new UserWithAdditionalInfo(
						claims.getSubject(), claims.get("nome",String.class),
						claims.get("cognome",String.class), claims.get("codiceFiscale",String.class),
						claims.get("email", String.class), null, null );
								
				// get user roles
				try {
					List<Acl> acls = myProfileService.getUserAcls(user.getCodiceFiscale(), ipa, applicationCode);
					user.setAcls(acls);
				} catch (Exception e) {
					// user not present in MyProfile
					logger.error("Error getting user profile", e);
					user.setAcls(new ArrayList<Acl>());
				}

				// set the current user (with details) from JWT Token into Spring Security
				// configuration
				UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
						user, null, user.getAuthorities());
				usernamePasswordAuthenticationToken
						.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
			} catch (IllegalArgumentException | SignatureException | InvalidTokenException e) {
				logger.warn("Unable to get JWT Token", e);
				request.setAttribute(JwtAuthenticationEntryPoint.TOKEN_ERROR_CODE_ATTRIB,
						JwtAuthenticationEntryPoint.TOKEN_ERROR_CODE_INVALID);
			} catch (ExpiredJwtException e) {
				logger.warn("JWT Token has expired", e);
				request.setAttribute(JwtAuthenticationEntryPoint.TOKEN_ERROR_CODE_ATTRIB,
						JwtAuthenticationEntryPoint.TOKEN_ERROR_CODE_EXPIRED);
			} catch (Exception e) {
				logger.warn("Generic error", e);
				request.setAttribute(JwtAuthenticationEntryPoint.TOKEN_ERROR_CODE_ATTRIB,
						JwtAuthenticationEntryPoint.TOKEN_ERROR_CODE_INVALID);
			} 
		} else {
			request.setAttribute(JwtAuthenticationEntryPoint.TOKEN_ERROR_CODE_ATTRIB,
					JwtAuthenticationEntryPoint.TOKEN_ERROR_CODE_MISSING);
		}
		chain.doFilter(request, response);
	}

	private String getAccessTokenFromCookie(HttpServletRequest request) {
		Cookie[] cookiesArr = request.getCookies();
		if (cookiesArr == null) return null;
		
		List<Cookie> cookies = new ArrayList<Cookie>(Arrays.asList(cookiesArr));
		Optional<Cookie> accessTokenCookieOptional = cookies.stream().filter(c -> c.getName().equals(Constants.COOKIE_NAME_ACCESS_TOKEN)).findFirst();
		
		return accessTokenCookieOptional.isPresent() ? accessTokenCookieOptional.get().getValue() : null;
	}
}
