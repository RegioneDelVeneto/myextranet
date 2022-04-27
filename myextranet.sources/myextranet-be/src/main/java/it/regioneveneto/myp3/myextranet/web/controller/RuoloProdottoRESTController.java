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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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
import it.regioneveneto.myp3.myextranet.service.RuoloProdottoService;
import it.regioneveneto.myp3.myextranet.service.SecurityService;
import it.regioneveneto.myp3.myextranet.utils.Constants;
import it.regioneveneto.myp3.myextranet.web.dto.RuoloProdottoDTO;

@RestController
@RequestMapping("/ruolo-prodotto")
@Tag(name = "tag_ruolo-prodotto", description = "Ruolo Prodotto")
public class RuoloProdottoRESTController {
    private static final Logger LOG = LoggerFactory.getLogger(RuoloProdottoRESTController.class);

    @Autowired
    private RuoloProdottoService ruoloProdottoService;

    @Autowired
    private SecurityService securityService;
    
    /**
     * Metodo GET per recuperare i ruoli prodotto in caso di autocomplete
     * @return comuni
     * @throws Exception
     */
    @Operation(
    		summary = "Metodo GET per recuperare i ruoli prodotto",
			responses = {
                    @ApiResponse(description = "GET per recuperare i ruoli prodotto in caso di autocomplete",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Object.class))),
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved ruoli prodotto"),
                    @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
                    @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
                    @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
            }
    )
    
    @GetMapping("/autocomplete")
    public ResponseEntity<List<RuoloProdottoDTO>> getRuoliProdottoAutocomplete(
    		@ModelAttribute( name = "_filter" ) RuoloProdottoDTO ruoloProdottoFilter,
    		@RequestParam( name = "maxLength", required = false, defaultValue = "20" ) final Integer maxLength,
    		@RequestParam( name = "_orderBy", required = false, defaultValue = "codRuolo" ) final String orderProperty,
    		@RequestParam( name = "_orderDir", required = false, defaultValue = "asc" ) final String orderDirection,
    		@AuthenticationPrincipal UserWithAdditionalInfo user
    		) {
    	LOG.debug("RuoloProdottoRESTController --> GET Lista ruoli prodotto (autocomplete)." );
    	
    	// check permission
    	try {
    		securityService.checkPermission(user, Constants.ACL_PRODOTTI, Constants.PERMISSION_VISUALIZZA);
    	} catch (MyExtranetSecurityException e) {
    		LOG.error(String.format("Access denied for user %s", user.getUsername()), e);
    		return new ResponseEntity(HttpStatus.FORBIDDEN);
    	}
    	
        List<RuoloProdottoDTO> ruoli = ruoloProdottoService.getRuoliProdotto(ruoloProdottoFilter, 1, maxLength, orderProperty, orderDirection).getRecords();
        LOG.debug("RuoloProdottoRESTController --> GET Lista ruoli prodotto eseguito con successo.");

        return new ResponseEntity<List<RuoloProdottoDTO>>(ruoli, HttpStatus.OK);

    }
}
