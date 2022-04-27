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

import java.sql.Date;
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
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import it.regioneveneto.myp3.myextranet.bean.CounterPerEntity;
import it.regioneveneto.myp3.myextranet.bean.ICounterPerEntity;
import it.regioneveneto.myp3.myextranet.exception.MyExtranetException;
import it.regioneveneto.myp3.myextranet.model.AuditWithValidityModel;
import it.regioneveneto.myp3.myextranet.model.CollaboratoreProgetto;
import it.regioneveneto.myp3.myextranet.model.Messaggio;
import it.regioneveneto.myp3.myextranet.model.MyExtranetContent;
import it.regioneveneto.myp3.myextranet.model.Utente;
import it.regioneveneto.myp3.myextranet.repository.CollaboratoreProgettoRepository;
import it.regioneveneto.myp3.myextranet.repository.UtenteRepository;
import it.regioneveneto.myp3.myextranet.security.UserWithAdditionalInfo;
import it.regioneveneto.myp3.myextranet.service.CollaboratoreProgettoService;
import it.regioneveneto.myp3.myextranet.service.MessaggioService;
import it.regioneveneto.myp3.myextranet.service.MyConfigService;
import it.regioneveneto.myp3.myextranet.service.MyPortalService;
import it.regioneveneto.myp3.myextranet.service.MySysConfigService;
import it.regioneveneto.myp3.myextranet.service.UtenteMessaggiService;
import it.regioneveneto.myp3.myextranet.service.UtenteService;
import it.regioneveneto.myp3.myextranet.utils.AuditUtils;
import it.regioneveneto.myp3.myextranet.utils.Constants;
import it.regioneveneto.myp3.myextranet.utils.ObjectMapperUtils;
import it.regioneveneto.myp3.myextranet.utils.RepositoryUtils;
import it.regioneveneto.myp3.myextranet.web.dto.CollaboratoreProgettoDTO;
import it.regioneveneto.myp3.myextranet.web.dto.CollaboratoreProgettoDettaglioFilterDTO;
import it.regioneveneto.myp3.myextranet.web.dto.CollaboratoreProgettoFilterDTO;
import it.regioneveneto.myp3.myextranet.web.dto.DettaglioCollaborazioneDTO;
import it.regioneveneto.myp3.myextranet.web.dto.MessaggioDTO;
import it.regioneveneto.myp3.myextranet.web.dto.OperazioneMassivaDTO;
import it.regioneveneto.myp3.myextranet.web.dto.OperazioneMassivaDTO.Target;
import it.regioneveneto.myp3.myextranet.web.dto.PagedData;

@Service
public class CollaboratoreProgettoServiceImpl implements CollaboratoreProgettoService {
    private static final Logger LOG = LoggerFactory.getLogger(CollaboratoreProgettoServiceImpl.class);

    @Autowired
    CollaboratoreProgettoRepository collaboratoreProgettoRepository;
    
    @Autowired
    UtenteRepository utenteRepository;
    
    @Autowired
    UtenteService utenteService;
        
    @Autowired
    MyPortalService myPortalService;
    
    @Autowired
    MessaggioService messaggioService;
    
    @Autowired
    UtenteMessaggiService utenteMessaggiService;
    
    @Autowired
    MyConfigService myConfigService;
    

    @Autowired
    MySysConfigService mySysConfigService;
    
    private final String ipa;
    
    private final String nuovoCollaboratoreEmailTitleKey;
    private final String nuovoCollaboratoreEmailBodyTemplateFileKey;
    private final String rifiutoCollaboratoreEmailTitleKey;
    private final String rifiutoCollaboratoreEmailBodyTemplateFileKey;
    private final String cancellazioneCollaboratoreEmailTitleKey;
    private final String cancellazioneCollaboratoreEmailBodyTemplateFileKey;
    private final String richiestaCollaborazioneEmailTitleKey;
    private final String richiestaCollaborazioneEmailBodyTemplateFileKey;
    private final String funzionarioCollaborazioniEmailAddressKey;

	public CollaboratoreProgettoServiceImpl(
			@Value("${myportal.ipa}") String ipa,
			@Value("${mychannel.messages.nuovoCollaboratore.myConfigKeys.emailTitle}") String nuovoCollaboratoreEmailTitleKey,
			@Value("${mychannel.messages.nuovoCollaboratore.myConfigKeys.emailBodyTemplateFile}") String nuovoCollaboratoreEmailBodyTemplateFileKey,
			@Value("${mychannel.messages.rifiutoCollaboratore.myConfigKeys.emailTitle}") String rifiutoCollaboratoreEmailTitleKey,
			@Value("${mychannel.messages.rifiutoCollaboratore.myConfigKeys.emailBodyTemplateFile}") String rifiutoCollaboratoreEmailBodyTemplateFileKey,
			@Value("${mychannel.messages.cancellazioneCollaboratore.myConfigKeys.emailTitle}") String cancellazioneCollaboratoreEmailTitleKey,
			@Value("${mychannel.messages.cancellazioneCollaboratore.myConfigKeys.emailBodyTemplateFile}") String cancellazioneCollaboratoreEmailBodyTemplateFileKey,
			@Value("${mychannel.messages.richiestaCollaborazione.myConfigKeys.emailTitle}") String richiestaCollaborazioneEmailTitleKey,
			@Value("${mychannel.messages.richiestaCollaborazione.myConfigKeys.emailBodyTemplateFile}") String richiestaCollaborazioneEmailBodyTemplateFileKey,
			@Value("${mychannel.addresses.funzionarioCollaborazioni.myConfigKeys.emailAddress}") String funzionarioCollaborazioniEmailAddressKey
			) {
		super();
		this.ipa = ipa;
		this.nuovoCollaboratoreEmailTitleKey = nuovoCollaboratoreEmailTitleKey;
		this.nuovoCollaboratoreEmailBodyTemplateFileKey = nuovoCollaboratoreEmailBodyTemplateFileKey;
		this.rifiutoCollaboratoreEmailTitleKey = rifiutoCollaboratoreEmailTitleKey;
		this.rifiutoCollaboratoreEmailBodyTemplateFileKey = rifiutoCollaboratoreEmailBodyTemplateFileKey;
		this.cancellazioneCollaboratoreEmailTitleKey = cancellazioneCollaboratoreEmailTitleKey;
		this.cancellazioneCollaboratoreEmailBodyTemplateFileKey = cancellazioneCollaboratoreEmailBodyTemplateFileKey;
		this.richiestaCollaborazioneEmailTitleKey = richiestaCollaborazioneEmailTitleKey;
		this.richiestaCollaborazioneEmailBodyTemplateFileKey = richiestaCollaborazioneEmailBodyTemplateFileKey;
		this.funzionarioCollaborazioniEmailAddressKey = funzionarioCollaborazioniEmailAddressKey;
	}

