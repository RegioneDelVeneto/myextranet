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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.apache.commons.text.StringSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;

import it.regioneveneto.myp3.mychannel.client.exception.MyChannelException;
import it.regioneveneto.myp3.mychannel.client.model.Attachment;
import it.regioneveneto.myp3.mychannel.client.model.MessageDetails;
import it.regioneveneto.myp3.myextranet.model.AuditModel;
import it.regioneveneto.myp3.myextranet.model.Messaggio;
import it.regioneveneto.myp3.myextranet.model.MyExtranetContent;
import it.regioneveneto.myp3.myextranet.repository.MessaggioRepository;
import it.regioneveneto.myp3.myextranet.service.MessaggioService;
import it.regioneveneto.myp3.myextranet.service.MyChannelService;
import it.regioneveneto.myp3.myextranet.service.MyConfigService;
import it.regioneveneto.myp3.myextranet.service.MyPortalService;
import it.regioneveneto.myp3.myextranet.service.MySysConfigService;
import it.regioneveneto.myp3.myextranet.utils.AuditUtils;
import it.regioneveneto.myp3.myextranet.utils.Constants;
import it.regioneveneto.myp3.myextranet.utils.ObjectMapperUtils;
import it.regioneveneto.myp3.myextranet.utils.RepositoryUtils;
import it.regioneveneto.myp3.myextranet.web.dto.MessaggioDTO;
import it.regioneveneto.myp3.myextranet.web.dto.MessaggioFilterDTO;
import it.regioneveneto.myp3.myextranet.web.dto.PagedData;

@Service
public class MessaggioServiceImpl implements MessaggioService {
	private static final Logger LOG = LoggerFactory.getLogger(MessaggioServiceImpl.class);
	
    @Autowired
    MessaggioRepository messaggioRepository;
    
    @Autowired
    MyChannelService myChannelService;
    
    @Autowired
    MyPortalService myPortalService;
    
    @Autowired
    MySysConfigService mySysConfigService;
    
    @Autowired
    MyConfigService myConfigService;
    
    private final String eventDetailTemplateKey;
    private final String ipa;

	public MessaggioServiceImpl(
			@Value("${myportal.ipa}") String ipa,
			@Value("${myportal.myConfigKeys.eventDetailTemplate}") String eventDetailTemplateKey
			) {
		super();
		this.ipa = ipa;
		this.eventDetailTemplateKey = eventDetailTemplateKey;
	}

	@Override
	@Transactional(rollbackFor = Exception.class, readOnly = true)
	public PagedData<MessaggioDTO> getMessaggi(MessaggioFilterDTO messaggioFilter, int pageNumber, int pageSize,
			String sortProperty, String sortDirection) {
		if (sortProperty == null) sortProperty = "dtInvio";
		if (sortDirection == null) sortDirection = "DESC";

		Pageable pageable = PagedData.buildPageable(pageNumber, pageSize, sortProperty, sortDirection);
		
		Page<Messaggio> page = messaggioRepository.findAll(RepositoryUtils.buildMessaggioFilterSpecification(messaggioFilter), pageable);

		PagedData<MessaggioDTO> pagedData = new PagedData<MessaggioDTO>(page, MessaggioDTO.class);
		return pagedData;
	}

	@Override
	public Messaggio insertAndSendMessaggio(Messaggio messaggio, Attachment[] attachments) throws MyChannelException {
		
		// send messaggio via email
		LOG.debug("**** SENDING EMAIL ****");
		MessageDetails messageDetails = new MessageDetails();
		messageDetails.setAddress(messaggio.getIndirizzo());
		messageDetails.setContent(messaggio.getMessaggio().getBytes());
		messageDetails.setReceiver(messaggio.getDestinatario());
		messageDetails.setTitle(messaggio.getTitolo());
		
		if (attachments != null) {
			messageDetails.setAttachments(attachments);
		}
		
		myChannelService.sendEmail(messageDetails);
		
		// add audit fields
		AuditModel audit = (AuditModel) messaggio;
		AuditUtils.fillAudit(audit);
		
		messaggio.setDtInvio(LocalDate.now());
		
		Messaggio savedMessaggio = messaggioRepository.save(messaggio);
		
		return savedMessaggio;
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class, readOnly = false)
	public MessaggioDTO insertAndSendMessaggio(@Valid MessaggioDTO newMessaggio) throws MyChannelException {
		
		Messaggio messaggio = ObjectMapperUtils.map(newMessaggio, Messaggio.class);
		
		Messaggio savedMessaggio = insertAndSendMessaggio(messaggio, null);
		
		return ObjectMapperUtils.map(savedMessaggio, MessaggioDTO.class);
	}
	
