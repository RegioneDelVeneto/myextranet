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
package it.regioneveneto.myp3.myextranet.service.impl;

import java.text.ParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import it.regioneveneto.myp3.myextranet.model.MyExtranetContent;
import it.regioneveneto.myp3.myextranet.model.MyPortalContent;
import it.regioneveneto.myp3.myextranet.model.MyPortalEntity;
import it.regioneveneto.myp3.myextranet.model.MyPortalPaginatedContent;
import it.regioneveneto.myp3.myextranet.service.MyPortalService;
import it.regioneveneto.myp3.myextranet.web.dto.ContentFilterDTO;
import it.regioneveneto.myp3.myextranet.web.dto.PagedData;

@Service
public class MyPortalServiceImpl implements MyPortalService {

	private static final Logger LOG = LoggerFactory.getLogger(MyPortalServiceImpl.class);

	@Value("${myportal.devUrl}")
	private String myPortalUrl;

	@Value("${myportal.paginationWorkaround}")
	private boolean paginationWorkaround;

	@Value("${myportal.ipa}")
	private String ipa;

	@Override
	public MyExtranetContent getContentById(String id) {
		StringBuilder strBuilder = new StringBuilder(myPortalUrl);
		strBuilder.append("/myportal/");
		strBuilder.append(ipa);
		strBuilder.append("/api/content/");
		strBuilder.append(id);

		RestTemplate restTemplate = new RestTemplate();

		ResponseEntity<MyPortalEntity> respEntity = restTemplate.getForEntity(strBuilder.toString(),
				MyPortalEntity.class);

		MyExtranetContent myextContent = MyExtranetContent
				.convertMyPortalToMyExtranet(respEntity.getBody().getEntity());

		return myextContent;

	}

	@Override
	public PagedData<MyExtranetContent> advancedSearchPaginated(ContentFilterDTO contentFilter, String contentType,
			int pageNumber, int pageSize, String orderProperty, String orderDirection) throws ParseException {

		return advancedSearchPaginated(contentFilter, contentType, pageNumber, pageSize, orderProperty, orderDirection,
				null);
	}

