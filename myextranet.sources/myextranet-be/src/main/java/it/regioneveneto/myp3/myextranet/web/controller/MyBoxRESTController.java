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


import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.regioneveneto.myp3.mybox.ContentMetadata;
import it.regioneveneto.myp3.myextranet.bean.ApiResult;
import it.regioneveneto.myp3.myextranet.exception.MyExtranetException;
import it.regioneveneto.myp3.myextranet.exception.MyExtranetSecurityException;
import it.regioneveneto.myp3.myextranet.security.UserWithAdditionalInfo;
import it.regioneveneto.myp3.myextranet.service.MyBoxService;
import it.regioneveneto.myp3.myextranet.service.SecurityService;
import it.regioneveneto.myp3.myextranet.utils.Constants;

@RestController
@RequestMapping("/mybox")
@Tag(name = "tag_mybox", description = "Mybox")
public class MyBoxRESTController {
    private static final Logger LOG = LoggerFactory.getLogger(MyBoxRESTController.class);

    @Autowired
    MyBoxService myBoxService;
    
    @Autowired
    SecurityService securityService;

    /**
     * Metodo GET per recuperare un contenuto da MyBox
     * @return province
     * @throws Exception
     */
    @Operation(
    		summary = "Metodo GET per recuperare un contenuto da MyBox",
			responses = {
                    @ApiResponse(description = "GET per recuperare un contenuto da MyBox",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Object.class))),
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved MyBox content"),
                    @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
                    @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
                    @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
            }
    )

    @GetMapping("/{contentId}")
    public ResponseEntity<InputStreamResource> getMyBoxContent(
    		@PathVariable( name = "contentId", required = true) final String contentId,
    		@AuthenticationPrincipal UserWithAdditionalInfo user
    		) {

        LOG.debug("MyBoxRESTController --> GET MyBox content" );
        
    	// check permission
    	try {
    		securityService.checkPermission(user, Constants.ACL_ENTI, Constants.PERMISSION_VISUALIZZA);
    	} catch (MyExtranetSecurityException e) {
    		LOG.error(String.format("Access denied for user %s", user.getUsername()), e);
    		return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_ACCESS_DENIED_TO_DATA_MESSAGE, e.getMessage(), Constants.API_ERROR_SECURITY_CODE), HttpStatus.FORBIDDEN);
		}
        
    	try {
			InputStream is = myBoxService.getFileContents(contentId);
			ContentMetadata metadata = myBoxService.getFileMetadata(contentId);
			
			String contentType = metadata.getMimeType();
			String fileName = metadata.getFileName();
			
			int idx = fileName.lastIndexOf("_");
			if (idx >= 0) {				
				fileName = fileName.substring(0, idx);
			}
			
			LOG.debug("MyBoxRESTController --> GET MyBox content eseguito con successo.");

			return ResponseEntity
			        .ok()
			        .header("Content-Type", contentType)
			        .header("Content-Disposition", String.format("attachment; filename=%s", fileName) )
			        .body(new InputStreamResource(is));
		} catch (MyExtranetException e) {
			LOG.error("Error getting content from MyBox", e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_GENERIC_MESSAGE, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}

    }
    
    @PostMapping("")
    public synchronized ResponseEntity<ApiResult> insertContent(
    		@RequestParam(name = "file", required = true) MultipartFile file,
    		@AuthenticationPrincipal UserWithAdditionalInfo user
    		) {
    	LOG.debug("MyBoxRESTController --> POST nuovo content");
    	
        // check permission
        try {
			securityService.checkPermission(user, Constants.ACL_ENTI, Constants.PERMISSION_GESTISCI);
		} catch (MyExtranetSecurityException e) {
			LOG.error(String.format("Access denied for user %s", user.getUsername()), e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_ACCESS_DENIED_TO_DATA_MESSAGE, e.getMessage(), Constants.API_ERROR_SECURITY_CODE), HttpStatus.FORBIDDEN);
		}
    	
    	try {
    	
    		ContentMetadata contentMetadata = new ContentMetadata();
    		contentMetadata.setFileName(String.format("%s_%s", file.getOriginalFilename(), UUID.randomUUID()));
    		contentMetadata.setLength(file.getSize());
    		contentMetadata.setMimeType(file.getContentType());
            
    		String contentId = myBoxService.storeFileContents(file.getInputStream(), contentMetadata);
            
            LOG.debug("MyBoxRESTController --> POST nuovo content eseguito con successo.");

            return new ResponseEntity<ApiResult>(new ApiResult(Constants.RESULT_OK, "Successfully created content", contentId), HttpStatus.CREATED);			
		} catch (Exception e) {
			LOG.error("MyBoxRESTController --> POST nuovo content non riuscito", e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_GENERIC_MESSAGE, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}

    }
    
    @PostMapping("/multi")
    public synchronized ResponseEntity<ApiResult> insertMultipleContent(
    		HttpServletResponse response, HttpServletRequest request,
    		@AuthenticationPrincipal UserWithAdditionalInfo user
    		) {
    	LOG.debug("MyBoxRESTController --> POST nuovo content");
    	
        // check permission
        try {
			securityService.checkPermission(user, Constants.ACL_ENTI, Constants.PERMISSION_GESTISCI);
		} catch (MyExtranetSecurityException e) {
			LOG.error(String.format("Access denied for user %s", user.getUsername()), e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_ACCESS_DENIED_TO_DATA_MESSAGE, e.getMessage(), Constants.API_ERROR_SECURITY_CODE), HttpStatus.FORBIDDEN);
		}
    	
    	try {
    	
    		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
    		Iterator<String> it = multipartRequest.getFileNames();
    		
    		Map<String, String> map = new HashMap<String, String>();
    		
    		while (it.hasNext()) {
    			String fileId =it.next();
    			MultipartFile file = multipartRequest.getFile(fileId);
    			
        		ContentMetadata contentMetadata = new ContentMetadata();
        		contentMetadata.setFileName(String.format("%s_%s", file.getOriginalFilename(), UUID.randomUUID()));
        		contentMetadata.setLength(file.getSize());
        		contentMetadata.setMimeType(file.getContentType());
                
        		String contentId = myBoxService.storeFileContents(file.getInputStream(), contentMetadata);

        		map.put(fileId, contentId);
    			
    			String filename = file.getOriginalFilename();
    			LOG.debug(String.format("File received: %s (%s)", fileId, filename));
    		}
            
            LOG.debug("MyBoxRESTController --> POST nuovo content eseguito con successo.");

            return new ResponseEntity<ApiResult>(new ApiResult(Constants.RESULT_OK, "Successfully created content", map), HttpStatus.CREATED);			
		} catch (Exception e) {
			LOG.error("MyBoxRESTController --> POST nuovo content non riuscito", e);
			return new ResponseEntity(new ApiResult(Constants.RESULT_KO, Constants.API_ERROR_GENERIC_MESSAGE, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}

    }

}
