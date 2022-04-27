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
		import java.util.HashMap;
		import java.util.List;
		import java.util.Map;
		import java.util.Optional;

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

		import it.regioneveneto.myp3.myextranet.exception.MyExtranetException;
		import it.regioneveneto.myp3.myextranet.exception.MyExtranetValidationException;
		import it.regioneveneto.myp3.myextranet.model.AuditWithValidityModel;
		import it.regioneveneto.myp3.myextranet.model.Ente;
		import it.regioneveneto.myp3.myextranet.model.Messaggio;
		import it.regioneveneto.myp3.myextranet.model.RappresentanteEnte;
		import it.regioneveneto.myp3.myextranet.model.Utente;
		import it.regioneveneto.myp3.myextranet.repository.EnteRepository;
		import it.regioneveneto.myp3.myextranet.repository.RappresentanteEnteRepository;
		import it.regioneveneto.myp3.myextranet.repository.UtenteRepository;
		import it.regioneveneto.myp3.myextranet.security.UserWithAdditionalInfo;
		import it.regioneveneto.myp3.myextranet.service.MessaggioService;
		import it.regioneveneto.myp3.myextranet.service.MyBoxService;
		import it.regioneveneto.myp3.myextranet.service.MyConfigService;
		import it.regioneveneto.myp3.myextranet.service.MySysConfigService;
		import it.regioneveneto.myp3.myextranet.service.RappresentanteEnteService;
		import it.regioneveneto.myp3.myextranet.service.UtenteMessaggiService;
		import it.regioneveneto.myp3.myextranet.service.UtenteService;
		import it.regioneveneto.myp3.myextranet.utils.AuditUtils;
		import it.regioneveneto.myp3.myextranet.utils.Constants;
		import it.regioneveneto.myp3.myextranet.utils.ObjectMapperUtils;
		import it.regioneveneto.myp3.myextranet.utils.RepositoryUtils;
		import it.regioneveneto.myp3.myextranet.utils.StringUtils;
		import it.regioneveneto.myp3.myextranet.web.dto.PagedData;
		import it.regioneveneto.myp3.myextranet.web.dto.RappresentanteEnteDTO;
		import it.regioneveneto.myp3.myextranet.web.dto.RappresentanteEnteFilterDTO;

@Service
public class RappresentanteEnteServiceImpl implements RappresentanteEnteService {
	private static final Logger LOG = LoggerFactory.getLogger(RappresentanteEnteServiceImpl.class);

	@Autowired
	RappresentanteEnteRepository rappresentanteEnteRepository;

	@Autowired
	UtenteRepository utenteRepository;

	@Autowired
	EnteRepository enteRepository;

	@Autowired
	MyBoxService myBoxService;

	@Autowired
	MyConfigService myConfigService;

	@Autowired
	MySysConfigService mySysConfigService;

	@Autowired
	MessaggioService messaggioService;

	@Autowired
	UtenteMessaggiService utenteMessaggiService;

	@Autowired
	UtenteService utenteService;

	private final String ipa;

	private final String nuovoRappresentanteEmailTitleKey;
	private final String nuovoRappresentanteEmailBodyTemplateFileKey;
	private final String rifiutoRappresentanteEmailTitleKey;
	private final String rifiutoRappresentanteEmailBodyTemplateFileKey;
	private final String richiestaRappresentanteEmailTitleKey;
	private final String richiestaRappresentanteEmailBodyTemplateFileKey;
	private final String responsabileProdottiEmailAddressKey;

