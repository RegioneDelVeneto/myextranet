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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.StringUtils;
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
import it.regioneveneto.myp3.myextranet.bean.StatEntiProdottiRow;
import it.regioneveneto.myp3.myextranet.bean.StatEventiRow;
import it.regioneveneto.myp3.myextranet.bean.StatNomeValoreRow;
import it.regioneveneto.myp3.myextranet.bean.StatPartecipantiRow;
import it.regioneveneto.myp3.myextranet.bean.StatProdottiRow;
import it.regioneveneto.myp3.myextranet.bean.StatServiziErogatiRow;
import it.regioneveneto.myp3.myextranet.exception.MyExtranetSecurityException;
import it.regioneveneto.myp3.myextranet.model.MyExtranetContent;
import it.regioneveneto.myp3.myextranet.security.UserWithAdditionalInfo;
import it.regioneveneto.myp3.myextranet.service.IscrittoEventoService;
import it.regioneveneto.myp3.myextranet.service.MyPortalService;
import it.regioneveneto.myp3.myextranet.service.ProdottoAttivabileService;
import it.regioneveneto.myp3.myextranet.service.ProdottoAttivatoService;
import it.regioneveneto.myp3.myextranet.service.SecurityService;
import it.regioneveneto.myp3.myextranet.service.StatisticheService;
import it.regioneveneto.myp3.myextranet.utils.Constants;
import it.regioneveneto.myp3.myextranet.web.dto.ContentFilterDTO;
import it.regioneveneto.myp3.myextranet.web.dto.PagedData;
import it.regioneveneto.myp3.myextranet.web.dto.StatEntiProdottiRecordDTO;
import it.regioneveneto.myp3.myextranet.web.dto.StatEventiRecordDTO;
import it.regioneveneto.myp3.myextranet.web.dto.StatNomeValoreRecordDTO;
import it.regioneveneto.myp3.myextranet.web.dto.StatPartecipantiRecordDTO;
import it.regioneveneto.myp3.myextranet.web.dto.StatProdottiRecordDTO;
import it.regioneveneto.myp3.myextranet.web.dto.StatServiziErogatiRecordDTO;

@RestController
@RequestMapping("/statistiche")
@Tag(name = "tag_statistiche", description = "Statistiche")
public class StatisticheRESTController {
	
    private static final Logger LOG = LoggerFactory.getLogger(StatisticheRESTController.class);
    
    @Autowired
    private MyPortalService myPortalService;
    
    @Autowired
    private IscrittoEventoService iscrittoEventoService;
    
    @Autowired
    private ProdottoAttivabileService prodottoAttivabileService;
    
    @Autowired
    private ProdottoAttivatoService prodottoAttivatoService;
    
    @Autowired
    private StatisticheService statisticheService;
    
