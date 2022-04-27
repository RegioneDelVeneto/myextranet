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
package it.regioneveneto.myp3.myextranet.web.controller;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.regioneveneto.myp3.myextranet.bean.ApiResult;
import it.regioneveneto.myp3.myextranet.exception.MyExtranetSecurityException;
import it.regioneveneto.myp3.myextranet.security.UserWithAdditionalInfo;
import it.regioneveneto.myp3.myextranet.service.SecurityService;
import it.regioneveneto.myp3.myextranet.service.UtenteMessaggiService;
import it.regioneveneto.myp3.myextranet.utils.Constants;
import it.regioneveneto.myp3.myextranet.web.dto.PagedData;
import it.regioneveneto.myp3.myextranet.web.dto.UtenteMessaggiDTO;
import it.regioneveneto.myp3.myextranet.web.dto.UtenteMessaggiFilterDTO;

@RestController
@RequestMapping("/utente-messaggi")
@Tag(name = "tag_utente_messaggi", description = "UtenteMessaggi")
public class UtenteMessaggiRESTController {
    private static final Logger LOG = LoggerFactory.getLogger(UtenteMessaggiRESTController.class);

    @Autowired
    private UtenteMessaggiService utenteMessaggiService;
    
    @Autowired
    private SecurityService securityService;