	public RappresentanteEnteServiceImpl(
			@Value("${myportal.ipa}") String ipa,
			@Value("${mychannel.messages.nuovoRappresentante.myConfigKeys.emailTitle}") String nuovoRappresentanteEmailTitleKey,
			@Value("${mychannel.messages.nuovoRappresentante.myConfigKeys.emailBodyTemplateFile}") String nuovoRappresentanteEmailBodyTemplateFileKey,
			@Value("${mychannel.messages.rifiutoRappresentante.myConfigKeys.emailTitle}") String rifiutoRappresentanteEmailTitleKey,
			@Value("${mychannel.messages.rifiutoRappresentante.myConfigKeys.emailBodyTemplateFile}") String rifiutoRappresentanteEmailBodyTemplateFileKey,
			@Value("${mychannel.messages.richiestaRappresentante.myConfigKeys.emailTitle}") String richiestaRappresentanteEmailTitleKey,
			@Value("${mychannel.messages.richiestaRappresentante.myConfigKeys.emailBodyTemplateFile}") String richiestaRappresentanteEmailBodyTemplateFileKey,
			@Value("${mychannel.addresses.responsabileProdotti.myConfigKeys.emailAddress}") String responsabileProdottiEmailAddressKey
	) {
		super();
		this.ipa = ipa;
		this.nuovoRappresentanteEmailTitleKey = nuovoRappresentanteEmailTitleKey;
		this.nuovoRappresentanteEmailBodyTemplateFileKey = nuovoRappresentanteEmailBodyTemplateFileKey;
		this.rifiutoRappresentanteEmailTitleKey = rifiutoRappresentanteEmailTitleKey;
		this.rifiutoRappresentanteEmailBodyTemplateFileKey = rifiutoRappresentanteEmailBodyTemplateFileKey;
		this.richiestaRappresentanteEmailTitleKey = richiestaRappresentanteEmailTitleKey;
		this.richiestaRappresentanteEmailBodyTemplateFileKey = richiestaRappresentanteEmailBodyTemplateFileKey;
		this.responsabileProdottiEmailAddressKey = responsabileProdottiEmailAddressKey;
	}

	@Override
	@Transactional(rollbackFor = Exception.class, readOnly = true)
	public PagedData<RappresentanteEnteDTO> getRappresentantiEnte(RappresentanteEnteFilterDTO rappresentanteEnteFilter,
																  int pageNumber, int pageSize, String sortProperty, String sortDirection) {
		if (sortProperty == null) sortProperty = "dtRichiesta";
		if (sortDirection == null) sortDirection = "DESC";

		Pageable pageable = PagedData.buildPageable(pageNumber, pageSize, sortProperty, sortDirection);

		Page<RappresentanteEnte> page = rappresentanteEnteRepository.findAll(RepositoryUtils.buildRappresentanteEnteFilterSpecification(rappresentanteEnteFilter), pageable);

		PagedData<RappresentanteEnteDTO> pagedData = new PagedData<RappresentanteEnteDTO>(page, RappresentanteEnteDTO.class);
		return pagedData;
	}

	@Override
	@Transactional(rollbackFor = Exception.class, readOnly = false)
	public RappresentanteEnteDTO insertRappresentanteEnte(@Valid RappresentanteEnteDTO newRappresentanteEnte,
														  boolean notValidYet) throws Exception {
		RappresentanteEnte rappresentanteEnte = ObjectMapperUtils.map(newRappresentanteEnte, RappresentanteEnte.class);

		// add audit fields
		AuditWithValidityModel audit = (AuditWithValidityModel) rappresentanteEnte;
		AuditUtils.fillAudit(audit);

		fillDefaults(rappresentanteEnte);

		RappresentanteEnte oldRappresentante = getActiveRepresentative(rappresentanteEnte.getTipoRappr(), rappresentanteEnte.getEnte());

		// check if current user is already a representative
		if (oldRappresentante != null && oldRappresentante.getUtente().getIdUtente().equals(rappresentanteEnte.getUtente().getIdUtente())) {
			throw new MyExtranetValidationException(String.format("L'utente risulta già il rappresentante di tipo %s per l'ente", rappresentanteEnte.getTipoRappr()));
		}

		if (notValidYet) {
			// i.e. if called from frontoffice
			rappresentanteEnte.setFlgConferma(Constants.RAPPRESENTANTE_ENTE_FLG_CONFERMA_IN_ATTESA);
			rappresentanteEnte.setDtConferma(null);
			rappresentanteEnte.setDtInizioVal(null);
			rappresentanteEnte.setDtFineVal(null);
		} else {
			// i.e. if called from backoffice
			// since it is an operator to insert the record, it is automatically confirmed

			// invalidate any other active representative for the Ente
			if (oldRappresentante != null) {
				invalidateOthers(rappresentanteEnte.getTipoRappr(), rappresentanteEnte.getEnte());
			}

			rappresentanteEnte.setFlgConferma(Constants.RAPPRESENTANTE_ENTE_FLG_CONFERMA_CONFERMATA);
			rappresentanteEnte.setDtConferma(LocalDate.now());
			rappresentanteEnte.setDtInizioVal(Date.valueOf(LocalDate.now()));
			rappresentanteEnte.setDtFineVal(Date.valueOf(Constants.DEFAULT_DATE_END_VALIDITY));
		}
		rappresentanteEnte.setMotivConferma(null);

		// check if there is another record for the same utente already
		checkForExistingRecordForUtente(rappresentanteEnte);

		// check if there is a pending request by another user
		checkForExistingPendingRequestByOthers(rappresentanteEnte);

		// check if the utente if valid
		checkForValidUtente(rappresentanteEnte.getUtente());

		// check if the ente if valid
		checkForValidEnte(rappresentanteEnte.getEnte());		

		RappresentanteEnte savedRappresentanteEnte = rappresentanteEnteRepository.save(rappresentanteEnte);

		// invio email
		if (!notValidYet) {

			// send confirmation email
			sendEmailNewRappresentanteEnte(rappresentanteEnte, oldRappresentante);
		} else {
			// new request email
			sendEmailNewRequestRappresentanteEnte(rappresentanteEnte);
		}

		return ObjectMapperUtils.map(savedRappresentanteEnte, RappresentanteEnteDTO.class);

	}

