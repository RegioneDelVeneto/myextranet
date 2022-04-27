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
package it.regioneveneto.myp3.myextranet.report;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class PDFReportService {
	private static final Logger LOG = LoggerFactory.getLogger(PDFReportService.class);
	
    protected final String reportGeneratorServiceUrl;
    
    protected final String foglioPresenzeJrxmlFileName;
    protected final String attestatoPartecipazioneJrxmlFileName;

	public PDFReportService(
			@Value("${report.generator.url}") String reportGeneratorServiceUrl, 
			@Value("${report.jasper.foglio-presenze}") String foglioPresenzeJrxmlFileName,
			@Value("${report.jasper.attestato-partecipazione}") String attestatoPartecipazioneJrxmlFileName) {
		super();
		this.reportGeneratorServiceUrl = reportGeneratorServiceUrl;
		this.foglioPresenzeJrxmlFileName = foglioPresenzeJrxmlFileName;
		this.attestatoPartecipazioneJrxmlFileName = attestatoPartecipazioneJrxmlFileName;
	}
	
	public String getTemplate(String templateFileName) throws FileNotFoundException {
	    try {
			InputStream in = getClass().getResourceAsStream("/report/" + templateFileName); 

			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			String template = (String)reader.lines().collect(Collectors.joining(System.lineSeparator()));
			
			return template;
		} catch (Exception e) {
			throw new FileNotFoundException("Not found: " + templateFileName);
		}
	}
	
	public ByteArrayInputStream callReportGenerator(String template, JSONObject data) {
		JSONObject jsonObj = new JSONObject();
		
		jsonObj.put("template", template);
		jsonObj.put("data", data);
		
		HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_JSON);
	    
	    String jsonString = jsonObj.toString();

	    HttpEntity<String> request = 
		  	      new HttpEntity<String>(jsonString, headers);
		    
	    RestTemplate restTemplate = new RestTemplate();
	   
	    ResponseEntity<byte[]> response = restTemplate.exchange(
	    		reportGeneratorServiceUrl,
	            HttpMethod.POST, request, byte[].class, "1");
			    
	    return new ByteArrayInputStream(response.getBody());
	}
}
