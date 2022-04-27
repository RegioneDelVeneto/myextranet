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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.regioneveneto.myp3.myextranet.exception.MyExtranetSecurityException;
import it.regioneveneto.myp3.myextranet.security.UserWithAdditionalInfo;
import it.regioneveneto.myp3.myextranet.service.SecurityService;
import it.regioneveneto.myp3.myextranet.service.StepProcedimentoProdottoService;
import it.regioneveneto.myp3.myextranet.utils.Constants;
import it.regioneveneto.myp3.myextranet.web.dto.PagedData;
import it.regioneveneto.myp3.myextranet.web.dto.StepProcedimentoProdottoDTO;
import it.regioneveneto.myp3.myextranet.web.dto.StepProcedimentoProdottoFilterDTO;

@RestController
@RequestMapping("/step-procedimento-prodotto")
@Tag(name = "tag_step-procedimento-prodotto", description = "Step procedimento prodotto")
public class StepProcedimentoProdottoRESTController extends ValidationController {
    private static final Logger LOG = LoggerFactory.getLogger(StepProcedimentoProdottoRESTController.class);

    @Autowired
    private StepProcedimentoProdottoService stepProcedimentoProdottoService;
    
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
    public ResponseEntity<PagedData<StepProcedimentoProdottoDTO>> getAllProcedimentiProdotto(
    		@ModelAttribute( name = "_filter" ) StepProcedimentoProdottoFilterDTO stepProcedimentoProdottoFilter,
    		@RequestParam( name = "_page", required = false, defaultValue = "0" ) final Integer pageNumber,
    		@RequestParam( name = "_pageSize", required = false, defaultValue = "10" ) final Integer pageSize,
    		@RequestParam( name = "_orderBy", required = false ) final String orderProperty,
    		@RequestParam( name = "_orderDir", required = false ) final String orderDirection,
    		@AuthenticationPrincipal UserWithAdditionalInfo user
    		) {
    	
        LOG.debug("StepProcedimentoProdottoRESTController --> GET Lista procedimenti prodotto");
        
        // check permission
        try {
			securityService.checkPermission(user, Constants.ACL_PRODOTTI, Constants.PERMISSION_VISUALIZZA);
		} catch (MyExtranetSecurityException e) {
			LOG.error(String.format("Access denied for user %s", user.getUsername()), e);
			return new ResponseEntity<PagedData<StepProcedimentoProdottoDTO>>(HttpStatus.FORBIDDEN);
		}
        
        PagedData<StepProcedimentoProdottoDTO> stepProcedimentoProdotto = stepProcedimentoProdottoService.getStepProcedimentoProdotto(stepProcedimentoProdottoFilter, pageNumber, pageSize, orderProperty, orderDirection);
        LOG.debug("StepProcedimentoProdottoRESTController --> GET Lista procedimenti prodotto eseguito con successo.");

        return new ResponseEntity<PagedData<StepProcedimentoProdottoDTO>>(stepProcedimentoProdotto, HttpStatus.OK);

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
    public ResponseEntity<StepProcedimentoProdottoDTO> getStepProcedimentoProdottoById(
    		@PathVariable( name = "_id", required = true) final Integer idProdottoProc,
    		@AuthenticationPrincipal UserWithAdditionalInfo user
    		) {
    	LOG.debug("StepProcedimentoProdottoRESTController --> GET procedimento prodotto with id " + idProdottoProc);
    	
        // check permission
        try {
			securityService.checkPermission(user, Constants.ACL_PRODOTTI, Constants.PERMISSION_VISUALIZZA);
		} catch (MyExtranetSecurityException e) {
			LOG.error(String.format("Access denied for user %s", user.getUsername()), e);
			return new ResponseEntity(HttpStatus.FORBIDDEN);
		}

        StepProcedimentoProdottoDTO stepProcedimentoProdotto;
		try {
			stepProcedimentoProdotto = stepProcedimentoProdottoService.getStepProcedimentoProdotto(idProdottoProc);

			LOG.debug("StepProcedimentoProdottoRESTController --> GET procedimento prodotto eseguito con successo.");

        	return new ResponseEntity<StepProcedimentoProdottoDTO>(stepProcedimentoProdotto, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("StepProcedimentoProdottoRESTController --> GET procedimento prodotto non riuscito", e);
	        return new ResponseEntity<StepProcedimentoProdottoDTO>(HttpStatus.BAD_REQUEST);
		}
    }
    
}