	private void checkForValidEnte(Ente ente) throws Exception {
		if (ente == null) {
			throw new Exception("L'ente non è stato specificato");
		}

		Optional<Ente> storedEnteOptional = enteRepository.findById(ente.getIdEnte());
		if (storedEnteOptional.isPresent()) {
			Ente storedEnte = storedEnteOptional.get();

			LocalDate today = LocalDate.now();

			if ((storedEnte.getDtFineVal() != null && storedEnte.getDtFineVal().before(Date.valueOf(today)))
					|| (storedEnte.getDtInizioVal() != null && storedEnte.getDtInizioVal().after(Date.valueOf(today)))) {
				throw new Exception("Ente non valido");
			}
		} else {
			throw new Exception("Ente non trovato");
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

	private void checkForExistingRecordForUtente(RappresentanteEnte rappresentanteEnte) throws Exception {
		Utente utente = rappresentanteEnte.getUtente();
		Ente ente = rappresentanteEnte.getEnte();
		String tipoRappr = rappresentanteEnte.getTipoRappr();
		if (utente == null || ente == null || tipoRappr == null) {
			return;
		}

		List<RappresentanteEnte> records = rappresentanteEnteRepository.findAllCurrentByEnteAndUtenteAndTipoRappr(ente, utente, tipoRappr, Date.valueOf(LocalDate.now()));
		if (records.size() > 0) {
			throw new MyExtranetValidationException(String.format("L'utente ha già una richiesta pendente", tipoRappr));
		}	
	}

	private void checkForExistingPendingRequestByOthers(RappresentanteEnte rappresentanteEnte) throws Exception {
		Utente utente = rappresentanteEnte.getUtente();
		Ente ente = rappresentanteEnte.getEnte();
		String tipoRappr = rappresentanteEnte.getTipoRappr();
		if (utente == null || ente == null || tipoRappr == null) {
			return;
		}

		List<RappresentanteEnte> records = rappresentanteEnteRepository.findAllOthersPendingByEnteAndUtenteAndTipoRappr(ente, utente, tipoRappr);
		if (records.size() > 0) {
			throw new MyExtranetValidationException(String.format("Risulta già una richiesta pendente per rappresentante di tipo %s dell'ente", tipoRappr));
		}
	}

	private void fillDefaults(RappresentanteEnte rappresentanteEnte) {
		LocalDate today = LocalDate.now();
		rappresentanteEnte.setDtRichiesta(today);

		if (rappresentanteEnte.getFlgConferma() == null) {
			rappresentanteEnte.setFlgConferma(Constants.RAPPRESENTANTE_ENTE_FLG_CONFERMA_IN_ATTESA);
		}

	}

	@Override
	@Transactional(rollbackFor = Exception.class, readOnly = false)
	public RappresentanteEnteDTO updateRappresentanteEnte(@Valid RappresentanteEnteDTO patchRappresentanteEnte,
														  boolean isPatch, boolean isFrontoffice, boolean isResubmission) throws Exception {

		// retrieve original
		Optional<RappresentanteEnte> originalRappresentanteEnteOptional = rappresentanteEnteRepository.findById(patchRappresentanteEnte.getIdRappr());
		if (!originalRappresentanteEnteOptional.isPresent()) {
			throw new Exception("Rappresentante Ente not found");
		}
		RappresentanteEnte originalRappresentanteEnte = originalRappresentanteEnteOptional.get();

		RappresentanteEnte rappresentanteEnte = RappresentanteEnteDTO.patchRappresentanteEnte(patchRappresentanteEnte, originalRappresentanteEnte, isPatch, true, isFrontoffice);

		if (isFrontoffice && isResubmission) {
			if (originalRappresentanteEnte.getFlgConferma().equals(Constants.RAPPRESENTANTE_ENTE_FLG_CONFERMA_DA_RISOTTOMETTERE)) {
				rappresentanteEnte.setFlgConferma(Constants.RAPPRESENTANTE_ENTE_FLG_CONFERMA_IN_ATTESA);
			} else {
				throw new MyExtranetValidationException("La richiesta non è da risottomettere");
			}
		}

		// set audit fields
		AuditWithValidityModel audit = (AuditWithValidityModel) rappresentanteEnte;
		AuditUtils.fillAudit(audit);

		// check if confirming
		if (rappresentanteEnte.getDtConferma() == null && rappresentanteEnte.getFlgConferma().equals(Constants.RAPPRESENTANTE_ENTE_FLG_CONFERMA_CONFERMATA)) {
			LocalDate today = LocalDate.now();

			rappresentanteEnte.setDtConferma(today);
			rappresentanteEnte.setDtInizioVal(Date.valueOf(today));
			rappresentanteEnte.setDtFineVal(Date.valueOf(Constants.DEFAULT_DATE_END_VALIDITY));

			// invalidate any other active representative for the Ente
			RappresentanteEnte oldRappresentante = getActiveRepresentative(rappresentanteEnte.getTipoRappr(), rappresentanteEnte.getEnte());
			if (oldRappresentante != null) {
				invalidateOthers(rappresentanteEnte.getTipoRappr(), rappresentanteEnte.getEnte());
			}

			// send confirmation email
			sendEmailNewRappresentanteEnte(rappresentanteEnte, oldRappresentante);
		}
		// check if rejecting
		if (rappresentanteEnte.getDtConferma() == null && rappresentanteEnte.getFlgConferma().equals(Constants.RAPPRESENTANTE_ENTE_FLG_CONFERMA_RIFIUTATA)) {
			LocalDate today = LocalDate.now();

			rappresentanteEnte.setDtConferma(today);

			// send rejection email
			sendEmailRejectNewRappresentanteEnte(rappresentanteEnte);
		}

		RappresentanteEnte savedRappresentanteEnte = rappresentanteEnteRepository.save(rappresentanteEnte);

		return ObjectMapperUtils.map(savedRappresentanteEnte, RappresentanteEnteDTO.class);
	}

	private Messaggio sendEmailRejectNewRappresentanteEnte(RappresentanteEnte rappresentanteEnte) throws Exception {
		Ente ente = rappresentanteEnte.getEnte();

		// load Utente
		Utente utente = utenteRepository.getOne(rappresentanteEnte.getUtente().getIdUtente());

		// additional addresses
		String additionalAddresses = ";" + getProductsOfficialAddress();

		Messaggio messaggio = new Messaggio();
		messaggio.setArea(Constants.MESSAGGIO_AREA_PROGETTO);
		messaggio.setTipo(Constants.MESSAGGIO_TIPO_COMUNICAZIONE);
		messaggio.setDestinatario(String.format("%s %s", utente.getNome(), utente.getCognome()));
		messaggio.setIndirizzo(utente.getEmail() + additionalAddresses);

		Map<String, String> customAttributes = Map.of(
				"conferma.motivazione", rappresentanteEnte.getMotivConferma(),
				"rappresentante.nomeCognome", messaggio.getDestinatario()
		);

		StringSubstitutor substitutor = getPlaceholderSubstitutor(messaggio, ente, customAttributes);

		String rifiutoRappresentanteEmailTitle =
				myConfigService.getConcatValue(ipa + rifiutoRappresentanteEmailTitleKey, "");

		messaggio.setTitolo(substitutor.replace(rifiutoRappresentanteEmailTitle));
		String bodyTemplate =
				myConfigService.getConcatValue(ipa + rifiutoRappresentanteEmailBodyTemplateFileKey, "");
		messaggio.setMessaggio(substitutor.replace(bodyTemplate));

		// insert and send messaggio
		Messaggio savedMessaggio = messaggioService.insertAndSendMessaggio(messaggio, null);

		// link utente with messaggio
		Integer idMessaggio = savedMessaggio.getIdMessaggio();
		Integer[] utenteIds = { utente.getIdUtente() };

		utenteMessaggiService.linkUtentiWithMessaggio(utenteIds, idMessaggio);

		return savedMessaggio;
	}

	private Messaggio sendEmailNewRequestRappresentanteEnte(RappresentanteEnte rappresentanteEnte) throws Exception {
		Ente ente = rappresentanteEnte.getEnte();

		// load Utente
		Utente utente = utenteRepository.getOne(rappresentanteEnte.getUtente().getIdUtente());

		// additional addresses
		String additionalAddresses = ";" + getProductsOfficialAddress();


		Messaggio messaggio = new Messaggio();
		messaggio.setArea(Constants.MESSAGGIO_AREA_PROGETTO);
		messaggio.setTipo(Constants.MESSAGGIO_TIPO_COMUNICAZIONE);
		messaggio.setDestinatario(String.format("%s %s", utente.getNome(), utente.getCognome()));
		messaggio.setIndirizzo(utente.getEmail() + additionalAddresses);




		Map<String, String> customAttributes = Map.of(
				"conferma.motivazione", StringUtils.stringOrEmpty(rappresentanteEnte.getMotivConferma()),
				"rappresentante.nomeCognome", messaggio.getDestinatario()
		);

		// if ente entity is not filled, get it from DB
		if (ente != null && ente.getIdEnte() != null && ente.getDenominazione() == null) {
			ente = enteRepository.getOne(ente.getIdEnte());
		}

		StringSubstitutor substitutor = getPlaceholderSubstitutor(messaggio, ente, customAttributes);

		String richiestaRappresentanteEmailTitle =
				myConfigService.getConcatValue(ipa + richiestaRappresentanteEmailTitleKey, "");

		messaggio.setTitolo(substitutor.replace(richiestaRappresentanteEmailTitle));
		String bodyTemplate =
				myConfigService.getConcatValue(ipa + richiestaRappresentanteEmailBodyTemplateFileKey, "");
		messaggio.setMessaggio(substitutor.replace(bodyTemplate));

		// insert and send messaggio
		Messaggio savedMessaggio = messaggioService.insertAndSendMessaggio(messaggio, null);

		// link utente with messaggio
		Integer idMessaggio = savedMessaggio.getIdMessaggio();
		Integer[] utenteIds = { utente.getIdUtente() };

		utenteMessaggiService.linkUtentiWithMessaggio(utenteIds, idMessaggio);

		return savedMessaggio;
	}

	private Messaggio sendEmailNewRappresentanteEnte(RappresentanteEnte rappresentanteEnte, RappresentanteEnte oldRappresentante) throws Exception {
		Ente ente = rappresentanteEnte.getEnte();

		// load Utente
		Utente utente = utenteRepository.getOne(rappresentanteEnte.getUtente().getIdUtente());
		Utente oldUtente = oldRappresentante != null && oldRappresentante.getUtente() != null ? utenteRepository.getOne(oldRappresentante.getUtente().getIdUtente()) : null;

		// additional addresses
		String additionalAddresses = oldUtente != null ? ";" + oldUtente.getEmail() : "";
		additionalAddresses += ";" + getProductsOfficialAddress();

		String linkAreaProdotti =
				mySysConfigService.getConcatValue(ipa + Constants.MYSYSCONFIG_KEY_MYEXTDOMAIN, "");
		linkAreaProdotti = linkAreaProdotti + Constants.MYSYSCONFIG_KEY_MYEXT_AREAPRODOTTI;

		Messaggio messaggio = new Messaggio();
		messaggio.setArea(Constants.MESSAGGIO_AREA_PROGETTO);
		messaggio.setTipo(Constants.MESSAGGIO_TIPO_COMUNICAZIONE);
		messaggio.setDestinatario(String.format("%s %s", utente.getNome(), utente.getCognome()));
		messaggio.setIndirizzo(utente.getEmail() + additionalAddresses);

		Map<String, String> customAttributes = Map.of(
				"conferma.motivazione", StringUtils.stringOrEmpty(rappresentanteEnte.getMotivConferma()),
				"rappresentante.nomeCognome", messaggio.getDestinatario(),
				"prodotti.link", linkAreaProdotti
		);

		// if ente entity is not filled, get it from DB
		if (ente != null && ente.getIdEnte() != null && ente.getDenominazione() == null) {
			ente = enteRepository.getOne(ente.getIdEnte());
		}

		StringSubstitutor substitutor = getPlaceholderSubstitutor(messaggio, ente, customAttributes);

		String nuovoRappresentanteEmailTitle =
				myConfigService.getConcatValue(ipa + nuovoRappresentanteEmailTitleKey, "");

		messaggio.setTitolo(substitutor.replace(nuovoRappresentanteEmailTitle));
		String bodyTemplate =
				myConfigService.getConcatValue(ipa + nuovoRappresentanteEmailBodyTemplateFileKey, "");
		messaggio.setMessaggio(substitutor.replace(bodyTemplate));

		// insert and send messaggio
		Messaggio savedMessaggio = messaggioService.insertAndSendMessaggio(messaggio, null);

		// link utente with messaggio
		Integer idMessaggio = savedMessaggio.getIdMessaggio();
		Integer[] utenteIds;
		if (oldUtente != null) {
			utenteIds = new Integer[2];
			utenteIds[1] = oldUtente.getIdUtente();
		} else {
			utenteIds = new Integer[1];
		}
		utenteIds[0] = utente.getIdUtente();

		utenteMessaggiService.linkUtentiWithMessaggio(utenteIds, idMessaggio);

		return savedMessaggio;

	}

	private StringSubstitutor getPlaceholderSubstitutor(Messaggio messaggio, Ente ente,
														Map<String, String> customValuesMap) {

		Map<String, String> valuesMap = new HashMap();
		valuesMap.putAll(Map.of(
				"ente.denominazione", it.regioneveneto.myp3.myextranet.utils.StringUtils.stringOrEmpty(ente.getDenominazione()),
				"messaggio.destinatario", it.regioneveneto.myp3.myextranet.utils.StringUtils.stringOrEmpty(messaggio.getDestinatario())
		));

		if (customValuesMap != null) {
			valuesMap.putAll(customValuesMap);
		}

		return new StringSubstitutor(valuesMap);
	}

	private String getProductsOfficialAddress() {
		return myConfigService.getConcatValue(ipa + responsabileProdottiEmailAddressKey, "");
	}

	private void invalidateOthers(String tipoRappr, Ente ente) {

		rappresentanteEnteRepository.invalidateActiveRepresentatives(tipoRappr, ente, Date.valueOf(LocalDate.now()));

	}

	private RappresentanteEnte getActiveRepresentative(String tipoRappr, Ente ente) {

		RappresentanteEnte oldRappresentante = null;

		List<RappresentanteEnte> listaRappresentanti = rappresentanteEnteRepository.findAllActiveRepresentatives(tipoRappr, ente, Date.valueOf(LocalDate.now()));
		// there should be only one in theory
		if (listaRappresentanti != null && listaRappresentanti.size() > 0) {
			oldRappresentante = listaRappresentanti.get(0);
		}

		return oldRappresentante;
	}

	@Override
	@Transactional(rollbackFor = Exception.class, readOnly = true)
	public RappresentanteEnteDTO getRappresentanteEnte(Integer idRappresentante) throws Exception {
		Optional<RappresentanteEnte> rappresentanteEnteOptional = rappresentanteEnteRepository.findById(idRappresentante);

		if(rappresentanteEnteOptional.isPresent()) {
			RappresentanteEnteDTO rappresentanteEnte = ObjectMapperUtils.map(rappresentanteEnteOptional.get(), RappresentanteEnteDTO.class);
			return rappresentanteEnte;
		} else {
			throw new Exception("RappresentanteEnte not found");
		}
	}

	@Override
	public boolean isRappresentante(String codiceFiscale, Integer idEnte) {

		List<RappresentanteEnte> found = rappresentanteEnteRepository.findAllActiveByEnteAndUtente(idEnte, codiceFiscale, Constants.RAPPRESENTANTE_ENTE_TIPO_RAP, Date.valueOf(LocalDate.now()));

		return found != null && found.size() > 0;
	}

	@Override
	@Transactional(rollbackFor = Exception.class, readOnly = true)
	public PagedData<RappresentanteEnteDTO> getEntiRappresentati(UserWithAdditionalInfo user, Integer pageNumber,
																 Integer pageSize, String sortProperty, String sortDirection) {
		Sort sort;
		if (sortProperty == null) {
			// default sorting is as follows: ente.idEnte ASC, dtRichiesta DESC, flagConferma ASC
			List<Sort.Order> orders = new ArrayList<Sort.Order>(3);
			orders.add(Order.asc("ente.idEnte"));
			orders.add(Order.desc("dtRichiesta"));
			orders.add(Order.asc("flgConferma"));
			sort = Sort.by(orders);
		} else {
			sort = Sort.by(Direction.fromString(sortDirection), sortProperty);
		}

		// force one large page
		pageNumber = 0;

		Pageable pageable = PagedData.buildPageable(pageNumber, pageSize, sort);

		Map<String, Object> userValidityProperties = utenteService.getUserValidityProperties(user.getCodiceFiscale());
		Integer userId = (Integer) userValidityProperties.get("userId");

		Page<RappresentanteEnte> page = rappresentanteEnteRepository.findAll(RepositoryUtils.buildEntiRappresentatiUtenteSpecification(userId), pageable);

		List<RappresentanteEnteDTO> preparedList = prepareRappresentantiList(page.getContent());

		PagedData<RappresentanteEnteDTO> pagedData = new PagedData<RappresentanteEnteDTO>(
				preparedList,
				preparedList.size(),
				page.getNumber() + 1,
				page.getNumberOfElements()
		);
		return pagedData;

	}

	private List<RappresentanteEnteDTO> prepareRappresentantiList(List<RappresentanteEnte> rappresentanti) {
		List<RappresentanteEnteDTO> result = new ArrayList<RappresentanteEnteDTO>();

		Date today = Date.valueOf(LocalDate.now());

		Integer currentIdEnte = -1;
		for (RappresentanteEnte rappresentante : rappresentanti) {
			Integer idEnte = rappresentante.getEnte().getIdEnte();
			if (currentIdEnte.equals(idEnte)) {
				continue;
			}
			currentIdEnte = idEnte;

			boolean isValid =
					rappresentante.getDtInizioVal() != null && rappresentante.getDtFineVal() != null &&
							rappresentante.getDtInizioVal().compareTo(today) <= 0 &&
							rappresentante.getDtFineVal().compareTo(today) > 0;


			RappresentanteEnteDTO item = ObjectMapperUtils.map(rappresentante, RappresentanteEnteDTO.class);

			item.setFlgAttivo(Constants.RAPPRESENTANTE_ENTE_FLG_CONFERMA_CONFERMATA.equals(rappresentante.getFlgConferma()) && isValid);

			result.add(item);
		}

		return result;
	}

	@Override
	public List<RappresentanteEnteDTO> prepareRappresentantiListDTO(List<RappresentanteEnteDTO> rappresentanti) {
		List<RappresentanteEnteDTO> result = new ArrayList<RappresentanteEnteDTO>();

		Date today = Date.valueOf(LocalDate.now());
		for (RappresentanteEnteDTO rappresentante : rappresentanti) {


			boolean isValid =
					rappresentante.getDtInizioVal() != null && rappresentante.getDtFineVal() != null &&
							rappresentante.getDtInizioVal().compareTo(today) <= 0 &&
							rappresentante.getDtFineVal().compareTo(today) > 0;


			rappresentante.setFlgAttivo(Constants.RAPPRESENTANTE_ENTE_FLG_CONFERMA_CONFERMATA.equals(rappresentante.getFlgConferma()) && isValid);

			result.add(rappresentante);
		}

		return result;
	}

	@Override
	public RappresentanteEnteDTO setFlgAttivoOnRAP(RappresentanteEnteDTO rappresentante) {

		Date today = Date.valueOf(LocalDate.now());

		boolean isValid =
				rappresentante.getDtInizioVal() != null && rappresentante.getDtFineVal() != null &&
						rappresentante.getDtInizioVal().compareTo(today) <= 0 &&
						rappresentante.getDtFineVal().compareTo(today) > 0;

		rappresentante.setFlgAttivo(Constants.RAPPRESENTANTE_ENTE_FLG_CONFERMA_CONFERMATA.equals(rappresentante.getFlgConferma()) && isValid);

		return rappresentante;
	}

}