	@Override
	public PagedData<MyExtranetContent> advancedSearchPaginated(ContentFilterDTO contentFilter, String contentType,
			Integer pageNumber, Integer pageSize, String orderProperty, String orderDirection, String[] contentIds)
			throws ParseException {
		StringBuilder strBuilder = new StringBuilder(myPortalUrl);
		strBuilder.append("/myportal/");
		strBuilder.append(ipa);
		strBuilder.append("/api/search-advanced-ids"); //-ids
		strBuilder.append("?page=");
		strBuilder.append(pageNumber);
		strBuilder.append("&&pageSize=");
		strBuilder.append(pageSize);

		if (orderDirection != null) {
			strBuilder.append("&&desc=");
			strBuilder.append(orderDirection);
		}

		if (orderProperty != null) {
			strBuilder.append("&&sortBy=");
			strBuilder.append(orderByFilterConvertion(orderProperty));
		}

		Map<String, Object> preconditions = new HashMap<String, Object>();
		preconditions.put("type", contentType);
		if (contentFilter.getParent() != null && !contentFilter.getParent().isBlank()) {
			preconditions.put("parent", contentFilter.getParent());
			preconditions.put("includeSubFolders", true);
		}
	

		Map<String, Map<String, Object>> attributes = new HashMap<String, Map<String, Object>>();
		if (contentFilter != null && contentFilter.getTitolo() != null && !contentFilter.getTitolo().isEmpty()) {
			Map<String, Object> sys_title = new HashMap<String, Object>();
			sys_title.put("value", contentFilter.getTitolo());
			sys_title.put("type", "TEXT");
			attributes.put("sys_title", sys_title);
		}
		
		//isActivableProduct
		if (contentFilter != null && contentFilter.getMyext_attivabile() != null) {
			Map<String, Object> myext_attivabile = new HashMap<String, Object>();
			myext_attivabile.put("value",contentFilter.getMyext_attivabile());
			myext_attivabile.put("type", "BOOLEAN");
			attributes.put("myext_attivabile", myext_attivabile);
		}

		if (contentFilter != null && contentFilter.getDescrizione() != null
				&& !contentFilter.getDescrizione().isEmpty()) {
			Map<String, Object> sys_description = new HashMap<String, Object>();
			sys_description.put("value", contentFilter.getDescrizione());
			sys_description.put("type", "TEXT");
			attributes.put("sys_description", sys_description);
		}

		if (contentFilter != null && contentFilter.getProdotto() != null && !contentFilter.getProdotto().isEmpty()) {
			Map<String, Object> myext_product_association = new HashMap<String, Object>();
			myext_product_association.put("value", contentFilter.getProdotto());
			myext_product_association.put("type", "ASSOCIATION");
			attributes.put("myext_product_association", myext_product_association);
		}
		if (contentFilter != null && contentFilter.getProgetto() != null && !contentFilter.getProgetto().isEmpty()) {
			Map<String, Object> myext_project_association = new HashMap<String, Object>();
			myext_project_association.put("value", contentFilter.getProgetto());
			myext_project_association.put("type", "ASSOCIATION");
			attributes.put("myext_project_association", myext_project_association);
		}

		Map<String, Object> dateRange = new HashMap<String, Object>();
		if (contentFilter != null && contentFilter.getDataA() != null) {
			dateRange.put("key_lte", "sys_start_date");
			dateRange.put("lte", contentFilter.getDataA()); // .toString());
		}
		if (contentFilter != null && contentFilter.getDataDa() != null) {

			dateRange.put("key_gte", "sys_start_date");
			dateRange.put("gte", contentFilter.getDataDa());// .toString());
		}

		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		JSONObject eventsRequestJsonObject = new JSONObject();
		eventsRequestJsonObject.put("preconditions", preconditions);
		eventsRequestJsonObject.put("attributes", attributes);
		eventsRequestJsonObject.put("dateRange", dateRange);
		
		if (contentIds != null && contentIds.length > 0) {
			JSONArray contentIdsJsonArray = new JSONArray();
			contentIdsJsonArray.addAll(Arrays.asList(contentIds));
			eventsRequestJsonObject.put("ids", Arrays.asList(contentIds));
		}

		eventsRequestJsonObject.put("alwaysReturnEntitiesCount", true);

		HttpEntity<String> request = new HttpEntity<String>(eventsRequestJsonObject.toString(), headers);

		MyPortalPaginatedContent result = restTemplate.postForObject(strBuilder.toString(), request,
				MyPortalPaginatedContent.class);

		String status = result.getStatus();
		// convertMyPortalToMyExtranet
		PagedData<MyExtranetContent> pagedContents = new PagedData<MyExtranetContent>(
				MyExtranetContent.convertMyPortalToMyExtranet(result.getPage().getEntities()),
				result.getPage().getEntitiesCount(), result.getPage().getIndex(), pageSize);

		return pagedContents;

	}

	public PagedData<MyExtranetContent> getContentsByIds(String[] contentIds, Integer pageNumber, Integer pageSize,
			String orderProperty, String orderDirection) {
		StringBuilder strBuilder = new StringBuilder(myPortalUrl);
		strBuilder.append("/myportal/");
		strBuilder.append(ipa);
		strBuilder.append("/api/content");
		strBuilder.append(buildContentIdsQueryString(contentIds));
		if (!paginationWorkaround) {
			strBuilder.append(
					buildPaginationQueryString(contentIds, pageNumber, pageSize, orderProperty, orderDirection));
		}

		String url = strBuilder.toString();
		LOG.debug(String.format("Calling MyPortal on URL: %s", url));

		RestTemplate restTemplate = new RestTemplate();

		MyPortalPaginatedContent result = restTemplate.getForObject(url, MyPortalPaginatedContent.class);

		// workaround for missing pagination in MyPortal service
		List<MyPortalContent> entities;
		int currentPage;
		int totalCount = result.getPage().getEntitiesCount();
		if (paginationWorkaround) {
			List<MyPortalContent> sorted = result.getPage().getEntities();

			// sublist
			int startIndex = (pageNumber - 1) * pageSize;
			int endIndex = startIndex + pageSize;
			entities = sorted.subList(startIndex, Math.min(endIndex, totalCount));

			currentPage = pageNumber;
		} else {
			entities = result.getPage().getEntities();
			currentPage = result.getPage().getIndex();
		}

		PagedData<MyExtranetContent> pagedContents = new PagedData<MyExtranetContent>(
				MyExtranetContent.convertMyPortalToMyExtranet(entities), totalCount, currentPage, pageSize);

		return pagedContents;
	}