	@Override
	public void addEventDetails(@Valid MessaggioDTO messaggio) {		
		
		String idEvento = messaggio.getIdContenuto();
		if (StringUtils.hasText(idEvento)) {
			StringBuilder sb = new StringBuilder(messaggio.getMessaggio());
			String eventDetailStr = getEventDetailString(idEvento);
			sb.append(eventDetailStr);
			messaggio.setMessaggio(sb.toString());
		}
	}
	
	@Override
	public void addEventDetails(Messaggio messaggio) {
		
		String idEvento = messaggio.getIdContenuto();
		if (StringUtils.hasText(idEvento)) {
			StringBuilder sb = new StringBuilder(messaggio.getMessaggio());
			String eventDetailStr = getEventDetailString(idEvento);
			sb.append(eventDetailStr);
			messaggio.setMessaggio(sb.toString());
		}
	}
	
	@Override
	public String getEventDetailString(String idEvento) {
		if (StringUtils.hasText(idEvento)) {
			
			MyExtranetContent event = myPortalService.getContentById(idEvento);
			
			StringSubstitutor substitutor = getPlaceholderSubstitutor(event);
			
			String eventDetailTemplate = 
					myConfigService.getConcatValue(ipa + eventDetailTemplateKey, "");
			
			String eventDetailStr = substitutor.replace(eventDetailTemplate);
			
			return eventDetailStr;
		}
		return "";
	}
	
	private StringSubstitutor getPlaceholderSubstitutor(MyExtranetContent event) {
		String tipoEvento = event.getTipo();
		
		String myPortalContentContextUrl = 	mySysConfigService.getConcatValue(ipa + Constants.MYSYSCONFIG_KEY_MYPORTALDOMAIN, "") +
				mySysConfigService.getConcatValue(ipa + Constants.MYSYSCONFIG_KEY_MYPORTALCONTEXT, "");
		
		String href = StringUtils.hasText(event.getSlug()) ?
				String.format("%sdettaglio/contenuto/%s", myPortalContentContextUrl, event.getSlug()) :
					String.format("%sdettaglio?contentId=%s&type=%s", myPortalContentContextUrl, event.getId(), tipoEvento);

		
		Map<String, String> valuesMap = new HashMap();
		valuesMap.putAll(Map.of(
                "evento.titolo", it.regioneveneto.myp3.myextranet.utils.StringUtils.stringOrEmpty(event.getTitolo()),
                "evento.aula", it.regioneveneto.myp3.myextranet.utils.StringUtils.stringOrEmpty(event.getAula()),
                "evento.descrizione", it.regioneveneto.myp3.myextranet.utils.StringUtils.stringOrEmpty(event.getDescrizione()),
                "evento.luogo", it.regioneveneto.myp3.myextranet.utils.StringUtils.stringOrEmpty(event.getLuogo()),
                "evento.sottotitolo", it.regioneveneto.myp3.myextranet.utils.StringUtils.stringOrEmpty(event.getSottotitolo()),
                "evento.link", it.regioneveneto.myp3.myextranet.utils.StringUtils.stringOrEmpty(href)
        ));
		
		return new StringSubstitutor(valuesMap);
	}
	
	@Override
	public String getTemplate(String templateFileName) throws FileNotFoundException {
		InputStream in = getClass().getResourceAsStream("/template/" + templateFileName); 

		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String template = (String)reader.lines().collect(Collectors.joining(System.lineSeparator()));
		
		return template;
	}

}
