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

import java.io.ByteArrayInputStream;
import java.time.LocalDate;

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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

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
import it.regioneveneto.myp3.myextranet.service.CollaboratoreProgettoService;
import it.regioneveneto.myp3.myextranet.service.CollaboratoreProgettoService;
import it.regioneveneto.myp3.myextranet.service.MyPortalService;
import it.regioneveneto.myp3.myextranet.service.SecurityService;
import it.regioneveneto.myp3.myextranet.utils.Constants;
import it.regioneveneto.myp3.myextranet.web.dto.CollaboratoreProgettoDTO;
import it.regioneveneto.myp3.myextranet.web.dto.CollaboratoreProgettoFilterDTO;
import it.regioneveneto.myp3.myextranet.web.dto.CollaboratoreProgettoDTO;
import it.regioneveneto.myp3.myextranet.web.dto.CollaboratoreProgettoFilterDTO;
import it.regioneveneto.myp3.myextranet.web.dto.MessaggioDTO;
import it.regioneveneto.myp3.myextranet.web.dto.OperazioneMassivaDTO;
import it.regioneveneto.myp3.myextranet.web.dto.PagedData;
import it.regioneveneto.myp3.myextranet.web.dto.UtenteFilterDTO;

@RestController
@RequestMapping("/collaboratore-progetto")
@Tag(name = "tag_collaboratore-progetto", description = "Collaboratore")
public class CollaboratoreProgettoRESTController extends ValidationController {
    private static final Logger LOG = LoggerFactory.getLogger(CollaboratoreProgettoRESTController.class);

    @Autowired
    private CollaboratoreProgettoService collaboratoreProgettoService;
    
    @Autowired
    private MyPortalService myPortalService;
    
    @Autowired
    private SecurityService securityService;

