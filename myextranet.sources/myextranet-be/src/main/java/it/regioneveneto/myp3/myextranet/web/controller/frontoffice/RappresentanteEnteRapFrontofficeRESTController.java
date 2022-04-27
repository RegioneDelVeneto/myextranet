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

import java.sql.Date;
import java.time.LocalDate;
import java.util.UUID;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.regioneveneto.myp3.mybox.ContentMetadata;
import it.regioneveneto.myp3.myextranet.bean.ApiResult;
import it.regioneveneto.myp3.myextranet.exception.MyExtranetSecurityException;
import it.regioneveneto.myp3.myextranet.exception.MyExtranetValidationException;
import it.regioneveneto.myp3.myextranet.model.MyExtranetContent;
import it.regioneveneto.myp3.myextranet.security.UserWithAdditionalInfo;
import it.regioneveneto.myp3.myextranet.service.RappresentanteEnteService;
import it.regioneveneto.myp3.myextranet.service.SecurityService;
import it.regioneveneto.myp3.myextranet.utils.Constants;
import it.regioneveneto.myp3.myextranet.web.controller.ValidationController;
import it.regioneveneto.myp3.myextranet.web.dto.IscrittoEventoDTO;
import it.regioneveneto.myp3.myextranet.web.dto.PagedData;
import it.regioneveneto.myp3.myextranet.web.dto.RappresentanteEnteDTO;
import it.regioneveneto.myp3.myextranet.web.dto.RappresentanteEnteFilterDTO;

@RestController
@RequestMapping("/frontoffice/rappresentante-ente-rap")
@Tag(name = "tag_rappresentante-ente-rap", description = "Rappresentante RAP")
public class RappresentanteEnteRapFrontofficeRESTController extends ValidationController {
	private static final Logger LOG = LoggerFactory.getLogger(RappresentanteEnteRapFrontofficeRESTController.class);
	
    @Autowired
    private RappresentanteEnteService rappresentanteEnteService;
        
    @Autowired
    private SecurityService securityService;

    
    @GetMapping("/{_id}")
    public ResponseEntity<RappresentanteEnteDTO> getRappresentanteEnteById(
    		@PathVariable( name = "_id", required = true) final Integer idRappresentante,
    		@AuthenticationPrincipal UserWithAdditionalInfo user
    		) {
    	LOG.debug("RappresentanteEnteRapFrontofficeRESTController --> GET rappresentante with id " + idRappresentante);
    	
        // check permission
        try {
        	securityService.checkUserValidity(user);
		} catch (MyExtranetSecurityException e) {
			LOG.error(String.format("Access denied for user %s", user.getUsername()), e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_ACCESS_DENIED_TO_DATA_MESSAGE, e.getMessage(), Constants.API_ERROR_SECURITY_CODE), HttpStatus.FORBIDDEN);
		}

