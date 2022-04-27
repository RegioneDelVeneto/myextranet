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

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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
import it.regioneveneto.myp3.myextranet.exception.PermissionException;
import it.regioneveneto.myp3.myextranet.security.UserWithAdditionalInfo;
import it.regioneveneto.myp3.myextranet.service.CollaboratoreProgettoService;
import it.regioneveneto.myp3.myextranet.service.SecurityService;
import it.regioneveneto.myp3.myextranet.utils.Constants;
import it.regioneveneto.myp3.myextranet.web.controller.ValidationController;
import it.regioneveneto.myp3.myextranet.web.dto.CollaboratoreProgettoDTO;
import it.regioneveneto.myp3.myextranet.web.dto.CollaboratoreProgettoDettaglioFilterDTO;
import it.regioneveneto.myp3.myextranet.web.dto.CollaboratoreProgettoFilterDTO;
import it.regioneveneto.myp3.myextranet.web.dto.DettaglioCollaborazioneDTO;
import it.regioneveneto.myp3.myextranet.web.dto.DettaglioIscrizioneDTO;
import it.regioneveneto.myp3.myextranet.web.dto.IscrittoEventoDettaglioFilterDTO;
import it.regioneveneto.myp3.myextranet.web.dto.PagedData;

@RestController
@RequestMapping("/frontoffice/collaboratore-progetto")
@Tag(name = "tag_collaboratore-progetto", description = "Collaboratore")
public class CollaboratoreProgettoFrontofficeRESTController extends ValidationController {
    private static final Logger LOG = LoggerFactory.getLogger(CollaboratoreProgettoFrontofficeRESTController.class);

    @Autowired
    private CollaboratoreProgettoService collaboratoreProgettoService;
        
    @Autowired
    private SecurityService securityService;

