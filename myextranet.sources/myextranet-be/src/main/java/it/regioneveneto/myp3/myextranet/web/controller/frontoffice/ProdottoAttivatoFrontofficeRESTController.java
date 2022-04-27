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
import it.regioneveneto.myp3.myextranet.exception.MyExtranetMissingOperationException;
import it.regioneveneto.myp3.myextranet.exception.MyExtranetSecurityException;
import it.regioneveneto.myp3.myextranet.exception.MyExtranetValidationException;
import it.regioneveneto.myp3.myextranet.security.UserWithAdditionalInfo;
import it.regioneveneto.myp3.myextranet.service.EnteService;
import it.regioneveneto.myp3.myextranet.service.ProdottoAttivatoService;
import it.regioneveneto.myp3.myextranet.service.RichiestaProdottoService;
import it.regioneveneto.myp3.myextranet.service.SecurityService;
import it.regioneveneto.myp3.myextranet.utils.Constants;
import it.regioneveneto.myp3.myextranet.web.controller.ValidationController;
import it.regioneveneto.myp3.myextranet.web.dto.AggiornaRichiestaProdottoAttivatoInputDTO;
import it.regioneveneto.myp3.myextranet.web.dto.AvviaRichiestaProdottoAttivatoInputDTO;
import it.regioneveneto.myp3.myextranet.web.dto.DatiRichiestaProdottoAttivatoDTO;
import it.regioneveneto.myp3.myextranet.web.dto.EnteDTO;
import it.regioneveneto.myp3.myextranet.web.dto.GenericAvanzamentoStepRichiestaInputDTO;
import it.regioneveneto.myp3.myextranet.web.dto.PagedData;
import it.regioneveneto.myp3.myextranet.web.dto.ProdottoAttivabileEnteDTO;
import it.regioneveneto.myp3.myextranet.web.dto.ProdottoAttivabileEnteFilterDTO;
import it.regioneveneto.myp3.myextranet.web.dto.ProdottoAttivatoDTO;
import it.regioneveneto.myp3.myextranet.web.dto.ProdottoAttivatoWithUsersDTO;

@RestController
@RequestMapping("/frontoffice/prodotto-attivato")
@Tag(name = "tag_prodotto-attivato", description = "Prodotto attivato")
public class ProdottoAttivatoFrontofficeRESTController extends ValidationController {
    private static final Logger LOG = LoggerFactory.getLogger(ProdottoAttivatoFrontofficeRESTController.class);

    @Autowired
    private ProdottoAttivatoService prodottoAttivatoService;
    
    @Autowired
    private EnteService enteService;
    
    @Autowired
    private RichiestaProdottoService richiestaProdottoService;
    
    @Autowired
    private SecurityService securityService;
    
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
    public ResponseEntity<ProdottoAttivatoWithUsersDTO> getProdottoAttivatoById(
    		@PathVariable( name = "_id", required = true) final Integer idAttivazione,
    		@AuthenticationPrincipal UserWithAdditionalInfo user
    		) {
    	LOG.debug("ProdottoAttivatoFrontofficeRESTController --> GET prodotto attivato with id " + idAttivazione);
    	
        // check permission
        try {
        	securityService.checkUserValidity(user);
		} catch (MyExtranetSecurityException e) {
			LOG.error(String.format("Access denied for user %s", user.getUsername()), e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_ACCESS_DENIED_TO_DATA_MESSAGE, e.getMessage(), Constants.API_ERROR_SECURITY_CODE), HttpStatus.FORBIDDEN);
		}

