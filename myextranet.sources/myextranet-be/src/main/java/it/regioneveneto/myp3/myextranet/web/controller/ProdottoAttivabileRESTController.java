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

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
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
import it.regioneveneto.myp3.myextranet.exception.MyExtranetSecurityException;
import it.regioneveneto.myp3.myextranet.exception.MyExtranetValidationException;
import it.regioneveneto.myp3.myextranet.security.UserWithAdditionalInfo;
import it.regioneveneto.myp3.myextranet.service.ProdottoAttivabileService;
import it.regioneveneto.myp3.myextranet.service.SecurityService;
import it.regioneveneto.myp3.myextranet.utils.Constants;
import it.regioneveneto.myp3.myextranet.web.dto.PagedData;
import it.regioneveneto.myp3.myextranet.web.dto.ProdottoAttivabileDTO;
import it.regioneveneto.myp3.myextranet.web.dto.ProdottoAttivabileFilterDTO;

@RestController
@RequestMapping("/prodotto-attivabile")
@Tag(name = "tag_prodotto-attivabile", description = "Prodotto attivabile")
public class ProdottoAttivabileRESTController extends ValidationController {
    private static final Logger LOG = LoggerFactory.getLogger(ProdottoAttivabileRESTController.class);

    @Autowired
    private ProdottoAttivabileService prodottoAttivabileService;
    
    @Autowired
    private SecurityService securityService;

