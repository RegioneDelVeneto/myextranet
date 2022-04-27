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

import java.util.List;
import java.util.UUID;

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
import it.regioneveneto.myp3.myextranet.model.RappresentanteEnte;
import it.regioneveneto.myp3.myextranet.security.UserWithAdditionalInfo;
import it.regioneveneto.myp3.myextranet.service.RappresentanteEnteService;
import it.regioneveneto.myp3.myextranet.service.SecurityService;
import it.regioneveneto.myp3.myextranet.utils.Constants;
import it.regioneveneto.myp3.myextranet.web.dto.IscrittoEventoDTO;
import it.regioneveneto.myp3.myextranet.web.dto.PagedData;
import it.regioneveneto.myp3.myextranet.web.dto.RappresentanteEnteConfermaDTO;
import it.regioneveneto.myp3.myextranet.web.dto.RappresentanteEnteDTO;
import it.regioneveneto.myp3.myextranet.web.dto.RappresentanteEnteFilterDTO;
import it.regioneveneto.myp3.myextranet.web.dto.UtenteDTO;

@RestController
@RequestMapping("/rappresentante-ente-rap")
@Tag(name = "tag_rappresentante-ente-rap", description = "Rappresentante RAP")
public class RappresentanteEnteRapRESTController extends ValidationController {
	private static final Logger LOG = LoggerFactory.getLogger(RappresentanteEnteRapRESTController.class);
	
    @Autowired
    private RappresentanteEnteService rappresentanteEnteService;
        
    @Autowired
    private SecurityService securityService;

