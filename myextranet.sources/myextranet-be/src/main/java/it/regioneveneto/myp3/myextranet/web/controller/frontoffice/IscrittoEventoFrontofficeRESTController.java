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

import java.io.ByteArrayInputStream;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
import it.regioneveneto.myp3.myextranet.exception.MyExtranetValidationException;
import it.regioneveneto.myp3.myextranet.model.MyExtranetContent;
import it.regioneveneto.myp3.myextranet.security.UserWithAdditionalInfo;
import it.regioneveneto.myp3.myextranet.service.IscrittoEventoService;
import it.regioneveneto.myp3.myextranet.service.MyPortalService;
import it.regioneveneto.myp3.myextranet.service.SecurityService;
import it.regioneveneto.myp3.myextranet.utils.Constants;
import it.regioneveneto.myp3.myextranet.web.controller.ValidationController;
import it.regioneveneto.myp3.myextranet.web.dto.DettaglioCollaborazioneDTO;
import it.regioneveneto.myp3.myextranet.web.dto.DettaglioIscrizioneDTO;
import it.regioneveneto.myp3.myextranet.web.dto.IscrittoEventoDTO;
import it.regioneveneto.myp3.myextranet.web.dto.IscrittoEventoDettaglioFilterDTO;
import it.regioneveneto.myp3.myextranet.web.dto.PagedData;

@RestController
@RequestMapping("/frontoffice/iscritto-evento")
@Tag(name = "tag_iscritto-evento", description = "Iscritto")
public class IscrittoEventoFrontofficeRESTController extends ValidationController {
    private static final Logger LOG = LoggerFactory.getLogger(IscrittoEventoFrontofficeRESTController.class);

    @Autowired
    private IscrittoEventoService iscrittoEventoService;
    
    @Autowired
    private MyPortalService myPortalService;
    
    @Autowired
    private SecurityService securityService;