	@Override
	@Transactional(rollbackFor = Exception.class, readOnly = true)
	public PagedData<CollaboratoreProgettoDTO> getCollaboratoriProgetto(CollaboratoreProgettoFilterDTO collaboratoreProgettoFilter, int pageNumber,
			int pageSize, String sortProperty, String sortDirection, Integer filterType) {
		Sort sort;
		if (sortProperty == null) {
			// default sorting is as follows: flgCoord DESC, utente.cognome ASC, utente.nome ASC
			List<Sort.Order> orders = new ArrayList<Sort.Order>(3);
			orders.add(Order.desc("flgCoord"));
			orders.add(Order.asc("utente.cognome"));
			orders.add(Order.asc("utente.nome"));
			sort = Sort.by(orders);
		} else {
			sort = Sort.by(Direction.fromString(sortDirection), sortProperty);
		}
		
		Pageable pageable = PagedData.buildPageable(pageNumber, pageSize, sort);
		
		Page<CollaboratoreProgetto> page = collaboratoreProgettoRepository.findAll(RepositoryUtils.buildCollaboratoreProgettoFilterSpecification(collaboratoreProgettoFilter, filterType), pageable);

		PagedData<CollaboratoreProgettoDTO> pagedData = new PagedData<CollaboratoreProgettoDTO>(page, CollaboratoreProgettoDTO.class);
		
		computeValidityFlag(pagedData);
		
		return pagedData;

	}
	
	private void computeValidityFlag(PagedData<CollaboratoreProgettoDTO> pagedData) {
		
		for (CollaboratoreProgettoDTO coll : pagedData.getRecords()) {
			setCollabValidityFlag(coll);
		}
		
	}
	
	private void setCollabValidityFlag(CollaboratoreProgettoDTO coll) {
		Date today = Date.valueOf(LocalDate.now());
		boolean isValid = coll.getDtInizioVal() != null && coll.getDtFineVal() != null &&
	
					coll.getDtInizioVal().compareTo(today) <= 0 &&
					coll.getDtFineVal().compareTo(today) > 0;
			
			coll.setValid(isValid);
	}
	

	private PagedData<CollaboratoreProgettoDTO> getCollaboratoriProgettoWithSort(CollaboratoreProgettoFilterDTO collaboratoreProgettoFilter, int pageNumber,
			int pageSize, Sort sort) {
		
		Pageable pageable = PagedData.buildPageable(pageNumber, pageSize, sort);
		
		Page<CollaboratoreProgetto> page = collaboratoreProgettoRepository.findAll(RepositoryUtils.buildCollaboratoreProgettoFilterSpecification(collaboratoreProgettoFilter, Constants.GET_COLLABORATORI_PROGETTO_FILTER_TYPE_ALL), pageable);

		PagedData<CollaboratoreProgettoDTO> pagedData = new PagedData<CollaboratoreProgettoDTO>(page, CollaboratoreProgettoDTO.class);
		return pagedData;

	}
	
	@Override
	@Transactional(rollbackFor = Exception.class, readOnly = true)
	public CollaboratoreProgettoDTO getCollaboratoreProgetto(Integer collaboratoreProgettoId) throws Exception {
								
		Optional<CollaboratoreProgetto> collaboratoreProgettoOptional = collaboratoreProgettoRepository.findById(collaboratoreProgettoId);

		if(collaboratoreProgettoOptional.isPresent()) {
			CollaboratoreProgettoDTO collaboratoreProgetto = ObjectMapperUtils.map(collaboratoreProgettoOptional.get(), CollaboratoreProgettoDTO.class);
			setCollabValidityFlag(collaboratoreProgetto);
			return collaboratoreProgetto;
		} else {
			throw new Exception("CollaboratoreProgetto not found");
		}

	}

