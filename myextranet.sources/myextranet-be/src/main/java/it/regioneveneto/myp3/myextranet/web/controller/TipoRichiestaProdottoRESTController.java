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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.regioneveneto.myp3.myextranet.service.TipoRichiestaProdottoService;
import it.regioneveneto.myp3.myextranet.web.dto.PagedData;
import it.regioneveneto.myp3.myextranet.web.dto.TipoRichiestaProdottoDTO;

@RestController
@RequestMapping("/tipo-richiesta-prodotto")
@Tag(name = "tag_tipo-richiesta-prodotto", description = "Tipo Richiesta Prodotto")
public class TipoRichiestaProdottoRESTController {
    private static final Logger LOG = LoggerFactory.getLogger(TipoRichiestaProdottoRESTController.class);

    @Autowired
    private TipoRichiestaProdottoService tipoRichiestaProdottoService;

    /**
     * Metodo GET per recuperare i tipi richiesta prodotto
     * @return tipi richiesta prodotto
     * @throws Exception
     */
    @Operation(
    		summary = "Metodo GET per recuperare i tipi richiesta prodotto",
			responses = {
                    @ApiResponse(description = "GET per recuperare i tipi richiesta prodotto",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Object.class))),
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved tipi richiesta prodotto"),
                    @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
                    @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
                    @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
            }
    )

    @GetMapping("")
    public ResponseEntity<PagedData<TipoRichiestaProdottoDTO>> getAllTipiRichiestaProdotto(
    		@RequestParam( name = "_page", required = false, defaultValue = "0" ) final Integer pageNumber,
    		@RequestParam( name = "_pageSize", required = false, defaultValue = "10" ) final Integer pageSize,
    		@RequestParam( name = "_orderBy", required = false ) final String orderProperty,
    		@RequestParam( name = "_orderDir", required = false ) final String orderDirection
    		) {

        LOG.debug("TipoRichiestaProdottoRESTController --> GET Lista tipi richiesta prodotto." );
        PagedData<TipoRichiestaProdottoDTO> province = tipoRichiestaProdottoService.getTipiRichiestaProdotto(pageNumber, pageSize, orderProperty, orderDirection);
        LOG.debug("TipoRichiestaProdottoRESTController --> GET Lista tipi richiesta prodotto eseguito con successo.");

        return new ResponseEntity<PagedData<TipoRichiestaProdottoDTO>>(province, HttpStatus.OK);

    }
}
