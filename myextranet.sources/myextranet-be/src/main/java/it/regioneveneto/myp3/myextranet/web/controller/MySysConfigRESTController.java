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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.regioneveneto.myp3.myconfig.client.models.ConfItemDto;
import it.regioneveneto.myp3.myextranet.bean.ApiResult;
import it.regioneveneto.myp3.myextranet.service.MyConfigService;
import it.regioneveneto.myp3.myextranet.service.MySysConfigService;
import it.regioneveneto.myp3.myextranet.utils.Constants;

@RestController
@RequestMapping("/mysysconfig")
@Tag(name = "tag_mysysconfig", description = "mysysconfig")
public class MySysConfigRESTController {

    private static final Logger LOG = LoggerFactory.getLogger(MySysConfigRESTController.class);
    
    @Value("${myconfig.baseUrl}") String baseUrl;
    @Value("${myportal.ipa}") String ipa;
    @Autowired
    private MySysConfigService mySysConfigService;
    
    /**
     * Metodo GET per ottenere val su myconfig da codice
     *
     * @return String auth token
     */
    @Operation(
            summary = "Metodo GET per ottenere val su myconfig da codice",
            responses = {
                    @ApiResponse(description = "GET per ottenere val su mysysconfig da codice",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Object.class))),
                    @ApiResponse(responseCode = "400", description = "Codice non valido")
            }
    )

    @GetMapping("/getItem")
    public ResponseEntity<ConfItemDto> getItemByCode(
    		@RequestParam( name = "codice", required = true, defaultValue = "" ) final String codice
    ) {
        LOG.debug("MySysConfigRESTController --> get per item by code {}.", codice);

        ConfItemDto configItem = this.mySysConfigService.getItem(codice);
        LOG.debug("MySysConfigRESTController --> get per item by code eseguita con successo.");
        return new ResponseEntity<ConfItemDto>(configItem, HttpStatus.OK);
    }
    
    @GetMapping("/getMyintranetDeployUrl")
    public ResponseEntity<ApiResult> getMyintranetDeployUrl()	 {
        LOG.debug("MySysConfigRESTController --> get per getMyintranetDeployUrl");
        String myintranetContentContextUrl = 	mySysConfigService.getConcatValue(ipa + Constants.MYSYSCONFIG_KEY_MYINTRANETLDOMAIN, "") +
				mySysConfigService.getConcatValue(ipa + Constants.MYSYSCONFIG_KEY_MYINTRANETCONTEXT, "");
        LOG.debug("MySysConfigRESTController --> get per getMyintranetDeployUrl eseguita con successo.");
        return  new ResponseEntity<ApiResult>(new ApiResult(Constants.RESULT_OK, "Returning myintranet url", myintranetContentContextUrl), HttpStatus.OK);
    }

	
}