    /**
     * Metodo GET per recuperare gli utentiMessaggi
     * @return utentiMessaggi
     * @throws Exception
     */
    @Operation(
    		summary = "Metodo GET per recuperare gli utentiMessaggi",
			responses = {
                    @ApiResponse(description = "GET per recuperare gli utentiMessaggi",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Object.class))),
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved utentiMessaggi"),
                    @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
                    @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
                    @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
            }
    )

    @GetMapping("")
    public ResponseEntity<PagedData<UtenteMessaggiDTO>> getAllUtentiMessaggi(
            @ModelAttribute( name = "_filter" ) UtenteMessaggiFilterDTO utenteMessaggiFilter,
    		@RequestParam( name = "_page", required = false, defaultValue = "0" ) final Integer pageNumber,
    		@RequestParam( name = "_pageSize", required = false, defaultValue = "10" ) final Integer pageSize,
    		@RequestParam( name = "_orderBy", required = false ) final String orderProperty,
    		@RequestParam( name = "_orderDir", required = false ) final String orderDirection,
    		@AuthenticationPrincipal UserWithAdditionalInfo user
    		) {
    	LOG.debug("UtenteMessaggiRESTController --> GET Lista utentiMessaggi." );
    	
    	// check permission
    	try {
    		securityService.checkPermission(user, Constants.ACL_UTENTI, Constants.PERMISSION_VISUALIZZA);
    	} catch (MyExtranetSecurityException e) {
    		LOG.error(String.format("Access denied for user %s", user.getUsername()), e);
    		return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_ACCESS_DENIED_TO_DATA_MESSAGE, e.getMessage(), Constants.API_ERROR_SECURITY_CODE), HttpStatus.FORBIDDEN);
    	}

        PagedData<UtenteMessaggiDTO> utentiMessaggi = utenteMessaggiService.getUtentiMessaggi(utenteMessaggiFilter, pageNumber, pageSize, orderProperty, orderDirection);
        LOG.debug("UtenteMessaggiRESTController --> GET Lista utentiMessaggi eseguito con successo.");

        return new ResponseEntity<PagedData<UtenteMessaggiDTO>>(utentiMessaggi, HttpStatus.OK);

    }
    
    /**
     * Metodo POST per inserire un utenteMessaggi
     * @return utenteMessaggi
     * @throws Exception
     */
    @Operation(
    		summary = "Metodo POST per inserire un utenteMessaggi",
			responses = {
                    @ApiResponse(description = "POST per inserire un utenteMessaggi",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Object.class))),
                    @ApiResponse(responseCode = "201", description = "Successfully created utenteMessaggi"),
                    @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
                    @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
                    @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
            }
    )
    @PostMapping("")
    public ResponseEntity<ApiResult> insertUtenteMessaggi(
    		@RequestBody final @Valid UtenteMessaggiDTO newUtenteMessaggi,
    		@AuthenticationPrincipal UserWithAdditionalInfo user
    		) {
    	LOG.debug("UtenteMessaggiRESTController --> POST nuovo utenteMessaggi." );
    	
    	// check permission
    	try {
    		securityService.checkPermission(user, Constants.ACL_UTENTI, Constants.PERMISSION_GESTISCI);
    	} catch (MyExtranetSecurityException e) {
    		LOG.error(String.format("Access denied for user %s", user.getUsername()), e);
    		return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_ACCESS_DENIED_TO_DATA_MESSAGE, e.getMessage(), Constants.API_ERROR_SECURITY_CODE), HttpStatus.FORBIDDEN);
    	}
    	
    	try {
            
            // check object does have an Id
            if (newUtenteMessaggi.getUtente() == null || newUtenteMessaggi.getMessaggio() == null) {
            	LOG.error("UtenteMessaggiRESTController --> POST utenteMessaggi usato impropriamente: l'oggetto non ha un ID");
            	return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_VALIDATION_MESSAGE, "l'oggetto non ha un ID", Constants.API_ERROR_VALIDATION_CODE), HttpStatus.BAD_REQUEST);
            }
            
            UtenteMessaggiDTO utenteMessaggi = utenteMessaggiService.insertUtenteMessaggi(newUtenteMessaggi);
            LOG.debug("UtenteMessaggiRESTController --> POST nuovo utenteMessaggi eseguito con successo.");

            return new ResponseEntity<ApiResult>(new ApiResult(Constants.RESULT_OK, "Successfully created utenteMessaggi", utenteMessaggi), HttpStatus.CREATED);			
		} catch (Exception e) {
			LOG.error("UtenteMessaggiRESTController --> POST nuovo utenteMessaggi non riuscito", e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_GENERIC_MESSAGE, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}

    }
    
    /**
     * Metodo PATCH per modificare parzialmente un utenteMessaggi
     * @return utenteMessaggi
     * @throws Exception
     */
    @Operation(
    		summary = "Metodo PATCH per modificare parzialmente un utenteMessaggi",
			responses = {
                    @ApiResponse(description = "PATCH per modificare parzialmente un utenteMessaggi",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Object.class))),
                    @ApiResponse(responseCode = "200", description = "Successfully patched utenteMessaggi"),
                    @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
                    @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
                    @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
            }
    )
    @PatchMapping("")
    public ResponseEntity<ApiResult> patchUtenteMessaggi(
    		@RequestBody final @Valid UtenteMessaggiDTO patchUtenteMessaggi,
    		@AuthenticationPrincipal UserWithAdditionalInfo user
    		) {
    	LOG.debug("UtenteMessaggiRESTController --> PATCH utenteMessaggi." );
    	
    	// check permission
    	try {
    		securityService.checkPermission(user, Constants.ACL_UTENTI, Constants.PERMISSION_GESTISCI);
    	} catch (MyExtranetSecurityException e) {
    		LOG.error(String.format("Access denied for user %s", user.getUsername()), e);
    		return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_ACCESS_DENIED_TO_DATA_MESSAGE, e.getMessage(), Constants.API_ERROR_SECURITY_CODE), HttpStatus.FORBIDDEN);
    	}
    	
    	try {
            
            // check object does have an Id
            if (patchUtenteMessaggi.getUtente() == null || patchUtenteMessaggi.getMessaggio() == null) {
            	LOG.error("UtenteMessaggiRESTController --> PATCH utenteMessaggi usato impropriamente: l'oggetto non ha un ID");
            	return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_VALIDATION_MESSAGE, "l'oggetto non ha un ID", Constants.API_ERROR_VALIDATION_CODE), HttpStatus.BAD_REQUEST);
            }
            
            UtenteMessaggiDTO utenteMessaggi = utenteMessaggiService.updateUtenteMessaggi(patchUtenteMessaggi, true);
            LOG.debug("UtenteMessaggiRESTController --> PATCH utenteMessaggi eseguito con successo.");

            return new ResponseEntity<ApiResult>(new ApiResult(Constants.RESULT_OK, "Successfully patched utenteMessaggi", utenteMessaggi), HttpStatus.OK);			
		} catch (Exception e) {
			LOG.error("UtenteMessaggiRESTController --> PATCH utenteMessaggi non riuscito", e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_GENERIC_MESSAGE, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}

    }
    
    /**
     * Metodo PUT per modificare un utenteMessaggi
     * @return utenteMessaggi
     * @throws Exception
     */
    @Operation(
    		summary = "Metodo PUT per modificare un utenteMessaggi",
			responses = {
                    @ApiResponse(description = "PUT per modificare un utenteMessaggi",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Object.class))),
                    @ApiResponse(responseCode = "200", description = "Successfully updated utenteMessaggi"),
                    @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
                    @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
                    @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
            }
    )
    @PutMapping("")
    public ResponseEntity<ApiResult> updateUtenteMessaggi(
    		@RequestBody final @Valid UtenteMessaggiDTO updateUtenteMessaggi,
    		@AuthenticationPrincipal UserWithAdditionalInfo user
    		) {
    	
    	// check permission
    	try {
    		securityService.checkPermission(user, Constants.ACL_UTENTI, Constants.PERMISSION_GESTISCI);
    	} catch (MyExtranetSecurityException e) {
    		LOG.error(String.format("Access denied for user %s", user.getUsername()), e);
    		return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_ACCESS_DENIED_TO_DATA_MESSAGE, e.getMessage(), Constants.API_ERROR_SECURITY_CODE), HttpStatus.FORBIDDEN);
    	}
    	
    	try {
            LOG.debug("UtenteMessaggiRESTController --> PUT utenteMessaggi." );
            
            // check object does have an Id
            if (updateUtenteMessaggi.getUtente() == null || updateUtenteMessaggi.getMessaggio() == null) {
            	LOG.error("UtenteMessaggiRESTController --> PUT utenteMessaggi usato impropriamente: l'oggetto non ha un ID");
            	return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_VALIDATION_MESSAGE, "l'oggetto non ha un ID", Constants.API_ERROR_VALIDATION_CODE), HttpStatus.BAD_REQUEST);
            }
            
            UtenteMessaggiDTO utenteMessaggi = utenteMessaggiService.updateUtenteMessaggi(updateUtenteMessaggi, false);
            LOG.debug("UtenteMessaggiRESTController --> PUT utenteMessaggi eseguito con successo.");

            return new ResponseEntity<ApiResult>(new ApiResult(Constants.RESULT_OK, "Successfully updated utenteMessaggi", utenteMessaggi), HttpStatus.OK);			
		} catch (Exception e) {
			LOG.error("UtenteMessaggiRESTController --> PUT utenteMessaggi non riuscito", e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_GENERIC_MESSAGE, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}

    }

}
