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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.naming.OperationNotSupportedException;
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
import org.springframework.util.StringUtils;

import it.regioneveneto.myp3.mychannel.client.model.Attachment;
import it.regioneveneto.myp3.myextranet.bean.IStatEventiRow;
import it.regioneveneto.myp3.myextranet.bean.IStatPartecipantiRow;
import it.regioneveneto.myp3.myextranet.bean.StatEventiRow;
import it.regioneveneto.myp3.myextranet.bean.StatPartecipantiRow;
import it.regioneveneto.myp3.myextranet.exception.MyExtranetException;
import it.regioneveneto.myp3.myextranet.model.AuditModel;
import it.regioneveneto.myp3.myextranet.model.Ente;
import it.regioneveneto.myp3.myextranet.model.IscrittoEvento;
import it.regioneveneto.myp3.myextranet.model.Messaggio;
import it.regioneveneto.myp3.myextranet.model.MyExtranetContent;
import it.regioneveneto.myp3.myextranet.model.Utente;
import it.regioneveneto.myp3.myextranet.report.AttestatoPDFReport;
import it.regioneveneto.myp3.myextranet.report.FoglioPresenzePDFReport;
import it.regioneveneto.myp3.myextranet.repository.IscrittoEventoRepository;
import it.regioneveneto.myp3.myextranet.repository.UtenteRepository;
import it.regioneveneto.myp3.myextranet.security.UserWithAdditionalInfo;
import it.regioneveneto.myp3.myextranet.service.IscrittoEventoService;
import it.regioneveneto.myp3.myextranet.service.MessaggioService;
import it.regioneveneto.myp3.myextranet.service.MyConfigService;
import it.regioneveneto.myp3.myextranet.service.MyPortalService;
import it.regioneveneto.myp3.myextranet.service.UtenteMessaggiService;
import it.regioneveneto.myp3.myextranet.service.UtenteService;
import it.regioneveneto.myp3.myextranet.utils.AuditUtils;
import it.regioneveneto.myp3.myextranet.utils.Constants;
import it.regioneveneto.myp3.myextranet.utils.DateUtils;
import it.regioneveneto.myp3.myextranet.utils.ObjectMapperUtils;
import it.regioneveneto.myp3.myextranet.utils.RepositoryUtils;
import it.regioneveneto.myp3.myextranet.web.dto.DettaglioIscrizioneDTO;
import it.regioneveneto.myp3.myextranet.web.dto.EnteDTO;
import it.regioneveneto.myp3.myextranet.web.dto.IscrittoEventoDTO;
import it.regioneveneto.myp3.myextranet.web.dto.IscrittoEventoDettaglioFilterDTO;
import it.regioneveneto.myp3.myextranet.web.dto.IscrittoEventoFilterDTO;
import it.regioneveneto.myp3.myextranet.web.dto.MessaggioDTO;
import it.regioneveneto.myp3.myextranet.web.dto.OperazioneMassivaDTO;
import it.regioneveneto.myp3.myextranet.web.dto.OperazioneMassivaDTO.Target;
import it.regioneveneto.myp3.myextranet.web.dto.PagedData;
import it.regioneveneto.myp3.myextranet.web.dto.UtenteDTO;

@Service
public class IscrittoEventoServiceImpl implements IscrittoEventoService {
    private static final Logger LOG = LoggerFactory.getLogger(IscrittoEventoServiceImpl.class);

    @Autowired
    IscrittoEventoRepository iscrittoEventoRepository;
    
    @Autowired
    UtenteRepository utenteRepository;
    
    @Autowired
    UtenteService utenteService;
    
    @Autowired
    FoglioPresenzePDFReport foglioPresenzeReportGenerator;
    
    @Autowired
    AttestatoPDFReport attestatoReportGenerator;
    
    @Autowired
    MyPortalService myPortalService;
    
    @Autowired
    MessaggioService messaggioService;
    
    @Autowired
    UtenteMessaggiService utenteMessaggiService;
    
    @Autowired
    MyConfigService myConfigService;
    
    private final int progressUnitMultiplier;
    private final String ipa;
    
    private final String invioAttestatoEmailTitleKey;
    private final String invioAttestatoEmailBodyTemplateFileKey;
    private final String nuovoIscrittoEmailTitleKey;
    private final String nuovoIscrittoEmailBodyTemplateFileKey;
    private final String annullamentoIscrittoEmailTitleKey;
    private final String annullamentoIscrittoEmailBodyTemplateFileKey;
    private final String richiestaQuestionarioEmailTitleKey;
    private final String richiestaQuestionarioEmailBodyTemplateFileKey;
    
    @Override
	public int getProgressUnitMultiplier() {
		return progressUnitMultiplier;
	}