        RappresentanteEnteDTO rappresentante;
		try {
			rappresentante = rappresentanteEnteService.getRappresentanteEnte(idRappresentante);
			
			computeFlagAttivo(rappresentante);
			
			securityService.checkIsDataOwner(rappresentante, user);

			LOG.debug("RappresentanteEnteRapFrontofficeRESTController --> GET rappresentante eseguito con successo.");

        	return new ResponseEntity<RappresentanteEnteDTO>(rappresentante, HttpStatus.OK);
		} catch (MyExtranetSecurityException e) {
			LOG.error(String.format("Access denied for user %s", user.getUsername()), e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_ACCESS_DENIED_TO_DATA_MESSAGE, e.getMessage(), Constants.API_ERROR_SECURITY_CODE), HttpStatus.FORBIDDEN);
		} catch (Exception e) {
			LOG.error("RappresentanteEnteRapFrontofficeRESTController --> GET rappresentante non riuscito", e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_GENERIC_MESSAGE, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
    }

    @PostMapping("")
    public synchronized ResponseEntity<ApiResult> insertRappresentanteEnte(
    		@RequestPart(name = "newRappresentanteEnte", required = true) String newRappresentanteEnteStr,
    		BindingResult bindingResult,
    		@AuthenticationPrincipal UserWithAdditionalInfo user
    		) {
    	LOG.debug("RappresentanteEnteRapFrontofficeRESTController --> POST nuovo rappresentante");
    	
        // check permission
        try {
        	securityService.checkUserValidity(user);        	
        } catch (MyExtranetSecurityException e) {
			LOG.error(String.format("Access denied for user %s", user.getUsername()), e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_ACCESS_DENIED_TO_DATA_MESSAGE, e.getMessage(), Constants.API_ERROR_SECURITY_CODE), HttpStatus.FORBIDDEN);
		}
    	
    	try {	
    		
    		ObjectMapper mapper = new ObjectMapper();
    		RappresentanteEnteDTO newRappresentanteEnte = mapper.readValue(newRappresentanteEnteStr, RappresentanteEnteDTO.class);
    		
    		// validity managed by backend
    		newRappresentanteEnte.setDtInizioVal(null);
    		newRappresentanteEnte.setDtFineVal(null);   		    		
        	securityService.checkIsDataOwner(newRappresentanteEnte, user);
    		
    		validate(newRappresentanteEnte, bindingResult);
            
            // check object does not have an Id
            if (newRappresentanteEnte.getIdRappr() != null) {
            	LOG.error("RappresentanteEnteRapFrontofficeRESTController --> POST nuovo rappresentante usato impropriamente: l'oggetto ha un ID");
            	return new ResponseEntity<ApiResult>(new ApiResult(Constants.RESULT_KO, "POST method is not for updates. Use PUT or PATCH instead.", newRappresentanteEnte), HttpStatus.BAD_REQUEST);
            }
            
            // true if from frontoffice
            boolean notValidYet = true;
            
            // set correct type
            newRappresentanteEnte.setTipoRappr(Constants.RAPPRESENTANTE_ENTE_TIPO_RAP);
            
            RappresentanteEnteDTO rappresentante = rappresentanteEnteService.insertRappresentanteEnte(newRappresentanteEnte, notValidYet);
                        
            LOG.debug("RappresentanteEnteRapFrontofficeRESTController --> POST nuovo rappresentante eseguito con successo.");

            return new ResponseEntity<ApiResult>(new ApiResult(Constants.RESULT_OK, "Successfully created rappresentante", rappresentante), HttpStatus.CREATED);			
    	} catch (MyExtranetValidationException e) {
    		LOG.error("Validation exception", e);
    		return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_VALIDATION_MESSAGE, e.getMessage(), Constants.API_ERROR_VALIDATION_CODE), HttpStatus.BAD_REQUEST);
    	} catch (MyExtranetSecurityException e) {
    		LOG.error(String.format("Access denied for user %s", user.getUsername()), e);
    		return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_ACCESS_DENIED_TO_DATA_MESSAGE, e.getMessage(), Constants.API_ERROR_SECURITY_CODE), HttpStatus.FORBIDDEN);
		} catch (Exception e) {
			LOG.error("RappresentanteEnteRapFrontofficeRESTController --> POST nuovo rappresentante non riuscito", e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_GENERIC_MESSAGE, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}

    }

    /**
     * Metodo PATCH per modificare parzialmente un rappresentante
     * @return rappresentante
     * @throws Exception
     */
    @Operation(
    		summary = "Metodo PATCH per modificare parzialmente un rappresentante",
			responses = {
                    @ApiResponse(description = "PATCH per modificare parzialmente un rappresentante",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Object.class))),
                    @ApiResponse(responseCode = "200", description = "Successfully patched rappresentante"),
                    @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
                    @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
                    @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
            }
    )
    @PatchMapping("")
    public ResponseEntity<ApiResult> patchRappresentanteEnte(
    		@RequestPart(name = "newRappresentanteEnte", required = true) String newRappresentanteEnteStr,
    		@AuthenticationPrincipal UserWithAdditionalInfo user
    		) {
    	LOG.debug("RappresentanteEnteRapFrontofficeRESTController --> PATCH rappresentante." );
    	
        // check permission
        try {
        	securityService.checkUserValidity(user);
		} catch (MyExtranetSecurityException e) {
			LOG.error(String.format("Access denied for user %s", user.getUsername()), e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_ACCESS_DENIED_TO_DATA_MESSAGE, e.getMessage(), Constants.API_ERROR_SECURITY_CODE), HttpStatus.FORBIDDEN);
		}
    	
    	try {
    		ObjectMapper mapper = new ObjectMapper();
    		RappresentanteEnteDTO patchRappresentanteEnte = mapper.readValue(newRappresentanteEnteStr, RappresentanteEnteDTO.class);
    		
    		// validity managed by backend
    		patchRappresentanteEnte.setDtInizioVal(null);
    		patchRappresentanteEnte.setDtFineVal(null);    		    		
    		
    		securityService.checkIsDataOwner(patchRappresentanteEnte, user);
            
            // check object does not have an Id
            if (patchRappresentanteEnte.getIdRappr() == null) {
            	LOG.error("RappresentanteEnteRapFrontofficeRESTController --> PATCH rappresentante usato impropriamente: l'oggetto non ha un ID");
            	return new ResponseEntity<ApiResult>(new ApiResult(Constants.RESULT_KO, "To use PATCH the object needs to have an ID", patchRappresentanteEnte), HttpStatus.BAD_REQUEST);
            }
            
            // force correct type
            patchRappresentanteEnte.setTipoRappr(Constants.RAPPRESENTANTE_ENTE_TIPO_RAP);
            
            // true in frontoffice
            boolean isFrontoffice = true;
            
            RappresentanteEnteDTO rappresentante = rappresentanteEnteService.updateRappresentanteEnte(patchRappresentanteEnte, true, isFrontoffice, false);
            LOG.debug("RappresentanteEnteRapFrontofficeRESTController --> PATCH rappresentante eseguito con successo.");

            return new ResponseEntity<ApiResult>(new ApiResult(Constants.RESULT_OK, "Successfully patched rappresentante", rappresentante), HttpStatus.OK);			
    	} catch (MyExtranetSecurityException e) {
    		LOG.error(String.format("Access denied for user %s", user.getUsername()), e);
    		return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_ACCESS_DENIED_TO_DATA_MESSAGE, e.getMessage(), Constants.API_ERROR_SECURITY_CODE), HttpStatus.FORBIDDEN);
		} catch (Exception e) {
			LOG.error("RappresentanteEnteRapFrontofficeRESTController --> PATCH rappresentante non riuscito", e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_GENERIC_MESSAGE, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}

    }  
    
    /**
     * Metodo PATCH per modificare parzialmente un rappresentante
     * @return rappresentante
     * @throws Exception
     */
    @Operation(
    		summary = "Metodo PATCH per modificare parzialmente un rappresentante",
			responses = {
                    @ApiResponse(description = "PATCH per modificare parzialmente un rappresentante",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Object.class))),
                    @ApiResponse(responseCode = "200", description = "Successfully patched rappresentante"),
                    @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
                    @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
                    @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
            }
    )
    @PatchMapping("/risottomissione")
    public ResponseEntity<ApiResult> patchRappresentanteEnteRisottomissione(
    		@RequestPart(name = "newRappresentanteEnte", required = true) String newRappresentanteEnteStr,
    		@AuthenticationPrincipal UserWithAdditionalInfo user
    		) {
    	LOG.debug("RappresentanteEnteRapFrontofficeRESTController --> PATCH rappresentante (risottomissione)." );
    	
        // check permission
        try {
        	securityService.checkUserValidity(user);
		} catch (MyExtranetSecurityException e) {
			LOG.error(String.format("Access denied for user %s", user.getUsername()), e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_ACCESS_DENIED_TO_DATA_MESSAGE, e.getMessage(), Constants.API_ERROR_SECURITY_CODE), HttpStatus.FORBIDDEN);
		}
    	
    	try {
    		ObjectMapper mapper = new ObjectMapper();
    		RappresentanteEnteDTO patchRappresentanteEnte = mapper.readValue(newRappresentanteEnteStr, RappresentanteEnteDTO.class);
    		
    		// validity managed by backend
    		patchRappresentanteEnte.setDtInizioVal(null);
    		patchRappresentanteEnte.setDtFineVal(null);    		    	
    		
    		securityService.checkIsDataOwner(patchRappresentanteEnte, user);
            
            // check object does not have an Id
            if (patchRappresentanteEnte.getIdRappr() == null) {
            	LOG.error("RappresentanteEnteRapFrontofficeRESTController --> PATCH rappresentante usato impropriamente: l'oggetto non ha un ID");
            	return new ResponseEntity<ApiResult>(new ApiResult(Constants.RESULT_KO, "To use PATCH the object needs to have an ID", patchRappresentanteEnte), HttpStatus.BAD_REQUEST);
            }
            
            // force correct type
            patchRappresentanteEnte.setTipoRappr(Constants.RAPPRESENTANTE_ENTE_TIPO_RAP);
            
            // true in frontoffice
            boolean isFrontoffice = true;
            
            RappresentanteEnteDTO rappresentante = rappresentanteEnteService.updateRappresentanteEnte(patchRappresentanteEnte, true, isFrontoffice, true);
            LOG.debug("RappresentanteEnteRapFrontofficeRESTController --> PATCH rappresentante (risottomissione) eseguito con successo.");

            return new ResponseEntity<ApiResult>(new ApiResult(Constants.RESULT_OK, "Successfully patched rappresentante (risottomissione)", rappresentante), HttpStatus.OK);			
    	} catch (MyExtranetSecurityException e) {
    		LOG.error(String.format("Access denied for user %s", user.getUsername()), e);
    		return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_ACCESS_DENIED_TO_DATA_MESSAGE, e.getMessage(), Constants.API_ERROR_SECURITY_CODE), HttpStatus.FORBIDDEN);
		} catch (Exception e) {
			LOG.error("RappresentanteEnteRapFrontofficeRESTController --> PATCH rappresentante (risottomissione) non riuscito", e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_GENERIC_MESSAGE, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}

    }  


    /**
     * Metodo PUT per modificare un rappresentante
     * @return rappresentante
     * @throws Exception
     */
    @Operation(
    		summary = "Metodo PUT per modificare un rappresentante",
			responses = {
                    @ApiResponse(description = "PUT per modificare un rappresentante",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Object.class))),
                    @ApiResponse(responseCode = "200", description = "Successfully updated rappresentante"),
                    @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
                    @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
                    @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
            }
    )
    @PutMapping("")
    public ResponseEntity<ApiResult> updateRappresentanteEnte(
    		@RequestPart(name = "newRappresentanteEnte", required = true) String newRappresentanteEnteStr,
    		BindingResult bindingResult,
    		@AuthenticationPrincipal UserWithAdditionalInfo user
    		) {
    	LOG.debug("RappresentanteEnteRapFrontofficeRESTController --> PUT rappresentante." );
    	
    	// check permission
    	try {
    		securityService.checkUserValidity(user);
    	} catch (MyExtranetSecurityException e) {
    		LOG.error(String.format("Access denied for user %s", user.getUsername()), e);
    		return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_ACCESS_DENIED_TO_DATA_MESSAGE, e.getMessage(), Constants.API_ERROR_SECURITY_CODE), HttpStatus.FORBIDDEN);
    	}
    	
    	try {
    		
    		ObjectMapper mapper = new ObjectMapper();
    		RappresentanteEnteDTO updateRappresentanteEnte = mapper.readValue(newRappresentanteEnteStr, RappresentanteEnteDTO.class);
    		
    		// validity managed by backend
    		updateRappresentanteEnte.setDtInizioVal(null);
    		updateRappresentanteEnte.setDtFineVal(null);
    		
    		securityService.checkIsDataOwner(updateRappresentanteEnte, user);
    		
    		validate(updateRappresentanteEnte, bindingResult);
            
            // check object does not have an Id
            if (updateRappresentanteEnte.getIdRappr() == null) {
            	LOG.error("RappresentanteEnteRapFrontofficeRESTController --> PUT rappresentante usato impropriamente: l'oggetto non ha un ID");
            	return new ResponseEntity<ApiResult>(new ApiResult(Constants.RESULT_KO, "To use PUT the object needs to have an ID", updateRappresentanteEnte), HttpStatus.BAD_REQUEST);
            }
            
            // force correct type
            updateRappresentanteEnte.setTipoRappr(Constants.RAPPRESENTANTE_ENTE_TIPO_RAP);
            
            // true in frontoffice
            boolean isFrontoffice = true;
            
            RappresentanteEnteDTO rappresentante = rappresentanteEnteService.updateRappresentanteEnte(updateRappresentanteEnte, false, isFrontoffice, false);
            LOG.debug("RappresentanteEnteRapFrontofficeRESTController --> PUT rappresentante eseguito con successo.");

            return new ResponseEntity<ApiResult>(new ApiResult(Constants.RESULT_OK, "Successfully updated rappresentante", rappresentante), HttpStatus.OK);			
    	} catch (MyExtranetValidationException e) {
    		LOG.error("Validation exception", e);
    		return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_VALIDATION_MESSAGE, e.getMessage(), Constants.API_ERROR_VALIDATION_CODE), HttpStatus.BAD_REQUEST);
    	} catch (MyExtranetSecurityException e) {
    		LOG.error(String.format("Access denied for user %s", user.getUsername()), e);
    		return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_ACCESS_DENIED_TO_DATA_MESSAGE, e.getMessage(), Constants.API_ERROR_SECURITY_CODE), HttpStatus.FORBIDDEN);
		} catch (Exception e) {
			LOG.error("RappresentanteEnteRapFrontofficeRESTController --> PUT rappresentante non riuscito", e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_GENERIC_MESSAGE, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}

    }
    
    /**
     * Metodo GET per recuperare gli enti rappresentati dall'utente
     * @return collaboratori
     * @throws Exception
     */
    @Operation(
    		summary = "Metodo GET per recuperare gli enti rappresentati dall'utente",
			responses = {
                    @ApiResponse(description = "GET per recuperare gli enti rappresentati dall'utente",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Object.class))),
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved enti"),
                    @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
                    @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
                    @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
            }
    )
    @GetMapping("/enti-rappresentati")
    public ResponseEntity<PagedData<RappresentanteEnteDTO>> getEntiRappresentati(
    		@RequestParam( name = "_page", required = false, defaultValue = "0" ) final Integer pageNumber,
    		@RequestParam( name = "_pageSize", required = false, defaultValue = "10" ) final Integer pageSize,
    		@RequestParam( name = "_orderBy", required = false ) final String orderProperty,
    		@RequestParam( name = "_orderDir", required = false ) final String orderDirection,
    		@AuthenticationPrincipal UserWithAdditionalInfo user
    		) {
    	
        LOG.debug("RappresentanteEnteRapFrontofficeRESTController --> GET Lista enti rappresentati dall'utente");
        
        // check permission
        try {
        	securityService.checkUserValidity(user);
		} catch (MyExtranetSecurityException e) {
			LOG.error(String.format("Access denied for user %s", user.getUsername()), e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_ACCESS_DENIED_TO_DATA_MESSAGE, e.getMessage(), Constants.API_ERROR_SECURITY_CODE), HttpStatus.FORBIDDEN);
		}

        PagedData<RappresentanteEnteDTO> enti = rappresentanteEnteService.getEntiRappresentati(user, pageNumber, pageSize, orderProperty, orderDirection);
        LOG.debug("RappresentanteEnteRapFrontofficeRESTController --> GET Lista enti rappresentati dall'utente eseguito con successo.");

        return new ResponseEntity<PagedData<RappresentanteEnteDTO>>(enti, HttpStatus.OK);

    }    
    
	private void computeFlagAttivo(RappresentanteEnteDTO rappresentante) {
		
		Date today = Date.valueOf(LocalDate.now());
		
		boolean isValid = 
				rappresentante.getDtInizioVal() != null && rappresentante.getDtFineVal() != null &&
				rappresentante.getDtInizioVal().compareTo(today) <= 0 &&
				rappresentante.getDtFineVal().compareTo(today) > 0;

		rappresentante.setFlgAttivo(Constants.RAPPRESENTANTE_ENTE_FLG_CONFERMA_CONFERMATA.equals(rappresentante.getFlgConferma()) && isValid);

	}

    @Override
    protected void validateDTO(Object dto, BindingResult bindingResult) {
    	RappresentanteEnteDTO rappresentante = (RappresentanteEnteDTO) dto;
    	// utente
    	if (rappresentante.getUtente() == null) {
    		bindingResult.addError(new ObjectError("Utente", "Utente è obbligatorio"));

    	}
    	// ente
    	if (rappresentante.getEnte() == null) {
    		bindingResult.addError(new ObjectError("Ente", "Ente è obbligatorio"));

    	}
    
    }

	public RappresentanteEnteRapFrontofficeRESTController() {
		
	}

}
