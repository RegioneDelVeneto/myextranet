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
import org.springframework.validation.BindingResult;
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
import it.regioneveneto.myp3.myextranet.exception.MyExtranetException;
import it.regioneveneto.myp3.myextranet.exception.MyExtranetSecurityException;
import it.regioneveneto.myp3.myextranet.exception.MyExtranetValidationException;
import it.regioneveneto.myp3.myextranet.security.UserWithAdditionalInfo;
import it.regioneveneto.myp3.myextranet.service.EnteService;
import it.regioneveneto.myp3.myextranet.service.MyBoxService;
import it.regioneveneto.myp3.myextranet.service.SecurityService;
import it.regioneveneto.myp3.myextranet.utils.Constants;
import it.regioneveneto.myp3.myextranet.web.dto.EnteDTO;
import it.regioneveneto.myp3.myextranet.web.dto.EnteFilterDTO;
import it.regioneveneto.myp3.myextranet.web.dto.IscrittoEventoDTO;
import it.regioneveneto.myp3.myextranet.web.dto.PagedData;

@RestController
@RequestMapping("/ente")
@Tag(name = "tag_ente", description = "Ente")
public class EnteRESTController extends ValidationController {
    private static final Logger LOG = LoggerFactory.getLogger(EnteRESTController.class);

    @Autowired
    private EnteService enteService;
    
    @Autowired
    private SecurityService securityService;
    
    @Autowired
    private MyBoxService myBoxService;