    /**
     * Metodo GET per recuperare i rappresentanti
     * @return rappresentanti
     * @throws Exception
     */
    @Operation(
    		summary = "Metodo GET per recuperare i rappresentanti",
			responses = {
                    @ApiResponse(description = "GET per recuperare i rappresentanti",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Object.class))),
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved rappresentanti"),
                    @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
                    @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
                    @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
            }
    )
    @GetMapping("")
    public ResponseEntity<PagedData<RappresentanteEnteDTO>> getAllRappresentantiEnte(
    		@ModelAttribute( name = "_filter" ) RappresentanteEnteFilterDTO rappresentanteEnteFilter,
    		@RequestParam( name = "_page", required = false, defaultValue = "0" ) final Integer pageNumber,
    		@RequestParam( name = "_pageSize", required = false, defaultValue = "10" ) final Integer pageSize,
    		@RequestParam( name = "_orderBy", required = false ) final String orderProperty,
    		@RequestParam( name = "_orderDir", required = false ) final String orderDirection,
    		@AuthenticationPrincipal UserWithAdditionalInfo user
    		) {
    	
        LOG.debug("RappresentanteEnteRapRESTController --> GET Lista rappresentanti");
        
        // check permission
        try {
			securityService.checkPermission(user, Constants.ACL_ENTI, Constants.PERMISSION_VISUALIZZA);
		} catch (MyExtranetSecurityException e) {
			LOG.error(String.format("Access denied for user %s", user.getUsername()), e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_ACCESS_DENIED_TO_DATA_MESSAGE, e.getMessage(), Constants.API_ERROR_SECURITY_CODE), HttpStatus.FORBIDDEN);
		}
        
        rappresentanteEnteFilter.setTipoRappr(Constants.RAPPRESENTANTE_ENTE_TIPO_RAP);
        
        PagedData<RappresentanteEnteDTO> rappresentanti = rappresentanteEnteService.getRappresentantiEnte(rappresentanteEnteFilter, pageNumber, pageSize, orderProperty, orderDirection);
        rappresentanteEnteService.prepareRappresentantiListDTO(rappresentanti.getRecords());
        
        
        
        LOG.debug("RappresentanteEnteRapRESTController --> GET Lista rappresentanti eseguito con successo.");

        return new ResponseEntity<PagedData<RappresentanteEnteDTO>>(rappresentanti, HttpStatus.OK);

    }
    
    @GetMapping("/{_id}")
    public ResponseEntity<RappresentanteEnteDTO> getRappresentanteEnteById(
    		@PathVariable( name = "_id", required = true) final Integer idRappresentante,
    		@AuthenticationPrincipal UserWithAdditionalInfo user
    		) {
    	LOG.debug("RappresentanteEnteRapRESTController --> GET rappresentante with id " + idRappresentante);
    	
        // check permission
        try {
			securityService.checkPermission(user, Constants.ACL_ENTI, Constants.PERMISSION_VISUALIZZA);
		} catch (MyExtranetSecurityException e) {
			LOG.error(String.format("Access denied for user %s", user.getUsername()), e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_ACCESS_DENIED_TO_DATA_MESSAGE, e.getMessage(), Constants.API_ERROR_SECURITY_CODE), HttpStatus.FORBIDDEN);
		}

        RappresentanteEnteDTO rappresentante;
		try {
			rappresentante = rappresentanteEnteService.getRappresentanteEnte(idRappresentante);

			LOG.debug("RappresentanteEnteRapRESTController --> GET rappresentante eseguito con successo.");

        	return new ResponseEntity<RappresentanteEnteDTO>(rappresentanteEnteService.setFlgAttivoOnRAP(rappresentante), HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("RappresentanteEnteRapRESTController --> GET rappresentante non riuscito", e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_GENERIC_MESSAGE, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
    }

    @PostMapping("")
    public synchronized ResponseEntity<ApiResult> insertRappresentanteEnte(
    		@RequestPart(name = "newRappresentanteEnte", required = true) String newRappresentanteEnteStr,
    		BindingResult bindingResult,
    		@AuthenticationPrincipal UserWithAdditionalInfo user
    		) {
    	LOG.debug("RappresentanteEnteRapRESTController --> POST nuovo rappresentante");

        // check permission
        try {
			securityService.checkPermission(user, Constants.ACL_ENTI, Constants.PERMISSION_GESTISCI);
		} catch (MyExtranetSecurityException e) {
			LOG.error(String.format("Access denied for user %s", user.getUsername()), e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_ACCESS_DENIED_TO_DATA_MESSAGE, e.getMessage(), Constants.API_ERROR_SECURITY_CODE), HttpStatus.FORBIDDEN);
		}

    	try {

    		ObjectMapper mapper = new ObjectMapper();
    		RappresentanteEnteDTO newRappresentanteEnte = mapper.readValue(newRappresentanteEnteStr, RappresentanteEnteDTO.class);    		
    		validate(newRappresentanteEnte, bindingResult);

            // check object does not have an Id
            if (newRappresentanteEnte.getIdRappr() != null) {
            	LOG.error("RappresentanteEnteRapRESTController --> POST nuovo rappresentante usato impropriamente: l'oggetto ha un ID");
            	return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_VALIDATION_MESSAGE, "l'oggetto ha un ID", Constants.API_ERROR_VALIDATION_CODE), HttpStatus.BAD_REQUEST);
            }

            // true if from frontoffice
            boolean notValidYet = false;

            // set correct type
            newRappresentanteEnte.setTipoRappr(Constants.RAPPRESENTANTE_ENTE_TIPO_RAP);

            RappresentanteEnteDTO rappresentante = rappresentanteEnteService.insertRappresentanteEnte(newRappresentanteEnte, notValidYet);

            LOG.debug("RappresentanteEnteRapRESTController --> POST nuovo rappresentante eseguito con successo.");

            return new ResponseEntity<ApiResult>(new ApiResult(Constants.RESULT_OK, "Successfully created rappresentante", rappresentante), HttpStatus.CREATED);
    	} catch (MyExtranetValidationException e) {
    		LOG.error("Validation exception", e);
    		return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_VALIDATION_MESSAGE, e.getMessage(), Constants.API_ERROR_VALIDATION_CODE), HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			LOG.error("RappresentanteEnteRapRESTController --> POST nuovo rappresentante non riuscito", e);
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
    	LOG.debug("RappresentanteEnteRapRESTController --> PATCH rappresentante." );

        // check permission
        try {
			securityService.checkPermission(user, Constants.ACL_ENTI, Constants.PERMISSION_GESTISCI);
		} catch (MyExtranetSecurityException e) {
			LOG.error(String.format("Access denied for user %s", user.getUsername()), e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_ACCESS_DENIED_TO_DATA_MESSAGE, e.getMessage(), Constants.API_ERROR_SECURITY_CODE), HttpStatus.FORBIDDEN);
		}

    	try {
    		ObjectMapper mapper = new ObjectMapper();
    		RappresentanteEnteDTO patchRappresentanteEnte = mapper.readValue(newRappresentanteEnteStr, RappresentanteEnteDTO.class);

            // check object does not have an Id
            if (patchRappresentanteEnte.getIdRappr() == null) {
            	LOG.error("RappresentanteEnteRapRESTController --> PATCH rappresentante usato impropriamente: l'oggetto non ha un ID");
            	return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_VALIDATION_MESSAGE, "l'oggetto non ha un ID", Constants.API_ERROR_VALIDATION_CODE), HttpStatus.BAD_REQUEST);
            }

            // force correct type
            patchRappresentanteEnte.setTipoRappr(Constants.RAPPRESENTANTE_ENTE_TIPO_RAP);

            // true in frontoffice
            boolean isFrontoffice = false;

            RappresentanteEnteDTO rappresentante = rappresentanteEnteService.updateRappresentanteEnte(patchRappresentanteEnte, true, isFrontoffice, false);
            LOG.debug("RappresentanteEnteRapRESTController --> PATCH rappresentante eseguito con successo.");

            return new ResponseEntity<ApiResult>(new ApiResult(Constants.RESULT_OK, "Successfully patched rappresentante", rappresentante), HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("RappresentanteEnteRapRESTController --> PATCH rappresentante non riuscito", e);
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
    	LOG.debug("RappresentanteEnteRapRESTController --> PUT rappresentante." );

    	// check permission
    	try {
    		securityService.checkPermission(user, Constants.ACL_ENTI, Constants.PERMISSION_GESTISCI);
    	} catch (MyExtranetSecurityException e) {
    		LOG.error(String.format("Access denied for user %s", user.getUsername()), e);
    		return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_ACCESS_DENIED_TO_DATA_MESSAGE, e.getMessage(), Constants.API_ERROR_SECURITY_CODE), HttpStatus.FORBIDDEN);
    	}

    	try {

    		ObjectMapper mapper = new ObjectMapper();
    		RappresentanteEnteDTO updateRappresentanteEnte = mapper.readValue(newRappresentanteEnteStr, RappresentanteEnteDTO.class);

    		validate(updateRappresentanteEnte, bindingResult);

            // check object does not have an Id
            if (updateRappresentanteEnte.getIdRappr() == null) {
            	LOG.error("RappresentanteEnteRapRESTController --> PUT rappresentante usato impropriamente: l'oggetto non ha un ID");
            	return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_VALIDATION_MESSAGE, "l'oggetto non ha un ID", Constants.API_ERROR_VALIDATION_CODE), HttpStatus.BAD_REQUEST);
            }

            // force correct type
            updateRappresentanteEnte.setTipoRappr(Constants.RAPPRESENTANTE_ENTE_TIPO_RAP);

            // true in frontoffice
            boolean isFrontoffice = false;

            RappresentanteEnteDTO rappresentante = rappresentanteEnteService.updateRappresentanteEnte(updateRappresentanteEnte, false, isFrontoffice, false);
            LOG.debug("RappresentanteEnteRapRESTController --> PUT rappresentante eseguito con successo.");

            return new ResponseEntity<ApiResult>(new ApiResult(Constants.RESULT_OK, "Successfully updated rappresentante", rappresentante), HttpStatus.OK);
    	} catch (MyExtranetValidationException e) {
    		LOG.error("Validation exception", e);
    		return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_VALIDATION_MESSAGE, e.getMessage(), Constants.API_ERROR_VALIDATION_CODE), HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			LOG.error("RappresentanteEnteRapRESTController --> PUT rappresentante non riuscito", e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_GENERIC_MESSAGE, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}

    }

    /**
     * Metodo POST per confermare o rifiutare un rappresentante
     * @return rappresentante
     * @throws Exception
     */
    @Operation(
    		summary = "Metodo POST per confermare o rifiutare un rappresentante",
			responses = {
                    @ApiResponse(description = "POST per confermare o rifiutare un rappresentante",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Object.class))),
                    @ApiResponse(responseCode = "200", description = "Successfully updated rappresentante"),
                    @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
                    @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
                    @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
            }
    )
    @PostMapping("conferma-rappresentante")
    public ResponseEntity<ApiResult> confirmRappresentanteEnte(
    		@RequestBody final @Valid RappresentanteEnteConfermaDTO rappresentanteEnteConferma,
    		@AuthenticationPrincipal UserWithAdditionalInfo user
    		) {
    	LOG.debug("RappresentanteEnteRapRESTController --> POST conferma rappresentante." );

    	// check permission
    	try {
    		securityService.checkPermission(user, Constants.ACL_ENTI, Constants.PERMISSION_GESTISCI);
    	} catch (MyExtranetSecurityException e) {
    		LOG.error(String.format("Access denied for user %s", user.getUsername()), e);
    		return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_ACCESS_DENIED_TO_DATA_MESSAGE, e.getMessage(), Constants.API_ERROR_SECURITY_CODE), HttpStatus.FORBIDDEN);
    	}

    	try {

            // check the idRappr is present
            if (rappresentanteEnteConferma.getIdRappr() == null) {
            	LOG.error("RappresentanteEnteRapRESTController --> POST conferma rappresentante senza idRappr");
            	return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_VALIDATION_MESSAGE, "id rappresentante obbligatorio", Constants.API_ERROR_VALIDATION_CODE), HttpStatus.BAD_REQUEST);
            }

            // check there is a motivation if conferma is false
            if (!Constants.RAPPRESENTANTE_ENTE_FLG_CONFERMA_CONFERMATA.equals(rappresentanteEnteConferma.getFlgConferma()) && rappresentanteEnteConferma.getMotivazione() == null) {
            	LOG.error("RappresentanteEnteRapRESTController --> POST conferma rappresentante negativo senza motivazione");
            	return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_VALIDATION_MESSAGE, "Motivazione obbligatoria se la conferma è negativa", Constants.API_ERROR_VALIDATION_CODE), HttpStatus.BAD_REQUEST);
            }


			if (Constants.RAPPRESENTANTE_ENTE_FLG_CONFERMA_CONFERMATA.equals(rappresentanteEnteConferma.getFlgConferma()) && (rappresentanteEnteConferma.getNumProtocollo() == null || rappresentanteEnteConferma.getDtProtocollo() == null)) {
				LOG.error("RappresentanteEnteRapRESTController --> POST conferma rappresentante negativo senza protocollo");
				return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_VALIDATION_MESSAGE, "Dati protocollo obbligatori se la conferma è positiva", Constants.API_ERROR_VALIDATION_CODE), HttpStatus.BAD_REQUEST);
			}

            RappresentanteEnteDTO updateRappresentanteEnte = new RappresentanteEnteDTO();
            updateRappresentanteEnte.setIdRappr(rappresentanteEnteConferma.getIdRappr());
            updateRappresentanteEnte.setFlgConferma(rappresentanteEnteConferma.getFlgConferma());
			updateRappresentanteEnte.setNumProtocollo(rappresentanteEnteConferma.getNumProtocollo());
			updateRappresentanteEnte.setDtProtocollo(rappresentanteEnteConferma.getDtProtocollo());
			updateRappresentanteEnte.setMotivConferma(rappresentanteEnteConferma.getMotivazione());

            // true in frontoffice
            boolean isFrontoffice = false;

            RappresentanteEnteDTO rappresentante = rappresentanteEnteService.updateRappresentanteEnte(updateRappresentanteEnte, true, isFrontoffice, false);
            LOG.debug("RappresentanteEnteRapRESTController --> POST conferma rappresentante eseguito con successo.");

            return new ResponseEntity<ApiResult>(new ApiResult(Constants.RESULT_OK, "Successfully updated rappresentante", rappresentante), HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("RappresentanteEnteRapRESTController --> POST conferma rappresentante non riuscito", e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_GENERIC_MESSAGE, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}

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


	public RappresentanteEnteRapRESTController() {
		
	}

}