        ProdottoAttivatoWithUsersDTO prodotto;
		try {
			prodotto = prodottoAttivatoService.getProdottoAttivatoWithUsers(idAttivazione);
			
			securityService.checkIsDataOwner(prodotto, user);

			LOG.debug("ProdottoAttivatoFrontofficeRESTController --> GET prodotto attivato eseguito con successo.");

        	return new ResponseEntity<ProdottoAttivatoWithUsersDTO>(prodotto, HttpStatus.OK);
		} catch (MyExtranetSecurityException e) {
			LOG.error(String.format("Access denied for user %s", user.getUsername()), e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_ACCESS_DENIED_TO_DATA_MESSAGE, e.getMessage(), Constants.API_ERROR_SECURITY_CODE), HttpStatus.FORBIDDEN);
		} catch (Exception e) {
			LOG.error("ProdottoAttivatoFrontofficeRESTController --> GET prodotto attivato non riuscito", e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_GENERIC_MESSAGE, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
    }
    
    /**
     * Metodo GET per recuperare i prodotti attivabili ed attivati per un ente
     * @return prodotti
     * @throws Exception
     */
    @Operation(
    		summary = "Metodo GET per recuperare i prodotti attivabili ed attivati per un ente",
			responses = {
                    @ApiResponse(description = "GET per recuperare i prodotti attivabili ed attivati per un ente",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Object.class))),
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved prodotti"),
                    @ApiResponse(responseCode = "400", description = "Collaboratore not found"),
                    @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
                    @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
                    @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
            }
    )
    @GetMapping("/prodotti-ente")
    public ResponseEntity<PagedData<ProdottoAttivabileEnteDTO>> getAllProdottiEnte(
    		@ModelAttribute( name = "_filter" ) ProdottoAttivabileEnteFilterDTO prodottoAttivabileEnteFilter,
    		@RequestParam( name = "_page", required = false, defaultValue = "0" ) final Integer pageNumber,
    		@RequestParam( name = "_pageSize", required = false, defaultValue = "10" ) final Integer pageSize,
    		@RequestParam( name = "_orderBy", required = false ) final String orderProperty,
    		@RequestParam( name = "_orderDir", required = false ) final String orderDirection,
    		@AuthenticationPrincipal UserWithAdditionalInfo user
    		) {
    	LOG.debug("ProdottoAttivatoFrontofficeRESTController --> GET Lista prodotti attivabili ed attivati per un ente");
    	
        // check permission
        try {
        	securityService.checkUserValidity(user);
		} catch (MyExtranetSecurityException e) {
			LOG.error(String.format("Access denied for user %s", user.getUsername()), e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_ACCESS_DENIED_TO_DATA_MESSAGE, e.getMessage(), Constants.API_ERROR_SECURITY_CODE), HttpStatus.FORBIDDEN);
		}

        // check filter
        if (prodottoAttivabileEnteFilter.getIdEnte() == null) {
        	LOG.error(String.format("Invalid filter: idEnte not provided"));
        	return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_VALIDATION_MESSAGE, "Invalid filter: idEnte not provided", Constants.API_ERROR_VALIDATION_CODE), HttpStatus.BAD_REQUEST);
        }
        
        try {
			EnteDTO ente = enteService.getEnte(prodottoAttivabileEnteFilter.getIdEnte());
			securityService.checkIsDataOwner(ente, user);
			
			PagedData<ProdottoAttivabileEnteDTO> prodotti = prodottoAttivatoService.getProdottiAttivabiliEnte(prodottoAttivabileEnteFilter, pageNumber, pageSize, orderProperty, orderDirection);
			LOG.debug("ProdottoAttivabileRESTController --> GET Lista prodotti attivabili eseguito con successo.");

			return new ResponseEntity<PagedData<ProdottoAttivabileEnteDTO>>(prodotti, HttpStatus.OK);
		} catch (MyExtranetSecurityException e) {
			LOG.error(String.format("Access denied for user %s", user.getUsername()), e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_ACCESS_DENIED_TO_DATA_MESSAGE, e.getMessage(), Constants.API_ERROR_SECURITY_CODE), HttpStatus.FORBIDDEN);
		} catch (Exception e) {
			LOG.error("IscrittoEventoFrontofficeRESTController --> GET iscritto non riuscito", e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_GENERIC_MESSAGE, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
    }

    /**
     * Metodo GET per recuperare dati utili per una richiesta prodotto
     * @param idEnte
     * @param idProdottoAtt
     * @param codTipoRich
     * @param user
     * @return
     */
    @Operation(
    		summary = "Metodo GET per recuperare dati utili per una richiesta prodotto",
			responses = {
                    @ApiResponse(description = "GET per recuperare dati utili per una richiesta prodotto",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Object.class))),
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved data"),
                    @ApiResponse(responseCode = "400", description = "Collaboratore not found"),
                    @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
                    @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
                    @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
            }
    )
    @GetMapping("/dati-richiesta-prodotto")
    public ResponseEntity<DatiRichiestaProdottoAttivatoDTO> getDatiRichiestaProdottoAttivato(
    		@RequestParam( name = "idEnte", required = true ) final Integer idEnte,
    		@RequestParam( name = "idProdottoAtt", required = true ) final Integer idProdottoAtt,
    		@RequestParam( name = "codTipoRich", required = false ) final String codTipoRich,
    		@AuthenticationPrincipal UserWithAdditionalInfo user
    		) {
    	LOG.debug("ProdottoAttivatoFrontofficeRESTController --> GET per recuperare dati utili per una richiesta prodotto");
    	
        // check permission
        try {
        	securityService.checkUserValidity(user);
		} catch (MyExtranetSecurityException e) {
			LOG.error(String.format("Access denied for user %s", user.getUsername()), e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_ACCESS_DENIED_TO_DATA_MESSAGE, e.getMessage(), Constants.API_ERROR_SECURITY_CODE), HttpStatus.FORBIDDEN);
		}

        // check filter
        if (idEnte == null) {
        	LOG.error(String.format("Invalid filter: idEnte not provided"));
        	return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_VALIDATION_MESSAGE, "Invalid input: idEnte not provided", Constants.API_ERROR_VALIDATION_CODE), HttpStatus.BAD_REQUEST);
        }
        
        try {
			EnteDTO ente = enteService.getEnte(idEnte);
			securityService.checkIsDataOwner(ente, user);
			
			DatiRichiestaProdottoAttivatoDTO data = prodottoAttivatoService.getDatiRichiestaProdottoAttivato(idEnte, idProdottoAtt, codTipoRich);
			LOG.debug("ProdottoAttivabileRESTController --> GET per recuperare dati utili per una richiesta prodotto eseguito con successo.");

			return new ResponseEntity<DatiRichiestaProdottoAttivatoDTO>(data, HttpStatus.OK);
		} catch (MyExtranetSecurityException e) {
			LOG.error(String.format("Access denied for user %s", user.getUsername()), e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_ACCESS_DENIED_TO_DATA_MESSAGE, e.getMessage(), Constants.API_ERROR_SECURITY_CODE), HttpStatus.FORBIDDEN);
		} catch (MyExtranetMissingOperationException e) {			
			LOG.error("IscrittoEventoFrontofficeRESTController --> GET per recuperare dati utili per una richiesta prodotto non riuscito", e);		
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_MISSING_CONF_MESSAGE, e.getMessage(), Constants.API_ERROR_MISSING_CONF_CODE), HttpStatus.BAD_REQUEST);
		} catch (MyExtranetValidationException e) {			
			LOG.error("IscrittoEventoFrontofficeRESTController --> GET per recuperare dati utili per una richiesta prodotto non riuscito", e);		
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_VALIDATION_MESSAGE, e.getMessage(), Constants.API_ERROR_VALIDATION_CODE), HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			LOG.error("IscrittoEventoFrontofficeRESTController --> GET per recuperare dati utili per una richiesta prodotto non riuscito", e);		
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_GENERIC_MESSAGE, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
    }

    
    /**
     * 
     * @param avviaRichiestaProdotto
     * @return
     */
    @Operation(
    		summary = "Metodo POST per avviare una richiesta prodotto",
			responses = {
                    @ApiResponse(description = "POST per avviare una richiesta prodotto",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Object.class))),
                    @ApiResponse(responseCode = "200", description = "Successfully executed"),
                    @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
                    @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
                    @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
            }
    )
    @PostMapping("avvia-richiesta")
    public synchronized ResponseEntity<ApiResult> avviaRichiestaProdotto(
    		@RequestBody final @Valid AvviaRichiestaProdottoAttivatoInputDTO input,
    		BindingResult bindingResult,
    		@AuthenticationPrincipal UserWithAdditionalInfo user
    		) {
    	LOG.debug("ProdottoAttivatoFrontofficeRESTController --> POST per avviare una richiesta prodotto");
    	
        // check permission
        try {
        	securityService.checkUserValidity(user);
        	
		} catch (MyExtranetSecurityException e) {
			LOG.error(String.format("Access denied for user %s", user.getUsername()), e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_ACCESS_DENIED_TO_DATA_MESSAGE, e.getMessage(), Constants.API_ERROR_SECURITY_CODE), HttpStatus.FORBIDDEN);
		}
    	
    	try {
    		
    		validate(input, bindingResult);
    		
        	EnteDTO ente = enteService.getEnte(input.getIdEnte());
        	securityService.checkIsDataOwner(ente, user);

        	GenericAvanzamentoStepRichiestaInputDTO genericInput = input.toGenericAvanzamentoStepRichiestaInputDTO();
        	
        	genericInput.setEsecutore(Constants.STEP_ESECUTORE_ENTE);
            
            ProdottoAttivatoDTO prodotto = prodottoAttivatoService.genericAvanzamentoStepRichiesta(genericInput);
                
            LOG.debug("ProdottoAttivatoFrontofficeRESTController --> POST per avviare una richiesta prodotto eseguito con successo.");

            return new ResponseEntity<ApiResult>(new ApiResult(Constants.RESULT_OK, "Successfully executed avviaRichiestaProdotto", prodotto), HttpStatus.CREATED);			
		} catch (MyExtranetSecurityException e) {
			LOG.error(String.format("Access denied for user %s", user.getUsername()), e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_ACCESS_DENIED_TO_DATA_MESSAGE, e.getMessage(), Constants.API_ERROR_SECURITY_CODE), HttpStatus.FORBIDDEN);
    	} catch (MyExtranetValidationException e) {
    		LOG.error("Validation exception", e);
    		return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_VALIDATION_MESSAGE, e.getMessage(), Constants.API_ERROR_VALIDATION_CODE), HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			LOG.error("ProdottoAttivatoFrontofficeRESTController --> POST per avviare una richiesta prodotto non riuscito", e);
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
    		@RequestBody final @Valid AggiornaRichiestaProdottoAttivatoInputDTO input,
    		BindingResult bindingResult,
    		@AuthenticationPrincipal UserWithAdditionalInfo user
    		) {
    	LOG.debug("ProdottoAttivatoFrontofficeRESTController --> POST per aggiornare una richiesta prodotto");
    	
        // check permission
        try {
        	securityService.checkUserValidity(user);
        	
		} catch (MyExtranetSecurityException e) {
			LOG.error(String.format("Access denied for user %s", user.getUsername()), e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_ACCESS_DENIED_TO_DATA_MESSAGE, e.getMessage(), Constants.API_ERROR_SECURITY_CODE), HttpStatus.FORBIDDEN);
		}
    	
    	try {
    		validate(input, bindingResult);    		
    		
        	EnteDTO ente = richiestaProdottoService.getEnteByIdProdAttivRich(input.getIdProdAttivRich());
        	securityService.checkIsDataOwner(ente, user);

    		GenericAvanzamentoStepRichiestaInputDTO genericInput = input.toGenericAvanzamentoStepRichiestaInputDTO();

    		genericInput.setIdEnte(ente.getIdEnte());
    		genericInput.setEsecutore(Constants.STEP_ESECUTORE_ENTE);
            
            ProdottoAttivatoDTO prodotto = prodottoAttivatoService.genericAvanzamentoStepRichiesta(genericInput);
                        
            LOG.debug("ProdottoAttivatoFrontofficeRESTController --> POST per aggiornare una richiesta prodotto eseguito con successo.");

            return new ResponseEntity<ApiResult>(new ApiResult(Constants.RESULT_OK, "Successfully executed aggiornaRichiestaProdotto", prodotto), HttpStatus.CREATED);			
		} catch (MyExtranetSecurityException e) {
			LOG.error(String.format("Access denied for user %s", user.getUsername()), e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_ACCESS_DENIED_TO_DATA_MESSAGE, e.getMessage(), Constants.API_ERROR_SECURITY_CODE), HttpStatus.FORBIDDEN);
    	} catch (MyExtranetValidationException e) {
    		LOG.error("Validation exception", e);
    		return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_VALIDATION_MESSAGE, e.getMessage(), Constants.API_ERROR_VALIDATION_CODE), HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			LOG.error("ProdottoAttivatoFrontofficeRESTController --> POST per aggiornare una richiesta prodotto non riuscito", e);
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
    	LOG.debug("ProdottoAttivatoFrontofficeRESTController --> POST nuovo prodotto attivato");
    	
        // check permission
        try {
        	securityService.checkUserValidity(user);
        	
        	securityService.checkIsDataOwner(newProdottoAttivato, user);
		} catch (MyExtranetSecurityException e) {
			LOG.error(String.format("Access denied for user %s", user.getUsername()), e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_ACCESS_DENIED_TO_DATA_MESSAGE, e.getMessage(), Constants.API_ERROR_SECURITY_CODE), HttpStatus.FORBIDDEN);
		}
    	
    	try {
    		
    		validate(newProdottoAttivato, bindingResult);    		
            
            ProdottoAttivatoDTO prodotto = prodottoAttivatoService.insertProdottoAttivato(newProdottoAttivato);
                        
            LOG.debug("ProdottoAttivatoFrontofficeRESTController --> POST nuovo prodotto attivato eseguito con successo.");

            return new ResponseEntity<ApiResult>(new ApiResult(Constants.RESULT_OK, "Successfully created prodotto attivato", prodotto), HttpStatus.CREATED);			
    	} catch (MyExtranetValidationException e) {
    		LOG.error("Validation exception", e);
    		return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_VALIDATION_MESSAGE, e.getMessage(), Constants.API_ERROR_VALIDATION_CODE), HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			LOG.error("ProdottoAttivatoFrontofficeRESTController --> POST nuovo prodotto attivato non riuscito", e);
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
        	securityService.checkUserValidity(user);
        	
		} catch (MyExtranetSecurityException e) {
			LOG.error(String.format("Access denied for user %s", user.getUsername()), e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_ACCESS_DENIED_TO_DATA_MESSAGE, e.getMessage(), Constants.API_ERROR_SECURITY_CODE), HttpStatus.FORBIDDEN);
		}
    	
    	try {
            
            // check object does not have an Id
            if (patchProdottoAttivato.getIdAttivazione() == null) {
            	LOG.error("ProdottoAttivatoRESTController --> PATCH prodotto usato impropriamente: l'oggetto non ha un ID");
            	return new ResponseEntity<ApiResult>(new ApiResult(Constants.RESULT_KO, "To use PATCH the object needs to have an ID", patchProdottoAttivato), HttpStatus.BAD_REQUEST);
            }
            
            ProdottoAttivatoDTO prodottoAttivatoStored = prodottoAttivatoService.getProdottoAttivato(patchProdottoAttivato.getIdAttivazione());
            
            securityService.checkIsDataOwner(prodottoAttivatoStored, user);;
            
            ProdottoAttivatoDTO prodotto = prodottoAttivatoService.updateProdottoAttivato(patchProdottoAttivato, true);
            LOG.debug("ProdottoAttivatoRESTController --> PATCH prodotto attivato eseguito con successo.");

            return new ResponseEntity<ApiResult>(new ApiResult(Constants.RESULT_OK, "Successfully patched prodotto attivato", prodotto), HttpStatus.OK);			
		} catch (MyExtranetSecurityException e) {
			LOG.error(String.format("Access denied for user %s", user.getUsername()), e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_ACCESS_DENIED_TO_DATA_MESSAGE, e.getMessage(), Constants.API_ERROR_SECURITY_CODE), HttpStatus.FORBIDDEN);
		} catch (Exception e) {
			LOG.error("ProdottoAttivatoRESTController --> PATCH prodotto attivato non riuscito", e);
			return new ResponseEntity<ApiResult>(new ApiResult(Constants.RESULT_KO, "Error patching prodotto attivato", e.getMessage()), HttpStatus.BAD_REQUEST);
		}

    }


}
