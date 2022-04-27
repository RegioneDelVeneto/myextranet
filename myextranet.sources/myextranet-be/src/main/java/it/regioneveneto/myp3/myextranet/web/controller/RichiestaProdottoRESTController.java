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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
import it.regioneveneto.myp3.myextranet.bean.UtentiProdottiAttivatiEnteExcelReportInput;
import it.regioneveneto.myp3.myextranet.exception.MyExtranetSecurityException;
import it.regioneveneto.myp3.myextranet.security.UserWithAdditionalInfo;
import it.regioneveneto.myp3.myextranet.service.EnteService;
import it.regioneveneto.myp3.myextranet.service.ExcelReportService;
import it.regioneveneto.myp3.myextranet.service.RichiestaProdottoService;
import it.regioneveneto.myp3.myextranet.service.SecurityService;
import it.regioneveneto.myp3.myextranet.utils.Constants;
import it.regioneveneto.myp3.myextranet.web.dto.ComunicazioneRichiestaProdottoInputDTO;
import it.regioneveneto.myp3.myextranet.web.dto.EnteDTO;
import it.regioneveneto.myp3.myextranet.web.dto.MessaggioDTO;
import it.regioneveneto.myp3.myextranet.web.dto.PagedData;
import it.regioneveneto.myp3.myextranet.web.dto.RichiestaProdottoDTO;
import it.regioneveneto.myp3.myextranet.web.dto.RichiestaProdottoFilterDTO;
import it.regioneveneto.myp3.myextranet.web.dto.RichiestaProdottoWithStepsDTO;

@RestController
@RequestMapping("/richiesta-prodotto")
@Tag(name = "tag_richiesta-prodotto", description = "Richiesta prodotto")
public class RichiestaProdottoRESTController extends ValidationController {
    private static final Logger LOG = LoggerFactory.getLogger(RichiestaProdottoRESTController.class);

    @Autowired
    private RichiestaProdottoService richiestaProdottoService;
    
    @Autowired
    private ExcelReportService excelReportService;
    
    @Autowired
    private EnteService enteService;
    
    @Autowired
    private SecurityService securityService;
    
