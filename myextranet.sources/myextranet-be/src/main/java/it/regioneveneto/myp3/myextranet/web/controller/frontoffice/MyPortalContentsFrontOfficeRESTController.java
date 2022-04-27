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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
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
import it.regioneveneto.myp3.myextranet.bean.CounterPerEntity;
import it.regioneveneto.myp3.myextranet.bean.ApiResult;
import it.regioneveneto.myp3.myextranet.bean.StatEventiRow;
import it.regioneveneto.myp3.myextranet.bean.StatPartecipantiRow;
import it.regioneveneto.myp3.myextranet.exception.MyExtranetSecurityException;
import it.regioneveneto.myp3.myextranet.model.MyExtranetContent;
import it.regioneveneto.myp3.myextranet.security.UserWithAdditionalInfo;
import it.regioneveneto.myp3.myextranet.service.CollaboratoreProgettoService;
import it.regioneveneto.myp3.myextranet.service.IscrittoEventoService;
import it.regioneveneto.myp3.myextranet.service.MyPortalService;
import it.regioneveneto.myp3.myextranet.service.SecurityService;
import it.regioneveneto.myp3.myextranet.utils.Constants;
import it.regioneveneto.myp3.myextranet.web.dto.ContentFilterDTO;
import it.regioneveneto.myp3.myextranet.web.dto.PagedData;
import it.regioneveneto.myp3.myextranet.web.dto.StatEventiRecordDTO;
import it.regioneveneto.myp3.myextranet.web.dto.StatPartecipantiRecordDTO;

@RestController
@RequestMapping("/frontoffice/myportal")
@Tag(name = "tag_myportal", description = "MyPortal")
public class MyPortalContentsFrontOfficeRESTController {
	
    private static final Logger LOG = LoggerFactory.getLogger(MyPortalContentsFrontOfficeRESTController.class);
    
    @Autowired
    private MyPortalService myPortalService;
    
    @Autowired
    private IscrittoEventoService iscrittoEventoService;
    
    @Autowired
    private CollaboratoreProgettoService collaboratoreProgettoService;
    
    @Autowired
    private SecurityService securityService;
    