	public IscrittoEventoServiceImpl(
			@Value("${myportal.ipa}") String ipa,
			@Value("${myintranet.progress.unitMultiplier}") int progressUnitMultiplier,
			@Value("${mychannel.messages.invioAttestato.myConfigKeys.emailTitle}") String invioAttestatoEmailTitleKey,
			@Value("${mychannel.messages.invioAttestato.myConfigKeys.emailBodyTemplateFile}") String invioAttestatoEmailBodyTemplateFileKey,
			@Value("${mychannel.messages.nuovoIscritto.myConfigKeys.emailTitle}") String nuovoIscrittoEmailTitleKey,
			@Value("${mychannel.messages.nuovoIscritto.myConfigKeys.emailBodyTemplateFile}") String nuovoIscrittoEmailBodyTemplateFileKey,
			@Value("${mychannel.messages.annullamentoIscritto.myConfigKeys.emailTitle}") String annullamentoIscrittoEmailTitleKey,
			@Value("${mychannel.messages.annullamentoIscritto.myConfigKeys.emailBodyTemplateFile}") String annullamentoIscrittoEmailBodyTemplateFileKey,
			@Value("${mychannel.messages.richiestaQuestionario.myConfigKeys.emailTitle}") String richiestaQuestionarioEmailTitleKey,
			@Value("${mychannel.messages.richiestaQuestionario.myConfigKeys.emailBodyTemplateFile}") String richiestaQuestionarioEmailBodyTemplateFileKey
			) {
		super();
		this.ipa = ipa;
		this.progressUnitMultiplier = progressUnitMultiplier;
		this.invioAttestatoEmailTitleKey = invioAttestatoEmailTitleKey;
		this.invioAttestatoEmailBodyTemplateFileKey = invioAttestatoEmailBodyTemplateFileKey;
		this.nuovoIscrittoEmailTitleKey = nuovoIscrittoEmailTitleKey;
		this.nuovoIscrittoEmailBodyTemplateFileKey = nuovoIscrittoEmailBodyTemplateFileKey;
		this.annullamentoIscrittoEmailTitleKey = annullamentoIscrittoEmailTitleKey;
		this.annullamentoIscrittoEmailBodyTemplateFileKey = annullamentoIscrittoEmailBodyTemplateFileKey;
		this.richiestaQuestionarioEmailTitleKey = richiestaQuestionarioEmailTitleKey;
		this.richiestaQuestionarioEmailBodyTemplateFileKey = richiestaQuestionarioEmailBodyTemplateFileKey;
	}

	@Override
	@Transactional(rollbackFor = Exception.class, readOnly = true)
	public PagedData<IscrittoEventoDTO> getIscrittiEvento(IscrittoEventoFilterDTO iscrittoEventoFilter, int pageNumber,
			int pageSize, String sortProperty, String sortDirection) {
		if (sortProperty == null) sortProperty = "dtIscrizione";
		if (sortDirection == null) sortDirection = "DESC";
		
		Pageable pageable = PagedData.buildPageable(pageNumber, pageSize, sortProperty, sortDirection);
		
		Page<IscrittoEvento> page = iscrittoEventoRepository.findAll(RepositoryUtils.buildIscrittoEventoFilterSpecification(iscrittoEventoFilter), pageable);
		
		for( IscrittoEvento iscritto : page.getContent()) {
			populateUserData(iscritto);
		}
		
		PagedData<IscrittoEventoDTO> pagedData = new PagedData<IscrittoEventoDTO>(page, IscrittoEventoDTO.class);
		return pagedData;

	}
	
	@Override
	@Transactional(rollbackFor = Exception.class, readOnly = true)
	public IscrittoEventoDTO getIscrittoEvento(Integer iscrittoId) throws Exception {
								
		Optional<IscrittoEvento> iscritto = iscrittoEventoRepository.findById(iscrittoId);

		if(iscritto.isPresent()) {
			
			IscrittoEvento iscrittoEvento = iscritto.get();
			populateUserData(iscrittoEvento);
			
			IscrittoEventoDTO iscrittoData =ObjectMapperUtils.map(iscritto.get(), IscrittoEventoDTO.class);
			return iscrittoData;
		} else {
			throw new Exception("Iscritto not found");
		}

	}

	@Override
	@Transactional(rollbackFor = Exception.class, readOnly = false)
	public IscrittoEventoDTO insertIscrittoEvento(@Valid IscrittoEventoDTO newIscrittoEvento) throws Exception {
		
		IscrittoEvento iscritto = ObjectMapperUtils.map(newIscrittoEvento, IscrittoEvento.class);
		
		// add audit fields
		AuditModel audit = (AuditModel) iscritto;
		AuditUtils.fillAudit(audit);
		
		fillDefaults(iscritto);

		// check if azienda and ente are both provided
		if (StringUtils.hasText(iscritto.getAzienda()) && iscritto.getEnte() != null) {
			throw new Exception("Specificare uno solo tra azienda ed ente");
		}
		
		// check if there is another record for the same utente already
		checkForExistingRecordForUtente(iscritto);
		
		IscrittoEvento savedIscritto = iscrittoEventoRepository.save(iscritto);
		
		// send email
		sendEmailNewIscritto(savedIscritto.getIdIscritto());
		
		return ObjectMapperUtils.map(savedIscritto, IscrittoEventoDTO.class);
	}
	
	private void checkForExistingRecordForUtente(IscrittoEvento iscritto) throws Exception {
		Utente utente = iscritto.getUtente();
		if (utente == null) {
			return;
		}
		
		List<IscrittoEvento> records = iscrittoEventoRepository.findAllByIdEventoAndUtente(iscritto.getIdEvento(), iscritto.getUtente());
		if (records.size() > 0) {
			throw new Exception("L'utente risulta già iscritto all'evento");
		}
	}