    /**
     * Metodo GET per recuperare i collaboratori
     * @return collaboratori
     * @throws Exception
     */
    @Operation(
    		summary = "Metodo GET per recuperare i collaboratori",
			responses = {
                    @ApiResponse(description = "GET per recuperare i collaboratori",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Object.class))),
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved collaboratori"),
                    @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
                    @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
                    @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
            }
    )

    @GetMapping("")
    public ResponseEntity<PagedData<CollaboratoreProgettoDTO>> getAllCollaboratoriProgetto(
    		@ModelAttribute( name = "_filter" ) CollaboratoreProgettoFilterDTO collaboratoreProgettoFilter,
    		@RequestParam( name = "_page", required = false, defaultValue = "0" ) final Integer pageNumber,
    		@RequestParam( name = "_pageSize", required = false, defaultValue = "10" ) final Integer pageSize,
    		@RequestParam( name = "_orderBy", required = false ) final String orderProperty,
    		@RequestParam( name = "_orderDir", required = false ) final String orderDirection,
    		@AuthenticationPrincipal UserWithAdditionalInfo user
    		) {
    	
        LOG.debug("CollaboratoreProgettoRESTController --> GET Lista collaboratori");
        
        // check permission
        try {
			securityService.checkPermission(user, Constants.ACL_PROGETTI, Constants.PERMISSION_VISUALIZZA);
		} catch (MyExtranetSecurityException e) {
			LOG.error(String.format("Access denied for user %s", user.getUsername()), e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_ACCESS_DENIED_TO_DATA_MESSAGE, e.getMessage(), Constants.API_ERROR_SECURITY_CODE), HttpStatus.FORBIDDEN);
		}
        
        PagedData<CollaboratoreProgettoDTO> collaboratori = collaboratoreProgettoService.getCollaboratoriProgetto(collaboratoreProgettoFilter, pageNumber, pageSize, orderProperty, orderDirection, Constants.GET_COLLABORATORI_PROGETTO_FILTER_TYPE_ALL);
        LOG.debug("CollaboratoreProgettoRESTController --> GET Lista collaboratori eseguito con successo.");

        return new ResponseEntity<PagedData<CollaboratoreProgettoDTO>>(collaboratori, HttpStatus.OK);

    }
    
    /**
     * Metodo GET per recuperare i collaboratori (FlgConferma = 1)
     * @return collaboratori
     * @throws Exception
     */
    @Operation(
    		summary = "Metodo GET per recuperare i collaboratori",
			responses = {
                    @ApiResponse(description = "GET per recuperare i collaboratori (FlgConferma = 1)",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Object.class))),
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved collaboratori"),
                    @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
                    @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
                    @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
            }
    )
    @GetMapping("/collaboratori")
    public ResponseEntity<PagedData<CollaboratoreProgettoDTO>> getAllCollaboratoriProgettoCollaboratori(
    		@ModelAttribute( name = "_filter" ) CollaboratoreProgettoFilterDTO collaboratoreProgettoFilter,
    		@RequestParam( name = "_page", required = false, defaultValue = "0" ) final Integer pageNumber,
    		@RequestParam( name = "_pageSize", required = false, defaultValue = "10" ) final Integer pageSize,
    		@RequestParam( name = "_orderBy", required = false ) final String orderProperty,
    		@RequestParam( name = "_orderDir", required = false ) final String orderDirection,
    		@AuthenticationPrincipal UserWithAdditionalInfo user
    		) {
    	
        LOG.debug("CollaboratoreProgettoRESTController --> GET Lista collaboratori (FlgConferma = 1)");
        
        // check permission
        try {
			securityService.checkPermission(user, Constants.ACL_PROGETTI, Constants.PERMISSION_VISUALIZZA);
		} catch (MyExtranetSecurityException e) {
			LOG.error(String.format("Access denied for user %s", user.getUsername()), e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_ACCESS_DENIED_TO_DATA_MESSAGE, e.getMessage(), Constants.API_ERROR_SECURITY_CODE), HttpStatus.FORBIDDEN);
		}
        
        PagedData<CollaboratoreProgettoDTO> collaboratori = collaboratoreProgettoService.getCollaboratoriProgetto(collaboratoreProgettoFilter, pageNumber, pageSize, orderProperty, orderDirection, Constants.GET_COLLABORATORI_PROGETTO_FILTER_TYPE_COLLABORATORI);
        LOG.debug("CollaboratoreProgettoRESTController --> GET Lista collaboratori (FlgConferma = 1) eseguito con successo.");

        return new ResponseEntity<PagedData<CollaboratoreProgettoDTO>>(collaboratori, HttpStatus.OK);

    }
    
    /**
     * Metodo GET per recuperare i collaboratori (FlgConferma = 1)
     * @return collaboratori
     * @throws Exception
     */
    @Operation(
    		summary = "Metodo GET per recuperare le richieste di collaborazione (FlgConferma = 2)",
			responses = {
                    @ApiResponse(description = "GET per recuperare le richieste di collaborazione (FlgConferma = 2)",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Object.class))),
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved richieste di collaborazione"),
                    @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
                    @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
                    @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
            }
    )
    @GetMapping("/richieste-collaborazione")
    public ResponseEntity<PagedData<CollaboratoreProgettoDTO>> getAllCollaboratoriProgettoRichiesteCollaborazione(
    		@ModelAttribute( name = "_filter" ) CollaboratoreProgettoFilterDTO collaboratoreProgettoFilter,
    		@RequestParam( name = "_page", required = false, defaultValue = "0" ) final Integer pageNumber,
    		@RequestParam( name = "_pageSize", required = false, defaultValue = "10" ) final Integer pageSize,
    		@RequestParam( name = "_orderBy", required = false ) final String orderProperty,
    		@RequestParam( name = "_orderDir", required = false ) final String orderDirection,
    		@AuthenticationPrincipal UserWithAdditionalInfo user
    		) {
    	
        LOG.debug("CollaboratoreProgettoRESTController --> GET Lista richieste di collaborazione (FlgConferma = 2)");
        
        // check permission
        try {
			securityService.checkPermission(user, Constants.ACL_PROGETTI, Constants.PERMISSION_VISUALIZZA);
		} catch (MyExtranetSecurityException e) {
			LOG.error(String.format("Access denied for user %s", user.getUsername()), e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_ACCESS_DENIED_TO_DATA_MESSAGE, e.getMessage(), Constants.API_ERROR_SECURITY_CODE), HttpStatus.FORBIDDEN);
		}
        
        PagedData<CollaboratoreProgettoDTO> collaboratori = collaboratoreProgettoService.getCollaboratoriProgetto(collaboratoreProgettoFilter, pageNumber, pageSize, orderProperty, orderDirection, Constants.GET_COLLABORATORI_PROGETTO_FILTER_TYPE_RICHIESTE_COLLABORAZIONE);
        LOG.debug("CollaboratoreProgettoRESTController --> GET Lista richieste di collaborazione (FlgConferma = 2) eseguito con successo.");

        return new ResponseEntity<PagedData<CollaboratoreProgettoDTO>>(collaboratori, HttpStatus.OK);

    }

    
    /**
     * Metodo GET per recuperare i collaboratori
     * @return collaboratori
     * @throws Exception
     */
    @Operation(
    		summary = "Metodo GET per recuperare un collaboratore tramite id",
			responses = {
                    @ApiResponse(description = "GET per recuperare un collaboratore",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Object.class))),
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved collaboratore"),
                    @ApiResponse(responseCode = "400", description = "Collaboratore not found"),
                    @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
                    @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
                    @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
            }
    )

    @GetMapping("/{_id}")
    public ResponseEntity<CollaboratoreProgettoDTO> getCollaboratoreById(
    		@PathVariable( name = "_id", required = true) final Integer collaboratoreId,
    		@AuthenticationPrincipal UserWithAdditionalInfo user
    		) {
    	LOG.debug("CollaboratoreProgettoRESTController --> GET collaboratore with id " + collaboratoreId);
    	
        // check permission
        try {
			securityService.checkPermission(user, Constants.ACL_PROGETTI, Constants.PERMISSION_VISUALIZZA);
		} catch (MyExtranetSecurityException e) {
			LOG.error(String.format("Access denied for user %s", user.getUsername()), e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_ACCESS_DENIED_TO_DATA_MESSAGE, e.getMessage(), Constants.API_ERROR_SECURITY_CODE), HttpStatus.FORBIDDEN);
		}

        CollaboratoreProgettoDTO collaboratore;
		try {
			collaboratore = collaboratoreProgettoService.getCollaboratoreProgetto(collaboratoreId);

			LOG.debug("CollaboratoreProgettoRESTController --> GET collaboratore eseguito con successo.");

        	return new ResponseEntity<CollaboratoreProgettoDTO>(collaboratore, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("CollaboratoreProgettoRESTController --> GET collaboratore non riuscito", e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_GENERIC_MESSAGE, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
    }
    
    /**
     * 
     * @param newCollaboratoreProgetto
     * @return
     */
    @Operation(
    		summary = "Metodo POST per inserire un collaboratore",
			responses = {
                    @ApiResponse(description = "POST per inserire un collaboratore",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Object.class))),
                    @ApiResponse(responseCode = "201", description = "Successfully created collaboratore"),
                    @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
                    @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
                    @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
            }
    )
    @PostMapping("")
    public synchronized ResponseEntity<ApiResult> insertCollaboratoreProgetto(
    		@RequestBody final @Valid CollaboratoreProgettoDTO newCollaboratoreProgetto,
    		BindingResult bindingResult,
    		@AuthenticationPrincipal UserWithAdditionalInfo user
    		) {
    	LOG.debug("CollaboratoreProgettoRESTController --> POST nuovo collaboratore");
    	
        // check permission
        try {
			securityService.checkPermission(user, Constants.ACL_PROGETTI, Constants.PERMISSION_GESTISCI);
		} catch (MyExtranetSecurityException e) {
			LOG.error(String.format("Access denied for user %s", user.getUsername()), e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_ACCESS_DENIED_TO_DATA_MESSAGE, e.getMessage(), Constants.API_ERROR_SECURITY_CODE), HttpStatus.FORBIDDEN);
		}
    	
    	try {
    		
    		validate(newCollaboratoreProgetto, bindingResult);
            
            // check object does not have an Id
            if (newCollaboratoreProgetto.getIdCollab() != null) {
            	LOG.error("CollaboratoreProgettoRESTController --> POST nuovo collaboratore usato impropriamente: l'oggetto ha un ID");
            	return new ResponseEntity<ApiResult>(new ApiResult(Constants.RESULT_KO, "POST method is not for updates. Use PUT or PATCH instead.", newCollaboratoreProgetto), HttpStatus.BAD_REQUEST);
            }
            
            // true if from frontoffice
            boolean notValidYet = false;
            
            CollaboratoreProgettoDTO collaboratore = collaboratoreProgettoService.insertCollaboratoreProgetto(newCollaboratoreProgetto, notValidYet);
                        
            LOG.debug("CollaboratoreProgettoRESTController --> POST nuovo collaboratore eseguito con successo.");

            return new ResponseEntity<ApiResult>(new ApiResult(Constants.RESULT_OK, "Successfully created collaboratore", collaboratore), HttpStatus.CREATED);			
    	} catch (MyExtranetValidationException e) {
    		LOG.error("Validation exception", e);
    		return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_VALIDATION_MESSAGE, e.getMessage(), Constants.API_ERROR_VALIDATION_CODE), HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			LOG.error("CollaboratoreProgettoRESTController --> POST nuovo collaboratore non riuscito", e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_GENERIC_MESSAGE, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}

    }

    /**
     * Metodo PATCH per modificare parzialmente un collaboratore
     * @return collaboratore
     * @throws Exception
     */
    @Operation(
    		summary = "Metodo PATCH per modificare parzialmente un collaboratore",
			responses = {
                    @ApiResponse(description = "PATCH per modificare parzialmente un collaboratore",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Object.class))),
                    @ApiResponse(responseCode = "200", description = "Successfully patched collaboratore"),
                    @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
                    @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
                    @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
            }
    )
    @PatchMapping("")
    public ResponseEntity<ApiResult> patchCollaboratoreProgetto(
    		@RequestBody final CollaboratoreProgettoDTO patchCollaboratoreProgetto,
    		@AuthenticationPrincipal UserWithAdditionalInfo user
    		) {
    	LOG.debug("CollaboratoreProgettoRESTController --> PATCH collaboratore." );
    	
        // check permission
        try {
			securityService.checkPermission(user, Constants.ACL_PROGETTI, Constants.PERMISSION_GESTISCI);
		} catch (MyExtranetSecurityException e) {
			LOG.error(String.format("Access denied for user %s", user.getUsername()), e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_ACCESS_DENIED_TO_DATA_MESSAGE, e.getMessage(), Constants.API_ERROR_SECURITY_CODE), HttpStatus.FORBIDDEN);
		}
    	
    	try {
            
            // check object does not have an Id
            if (patchCollaboratoreProgetto.getIdCollab() == null) {
            	LOG.error("CollaboratoreProgettoRESTController --> PATCH collaboratore usato impropriamente: l'oggetto non ha un ID");
            	return new ResponseEntity<ApiResult>(new ApiResult(Constants.RESULT_KO, "To use PATCH the object needs to have an ID", patchCollaboratoreProgetto), HttpStatus.BAD_REQUEST);
            }
            
            CollaboratoreProgettoDTO collaboratore = collaboratoreProgettoService.updateCollaboratoreProgetto(patchCollaboratoreProgetto, true);
            LOG.debug("CollaboratoreProgettoRESTController --> PATCH collaboratore eseguito con successo.");

            return new ResponseEntity<ApiResult>(new ApiResult(Constants.RESULT_OK, "Successfully patched collaboratore", collaboratore), HttpStatus.OK);			
		} catch (Exception e) {
			LOG.error("CollaboratoreProgettoRESTController --> PATCH collaboratore non riuscito", e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_GENERIC_MESSAGE, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}

    }  
    
   
    /**
     * Richiesta di una nuova operazione su una lista di collaboratori
     * 
     * @param operazione descrittore dell'operazione
     * @return
     */
    @Operation(
    		summary = "Metodo POST per eseguire una nuova operazione massiva sui collaboratori",
			responses = {
                    @ApiResponse(description = "POST per eseguire una nuova operazione massiva sui collaboratori",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Object.class))),
                    @ApiResponse(responseCode = "200", description = "Operation executed successfully"),
                    @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
                    @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
                    @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
            }
    )
    @PostMapping("operazione-massiva")
    public ResponseEntity<ApiResult> insertOperazioneMassivaCollaboratoreProgetto(
    		@RequestBody final @Valid OperazioneMassivaDTO operazione,
    		@AuthenticationPrincipal UserWithAdditionalInfo user
    		) {
    	LOG.debug("CollaboratoreProgettoRESTController --> POST nuova operazione massiva sui collaboratori." );
    	
        // check permission
        try {
			securityService.checkPermission(user, Constants.ACL_PROGETTI, Constants.PERMISSION_GESTISCI);
		} catch (MyExtranetSecurityException e) {
			LOG.error(String.format("Access denied for user %s", user.getUsername()), e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_ACCESS_DENIED_TO_DATA_MESSAGE, e.getMessage(), Constants.API_ERROR_SECURITY_CODE), HttpStatus.FORBIDDEN);
		}
    	
    	try {
            
            // check if tipo operazione is present
            if (operazione.getTipoOperazione() == null) {
            	LOG.error("CollaboratoreProgettoRESTController --> POST nuova operazione massiva sui collaboratori usato impropriamente: tipo operazione mancante");
            	return new ResponseEntity<ApiResult>(new ApiResult(Constants.RESULT_KO, "Missing operation type", operazione), HttpStatus.BAD_REQUEST);
            }
            
            // execute operation
            collaboratoreProgettoService.executeOperationOnCollaboratoriProgetto(operazione);
            
            LOG.debug("CollaboratoreProgettoRESTController --> POST operazione massiva sui collaboratori eseguito con successo.");

            return new ResponseEntity<ApiResult>(new ApiResult(Constants.RESULT_OK, "Operation successfully performed", operazione), HttpStatus.OK);			
		} catch (Exception e) {
			LOG.error("CollaboratoreProgettoRESTController --> POST operazione massiva sui collaboratori non riuscito", e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_GENERIC_MESSAGE, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}

    }
        
    /**
     * Richiesta di una nuova comunicazione a tutti i collaboratori ad un progetto
     * 
     * @param messaggio messaggio da inviare
     * @return
     */
    @Operation(
    		summary = "Metodo POST per inviare una nuova comunicazione a tutti i collaboratori ad un progetto",
			responses = {
                    @ApiResponse(description = "POST per inviare una nuova comunicazione a tutti i collaboratori ad un progetto",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Object.class))),
                    @ApiResponse(responseCode = "200", description = "Operation executed successfully"),
                    @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
                    @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
                    @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
            }
    )
    @PostMapping("/{idProgetto}/comunicazione")
    public ResponseEntity<ApiResult> insertComunicazioneCollaboratoriProgetto(
    		@PathVariable final String idProgetto,
    		@RequestBody final MessaggioDTO messaggio,
    		@AuthenticationPrincipal UserWithAdditionalInfo user
    		) {
    	LOG.debug("CollaboratoreProgettoRESTController --> POST nuova comunicazione ai collaboratori." );
    	
    	// check permission
    	try {
    		securityService.checkPermission(user, Constants.ACL_PROGETTI, Constants.PERMISSION_GESTISCI);
    	} catch (MyExtranetSecurityException e) {
    		LOG.error(String.format("Access denied for user %s", user.getUsername()), e);
    		return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_ACCESS_DENIED_TO_DATA_MESSAGE, e.getMessage(), Constants.API_ERROR_SECURITY_CODE), HttpStatus.FORBIDDEN);
    	}
    	
    	try {
            
            // check if needed fields are present
            if (messaggio.getTitolo() == null || messaggio.getMessaggio() == null) {
            	LOG.error("CollaboratoreProgettoRESTController --> POST nuova comunicazione ai collaboratori usato impropriamente: titolo e/o messaggio mancante");
            	return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_VALIDATION_MESSAGE, "titolo e/o messaggio mancante", Constants.API_ERROR_VALIDATION_CODE), HttpStatus.BAD_REQUEST);
            }
            
            // set some properties
            messaggio.setTipo(Constants.MESSAGGIO_TIPO_COMUNICAZIONE);
            messaggio.setArea(Constants.MESSAGGIO_AREA_PROGETTO);
            messaggio.setIdContenuto(idProgetto);
            messaggio.setDestinatario("A tutti i collaboratori");
            
            // execute operation
            MessaggioDTO sentMessaggio = collaboratoreProgettoService.sendMessaggioToCollaboratoriProgetto(messaggio);
            
            LOG.debug("CollaboratoreProgettoRESTController --> POST nuova comunicazione ai collaboratori eseguito con successo.");

            return new ResponseEntity<ApiResult>(new ApiResult(Constants.RESULT_OK, "Operation successfully performed", sentMessaggio), HttpStatus.OK);			
		} catch (Exception e) {
			LOG.error("CollaboratoreProgettoRESTController --> POST nuova comunicazione ai collaboratori non riuscito", e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_GENERIC_MESSAGE, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}

    }
    
    @PostMapping("/comunicazione")
    public ResponseEntity<ApiResult> insertComunicazioneCollaboratoriProgettoFiltered(
    		@ModelAttribute( name = "_filter" ) CollaboratoreProgettoFilterDTO collaboratoreProgettoFilter,
    		@RequestBody final MessaggioDTO messaggio,
    		@AuthenticationPrincipal UserWithAdditionalInfo user
    		) {
    	LOG.debug("CollaboratoreProgettoRESTController --> POST nuova comunicazione ai collaboratori filtrati." );
    	
    	// check permission
    	try {
    		securityService.checkPermission(user, Constants.ACL_PROGETTI, Constants.PERMISSION_GESTISCI);
    	} catch (MyExtranetSecurityException e) {
    		LOG.error(String.format("Access denied for user %s", user.getUsername()), e);
    		return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_ACCESS_DENIED_TO_DATA_MESSAGE, e.getMessage(), Constants.API_ERROR_SECURITY_CODE), HttpStatus.FORBIDDEN);
    	}
    	
    	try {
            
            // check if needed fields are present
            if (messaggio.getTitolo() == null || messaggio.getMessaggio() == null) {
            	LOG.error("CollaboratoreProgettoRESTController --> POST nuova comunicazione ai collaboratori filtrati usato impropriamente: titolo e/o messaggio mancante");
            	return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_VALIDATION_MESSAGE, "titolo e/o messaggio mancante", Constants.API_ERROR_VALIDATION_CODE), HttpStatus.BAD_REQUEST);
            }
            
            // set some properties
            messaggio.setTipo(Constants.MESSAGGIO_TIPO_COMUNICAZIONE);
            messaggio.setArea(Constants.MESSAGGIO_AREA_PROGETTO);
            messaggio.setDestinatario("Ai collaboratori");
            
            // execute operation
            MessaggioDTO sentMessaggio = collaboratoreProgettoService.sendMessaggioToCollaboratoriProgetto(collaboratoreProgettoFilter, messaggio);
            
            LOG.debug("CollaboratoreProgettoRESTController --> POST nuova comunicazione ai collaboratori filtrati eseguito con successo.");

            return new ResponseEntity<ApiResult>(new ApiResult(Constants.RESULT_OK, "Operation successfully performed", sentMessaggio), HttpStatus.OK);			
		} catch (Exception e) {
			LOG.error("CollaboratoreProgettoRESTController --> POST nuova comunicazione ai collaboratori filtrati non riuscito", e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_GENERIC_MESSAGE, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}

    }
    
    /**
     * Metodo POST per annullare una richiesta di collaborazione
     * @param details
     * @return
     */
    @Operation(
    		summary = "Metodo POST per annullare una richiesta di collaborazione",
			responses = {
                    @ApiResponse(description = "POST per annullare una richiesta di collaborazione",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Object.class))),
                    @ApiResponse(responseCode = "200", description = "Successfully executed"),
                    @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
                    @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
                    @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
            }
    )
    @PostMapping("/cancel")
    public synchronized ResponseEntity<ApiResult> cancelRequestCollaboratoreProgetto(
    		@RequestBody final @Valid CollaboratoreProgettoDTO details,
    		BindingResult bindingResult,
    		@AuthenticationPrincipal UserWithAdditionalInfo user
    		) {
    	LOG.debug("CollaboratoreProgettoRESTController --> POST per annullare una richiesta di collaborazione");
    	
        // check permission
        try {
			securityService.checkPermission(user, Constants.ACL_PROGETTI, Constants.PERMISSION_GESTISCI);
		} catch (MyExtranetSecurityException e) {
			LOG.error(String.format("Access denied for user %s", user.getUsername()), e);
			return new ResponseEntity(HttpStatus.FORBIDDEN);
		}
    	
    	try {
    		            
            // check object does have an Id
            if (details.getIdCollab() == null) {
            	LOG.error("CollaboratoreProgettoRESTController --> POST per annullare una richiesta di collaborazione usato impropriamente: l'oggetto non ha un ID");
            	return new ResponseEntity<ApiResult>(new ApiResult(Constants.RESULT_KO, "This POST needs an idCollab ", details), HttpStatus.BAD_REQUEST);
            }
            
            if (details.getMotivConferma() == null) {
            	details.setMotivConferma("annullata da un operatore");
            }
            
            details.setFlgConferma(Constants.COLLABORATORE_PROGETTO_FLG_CONFERMA_ANNULLATA_DA_OPERATORE);
            
            CollaboratoreProgettoDTO collaboratore = collaboratoreProgettoService.updateCollaboratoreProgetto(details, true);
                        
            LOG.debug("CollaboratoreProgettoRESTController --> POST per annullare una richiesta di collaborazione eseguito con successo.");

            return new ResponseEntity<ApiResult>(new ApiResult(Constants.RESULT_OK, "Successfully executed", collaboratore), HttpStatus.OK);			
		} catch (Exception e) {
			LOG.error("CollaboratoreProgettoRESTController --> POST per annullare una richiesta di collaborazione non riuscito", e);
			return new ResponseEntity<ApiResult>(new ApiResult(Constants.RESULT_KO, "Error executing operation", e.getMessage()), HttpStatus.BAD_REQUEST);
		}

    }


    
}