    /**
     * Metodo GET per la ricerca avanzata dei contenuti
     * @throws ParseException 
     */
    @Operation(
    		summary = "Metodo GET per la ricerca avanzata dei contenuti",
    		responses = {
    				@ApiResponse(description = "GET per ricerca avanzata contenuti myPortal", 
    						content = @Content(mediaType = "application/json",
    								schema = @Schema(implementation = Object.class))),
    	                   
    				@ApiResponse(responseCode = "200", description = "Successfully retrieved contents"),
                    @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
                    @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
                    @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
    		}
    	)
    @GetMapping("/contents")
    public ResponseEntity advancedSearchPaginated(
    		@ModelAttribute( name = "_filter") ContentFilterDTO contentFilter,
    		@RequestParam( name = "contentIds", required = false ) final String[] contentIds,
    		@RequestParam( name = "_type", required = true ) final String contentType,
    		@RequestParam( name = "_page", required = false, defaultValue = "0" ) final Integer pageNumber,
    		@RequestParam( name = "_pageSize", required = false, defaultValue = "10" ) final Integer pageSize,
    		@RequestParam( name = "_orderBy", required = false ) final String orderProperty,
    		@RequestParam( name = "_orderDir", required = false ) final String orderDirection,
    		@AuthenticationPrincipal UserWithAdditionalInfo user
    		) throws ParseException{
    	LOG.debug("MyPortalContentsRESTController --> GET contents" );
    	
    	// check permission
    	try {
    		securityService.checkUserValidity(user);
    	} catch (MyExtranetSecurityException e) {
    		LOG.error(String.format("Access denied for user %s", user.getUsername()), e);
    		return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_ACCESS_DENIED_TO_DATA_MESSAGE, e.getMessage(), Constants.API_ERROR_SECURITY_CODE), HttpStatus.FORBIDDEN);
    	}
    	
    	PagedData<MyExtranetContent> pagedContents;
    	
    	if (contentIds != null) {
    		pagedContents = myPortalService.getContentsByIds(contentIds, pageNumber, pageSize,orderProperty, orderDirection);
    	} else {    	
    		if (contentType == null) {
    			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_VALIDATION_MESSAGE, "Parametro '_type' non presente", Constants.API_ERROR_VALIDATION_CODE), HttpStatus.BAD_REQUEST);
    		}
    		pagedContents = myPortalService.advancedSearchPaginated(contentFilter, contentType, pageNumber, pageSize,orderProperty, orderDirection);
    	}
    	
    	LOG.debug("MyPortalContentsRESTController --> GET contents eseguito con successo.");
        return new ResponseEntity<PagedData<MyExtranetContent>>(pagedContents, HttpStatus.OK);

    }
    
    @Operation(
    		summary = "Metodo GET per recupero di un contenuto da myportal",
    		responses = {
    				@ApiResponse(description = "GET per recupero di un contenuto da myportal", 
    						content = @Content(mediaType = "application/json",
    								schema = @Schema(implementation = Object.class))),
    	                   
    				@ApiResponse(responseCode = "200", description = "Successfully retrieved contents"),
                    @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
                    @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
                    @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
    		}
    	)
    @GetMapping("/content/{id}")
    public ResponseEntity advancedSearchPaginated(
    		@PathVariable(name = "id") final String id,
    		@AuthenticationPrincipal UserWithAdditionalInfo user
    		) throws ParseException{
    	LOG.debug("MyPortalContentsRESTController --> GET content" );
    	
    	MyExtranetContent pagedContents = myPortalService.getContentById(id);

    	
    	// check permission
    	try {
    		securityService.checkUserValidity(user);
    	} catch (MyExtranetSecurityException e) {
    		LOG.error(String.format("Access denied for user %s", user.getUsername()), e);
    		return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_ACCESS_DENIED_TO_DATA_MESSAGE, e.getMessage(), Constants.API_ERROR_SECURITY_CODE), HttpStatus.FORBIDDEN);
    	}
    	
        
    	LOG.debug("MyPortalContentsRESTController --> GET content eseguito con successo.");
        return new ResponseEntity<MyExtranetContent>(pagedContents, HttpStatus.OK);

    }
       
    	
	private PagedData<MyExtranetContent> getProjectsWithPendingRequestsOnly(ContentFilterDTO contentFilter,
			String contentType, Integer pageNumber, Integer pageSize, String orderProperty, String orderDirection) throws ParseException {
		
		// get data from DB
		List<CounterPerEntity> counters = collaboratoreProgettoService.getPendingCollaborationRequestsPerProject(null);
		
		String[] ids = counters.stream().map(c -> c.getId()).toArray(String[]::new);
		
		// get data from MyPortal
		PagedData<MyExtranetContent> myPortalPaged = myPortalService.advancedSearchPaginated(contentFilter, contentType, pageNumber, pageSize, orderProperty, orderDirection, ids);
		
		// put things together
		populatePendingCounter(myPortalPaged, counters);
		
		return myPortalPaged;
	}

	private PagedData<MyExtranetContent> getProjectsWithPendingRequestsCounter(ContentFilterDTO contentFilter,
			String contentType, Integer pageNumber, Integer pageSize, String orderProperty, String orderDirection) throws ParseException {
		
		// get data from MyPortal
		PagedData<MyExtranetContent> myPortalPaged = myPortalService.advancedSearchPaginated(contentFilter, contentType, pageNumber, pageSize, orderProperty, orderDirection);

		String[] ids = myPortalPaged.getRecords().stream().map(p -> p.getId()).toArray(String[]::new);
		
		// get data from DB
		List<CounterPerEntity> counters = collaboratoreProgettoService.getPendingCollaborationRequestsPerProject(ids);

		// put things together
		populatePendingCounter(myPortalPaged, counters);
		
		return myPortalPaged;
	}
	
	private void populatePendingCounter(PagedData<MyExtranetContent> myPortalPaged, List<CounterPerEntity> counters) {
		
		for (MyExtranetContent project : myPortalPaged.getRecords()) {
			Optional<CounterPerEntity> counterOptional = counters.stream().filter(c -> c.getId().equals(project.getId())).findAny();
			
			project.setPendingCounter(counterOptional.isPresent() ? counterOptional.get().getCount() : 0);
		}
	}
    

}