	@Override
	@Transactional(rollbackFor = Exception.class, readOnly = false)
	public CollaboratoreProgettoDTO insertCollaboratoreProgetto(@Valid CollaboratoreProgettoDTO newCollaboratoreProgetto, boolean notValidYet) throws Exception {
		
		CollaboratoreProgetto savedCollaboratoreProgetto = null;
		
		CollaboratoreProgetto collaboratoreProgetto = ObjectMapperUtils.map(newCollaboratoreProgetto, CollaboratoreProgetto.class);
		
		// validity dates are managed by the backend
		collaboratoreProgetto.setDtInizioVal(null);
		collaboratoreProgetto.setDtFineVal(null);
		
		// add audit fields
		AuditWithValidityModel audit = (AuditWithValidityModel) collaboratoreProgetto;
		AuditUtils.fillAudit(audit);
		
		fillDefaults(collaboratoreProgetto);
		
		// check if the utente if valid
		checkForValidUtente(collaboratoreProgetto.getUtente());
		
		if (notValidYet) {
			// i.e. if called from frontoffice
			collaboratoreProgetto.setFlgConferma(Constants.COLLABORATORE_PROGETTO_FLG_CONFERMA_IN_ATTESA);
			collaboratoreProgetto.setFlgCoord(0);
			collaboratoreProgetto.setDtConferma(null);
			collaboratoreProgetto.setDtInizioVal(null);
			collaboratoreProgetto.setDtFineVal(null);
		} else {
			// i.e. if called from backoffice
            // since it is an operator to insert the record, it is automatically confirmed
			collaboratoreProgetto.setFlgConferma(Constants.COLLABORATORE_PROGETTO_FLG_CONFERMA_CONFERMATA);
			collaboratoreProgetto.setDtConferma(LocalDate.now());
		}
		collaboratoreProgetto.setMotivConferma(null);
		

				
		// check if there is another record for the same utente already
		checkForExistingRecordForUtente(collaboratoreProgetto);
		
		// check if rejected request exists
		CollaboratoreProgetto rejectedCollaboration = findRejectedCollaboration(collaboratoreProgetto);
		
		// check if past collaboration exists
		CollaboratoreProgetto pastCollaboration = rejectedCollaboration == null ? findPastCollaboration(collaboratoreProgetto) : null;
		
		if (rejectedCollaboration != null) {
			
			// recover old record
			rejectedCollaboration.setFlgConferma(collaboratoreProgetto.getFlgConferma());
			rejectedCollaboration.setFlgCoord(collaboratoreProgetto.getFlgCoord());
			rejectedCollaboration.setDtConferma(collaboratoreProgetto.getDtConferma());
			rejectedCollaboration.setDtInizioVal(collaboratoreProgetto.getDtInizioVal());
			rejectedCollaboration.setDtFineVal(collaboratoreProgetto.getDtFineVal());
			rejectedCollaboration.setMotivConferma(collaboratoreProgetto.getMotivConferma());
			
			savedCollaboratoreProgetto = collaboratoreProgettoRepository.save(rejectedCollaboration);
		} else if (pastCollaboration != null) {
			
			// save historical record. Two steps to ensure detachment from DB
			CollaboratoreProgettoDTO historicalDTO = ObjectMapperUtils.map(pastCollaboration, CollaboratoreProgettoDTO.class);
			CollaboratoreProgetto historical = ObjectMapperUtils.map(historicalDTO, CollaboratoreProgetto.class);
			historical.setIdCollab(null);
			historical.setDtIns(pastCollaboration.getDtIns());
			historical.setIdOperIns(pastCollaboration.getIdOperIns());
			historical.setOperIns(pastCollaboration.getOperIns());
			historical.setDtUltMod(pastCollaboration.getDtUltMod());
			historical.setIdOperUltMod(pastCollaboration.getIdOperUltMod());
			historical.setOperUltMod(pastCollaboration.getOperUltMod());
			historical.setDtInizioVal(pastCollaboration.getDtInizioVal());
			historical.setDtFineVal(pastCollaboration.getDtFineVal());
			
			collaboratoreProgettoRepository.save(historical);
			
			// recover old record
			pastCollaboration.setFlgConferma(collaboratoreProgetto.getFlgConferma());
			pastCollaboration.setFlgCoord(collaboratoreProgetto.getFlgCoord());
			pastCollaboration.setDtConferma(collaboratoreProgetto.getDtConferma());
			pastCollaboration.setDtInizioVal(collaboratoreProgetto.getDtInizioVal());
			pastCollaboration.setDtFineVal(collaboratoreProgetto.getDtFineVal());
			pastCollaboration.setMotivConferma(collaboratoreProgetto.getMotivConferma());
			pastCollaboration.setDtRichiesta(LocalDate.now());
			
			savedCollaboratoreProgetto = collaboratoreProgettoRepository.save(pastCollaboration);
		} else {			
			savedCollaboratoreProgetto = collaboratoreProgettoRepository.save(collaboratoreProgetto);
		}
		
		
		// invio email
		if (!notValidYet) {				
			sendEmailNewCollaboratoreProgetto(savedCollaboratoreProgetto);
		} else {
			sendEmailNewRequestCollaboratoreProgetto(savedCollaboratoreProgetto);
		}
		
		return ObjectMapperUtils.map(savedCollaboratoreProgetto, CollaboratoreProgettoDTO.class);
	}
	
	private CollaboratoreProgetto findPastCollaboration(CollaboratoreProgetto collaboratoreProgetto) {
		Utente utente = collaboratoreProgetto.getUtente();
		String idProgetto = collaboratoreProgetto.getIdProgetto();
		
		List<CollaboratoreProgetto> records = collaboratoreProgettoRepository.findPastCollaborations(utente, idProgetto, Date.valueOf(LocalDate.now()));
		
		if (records.size() > 0) {
			return records.get(0);
		} else {
			return null;
		}
	}

	private CollaboratoreProgetto findRejectedCollaboration(CollaboratoreProgetto collaboratoreProgetto) {
		Utente utente = collaboratoreProgetto.getUtente();
		String idProgetto = collaboratoreProgetto.getIdProgetto();
		
		List<CollaboratoreProgetto> records = collaboratoreProgettoRepository.findAllByFlgConfermaAndUtenteAndIdProgetto(Constants.COLLABORATORE_PROGETTO_FLG_CONFERMA_RIFIUTATA, utente, idProgetto);
		
		if (records.size() > 0) {
			return records.get(0);
		} else {
			return null;
		}
	}