    @Autowired
    private SecurityService securityService;
    
    
    /**
     * Metodo GET per le statistiche sugli eventi (StatEventi)
     * @throws ParseException 
     */
    @Operation(
    		summary = "Metodo GET per le statistiche sugli eventi (StatEventi)",
    		responses = {
    				@ApiResponse(description = "GET per le statistiche sugli eventi (StatEventi)", 
    						content = @Content(mediaType = "application/json",
    								schema = @Schema(implementation = Object.class))),
    	                   
    				@ApiResponse(responseCode = "200", description = "Successfully retrieved contents"),
                    @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
                    @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
                    @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
    		}
    	)
    @GetMapping("/stat-eventi")
    public ResponseEntity StatEventi(
    		@ModelAttribute( name = "_filter") ContentFilterDTO contentFilter,
    		@RequestParam( name = "outputType", required = false, defaultValue = "CSV"  ) final String outputType,
    		@AuthenticationPrincipal UserWithAdditionalInfo user
    		) throws ParseException{
    	LOG.debug("StatisticheRESTController --> GET statistiche sugli eventi (StatEventi)" );
    	
    	// check permission
    	try {
    		securityService.checkPermission(user, Constants.ACL_EVENTI, Constants.PERMISSION_VISUALIZZA);
    	} catch (MyExtranetSecurityException e) {
    		LOG.error(String.format("Access denied for user %s", user.getUsername()), e);
    		return new ResponseEntity(HttpStatus.FORBIDDEN);
    	}
    	
    	boolean outputCSV = "CSV".equals(outputType);
    	
    	List<StatEventiRecordDTO> outputRecords = new ArrayList<StatEventiRecordDTO>();
    	
    	PagedData<MyExtranetContent> pagedContents;
    	
    	String contentType = "CT_myextranet_d_myext_events";

		pagedContents = myPortalService.advancedSearchPaginated(contentFilter, contentType, 1, 9999999, null, null);

		List<MyExtranetContent> contents = pagedContents.getRecords();			
		
		StringWriter sw = new StringWriter();
		PrintWriter writer = new PrintWriter(sw);
		
		if (outputCSV) {			
			new StatEventiRecordDTO().writeCSVHeader(writer);
		}
		List<String> eventIds = new ArrayList<String>();
		List<String> refsIds = new ArrayList<String>();
		
		contents = contents.stream().filter(content -> {
				
				if (!content.isAnnullato()) {
					eventIds.add(content.getId());
					// collect references' ids				
					addIfNotNull(refsIds, content.getProdotto());
					addIfNotNull(refsIds, content.getProgetto());
					return true;
				} else  {
					LOG.debug("Discarding canceled event");
					return false;
				}
			}).collect(Collectors.toList());;
		
		// get data from DB 
		List<StatEventiRow> rows = iscrittoEventoService.getStatEventiRows(eventIds);
		
		// get refs data from MyPortal
		PagedData<MyExtranetContent> refs = null;
		try {			
			refs = myPortalService.getContentsByIds(refsIds.toArray(String[]::new), 1, 999999, null, null);
		} catch (Exception e) {
			LOG.warn("Error getting MyPortal contents by ids", e);
		}
		
		Map<String, String> productPlatformMap = new HashMap<String, String>();
		List<String> platformIds = new ArrayList<String>();
		
		List<MyExtranetContent> refRecords = refs != null ? refs.getRecords() : new ArrayList<MyExtranetContent>();
		
		// for every ref of type prodotto get the piattaformaAbilitante
		for (MyExtranetContent refContent : refRecords) {
			if ("CT_myextranet_d_myext_products".equals(refContent.getTipo())) {
				String platformId = refContent.getPiattaformaAbilitante();
				if (platformId != null) {					
					productPlatformMap.put(refContent.getId(), platformId);
					platformIds.add(platformId);
				}
			}
		}
		
		// retrieve platforms
		PagedData<MyExtranetContent> platformRefs = null;
		try {			
			platformRefs = myPortalService.getContentsByIds(platformIds.toArray(String[]::new), 1, 999999, null, null);
		} catch (Exception e) {
			LOG.warn("Error getting MyPortal platform contents by ids", e);
		}
		
		List<MyExtranetContent> platformRefRecords = platformRefs != null ? platformRefs.getRecords() : new ArrayList<MyExtranetContent>();
		
		refRecords.addAll(platformRefRecords);
		
		for (MyExtranetContent content : contents) {
			StatEventiRecordDTO record = StatEventiRecordDTO.fromMyExtranetContent(content);
			
			// integrate with DB data
			record.mergeWithDBData(rows);
			
			// resolve refs
			if (refs != null) {					
				record.resolveRefs(refRecords, productPlatformMap);
			}
			
			if (outputCSV) {				
				record.writeCSVRow(writer);
			} else {
				outputRecords.add(record);
			}
		}
    	
    	LOG.debug("StatisticheRESTController --> GET statistiche sugli eventi (StatEventi) eseguito con successo.");     
    	
    	if (outputCSV) {    		
    		var headers = new HttpHeaders();
    		headers.add("Content-Disposition", "inline; filename=stat_eventi.csv");
    		headers.add("Content-Type", "text/csv");
    		
    		byte[] result;
    		
    		try {
				result = sw.toString().getBytes("UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				result = sw.toString().getBytes();
			}
    		
    		return ResponseEntity
    				.ok()
    				.headers(headers)
    				.body(result);
    	} else {
    		return new ResponseEntity<List<StatEventiRecordDTO>>(outputRecords, HttpStatus.OK);
    	}

    }

    private void addIfNotNull(List<String> refsIds, String stringToAdd) {
		if (stringToAdd != null) {
			refsIds.add(stringToAdd);
		}
	}

	/**
     * Metodo GET per le statistiche sui partecipanti (StatPartecipanti)
     * @throws ParseException 
     */
    @Operation(
    		summary = "Metodo GET per le statistiche sui partecipanti (StatPartecipanti)",
    		responses = {
    				@ApiResponse(description = "GET per le statistiche sui partecipanti (StatPartecipanti)", 
    						content = @Content(mediaType = "application/json",
    								schema = @Schema(implementation = Object.class))),
    	                   
    				@ApiResponse(responseCode = "200", description = "Successfully retrieved contents"),
                    @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
                    @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
                    @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
    		}
    	)
    @GetMapping("/stat-partecipanti")
    public ResponseEntity StatPartecipanti(
    		@ModelAttribute( name = "_filter") ContentFilterDTO contentFilter,
    		@RequestParam( name = "outputType", required = false, defaultValue = "CSV"  ) final String outputType,
    		@AuthenticationPrincipal UserWithAdditionalInfo user
    		) throws ParseException{
    	LOG.debug("StatisticheRESTController --> GET statistiche sui partecipanti (StatPartecipanti)" );
    	
    	// check permission
    	try {
    		securityService.checkPermission(user, Constants.ACL_EVENTI, Constants.PERMISSION_VISUALIZZA);
    	} catch (MyExtranetSecurityException e) {
    		LOG.error(String.format("Access denied for user %s", user.getUsername()), e);
    		return new ResponseEntity(HttpStatus.FORBIDDEN);
    	}
    	
    	boolean outputCSV = "CSV".equals(outputType);
    	
    	List<StatPartecipantiRecordDTO> outputRecords = new ArrayList<StatPartecipantiRecordDTO>();
    	
    	PagedData<MyExtranetContent> pagedContents;
    	
    	String contentType = "CT_myextranet_d_myext_events";

		pagedContents = myPortalService.advancedSearchPaginated(contentFilter, contentType, 1, 9999999, null, null);

		List<MyExtranetContent> contents = pagedContents.getRecords();			
		
		StringWriter sw = new StringWriter();
		PrintWriter writer = new PrintWriter(sw);
		
		if (outputCSV) {			
			new StatPartecipantiRecordDTO().writeCSVHeader(writer);
		}
		List<String> eventIds = new ArrayList<String>();
		List<String> refsIds = new ArrayList<String>();
		
		contents = contents.stream().filter(content -> {
				
				if (!content.isAnnullato()) {
					eventIds.add(content.getId());
					// collect references' ids
					addIfNotNull(refsIds, content.getProdotto());
					addIfNotNull(refsIds, content.getProgetto());
					return true;
				} else  {
					LOG.debug("Discarding canceled event");
					return false;
				}
			}).collect(Collectors.toList());;
		
		// get data from DB 
		List<StatPartecipantiRow> rows = iscrittoEventoService.getStatPartecipantiRows(eventIds);
		
		// get refs data from MyPortal
		PagedData<MyExtranetContent> refs = null;
		try {			
			refs = myPortalService.getContentsByIds(refsIds.toArray(String[]::new), 1, 999999, null, null);
		} catch (Exception e) {
			LOG.warn("Error getting MyPortal contents by ids", e);
		}
		
		Map<String, String> productPlatformMap = new HashMap<String, String>();
		List<String> platformIds = new ArrayList<String>();
		
		List<MyExtranetContent> refRecords = refs != null ? refs.getRecords() : new ArrayList<MyExtranetContent>();

		// for every ref of type prodotto get the piattaformaAbilitante
		for (MyExtranetContent refContent : refRecords) {
			if ("CT_myextranet_d_myext_products".equals(refContent.getTipo())) {
				String platformId = refContent.getPiattaformaAbilitante();
				if (platformId != null) {					
					productPlatformMap.put(refContent.getId(), platformId);
					platformIds.add(platformId);
				}
			}
		}
		
		// retrieve platforms
		PagedData<MyExtranetContent> platformRefs = null;
		try {			
			platformRefs = myPortalService.getContentsByIds(platformIds.toArray(String[]::new), 1, 999999, null, null);
		} catch (Exception e) {
			LOG.warn("Error getting MyPortal platform contents by ids", e);
		}
		
		List<MyExtranetContent> platformRefRecords = platformRefs != null ? platformRefs.getRecords() : new ArrayList<MyExtranetContent>();
		
		refRecords.addAll(platformRefRecords);

		for (StatPartecipantiRow row : rows) {
			StatPartecipantiRecordDTO record = StatPartecipantiRecordDTO.fromStatPartecipantiRow(row);
			
			// integrate with MyPortal data
			record.mergeWithMyPortalData(contents);
			
			// resolve refs
			if (refs != null) {					
				record.resolveRefs(refRecords, productPlatformMap);
			}
			
			if (outputCSV) {				
				record.writeCSVRow(writer);
			} else {
				outputRecords.add(record);
			}
		}
    	
    	LOG.debug("StatisticheRESTController --> GET statistiche sui partecipanti (StatPartecipanti) eseguito con successo.");
    	
    	if (outputCSV) {    		
    		var headers = new HttpHeaders();
    		headers.add("Content-Disposition", "inline; filename=stat_partecipanti.csv");
    		headers.add("Content-Type", "text/csv");
    		
    		byte[] result;
    		
    		try {
				result = sw.toString().getBytes("UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				result = sw.toString().getBytes();
			}
    		
    		return ResponseEntity
    				.ok()
    				.headers(headers)
    				.body(result);
    	} else {
    		return new ResponseEntity<List<StatPartecipantiRecordDTO>>(outputRecords, HttpStatus.OK);
    	}

    }
    
	/**
     * Metodo GET per le statistiche generali (StatGenerali)
     * @throws ParseException 
     */
    @Operation(
    		summary = "Metodo GET per le statistiche generali (StatGenerali)",
    		responses = {
    				@ApiResponse(description = "GET per le statistiche generali (StatGenerali)", 
    						content = @Content(mediaType = "application/json",
    								schema = @Schema(implementation = Object.class))),
    	                   
    				@ApiResponse(responseCode = "200", description = "Successfully retrieved contents"),
                    @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
                    @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
                    @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
    		}
    	)
    @GetMapping("/stat-generali")
    public ResponseEntity StatGenerali(
    		@RequestParam( name = "outputType", required = false, defaultValue = "CSV"  ) final String outputType,
    		@AuthenticationPrincipal UserWithAdditionalInfo user
    		) throws ParseException{
    	LOG.debug("StatisticheRESTController --> GET statistiche generali (StatGenerali)" );
    	
    	// check permission
    	try {
    		securityService.checkPermission(user, Constants.ACL_PRODOTTI, Constants.PERMISSION_VISUALIZZA);
    	} catch (MyExtranetSecurityException e) {
    		LOG.error(String.format("Access denied for user %s", user.getUsername()), e);
    		return new ResponseEntity(HttpStatus.FORBIDDEN);
    	}
    	
    	boolean outputCSV = "CSV".equals(outputType);
    	
    	List<StatNomeValoreRecordDTO> outputRecords = new ArrayList<StatNomeValoreRecordDTO>();
    			
		StringWriter sw = new StringWriter();
		PrintWriter writer = new PrintWriter(sw);
		
		if (outputCSV) {			
			new StatNomeValoreRecordDTO().writeCSVHeader(writer);
		}
				
		// get data from DB 
		List<StatNomeValoreRow> rows = statisticheService.getStatGeneraliRows();
		
		for (StatNomeValoreRow row : rows) {
			StatNomeValoreRecordDTO record = StatNomeValoreRecordDTO.fromStatNomeValoreRow(row);
			
			if (outputCSV) {				
				record.writeCSVRow(writer);
			} else {
				outputRecords.add(record);
			}
		}
    	
    	LOG.debug("StatisticheRESTController --> GET statistiche generali (StatGenerali) eseguito con successo.");
    	
    	if (outputCSV) {    		
    		var headers = new HttpHeaders();
    		headers.add("Content-Disposition", "inline; filename=stat_generali.csv");
    		headers.add("Content-Type", "text/csv");
    		
    		byte[] result;
    		
    		try {
				result = sw.toString().getBytes("UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				result = sw.toString().getBytes();
			}
    		
    		return ResponseEntity
    				.ok()
    				.headers(headers)
    				.body(result);
    	} else {
    		return new ResponseEntity<List<StatNomeValoreRecordDTO>>(outputRecords, HttpStatus.OK);
    	}

    }

    
	/**
     * Metodo GET per le statistiche sui prodotti (StatProdotti)
     * @throws ParseException 
     */
    @Operation(
    		summary = "Metodo GET per le statistiche sui prodotti (StatProdotti)",
    		responses = {
    				@ApiResponse(description = "GET per le statistiche sui prodotti (StatProdotti)", 
    						content = @Content(mediaType = "application/json",
    								schema = @Schema(implementation = Object.class))),
    	                   
    				@ApiResponse(responseCode = "200", description = "Successfully retrieved contents"),
                    @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
                    @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
                    @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
    		}
    	)
    @GetMapping("/stat-prodotti")
    public ResponseEntity StatProdotti(
    		@RequestParam( name = "outputType", required = false, defaultValue = "CSV"  ) final String outputType,
    		@AuthenticationPrincipal UserWithAdditionalInfo user
    		) throws ParseException{
    	LOG.debug("StatisticheRESTController --> GET statistiche sui prodotti (StatProdotti)" );
    	
    	// check permission
    	try {
    		securityService.checkPermission(user, Constants.ACL_PRODOTTI, Constants.PERMISSION_VISUALIZZA);
    	} catch (MyExtranetSecurityException e) {
    		LOG.error(String.format("Access denied for user %s", user.getUsername()), e);
    		return new ResponseEntity(HttpStatus.FORBIDDEN);
    	}
    	
    	boolean outputCSV = "CSV".equals(outputType);
    	
    	List<StatProdottiRecordDTO> outputRecords = new ArrayList<StatProdottiRecordDTO>();
    			
		StringWriter sw = new StringWriter();
		PrintWriter writer = new PrintWriter(sw);
		
		if (outputCSV) {			
			new StatProdottiRecordDTO().writeCSVHeader(writer);
		}
				
		// get data from DB 
		List<StatProdottiRow> rows = prodottoAttivabileService.getStatProdottiRows();
				
		for (StatProdottiRow row : rows) {
			StatProdottiRecordDTO record = StatProdottiRecordDTO.fromStatProdottiRow(row);
									
			if (outputCSV) {				
				record.writeCSVRow(writer);
			} else {
				outputRecords.add(record);
			}
		}
    	
    	LOG.debug("StatisticheRESTController --> GET statistiche sui prodotti (StatProdotti) eseguito con successo.");
    	
    	if (outputCSV) {    		
    		var headers = new HttpHeaders();
    		headers.add("Content-Disposition", "inline; filename=stat_prodotti.csv");
    		headers.add("Content-Type", "text/csv");
    		
    		byte[] result;
    		
    		try {
				result = sw.toString().getBytes("UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				result = sw.toString().getBytes();
			}
    		
    		return ResponseEntity
    				.ok()
    				.headers(headers)
    				.body(result);

    	} else {
    		return new ResponseEntity<List<StatProdottiRecordDTO>>(outputRecords, HttpStatus.OK);
    	}

    }

	/**
     * Metodo GET per le statistiche sui prodotti degli enti (StatEntiProdotti)
     * @throws ParseException 
     */
    @Operation(
    		summary = "Metodo GET per le statistiche sui prodotti degli enti (StatEntiProdotti)",
    		responses = {
    				@ApiResponse(description = "GET per le statistiche sui prodotti degli enti (StatEntiProdotti)", 
    						content = @Content(mediaType = "application/json",
    								schema = @Schema(implementation = Object.class))),
    	                   
    				@ApiResponse(responseCode = "200", description = "Successfully retrieved contents"),
                    @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
                    @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
                    @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
    		}
    	)
    @GetMapping("/stat-enti-prodotti")
    public ResponseEntity StatEntiProdotti(
    		@RequestParam( name = "outputType", required = false, defaultValue = "CSV"  ) final String outputType,
    		@AuthenticationPrincipal UserWithAdditionalInfo user
    		) throws ParseException{
    	LOG.debug("StatisticheRESTController --> GET statistiche sui prodotti (StatProdotti)" );
    	
    	// check permission
    	try {
    		securityService.checkPermission(user, Constants.ACL_PRODOTTI, Constants.PERMISSION_VISUALIZZA);
    	} catch (MyExtranetSecurityException e) {
    		LOG.error(String.format("Access denied for user %s", user.getUsername()), e);
    		return new ResponseEntity(HttpStatus.FORBIDDEN);
    	}
    	
    	boolean outputCSV = "CSV".equals(outputType);
    	
    	List<StatEntiProdottiRecordDTO> outputRecords = new ArrayList<StatEntiProdottiRecordDTO>();
    			
		StringWriter sw = new StringWriter();
		PrintWriter writer = new PrintWriter(sw);
		
		if (outputCSV) {			
			new StatEntiProdottiRecordDTO().writeCSVHeader(writer);
		}
				
		// get data from DB 
		List<StatEntiProdottiRow> rows = prodottoAttivatoService.getStatEntiProdottiRows();
		
		for (StatEntiProdottiRow row : rows) {
			StatEntiProdottiRecordDTO record = StatEntiProdottiRecordDTO.fromStatEntiProdottiRow(row);
			
			if (outputCSV) {				
				record.writeCSVRow(writer);
			} else {
				outputRecords.add(record);
			}
		}
    	
    	LOG.debug("StatisticheRESTController --> GET statistiche sui prodotti degli enti (StatEntiProdotti) eseguito con successo.");
    	
    	if (outputCSV) {    		
    		var headers = new HttpHeaders();
    		headers.add("Content-Disposition", "inline; filename=stat_enti_prodotti.csv");
    		headers.add("Content-Type", "text/csv");
    		
    		byte[] result;
    		
    		try {
				result = sw.toString().getBytes("UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				result = sw.toString().getBytes();
			}
    		
    		return ResponseEntity
    				.ok()
    				.headers(headers)
    				.body(result);
    	} else {
    		return new ResponseEntity<List<StatEntiProdottiRecordDTO>>(outputRecords, HttpStatus.OK);
    	}

    }
    
	/**
     * Metodo GET per le statistiche sui servizi erogati (StatServiziErogati)
     * @throws ParseException 
     */
    @Operation(
    		summary = "Metodo GET per le statistiche sui servizi erogati (StatServiziErogati)",
    		responses = {
    				@ApiResponse(description = "GET per le statistiche sui servizi erogati (StatServiziErogati)", 
    						content = @Content(mediaType = "application/json",
    								schema = @Schema(implementation = Object.class))),
    	                   
    				@ApiResponse(responseCode = "200", description = "Successfully retrieved contents"),
                    @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
                    @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
                    @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
    		}
    	)
    @GetMapping("/stat-servizi-erogati")
    public ResponseEntity StatServiziErogati(
    		@RequestParam( name = "outputType", required = false, defaultValue = "CSV"  ) final String outputType,
    		@AuthenticationPrincipal UserWithAdditionalInfo user
    		) throws ParseException{
    	LOG.debug("StatisticheRESTController --> GET statistiche sui prodotti (StatProdotti)" );
    	
    	// check permission
    	try {
    		securityService.checkPermission(user, Constants.ACL_PRODOTTI, Constants.PERMISSION_VISUALIZZA);
    	} catch (MyExtranetSecurityException e) {
    		LOG.error(String.format("Access denied for user %s", user.getUsername()), e);
    		return new ResponseEntity(HttpStatus.FORBIDDEN);
    	}
    	
    	boolean outputCSV = "CSV".equals(outputType);
    	
    	List<StatServiziErogatiRecordDTO> outputRecords = new ArrayList<StatServiziErogatiRecordDTO>();
    			
		StringWriter sw = new StringWriter();
		PrintWriter writer = new PrintWriter(sw);
		
		if (outputCSV) {			
			new StatServiziErogatiRecordDTO().writeCSVHeader(writer);
		}
		
		// get data about products/services from MyPortal
    	PagedData<MyExtranetContent> pagedContents;
    	
    	String contentType = "CT_myextranet_d_myext_services";

    	ContentFilterDTO contentFilter = new ContentFilterDTO();
		pagedContents = myPortalService.advancedSearchPaginated(contentFilter, contentType, 1, 9999999, null, null);

		List<MyExtranetContent> contents = pagedContents.getRecords();			

		Map<String, Integer> productServicesMap = new HashMap<String, Integer>();
		
		for (MyExtranetContent serviceContent: contents) {
			List<String> products = getProductsList(serviceContent);
			
			for (String product : products) {
				Integer counter = productServicesMap.get(product);
				if (counter == null) {
					counter = Integer.valueOf(1);
					productServicesMap.put(product, counter);
				} else {
					productServicesMap.put(product, counter + 1);
				}
			}
		}
				
		// get data from DB 
		List<StatServiziErogatiRow> rows = prodottoAttivatoService.getStatServiziErogatiRows();
		
		String currentTipologiaEnte = null;
		int currentCountProdotti = 0;
		int currentCountServizi = 0;
		
		for (StatServiziErogatiRow row : rows) {
			
			String tipologiaEnte = row.getDesCategoria();
			
			if (!tipologiaEnte.equals(currentTipologiaEnte)) {
				
				if (currentTipologiaEnte != null) {
					StatServiziErogatiRecordDTO record = new StatServiziErogatiRecordDTO();
					record.setTipologiaEnte(currentTipologiaEnte);
					record.setNumProdotti(currentCountProdotti);
					record.setNumServizi(currentCountServizi);
					
					if (outputCSV) {				
						record.writeCSVRow(writer);
					} else {
						outputRecords.add(record);
					}
				}
				
				currentTipologiaEnte = tipologiaEnte;
				currentCountProdotti = 0;
				currentCountServizi = 0;
			}
			
			// increment
			currentCountProdotti += row.getCount();
			Integer numServices = productServicesMap.get(row.getIdProdotto());
			if (numServices == null) numServices = 0;
			currentCountServizi += (row.getCount() * numServices);
			
		}
		// last record
		if (currentTipologiaEnte != null) {
			StatServiziErogatiRecordDTO record = new StatServiziErogatiRecordDTO();
			record.setTipologiaEnte(currentTipologiaEnte);
			record.setNumProdotti(currentCountProdotti);
			record.setNumServizi(currentCountServizi);
			
			if (outputCSV) {				
				record.writeCSVRow(writer);
			} else {
				outputRecords.add(record);
			}
		}
    	
    	LOG.debug("StatisticheRESTController --> GET statistiche sui servizi erogati (StatServiziErogati) eseguito con successo.");
    	
    	if (outputCSV) {    		
    		var headers = new HttpHeaders();
    		headers.add("Content-Disposition", "inline; filename=stat_servizi_erogati.csv");
    		headers.add("Content-Type", "text/csv");
    		
    		byte[] result;
    		
    		try {
				result = sw.toString().getBytes("UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				result = sw.toString().getBytes();
			}
    		
    		return ResponseEntity
    				.ok()
    				.headers(headers)
    				.body(result);
    	} else {
    		return new ResponseEntity<List<StatServiziErogatiRecordDTO>>(outputRecords, HttpStatus.OK);
    	}

    }

	private List<String> getProductsList(MyExtranetContent serviceContent) {
		
		
		String prodotto = serviceContent.getProdotto();
		
		LOG.debug(String.format("Productlist = %s", prodotto));
		
		if (!StringUtils.hasText(prodotto)) return new ArrayList<String>();
		
		String[] items = prodotto.split(";");
		
		return Arrays.asList(items);
	}


}
