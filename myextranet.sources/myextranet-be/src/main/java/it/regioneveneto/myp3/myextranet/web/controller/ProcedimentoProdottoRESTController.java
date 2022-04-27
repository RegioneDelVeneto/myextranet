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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
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
import it.regioneveneto.myp3.myextranet.security.UserWithAdditionalInfo;
import it.regioneveneto.myp3.myextranet.service.ProcedimentoProdottoService;
import it.regioneveneto.myp3.myextranet.service.SecurityService;
import it.regioneveneto.myp3.myextranet.utils.Constants;
import it.regioneveneto.myp3.myextranet.web.dto.PagedData;
import it.regioneveneto.myp3.myextranet.web.dto.ProcedimentoProdottoDTO;
import it.regioneveneto.myp3.myextranet.web.dto.ProcedimentoProdottoFilterDTO;
import it.regioneveneto.myp3.myextranet.web.dto.ProcedimentoProdottoUpdateStepsDTO;
import it.regioneveneto.myp3.myextranet.web.dto.ProcedimentoProdottoWithStepsDTO;

@RestController
@RequestMapping("/procedimento-prodotto")
@Tag(name = "tag_procedimento-prodotto", description = "Procedimento prodotto")
public class ProcedimentoProdottoRESTController extends ValidationController {
    private static final Logger LOG = LoggerFactory.getLogger(ProcedimentoProdottoRESTController.class);

    @Autowired
    private ProcedimentoProdottoService procedimentoProdottoService;
    
    @Autowired
    private SecurityService securityService;