    /**
     * Metodo GET per recuperare una richiesta prodotto tramite id
     * @return procedimento prodotto
     * @throws Exception
     */
    @Operation(
    		summary = "Metodo GET per recuperare una richiesta prodotto tramite id",
			responses = {
                    @ApiResponse(description = "GET per recuperare una richiesta prodotto",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Object.class))),
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved richiesta prodotto"),
                    @ApiResponse(responseCode = "400", description = "Richiesta prodotto not found"),
                    @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
                    @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
                    @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
            }
    )
    @GetMapping("/{_id}")
    public ResponseEntity<RichiestaProdottoWithStepsDTO> getRichiestaProdottoById(
    		@PathVariable( name = "_id", required = true) final Integer idProdAttivRich,
    		@AuthenticationPrincipal UserWithAdditionalInfo user
    		) {
    	LOG.debug("RichiestaProdottoRESTController --> GET richiesta prodotto with id " + idProdAttivRich);
    	
    	// check permission
    	try {
    		securityService.checkPermission(user, Constants.ACL_PRODOTTI, Constants.PERMISSION_VISUALIZZA);
    	} catch (MyExtranetSecurityException e) {
    		LOG.error(String.format("Access denied for user %s", user.getUsername()), e);
    		return new ResponseEntity(HttpStatus.FORBIDDEN);
    	}

        RichiestaProdottoWithStepsDTO richiestaProdotto;
		try {			
			richiestaProdotto = richiestaProdottoService.getRichiestaProdottoWithSteps(idProdAttivRich);
						
			LOG.debug("RichiestaProdottoRESTController --> GET richiesta prodotto eseguito con successo.");

        	return new ResponseEntity<RichiestaProdottoWithStepsDTO>(richiestaProdotto, HttpStatus.OK);
		} catch (MyExtranetSecurityException e) {
			LOG.error(String.format("Access denied for user %s", user.getUsername()), e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_ACCESS_DENIED_TO_DATA_MESSAGE, e.getMessage(), Constants.API_ERROR_SECURITY_CODE), HttpStatus.FORBIDDEN);
		} catch (Exception e) {
			LOG.error("RichiestaProdottoRESTController --> GET richiesta prodotto non riuscito", e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_GENERIC_MESSAGE, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
    }
    
    
    /**
     * Metodo GET per recuperare una richiesta prodotto tramite id
     * @return procedimento prodotto
     * @throws Exception
     */
    @Operation(
    		summary = "Metodo GET per generare report XLS su utenti richiesta prodotto",
			responses = {
                    @ApiResponse(description = "GET per generare report XLS su utenti richiesta prodotto",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Object.class))),
                    @ApiResponse(responseCode = "200", description = "Successfully generated report"),
                    @ApiResponse(responseCode = "400", description = "Richiesta prodotto not found"),
                    @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
                    @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
                    @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
            }
    )
    @GetMapping("/{_id}/utenti-richiesta-prodotto-excel")
    public ResponseEntity<InputStreamResource> getUtentiRichiestaProdottoExcelById(
    		@PathVariable( name = "_id", required = true) final Integer idProdAttivRich,
    		@AuthenticationPrincipal UserWithAdditionalInfo user
    		) {
    	LOG.debug("RichiestaProdottoRESTController --> GET per generare report XLS su utenti richiesta prodotto with id " + idProdAttivRich);
    	
    	// check permission
    	try {
    		securityService.checkPermission(user, Constants.ACL_PRODOTTI, Constants.PERMISSION_VISUALIZZA);
    	} catch (MyExtranetSecurityException e) {
    		LOG.error(String.format("Access denied for user %s", user.getUsername()), e);
    		return new ResponseEntity(HttpStatus.FORBIDDEN);
    	}
    	
    	UtentiProdottiAttivatiEnteExcelReportInput input;
		try {
			input = richiestaProdottoService.buildUtentiProdottiAttivatiEnteExcelReportInput(idProdAttivRich);
		} catch (Exception e) {
			LOG.error("RichiestaProdottoRESTController --> GET per generare report XLS su utenti richiesta prodotto non riuscito", e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_GENERIC_MESSAGE, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}

        byte[] report = excelReportService.generateUtentiProdottiAttivatiEnteExcelReport(input);
        ByteArrayInputStream bis = new ByteArrayInputStream(report);
        
        LOG.debug("RichiestaProdottoRESTController --> POST per generare report XLS su utenti richiesta prodotto eseguito con successo.");

        var headers = new HttpHeaders();
        headers.add("Content-Type", "application/vnd.ms-excel");
        headers.add("Content-Disposition", "inline; filename=utenti_prodotto.xls");

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(new InputStreamResource(bis));

    }
    
    
    /**
     * Metodo GET per recuperare le richieste prodotto
     * @return procedimenti prodotto
     * @throws Exception
     */
    @Operation(
    		summary = "Metodo GET per recuperare le richieste prodotto",
			responses = {
                    @ApiResponse(description = "GET per recuperare le richieste prodotto",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Object.class))),
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved richieste prodotto"),
                    @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
                    @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
                    @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
            }
    )

    @GetMapping("")
    public ResponseEntity<PagedData<RichiestaProdottoDTO>> getAllRichiesteProdotto(
    		@ModelAttribute( name = "_filter" ) RichiestaProdottoFilterDTO richiestaProdottoFilter,
    		@RequestParam( name = "_page", required = false, defaultValue = "0" ) final Integer pageNumber,
    		@RequestParam( name = "_pageSize", required = false, defaultValue = "10" ) final Integer pageSize,
    		@RequestParam( name = "_orderBy", required = false ) final String orderProperty,
    		@RequestParam( name = "_orderDir", required = false ) final String orderDirection,
    		@AuthenticationPrincipal UserWithAdditionalInfo user
    		) {
    	
        LOG.debug("RichiestaProdottoRESTController --> GET Lista richieste prodotto");
        
        // check permission
        try {
			securityService.checkPermission(user, Constants.ACL_PRODOTTI, Constants.PERMISSION_VISUALIZZA);
		} catch (MyExtranetSecurityException e) {
			LOG.error(String.format("Access denied for user %s", user.getUsername()), e);
			return new ResponseEntity<PagedData<RichiestaProdottoDTO>>(HttpStatus.FORBIDDEN);
		}
        
        PagedData<RichiestaProdottoDTO> richiesteProdotto = richiestaProdottoService.getRichiesteProdotto(richiestaProdottoFilter, pageNumber, pageSize, orderProperty, orderDirection);
        LOG.debug("RichiestaProdottoRESTController --> GET Lista richieste prodotto eseguito con successo.");

        return new ResponseEntity<PagedData<RichiestaProdottoDTO>>(richiesteProdotto, HttpStatus.OK);

    }

    /**
     * Richiesta di una nuova comunicazione relativa ad una richiesta prodotto
     * 
     * @param messaggio messaggio da inviare
     * @return
     */
    @Operation(
    		summary = "Metodo POST per inviare una nuova comunicazione relativa ad una richiesta prodotto",
			responses = {
                    @ApiResponse(description = "POST per inviare una nuova comunicazione relativa ad una richiesta prodotto",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Object.class))),
                    @ApiResponse(responseCode = "200", description = "Operation executed successfully"),
                    @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
                    @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
                    @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
            }
    )
    @PostMapping("/{_id}/comunicazione")
    public ResponseEntity<ApiResult> insertComunicazioneRichiestaProdotto(
    		@PathVariable( name = "_id", required = true) final Integer idProdAttivRich,
    		@RequestBody final ComunicazioneRichiestaProdottoInputDTO input,
    		@AuthenticationPrincipal UserWithAdditionalInfo user
    		) {
    	LOG.debug("RichiestaProdottoRESTController --> POST nuova comunicazione relativa ad una richiesta prodotto." );
    	
    	// check permission
    	try {
    		securityService.checkPermission(user, Constants.ACL_PROGETTI, Constants.PERMISSION_GESTISCI);
    	} catch (MyExtranetSecurityException e) {
    		LOG.error(String.format("Access denied for user %s", user.getUsername()), e);
    		return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_ACCESS_DENIED_TO_DATA_MESSAGE, e.getMessage(), Constants.API_ERROR_SECURITY_CODE), HttpStatus.FORBIDDEN);
    	}
    	
    	try {
            
            // check if needed fields are present
            if (input.getTitolo() == null || input.getMessaggio() == null) {
            	LOG.error("RichiestaProdottoRESTController --> POST nuova comunicazione relativa ad una richiesta prodotto usato impropriamente: titolo e/o messaggio mancante");
            	return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_VALIDATION_MESSAGE, "titolo e/o messaggio mancante", Constants.API_ERROR_VALIDATION_CODE), HttpStatus.BAD_REQUEST);
            }
                        
            // execute operation
            MessaggioDTO sentMessaggio = richiestaProdottoService.sendMessaggioAboutRichiestaProdotto(idProdAttivRich, input);
            
            LOG.debug("RichiestaProdottoRESTController --> POST nuova comunicazione relativa ad una richiesta prodotto eseguito con successo.");

            return new ResponseEntity<ApiResult>(new ApiResult(Constants.RESULT_OK, "Operation successfully performed", sentMessaggio), HttpStatus.OK);			
		} catch (Exception e) {
			LOG.error("RichiestaProdottoRESTController --> POST nuova comunicazione relativa ad una richiesta prodotto non riuscito", e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_GENERIC_MESSAGE, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}

    }

}
