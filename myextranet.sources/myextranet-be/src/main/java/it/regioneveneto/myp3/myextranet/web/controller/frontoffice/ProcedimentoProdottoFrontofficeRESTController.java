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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
import it.regioneveneto.myp3.myextranet.security.UserWithAdditionalInfo;
import it.regioneveneto.myp3.myextranet.service.EnteService;
import it.regioneveneto.myp3.myextranet.service.ProcedimentoProdottoService;
import it.regioneveneto.myp3.myextranet.service.SecurityService;
import it.regioneveneto.myp3.myextranet.utils.Constants;
import it.regioneveneto.myp3.myextranet.web.controller.ValidationController;
import it.regioneveneto.myp3.myextranet.web.dto.EnteDTO;
import it.regioneveneto.myp3.myextranet.web.dto.ProcedimentoProdottoWithStepsDTO;

@RestController
@RequestMapping("/frontoffice/procedimento-prodotto")
@Tag(name = "tag_procedimento-prodotto", description = "Procedimento prodotto")
public class ProcedimentoProdottoFrontofficeRESTController extends ValidationController {
    private static final Logger LOG = LoggerFactory.getLogger(ProcedimentoProdottoFrontofficeRESTController.class);

    @Autowired
    private ProcedimentoProdottoService procedimentoProdottoService;
    
    @Autowired
    private EnteService enteService;
    
    @Autowired
    private SecurityService securityService;
    
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
    	LOG.debug("ProcedimentoProdottoFrontofficeRESTController --> GET procedimento prodotto with id " + idProdottoProc);
    	
        // check permission
        try {
        	securityService.checkUserValidity(user);
		} catch (MyExtranetSecurityException e) {
			LOG.error(String.format("Access denied for user %s", user.getUsername()), e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_ACCESS_DENIED_TO_DATA_MESSAGE, e.getMessage(), Constants.API_ERROR_SECURITY_CODE), HttpStatus.FORBIDDEN);
		}

        ProcedimentoProdottoWithStepsDTO procedimentoProdotto;
		try {
			procedimentoProdotto = procedimentoProdottoService.getProcedimentoProdotto(idProdottoProc);

			LOG.debug("ProcedimentoProdottoFrontofficeRESTController --> GET procedimento prodotto eseguito con successo.");

        	return new ResponseEntity<ProcedimentoProdottoWithStepsDTO>(procedimentoProdotto, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("ProcedimentoProdottoFrontofficeRESTController --> GET procedimento prodotto non riuscito", e);
	        return new ResponseEntity<ProcedimentoProdottoWithStepsDTO>(HttpStatus.BAD_REQUEST);
		}
    }
    
    /**
     * Metodo GET per recuperare il primo step di un procedimento prodotto dato idProdottoAtt e codTipoRich
     * @return prodotti
     * @throws Exception
     */
    @Operation(
    		summary = "Metodo GET per recuperare il primo step di un procedimento prodotto",
			responses = {
                    @ApiResponse(description = "GET per recuperare il primo step di un procedimento prodotto",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Object.class))),
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved data"),
                    @ApiResponse(responseCode = "400", description = "Procedimento prodotto not found"),
                    @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
                    @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
                    @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
            }
    )
    @GetMapping("/primo-step")
    public ResponseEntity<ProcedimentoProdottoWithStepsDTO> getProcedimentoProdottoPrimoStep(
    		@RequestParam( name = "idEnte", required = true) final Integer idEnte,
    		@RequestParam( name = "idProdottoAtt", required = true) final Integer idProdottoAtt,
    		@RequestParam( name = "codTipoRich", required = true) final String codTipoRich,
    		@AuthenticationPrincipal UserWithAdditionalInfo user
    		) {
    	LOG.debug("ProcedimentoProdottoFrontofficeRESTController --> GET primo step di un procedimento prodotto");
    	
        // check permission
        try {
        	securityService.checkUserValidity(user);
		} catch (MyExtranetSecurityException e) {
			LOG.error(String.format("Access denied for user %s", user.getUsername()), e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_ACCESS_DENIED_TO_DATA_MESSAGE, e.getMessage(), Constants.API_ERROR_SECURITY_CODE), HttpStatus.FORBIDDEN);
		}
        
        try {
			EnteDTO ente = enteService.getEnte(idEnte);
			securityService.checkIsDataOwner(ente, user);
			
			ProcedimentoProdottoWithStepsDTO procedimentoProdotto = procedimentoProdottoService.getProcedimentoProdottoPrimoStep(idProdottoAtt, codTipoRich);
			
			LOG.debug("ProcedimentoProdottoFrontofficeRESTController --> GET primo step di un procedimento prodotto eseguito con successo.");

			return new ResponseEntity<ProcedimentoProdottoWithStepsDTO>(procedimentoProdotto, HttpStatus.OK);
		} catch (MyExtranetSecurityException e) {
			LOG.error(String.format("Access denied for user %s", user.getUsername()), e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_ACCESS_DENIED_TO_DATA_MESSAGE, e.getMessage(), Constants.API_ERROR_SECURITY_CODE), HttpStatus.FORBIDDEN);
		} catch (Exception e) {
			LOG.error("ProcedimentoProdottoFrontofficeRESTController --> GET primo step di un procedimento prodotto non riuscito", e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_GENERIC_MESSAGE, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
    }

}
