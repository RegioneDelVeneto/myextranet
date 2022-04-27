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
import it.regioneveneto.myp3.myextranet.service.ProdottoAttivatoService;
import it.regioneveneto.myp3.myextranet.service.RichiestaProdottoService;
import it.regioneveneto.myp3.myextranet.service.SecurityService;
import it.regioneveneto.myp3.myextranet.utils.Constants;
import it.regioneveneto.myp3.myextranet.web.dto.AggiornaRichiestaProdottoAttivatoInputBackofficeDTO;
import it.regioneveneto.myp3.myextranet.web.dto.EnteDTO;
import it.regioneveneto.myp3.myextranet.web.dto.GenericAvanzamentoStepRichiestaInputDTO;
import it.regioneveneto.myp3.myextranet.web.dto.MessaggioDTO;
import it.regioneveneto.myp3.myextranet.web.dto.PagedData;
import it.regioneveneto.myp3.myextranet.web.dto.ProdottoAttivatoComunicazioneFilterDTO;
import it.regioneveneto.myp3.myextranet.web.dto.ProdottoAttivatoDTO;
import it.regioneveneto.myp3.myextranet.web.dto.ProdottoAttivatoFilterDTO;
import it.regioneveneto.myp3.myextranet.web.dto.ProdottoAttivatoWithStatusDTO;

@RestController
@RequestMapping("/prodotto-attivato")
@Tag(name = "tag_prodotto-attivato", description = "Prodotto attivato")
public class ProdottoAttivatoRESTController extends ValidationController {
    private static final Logger LOG = LoggerFactory.getLogger(ProdottoAttivatoRESTController.class);

    @Autowired
    private ProdottoAttivatoService prodottoAttivatoService;
    
    @Autowired
    private RichiestaProdottoService richiestaProdottoService;
    
    @Autowired
    private SecurityService securityService;