    /**
     * Metodo GET per recuperare i procedimenti prodotto
     * @return procedimenti prodotto
     * @throws Exception
     */
    @Operation(
    		summary = "Metodo GET per recuperare i procedimenti prodotto",
			responses = {
                    @ApiResponse(description = "GET per recuperare i procedimenti prodotto",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Object.class))),
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved procedimenti prodotto"),
                    @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
                    @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
                    @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
            }
    )

    @GetMapping("")
    public ResponseEntity<PagedData<ProcedimentoProdottoDTO>> getAllProcedimentiProdotto(
    		@ModelAttribute( name = "_filter" ) ProcedimentoProdottoFilterDTO procedimentoProdottoFilter,
    		@RequestParam( name = "_page", required = false, defaultValue = "0" ) final Integer pageNumber,
    		@RequestParam( name = "_pageSize", required = false, defaultValue = "10" ) final Integer pageSize,
    		@RequestParam( name = "_orderBy", required = false ) final String orderProperty,
    		@RequestParam( name = "_orderDir", required = false ) final String orderDirection,
    		@AuthenticationPrincipal UserWithAdditionalInfo user
    		) {
    	
        LOG.debug("ProcedimentoProdottoRESTController --> GET Lista procedimenti prodotto");
        
        // check permission
        try {
			securityService.checkPermission(user, Constants.ACL_PRODOTTI, Constants.PERMISSION_VISUALIZZA);
		} catch (MyExtranetSecurityException e) {
			LOG.error(String.format("Access denied for user %s", user.getUsername()), e);
			return new ResponseEntity<PagedData<ProcedimentoProdottoDTO>>(HttpStatus.FORBIDDEN);
		}
        
        PagedData<ProcedimentoProdottoDTO> procedimentiProdotto = procedimentoProdottoService.getProcedimentiProdotto(procedimentoProdottoFilter, pageNumber, pageSize, orderProperty, orderDirection);
        LOG.debug("ProcedimentoProdottoRESTController --> GET Lista procedimenti prodotto eseguito con successo.");

        return new ResponseEntity<PagedData<ProcedimentoProdottoDTO>>(procedimentiProdotto, HttpStatus.OK);

    }
    
    /**
     * Metodo GET per recuperare un procedimento prodotto tramite id
     * @return procedimento prodotto
     * @throws Exception
     */
    @Operation(
    		summary = "Metodo GET per recuperare un procedimento prodotto tramite id",
			responses = {
                    @ApiResponse(description = "GET per recuperare un procedimento prodotto",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Object.class))),
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved procedimento prodotto"),
                    @ApiResponse(responseCode = "400", description = "Collaboratore not found"),
                    @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
                    @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
                    @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
            }
    )

    @GetMapping("/{_id}")
    public ResponseEntity<ProcedimentoProdottoWithStepsDTO> getProcedimentoProdottoById(
    		@PathVariable( name = "_id", required = true) final Integer idProdottoProc,
    		@AuthenticationPrincipal UserWithAdditionalInfo user
    		) {
    	LOG.debug("ProcedimentoProdottoRESTController --> GET procedimento prodotto with id " + idProdottoProc);
    	
        // check permission
        try {
			securityService.checkPermission(user, Constants.ACL_PRODOTTI, Constants.PERMISSION_VISUALIZZA);
		} catch (MyExtranetSecurityException e) {
			LOG.error(String.format("Access denied for user %s", user.getUsername()), e);
			return new ResponseEntity(HttpStatus.FORBIDDEN);
		}

        ProcedimentoProdottoWithStepsDTO procedimentoProdotto;
		try {
			procedimentoProdotto = procedimentoProdottoService.getProcedimentoProdotto(idProdottoProc);

			LOG.debug("ProcedimentoProdottoRESTController --> GET procedimento prodotto eseguito con successo.");

        	return new ResponseEntity<ProcedimentoProdottoWithStepsDTO>(procedimentoProdotto, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("ProcedimentoProdottoRESTController --> GET procedimento prodotto non riuscito", e);
	        return new ResponseEntity<ProcedimentoProdottoWithStepsDTO>(HttpStatus.BAD_REQUEST);
		}
    }
    
    /**
     * Richiesta di una nuova operazione di aggiornamento di un procedimento e dei sui step
     * 
     * @param procedimento con lista degli step da modificare
     * @return
     */
    @Operation(
    		summary = "Metodo POST per eseguire una nuova operazione di aggiornamento di un procedimento e dei sui step",
			responses = {
                    @ApiResponse(description = "POST per eseguire una nuova operazione di aggiornamento di un procedimento e dei sui step",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Object.class))),
                    @ApiResponse(responseCode = "200", description = "Operation executed successfully"),
                    @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
                    @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
                    @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
            }
    )
    @PostMapping("/aggiorna-step-procedimento-prodotto")
    public ResponseEntity<ApiResult> aggiornamentoStepProcedimentoProdotto(
    		@RequestBody final @Valid ProcedimentoProdottoUpdateStepsDTO procedimentoProdotto,
    		@AuthenticationPrincipal UserWithAdditionalInfo user
    		) {
    	LOG.debug("ProcedimentoProdottoRESTController --> POST nuova operazione di aggiornamento di un procedimento e dei sui step." );
    	
        // check permission
        try {
			securityService.checkPermission(user, Constants.ACL_PRODOTTI, Constants.PERMISSION_GESTISCI);
		} catch (MyExtranetSecurityException e) {
			LOG.error(String.format("Access denied for user %s", user.getUsername()), e);
			return new ResponseEntity(HttpStatus.FORBIDDEN);
		}
    	
    	try {
              
            // execute operation
    		procedimentoProdottoService.aggiornaStepProcedimentoProdotto(procedimentoProdotto);
            
            LOG.debug("ProcedimentoProdottoRESTController --> POST operazione di aggiornamento di un procedimento e dei sui step eseguito con successo.");

            return new ResponseEntity<ApiResult>(new ApiResult(Constants.RESULT_OK, "Operation successfully performed", procedimentoProdotto), HttpStatus.OK);			
    	} catch (MyExtranetValidationException e) {
    		LOG.error("Validation exception", e);
    		return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_VALIDATION_MESSAGE, e.getMessage(), Constants.API_ERROR_VALIDATION_CODE), HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			LOG.error("ProcedimentoProdottoRESTController --> POST operazione di aggiornamento di un procedimento e dei sui step non riuscito", e);
			return new ResponseEntity<ApiResult>(new ApiResult(Constants.RESULT_KO, "Error executing operation", e.getMessage()), HttpStatus.BAD_REQUEST);
		}

    }

    /**
     * Metodo DELETE per eliminare un procedimento
     * @return procedimento eliminato
     * @throws Exception
     */
    @Operation(
    		summary = "Metodo DELETE per eliminare un procedimento",
			responses = {
                    @ApiResponse(description = "DELETE per eliminare un procedimento",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Object.class))),
                    @ApiResponse(responseCode = "200", description = "Successfully deleted procedimento"),
                    @ApiResponse(responseCode = "400", description = "procedimento not found"),
                    @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
                    @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
                    @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
            }
    )
    @DeleteMapping("/{_id}")
    public ResponseEntity<ApiResult> deleteProdottoById(
    		@PathVariable( name = "_id", required = true) final Integer idProdottoProc,
    		@AuthenticationPrincipal UserWithAdditionalInfo user
    		) {
    	LOG.debug("ProcedimentoProdottoRESTController --> DELETE procedimento with id " + idProdottoProc);
    	
        // check permission
        try {
			securityService.checkPermission(user, Constants.ACL_PRODOTTI, Constants.PERMISSION_GESTISCI);
		} catch (MyExtranetSecurityException e) {
			LOG.error(String.format("Access denied for user %s", user.getUsername()), e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_ACCESS_DENIED_TO_DATA_MESSAGE, e.getMessage(), Constants.API_ERROR_SECURITY_CODE), HttpStatus.FORBIDDEN);
		}

        ProcedimentoProdottoDTO procedimento;
		try {
			procedimento = procedimentoProdottoService.deleteProcedimentoProdotto(idProdottoProc);

			LOG.debug("ProcedimentoProdottoRESTController --> DELETE procedimento eseguito con successo.");

			return new ResponseEntity<ApiResult>(new ApiResult(Constants.RESULT_OK, "Successfully deleted procedimento", procedimento), HttpStatus.OK);
		} catch (DataIntegrityViolationException e) {
			LOG.error("Cannot delete Procedimento Prodotto with idProdottoProc = " + idProdottoProc, e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_VALIDATION_MESSAGE, "Impossibile eliminare l'oggetto in quanto referenziato da altri oggetti", Constants.API_ERROR_VALIDATION_CODE), HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			LOG.error("ProcedimentoProdottoRESTController --> DELETE procedimento non riuscito", e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_GENERIC_MESSAGE, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
    }

    
}