    /**
     * Metodo GET per recuperare i collaboratori
     * @return collaboratori
     * @throws Exception
     */
    @Operation(
    		summary = "Metodo GET per recuperare i collaboratori di un proprio progetto",
			responses = {
                    @ApiResponse(description = "GET per recuperare i collaboratori di un proprio progetto",
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
    	
        LOG.debug("CollaboratoreProgettoFrontofficeRESTController --> GET Lista collaboratori");
        
        // check permission
        try {
        	securityService.checkUserValidity(user);
        	
            securityService.checkUserBelongsToProgetto(collaboratoreProgettoFilter.getIdProgetto(), user);
		} catch (MyExtranetSecurityException e) {
			LOG.error(String.format("Access denied for user %s", user.getUsername()), e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_ACCESS_DENIED_TO_DATA_MESSAGE, e.getMessage(), Constants.API_ERROR_SECURITY_CODE), HttpStatus.FORBIDDEN);
		}

        PagedData<CollaboratoreProgettoDTO> collaboratori = collaboratoreProgettoService.getCollaboratoriProgetto(collaboratoreProgettoFilter, pageNumber, pageSize, orderProperty, orderDirection, Constants.GET_COLLABORATORI_PROGETTO_FILTER_TYPE_ALL);
        LOG.debug("CollaboratoreProgettoFrontofficeRESTController --> GET Lista collaboratori eseguito con successo.");

        return new ResponseEntity<PagedData<CollaboratoreProgettoDTO>>(collaboratori, HttpStatus.OK);

    }
    
    /**
     * Metodo GET per recuperare i progetti dell'utente
     * @return collaboratori
     * @throws Exception
     */
    @Operation(
    		summary = "Metodo GET per recuperare i progetti dell'utente",
			responses = {
                    @ApiResponse(description = "GET per recuperare i progetti dell'utente",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Object.class))),
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved progetti"),
                    @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
                    @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
                    @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
            }
    )
    @GetMapping("/progetti")
    public ResponseEntity<PagedData<DettaglioCollaborazioneDTO>> getDettaglioProgettiCollaborati(
    		@RequestParam( name = "_page", required = false, defaultValue = "0" ) final Integer pageNumber,
    		@RequestParam( name = "_pageSize", required = false, defaultValue = "10" ) final Integer pageSize,
    		@RequestParam( name = "_orderBy", required = false ) final String orderProperty,
    		@RequestParam( name = "_orderDir", required = false ) final String orderDirection,
    		@AuthenticationPrincipal UserWithAdditionalInfo user
    		) {
    	
        LOG.debug("CollaboratoreProgettoFrontofficeRESTController --> GET Lista progetti");
        
        // check permission
        try {
        	securityService.checkUserValidity(user);
		} catch (MyExtranetSecurityException e) {
			LOG.error(String.format("Access denied for user %s", user.getUsername()), e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_ACCESS_DENIED_TO_DATA_MESSAGE, e.getMessage(), Constants.API_ERROR_SECURITY_CODE), HttpStatus.FORBIDDEN);
		}

        PagedData<DettaglioCollaborazioneDTO> progetti = collaboratoreProgettoService.getDettaglioProgettiCollaborati(user, pageNumber, pageSize, orderProperty, orderDirection);
        LOG.debug("CollaboratoreProgettoFrontofficeRESTController --> GET Lista progetti eseguito con successo.");

        return new ResponseEntity<PagedData<DettaglioCollaborazioneDTO>>(progetti, HttpStatus.OK);

    }    
    
    /**
     * Metodo GET per recuperare i dati di una collaborazione
     * @return collaborazione
     * @throws Exception
     */
    @Operation(
    		summary = "Metodo GET per recuperare i dati di una collaborazione",
			responses = {
                    @ApiResponse(description = "GET per recuperare i dati di una collaborazione",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Object.class))),
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved collaborazione"),
                    @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
                    @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
                    @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
            }
    )
    @GetMapping("/collaborazione")
    public ResponseEntity<DettaglioCollaborazioneDTO> getDettaglioCollaborazioneByFilter(
    		@ModelAttribute( name = "_filter" ) CollaboratoreProgettoDettaglioFilterDTO collaboratoreProgettoDettaglioFilter,
    		@AuthenticationPrincipal UserWithAdditionalInfo user
    		) {
    	
        LOG.debug("CollaboratoreProgettoFrontofficeRESTController --> GET collaborazione");
        
        // check permission
        try {
        	securityService.checkUserValidity(user);
		} catch (MyExtranetSecurityException e) {
			LOG.error(String.format("Access denied for user %s", user.getUsername()), e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_ACCESS_DENIED_TO_DATA_MESSAGE, e.getMessage(), Constants.API_ERROR_SECURITY_CODE), HttpStatus.FORBIDDEN);
		}
        
        // check filter
        if (collaboratoreProgettoDettaglioFilter.getIdCollab() == null && collaboratoreProgettoDettaglioFilter.getIdProgetto() == null) {
        	LOG.error("Invalid CollaboratoreProgettoDettaglioFilterDTO");
        	return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_GENERIC_MESSAGE, null), HttpStatus.BAD_REQUEST);
        }

        DettaglioCollaborazioneDTO collaborazione;
		try {
			collaborazione = collaboratoreProgettoService.getDettaglioCollaborazioneProgetto(collaboratoreProgettoDettaglioFilter, user);
			
			if(collaborazione.getCollaboratoreProgetto() != null) {
				securityService.checkIsDataOwner(collaborazione.getCollaboratoreProgetto(), user);
			}
			
		} catch (MyExtranetSecurityException e) {
			LOG.error(String.format("Access denied for user %s", user.getUsername()), e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_ACCESS_DENIED_TO_DATA_MESSAGE, e.getMessage(), Constants.API_ERROR_SECURITY_CODE), HttpStatus.FORBIDDEN);
		} catch (Exception e) {
			LOG.error("Error retrieving collaborazione", e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_GENERIC_MESSAGE, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
        LOG.debug("CollaboratoreProgettoFrontofficeRESTController --> GET collaborazione eseguito con successo.");

        return new ResponseEntity<DettaglioCollaborazioneDTO>(collaborazione, HttpStatus.OK);

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
    public ResponseEntity<DettaglioCollaborazioneDTO> getDettaglioCollaborazioneById(
    		@PathVariable( name = "_id", required = true) final Integer collaboratoreId,
    		@AuthenticationPrincipal UserWithAdditionalInfo user
    		) {
    	LOG.debug("CollaboratoreProgettoFrontofficeRESTController --> GET collaboratore with id " + collaboratoreId);
    	
        // check permission
        try {
        	securityService.checkUserValidity(user);
		} catch (MyExtranetSecurityException e) {
			LOG.error(String.format("Access denied for user %s", user.getUsername()), e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_ACCESS_DENIED_TO_DATA_MESSAGE, e.getMessage(), Constants.API_ERROR_SECURITY_CODE), HttpStatus.FORBIDDEN);
		}

        DettaglioCollaborazioneDTO collaborazione;
		try {
			collaborazione = collaboratoreProgettoService.getDettaglioCollaborazioneProgetto(collaboratoreId);
			
			securityService.checkIsDataOwner(collaborazione.getCollaboratoreProgetto(), user);

			LOG.debug("CollaboratoreProgettoFrontofficeRESTController --> GET collaboratore eseguito con successo.");

        	return new ResponseEntity<DettaglioCollaborazioneDTO>(collaborazione, HttpStatus.OK);
		} catch (MyExtranetSecurityException e) {
			LOG.error(String.format("Access denied for user %s", user.getUsername()), e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_ACCESS_DENIED_TO_DATA_MESSAGE, e.getMessage(), Constants.API_ERROR_SECURITY_CODE), HttpStatus.FORBIDDEN);
		} catch (Exception e) {
			LOG.error("CollaboratoreProgettoFrontofficeRESTController --> GET collaboratore non riuscito", e);
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
    	LOG.debug("CollaboratoreProgettoFrontofficeRESTController --> POST nuovo collaboratore");
    	
        // check permission
        try {
        	securityService.checkUserValidity(user);
        	
        	securityService.checkIsDataOwner(newCollaboratoreProgetto, user);
		} catch (MyExtranetSecurityException e) {
			LOG.error(String.format("Access denied for user %s", user.getUsername()), e);
			return new ResponseEntity(HttpStatus.FORBIDDEN);
		}
    	
    	try {
    		
    		validate(newCollaboratoreProgetto, bindingResult);
            
            // check object does not have an Id
            if (newCollaboratoreProgetto.getIdCollab() != null) {
            	LOG.error("CollaboratoreProgettoFrontofficeRESTController --> POST nuovo collaboratore usato impropriamente: l'oggetto ha un ID");
            	return new ResponseEntity<ApiResult>(new ApiResult(Constants.RESULT_KO, "POST method is not for updates. Use PUT or PATCH instead.", newCollaboratoreProgetto), HttpStatus.BAD_REQUEST);
            }
            
            // true if from frontoffice
            boolean notValidYet = true;
            
            CollaboratoreProgettoDTO collaboratore = collaboratoreProgettoService.insertCollaboratoreProgetto(newCollaboratoreProgetto, notValidYet);
                        
            LOG.debug("CollaboratoreProgettoFrontofficeRESTController --> POST nuovo collaboratore eseguito con successo.");

            return new ResponseEntity<ApiResult>(new ApiResult(Constants.RESULT_OK, "Successfully created collaboratore", collaboratore), HttpStatus.CREATED);			
    	} catch (MyExtranetValidationException e) {
    		LOG.error("Validation exception", e);
    		return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_VALIDATION_MESSAGE, e.getMessage(), Constants.API_ERROR_VALIDATION_CODE), HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			LOG.error("CollaboratoreProgettoFrontofficeRESTController --> POST nuovo collaboratore non riuscito", e);
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
    	LOG.debug("CollaboratoreProgettoFrontofficeRESTController --> POST per annullare una richiesta di collaborazione");
    	
        // check permission
        try {
        	securityService.checkUserValidity(user);
        	
        	securityService.checkIsDataOwner(details, user);
		} catch (MyExtranetSecurityException e) {
			LOG.error(String.format("Access denied for user %s", user.getUsername()), e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_ACCESS_DENIED_TO_DATA_MESSAGE, e.getMessage(), Constants.API_ERROR_SECURITY_CODE), HttpStatus.FORBIDDEN);
		}
    	
    	try {
    		            
            // check object does have an Id
            if (details.getIdCollab() == null) {
            	LOG.error("CollaboratoreProgettoFrontofficeRESTController --> POST per annullare una richiesta di collaborazione usato impropriamente: l'oggetto non ha un ID");
            	return new ResponseEntity<ApiResult>(new ApiResult(Constants.RESULT_KO, "This POST needs an idCollab ", details), HttpStatus.BAD_REQUEST);
            }
            
            if (details.getMotivConferma() == null) {
            	details.setMotivConferma("annullata dall'utente");
            }
            
            details.setFlgConferma(Constants.COLLABORATORE_PROGETTO_FLG_CONFERMA_ANNULLATA_DALL_UTENTE);
            
            CollaboratoreProgettoDTO collaboratore = collaboratoreProgettoService.updateCollaboratoreProgetto(details, true);
                        
            LOG.debug("CollaboratoreProgettoFrontofficeRESTController --> POST per annullare una richiesta di collaborazione eseguito con successo.");

            return new ResponseEntity<ApiResult>(new ApiResult(Constants.RESULT_OK, "Successfully executed", collaboratore), HttpStatus.OK);			
		} catch (Exception e) {
			LOG.error("CollaboratoreProgettoFrontofficeRESTController --> POST per annullare una richiesta di collaborazione non riuscito", e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_GENERIC_MESSAGE, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}

    }


                
}
