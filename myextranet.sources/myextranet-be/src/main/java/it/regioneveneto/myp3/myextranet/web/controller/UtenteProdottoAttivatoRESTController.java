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
import java.util.List;

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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
import it.regioneveneto.myp3.myextranet.service.ExcelReportService;
import it.regioneveneto.myp3.myextranet.service.ProdottoAttivatoService;
import it.regioneveneto.myp3.myextranet.service.SecurityService;
import it.regioneveneto.myp3.myextranet.service.UtenteProdottoAttivatoService;
import it.regioneveneto.myp3.myextranet.utils.Constants;
import it.regioneveneto.myp3.myextranet.web.dto.GruppoUtenteProdottoAttivatoDTO;
import it.regioneveneto.myp3.myextranet.web.dto.OperazioneUtenteProdottoAttivatoDTO;
import it.regioneveneto.myp3.myextranet.web.dto.ProdottoAttivatoDTO;
import it.regioneveneto.myp3.myextranet.web.dto.UtenteProdottoAttivatoDTO;

@RestController
@RequestMapping("/utente-prodotto-attivato")
@Tag(name = "tag_utente-prodotto-attivato", description = "Utente prodotto attivato")
public class UtenteProdottoAttivatoRESTController extends ValidationController {
	private static final Logger LOG = LoggerFactory.getLogger(UtenteProdottoAttivatoRESTController.class);

	@Autowired
	private UtenteProdottoAttivatoService utenteProdottoAttivatoService;

	@Autowired
	private SecurityService securityService;

	@Autowired
	private ProdottoAttivatoService prodottoAttivatoService;
	
	@Autowired
	private ExcelReportService excelReportService;