    /**
     * Metodo GET per recuperare i prodotti attivati
     * @return prodotti attivati
     * @throws Exception
     */
    @Operation(
    		summary = "Metodo GET per recuperare i prodotti attivati",
			responses = {
                    @ApiResponse(description = "GET per recuperare i prodotti attivati",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Object.class))),
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved prodotti attivati"),
                    @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
                    @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
                    @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
            }
    )

    @GetMapping("")
    public ResponseEntity<PagedData<ProdottoAttivatoWithStatusDTO>> getAllProdottiAttivati(
    		@ModelAttribute( name = "_filter" ) ProdottoAttivatoFilterDTO prodottoAttivatoFilter,
    		@RequestParam( name = "_page", required = false, defaultValue = "0" ) final Integer pageNumber,
    		@RequestParam( name = "_pageSize", required = false, defaultValue = "10" ) final Integer pageSize,
    		@RequestParam( name = "_orderBy", required = false ) final String orderProperty,
    		@RequestParam( name = "_orderDir", required = false ) final String orderDirection,
    		@AuthenticationPrincipal UserWithAdditionalInfo user
    		) {
    	
        LOG.debug("ProdottoAttivatoRESTController --> GET Lista prodotti attivati");
        
        // check permission
        try {
			securityService.checkPermission(user, Constants.ACL_PRODOTTI, Constants.PERMISSION_VISUALIZZA);
		} catch (MyExtranetSecurityException e) {
			LOG.error(String.format("Access denied for user %s", user.getUsername()), e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_ACCESS_DENIED_TO_DATA_MESSAGE, e.getMessage(), Constants.API_ERROR_SECURITY_CODE), HttpStatus.FORBIDDEN);
		}
        
        PagedData<ProdottoAttivatoWithStatusDTO> prodotti = prodottoAttivatoService.getProdottiAttivati(prodottoAttivatoFilter, pageNumber, pageSize, orderProperty, orderDirection);
        LOG.debug("ProdottoAttivatoRESTController --> GET Lista prodotti attivati eseguito con successo.");

        return new ResponseEntity<PagedData<ProdottoAttivatoWithStatusDTO>>(prodotti, HttpStatus.OK);

    }
    
    /**
     * Metodo GET per recuperare un prodotto attivato tramite id
     * @return prodotto attivato
     * @throws Exception
     */
    @Operation(
    		summary = "Metodo GET per recuperare un prodotto attivato tramite id",
			responses = {
                    @ApiResponse(description = "GET per recuperare un prodotto attivato",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Object.class))),
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved prodotto attivato"),
                    @ApiResponse(responseCode = "400", description = "Collaboratore not found"),
                    @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
                    @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
                    @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
            }
    )

    @GetMapping("/{_id}")
    public ResponseEntity<ProdottoAttivatoWithStatusDTO> getProdottoAttivatoById(
    		@PathVariable( name = "_id", required = true) final Integer idAttivazione,
    		@AuthenticationPrincipal UserWithAdditionalInfo user
    		) {
    	LOG.debug("ProdottoAttivatoRESTController --> GET prodotto attivato with id " + idAttivazione);
    	
        // check permission
        try {
			securityService.checkPermission(user, Constants.ACL_PRODOTTI, Constants.PERMISSION_VISUALIZZA);
		} catch (MyExtranetSecurityException e) {
			LOG.error(String.format("Access denied for user %s", user.getUsername()), e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_ACCESS_DENIED_TO_DATA_MESSAGE, e.getMessage(), Constants.API_ERROR_SECURITY_CODE), HttpStatus.FORBIDDEN);
		}

        ProdottoAttivatoWithStatusDTO prodotto;
		try {
			prodotto = prodottoAttivatoService.getProdottoAttivatoWithStatus(idAttivazione);

			LOG.debug("ProdottoAttivatoRESTController --> GET prodotto attivato eseguito con successo.");

        	return new ResponseEntity<ProdottoAttivatoWithStatusDTO>(prodotto, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("ProdottoAttivatoRESTController --> GET prodotto attivato non riuscito", e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_GENERIC_MESSAGE, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
    }
    
    /**
     * 
     * @param newProdottoAttivato
     * @return
     */
    @Operation(
    		summary = "Metodo POST per inserire un prodotto attivato",
			responses = {
                    @ApiResponse(description = "POST per inserire un prodotto attivato",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Object.class))),
                    @ApiResponse(responseCode = "201", description = "Successfully created prodotto attivato"),
                    @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
                    @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
                    @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
            }
    )
    @PostMapping("")
    public synchronized ResponseEntity<ApiResult> insertProdottoAttivato(
    		@RequestBody final @Valid ProdottoAttivatoDTO newProdottoAttivato,
    		BindingResult bindingResult,
    		@AuthenticationPrincipal UserWithAdditionalInfo user
    		) {
    	LOG.debug("ProdottoAttivatoRESTController --> POST nuovo prodotto attivato");
    	
        // check permission
        try {
			securityService.checkPermission(user, Constants.ACL_PRODOTTI, Constants.PERMISSION_GESTISCI);
		} catch (MyExtranetSecurityException e) {
			LOG.error(String.format("Access denied for user %s", user.getUsername()), e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_ACCESS_DENIED_TO_DATA_MESSAGE, e.getMessage(), Constants.API_ERROR_SECURITY_CODE), HttpStatus.FORBIDDEN);
		}
    	
    	try {
    		
    		validate(newProdottoAttivato, bindingResult);
            
            ProdottoAttivatoDTO prodotto = prodottoAttivatoService.insertProdottoAttivato(newProdottoAttivato);
                        
            LOG.debug("ProdottoAttivatoRESTController --> POST nuovo prodotto attivato eseguito con successo.");

            return new ResponseEntity<ApiResult>(new ApiResult(Constants.RESULT_OK, "Successfully created prodotto attivato", prodotto), HttpStatus.CREATED);			
    	} catch (MyExtranetValidationException e) {
    		LOG.error("Validation exception", e);
    		return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_VALIDATION_MESSAGE, e.getMessage(), Constants.API_ERROR_VALIDATION_CODE), HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			LOG.error("ProdottoAttivatoRESTController --> POST nuovo prodotto attivato non riuscito", e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_GENERIC_MESSAGE, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}

    }
    
    /**
     * 
     * @param aggiornaRichiestaProdotto
     * @return
     */
    @Operation(
    		summary = "Metodo POST per aggiornare una richiesta prodotto",
			responses = {
                    @ApiResponse(description = "POST per aggiornare una richiesta prodotto",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Object.class))),
                    @ApiResponse(responseCode = "200", description = "Successfully executed"),
                    @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
                    @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
                    @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
            }
    )
    @PostMapping("aggiorna-richiesta")
    public synchronized ResponseEntity<ApiResult> aggiornaRichiestaProdotto(
    		@RequestBody final @Valid AggiornaRichiestaProdottoAttivatoInputBackofficeDTO input,
    		BindingResult bindingResult,
    		@AuthenticationPrincipal UserWithAdditionalInfo user
    		) {
    	LOG.debug("ProdottoAttivatoRESTController --> POST per aggiornare una richiesta prodotto");
    	
        // check permission
        try {
			securityService.checkPermission(user, Constants.ACL_PRODOTTI, Constants.PERMISSION_VISUALIZZA);
		} catch (MyExtranetSecurityException e) {
			LOG.error(String.format("Access denied for user %s", user.getUsername()), e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_ACCESS_DENIED_TO_DATA_MESSAGE, e.getMessage(), Constants.API_ERROR_SECURITY_CODE), HttpStatus.FORBIDDEN);
		}
    	
    	try {
    		validate(input, bindingResult);
    		
        	EnteDTO ente = richiestaProdottoService.getEnteByIdProdAttivRich(input.getIdProdAttivRich());        	
    		GenericAvanzamentoStepRichiestaInputDTO genericInput = input.toGenericAvanzamentoStepRichiestaInputDTO();

    		genericInput.setIdEnte(ente.getIdEnte());
    		genericInput.setEsecutore(Constants.STEP_ESECUTORE_REGIONE);
            
            ProdottoAttivatoDTO prodotto = prodottoAttivatoService.genericAvanzamentoStepRichiesta(genericInput);
                        
            LOG.debug("ProdottoAttivatoRESTController --> POST per aggiornare una richiesta prodotto eseguito con successo.");

            return new ResponseEntity<ApiResult>(new ApiResult(Constants.RESULT_OK, "Successfully executed aggiornaRichiestaProdotto", prodotto), HttpStatus.CREATED);			
		} catch (MyExtranetSecurityException e) {
			LOG.error(String.format("Access denied for user %s", user.getUsername()), e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_ACCESS_DENIED_TO_DATA_MESSAGE, e.getMessage(), Constants.API_ERROR_SECURITY_CODE), HttpStatus.FORBIDDEN);
    	} catch (MyExtranetValidationException e) {
    		LOG.error("Validation exception", e);
    		return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_VALIDATION_MESSAGE, e.getMessage(), Constants.API_ERROR_VALIDATION_CODE), HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			LOG.error("ProdottoAttivatoRESTController --> POST per aggiornare una richiesta prodotto non riuscito", e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_GENERIC_MESSAGE, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}

    }
    

    /**
     * Metodo PATCH per modificare parzialmente un prodotto attivato
     * @return prodotto
     * @throws Exception
     */
    @Operation(
    		summary = "Metodo PATCH per modificare parzialmente un prodotto attivato",
			responses = {
                    @ApiResponse(description = "PATCH per modificare parzialmente un prodotto attivato",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Object.class))),
                    @ApiResponse(responseCode = "200", description = "Successfully patched prodotto attivato"),
                    @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
                    @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
                    @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
            }
    )
    @PatchMapping("")
    public ResponseEntity<ApiResult> patchProdottoAttivato(
    		@RequestBody final ProdottoAttivatoDTO patchProdottoAttivato,
    		@AuthenticationPrincipal UserWithAdditionalInfo user
    		) {
    	LOG.debug("ProdottoAttivatoRESTController --> PATCH prodotto attivato." );
    	
        // check permission
        try {
			securityService.checkPermission(user, Constants.ACL_PRODOTTI, Constants.PERMISSION_GESTISCI);
		} catch (MyExtranetSecurityException e) {
			LOG.error(String.format("Access denied for user %s", user.getUsername()), e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_ACCESS_DENIED_TO_DATA_MESSAGE, e.getMessage(), Constants.API_ERROR_SECURITY_CODE), HttpStatus.FORBIDDEN);
		}
    	
    	try {
            
            // check object does not have an Id
            if (patchProdottoAttivato.getIdAttivazione() == null) {
            	LOG.error("ProdottoAttivatoRESTController --> PATCH prodotto usato impropriamente: l'oggetto non ha un ID");
            	return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_VALIDATION_MESSAGE, "l'oggetto non ha un ID", Constants.API_ERROR_VALIDATION_CODE), HttpStatus.BAD_REQUEST);
            }
            
            ProdottoAttivatoDTO prodotto = prodottoAttivatoService.updateProdottoAttivato(patchProdottoAttivato, true);
            LOG.debug("ProdottoAttivatoRESTController --> PATCH prodotto attivato eseguito con successo.");

            return new ResponseEntity<ApiResult>(new ApiResult(Constants.RESULT_OK, "Successfully patched prodotto attivato", prodotto), HttpStatus.OK);			
		} catch (Exception e) {
			LOG.error("ProdottoAttivatoRESTController --> PATCH prodotto attivato non riuscito", e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_GENERIC_MESSAGE, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}

    }
        
    /**
     * Metodo PUT per modificare un prodotto attivato
     * @return prodotto attivato
     * @throws Exception
     */
    @Operation(
    		summary = "Metodo PUT per modificare un prodotto attivato",
			responses = {
                    @ApiResponse(description = "PUT per modificare un prodotto attivato",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Object.class))),
                    @ApiResponse(responseCode = "200", description = "Successfully updated prodotto attivato"),
                    @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
                    @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
                    @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
            }
    )
    @PutMapping("")
    public ResponseEntity<ApiResult> updateProdottoAttivato(
    		@RequestBody final @Valid ProdottoAttivatoDTO updateProdottoAttivato,
    		BindingResult bindingResult,
    		@AuthenticationPrincipal UserWithAdditionalInfo user
    		) {
    	LOG.debug("ProdottoAttivatoRESTController --> PUT prodotto attivato." );
    	
    	// check permission
    	try {
    		securityService.checkPermission(user, Constants.ACL_PRODOTTI, Constants.PERMISSION_GESTISCI);
    	} catch (MyExtranetSecurityException e) {
    		LOG.error(String.format("Access denied for user %s", user.getUsername()), e);
    		return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_ACCESS_DENIED_TO_DATA_MESSAGE, e.getMessage(), Constants.API_ERROR_SECURITY_CODE), HttpStatus.FORBIDDEN);
    	}
    	
    	try {
    		validate(updateProdottoAttivato, bindingResult);
            
            // check object does not have an Id
            if (updateProdottoAttivato.getIdAttivazione() == null) {
            	LOG.error("ProdottoAttivatoRESTController --> PUT prodotto attivato usato impropriamente: l'oggetto non ha un ID");
            	return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_VALIDATION_MESSAGE, "l'oggetto non ha un ID", Constants.API_ERROR_VALIDATION_CODE), HttpStatus.BAD_REQUEST);
            }
            
            ProdottoAttivatoDTO prodotto = prodottoAttivatoService.updateProdottoAttivato(updateProdottoAttivato, false);
            LOG.debug("ProdottoAttivatoRESTController --> PUT prodotto attivato eseguito con successo.");

            return new ResponseEntity<ApiResult>(new ApiResult(Constants.RESULT_OK, "Successfully updated prodotto attivato", prodotto), HttpStatus.OK);			
    	} catch (MyExtranetValidationException e) {
    		LOG.error("Validation exception", e);
    		return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_VALIDATION_MESSAGE, e.getMessage(), Constants.API_ERROR_VALIDATION_CODE), HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			LOG.error("ProdottoAttivatoRESTController --> PUT prodotto attivato non riuscito", e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_GENERIC_MESSAGE, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}

    }
    
    @PostMapping("/comunicazione")
    public ResponseEntity<ApiResult> insertComunicazioneUtentiProdottoAttivatoFiltered(
    		@ModelAttribute( name = "_filter" ) ProdottoAttivatoComunicazioneFilterDTO prodottoAttivatoComunicazioneFilter,
    		@RequestBody final MessaggioDTO messaggio,
    		@AuthenticationPrincipal UserWithAdditionalInfo user
    		) {
    	LOG.debug("ProdottoAttivatoRESTController --> POST nuova comunicazione ai collaboratori filtrati." );
    	
    	// check permission
    	try {
    		securityService.checkPermission(user, Constants.ACL_PRODOTTI, Constants.PERMISSION_GESTISCI);
    	} catch (MyExtranetSecurityException e) {
    		LOG.error(String.format("Access denied for user %s", user.getUsername()), e);
    		return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_ACCESS_DENIED_TO_DATA_MESSAGE, e.getMessage(), Constants.API_ERROR_SECURITY_CODE), HttpStatus.FORBIDDEN);
    	}
    	
    	try {
            
            // check if needed fields are present
            if (messaggio.getTitolo() == null || messaggio.getMessaggio() == null) {
            	LOG.error("ProdottoAttivatoRESTController --> POST nuova comunicazione ai collaboratori filtrati usato impropriamente: titolo e/o messaggio mancante");
            	return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_VALIDATION_MESSAGE, "titolo e/o messaggio mancante", Constants.API_ERROR_VALIDATION_CODE), HttpStatus.BAD_REQUEST);
            }
            
            // set some properties
            messaggio.setTipo(Constants.MESSAGGIO_TIPO_COMUNICAZIONE);
            messaggio.setArea(Constants.MESSAGGIO_AREA_PRODOTTO);
            messaggio.setDestinatario("Agli utenti del progetto");
            
            // execute operation
            MessaggioDTO sentMessaggio = prodottoAttivatoService.sendMessaggioToUtentiProdottiAttivato(prodottoAttivatoComunicazioneFilter, messaggio);
            
            LOG.debug("ProdottoAttivatoRESTController --> POST nuova comunicazione ai collaboratori filtrati eseguito con successo.");

            return new ResponseEntity<ApiResult>(new ApiResult(Constants.RESULT_OK, "Operation successfully performed", sentMessaggio), HttpStatus.OK);			
		} catch (Exception e) {
			LOG.error("ProdottoAttivatoRESTController --> POST nuova comunicazione ai collaboratori filtrati non riuscito", e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_GENERIC_MESSAGE, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}

    }    
}