    /**
     * Metodo GET per recuperare gli enti
     * @return enti
     * @throws Exception
     */
    @Operation(
    		summary = "Metodo GET per recuperare gli enti",
			responses = {
                    @ApiResponse(description = "GET per recuperare gli enti",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Object.class))),
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved enti"),
                    @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
                    @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
                    @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
            }
    )

    @GetMapping("")
    public ResponseEntity<PagedData<EnteDTO>> getAllEnti(
    		@ModelAttribute( name = "_filter" ) EnteFilterDTO enteFilter,
    		@RequestParam( name = "_page", required = false, defaultValue = "0" ) final Integer pageNumber,
    		@RequestParam( name = "_pageSize", required = false, defaultValue = "10" ) final Integer pageSize,
    		@RequestParam( name = "_orderBy", required = false ) final String orderProperty,
    		@RequestParam( name = "_orderDir", required = false ) final String orderDirection,
    		@AuthenticationPrincipal UserWithAdditionalInfo user
    		) {
    	LOG.debug("EnteRESTController --> GET Lista enti." );
    	
    	// check permission
    	try {
    		securityService.checkPermission(user, Constants.ACL_ENTI, Constants.PERMISSION_VISUALIZZA);
    	} catch (MyExtranetSecurityException e) {
    		LOG.error(String.format("Access denied for user %s", user.getUsername()), e);
    		return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_ACCESS_DENIED_TO_DATA_MESSAGE, e.getMessage(), Constants.API_ERROR_SECURITY_CODE), HttpStatus.FORBIDDEN);
    	}

        PagedData<EnteDTO> enti = enteService.getEnti(enteFilter, pageNumber, pageSize, orderProperty, orderDirection);
        LOG.debug("EnteRESTController --> GET Lista enti eseguito con successo.");

        return new ResponseEntity<PagedData<EnteDTO>>(enti, HttpStatus.OK);

    }
    
    /**
     * Metodo GET per recuperare un ente
     * @return enti
     * @throws Exception
     */
    @Operation(
    		summary = "Metodo GET per un ente tramite id",
			responses = {
                    @ApiResponse(description = "GET per recuperare un ente",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Object.class))),
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved ente"),
                    @ApiResponse(responseCode = "400", description = "Ente not found"),
                    @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
                    @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
                    @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
            }
    )

    @GetMapping("/{_id}")
    public ResponseEntity<EnteDTO> getEnteById(
    		@PathVariable( name = "_id", required = true) final Integer idEnte,
    		@AuthenticationPrincipal UserWithAdditionalInfo user
    		) {
    	LOG.debug("EnteRESTController --> GET ente with id " + idEnte);
    	
        // check permission
        try {
			securityService.checkPermission(user, Constants.ACL_ENTI, Constants.PERMISSION_VISUALIZZA);
		} catch (MyExtranetSecurityException e) {
			LOG.error(String.format("Access denied for user %s", user.getUsername()), e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_ACCESS_DENIED_TO_DATA_MESSAGE, e.getMessage(), Constants.API_ERROR_SECURITY_CODE), HttpStatus.FORBIDDEN);
		}

        EnteDTO ente;
		try {
			ente = enteService.getEnte(idEnte);
			
			// get logo metadata
			if (ente.getLogo() != null) {
				populateLogoMetadata(ente);
			}

			LOG.debug("EnteRESTController --> GET ente eseguito con successo.");

        	return new ResponseEntity<EnteDTO>(ente, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("EnteRESTController --> GET ente non riuscito", e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_GENERIC_MESSAGE, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
    }
    
    private void populateLogoMetadata(EnteDTO ente) {
		
    	String logoStr = ente.getLogo();
    	if (logoStr.length() >= 32) {
    		try {
				String id = logoStr.substring(logoStr.length() - 32);
				ContentMetadata metadata = myBoxService.getFileMetadata(id);
				
				myBoxService.cleanMetadataFileName(metadata);
				
				ente.setLogoFileMetadata(metadata);
			} catch (MyExtranetException e) {
				LOG.error("Cannot retrieve MyBox metadata", e);
			}
    		
    	}
    	
	}

    
    /**
     * Metodo GET per recuperare gli enti in caso di autocomplete
     * @return enti
     * @throws Exception
     */
    @Operation(
    		summary = "Metodo GET per recuperare gli enti",
			responses = {
                    @ApiResponse(description = "GET per recuperare gli enti in caso di autocomplete",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Object.class))),
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved enti"),
                    @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
                    @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
                    @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
            }
    )
    
    @GetMapping("/autocomplete")
    public ResponseEntity<List<EnteDTO>> getEntiAutocomplete(
    		@ModelAttribute( name = "_filter" ) EnteFilterDTO enteFilter,
    		@RequestParam( name = "maxLength", required = false, defaultValue = "20" ) final Integer maxLength,
    		@RequestParam( name = "_orderBy", required = false, defaultValue = "denominazione" ) final String orderProperty,
    		@RequestParam( name = "_orderDir", required = false, defaultValue = "asc" ) final String orderDirection,
    		@AuthenticationPrincipal UserWithAdditionalInfo user
    		) {
    	LOG.debug("EnteRESTController --> GET Lista enti (autocomplete)." );

        List<EnteDTO> enti = enteService.getEnti(enteFilter, 1, maxLength, orderProperty, orderDirection).getRecords();
        LOG.debug("EnteRESTController --> GET Lista enti eseguito con successo.");

        return new ResponseEntity<List<EnteDTO>>(enti, HttpStatus.OK);

    }
    
    /**
     * 
     * @param newEnte
     * @return
     */
    @Operation(
    		summary = "Metodo POST per inserire un ente",
			responses = {
                    @ApiResponse(description = "POST per inserire un ente",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Object.class))),
                    @ApiResponse(responseCode = "201", description = "Successfully created ente"),
                    @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
                    @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
                    @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
            }
    )
    @PostMapping("")
    public synchronized ResponseEntity<ApiResult> insertEnte(
    		@RequestPart(name = "newEnte", required = true) String newEnteStr,
    		BindingResult bindingResult,
    		@RequestPart(name = "logoFile", required = false) MultipartFile logoFile,
    		@AuthenticationPrincipal UserWithAdditionalInfo user
    		) {
    	LOG.debug("EnteRESTController --> POST nuovo ente");
    	
        // check permission
        try {
        	securityService.checkPermission(user, Constants.ACL_ENTI, Constants.PERMISSION_GESTISCI);
		} catch (MyExtranetSecurityException e) {
			LOG.error(String.format("Access denied for user %s", user.getUsername()), e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_ACCESS_DENIED_TO_DATA_MESSAGE, e.getMessage(), Constants.API_ERROR_SECURITY_CODE), HttpStatus.FORBIDDEN);
		}
    	
    	try {
    		
    		ObjectMapper mapper = new ObjectMapper();
    		EnteDTO newEnte = mapper.readValue(newEnteStr, EnteDTO.class);
    		
    		if (logoFile != null) {
    			newEnte.setLogoFileInputStream(logoFile.getInputStream());
        		ContentMetadata contentMetadata = new ContentMetadata();
        		contentMetadata.setFileName(String.format("%s_%s", logoFile.getOriginalFilename(), UUID.randomUUID()));
        		contentMetadata.setLength(logoFile.getSize());
        		contentMetadata.setMimeType(logoFile.getContentType());
    			newEnte.setLogoFileMetadata(contentMetadata);
    		}
    		
    		validate(newEnte, bindingResult);
            
            // check object does not have an Id
            if (newEnte.getIdEnte() != null) {
            	LOG.error("EnteRESTController --> POST nuovo ente usato impropriamente: l'oggetto ha un ID");
            	return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_VALIDATION_MESSAGE, "l'oggetto ha un ID", Constants.API_ERROR_VALIDATION_CODE), HttpStatus.BAD_REQUEST);
            }
            
            EnteDTO ente = enteService.insertEnte(newEnte);
            LOG.debug("EnteRESTController --> POST nuovo ente eseguito con successo.");

            return new ResponseEntity<ApiResult>(new ApiResult(Constants.RESULT_OK, "Successfully created ente", ente), HttpStatus.CREATED);			
    	} catch (MyExtranetValidationException e) {
    		LOG.error("Validation exception", e);
    		return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_VALIDATION_MESSAGE, e.getMessage(), Constants.API_ERROR_VALIDATION_CODE), HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			LOG.error("EnteRESTController --> POST nuovo ente non riuscito", e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_GENERIC_MESSAGE, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}

    }

    /**
     * Metodo PATCH per modificare parzialmente un ente
     * @return ente
     * @throws Exception
     */
    @Operation(
    		summary = "Metodo PATCH per modificare parzialmente un ente",
			responses = {
                    @ApiResponse(description = "PATCH per modificare parzialmente un ente",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Object.class))),
                    @ApiResponse(responseCode = "200", description = "Successfully patched ente"),
                    @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
                    @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
                    @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
            }
    )
    @PatchMapping("")
    public ResponseEntity<ApiResult> patchEnte(
    		@RequestPart(name = "newEnte", required = true) String newEnteStr,
    		@RequestPart(name = "logoFile", required = false) MultipartFile logoFile,
    		@AuthenticationPrincipal UserWithAdditionalInfo user
    		) {
    	LOG.debug("EnteRESTController --> PATCH ente." );
    	
        // check permission
        try {
        	securityService.checkPermission(user, Constants.ACL_ENTI, Constants.PERMISSION_GESTISCI);
		} catch (MyExtranetSecurityException e) {
			LOG.error(String.format("Access denied for user %s", user.getUsername()), e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_ACCESS_DENIED_TO_DATA_MESSAGE, e.getMessage(), Constants.API_ERROR_SECURITY_CODE), HttpStatus.FORBIDDEN);
		}
    	
    	try {
    		
    		ObjectMapper mapper = new ObjectMapper();
    		EnteDTO patchEnte = mapper.readValue(newEnteStr, EnteDTO.class);
    		
    		if (logoFile != null) {
    			patchEnte.setLogoFileInputStream(logoFile.getInputStream());
        		ContentMetadata contentMetadata = new ContentMetadata();
        		contentMetadata.setFileName(String.format("%s_%s", logoFile.getOriginalFilename(), UUID.randomUUID()));
        		contentMetadata.setLength(logoFile.getSize());
        		contentMetadata.setMimeType(logoFile.getContentType());
        		patchEnte.setLogoFileMetadata(contentMetadata);
    		}
            
            // check object does not have an Id
            if (patchEnte.getIdEnte() == null) {
            	LOG.error("EnteRESTController --> PATCH ente usato impropriamente: l'oggetto non ha un ID");
            	return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_VALIDATION_MESSAGE, "l'oggetto non ha un ID", Constants.API_ERROR_VALIDATION_CODE), HttpStatus.BAD_REQUEST);
            }
            
            EnteDTO ente = enteService.updateEnte(patchEnte, true);
            LOG.debug("EnteRESTController --> PATCH ente eseguito con successo.");

            return new ResponseEntity<ApiResult>(new ApiResult(Constants.RESULT_OK, "Successfully patched ente", ente), HttpStatus.OK);		
    	} catch (MyExtranetValidationException e) {
    		LOG.error("Validation exception", e);
    		return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_VALIDATION_MESSAGE, e.getMessage(), Constants.API_ERROR_VALIDATION_CODE), HttpStatus.BAD_REQUEST);		
		} catch (Exception e) {
			LOG.error("EnteRESTController --> PATCH ente non riuscito", e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_GENERIC_MESSAGE, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}

    }  

    /**
     * Metodo PUT per modificare un ente
     * @return ente
     * @throws Exception
     */
    @Operation(
    		summary = "Metodo PUT per modificare un ente",
			responses = {
                    @ApiResponse(description = "PUT per modificare un ente",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Object.class))),
                    @ApiResponse(responseCode = "200", description = "Successfully updated ente"),
                    @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
                    @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
                    @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
            }
    )
    @PutMapping("")
    public ResponseEntity<ApiResult> updateEnte(
    		@RequestPart(name = "newEnte", required = true) String newEnteStr,
    		BindingResult bindingResult,
    		@RequestPart(name = "logoFile", required = false) MultipartFile logoFile,
    		@AuthenticationPrincipal UserWithAdditionalInfo user
    		) {
    	LOG.debug("EnteRESTController --> PUT ente." );
    	
    	// check permission
    	try {
    		securityService.checkPermission(user, Constants.ACL_ENTI, Constants.PERMISSION_GESTISCI);
    	} catch (MyExtranetSecurityException e) {
    		LOG.error(String.format("Access denied for user %s", user.getUsername()), e);
    		return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_ACCESS_DENIED_TO_DATA_MESSAGE, e.getMessage(), Constants.API_ERROR_SECURITY_CODE), HttpStatus.FORBIDDEN);
    	}
    	
    	try {
    		
    		ObjectMapper mapper = new ObjectMapper();
    		EnteDTO updateEnte = mapper.readValue(newEnteStr, EnteDTO.class);
    		
    		if (logoFile != null) {
    			updateEnte.setLogoFileInputStream(logoFile.getInputStream());
        		ContentMetadata contentMetadata = new ContentMetadata();
        		contentMetadata.setFileName(String.format("%s_%s", logoFile.getOriginalFilename(), UUID.randomUUID()));
        		contentMetadata.setLength(logoFile.getSize());
        		contentMetadata.setMimeType(logoFile.getContentType());
        		updateEnte.setLogoFileMetadata(contentMetadata);
    		}
    		
    		validate(updateEnte, bindingResult);
            
            // check object does not have an Id
            if (updateEnte.getIdEnte() == null) {
            	LOG.error("EnteRESTController --> PUT ente usato impropriamente: l'oggetto non ha un ID");
            	return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_VALIDATION_MESSAGE, "l'oggetto non ha un ID", Constants.API_ERROR_VALIDATION_CODE), HttpStatus.BAD_REQUEST);
            }
            
            EnteDTO ente = enteService.updateEnte(updateEnte, false);
            LOG.debug("EnteRESTController --> PUT ente eseguito con successo.");

            return new ResponseEntity<ApiResult>(new ApiResult(Constants.RESULT_OK, "Successfully updated ente", ente), HttpStatus.OK);			
    	} catch (MyExtranetValidationException e) {
    		LOG.error("Validation exception", e);
    		return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_VALIDATION_MESSAGE, e.getMessage(), Constants.API_ERROR_VALIDATION_CODE), HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			LOG.error("EnteRESTController --> PUT ente non riuscito", e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_GENERIC_MESSAGE, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}

    }
}
