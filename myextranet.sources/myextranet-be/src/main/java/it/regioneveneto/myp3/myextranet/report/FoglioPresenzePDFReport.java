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
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.regioneveneto.myp3.myextranet.model.IscrittoEvento;
import it.regioneveneto.myp3.myextranet.utils.StringUtils;

@Service
public class FoglioPresenzePDFReport  {
	
	@Autowired
	PDFReportService pdfReportService;

	private static final Logger LOG = LoggerFactory.getLogger(FoglioPresenzePDFReport.class);

	public ByteArrayInputStream generateReport(String title, String subtitle, List<IscrittoEvento> iscritti)  {
	
		LOG.debug(String.format("Report Generator Service URL: %s", pdfReportService.reportGeneratorServiceUrl));
		LOG.debug(String.format("Foglio Presenze Report file: %s", pdfReportService.foglioPresenzeJrxmlFileName));
		
		// prepare input for myreportgenerator service and call it
		try {

			String template = pdfReportService.getTemplate(pdfReportService.foglioPresenzeJrxmlFileName);
			FoglioPresenzeParameters data = new FoglioPresenzeParameters(title, subtitle, iscritti);
						
			return pdfReportService.callReportGenerator(template, data.getJsonObj());
		    
		} catch (FileNotFoundException e) {
			LOG.error(e.getLocalizedMessage());
			return null;
		}
		
	}
	
	private static List<String> iscrittiToJsonStringList(List<IscrittoEvento> iscritti) {
		List<String> list = new ArrayList<String>(iscritti.size());
		
		for (IscrittoEvento iscrittoEvento : iscritti) {
			final JSONObject jo = new JSONObject();
			jo.put("nome", StringUtils.stringOrEmpty(iscrittoEvento.getNome()));
			jo.put("cognome", StringUtils.stringOrEmpty(iscrittoEvento.getCognome()));
			jo.put("email", StringUtils.stringOrEmpty(iscrittoEvento.getEmail()));
			String ente = iscrittoEvento.getEnte() != null ? iscrittoEvento.getEnte().getDenominazione() : StringUtils.stringOrEmpty(iscrittoEvento.getAzienda());
			jo.put("ente", ente);
			list.add(jo.toJSONString());
		}
		
		return list;
	}
	
	public class FoglioPresenzeParameters {
		
		private JSONObject jsonObj = new JSONObject();
		
		public JSONObject getJsonObj() {
			return jsonObj;
		}
		
		public FoglioPresenzeParameters(String title, String subtitle, List<IscrittoEvento> iscritti) {
			super();
			
			jsonObj.put("title", title);
			jsonObj.put("subtitle", subtitle);
			JSONArray jRows = new JSONArray();
			jRows.addAll(iscrittiToJsonStringList(iscritti));	
			jsonObj.put("rows", jRows);
		}

	}
}