	private void fillDefaults(IscrittoEvento iscritto) {
		LocalDate today = LocalDate.now();
		iscritto.setDtIscrizione(today);
		
		if (iscritto.getFlgPartecipLoco() == null) {
			iscritto.setFlgPartecipLoco(0);
		}
		if (iscritto.getFlgPartecipRemoto() == null) {
			iscritto.setFlgPartecipRemoto(0);
		}
		if (iscritto.getFlgRelatore() == null) {
			iscritto.setFlgRelatore(0);
		}
	}

	private void populateFromUtente(IscrittoEvento iscritto) { 
		
		if(iscritto.getUtente() != null && iscritto.getUtente().getIdUtente() != null ) {
			Integer idUtente = iscritto.getUtente().getIdUtente();
			Optional<Utente> utenteOptional = utenteRepository.findById(idUtente);
			
			if (utenteOptional.isPresent()) {
				final Utente utente = utenteOptional.get();
				
				iscritto.setNome(utente.getNome());
				iscritto.setCognome(utente.getCognome());
				iscritto.setEmail(utente.getEmail());
				if (utente.getEnte() != null) {
					Ente e = new Ente();
					Ente ente = utente.getEnte();
					e.setIdEnte(ente.getIdEnte());
					e.setDenominazione(ente.getDenominazione());
					iscritto.setEnte(e);
				}
			}
		}
	}
	
	private void populateFromUtenteDTO(IscrittoEventoDTO iscritto) { 
		if(iscritto.getUtente() != null && iscritto.getUtente().getIdUtente() != null ) {
			Integer idUtente = iscritto.getUtente().getIdUtente();
			
			Optional<Utente> utenteOptional = utenteRepository.findById(idUtente);
			
			if (utenteOptional.isPresent()) {
				final Utente utente = utenteOptional.get();
				
				iscritto.setNome(utente.getNome());
				iscritto.setCognome(utente.getCognome());
				iscritto.setEmail(utente.getEmail());
				if (utente.getEnte() != null) {
					Ente e = new Ente();
					Ente ente = utente.getEnte();
					e.setIdEnte(ente.getIdEnte());
					e.setDenominazione(ente.getDenominazione());
					EnteDTO enteDto = ObjectMapperUtils.map(ente, EnteDTO.class);
					iscritto.setEnte(enteDto);
				}
			}
		}
	}
	
