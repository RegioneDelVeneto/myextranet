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
package it.regioneveneto.myp3.myextranet.web.controller.frontoffice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
import it.regioneveneto.myp3.myextranet.exception.EnteExpiredException;
import it.regioneveneto.myp3.myextranet.exception.EnteNotPresentException;
import it.regioneveneto.myp3.myextranet.exception.MyExtranetSecurityException;
import it.regioneveneto.myp3.myextranet.exception.MyExtranetValidationException;
import it.regioneveneto.myp3.myextranet.security.UserWithAdditionalInfo;
import it.regioneveneto.myp3.myextranet.service.EnteService;
import it.regioneveneto.myp3.myextranet.service.SecurityService;
import it.regioneveneto.myp3.myextranet.service.UtenteService;
import it.regioneveneto.myp3.myextranet.utils.Constants;
import it.regioneveneto.myp3.myextranet.web.controller.ValidationController;
import it.regioneveneto.myp3.myextranet.web.dto.AutocompleteFilterDTO;
import it.regioneveneto.myp3.myextranet.web.dto.EnteDTO;
import it.regioneveneto.myp3.myextranet.web.dto.UtenteDTO;
import it.regioneveneto.myp3.myextranet.web.dto.UtenteFilterDTO;

@RestController
@RequestMapping("/frontoffice/utente")
@Tag(name = "tag_utente", description = "Utente")
public class UtenteFrontofficeRESTController extends ValidationController {
    private static final Logger LOG = LoggerFactory.getLogger(UtenteFrontofficeRESTController.class);

    @Autowired
    private UtenteService utenteService;
    
    @Autowired
    private EnteService enteService;
    