	private void checkForValidUtente(Utente utente) throws Exception {
		if (utente == null) {
			throw new Exception("L'utente non è stato specificato");
		}
		
		Optional<Utente> storedUtenteOptional = utenteRepository.findById(utente.getIdUtente());
		if (storedUtenteOptional.isPresent()) {
			Utente storedUtente = storedUtenteOptional.get();
			
			LocalDate today = LocalDate.now();
			
			if (storedUtente.getFlgArchived().equals(1) 
					|| (storedUtente.getDtFineVal() != null && storedUtente.getDtFineVal().before(Date.valueOf(today)))
					|| (storedUtente.getDtInizioVal() != null && storedUtente.getDtInizioVal().after(Date.valueOf(today)))) {
				throw new Exception("Utente non valido");
			}
		} else {
			throw new Exception("Utente non trovato");
		}
		
	}

	private void checkForExistingRecordForUtente(CollaboratoreProgetto collaboratoreProgetto) throws Exception {
		Utente utente = collaboratoreProgetto.getUtente();
		if (utente == null) {
			return;
		}
		
		List<CollaboratoreProgetto> records = collaboratoreProgettoRepository.findAllCurrentByIdProgettoAndUtente(collaboratoreProgetto.getIdProgetto(), collaboratoreProgetto.getUtente(), Date.valueOf(LocalDate.now()));
		if (records.size() > 0) {
			throw new Exception("L'utente risulta già un collaboratore al progetto");
		}
	}