    /**
     * Metodo GET per recuperare gli eventi dell'utente
     * @return collaboratori
     * @throws Exception
     */
    @Operation(
    		summary = "Metodo GET per recuperare gli eventi dell'utente",
			responses = {
                    @ApiResponse(description = "GET per recuperare gli eventi dell'utente",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Object.class))),
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved progetti"),
                    @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
                    @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
                    @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
            }
    )
    @GetMapping("/eventi")
    public ResponseEntity<PagedData<MyExtranetContent>> getEventiIscritti(
    		@RequestParam( name = "_page", required = false, defaultValue = "0" ) final Integer pageNumber,
    		@RequestParam( name = "_pageSize", required = false, defaultValue = "10" ) final Integer pageSize,
    		@RequestParam( name = "_orderBy", required = false ) final String orderProperty,
    		@RequestParam( name = "_orderDir", required = false ) final String orderDirection,
    		@AuthenticationPrincipal UserWithAdditionalInfo user
    		) {
    	
        LOG.debug("IscrittoEventoFrontofficeRESTController --> GET Lista eventi");
        
        // check permission
        try {
        	securityService.checkUserValidity(user);
		} catch (MyExtranetSecurityException e) {
			LOG.error(String.format("Access denied for user %s", user.getUsername()), e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_ACCESS_DENIED_TO_DATA_MESSAGE, e.getMessage(), Constants.API_ERROR_SECURITY_CODE), HttpStatus.FORBIDDEN);
		}

        PagedData<MyExtranetContent> progetti = iscrittoEventoService.getEventiIscritti(user, pageNumber, pageSize, orderProperty, orderDirection);
        LOG.debug("IscrittoEventoFrontofficeRESTController --> GET Lista eventi eseguito con successo.");

        return new ResponseEntity<PagedData<MyExtranetContent>>(progetti, HttpStatus.OK);

    }    
    
    /**
     * Metodo GET per recuperare i dati di un'iscrizione
     * @return collaboratori
     * @throws Exception
     */
    @Operation(
    		summary = "Metodo GET per recuperare i dati di un'iscrizione",
			responses = {
                    @ApiResponse(description = "GET per recuperare i dati di un'iscrizione",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Object.class))),
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved iscrizione"),
                    @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
                    @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
                    @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
            }
    )
    @GetMapping("/iscrizione")
    public ResponseEntity<DettaglioIscrizioneDTO> getIscrizioneEventoIscritto(
    		@ModelAttribute( name = "_filter" ) IscrittoEventoDettaglioFilterDTO iscrittoEventoDettaglioFilter,
    		@AuthenticationPrincipal UserWithAdditionalInfo user
    		) {
    	
        LOG.debug("IscrittoEventoFrontofficeRESTController --> GET iscrizione");
        
        // check permission
        try {
        	securityService.checkUserValidity(user);
		} catch (MyExtranetSecurityException e) {
			LOG.error(String.format("Access denied for user %s", user.getUsername()), e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_ACCESS_DENIED_TO_DATA_MESSAGE, e.getMessage(), Constants.API_ERROR_SECURITY_CODE), HttpStatus.FORBIDDEN);
		}
        
        // check filter
        if (iscrittoEventoDettaglioFilter.getIdIscritto() == null && iscrittoEventoDettaglioFilter.getIdEvento() == null) {
        	LOG.error("Invalid IscrittoEventoDettaglioFilterDTO");
        	return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_GENERIC_MESSAGE, "Invalid IscrittoEventoDettaglioFilterDTO", Constants.API_ERROR_GENERIC_CODE), HttpStatus.BAD_REQUEST);
        }

        DettaglioIscrizioneDTO iscrizione;
		try {
			iscrizione = iscrittoEventoService.getDettaglioIscrizione(iscrittoEventoDettaglioFilter, user);
			
			securityService.checkIsDataOwner(iscrizione.getIscrittoEvento(), user);
		} catch (MyExtranetSecurityException e) {
			LOG.error(String.format("Access denied for user %s", user.getUsername()), e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_ACCESS_DENIED_TO_DATA_MESSAGE, e.getMessage(), Constants.API_ERROR_SECURITY_CODE), HttpStatus.FORBIDDEN);
		} catch (Exception e) {
			LOG.error("Error retrieving iscrizione", e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_GENERIC_MESSAGE, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
        LOG.debug("IscrittoEventoFrontofficeRESTController --> GET iscrizione eseguito con successo.");

        return new ResponseEntity<DettaglioIscrizioneDTO>(iscrizione, HttpStatus.OK);

    }    

    
    /**
     * Metodo GET per recuperare un iscritto tramite id
     * @return enti
     * @throws Exception
     */
    @Operation(
    		summary = "Metodo GET per un iscritto tramite id",
			responses = {
                    @ApiResponse(description = "GET per recuperare un iscritto tramite id",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Object.class))),
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved iscritto"),
                    @ApiResponse(responseCode = "400", description = "Iscritto not found"),
                    @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
                    @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
                    @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
            }
    )
    @GetMapping("/{_id}")
    public ResponseEntity<IscrittoEventoDTO> getIscrittoById(
    		@PathVariable( name = "_id", required = true) final Integer iscrittoId,
    		@AuthenticationPrincipal UserWithAdditionalInfo user
    		) {
    	LOG.debug("IscrittoEventoFrontofficeRESTController --> GET iscritto with id " + iscrittoId);
    	
        // check permission
        try {
        	securityService.checkUserValidity(user);
		} catch (MyExtranetSecurityException e) {
			LOG.error(String.format("Access denied for user %s", user.getUsername()), e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_ACCESS_DENIED_TO_DATA_MESSAGE, e.getMessage(), Constants.API_ERROR_SECURITY_CODE), HttpStatus.FORBIDDEN);
		}

        IscrittoEventoDTO iscritto;
		try {
			iscritto = iscrittoEventoService.getIscrittoEvento(iscrittoId);
			
			securityService.checkIsDataOwner(iscritto, user);

			LOG.debug("IscrittoEventoFrontofficeRESTController --> GET iscritto eseguito con successo.");

        	return new ResponseEntity<IscrittoEventoDTO>(iscritto, HttpStatus.OK);
		} catch (MyExtranetSecurityException e) {
			LOG.error(String.format("Access denied for user %s", user.getUsername()), e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_ACCESS_DENIED_TO_DATA_MESSAGE, e.getMessage(), Constants.API_ERROR_SECURITY_CODE), HttpStatus.FORBIDDEN);
		} catch (Exception e) {
			LOG.error("IscrittoEventoFrontofficeRESTController --> GET iscritto non riuscito", e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_GENERIC_MESSAGE, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
    }
    
    /**
     * Metodo DELETE per eliminare un iscritto
     * @return iscritto eliminato
     * @throws Exception
     */
    @Operation(
    		summary = "Metodo DELETE per eliminare un iscritto",
			responses = {
                    @ApiResponse(description = "DELETE per eliminare un iscritto",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Object.class))),
                    @ApiResponse(responseCode = "200", description = "Successfully deleted iscritto"),
                    @ApiResponse(responseCode = "400", description = "Iscritto not found"),
                    @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
                    @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
                    @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
            }
    )
    @DeleteMapping("/{_id}")
    public ResponseEntity<ApiResult> deleteIscrittoById(
    		@PathVariable( name = "_id", required = true) final Integer iscrittoId,
    		@AuthenticationPrincipal UserWithAdditionalInfo user
    		) {
    	LOG.debug("IscrittoEventoRESTController --> DELETE iscritto with id " + iscrittoId);
    	
        // check permission
        try {
        	securityService.checkUserValidity(user);
		} catch (MyExtranetSecurityException e) {
			LOG.error(String.format("Access denied for user %s", user.getUsername()), e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_ACCESS_DENIED_TO_DATA_MESSAGE, e.getMessage(), Constants.API_ERROR_SECURITY_CODE), HttpStatus.FORBIDDEN);
		}

        IscrittoEventoDTO iscritto;
		try {
			iscritto = iscrittoEventoService.getIscrittoEvento(iscrittoId);
			
			securityService.checkIsDataOwner(iscritto, user);
			
			iscritto = iscrittoEventoService.deleteIscrittoEvento(iscrittoId);
			
			LOG.debug("IscrittoEventoRESTController --> DELETE iscritto eseguito con successo.");

			return new ResponseEntity<ApiResult>(new ApiResult(Constants.RESULT_OK, "Successfully deleted iscritto", iscritto), HttpStatus.OK);
		} catch (MyExtranetSecurityException e) {
			LOG.error(String.format("Access denied for user %s", user.getUsername()), e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_ACCESS_DENIED_TO_DATA_MESSAGE, e.getMessage(), Constants.API_ERROR_SECURITY_CODE), HttpStatus.FORBIDDEN);
		} catch (Exception e) {
			LOG.error("IscrittoEventoRESTController --> DELETE iscritto non riuscito", e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_GENERIC_MESSAGE, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
    }

    
    /**
     * Generate report "attestato partecipazione"
     * 
     * @param idEvento
     * @param idIscritto
     * @return
     */
    @Operation(
    		summary = "Metodo GET per generare un attestato",
			responses = {
                    @ApiResponse(description = "GET per generare un attestato",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Object.class))),
                    @ApiResponse(responseCode = "200", description = "Successfully generated report"),
                    @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
                    @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
                    @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
            }
    )
    @GetMapping("/attestato/{idIscritto}")
    public ResponseEntity<InputStreamResource> getAttestato(
    		@PathVariable final Integer idIscritto,
    		@AuthenticationPrincipal UserWithAdditionalInfo user
    		) {
    	
    	try {
    		LOG.debug("IscrittoEventoFrontofficeRESTController --> GET attestato");
    		
            // check permission
            try {
            	securityService.checkUserValidity(user);
            	
            	IscrittoEventoDTO iscritto = iscrittoEventoService.getIscrittoEvento(idIscritto);
            	securityService.checkIsDataOwner(iscritto, user);
    		} catch (MyExtranetSecurityException e) {
    			LOG.error(String.format("Access denied for user %s", user.getUsername()), e);
    			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_ACCESS_DENIED_TO_DATA_MESSAGE, e.getMessage(), Constants.API_ERROR_SECURITY_CODE), HttpStatus.FORBIDDEN);
    		}
            
    		ByteArrayInputStream bis = iscrittoEventoService.generateReportAttestato(idIscritto);
    		LOG.debug("IscrittoEventoFrontofficeRESTController --> GET attestato eseguito con successo.");
    		
    		var headers = new HttpHeaders();
    		headers.add("Content-Disposition", "inline; filename=attestato.pdf");
    		
    		return ResponseEntity
    				.ok()
    				.headers(headers)
    				.contentType(MediaType.APPLICATION_PDF)
    				.body(new InputStreamResource(bis));
		} catch (Exception e) {
			LOG.error("IscrittoEventoFrontofficeRESTController --> GET attestato non riuscito", e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_GENERIC_MESSAGE, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}

    }

    
    /**
     * 
     * @param newIscrittoEvento
     * @return
     */
    @Operation(
    		summary = "Metodo POST per inserire un iscritto",
			responses = {
                    @ApiResponse(description = "POST per inserire un iscritto",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Object.class))),
                    @ApiResponse(responseCode = "201", description = "Successfully created iscritto"),
                    @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
                    @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
                    @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
            }
    )
    @PostMapping("")
    public synchronized ResponseEntity<ApiResult> insertIscrittoEvento(
    		@RequestBody final @Valid IscrittoEventoDTO newIscrittoEvento,
    		BindingResult bindingResult,
    		@AuthenticationPrincipal UserWithAdditionalInfo user
    		) {
    	LOG.debug("IscrittoEventoFrontofficeRESTController --> POST nuovo iscritto");
    	
        // check permission
        try {
        	securityService.checkUserValidity(user);
        	
        	securityService.checkIsDataOwner(newIscrittoEvento, user);
		} catch (MyExtranetSecurityException e) {
			LOG.error(String.format("Access denied for user %s", user.getUsername()), e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_ACCESS_DENIED_TO_DATA_MESSAGE, e.getMessage(), Constants.API_ERROR_SECURITY_CODE), HttpStatus.FORBIDDEN);
		}
    	
    	try {
    		
    		validate(newIscrittoEvento, bindingResult);
            
            // check object does not have an Id
            if (newIscrittoEvento.getIdIscritto() != null) {
            	LOG.error("IscrittoEventoFrontofficeRESTController --> POST nuovo iscritto usato impropriamente: l'oggetto ha un ID");
            	return new ResponseEntity<ApiResult>(new ApiResult(Constants.RESULT_KO, "POST method is not for updates. Use PUT or PATCH instead.", newIscrittoEvento), HttpStatus.BAD_REQUEST);
            }
            
            IscrittoEventoDTO iscritto = iscrittoEventoService.insertIscrittoEvento(newIscrittoEvento);
            LOG.debug("IscrittoEventoFrontofficeRESTController --> POST nuovo iscritto eseguito con successo.");

            return new ResponseEntity<ApiResult>(new ApiResult(Constants.RESULT_OK, "Successfully created iscritto", iscritto), HttpStatus.CREATED);			
    	} catch (MyExtranetValidationException e) {
    		LOG.error("Validation exception", e);
    		return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_VALIDATION_MESSAGE, e.getMessage(), Constants.API_ERROR_VALIDATION_CODE), HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			LOG.error("IscrittoEventoFrontofficeRESTController --> POST nuovo iscritto non riuscito", e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_GENERIC_MESSAGE, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}

    }

    /**
     * Metodo PATCH per modificare parzialmente un iscritto
     * @return iscritto
     * @throws Exception
     */
    @Operation(
    		summary = "Metodo PATCH per modificare parzialmente un iscritto",
			responses = {
                    @ApiResponse(description = "PATCH per modificare parzialmente un iscritto",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Object.class))),
                    @ApiResponse(responseCode = "200", description = "Successfully patched iscritto"),
                    @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
                    @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
                    @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
            }
    )
    @PatchMapping("")
    public ResponseEntity<ApiResult> patchIscrittoEvento(
    		@RequestBody final IscrittoEventoDTO patchIscrittoEvento,
    		@AuthenticationPrincipal UserWithAdditionalInfo user
    		) {
    	LOG.debug("IscrittoEventoFrontofficeRESTController --> PATCH iscritto." );
    	
        // check permission
        try {
        	securityService.checkUserValidity(user);
        	
        	securityService.checkIsDataOwner(patchIscrittoEvento, user);
		} catch (MyExtranetSecurityException e) {
			LOG.error(String.format("Access denied for user %s", user.getUsername()), e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_ACCESS_DENIED_TO_DATA_MESSAGE, e.getMessage(), Constants.API_ERROR_SECURITY_CODE), HttpStatus.FORBIDDEN);
		}
    	
    	try {
            
            // check object does not have an Id
            if (patchIscrittoEvento.getIdIscritto() == null) {
            	LOG.error("IscrittoEventoFrontofficeRESTController --> PATCH iscritto usato impropriamente: l'oggetto non ha un ID");
            	return new ResponseEntity<ApiResult>(new ApiResult(Constants.RESULT_KO, "To use PATCH the object needs to have an ID", patchIscrittoEvento), HttpStatus.BAD_REQUEST);
            }
            
            IscrittoEventoDTO utente = iscrittoEventoService.updateIscrittoEvento(patchIscrittoEvento, true);
            LOG.debug("IscrittoEventoFrontofficeRESTController --> PATCH iscritto eseguito con successo.");

            return new ResponseEntity<ApiResult>(new ApiResult(Constants.RESULT_OK, "Successfully patched iscritto", utente), HttpStatus.OK);			
		} catch (Exception e) {
			LOG.error("IscrittoEventoFrontofficeRESTController --> PATCH iscritto non riuscito", e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_GENERIC_MESSAGE, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}

    }  
    
    
    @Override
    protected void validateDTO(Object dto, BindingResult bindingResult) {
    	IscrittoEventoDTO iscritto = (IscrittoEventoDTO) dto;
    	// nome + cognome + email OR utente
    	if (iscritto.getUtente() == null) {
    		if (!StringUtils.hasText(iscritto.getNome())) {
    			bindingResult.addError(new ObjectError("Nome", "Nome è obbligatorio"));
    		}
    		if (!StringUtils.hasText(iscritto.getCognome())) {
    			bindingResult.addError(new ObjectError("Cognome", "Cognome è obbligatorio"));
    		}
    		if (!StringUtils.hasText(iscritto.getEmail())) {
    			bindingResult.addError(new ObjectError("Email", "Email è obbligatorio"));
    		}
    	}
    	
    	// azienda OR ente OR neither one
    	if (iscritto.getEnte() != null && StringUtils.hasText(iscritto.getAzienda())) {
    		bindingResult.addError(new ObjectError("Azienda", "Specificare solo uno tra Ente e Azienda"));
    	}
       	
       	MyExtranetContent event = myPortalService.getContentById(iscritto.getIdEvento());
       	// check if event has been canceled
       	boolean isCanceled = event.isAnnullato();
       	if (isCanceled) {
       		bindingResult.addError(new ObjectError("idEvento", "Questo evento è stato annullato"));
       		return;
       	}
       	
       	// check if seats still available
       	boolean prefInPresenza = "PRE".equals(iscritto.getFlgPartecipPref());
       	boolean eventInPresenza = event.isInPresenza();
       	boolean eventInStreaming = event.isInStreaming();
       	Integer maxPartecipanti = event.getNumeroMaxPartecipanti();
       	boolean limitedAttendance = maxPartecipanti >= 0;
       	if (!eventInPresenza && prefInPresenza) {
       		bindingResult.addError(new ObjectError("flgPartecipPref", "Questo evento non prevede la partecipazione in presenza"));
       	} else if (eventInPresenza && !eventInStreaming && limitedAttendance) {
       		// check availability
       		Integer numIscritti = iscrittoEventoService.getNumIscritti(iscritto.getIdEvento());
       		if (numIscritti >= maxPartecipanti) {
       			bindingResult.addError(new ObjectError("flgPartecipPref", "Superato il numero massimo di partecipanti per questo evento"));
       		}
       	} else if (eventInPresenza && prefInPresenza && limitedAttendance) {       		
       		// check availability
       		Integer numIscritti = iscrittoEventoService.getNumIscrittiInPresenza(iscritto.getIdEvento());
       		if (numIscritti >= maxPartecipanti) {
       			bindingResult.addError(new ObjectError("flgPartecipPref", "Superato il numero massimo di partecipanti in presenza per questo evento"));
       		}
       	}
    }
    
}