    /**
     * Metodo GET per recuperare i prodotti attivabili
     * @return prodotti attivabili
     * @throws Exception
     */
    @Operation(
    		summary = "Metodo GET per recuperare i prodotti attivabili",
			responses = {
                    @ApiResponse(description = "GET per recuperare i prodotti attivabili",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Object.class))),
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved prodotti attivabili"),
                    @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
                    @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
                    @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
            }
    )

    @GetMapping("")
    public ResponseEntity<PagedData<ProdottoAttivabileDTO>> getAllProdottiAttivabili(
    		@ModelAttribute( name = "_filter" ) ProdottoAttivabileFilterDTO prodottoAttivabileFilter,
    		@RequestParam( name = "_page", required = false, defaultValue = "0" ) final Integer pageNumber,
    		@RequestParam( name = "_pageSize", required = false, defaultValue = "10" ) final Integer pageSize,
    		@RequestParam( name = "_orderBy", required = false ) final String orderProperty,
    		@RequestParam( name = "_orderDir", required = false ) final String orderDirection,
    		@AuthenticationPrincipal UserWithAdditionalInfo user
    		) {
    	
        LOG.debug("ProdottoAttivabileRESTController --> GET Lista prodotti attivabili");
        
        // check permission
        try {
			securityService.checkPermission(user, Constants.ACL_PRODOTTI, Constants.PERMISSION_VISUALIZZA);
		} catch (MyExtranetSecurityException e) {
			LOG.error(String.format("Access denied for user %s", user.getUsername()), e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_ACCESS_DENIED_TO_DATA_MESSAGE, e.getMessage(), Constants.API_ERROR_SECURITY_CODE), HttpStatus.FORBIDDEN);
		}
        
        PagedData<ProdottoAttivabileDTO> prodotti = prodottoAttivabileService.getProdottiAttivabili(prodottoAttivabileFilter, pageNumber, pageSize, orderProperty, orderDirection);
        LOG.debug("ProdottoAttivabileRESTController --> GET Lista prodotti attivabili eseguito con successo.");

        return new ResponseEntity<PagedData<ProdottoAttivabileDTO>>(prodotti, HttpStatus.OK);

    }
    
    /**
     * Metodo GET per recuperare i prodotti attivabili
     * @return prodotti attivabili
     * @throws Exception
     */
    @Operation(
    		summary = "Metodo GET per recuperare un prodotto tramite id",
			responses = {
                    @ApiResponse(description = "GET per recuperare un prodotto",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Object.class))),
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved prodotto"),
                    @ApiResponse(responseCode = "400", description = "Collaboratore not found"),
                    @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
                    @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
                    @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
            }
    )

    @GetMapping("/{_id}")
    public ResponseEntity<ProdottoAttivabileDTO> getProdottoAttivabileById(
    		@PathVariable( name = "_id", required = true) final Integer idProdottoAtt,
    		@AuthenticationPrincipal UserWithAdditionalInfo user
    		) {
    	LOG.debug("ProdottoAttivabileRESTController --> GET prodotto with id " + idProdottoAtt);
    	
        // check permission
        try {
			securityService.checkPermission(user, Constants.ACL_PRODOTTI, Constants.PERMISSION_VISUALIZZA);
		} catch (MyExtranetSecurityException e) {
			LOG.error(String.format("Access denied for user %s", user.getUsername()), e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_ACCESS_DENIED_TO_DATA_MESSAGE, e.getMessage(), Constants.API_ERROR_SECURITY_CODE), HttpStatus.FORBIDDEN);
		}

        ProdottoAttivabileDTO prodotto;
		try {
			prodotto = prodottoAttivabileService.getProdottoAttivabile(idProdottoAtt);

			LOG.debug("ProdottoAttivabileRESTController --> GET prodotto eseguito con successo.");

        	return new ResponseEntity<ProdottoAttivabileDTO>(prodotto, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("ProdottoAttivabileRESTController --> GET prodotto non riuscito", e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_GENERIC_MESSAGE, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
    }
    
    /**
     * Metodo GET per recuperare i prodotti in caso di autocomplete
     * @return enti
     * @throws Exception
     */
    @Operation(
    		summary = "Metodo GET per recuperare gli enti",
			responses = {
                    @ApiResponse(description = "GET per recuperare i prodotti in caso di autocomplete",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Object.class))),
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved prodotti"),
                    @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
                    @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
                    @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
            }
    )
    
    @GetMapping("/autocomplete")
    public ResponseEntity<List<ProdottoAttivabileDTO>> getProdottiAttivabiliAutocomplete(
    		@ModelAttribute( name = "_filter" ) ProdottoAttivabileFilterDTO prodottoFilter,
    		@RequestParam( name = "maxLength", required = false, defaultValue = "20" ) final Integer maxLength,
    		@RequestParam( name = "_orderBy", required = false, defaultValue = "nomeProdottoAttiv" ) final String orderProperty,
    		@RequestParam( name = "_orderDir", required = false, defaultValue = "asc" ) final String orderDirection,
    		@AuthenticationPrincipal UserWithAdditionalInfo user
    		) {
    	LOG.debug("ProdottoAttivabileRESTController --> GET Lista prodotti (autocomplete)." );
    	
    	// check permission
    	try {
    		securityService.checkPermission(user, Constants.ACL_PRODOTTI, Constants.PERMISSION_VISUALIZZA);
    	} catch (MyExtranetSecurityException e) {
    		LOG.error(String.format("Access denied for user %s", user.getUsername()), e);
    		return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_ACCESS_DENIED_TO_DATA_MESSAGE, e.getMessage(), Constants.API_ERROR_SECURITY_CODE), HttpStatus.FORBIDDEN);
    	}
    	
        List<ProdottoAttivabileDTO> enti = prodottoAttivabileService.getProdottiAttivabili(prodottoFilter, 1, maxLength, orderProperty, orderDirection).getRecords();
        LOG.debug("ProdottoAttivabileRESTController --> GET Lista prodotti eseguito con successo.");

        return new ResponseEntity<List<ProdottoAttivabileDTO>>(enti, HttpStatus.OK);

    }
    
    /**
     * 
     * @param newProdottoAttivabile
     * @return
     */
    @Operation(
    		summary = "Metodo POST per inserire un prodotto attivabile",
			responses = {
                    @ApiResponse(description = "POST per inserire un prodotto attivabile",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Object.class))),
                    @ApiResponse(responseCode = "201", description = "Successfully created prodotto attivabile"),
                    @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
                    @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
                    @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
            }
    )
    @PostMapping("")
    public synchronized ResponseEntity<ApiResult> insertProdottoAttivabile(
    		@RequestBody final @Valid ProdottoAttivabileDTO newProdottoAttivabile,
    		BindingResult bindingResult,
    		@AuthenticationPrincipal UserWithAdditionalInfo user
    		) {
    	LOG.debug("ProdottoAttivabileRESTController --> POST nuovo prodotto attivabile");
    	
        // check permission
        try {
			securityService.checkPermission(user, Constants.ACL_PRODOTTI, Constants.PERMISSION_GESTISCI);
		} catch (MyExtranetSecurityException e) {
			LOG.error(String.format("Access denied for user %s", user.getUsername()), e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_ACCESS_DENIED_TO_DATA_MESSAGE, e.getMessage(), Constants.API_ERROR_SECURITY_CODE), HttpStatus.FORBIDDEN);
		}
    	
    	try {
    		
    		validate(newProdottoAttivabile, bindingResult);
            
            ProdottoAttivabileDTO prodotto = prodottoAttivabileService.insertProdottoAttivabile(newProdottoAttivabile);
                        
            LOG.debug("ProdottoAttivabileRESTController --> POST nuovo prodotto attivabile eseguito con successo.");

            return new ResponseEntity<ApiResult>(new ApiResult(Constants.RESULT_OK, "Successfully created prodotto attivabile", prodotto), HttpStatus.CREATED);			
    	} catch (MyExtranetValidationException e) {
    		LOG.error("Validation exception", e);
    		return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_VALIDATION_MESSAGE, e.getMessage(), Constants.API_ERROR_VALIDATION_CODE), HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			LOG.error("ProdottoAttivabileRESTController --> POST nuovo prodotto attivabile non riuscito", e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_GENERIC_MESSAGE, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}

    }

    /**
     * Metodo PATCH per modificare parzialmente un prodotto attivabile
     * @return prodotto
     * @throws Exception
     */
    @Operation(
    		summary = "Metodo PATCH per modificare parzialmente un prodotto attivabile",
			responses = {
                    @ApiResponse(description = "PATCH per modificare parzialmente un prodotto attivabile",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Object.class))),
                    @ApiResponse(responseCode = "200", description = "Successfully patched prodotto attivabile"),
                    @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
                    @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
                    @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
            }
    )
    @PatchMapping("")
    public ResponseEntity<ApiResult> patchProdottoAttivabile(
    		@RequestBody final ProdottoAttivabileDTO patchProdottoAttivabile,
    		@AuthenticationPrincipal UserWithAdditionalInfo user
    		) {
    	LOG.debug("ProdottoAttivabileRESTController --> PATCH prodotto attivabile." );
    	
        // check permission
        try {
			securityService.checkPermission(user, Constants.ACL_PRODOTTI, Constants.PERMISSION_GESTISCI);
		} catch (MyExtranetSecurityException e) {
			LOG.error(String.format("Access denied for user %s", user.getUsername()), e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_ACCESS_DENIED_TO_DATA_MESSAGE, e.getMessage(), Constants.API_ERROR_SECURITY_CODE), HttpStatus.FORBIDDEN);
		}
    	
    	try {
            
            // check object does not have an Id
            if (patchProdottoAttivabile.getIdProdottoAtt() == null) {
            	LOG.error("ProdottoAttivabileRESTController --> PATCH prodotto usato impropriamente: l'oggetto non ha un ID");
            	return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_VALIDATION_MESSAGE, "l'oggetto non ha un ID", Constants.API_ERROR_VALIDATION_CODE), HttpStatus.BAD_REQUEST);
            }
            
            ProdottoAttivabileDTO prodotto = prodottoAttivabileService.updateProdottoAttivabile(patchProdottoAttivabile, true);
            LOG.debug("ProdottoAttivabileRESTController --> PATCH prodotto attivabile eseguito con successo.");

            return new ResponseEntity<ApiResult>(new ApiResult(Constants.RESULT_OK, "Successfully patched prodotto attivabile", prodotto), HttpStatus.OK);			
		} catch (Exception e) {
			LOG.error("ProdottoAttivabileRESTController --> PATCH prodotto attivabile non riuscito", e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_GENERIC_MESSAGE, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}

    }
        
    /**
     * Metodo PUT per modificare un prodotto attivabile
     * @return prodotto attivabile
     * @throws Exception
     */
    @Operation(
    		summary = "Metodo PUT per modificare un prodotto attivabile",
			responses = {
                    @ApiResponse(description = "PUT per modificare un prodotto attivabile",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Object.class))),
                    @ApiResponse(responseCode = "200", description = "Successfully updated prodotto attivabile"),
                    @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
                    @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
                    @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
            }
    )
    @PutMapping("")
    public ResponseEntity<ApiResult> updateProdottoAttivabile(
    		@RequestBody final @Valid ProdottoAttivabileDTO updateProdottoAttivabile,
    		BindingResult bindingResult,
    		@AuthenticationPrincipal UserWithAdditionalInfo user
    		) {
    	LOG.debug("ProdottoAttivabileRESTController --> PUT prodotto attivabile." );
    	
    	// check permission
    	try {
    		securityService.checkPermission(user, Constants.ACL_PRODOTTI, Constants.PERMISSION_GESTISCI);
    	} catch (MyExtranetSecurityException e) {
    		LOG.error(String.format("Access denied for user %s", user.getUsername()), e);
    		return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_ACCESS_DENIED_TO_DATA_MESSAGE, e.getMessage(), Constants.API_ERROR_SECURITY_CODE), HttpStatus.FORBIDDEN);
    	}
    	
    	try {
    		validate(updateProdottoAttivabile, bindingResult);
            
            // check object does not have an Id
            if (updateProdottoAttivabile.getIdProdottoAtt() == null) {
            	LOG.error("ProdottoAttivabileRESTController --> PUT prodotto attivabile usato impropriamente: l'oggetto non ha un ID");
            	return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_VALIDATION_MESSAGE, "l'oggetto non ha un ID", Constants.API_ERROR_VALIDATION_CODE), HttpStatus.BAD_REQUEST);
            }
            
            ProdottoAttivabileDTO prodotto = prodottoAttivabileService.updateProdottoAttivabile(updateProdottoAttivabile, false);
            LOG.debug("ProdottoAttivabileRESTController --> PUT prodotto attivabile eseguito con successo.");

            return new ResponseEntity<ApiResult>(new ApiResult(Constants.RESULT_OK, "Successfully updated prodotto attivabile", prodotto), HttpStatus.OK);			
    	} catch (MyExtranetValidationException e) {
    		LOG.error("Validation exception", e);
    		return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_VALIDATION_MESSAGE, e.getMessage(), Constants.API_ERROR_VALIDATION_CODE), HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			LOG.error("ProdottoAttivabileRESTController --> PUT prodotto attivabile non riuscito", e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_GENERIC_MESSAGE, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}

    }

    /**
     * Metodo DELETE per eliminare un prodotto
     * @return prodotto eliminato
     * @throws Exception
     */
    @Operation(
    		summary = "Metodo DELETE per eliminare un prodotto",
			responses = {
                    @ApiResponse(description = "DELETE per eliminare un prodotto",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Object.class))),
                    @ApiResponse(responseCode = "200", description = "Successfully deleted prodotto"),
                    @ApiResponse(responseCode = "400", description = "prodotto not found"),
                    @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
                    @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
                    @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
            }
    )
    @DeleteMapping("/{_id}")
    public ResponseEntity<ApiResult> deleteProdottoById(
    		@PathVariable( name = "_id", required = true) final Integer idProdottoAtt,
    		@AuthenticationPrincipal UserWithAdditionalInfo user
    		) {
    	LOG.debug("ProdottoAttivabileRESTController --> DELETE prodotto with id " + idProdottoAtt);
    	
        // check permission
        try {
			securityService.checkPermission(user, Constants.ACL_PRODOTTI, Constants.PERMISSION_GESTISCI);
		} catch (MyExtranetSecurityException e) {
			LOG.error(String.format("Access denied for user %s", user.getUsername()), e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_ACCESS_DENIED_TO_DATA_MESSAGE, e.getMessage(), Constants.API_ERROR_SECURITY_CODE), HttpStatus.FORBIDDEN);
		}

        ProdottoAttivabileDTO prodotto;
		try {
			prodotto = prodottoAttivabileService.deleteProdottoAttivabile(idProdottoAtt);

			LOG.debug("ProdottoAttivabileRESTController --> DELETE prodotto eseguito con successo.");

			return new ResponseEntity<ApiResult>(new ApiResult(Constants.RESULT_OK, "Successfully deleted prodotto", prodotto), HttpStatus.OK);
		} catch (DataIntegrityViolationException e) {
			LOG.error("Cannot delete Prodotto Attivabile with idProdottoAtt = " + idProdottoAtt, e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_VALIDATION_MESSAGE, "Impossibile eliminare l'oggetto in quanto referenziato da altri oggetti", Constants.API_ERROR_VALIDATION_CODE), HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			LOG.error("ProdottoAttivabileRESTController --> DELETE prodotto non riuscito", e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_GENERIC_MESSAGE, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
    }

    
}