	/**
	 * Metodo GET per recuperare un utente
	 * 
	 * @return utente
	 * @throws Exception
	 */
	@Operation(summary = "Metodo GET per un utente tramite id", responses = {
			@ApiResponse(description = "GET per recuperare un utente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Object.class))),
			@ApiResponse(responseCode = "200", description = "Successfully retrieved utente"),
			@ApiResponse(responseCode = "400", description = "Ente not found"),
			@ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
			@ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
			@ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found") })

	@GetMapping("/{_id}")
	public ResponseEntity<UtenteProdottoAttivatoDTO> getUtenteProdottoAttivatoById(
			@PathVariable(name = "_id", required = true) final Integer idUtenteProd,
			@AuthenticationPrincipal UserWithAdditionalInfo user) {
		LOG.debug("UtenteProdottoAttivatoRESTController --> GET utente with id " + idUtenteProd);

		// check permission
		try {
			securityService.checkPermission(user, Constants.ACL_PRODOTTI, Constants.PERMISSION_VISUALIZZA);
		} catch (MyExtranetSecurityException e) {
			LOG.error(String.format("Access denied for user %s", user.getUsername()), e);

			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_ACCESS_DENIED_TO_DATA_MESSAGE, e.getMessage(), Constants.API_ERROR_SECURITY_CODE), HttpStatus.FORBIDDEN);
		}

		UtenteProdottoAttivatoDTO utente;
		try {
			utente = utenteProdottoAttivatoService.getUtenteProdottoAttivato(idUtenteProd);

			LOG.debug("UtenteProdottoAttivatoRESTController --> GET utente eseguito con successo.");

			return new ResponseEntity<UtenteProdottoAttivatoDTO>(utente, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("UtenteProdottoAttivatoRESTController --> GET utente non riuscito", e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_GENERIC_MESSAGE, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Metodo GET per recuperare gli utenti di un prodotto attivato, raggruppati per
	 * ruolo
	 * 
	 * @return utenti
	 * @throws Exception
	 */
	@Operation(summary = "Metodo GET per recuperare gli utenti", responses = {
			@ApiResponse(description = "GET per recuperare gli utenti di un prodotto attivato, raggruppati per ruolo", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Object.class))),
			@ApiResponse(responseCode = "200", description = "Successfully retrieved utenti"),
			@ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
			@ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
			@ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found") })
	@GetMapping("/gruppi-utenti-prodotto")
	public ResponseEntity<List<GruppoUtenteProdottoAttivatoDTO>> getGruppiUtenteProdottoAttivato(
			@ModelAttribute(name = "idAttivazione") Integer idAttivazione,
			@AuthenticationPrincipal UserWithAdditionalInfo user) {
		LOG.debug("UtenteProdottoAttivatoRESTController --> GET Lista utenti (gruppi).");

		// check permission
		try {
			securityService.checkPermission(user, Constants.ACL_PRODOTTI, Constants.PERMISSION_VISUALIZZA);
		} catch (MyExtranetSecurityException e) {
			LOG.error(String.format("Access denied for user %s", user.getUsername()), e);

			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_ACCESS_DENIED_TO_DATA_MESSAGE, e.getMessage(), Constants.API_ERROR_SECURITY_CODE), HttpStatus.FORBIDDEN);
		}

		if (idAttivazione == null) {
			LOG.error("UtenteProdottoAttivatoRESTController --> GET utente non riuscito", "idAttivazione mancante");
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_VALIDATION_MESSAGE, "idAttivazione mancante", Constants.API_ERROR_VALIDATION_CODE), HttpStatus.BAD_REQUEST);
		}

		// check product is activated
		try {
			ProdottoAttivatoDTO prodottoAttivato = prodottoAttivatoService.getProdottoAttivato(idAttivazione);
			
			// controllo attivazione
			if (prodottoAttivato == null || !prodottoAttivato.isValid()) {
				return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_VALIDATION_MESSAGE, "Prodotto non attivato", Constants.API_ERROR_VALIDATION_CODE), HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_GENERIC_MESSAGE, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}

		ProdottoAttivatoDTO prodotto = new ProdottoAttivatoDTO();
		prodotto.setIdAttivazione(idAttivazione);
		List<GruppoUtenteProdottoAttivatoDTO> gruppiUtenti = utenteProdottoAttivatoService.getUtentiProdottoAttivato(prodotto, true);
		LOG.debug("UtenteProdottoAttivatoRESTController --> GET Lista utenti (gruppi) eseguito con successo.");

		return new ResponseEntity<List<GruppoUtenteProdottoAttivatoDTO>>(gruppiUtenti, HttpStatus.OK);

	}
	
    /**
     * Richiesta di una nuova operazione di aggiornamento su una lista di utenti
     * 
     * @param lista di descrittori dell'operazione
     * @return
     */
    @Operation(
    		summary = "Metodo POST per eseguire una nuova operazione di aggiornamento su una lista di utenti",
			responses = {
                    @ApiResponse(description = "POST per eseguire una nuova operazione di aggiornamento su una lista di utenti",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Object.class))),
                    @ApiResponse(responseCode = "200", description = "Operation executed successfully"),
                    @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
                    @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
                    @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
            }
    )
    @PostMapping("/aggiorna-utenti-prodotto")
    public ResponseEntity<ApiResult> aggiornamentoListaUtentiProdottoAttivato(
    		@RequestBody final @Valid List<OperazioneUtenteProdottoAttivatoDTO> operazioni,
    		@AuthenticationPrincipal UserWithAdditionalInfo user
    		) {
    	LOG.debug("UtenteProdottoAttivatoRESTController --> POST nuova operazione di aggiornamento su una lista di utenti." );
    	
        // check permission
        try {
			securityService.checkPermission(user, Constants.ACL_PRODOTTI, Constants.PERMISSION_GESTISCI);
		} catch (MyExtranetSecurityException e) {
			LOG.error(String.format("Access denied for user %s", user.getUsername()), e);

			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_ACCESS_DENIED_TO_DATA_MESSAGE, e.getMessage(), Constants.API_ERROR_SECURITY_CODE), HttpStatus.FORBIDDEN);
		}
    	
    	try {
              
            // execute operation
    		utenteProdottoAttivatoService.aggiornaListaUtentiProdottoAttivato(operazioni);
            
            LOG.debug("UtenteProdottoAttivatoRESTController --> POST operazione di aggiornamento su una lista di utenti eseguito con successo.");

            return new ResponseEntity<ApiResult>(new ApiResult(Constants.RESULT_OK, "Operation successfully performed", operazioni), HttpStatus.OK);			
		} catch (Exception e) {
			LOG.error("UtenteProdottoAttivatoRESTController --> POST operazione di aggiornamento su una lista di utenti non riuscito", e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_GENERIC_MESSAGE, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}

    }

    /**
     * Generate report "UtentiProdottiAttivatiEnte"
     * 
     * @param input
     * @return XLS file
     */
    @Operation(
    		summary = "Metodo POST per generare report XLS su utenti prodotti",
			responses = {
                    @ApiResponse(description = "POST per generare report XLS su utenti prodotti",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Object.class))),
                    @ApiResponse(responseCode = "200", description = "Successfully generated report"),
                    @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
                    @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
                    @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
            }
    )
    @PostMapping("/utenti-prodotti-attivati-excel")
    public ResponseEntity<InputStreamResource> getUtentiProdottiAttivatiEnteExcel(
    		@RequestBody final @Valid UtentiProdottiAttivatiEnteExcelReportInput input,
    		@AuthenticationPrincipal UserWithAdditionalInfo user
    		) {
    	
        LOG.debug("UtenteProdottoAttivatoRESTController --> POST per generare report XLS su utenti prodotti");
        
        // check permission
        try {
			securityService.checkPermission(user, Constants.ACL_PRODOTTI, Constants.PERMISSION_VISUALIZZA);
		} catch (MyExtranetSecurityException e) {
			LOG.error(String.format("Access denied for user %s", user.getUsername()), e);
			return new ResponseEntity(HttpStatus.FORBIDDEN);
		}    
        
        byte[] report = excelReportService.generateUtentiProdottiAttivatiEnteExcelReport(input);
        ByteArrayInputStream bis = new ByteArrayInputStream(report);
        
        LOG.debug("UtenteProdottoAttivatoRESTController --> POST per generare report XLS su utenti prodotti eseguito con successo.");

        var headers = new HttpHeaders();
        headers.add("Content-Type", "application/vnd.ms-excel");
        headers.add("Content-Disposition", "inline; filename=utenti_prodotti.xls");

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(new InputStreamResource(bis));

    }


}
