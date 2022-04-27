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

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AttestatoPDFReport  {
	
	@Autowired
	PDFReportService pdfReportService;

	private static final Logger LOG = LoggerFactory.getLogger(AttestatoPDFReport.class);
	
	public ByteArrayInputStream generateReport(String title, String personName, String textWhat, String textWhen, String textWhere) {
		
		LOG.debug(String.format("Report Generator Service URL: %s", pdfReportService.reportGeneratorServiceUrl));
		LOG.debug(String.format("Attestato Partecipazione Report file: %s", pdfReportService.attestatoPartecipazioneJrxmlFileName));
		
		// prepare input for myreportgenerator service and call it
		try {

			String template = pdfReportService.getTemplate(pdfReportService.attestatoPartecipazioneJrxmlFileName);
			// LOG.debug("Template: " + template);

			AttestatoParameters data = new AttestatoParameters(title, personName, textWhat, textWhen, textWhere);
						
			return pdfReportService.callReportGenerator(template, data.getJsonObj());
		    
		} catch (FileNotFoundException e) {
			LOG.error(e.getLocalizedMessage());
			return null;
		}
	}
	
	public class AttestatoParameters {
		
		private JSONObject jsonObj = new JSONObject();
		
		public JSONObject getJsonObj() {
			return jsonObj;
		}
		
		public AttestatoParameters(String title, String personName, String textWhat, String textWhen, String textWhere) {
			super();
			
			jsonObj.put("title", title);
			jsonObj.put("person_name", personName);
			jsonObj.put("text_what", textWhat);
			jsonObj.put("text_when", textWhen);
			jsonObj.put("text_where", textWhere);
		}
	}
}