	private void fillDefaults(CollaboratoreProgetto collaboratoreProgetto) {
		LocalDate today = LocalDate.now();
		collaboratoreProgetto.setDtRichiesta(today);
		
		if (collaboratoreProgetto.getFlgConferma() == null) {
			collaboratoreProgetto.setFlgConferma(0);
		}
		if (collaboratoreProgetto.getFlgCoord() == null) {
			collaboratoreProgetto.setFlgCoord(0);
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class, readOnly = false)
	public CollaboratoreProgettoDTO updateCollaboratoreProgetto(CollaboratoreProgettoDTO patchCollaboratoreProgetto, boolean isPatch) throws Exception {
				
		// retrieve original
		Optional<CollaboratoreProgetto> originalCollaboratoreProgettoOptional = collaboratoreProgettoRepository.findById(patchCollaboratoreProgetto.getIdCollab());
		if (!originalCollaboratoreProgettoOptional.isPresent()) {
			throw new Exception("Collaboratore Progetto not found");
		}
		CollaboratoreProgetto originalCollaboratoreProgetto = originalCollaboratoreProgettoOptional.get();

		CollaboratoreProgetto collaboratoreProgetto = CollaboratoreProgettoDTO.patchCollaboratoreProgetto(patchCollaboratoreProgetto, originalCollaboratoreProgetto, isPatch, true);
		
		// validity dates are managed by the backend
		collaboratoreProgetto.setDtInizioVal(originalCollaboratoreProgetto.getDtInizioVal());
		collaboratoreProgetto.setDtFineVal(originalCollaboratoreProgetto.getDtFineVal());
		
		// set audit fields
		AuditWithValidityModel audit = (AuditWithValidityModel) collaboratoreProgetto;
		AuditUtils.fillAudit(audit);
		
		// check if confirming
		if (collaboratoreProgetto.getDtConferma() == null && collaboratoreProgetto.getFlgConferma().equals(Constants.COLLABORATORE_PROGETTO_FLG_CONFERMA_CONFERMATA)) {
			LocalDate today = LocalDate.now();
			
			collaboratoreProgetto.setDtConferma(today);
			collaboratoreProgetto.setDtInizioVal(Date.valueOf(today));
			collaboratoreProgetto.setDtFineVal(Date.valueOf(Constants.DEFAULT_DATE_END_VALIDITY));
			
			// send confirmation email
			sendEmailNewCollaboratoreProgetto(collaboratoreProgetto);
		}
		// check if rejecting
		if (collaboratoreProgetto.getDtConferma() == null && collaboratoreProgetto.getFlgConferma().equals(Constants.COLLABORATORE_PROGETTO_FLG_CONFERMA_RIFIUTATA)) {
			LocalDate today = LocalDate.now();
			
			collaboratoreProgetto.setDtConferma(today);
			
			// send rejection email
			sendEmailRejectNewCollaboratoreProgetto(collaboratoreProgetto);
		}
		// check if canceling
		if (collaboratoreProgetto.getFlgConferma().equals(Constants.COLLABORATORE_PROGETTO_FLG_CONFERMA_ANNULLATA_DALL_UTENTE) ||
				collaboratoreProgetto.getFlgConferma().equals(Constants.COLLABORATORE_PROGETTO_FLG_CONFERMA_ANNULLATA_DA_OPERATORE)) {
						
			LocalDate today = LocalDate.now();
			if (collaboratoreProgetto.getDtConferma() == null) {				
				
				collaboratoreProgetto.setDtConferma(today);
			}
			if (collaboratoreProgetto.getDtFineVal() != null) {
				collaboratoreProgetto.setDtFineVal(Date.valueOf(today));
			}
			
			boolean deleting = originalCollaboratoreProgetto.getFlgConferma().equals(Constants.COLLABORATORE_PROGETTO_FLG_CONFERMA_IN_ATTESA);
			
			if (deleting) {
				// delete record
				collaboratoreProgettoRepository.deleteById(collaboratoreProgetto.getIdCollab());
			}
			
			// send cancellation email
			boolean sendAlsoToOfficial = collaboratoreProgetto.getFlgConferma().equals(Constants.COLLABORATORE_PROGETTO_FLG_CONFERMA_ANNULLATA_DALL_UTENTE);
			sendEmailCancelCollaboratoreProgetto(collaboratoreProgetto, sendAlsoToOfficial);
			
			if (deleting) {			
				return ObjectMapperUtils.map(collaboratoreProgetto, CollaboratoreProgettoDTO.class);
			}
			
			// leave original value of flgConferma
			collaboratoreProgetto.setFlgConferma(originalCollaboratoreProgetto.getFlgConferma()); //-> la validità èè definita dalle date
		}
		
		
		CollaboratoreProgetto savedCollaboratoreProgetto = collaboratoreProgettoRepository.save(collaboratoreProgetto);
		
		return ObjectMapperUtils.map(savedCollaboratoreProgetto, CollaboratoreProgettoDTO.class);

	}

	@Override
	@Transactional(rollbackFor = Exception.class, readOnly = false)
	public MessaggioDTO sendMessaggioToCollaboratoriProgetto(MessaggioDTO messaggio) throws Exception {
		// get list of collaboratori
		List<CollaboratoreProgetto> collaboratori = collaboratoreProgettoRepository.findAllByIdProgettoOrderByUtenteCognomeAscUtenteNomeAsc(messaggio.getIdContenuto());
		
		// concatenate email addresses
		String[] indirizzi = collaboratori.stream()
				.map(c -> c.getUtente().getEmail()).toArray(String[]::new);
		
		String indirizzo = String.join(";", indirizzi);
		messaggio.setIndirizzo(indirizzo);
		
		// insert and send messaggio
		MessaggioDTO savedMessaggio = messaggioService.insertAndSendMessaggio(messaggio);
		
		// link utenti with messaggio
		Integer idMessaggio = savedMessaggio.getIdMessaggio();
		
		Integer[] collaboratoriThatAreUtentiIds = collaboratori.stream()
				.filter(c -> c.getUtente() != null)
				.map(c -> c.getUtente().getIdUtente()).toArray(Integer[]::new);
		
		utenteMessaggiService.linkUtentiWithMessaggio(collaboratoriThatAreUtentiIds, idMessaggio);
		
		return savedMessaggio;
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class, readOnly = false)
	public MessaggioDTO sendMessaggioToCollaboratoriProgetto(CollaboratoreProgettoFilterDTO collaboratoreProgettoFilter, MessaggioDTO messaggio) throws Exception {
		
		// get list of collaboratori
		String sortProperty = "dtRichiesta";
		String sortDirection = "ASC";
		
		Pageable pageable = PagedData.buildPageable(0, 999999, sortProperty, sortDirection);
		
		Page<CollaboratoreProgetto> page = collaboratoreProgettoRepository.findAll(RepositoryUtils.buildCollaboratoreProgettoFilterSpecification(collaboratoreProgettoFilter, Constants.GET_COLLABORATORI_PROGETTO_FILTER_TYPE_ALL), pageable);
		
		List<CollaboratoreProgetto> collaboratori = page.toList();
		
		// concatenate email addresses
		String[] indirizzi = collaboratori.stream()
				.map(c -> c.getUtente().getEmail()).toArray(String[]::new);
		
		String indirizzo = String.join(";", indirizzi);
		messaggio.setIndirizzo(indirizzo);
		
		// insert and send messaggio
		MessaggioDTO savedMessaggio = messaggioService.insertAndSendMessaggio(messaggio);
		
		// link utenti with messaggio
		Integer idMessaggio = savedMessaggio.getIdMessaggio();
		
		Integer[] utentiIds = collaboratori.stream()
				.map(c -> c.getUtente().getIdUtente()).toArray(Integer[]::new);
		
		utenteMessaggiService.linkUtentiWithMessaggio(utentiIds, idMessaggio);
		
		return savedMessaggio;
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class, readOnly = false)
	public void executeOperationOnCollaboratoriProgetto(@Valid OperazioneMassivaDTO operazione) throws Exception {
		
		LOG.debug(String.format("Requested operation: %s", operazione.getTipoOperazione()));
		
		switch (operazione.getTipoOperazione()) {
		case Constants.OPERAZIONE_COLLABORATOREPROGETTO_IMPOSTA_COORDINATORE:
			impostaCoordinatore(operazione);
			break;		

		default:
			throw new OperationNotSupportedException("Operation non supported");
			// break;
		}
		
	}
		
	private StringSubstitutor getPlaceholderSubstitutor(Messaggio messaggio, MyExtranetContent progetto, Map<String, String> customValuesMap) {
		
		String tipoProgetto = progetto.getTipo();	
		
		String myPortalContentContextUrl = 	mySysConfigService.getConcatValue(ipa + Constants.MYSYSCONFIG_KEY_MYPORTALDOMAIN, "") +
				mySysConfigService.getConcatValue(ipa + Constants.MYSYSCONFIG_KEY_MYPORTALCONTEXT, "");
		
		String href = StringUtils.hasText(progetto.getSlug()) ?
				String.format("%sdettaglio/contenuto/%s", myPortalContentContextUrl, progetto.getSlug()) :
					String.format("%sdettaglio?contentId=%s&type=%s", myPortalContentContextUrl, progetto.getId(), tipoProgetto);
		
		Map<String, String> valuesMap = new HashMap();
		valuesMap.putAll(Map.of(
                "progetto.titolo", it.regioneveneto.myp3.myextranet.utils.StringUtils.stringOrEmpty(progetto.getTitolo()),
                "progetto.descrizione", it.regioneveneto.myp3.myextranet.utils.StringUtils.stringOrEmpty(progetto.getDescrizione()),
                "progetto.sottotitolo", it.regioneveneto.myp3.myextranet.utils.StringUtils.stringOrEmpty(progetto.getSottotitolo()),
                "messaggio.destinatario", it.regioneveneto.myp3.myextranet.utils.StringUtils.stringOrEmpty(messaggio.getDestinatario()),
                "progetto.link", it.regioneveneto.myp3.myextranet.utils.StringUtils.stringOrEmpty(href)
        ));
		
		if (customValuesMap != null) {
			valuesMap.putAll(customValuesMap);
		}
		
		return new StringSubstitutor(valuesMap);
	}

	private Messaggio sendEmailNewRequestCollaboratoreProgetto(CollaboratoreProgetto collaboratoreProgetto) throws Exception {
		MyExtranetContent project = myPortalService.getContentById(collaboratoreProgetto.getIdProgetto());
		
		// load Utente
		Utente utente = utenteRepository.getOne(collaboratoreProgetto.getUtente().getIdUtente());
		
		// additional addresses
		String additionalAddresses = ";" + getCollaborationsOfficialAddress();
		
		Messaggio messaggio = new Messaggio();
		messaggio.setArea(Constants.MESSAGGIO_AREA_PROGETTO);
		messaggio.setTipo(Constants.MESSAGGIO_TIPO_COMUNICAZIONE);
		messaggio.setDestinatario(String.format("%s %s", utente.getNome(), utente.getCognome()));
		messaggio.setIdContenuto(collaboratoreProgetto.getIdProgetto());
		messaggio.setIndirizzo(utente.getEmail() + additionalAddresses);
		
		Map<String, String> customAttributes = new HashMap<String, String>();
		
		StringSubstitutor substitutor = getPlaceholderSubstitutor(messaggio, project, customAttributes);

		String richiestaCollaborazioneEmailTitle = 
				myConfigService.getConcatValue(ipa + richiestaCollaborazioneEmailTitleKey, "");
		
		messaggio.setTitolo(substitutor.replace(richiestaCollaborazioneEmailTitle));
		String bodyTemplate = 
				myConfigService.getConcatValue(ipa + richiestaCollaborazioneEmailBodyTemplateFileKey, "");
		messaggio.setMessaggio(substitutor.replace(bodyTemplate));
				
		// insert and send messaggio
		Messaggio savedMessaggio = messaggioService.insertAndSendMessaggio(messaggio, null);
		
		// link utente with messaggio
		Integer idMessaggio = savedMessaggio.getIdMessaggio();
		Integer[] utenteIds = { utente.getIdUtente() };
		
		utenteMessaggiService.linkUtentiWithMessaggio(utenteIds, idMessaggio);
		
		return savedMessaggio;
	}
	
	private Messaggio sendEmailCancelCollaboratoreProgetto(CollaboratoreProgetto collaboratoreProgetto, boolean sendAlsoToOfficial) throws Exception {
		MyExtranetContent project = myPortalService.getContentById(collaboratoreProgetto.getIdProgetto());
		
		// load Utente
		Utente utente = utenteRepository.getOne(collaboratoreProgetto.getUtente().getIdUtente());
		
		// additional addresses
		String additionalAddresses = sendAlsoToOfficial ? ";" + getCollaborationsOfficialAddress()  : "";
		
		Messaggio messaggio = new Messaggio();
		messaggio.setArea(Constants.MESSAGGIO_AREA_PROGETTO);
		messaggio.setTipo(Constants.MESSAGGIO_TIPO_COMUNICAZIONE);
		messaggio.setDestinatario(String.format("%s %s", utente.getNome(), utente.getCognome()));
		messaggio.setIdContenuto(collaboratoreProgetto.getIdProgetto());
		messaggio.setIndirizzo(utente.getEmail() + additionalAddresses);
		
		Map<String, String> customAttributes = Map.of("conferma.motivazione", collaboratoreProgetto.getMotivConferma());
		
		StringSubstitutor substitutor = getPlaceholderSubstitutor(messaggio, project, customAttributes);
		
		String cancellazioneCollaboratoreEmailTitle = 
				myConfigService.getConcatValue(ipa + cancellazioneCollaboratoreEmailTitleKey, "");
		
		messaggio.setTitolo(substitutor.replace(cancellazioneCollaboratoreEmailTitle));
		String bodyTemplate = 
				myConfigService.getConcatValue(ipa + cancellazioneCollaboratoreEmailBodyTemplateFileKey, "");
		messaggio.setMessaggio(substitutor.replace(bodyTemplate));
				
		// insert and send messaggio
		Messaggio savedMessaggio = messaggioService.insertAndSendMessaggio(messaggio, null);
		
		// link utente with messaggio
		Integer idMessaggio = savedMessaggio.getIdMessaggio();
		Integer[] utenteIds = { utente.getIdUtente() };
		
		utenteMessaggiService.linkUtentiWithMessaggio(utenteIds, idMessaggio);
		
		return savedMessaggio;
	}
	
	private String getCollaborationsOfficialAddress() {
		return myConfigService.getConcatValue(ipa + funzionarioCollaborazioniEmailAddressKey, "");
	}

	private Messaggio sendEmailRejectNewCollaboratoreProgetto(CollaboratoreProgetto collaboratoreProgetto) throws Exception {
		MyExtranetContent project = myPortalService.getContentById(collaboratoreProgetto.getIdProgetto());
		
		// load Utente
		Utente utente = utenteRepository.getOne(collaboratoreProgetto.getUtente().getIdUtente());
		
		Messaggio messaggio = new Messaggio();
		messaggio.setArea(Constants.MESSAGGIO_AREA_PROGETTO);
		messaggio.setTipo(Constants.MESSAGGIO_TIPO_COMUNICAZIONE);
		messaggio.setDestinatario(String.format("%s %s", utente.getNome(), utente.getCognome()));
		messaggio.setIdContenuto(collaboratoreProgetto.getIdProgetto());
		messaggio.setIndirizzo(utente.getEmail());
		
		Map<String, String> customAttributes = Map.of("conferma.motivazione", collaboratoreProgetto.getMotivConferma());
		
		StringSubstitutor substitutor = getPlaceholderSubstitutor(messaggio, project, customAttributes);
		
		String rifiutoCollaboratoreEmailTitle = 
				myConfigService.getConcatValue(ipa + rifiutoCollaboratoreEmailTitleKey, "");
		
		messaggio.setTitolo(substitutor.replace(rifiutoCollaboratoreEmailTitle));
		String bodyTemplate = 
				myConfigService.getConcatValue(ipa + rifiutoCollaboratoreEmailBodyTemplateFileKey, "");
		messaggio.setMessaggio(substitutor.replace(bodyTemplate));
				
		// insert and send messaggio
		Messaggio savedMessaggio = messaggioService.insertAndSendMessaggio(messaggio, null);
		
		// link utente with messaggio
		Integer idMessaggio = savedMessaggio.getIdMessaggio();
		Integer[] utenteIds = { utente.getIdUtente() };
		
		utenteMessaggiService.linkUtentiWithMessaggio(utenteIds, idMessaggio);
		
		return savedMessaggio;
	}

	
	private Messaggio sendEmailNewCollaboratoreProgetto(CollaboratoreProgetto collaboratoreProgetto) throws Exception {
				
		MyExtranetContent project = myPortalService.getContentById(collaboratoreProgetto.getIdProgetto());
		
		// load Utente
		Utente utente = utenteRepository.getOne(collaboratoreProgetto.getUtente().getIdUtente());
				
		Messaggio messaggio = new Messaggio();
		messaggio.setArea(Constants.MESSAGGIO_AREA_PROGETTO);
		messaggio.setTipo(Constants.MESSAGGIO_TIPO_COMUNICAZIONE);
		messaggio.setDestinatario(String.format("%s %s", utente.getNome(), utente.getCognome()));
		messaggio.setIdContenuto(collaboratoreProgetto.getIdProgetto());
		messaggio.setIndirizzo(utente.getEmail());
		
		StringSubstitutor substitutor = getPlaceholderSubstitutor(messaggio, project, null);
		
		String nuovoCollaboratoreEmailTitle = 
				myConfigService.getConcatValue(ipa + nuovoCollaboratoreEmailTitleKey, "");
		
		messaggio.setTitolo(substitutor.replace(nuovoCollaboratoreEmailTitle));
		String bodyTemplate = 
				myConfigService.getConcatValue(ipa + nuovoCollaboratoreEmailBodyTemplateFileKey, "");
		messaggio.setMessaggio(substitutor.replace(bodyTemplate));
				
		// insert and send messaggio
		Messaggio savedMessaggio = messaggioService.insertAndSendMessaggio(messaggio, null);
		
		// link utente with messaggio
		Integer idMessaggio = savedMessaggio.getIdMessaggio();
		Integer[] utenteIds = { utente.getIdUtente() };
		
		utenteMessaggiService.linkUtentiWithMessaggio(utenteIds, idMessaggio);
		
		return savedMessaggio;
	}
	
	private void impostaCoordinatore(OperazioneMassivaDTO operazione) {
		
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
		
		collaboratoreProgettoRepository.setFlgCoordForIn(0, toTurnOff.toArray(Integer[]::new));
		collaboratoreProgettoRepository.setFlgCoordForIn(1, toTurnOn.toArray(Integer[]::new));
	}

	@Override
	public boolean isActiveCollaboratoreProgetto(String idProgetto, String codiceFiscale) {
		CollaboratoreProgetto collaboratore = collaboratoreProgettoRepository.findActiveCollaboration(idProgetto, codiceFiscale, Date.valueOf(LocalDate.now()));
		
		return collaboratore != null;
	}

	@Override
	@Transactional(rollbackFor = Exception.class, readOnly = true)
	public PagedData<MyExtranetContent> getProgettiCollaborati(UserWithAdditionalInfo user, Integer pageNumber,
			Integer pageSize, String orderProperty, String orderDirection) {
		
		String[] contentIds = collaboratoreProgettoRepository.getProjectIdsForActiveCollaborations(user.getCodiceFiscale(), Date.valueOf(LocalDate.now()));
		
		if (contentIds == null || contentIds.length == 0) {
			return new PagedData<MyExtranetContent>(new ArrayList<MyExtranetContent>(), 0, 1, pageSize);
		}
		
		PagedData<MyExtranetContent> pagedContents = myPortalService.getContentsByIds(contentIds, pageNumber, pageSize,orderProperty, orderDirection);
		
		return pagedContents;
	}

	@Override
	@Transactional(rollbackFor = Exception.class, readOnly = true)
	public PagedData<DettaglioCollaborazioneDTO> getDettaglioProgettiCollaborati(UserWithAdditionalInfo user,
			Integer pageNumber, Integer pageSize, String sortProperty, String sortDirection) {
		
		if (sortProperty == null) sortProperty = "dtRichiesta";
		if (sortDirection == null) sortDirection = "ASC";
		
		Pageable pageable = PagedData.buildPageable(pageNumber, pageSize, sortProperty, sortDirection);
		
		// get userId
		Map<String, Object> userValidityProperties = utenteService.getUserValidityProperties(user.getCodiceFiscale());
		Integer userId = (Integer) userValidityProperties.get("userId");
				
		Page<CollaboratoreProgetto> page = collaboratoreProgettoRepository.findAll(RepositoryUtils.buildAllCurrentCollaboratoreProgettoByCodFiscaleSpecification(userId), pageable);
		
		List<CollaboratoreProgetto> collaboratoriProgetto = page.getContent();
		
		if (collaboratoriProgetto == null || collaboratoriProgetto.size() == 0) {
			return new PagedData<DettaglioCollaborazioneDTO>(new ArrayList<DettaglioCollaborazioneDTO>(), 0, 1, pageSize);
		}
		
		DettaglioCollaborazioneDTO[] dettagli = collaboratoriProgetto.stream().map(c -> {
			MyExtranetContent progetto =  myPortalService.getContentById(c.getIdProgetto());
			DettaglioCollaborazioneDTO dettaglio = new DettaglioCollaborazioneDTO();
			dettaglio.setMyExtranetContent(progetto);
			dettaglio.setCollaboratoreProgetto(ObjectMapperUtils.map(c, CollaboratoreProgettoDTO.class));
			return dettaglio;
		}).toArray(DettaglioCollaborazioneDTO[]::new);

		PagedData<DettaglioCollaborazioneDTO> pagedData = new PagedData<DettaglioCollaborazioneDTO>(Arrays.asList(dettagli), page);
		return pagedData;

	}

	@Override
	@Transactional(rollbackFor = Exception.class, readOnly = true)
	public DettaglioCollaborazioneDTO getDettaglioCollaborazioneProgetto(Integer collaboratoreProgettoId) throws Exception {
		
		Optional<CollaboratoreProgetto> collaboratoreProgettoOptional = collaboratoreProgettoRepository.findById(collaboratoreProgettoId);

		if(collaboratoreProgettoOptional.isPresent()) {
			CollaboratoreProgettoDTO collaboratoreProgetto = ObjectMapperUtils.map(collaboratoreProgettoOptional.get(), CollaboratoreProgettoDTO.class);
			
			MyExtranetContent myExtranetContent = myPortalService.getContentById(collaboratoreProgetto.getIdProgetto());
			
			DettaglioCollaborazioneDTO dettaglioCollaborazione = new DettaglioCollaborazioneDTO();
			
			setCollabValidityFlag(collaboratoreProgetto);
			
			dettaglioCollaborazione.setCollaboratoreProgetto(collaboratoreProgetto);
			dettaglioCollaborazione.setMyExtranetContent(myExtranetContent);
			
			return dettaglioCollaborazione;
		} else {
			throw new Exception("CollaboratoreProgetto not found");
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class, readOnly = true)
	public DettaglioCollaborazioneDTO getDettaglioCollaborazioneProgetto(
			CollaboratoreProgettoDettaglioFilterDTO collaboratoreProgettoDettaglioFilter, UserWithAdditionalInfo user) throws Exception {

		Integer idCollaborazione = collaboratoreProgettoDettaglioFilter.getIdCollab();
		
		String idProgetto = null;
		
		DettaglioCollaborazioneDTO dettaglio = new DettaglioCollaborazioneDTO();
		
		if (idCollaborazione != null) {
			// search by idCollab
			Optional<CollaboratoreProgetto> collaborazione = collaboratoreProgettoRepository.findById(idCollaborazione);
			
			if (collaborazione.isPresent()) {
				CollaboratoreProgettoDTO collaborazioneData = ObjectMapperUtils.map(collaborazione.get(), CollaboratoreProgettoDTO.class);
				setCollabValidityFlag(collaborazioneData);
				dettaglio.setCollaboratoreProgetto(collaborazioneData);
				idProgetto = collaborazioneData.getIdProgetto();
			} else {
				throw new MyExtranetException("Collaborazione progetto not found");
			}
		} else {
			// search by idProgetto and logged-in user
			
			// get users' userId
			Map<String, Object> userValidityProperties = utenteService.getUserValidityProperties(user.getCodiceFiscale());
			Integer idUtente = (Integer) userValidityProperties.get("userId");
			idProgetto = collaboratoreProgettoDettaglioFilter.getIdProgetto();
			CollaboratoreProgettoFilterDTO collaboratoreProgettoFilter = new CollaboratoreProgettoFilterDTO();
			collaboratoreProgettoFilter.setIdProgetto(idProgetto);
			collaboratoreProgettoFilter.setIdUtente(idUtente);
			
			List<Sort.Order> orders = new ArrayList<Sort.Order>(2);
			//orders.add(Order.asc("flgConferma"));
			orders.add(Order.desc("dtRichiesta"));
			orders.add(Order.desc("idCollab"));
			Sort sort = Sort.by(orders);
			
			PagedData<CollaboratoreProgettoDTO> page = getCollaboratoriProgettoWithSort(collaboratoreProgettoFilter, 1, 999, sort);
			List<CollaboratoreProgettoDTO> list = page.getRecords();
			if (list != null && list.size() > 0) {
				CollaboratoreProgettoDTO collaboratoreProgetto = list.get(0);
				setCollabValidityFlag(collaboratoreProgetto);
				dettaglio.setCollaboratoreProgetto(collaboratoreProgetto);
			} 			
			else if(list == null) {
				throw new MyExtranetException("Collaboratore Progetto not found");
			}
		}
		
		// integrate with MyCMS data
		MyExtranetContent myExtranetContent = myPortalService.getContentById(idProgetto);
		dettaglio.setMyExtranetContent(myExtranetContent);
		
		return dettaglio;
	}

	@Override
	@Transactional(rollbackFor = Exception.class, readOnly = true)
	public List<CounterPerEntity> getPendingCollaborationRequestsPerProject(String[] ids) {
		
		List<ICounterPerEntity> counters;
		
		if (ids == null) {
			counters = collaboratoreProgettoRepository.getCountersForProjectsWithPendingRequests();
		} else {
			counters = collaboratoreProgettoRepository.getCountersForProjectsWithPendingRequestsInList(ids);
		}
		
		List<CounterPerEntity> convertedCounters = ObjectMapperUtils.mapAll(counters, CounterPerEntity.class);
		return convertedCounters;
	}

}