	private Object buildPaginationQueryString(String[] contentIds, Integer pageNumber, Integer pageSize,
			String orderProperty, String orderDirection) {

		int count = 0;
		StringBuilder strBuilder = new StringBuilder();
		String prefix = contentIds == null || contentIds.length == 0 ? "?" : "&";
		if (pageNumber != null) {
			strBuilder.append(prefix).append("pageIndex=").append(pageNumber);
			prefix = "&";
		}
		if (pageSize != null) {
			strBuilder.append(prefix).append("pageSize=").append(pageSize);
			prefix = "&";
		}

		return strBuilder.toString();
	}

	private String buildContentIdsQueryString(String[] contentIds) {
		return buildContentIdsQueryString(contentIds, "?");
	}

	private String buildContentIdsQueryString(String[] contentIds, String start) {
		if (contentIds == null || contentIds.length == 0) {
			return "";
		}

		String[] contentIdsStringArray = Arrays.asList(contentIds).stream().map(i -> String.format("contentIds=%s", i))
				.toArray(String[]::new);

		return start + String.join("&", contentIdsStringArray);
	}

	private String orderByFilterConvertion(String myextPropName) {
		switch (myextPropName) {
		case "titolo": {
			return "attributes.sys_title";

		}
		case "id": {
			return "_id";

		}
		case "tipo": {
			return "type";

		}
		case "parent": {
			return "parent";

		}
		case "slug": {
			return "slug";

		}
		case "logo": {
			return "attributes.sys_uri_immagine";

		}
		case "sottotitolo": {
			return "attributes.sys_sottotitolo";

		}
		case "urlQuestionario": {
			return "attributes.myext_output_summary";

		}
		case "urlQuestionarioIn": {
			return "attributes.myext_input_summary";

		}
		case "streamingLink": {
			return "attributes.myext_streaming_link";

		}
		case "linkStreamingRelatore": {
			return "attributes.myext_realatore_streaming_link";

		}
		case "data": {
			return "attributes.sys_start_date";

		}
		case "dateA": {
			return "attributes.sys_end_date";

		}
		case "dateDa": {
			return "attributes.sys_start_date";

		}
		case "prodotto": {
			return "attributes.myext_product_association";

		}
		case "progetto": {
			return "attributes.myext_project_association";

		}
		case "piattaformaAbilitante": {
			return "attributes.myext_platform_association";

		}
		case "aula": {
			return "attributes.myext_classroom";

		}
		case "inPresenza": {
			return "attributes.myext_presence_boolean";

		}
		case "inStreaming": {
			return "attributes.myext_streaming_boolean";

		}
		case "numeroMaxPartecipanti": {
			return "attributes.myext_max_participants";

		}
		case "annullato": {
			return "attributes.myext_cancel_event_boolean";

		}
		case "email": {
			return "attributes.myext_email";

		}
		case "telefono": {
			return "attributes.myext_telephone";

		}
		case "luogo": {
			return "attributes.sys_luogo";

		}
		case "htmlStreamingNotes": {
			return "attributes.myext_straming_notes";

		}
		case "indicazioniStreamingRelatore": {
			return "attributes.myext_realatore_straming_notes";

		}
		default: {
			return myextPropName;
		}
		}
	}

}