	private void populateUserData(IscrittoEvento iscrittoEvento) { 
		
		if(iscrittoEvento.getUtente() != null ) {
			
			Utente utente = iscrittoEvento.getUtente();
			
			iscrittoEvento.setNome(utente.getNome());
			iscrittoEvento.setCognome(utente.getCognome());
			iscrittoEvento.setEmail(utente.getEmail());
			iscrittoEvento.setAzienda(utente.getAzienda());
			
			if (utente.getEnte() != null) {	
				Ente e = new Ente();
				Ente ente = utente.getEnte();
				e.setIdEnte(ente.getIdEnte());
				e.setDenominazione(ente.getDenominazione());
				iscrittoEvento.setEnte(e);
			}
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class, readOnly = false)
	public IscrittoEventoDTO updateIscrittoEvento(IscrittoEventoDTO patchIscrittoEvento, boolean isPatch) throws Exception {
		
		// retrieve original
		Optional<IscrittoEvento> originalIscrittoEventoOptional = iscrittoEventoRepository.findById(patchIscrittoEvento.getIdIscritto());
		if (!originalIscrittoEventoOptional.isPresent()) {
			throw new Exception("Iscritto Evento not found");
		}
		IscrittoEvento originalIscrittoEvento = originalIscrittoEventoOptional.get();

		
		IscrittoEvento iscritto = IscrittoEventoDTO.patchIscrittoEvento(patchIscrittoEvento, originalIscrittoEvento, isPatch, true);
		
		// set audit fields
		AuditModel audit = (AuditModel) iscritto;
		AuditUtils.fillAudit(audit);
		
		IscrittoEvento savedIscrittoEvento = iscrittoEventoRepository.save(iscritto);
		
		return ObjectMapperUtils.map(savedIscrittoEvento, IscrittoEventoDTO.class);

	}
	
	@Override
	@Transactional(rollbackFor = Exception.class, readOnly = false)
	public IscrittoEventoDTO deleteIscrittoEvento(Integer iscrittoId) throws Exception {
		// retrieve original
		Optional<IscrittoEvento> originalIscrittoEventoOptional = iscrittoEventoRepository.findById(iscrittoId);
		if (!originalIscrittoEventoOptional.isPresent()) {
			throw new Exception("Iscritto Evento not found");
		}
		IscrittoEvento originalIscrittoEvento = originalIscrittoEventoOptional.get();
		IscrittoEventoDTO iscrittoEvento = ObjectMapperUtils.map(originalIscrittoEvento, IscrittoEventoDTO.class);
		
		iscrittoEventoRepository.delete(originalIscrittoEvento);
				
		// send email
		sendEmailDeleteIscritto(iscrittoEvento);
		
		return iscrittoEvento;
	}
	
	private Messaggio sendEmailDeleteIscritto(IscrittoEventoDTO iscritto) throws Exception {
				
		MyExtranetContent event = myPortalService.getContentById(iscritto.getIdEvento());
		
		populateFromUtenteDTO(iscritto);
		
		Messaggio messaggio = new Messaggio();
		messaggio.setArea(Constants.MESSAGGIO_AREA_EVENTO);
		messaggio.setTipo(Constants.MESSAGGIO_TIPO_COMUNICAZIONE);
		messaggio.setDestinatario(String.format("%s %s", iscritto.getNome(), iscritto.getCognome()));
		messaggio.setIdContenuto(iscritto.getIdEvento());
		messaggio.setIndirizzo(iscritto.getEmail());
		
		StringSubstitutor substitutor = getPlaceholderSubstitutor(messaggio, event, null);
		
		String nuovoIscrittoEmailTitle = 
				myConfigService.getConcatValue(ipa + annullamentoIscrittoEmailTitleKey, "");
		
		messaggio.setTitolo(substitutor.replace(nuovoIscrittoEmailTitle));
		String bodyTemplate = 
				myConfigService.getConcatValue(ipa + annullamentoIscrittoEmailBodyTemplateFileKey, "");
		messaggio.setMessaggio(substitutor.replace(bodyTemplate));
				
		// insert and send messaggio
		Messaggio savedMessaggio = messaggioService.insertAndSendMessaggio(messaggio, null);
		
		// check if iscritto is an utente
		UtenteDTO utente = iscritto.getUtente();
		
		if (utente != null) {
			// link utente with messaggio
			Integer idMessaggio = savedMessaggio.getIdMessaggio();
			Integer[] utenteIds = { utente.getIdUtente() };
			
			utenteMessaggiService.linkUtentiWithMessaggio(utenteIds, idMessaggio);
		}
		
		return savedMessaggio;
	}

	@Override
	@Transactional(rollbackFor = Exception.class, readOnly = false)
	public MessaggioDTO sendMessaggioToIscritti(MessaggioDTO messaggio) throws Exception {
		// get list of iscritti
		List<IscrittoEvento> iscritti = iscrittoEventoRepository.findAllByIdEventoOrderByCognomeAscNomeAsc(messaggio.getIdContenuto());
		
		// concatenate email addresses
		String[] indirizzi = iscritti.stream()
				.map( i-> {
					populateUserData(i);
					return i;
				})
				.map(i -> i.getEmail()).toArray(String[]::new);
		
		String indirizzo = String.join(";", indirizzi);
		messaggio.setIndirizzo(indirizzo);
		
		// insert and send messaggio
		MessaggioDTO savedMessaggio = messaggioService.insertAndSendMessaggio(messaggio);
		
		// link utenti with messaggio
		Integer idMessaggio = savedMessaggio.getIdMessaggio();
		
		Integer[] iscrittiThatAreUtentiIds = iscritti.stream()
				.filter(i -> i.getUtente() != null)
				.map(i -> i.getUtente().getIdUtente()).toArray(Integer[]::new);
		
		utenteMessaggiService.linkUtentiWithMessaggio(iscrittiThatAreUtentiIds, idMessaggio);
		
		return savedMessaggio;
	}
	
	
	@Override
	@Transactional(rollbackFor = Exception.class, readOnly = false)
	public void executeOperationOnIscrittiEvento(@Valid OperazioneMassivaDTO operazione, OutputStream outputStream) throws Exception {
		
		LOG.debug(String.format("Requested operation: %s (with progress)", operazione.getTipoOperazione()));
		
		switch (operazione.getTipoOperazione()) {		

		case Constants.OPERAZIONE_ISCRITTOEVENTO_INVIA_ATTESTATI:
			inviaAttestati(operazione, outputStream);
			break;

		default:
			LOG.error(String.format("executeOperationOnIscrittiEvento with progress: operation %s not supported", operazione.getTipoOperazione()));
			try {
				outputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}

	@Override
	@Transactional(rollbackFor = Exception.class, readOnly = false)
	public void executeOperationOnIscrittiEvento(@Valid OperazioneMassivaDTO operazione) throws Exception {
		
		LOG.debug(String.format("Requested operation: %s", operazione.getTipoOperazione()));
		
		switch (operazione.getTipoOperazione()) {
		case Constants.OPERAZIONE_ISCRITTOEVENTO_IMPOSTA_PRESENZE:
			impostaPresenze(operazione);
			break;
			
		case Constants.OPERAZIONE_ISCRITTOEVENTO_IMPOSTA_RELATORE:
			impostaRelatore(operazione);
			break;			

		case Constants.OPERAZIONE_ISCRITTOEVENTO_INVIA_ATTESTATI:
			inviaAttestati(operazione);
			break;

		case Constants.OPERAZIONE_ISCRITTOEVENTO_INVIA_RICHIESTA_COMPILAZIONE_QUESTIONARIO:
			inviaRichiesteQuestionario(operazione);
			break;

		default:
			throw new OperationNotSupportedException("Operation non supported");
		}
		
	}
	
	private void inviaRichiesteQuestionario(OperazioneMassivaDTO operazione) throws Exception {
		Target[] targets = operazione.getTargets();
		
		for (Target target : targets) {
			inviaRichiestaQuestionario(target.getId());
		}
	}
	
	private void inviaRichiestaQuestionario(Integer idIscritto) throws Exception {
		
		try {
			sendRichiestaQuestionario(idIscritto);			
			iscrittoEventoRepository.setDtRichQuestionario(LocalDate.now(), idIscritto);
		} catch (Exception e) {
			LOG.error("Error while sending survey invitation", e);
			throw e;
		}
	}
	
	private void inviaAttestati(OperazioneMassivaDTO operazione) throws Exception {
		Target[] targets = operazione.getTargets();
		
		for (Target target : targets) {
			// send document
			inviaAttestato(target.getId());
		}
	
	}
	
	private void inviaAttestati(OperazioneMassivaDTO operazione, OutputStream outputStream) throws Exception {
		Target[] targets = operazione.getTargets();
		
		// final int TOTALBYTES = 100 * progressUnitMultiplier;
		int numTargets = targets.length;
		double increment = 100.0 / numTargets;
		int intProgress = 0;
		double doubleProgress = 0.0;
		
		for (Target target : targets) {
			// send document
			inviaAttestato(target.getId());
			
			// send progress to client
			doubleProgress += increment;
			double realDiff = doubleProgress - intProgress;
			int intDiff = (int) Math.floor(realDiff);
			sendProgress(outputStream, intDiff * progressUnitMultiplier);
			intProgress += intDiff;
		}
		
		// send any rest
		int intDiff = 100 - intProgress;
		sendProgress(outputStream, intDiff * progressUnitMultiplier);
	}
	
	private void sendProgress(OutputStream outputStream, int numBytes) {
		byte[] ba = new byte[numBytes];
		Arrays.fill(ba, "*".getBytes()[0]);
		try {
			outputStream.write(ba);
			outputStream.flush();
		} catch (IOException e) {
			LOG.error("Error while sending progress", e);
		}
	}

	private void inviaAttestato(Integer idIscritto) throws Exception {
		try {
			ByteArrayInputStream bis = generatePDFReportAttestato(idIscritto);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			
			copyInputStreamToOutputStream(bis, baos);
			byte[] attachmentContent = baos.toByteArray();
			
			Attachment attachment = new Attachment();
			attachment.setContent(attachmentContent);
			attachment.setMimeType("application/pdf");
			attachment.setName("Attestato.pdf");
			
			sendAttestato(idIscritto, attachment);
			
			// set dtInvioAttestato
			iscrittoEventoRepository.setDtInvioAttestato(LocalDate.now(), idIscritto);
		} catch (Exception e) {
			LOG.error("Error while generating and sending document", e);
			throw e;
		}
	}
	
	private void copyInputStreamToOutputStream(InputStream source, OutputStream target) throws IOException {
	    byte[] buf = new byte[8192];
	    int length;
	    while ((length = source.read(buf)) > 0) {
	        target.write(buf, 0, length);
	    }
	}
	
	private Messaggio sendRichiestaQuestionario(Integer idIscritto) throws Exception {
		
		IscrittoEvento iscritto = iscrittoEventoRepository.getOne(idIscritto);
		
		populateUserData(iscritto);
		
		MyExtranetContent event = myPortalService.getContentById(iscritto.getIdEvento());
		
		Messaggio messaggio = new Messaggio();
		messaggio.setArea(Constants.MESSAGGIO_AREA_EVENTO);
		messaggio.setTipo(Constants.MESSAGGIO_TIPO_COMUNICAZIONE);
		messaggio.setDestinatario(String.format("%s %s", iscritto.getNome(), iscritto.getCognome()));
		messaggio.setIdContenuto(iscritto.getIdEvento());
		messaggio.setIndirizzo(iscritto.getEmail());
		
		// link to survey
		String linkUrl = event.getUrlQuestionario();
		String link = String.format("<p/><a href=\"%s\">%s</a>", linkUrl, "Vai al questionario");
		
		Map<String, String> additionalValuesMap = Map.of(
                "questionario.link", link
        );
		StringSubstitutor substitutor = getPlaceholderSubstitutor(messaggio, event, additionalValuesMap);
		
		String richiestaQuestionarioEmailTitle = 
				myConfigService.getConcatValue(ipa + richiestaQuestionarioEmailTitleKey, "");
				
		
		messaggio.setTitolo(substitutor.replace(richiestaQuestionarioEmailTitle));
		String bodyTemplate = 
				myConfigService.getConcatValue(ipa + richiestaQuestionarioEmailBodyTemplateFileKey, "");
		messaggio.setMessaggio(substitutor.replace(bodyTemplate));
				
		// insert and send messaggio
		Messaggio savedMessaggio = messaggioService.insertAndSendMessaggio(messaggio, null);
		
		// check if iscritto is an utente
		Utente utente = iscritto.getUtente();
		
		if (utente != null) {
			// link utente with messaggio
			Integer idMessaggio = savedMessaggio.getIdMessaggio();
			Integer[] utenteIds = { utente.getIdUtente() };
			
			utenteMessaggiService.linkUtentiWithMessaggio(utenteIds, idMessaggio);
		}
		
		return savedMessaggio;
	}
	
	
	private Messaggio sendAttestato(Integer idIscritto, Attachment attachment) throws Exception {

		Attachment[] attachments = { attachment };
		
		IscrittoEvento iscritto = iscrittoEventoRepository.getOne(idIscritto);
		
		populateUserData(iscritto);
		
		MyExtranetContent event = myPortalService.getContentById(iscritto.getIdEvento());
		
		Messaggio messaggio = new Messaggio();
		messaggio.setArea(Constants.MESSAGGIO_AREA_EVENTO);
		messaggio.setTipo(Constants.MESSAGGIO_TIPO_COMUNICAZIONE);
		messaggio.setDestinatario(String.format("%s %s", iscritto.getNome(), iscritto.getCognome()));
		messaggio.setIdContenuto(iscritto.getIdEvento());
		messaggio.setIndirizzo(iscritto.getEmail());
		
		StringSubstitutor substitutor = getPlaceholderSubstitutor(messaggio, event, null);
		
		String invioAttestatoEmailTitle = 
				myConfigService.getConcatValue(ipa + invioAttestatoEmailTitleKey, "");
		
		messaggio.setTitolo(substitutor.replace(invioAttestatoEmailTitle));

		String bodyTemplate = 
				myConfigService.getConcatValue(ipa + invioAttestatoEmailBodyTemplateFileKey, "");
		messaggio.setMessaggio(substitutor.replace(bodyTemplate));
		
		// insert and send messaggio
		Messaggio savedMessaggio = messaggioService.insertAndSendMessaggio(messaggio, attachments);
		
		// check if iscritto is an utente
		Utente utente = iscritto.getUtente();
		
		if (utente != null) {
			// link utente with messaggio
			Integer idMessaggio = savedMessaggio.getIdMessaggio();
			Integer[] utenteIds = { utente.getIdUtente() };
			
			utenteMessaggiService.linkUtentiWithMessaggio(utenteIds, idMessaggio);
		}
		
		return savedMessaggio;
	}
	
	private StringSubstitutor getPlaceholderSubstitutor(Messaggio messaggio, MyExtranetContent event, Map<String, String> customValuesMap) {
		
		Map<String, String> valuesMap = new HashMap();
		valuesMap.putAll(Map.of(
                "evento.titolo", it.regioneveneto.myp3.myextranet.utils.StringUtils.stringOrEmpty(event.getTitolo()),
                "evento.aula", it.regioneveneto.myp3.myextranet.utils.StringUtils.stringOrEmpty(event.getAula()),
                "evento.descrizione", it.regioneveneto.myp3.myextranet.utils.StringUtils.stringOrEmpty(event.getDescrizione()),
                "evento.luogo", it.regioneveneto.myp3.myextranet.utils.StringUtils.stringOrEmpty(event.getLuogo()),
                "evento.sottotitolo", it.regioneveneto.myp3.myextranet.utils.StringUtils.stringOrEmpty(event.getSottotitolo()),
                "evento.dettaglio", messaggioService.getEventDetailString(event.getId()),
                "messaggio.destinatario", it.regioneveneto.myp3.myextranet.utils.StringUtils.stringOrEmpty(messaggio.getDestinatario())
        ));
		
		if (customValuesMap != null) {
			valuesMap.putAll(customValuesMap);
		}
		
		return new StringSubstitutor(valuesMap);
	}

	private Messaggio sendEmailNewIscritto(Integer idIscritto) throws Exception {
		
		IscrittoEvento iscritto = iscrittoEventoRepository.getOne(idIscritto); //populateUserData
		
		MyExtranetContent event = myPortalService.getContentById(iscritto.getIdEvento());
		
		populateFromUtente(iscritto);
		
		Messaggio messaggio = new Messaggio();
		messaggio.setArea(Constants.MESSAGGIO_AREA_EVENTO);
		messaggio.setTipo(Constants.MESSAGGIO_TIPO_COMUNICAZIONE);
		messaggio.setDestinatario(String.format("%s %s", iscritto.getNome(), iscritto.getCognome()));
		messaggio.setIdContenuto(iscritto.getIdEvento());
		messaggio.setIndirizzo(iscritto.getEmail());
		
		StringSubstitutor substitutor = getPlaceholderSubstitutor(messaggio, event, null);
		
		String nuovoIscrittoEmailTitle = 
				myConfigService.getConcatValue(ipa + nuovoIscrittoEmailTitleKey, "");
		
		messaggio.setTitolo(substitutor.replace(nuovoIscrittoEmailTitle));
		String bodyTemplate = 
				myConfigService.getConcatValue(ipa + nuovoIscrittoEmailBodyTemplateFileKey, "");
		messaggio.setMessaggio(substitutor.replace(bodyTemplate));
				
		// insert and send messaggio
		Messaggio savedMessaggio = messaggioService.insertAndSendMessaggio(messaggio, null);
		
		// check if iscritto is an utente
		Utente utente = iscritto.getUtente();
		
		if (utente != null) {
			// link utente with messaggio
			Integer idMessaggio = savedMessaggio.getIdMessaggio();
			Integer[] utenteIds = { utente.getIdUtente() };
			
			utenteMessaggiService.linkUtentiWithMessaggio(utenteIds, idMessaggio);
		}
		
		return savedMessaggio;
	}

	
	private void impostaPresenze(OperazioneMassivaDTO operazione) {
		
		Target[] targets = operazione.getTargets();
		
		List<Integer> toTurnOff = new ArrayList<Integer>();
		List<Integer> toTurnOn = new ArrayList<Integer>();
		for (Target target : targets) {
			if (target.getIntValue() == 0) {
				toTurnOff.add(target.getId());
			}
			if (target.getIntValue() == 1) {
				toTurnOn.add(target.getId());
			}
		}
		
		iscrittoEventoRepository.setFlgPartecipLocoForIn(0, toTurnOff.toArray(Integer[]::new));
		iscrittoEventoRepository.setFlgPartecipLocoForIn(1, toTurnOn.toArray(Integer[]::new));
	}
	
	private void impostaRelatore(OperazioneMassivaDTO operazione) {
		
		Target[] targets = operazione.getTargets();
		
		List<Integer> toTurnOff = new ArrayList<Integer>();
		List<Integer> toTurnOn = new ArrayList<Integer>();
		for (Target target : targets) {
			if (target.getIntValue() == 0) {
				toTurnOff.add(target.getId());
			}
			if (target.getIntValue() == 1) {
				toTurnOn.add(target.getId());
			}
		}
		
		iscrittoEventoRepository.setFlgRelatoreForIn(0, toTurnOff.toArray(Integer[]::new));
		iscrittoEventoRepository.setFlgRelatoreForIn(1, toTurnOn.toArray(Integer[]::new));
	}

	@Override
	@Transactional(rollbackFor = Exception.class, readOnly = true)
	public ByteArrayInputStream generateReportFoglioPresenze(String idEvento) {
		// get list of attendees
		List<IscrittoEvento> iscritti = iscrittoEventoRepository.findAllByIdEventoOrderByCognomeAscNomeAsc(idEvento);
		
		for( IscrittoEvento iscritto : iscritti) {
			populateUserData(iscritto);
		}
		
		// defaults for testing
		String title = "";
		String subtitle = "";
		
		// get event details
		try {
			MyExtranetContent event = myPortalService.getContentById(idEvento);
			if (event != null) {
				title = event.getTitolo();
				String luogo = event.getLuogo();
				String dataDa = event.getDateDa();
				String dataA = event.getDateA();
				// format date
				String data = DateUtils.formatDateRange(dataDa, dataA, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", true);
				String [] subtitleTokens = {luogo, data};
				subtitle = it.regioneveneto.myp3.myextranet.utils.StringUtils.concatenateNotNulls(subtitleTokens, null);
			}
			
		} catch (Exception e) {
			LOG.error("MyPortal service is not available, using test values", e);
		}
		
				
		return foglioPresenzeReportGenerator.generateReport(title, subtitle, iscritti);
		
	}

	@Override
	@Transactional(rollbackFor = Exception.class, readOnly = true)
	public ByteArrayInputStream generateReportAttestato(Integer idIscritto) throws Exception {
		return generatePDFReportAttestato(idIscritto);
	}
	
	@Override
	public ByteArrayInputStream generatePDFReportAttestato(Integer idIscritto) throws Exception {
		// get attendee
		Optional<IscrittoEvento> iscrittoOptional = iscrittoEventoRepository.findById(idIscritto);
		if (!iscrittoOptional.isPresent()) {
			throw new Exception("Iscritto Evento not found");
		}
		
		IscrittoEvento iscritto = iscrittoOptional.get();
		
		populateUserData(iscritto);

		String personName = String.format("%s %s", iscritto.getNome(), iscritto.getCognome());
		
		String idEvento = iscritto.getIdEvento();
		
		// defaults for testing
		String title = "";
		String textWhat = "";
		String textWhen = "";
		String textWhere = "";
		
		// get event details
		try {
			MyExtranetContent event = myPortalService.getContentById(idEvento);
			if (event != null) {
				title = event.getTitolo();
				
				// String organizzatore = event.get
				textWhat = String.format("ha partecipato a%s organizzato da%s - %s", 
						"ll'Incontro di Approfondimento tra Pari",
						"l Cluster per la Società dell'Informazione",
						"Sezione Sistemi Informativi della Regione del Veneto"
						);
				String dataDa = event.getDateDa();
				String dataA = event.getDateA();
				String data = DateUtils.formatDateRange(dataDa, dataA, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", false);
				textWhen = data;
				String aula = event.getAula();
				String luogo = event.getLuogo();
				
				String[] aulaLuogoTokens = {aula, luogo};
				String aulaLuogo = it.regioneveneto.myp3.myextranet.utils.StringUtils.concatenateNotNulls(aulaLuogoTokens,  null);
				
				textWhere = StringUtils.hasText(aulaLuogo) ? "presso " + aulaLuogo : "" ;
			}
			
		} catch (Exception e) {
			LOG.error("MyPortal service is not available, using test values", e);
		}
		
		return attestatoReportGenerator.generateReport(title, personName, textWhat, textWhen, textWhere);
	}

	@Override
	public Integer getNumIscritti(String idEvento) {
		return iscrittoEventoRepository.countByIdEvento(idEvento);
	}

	@Override
	public Integer getNumIscrittiInPresenza(String idEvento) {
		return iscrittoEventoRepository.countByIdEventoAndFlgPartecipPref(idEvento, "PRE");
	}

	@Override
	public Integer getNumPostiDisponibili(String idEvento) {
		MyExtranetContent event = myPortalService.getContentById(idEvento);
		boolean inPresenza = event.isInPresenza();
		boolean inStreaming = event.isInStreaming();
		
		Integer max = event.getNumeroMaxPartecipanti();
		
		if (max <= 0) return -1; // meaning unlimited
		
		if (inStreaming && !inPresenza) return -1;
		
		Integer numIscritti = 0;
		
		if (inPresenza && !inStreaming) {
			numIscritti = getNumIscritti(idEvento);
		}
		
		if (inPresenza && inStreaming) {
			numIscritti = getNumIscrittiInPresenza(idEvento);
		}
		
		return max - numIscritti;
	}

	@Override
	public PagedData<MyExtranetContent> getEventiIscritti(UserWithAdditionalInfo user, Integer pageNumber,
			Integer pageSize, String orderProperty, String orderDirection) {
		
		// get userId
		Map<String, Object> userValidityProperties = utenteService.getUserValidityProperties(user.getCodiceFiscale());
		Integer userId = (Integer) userValidityProperties.get("userId");
		
		String[] contentIds = iscrittoEventoRepository.getEventIdsForSubscriptions(userId);
		
		if (contentIds == null || contentIds.length == 0) {
			return new PagedData<MyExtranetContent>(new ArrayList<MyExtranetContent>(), 0, 1, pageSize);
		}
		
		PagedData<MyExtranetContent> pagedContents = myPortalService.getContentsByIds(contentIds, pageNumber, pageSize,orderProperty, orderDirection);
		
		return pagedContents;
	}

	@Override
	@Transactional(rollbackFor = Exception.class, readOnly = true)
	public DettaglioIscrizioneDTO getDettaglioIscrizione(IscrittoEventoDettaglioFilterDTO iscrittoEventoDettaglioFilter, UserWithAdditionalInfo user) throws Exception {
		Integer idIscritto = iscrittoEventoDettaglioFilter.getIdIscritto();
		
		DettaglioIscrizioneDTO dettaglio = new DettaglioIscrizioneDTO();
		
		if (idIscritto != null) {
			
			Optional<IscrittoEvento> iscritto = iscrittoEventoRepository.findById(idIscritto);

			if(iscritto.isPresent()) {
				IscrittoEvento iscrittoEvento = iscritto.get();
				populateUserData(iscrittoEvento);
				IscrittoEventoDTO iscrittoData = ObjectMapperUtils.map(iscrittoEvento, IscrittoEventoDTO.class);
				dettaglio.setIscrittoEvento(iscrittoData);
			} else {
				throw new MyExtranetException("Iscritto Evento not found");
			}
		} else {
			// search by idEvento and logged-in user
			Map<String, Object> userValidityProperties = utenteService.getUserValidityProperties(user.getCodiceFiscale());
			Integer idUtente = (Integer) userValidityProperties.get("userId");

			IscrittoEventoFilterDTO iscrittoEventoFilter = new IscrittoEventoFilterDTO();
			iscrittoEventoFilter.setIdEvento(iscrittoEventoDettaglioFilter.getIdEvento());
			iscrittoEventoFilter.setIdUtente(idUtente);
			PagedData<IscrittoEventoDTO> page = getIscrittiEvento(iscrittoEventoFilter, 1, 999, "idIscritto", "ASC");
			List<IscrittoEventoDTO> list = page.getRecords();
			if (list != null && list.size() > 0) {
				IscrittoEventoDTO iscrittoEvento = list.get(0);
				dettaglio.setIscrittoEvento(iscrittoEvento);
			} else {
				dettaglio.setIscrittoEvento(null);
			}
		}
		
		// integrate with MyCMS data
		MyExtranetContent myExtranetContent = myPortalService.getContentById(iscrittoEventoDettaglioFilter.getIdEvento());
		dettaglio.setMyExtranetContent(myExtranetContent);
		
		return dettaglio;
	}

	@Override
	@Transactional(rollbackFor = Exception.class, readOnly = true)
	public List<StatEventiRow> getStatEventiRows(List<String> eventIds) {
		
		List<IStatEventiRow> results = iscrittoEventoRepository.getStatEventiRows(eventIds);
		
		List<StatEventiRow> convertedResults = ObjectMapperUtils.mapAll(results, StatEventiRow.class);
		
		return convertedResults;
	}

	@Override
	@Transactional(rollbackFor = Exception.class, readOnly = true)
	public List<StatPartecipantiRow> getStatPartecipantiRows(List<String> eventIds) {
		List<IStatPartecipantiRow> results = iscrittoEventoRepository.getStatPartecipantiRows(eventIds);
		
		List<StatPartecipantiRow> convertedResults = ObjectMapperUtils.mapAll(results, StatPartecipantiRow.class);
		
		return convertedResults;
	}

}
