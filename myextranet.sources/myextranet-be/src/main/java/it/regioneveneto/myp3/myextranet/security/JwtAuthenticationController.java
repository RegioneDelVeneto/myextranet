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

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.info.BuildProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import it.regioneveneto.myp3.myextranet.service.UtenteService;
import it.regioneveneto.myp3.myextranet.utils.Constants;

@RestController
@CrossOrigin
public class JwtAuthenticationController {

	private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationController.class);

	@Value("${auth.fake.enabled:false}")
	private String fakeAuthEnabled;
	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private JwtTokenUtil jwtTokenUtil;
	@Autowired
	private FakeUserDetailsService userDetailsService;

	
	@Autowired
	private UtenteService utenteService;

	@PostMapping("/public/authenticate")
	public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) {
		if (!"true".equalsIgnoreCase(fakeAuthEnabled))
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

		Map<String, Object> responseMap = new HashMap<>();
		ResponseEntity.BodyBuilder responseBuilder;
		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
					authenticationRequest.getUsername(), authenticationRequest.getPassword()));
			// fake authentication
			final UserWithAdditionalInfo userDetails = userDetailsService
					.loadUserByUsername(authenticationRequest.getUsername());
			Map<String, Object> claims = new HashMap<>();
			claims.put("cognome", userDetails.getCognome());
			claims.put("nome", userDetails.getNome());
			claims.put("codiceFiscale", userDetails.getCodiceFiscale());
			claims.put("email", userDetails.getEmail());
			final String token = jwtTokenUtil.generateToken(authenticationRequest.getUsername(), claims);
			
			// store as valid token
			jwtTokenUtil.storeTokenInCache(token);

			responseMap.put("username", userDetails.getUsername());
			responseMap.put("givenName", userDetails.getNome());
			responseMap.put("familyName", userDetails.getCognome());
			responseMap.put("codFiscale", userDetails.getCodiceFiscale());
			responseMap.put("accessToken", token);
			responseBuilder = ResponseEntity.status(HttpStatus.OK);
		} catch (BadCredentialsException bce) {
			logger.error("bad credentials", bce);
			responseMap.put("errorCode", "BAD_CREDENTIALS");
			responseBuilder = ResponseEntity.status(HttpStatus.UNAUTHORIZED);
		} catch (DisabledException e) {
			logger.error("User disabled", e);
			responseMap.put("errorCode", "USER_DISABLED");
			responseBuilder = ResponseEntity.status(HttpStatus.UNAUTHORIZED);
		} catch (Exception ex) {
			logger.error("Generic error", ex);
			responseMap.put("errorCode", "GENERIC_ERROR");
			responseBuilder = ResponseEntity.status(HttpStatus.UNAUTHORIZED);
		}

		return responseBuilder.body(responseMap);
	}
	
	@GetMapping("/user-logout")
	public ResponseEntity<?> logout(@CookieValue(name = Constants.COOKIE_NAME_ACCESS_TOKEN) String jwtToken, @AuthenticationPrincipal UserWithAdditionalInfo user) {
		
		try {
			
			jwtTokenUtil.evictTokenFromCache(jwtToken);
			return ResponseEntity.status(HttpStatus.OK).build();
			
		} catch (Exception e) {
			
			logger.error("Generic ERROR", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
			
		}
		
	}
}
