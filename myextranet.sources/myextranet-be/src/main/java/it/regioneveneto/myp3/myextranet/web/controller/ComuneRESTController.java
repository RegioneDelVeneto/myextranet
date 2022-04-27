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
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.regioneveneto.myp3.myextranet.security.UserWithAdditionalInfo;
import it.regioneveneto.myp3.myextranet.service.ComuneService;
import it.regioneveneto.myp3.myextranet.web.dto.ComuneDTO;
import it.regioneveneto.myp3.myextranet.web.dto.ComuneFilterDTO;
import it.regioneveneto.myp3.myextranet.web.dto.PagedData;
import it.regioneveneto.myp3.myextranet.web.dto.ProvinciaDTO;

@RestController
@RequestMapping("/comune")
@Tag(name = "tag_comune", description = "Comune")
public class ComuneRESTController {
    private static final Logger LOG = LoggerFactory.getLogger(ComuneRESTController.class);

    @Autowired
    private ComuneService comuneService;

    /**
     * Metodo GET per recuperare i comuni
     * @return comuni
     * @throws Exception
     */
    @Operation(
            summary = "Metodo GET per recuperare i comuni di una provincia",
            responses = {
                    @ApiResponse(description = "GET per recuperare i comuni di una provincia",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Object.class))),
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved comuni"),
                    @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
                    @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
                    @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
            }
    )

    @GetMapping("")
    public ResponseEntity<PagedData<ComuneDTO>> getAllComuneByProvincia(
    		@Parameter( description = "Identificativo della Provincia", required = true, example = "PD" )
    		@RequestParam( name = "IdProvincia", required = true ) final String IdProvincia,
    		@RequestParam( name = "_page", required = false, defaultValue = "0" ) final Integer pageNumber,
    		@RequestParam( name = "_pageSize", required = false, defaultValue = "10" ) final Integer pageSize,
    		@RequestParam( name = "_orderBy", required = false ) final String orderProperty,
    		@RequestParam( name = "_orderDir", required = false ) final String orderDirection
    		) {

        LOG.debug("ComuneRESTController --> GET Lista comuni by provincia." );
        ProvinciaDTO provincia = new ProvinciaDTO();
        provincia.setCodProvincia(IdProvincia);
        PagedData<ComuneDTO> comuni = comuneService.getComuniByProvincia(provincia, pageNumber, pageSize, orderProperty, orderDirection);
        LOG.debug("ComuneRESTController --> GET Lista comuni by provincia eseguito con successo.");

        return new ResponseEntity<PagedData<ComuneDTO>>(comuni, HttpStatus.OK);

    }
    
    /**
     * Metodo GET per recuperare i comuni in caso di autocomplete
     * @return comuni
     * @throws Exception
     */
    @Operation(
    		summary = "Metodo GET per recuperare i comuni",
			responses = {
                    @ApiResponse(description = "GET per recuperare i comuni in caso di autocomplete",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Object.class))),
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved comuni"),
                    @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
                    @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
                    @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
            }
    )
    
    @GetMapping("/autocomplete")
    public ResponseEntity<List<ComuneDTO>> getComuniAutocomplete(
    		@ModelAttribute( name = "_filter" ) ComuneFilterDTO comuneFilter,
    		@RequestParam( name = "maxLength", required = false, defaultValue = "20" ) final Integer maxLength,
    		@RequestParam( name = "_orderBy", required = false, defaultValue = "desComune" ) final String orderProperty,
    		@RequestParam( name = "_orderDir", required = false, defaultValue = "asc" ) final String orderDirection,
    		@AuthenticationPrincipal UserWithAdditionalInfo user
    		) {
    	LOG.debug("ComuneRESTController --> GET Lista comuni (autocomplete)." );    	
    	
        List<ComuneDTO> comuni = comuneService.getComuni(comuneFilter, 1, maxLength, orderProperty, orderDirection).getRecords();
        LOG.debug("ComuneRESTController --> GET Lista comuni eseguito con successo.");

        return new ResponseEntity<List<ComuneDTO>>(comuni, HttpStatus.OK);

    }
}