    @Autowired
    private SecurityService securityService;

    
    /**
     * Metodo GET per recuperare un utente usando l'id
     * @return utente
     * @throws Exception
     */
    @Operation(
    		summary = "Metodo GET per recuperare un utente",
			responses = {
                    @ApiResponse(description = "GET per recuperare un utente",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Object.class))),
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved utente"),
                    @ApiResponse(responseCode = "400", description = "User not found"),
                    @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
                    @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
                    @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
            }
    )

    @GetMapping("/{_id}")
    public ResponseEntity<UtenteDTO> getUtente(          
    		@PathVariable( name = "_id", required = true) final Integer utenteId,
    		@AuthenticationPrincipal UserWithAdditionalInfo user
    		) {
		try {
	        LOG.debug("UtenteFrontofficeRESTController --> GET utente by Id." );
	        
	    	// check permission
	    	try {
	    		securityService.checkUserValidity(user, true, true, false);
	    	} catch (MyExtranetSecurityException e) {
	    		LOG.error(String.format("Access denied for user %s", user.getUsername()), e);
	    		return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_ACCESS_DENIED_TO_DATA_MESSAGE, e.getMessage(), Constants.API_ERROR_SECURITY_CODE), HttpStatus.FORBIDDEN);
	    	}
	    	
	        UtenteDTO utente;
			utente = utenteService.getUtente(utenteId);
			
			securityService.checkIsDataOwner(utente, user);

	        LOG.debug("UtenteFrontofficeRESTController --> GET utente eseguito con successo.");
	
	        return new ResponseEntity<UtenteDTO>(utente, HttpStatus.OK);
		} catch (MyExtranetSecurityException e) {
			LOG.error(String.format("Access denied for user %s", user.getUsername()), e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_ACCESS_DENIED_TO_DATA_MESSAGE, e.getMessage(), Constants.API_ERROR_SECURITY_CODE), HttpStatus.FORBIDDEN);
		} catch (Exception e) {
			LOG.error("UtenteFrontofficeRESTController --> GET utente non riuscito", e);		
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_GENERIC_MESSAGE, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);

		}
    }
    
	@GetMapping("/userinfo")
	public ResponseEntity<?> userInfo(@AuthenticationPrincipal UserWithAdditionalInfo user) {
		Map<String, Object> responseMap = new HashMap<>();
		responseMap.put("username", user.getUsername());
		responseMap.put("givenName", user.getNome());
		responseMap.put("familyName", user.getCognome());
		responseMap.put("codFiscale", user.getCodiceFiscale());
		responseMap.put("email", user.getEmail());
		responseMap.put("acls", user.getAcls());
		
		Map<String, Object> userValidityProperties = utenteService.getUserValidityProperties(user.getCodiceFiscale());
		responseMap.putAll(userValidityProperties);
		
		return ResponseEntity.status(HttpStatus.OK).body(responseMap);
	}
	
    /**
     * Metodo GET per recuperare gli utenti in caso di autocomplete
     * @return utenti
     * @throws Exception
     */
    @Operation(
    		summary = "Metodo GET per recuperare gli utenti",
			responses = {
                    @ApiResponse(description = "GET per recuperare gli utenti in caso di autocomplete",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Object.class))),
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved utenti"),
                    @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
                    @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
                    @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
            }
    )
    
    @GetMapping("/autocomplete")
    public ResponseEntity<List<UtenteDTO>> getUtentiAutocomplete(
    		@ModelAttribute( name = "_filter" ) AutocompleteFilterDTO autocompleteFilter,
    		@RequestParam( name = "maxLength", required = false, defaultValue = "20" ) final Integer maxLength,
    		@RequestParam( name = "_orderBy", required = false, defaultValue = "cognome" ) final String orderProperty,
    		@RequestParam( name = "_orderDir", required = false, defaultValue = "asc" ) final String orderDirection,
    		@AuthenticationPrincipal UserWithAdditionalInfo user
    		) {
    	LOG.debug("UtenteFrontofficeRESTController --> GET Lista utenti (autocomplete)." );
    	
    	// check permission
    	try {
    		securityService.checkUserValidity(user);
    	} catch (MyExtranetSecurityException e) {
    		LOG.error(String.format("Access denied for user %s", user.getUsername()), e);
    		return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_ACCESS_DENIED_TO_DATA_MESSAGE, e.getMessage(), Constants.API_ERROR_SECURITY_CODE), HttpStatus.FORBIDDEN);
    	}
    	
    	UtenteFilterDTO utenteFilter = new UtenteFilterDTO();
    	utenteFilter.setCodFiscale(autocompleteFilter.getSearchString());
    	utenteFilter.setFlgArchived(0);
    	
        List<UtenteDTO> utenti = utenteService.getUtenti(utenteFilter, 1, maxLength, orderProperty, orderDirection).getRecords();
        
        // reduce depth
        for (UtenteDTO utente : utenti) {
			UtenteDTO utentePadre = utente.getUtentePadre();
			if (utentePadre != null) {
				UtenteDTO up = new UtenteDTO();
				up.setIdUtente(utentePadre.getIdUtente());
				utente.setUtentePadre(up);
			}
		}
        
        LOG.debug("UtenteFrontofficeRESTController --> GET Lista utenti eseguito con successo.");

        return new ResponseEntity<List<UtenteDTO>>(utenti, HttpStatus.OK);

    }    
    
    /**
     * Metodo POST per inserire un utente
     * @return utente
     * @throws Exception
     */
    @Operation(
    		summary = "Metodo POST per inserire un utente",
			responses = {
                    @ApiResponse(description = "POST per inserire un utente",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Object.class))),
                    @ApiResponse(responseCode = "201", description = "Successfully created utente"),
                    @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
                    @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
                    @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
            }
    )
    @PostMapping("")
    public ResponseEntity<ApiResult> insertUtente(
    		@RequestBody final @Valid UtenteDTO newUtente,
    		BindingResult bindingResult,
    		@AuthenticationPrincipal UserWithAdditionalInfo user
    		) {
    	LOG.debug("UtenteFrontofficeRESTController --> POST nuovo utente." );
    	
    	// check permission
    	try {
    		
    		securityService.checkIsDataOwner(newUtente, user);
    	} catch (MyExtranetSecurityException e) {
    		LOG.error(String.format("Access denied for user %s", user.getUsername()), e);
    		return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_ACCESS_DENIED_TO_DATA_MESSAGE, e.getMessage(), Constants.API_ERROR_SECURITY_CODE), HttpStatus.FORBIDDEN);
    	}
    	
    	try {
    		validate(newUtente, bindingResult);
            
            // check object does not have an Id
            if (newUtente.getIdUtente() != null) {
            	LOG.error("UtenteFrontofficeRESTController --> POST nuovo utente usato impropriamente: l'oggetto ha un ID");
            	return new ResponseEntity<ApiResult>(new ApiResult(Constants.RESULT_KO, "POST method is not for updates. Use PUT or PATCH instead.", newUtente), HttpStatus.BAD_REQUEST);
            }
            
            UtenteDTO utente = utenteService.insertUtente(newUtente);
            LOG.debug("UtenteFrontofficeRESTController --> POST nuovo utente eseguito con successo.");

            return new ResponseEntity<ApiResult>(new ApiResult(Constants.RESULT_OK, "Successfully created utente", utente), HttpStatus.CREATED);			
    	} catch (MyExtranetValidationException e) {
    		LOG.error("Validation exception", e);
    		return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_VALIDATION_MESSAGE, e.getMessage(), Constants.API_ERROR_VALIDATION_CODE), HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			LOG.error("UtenteFrontofficeRESTController --> POST nuovo utente non riuscito", e);
			return new ResponseEntity<ApiResult>(new ApiResult(Constants.RESULT_KO, "Error creating utente", e.getMessage()), HttpStatus.BAD_REQUEST);
		}

    }
    
    
    /**
     * Metodo PATCH per modificare parzialmente un utente
     * @return utente
     * @throws Exception
     */
    @Operation(
    		summary = "Metodo PATCH per modificare parzialmente un utente",
			responses = {
                    @ApiResponse(description = "PATCH per modificare parzialmente un utente",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Object.class))),
                    @ApiResponse(responseCode = "200", description = "Successfully patched utente"),
                    @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
                    @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
                    @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
            }
    )
    @PatchMapping("")
    public ResponseEntity<ApiResult> patchUtente(
    		@RequestBody final @Valid UtenteDTO patchUtente,
    		BindingResult bindingResult,
    		@AuthenticationPrincipal UserWithAdditionalInfo user
    		) {
    	LOG.debug("UtenteFrontofficeRESTController --> PATCH utente." );
    	
    	// check permission
    	try {
    		securityService.checkUserValidity(user, true, true, false);
    		
    		securityService.checkIsDataOwner(patchUtente, user);
    	} catch (MyExtranetSecurityException e) {
    		LOG.error(String.format("Access denied for user %s", user.getUsername()), e);
    		return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_ACCESS_DENIED_TO_DATA_MESSAGE, e.getMessage(), Constants.API_ERROR_SECURITY_CODE), HttpStatus.FORBIDDEN);
    	}
    	
    	try {
    		validate(patchUtente, bindingResult);
            
            // check object does not have an Id
            if (patchUtente.getIdUtente() == null) {
            	LOG.error("UtenteFrontofficeRESTController --> PATCH utente usato impropriamente: l'oggetto non ha un ID");
            	return new ResponseEntity<ApiResult>(new ApiResult(Constants.RESULT_KO, "To use PATCH the object needs to have an ID", patchUtente), HttpStatus.BAD_REQUEST);
            }
            
            UtenteDTO utente = utenteService.updateUtente(patchUtente, true);
            LOG.debug("UtenteFrontofficeRESTController --> PATCH utente eseguito con successo.");

            return new ResponseEntity<ApiResult>(new ApiResult(Constants.RESULT_OK, "Successfully patched utente", utente), HttpStatus.OK);			
    	} catch (MyExtranetValidationException e) {
    		LOG.error("Validation exception", e);
    		return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_VALIDATION_MESSAGE, e.getMessage(), Constants.API_ERROR_VALIDATION_CODE), HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			LOG.error("UtenteFrontofficeRESTController --> PATCH utente non riuscito", e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_GENERIC_MESSAGE, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}

    }
    
    /**
     * Metodo PUT per modificare un utente
     * @return utente
     * @throws Exception
     */
    @Operation(
    		summary = "Metodo PUT per modificare un utente",
			responses = {
                    @ApiResponse(description = "PUT per modificare un utente",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Object.class))),
                    @ApiResponse(responseCode = "200", description = "Successfully updated utente"),
                    @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
                    @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
                    @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
            }
    )
    @PutMapping("")
    public ResponseEntity<ApiResult> updateUtente(
    		@RequestBody final @Valid UtenteDTO updateUtente,
    		BindingResult bindingResult,
    		@AuthenticationPrincipal UserWithAdditionalInfo user
    		) {
    	LOG.debug("UtenteFrontofficeRESTController --> PUT utente." );
    	
    	// check permission
    	try {
    		securityService.checkUserValidity(user, true, true, false);
    		
    		securityService.checkIsDataOwner(updateUtente, user);
    	} catch (MyExtranetSecurityException e) {
    		LOG.error(String.format("Access denied for user %s", user.getUsername()), e);
    		return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_ACCESS_DENIED_TO_DATA_MESSAGE, e.getMessage(), Constants.API_ERROR_SECURITY_CODE), HttpStatus.FORBIDDEN);
    	}
    	
    	try {
    		validate(updateUtente, bindingResult);
            
            // check object does not have an Id
            if (updateUtente.getIdUtente() == null) {
            	LOG.error("UtenteFrontofficeRESTController --> PUT utente usato impropriamente: l'oggetto non ha un ID");
            	return new ResponseEntity<ApiResult>(new ApiResult(Constants.RESULT_KO, "To use PUT the object needs to have an ID", updateUtente), HttpStatus.BAD_REQUEST);
            }
            
            UtenteDTO utente = utenteService.updateUtente(updateUtente, false);
            LOG.debug("UtenteFrontofficeRESTController --> PUT utente eseguito con successo.");

            return new ResponseEntity<ApiResult>(new ApiResult(Constants.RESULT_OK, "Successfully updated utente", utente), HttpStatus.OK);			
    	} catch (MyExtranetValidationException e) {
    		LOG.error("Validation exception", e);
    		return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_VALIDATION_MESSAGE, e.getMessage(), Constants.API_ERROR_VALIDATION_CODE), HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			LOG.error("UtenteFrontofficeRESTController --> PUT utente non riuscito", e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_GENERIC_MESSAGE, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}

    }
    
    @Override
    protected void validateDTO(Object dto, BindingResult bindingResult) {
    	UtenteDTO utente = (UtenteDTO) dto;
    	
    	//  check that at least one of (telefono, telefonoUff) is not empty
    	if (!StringUtils.hasText(utente.getTelefono()) && !StringUtils.hasText(utente.getTelefonoUff())) {
    		bindingResult.addError(new ObjectError("Telefono", "Indicare almeno un numero di telefono"));
    	}
    	
    	// check that at least one of (ente, azienda) is provided
    	if (utente.getEnte() == null && !StringUtils.hasText(utente.getAzienda())) {
    		bindingResult.addError(new ObjectError("Ente", "Indicare almeno uno tra ente e azienda"));
    	}
    	
    	// check that ente is valid
    	try {
			enteService.checkEnteIsValid(utente.getEnte());
		} catch (EnteExpiredException e) {
			bindingResult.addError(new ObjectError("Ente", "L'Ente selezionato è scaduto"));
		} catch (EnteNotPresentException e) {
			bindingResult.addError(new ObjectError("Ente", "L'Ente selezionato non esiste"));
		}
    }
}
