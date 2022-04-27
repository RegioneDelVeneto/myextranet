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
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.regioneveneto.myp3.myextranet.bean.ApiResult;
import it.regioneveneto.myp3.myextranet.exception.MyExtranetSecurityException;
import it.regioneveneto.myp3.myextranet.model.MyExtranetContent;
import it.regioneveneto.myp3.myextranet.security.UserWithAdditionalInfo;
import it.regioneveneto.myp3.myextranet.service.IscrittoEventoService;
import it.regioneveneto.myp3.myextranet.service.SecurityService;
import it.regioneveneto.myp3.myextranet.service.impl.MyPortalServiceImpl;
import it.regioneveneto.myp3.myextranet.utils.Constants;
import it.regioneveneto.myp3.myextranet.utils.ObjectMapperUtils;

@RestController
@RequestMapping("/frontoffice/evento")
@Tag(name = "tag_frontoffice_evento", description = "Evento")
public class EventoFrontofficeRESTController {
    private static final Logger LOG = LoggerFactory.getLogger(EventoFrontofficeRESTController.class);

    @Autowired
    private MyPortalServiceImpl myPortalService;
    
    @Autowired
    private SecurityService securityService;
    
    @Autowired
    private IscrittoEventoService iscrittoEventoService;

    /**
     * Metodo GET per recuperare un evento
     * @return evento
     * @throws Exception
     */
    @Operation(
            summary = "Metodo GET per recuperare un evento",
            responses = {
                    @ApiResponse(description = "GET per recuperare un evento",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Object.class))),
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved evento"),
                    @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
                    @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
                    @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
            }
    )
    @GetMapping("/{idEvento}")
    public ResponseEntity<MyExtranetContent> getEvento(
    		@PathVariable( name = "idEvento", required = true) final String idEvento,
    		@AuthenticationPrincipal UserWithAdditionalInfo user
    		) {

        LOG.debug("EventoFrontofficeRESTController --> GET evento" );
        
    	// check permission
    	try {
    		securityService.checkUserValidity(user);
    	} catch (MyExtranetSecurityException e) {
    		LOG.error(String.format("Access denied for user %s", user.getUsername()), e);
    		return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_ACCESS_DENIED_TO_DATA_MESSAGE, e.getMessage(), Constants.API_ERROR_SECURITY_CODE), HttpStatus.FORBIDDEN);
		}
        
        MyExtranetContent evento = myPortalService.getContentById(idEvento);
        
        MyExtranetContent eventoDTO = ObjectMapperUtils.map(evento, MyExtranetContent.class);
        
        // compute availability
        Integer numPostiDisponibili = iscrittoEventoService.getNumPostiDisponibili(idEvento);
        eventoDTO.setPostiDisponibili(numPostiDisponibili);
        
        LOG.debug("EventoFrontofficeRESTController --> GET evento eseguito con successo.");

        return new ResponseEntity<MyExtranetContent>(eventoDTO, HttpStatus.OK);

    }
}
