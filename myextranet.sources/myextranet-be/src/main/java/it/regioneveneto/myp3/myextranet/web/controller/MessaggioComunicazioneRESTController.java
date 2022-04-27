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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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
import it.regioneveneto.myp3.myextranet.security.UserWithAdditionalInfo;
import it.regioneveneto.myp3.myextranet.service.MessaggioService;
import it.regioneveneto.myp3.myextranet.service.SecurityService;
import it.regioneveneto.myp3.myextranet.utils.Constants;
import it.regioneveneto.myp3.myextranet.web.dto.MessaggioDTO;
import it.regioneveneto.myp3.myextranet.web.dto.MessaggioFilterDTO;
import it.regioneveneto.myp3.myextranet.web.dto.PagedData;

@RestController
@RequestMapping("/comunicazione")
@Tag(name = "tag_comunicazione", description = "Comunicazione")
public class MessaggioComunicazioneRESTController {
    private static final Logger LOG = LoggerFactory.getLogger(MessaggioComunicazioneRESTController.class);

    @Autowired
    private MessaggioService messaggioService;
    
    @Autowired
    private SecurityService securityService;

    /**
     * Metodo GET per recuperare le comunicazioni
     * @return comunicazioni
     * @throws Exception
     */
    @Operation(
    		summary = "Metodo GET per recuperare le comunicazioni",
			responses = {
                    @ApiResponse(description = "GET per recuperare le comunicazioni",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Object.class))),
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved comunicazioni"),
                    @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
                    @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
                    @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
            }
    )

    @GetMapping("")
    public ResponseEntity<PagedData<MessaggioDTO>> getAllComunicazioni(
    		@ModelAttribute( name = "_filter" ) MessaggioFilterDTO messaggioFilter,
    		@RequestParam( name = "_page", required = false, defaultValue = "0" ) final Integer pageNumber,
    		@RequestParam( name = "_pageSize", required = false, defaultValue = "10" ) final Integer pageSize,
    		@RequestParam( name = "_orderBy", required = false ) final String orderProperty,
    		@RequestParam( name = "_orderDir", required = false ) final String orderDirection,
    		@AuthenticationPrincipal UserWithAdditionalInfo user
    		) {
    	LOG.debug("MessaggioComunicazioneRESTController --> GET Lista comunicazioni." );
    	
    	// check permission
    	try {
    		securityService.checkPermission(user, Constants.ACL_UTENTI, Constants.PERMISSION_VISUALIZZA);
    	} catch (MyExtranetSecurityException e) {
    		LOG.error(String.format("Access denied for user %s", user.getUsername()), e);
    		return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_ACCESS_DENIED_TO_DATA_MESSAGE, e.getMessage(), Constants.API_ERROR_SECURITY_CODE), HttpStatus.FORBIDDEN);
    	}

        // set the correct tipo for this controller
        messaggioFilter.setTipo(Constants.MESSAGGIO_TIPO_COMUNICAZIONE);
        
        PagedData<MessaggioDTO> messaggi = messaggioService.getMessaggi(messaggioFilter, pageNumber, pageSize, orderProperty, orderDirection);
        LOG.debug("MessaggioComunicazioneRESTController --> GET Lista comunicazioni eseguito con successo.");

        return new ResponseEntity<PagedData<MessaggioDTO>>(messaggi, HttpStatus.OK);

    }
    
    /**
     * Metodo POST per inserire/inviare una comunicazione
     * @return comunicazione
     * @throws Exception
     */
    @Operation(
    		summary = "Metodo POST per inserire una comunicazione",
			responses = {
                    @ApiResponse(description = "POST per inserire una comunicazione",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Object.class))),
                    @ApiResponse(responseCode = "201", description = "Successfully created comunicazione"),
                    @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
                    @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
                    @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
            }
    )
    @PostMapping("")
    public ResponseEntity<ApiResult> insertComunicazione(
    		@RequestBody final @Valid MessaggioDTO newMessaggio,
    		@AuthenticationPrincipal UserWithAdditionalInfo user
    		) {
    	LOG.debug("MessaggioInvitoRESTController --> POST nuova comunicazione." );
    	
    	// check permission
    	try {
    		securityService.checkPermission(user, Constants.ACL_UTENTI, Constants.PERMISSION_GESTISCI);
    	} catch (MyExtranetSecurityException e) {
    		LOG.error(String.format("Access denied for user %s", user.getUsername()), e);
    		return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_ACCESS_DENIED_TO_DATA_MESSAGE, e.getMessage(), Constants.API_ERROR_SECURITY_CODE), HttpStatus.FORBIDDEN);
    	}
    	
    	try {
            
            // check object does not have an Id
            if (newMessaggio.getIdMessaggio() != null) {
            	LOG.error("MessaggioInvitoRESTController --> POST nuova comunicazione usato impropriamente: l'oggetto ha un ID");
            	return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_VALIDATION_MESSAGE, "l'oggetto ha un ID", Constants.API_ERROR_VALIDATION_CODE), HttpStatus.BAD_REQUEST);
            }
            
            // set the correct tipo
            newMessaggio.setTipo(Constants.MESSAGGIO_TIPO_COMUNICAZIONE);
            
            // add evento details to message body
            messaggioService.addEventDetails(newMessaggio);
            
            MessaggioDTO messaggio = messaggioService.insertAndSendMessaggio(newMessaggio);
            LOG.debug("MessaggioInvitoRESTController --> POST nuova comunicazione eseguito con successo.");

            return new ResponseEntity<ApiResult>(new ApiResult(Constants.RESULT_OK, "Successfully created comunicazione", messaggio), HttpStatus.CREATED);			
		} catch (Exception e) {
			LOG.error("MessaggioInvitoRESTController --> POST nuova comunicazione non riuscito", e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_GENERIC_MESSAGE, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}

    }    
    
}
