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
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collector;
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
import org.springframework.util.StringUtils;

import it.regioneveneto.myp3.myextranet.bean.IStatEntiProdottiRow;
import it.regioneveneto.myp3.myextranet.bean.IStatServiziErogatiRow;
import it.regioneveneto.myp3.myextranet.bean.StatEntiProdottiRow;
import it.regioneveneto.myp3.myextranet.bean.StatServiziErogatiRow;
import it.regioneveneto.myp3.myextranet.exception.MyExtranetException;
import it.regioneveneto.myp3.myextranet.exception.MyExtranetMissingOperationException;
import it.regioneveneto.myp3.myextranet.exception.MyExtranetValidationException;
import it.regioneveneto.myp3.myextranet.model.AuditModel;
import it.regioneveneto.myp3.myextranet.model.AuditWithValidityModel;
import it.regioneveneto.myp3.myextranet.model.DocumentoRichiestaProdotto;
import it.regioneveneto.myp3.myextranet.model.Ente;
import it.regioneveneto.myp3.myextranet.model.Messaggio;
import it.regioneveneto.myp3.myextranet.model.MyExtranetContent;
import it.regioneveneto.myp3.myextranet.model.ProcedimentoProdotto;
import it.regioneveneto.myp3.myextranet.model.ProdottoAttivabile;
import it.regioneveneto.myp3.myextranet.model.ProdottoAttivato;
import it.regioneveneto.myp3.myextranet.model.RappresentanteEnte;
import it.regioneveneto.myp3.myextranet.model.RichiestaProdotto;
import it.regioneveneto.myp3.myextranet.model.RuoloProdotto;
import it.regioneveneto.myp3.myextranet.model.StepProcedimentoProdotto;
import it.regioneveneto.myp3.myextranet.model.StepRichiestaProdotto;
import it.regioneveneto.myp3.myextranet.model.TipoRichiestaProdotto;
import it.regioneveneto.myp3.myextranet.model.Utente;
import it.regioneveneto.myp3.myextranet.model.UtenteProdottoAttivato;
import it.regioneveneto.myp3.myextranet.model.UtenteRichiestaProdotto;
import it.regioneveneto.myp3.myextranet.repository.DocumentoRichiestaProdottoRepository;
import it.regioneveneto.myp3.myextranet.repository.EnteRepository;
import it.regioneveneto.myp3.myextranet.repository.ProcedimentoProdottoRepository;
import it.regioneveneto.myp3.myextranet.repository.ProdottoAttivabileRepository;
import it.regioneveneto.myp3.myextranet.repository.ProdottoAttivatoRepository;
import it.regioneveneto.myp3.myextranet.repository.RappresentanteEnteRepository;
import it.regioneveneto.myp3.myextranet.repository.RichiestaProdottoRepository;
import it.regioneveneto.myp3.myextranet.repository.StepProcedimentoProdottoRepository;
import it.regioneveneto.myp3.myextranet.repository.StepRichiestaProdottoRepository;
import it.regioneveneto.myp3.myextranet.repository.TipoRichiestaProdottoRepository;
import it.regioneveneto.myp3.myextranet.repository.UtenteProdottoAttivatoRepository;
import it.regioneveneto.myp3.myextranet.repository.UtenteRepository;
import it.regioneveneto.myp3.myextranet.repository.UtenteRichiestaProdottoRepository;
import it.regioneveneto.myp3.myextranet.service.MessaggioService;
import it.regioneveneto.myp3.myextranet.service.MyConfigService;
import it.regioneveneto.myp3.myextranet.service.MyPortalService;
import it.regioneveneto.myp3.myextranet.service.ProcedimentoProdottoService;
import it.regioneveneto.myp3.myextranet.service.ProdottoAttivatoService;
import it.regioneveneto.myp3.myextranet.service.RichiestaProdottoService;
import it.regioneveneto.myp3.myextranet.service.UtenteMessaggiService;
import it.regioneveneto.myp3.myextranet.utils.AuditUtils;
import it.regioneveneto.myp3.myextranet.utils.Constants;
import it.regioneveneto.myp3.myextranet.utils.ObjectMapperUtils;
import it.regioneveneto.myp3.myextranet.utils.RepositoryUtils;
import it.regioneveneto.myp3.myextranet.web.dto.DatiRichiestaProdottoAttivatoDTO;
import it.regioneveneto.myp3.myextranet.web.dto.DocumentoRichiestaProdottoDTO;
import it.regioneveneto.myp3.myextranet.web.dto.ExtendedGenericAvanzamentoStepRichiestaInputDTO;
import it.regioneveneto.myp3.myextranet.web.dto.GenericAvanzamentoStepRichiestaInputDTO;
import it.regioneveneto.myp3.myextranet.web.dto.MessaggioDTO;
import it.regioneveneto.myp3.myextranet.web.dto.OperazioneDocumentoRichiestaProdottoDTO;
import it.regioneveneto.myp3.myextranet.web.dto.OperazioneUtenteRichiestaProdottoDTO;
import it.regioneveneto.myp3.myextranet.web.dto.PagedData;
import it.regioneveneto.myp3.myextranet.web.dto.ProcedimentoProdottoWithStepsDTO;
import it.regioneveneto.myp3.myextranet.web.dto.ProdottoAttivabileEnteDTO;
import it.regioneveneto.myp3.myextranet.web.dto.ProdottoAttivabileEnteFilterDTO;
import it.regioneveneto.myp3.myextranet.web.dto.ProdottoAttivabileFilterDTO;
import it.regioneveneto.myp3.myextranet.web.dto.ProdottoAttivatoComunicazioneFilterDTO;
import it.regioneveneto.myp3.myextranet.web.dto.ProdottoAttivatoDTO;
import it.regioneveneto.myp3.myextranet.web.dto.ProdottoAttivatoFilterDTO;
import it.regioneveneto.myp3.myextranet.web.dto.ProdottoAttivatoWithStatusDTO;
import it.regioneveneto.myp3.myextranet.web.dto.ProdottoAttivatoWithUsersDTO;
import it.regioneveneto.myp3.myextranet.web.dto.RichiestaProdottoWithStepsDTO;
import it.regioneveneto.myp3.myextranet.web.dto.StepDescDTO;
import it.regioneveneto.myp3.myextranet.web.dto.StepProcedimentoProdottoDTO;
import it.regioneveneto.myp3.myextranet.web.dto.UtenteProdottoAttivatoDTO;
import it.regioneveneto.myp3.myextranet.web.dto.UtenteRichiestaProdottoDTO;

@Service
public class ProdottoAttivatoServiceImpl implements ProdottoAttivatoService {
    private static final Logger LOG = LoggerFactory.getLogger(ProdottoAttivatoServiceImpl.class);

    @Autowired
    ProdottoAttivatoRepository prodottoAttivatoRepository;   
    
    @Autowired
    ProdottoAttivabileRepository prodottoAttivabileRepository;
    
    @Autowired
    RichiestaProdottoRepository richiestaProdottoRepository;
    
    @Autowired
    StepRichiestaProdottoRepository stepRichiestaProdottoRepository;
    
    @Autowired
    UtenteRichiestaProdottoRepository utenteRichiestaProdottoRepository;
    
    @Autowired
    DocumentoRichiestaProdottoRepository documentoRichiestaProdottoRepository;
    
    @Autowired
    ProcedimentoProdottoRepository procedimentoProdottoRepository;
    
    @Autowired
    StepProcedimentoProdottoRepository stepProcedimentoProdottoRepository;
    
    @Autowired
    UtenteProdottoAttivatoRepository utenteProdottoAttivatoRepository;
    
    @Autowired
    RappresentanteEnteRepository rappresentanteEnteRepository;
    
    @Autowired
    EnteRepository enteRepository;
    
    @Autowired
    UtenteRepository utenteRepository;
    
    @Autowired
    TipoRichiestaProdottoRepository tipoRichiestaProdottoRepository;
    
    @Autowired
    RichiestaProdottoService richiestaProdottoService;
    
    @Autowired
    UtenteMessaggiService utenteMessaggiService;
    
    @Autowired
    MessaggioService messaggioService;
    
    @Autowired
    MyConfigService myConfigService;
    
    @Autowired
    MyPortalService myPortalService;
    
    @Autowired
    ProcedimentoProdottoService procedimentoProdottoService;
        
	private final String ipa;
	
    private final String stepRichiestaProdottoEmailTitleKey;
    private final String stepRichiestaProdottoEmailBodyTemplateFileKey;
	private final String responsabileProdottiEmailAddressKey;

	public ProdottoAttivatoServiceImpl(
			@Value("${myportal.ipa}") String ipa,
			@Value("${mychannel.messages.stepRichiestaProdotto.myConfigKeys.emailTitle}") String stepRichiestaProdottoEmailTitleKey,
			@Value("${mychannel.messages.stepRichiestaProdotto.myConfigKeys.emailBodyTemplateFile}") String stepRichiestaProdottoEmailBodyTemplateFileKey,
			@Value("${mychannel.addresses.responsabileProdotti.myConfigKeys.emailAddress}") String responsabileProdottiEmailAddressKey
			) {
		super();
		this.ipa = ipa;
		this.stepRichiestaProdottoEmailTitleKey = stepRichiestaProdottoEmailTitleKey;
		this.stepRichiestaProdottoEmailBodyTemplateFileKey = stepRichiestaProdottoEmailBodyTemplateFileKey;
		this.responsabileProdottiEmailAddressKey = responsabileProdottiEmailAddressKey;
	}

	@Override
	@Transactional(rollbackFor = Exception.class, readOnly = true)
	public PagedData<ProdottoAttivatoWithStatusDTO> getProdottiAttivati(ProdottoAttivatoFilterDTO prodottoAttivatoFilter, int pageNumber,
			int pageSize, String sortProperty, String sortDirection) {
		if (sortProperty == null) sortProperty = "dtInizioVal";
		if (sortDirection == null) sortDirection = "ASC";
		
		Pageable pageable = PagedData.buildPageable(pageNumber, pageSize, sortProperty, sortDirection);
		
		List<Integer> withPendingRequestsIds = null;
		if (prodottoAttivatoFilter.getWithPendingRequests() != null) {
			withPendingRequestsIds = richiestaProdottoRepository.getIdAttivazioneByFlgFineRich(0);
		}
		
		List<Integer> filteredByStateIds = null;
		if (prodottoAttivatoFilter.getStato() != null) {
			Integer filterStato = prodottoAttivatoFilter.getStato();
			if (Constants.PRODOTTO_ATTIVATO_STATO_DA_ATTIVARE.equals(filterStato)) {
				filteredByStateIds = prodottoAttivatoRepository.getIdAttivazioneNotValidWithNoPendingRequests();
			} else if (Constants.PRODOTTO_ATTIVATO_STATO_IN_FASE_DI_ATTIVAZIONE.equals(filterStato)) {
				filteredByStateIds = prodottoAttivatoRepository.getIdAttivazioneNotValidWithPendingRequests();
			} else if (Constants.PRODOTTO_ATTIVATO_STATO_ATTIVATO.equals(filterStato)) {
				filteredByStateIds = prodottoAttivatoRepository.getIdAttivazioneValid();
			}
		}
		
		Page<ProdottoAttivato> page = prodottoAttivatoRepository
				.findAll(RepositoryUtils.buildProdottoAttivatoFilterSpecification(prodottoAttivatoFilter, withPendingRequestsIds, filteredByStateIds), pageable);

		PagedData<ProdottoAttivatoWithStatusDTO> pagedData = new PagedData<ProdottoAttivatoWithStatusDTO>(page, ProdottoAttivatoWithStatusDTO.class);
		
		// compute numRich and stato
		populateExtraFields(pagedData.getRecords());
		
		return pagedData;

	}
	
	private void populateExtraFields(List<ProdottoAttivatoWithStatusDTO> records) {
		for (ProdottoAttivatoWithStatusDTO prodottoAttivato : records) {
			// numRich
			setStatusOnProdottoAttivato(prodottoAttivato);
			
		}
		
	}
	
	private void setStatusOnProdottoAttivato(ProdottoAttivatoWithStatusDTO prodottoAttivato) {
		Integer numRich = richiestaProdottoRepository.countAllByProdottoAttivatoIdAttivazioneAndFlgFineRich(prodottoAttivato.getIdAttivazione(), 0);
		prodottoAttivato.setNumRich(numRich);
		
		// stato
		if (!prodottoAttivato.isValid() && numRich.equals(0)) {
			prodottoAttivato.setStato(Constants.PRODOTTO_ATTIVATO_STATO_DA_ATTIVARE); 
		} else if(!prodottoAttivato.isValid() && numRich.equals(1)) {
			prodottoAttivato.setStato(Constants.PRODOTTO_ATTIVATO_STATO_IN_FASE_DI_ATTIVAZIONE); 
		} else if (prodottoAttivato.isValid()) {
			prodottoAttivato.setStato(Constants.PRODOTTO_ATTIVATO_STATO_ATTIVATO); 
		}
		
	}

	@Override
	@Transactional(rollbackFor = Exception.class, readOnly = true)
	public ProdottoAttivatoDTO getProdottoAttivato(Integer idAttivazione) throws Exception {
								
		Optional<ProdottoAttivato> prodottoAttivatoOptional = prodottoAttivatoRepository.findById(idAttivazione);

		if(prodottoAttivatoOptional.isPresent()) {
			ProdottoAttivatoDTO prodottoAttivato = ObjectMapperUtils.map(prodottoAttivatoOptional.get(), ProdottoAttivatoDTO.class);
			return prodottoAttivato;
		} else {
			throw new Exception("ProdottoAttivato not found");
		}

	}
	
	@Override
	@Transactional(rollbackFor = Exception.class, readOnly = true)
	public ProdottoAttivatoWithStatusDTO getProdottoAttivatoWithStatus(Integer idAttivazione) throws Exception {
								
		Optional<ProdottoAttivato> prodottoAttivatoOptional = prodottoAttivatoRepository.findById(idAttivazione);

		if(prodottoAttivatoOptional.isPresent()) {
			ProdottoAttivatoWithStatusDTO prodottoAttivato = ObjectMapperUtils.map(prodottoAttivatoOptional.get(), ProdottoAttivatoWithStatusDTO.class);
			setStatusOnProdottoAttivato(prodottoAttivato);
			return prodottoAttivato;
		} else {
			throw new Exception("ProdottoAttivato not found");
		}

	}
	
	
	@Override
	@Transactional(rollbackFor = Exception.class, readOnly = true)
	public DatiRichiestaProdottoAttivatoDTO getDatiRichiestaProdottoAttivato(Integer idEnte, Integer idProdottoAtt,
			String codTipoRich) throws Exception {
		
		DatiRichiestaProdottoAttivatoDTO result = new DatiRichiestaProdottoAttivatoDTO();
		
		List<ProdottoAttivato> prodottiAttivati = prodottoAttivatoRepository.findAllValidByProdottoAttivabileIdProdottoAttAndEnteIdEnte(idProdottoAtt, idEnte, Date.valueOf(LocalDate.now()));
		
		ProdottoAttivato prodottoAttivato = null;
		boolean prodottoAttivo = false;
		Integer idAttivazione = null;
		
		if (prodottiAttivati != null && prodottiAttivati.size() > 0) {
			prodottoAttivato = prodottiAttivati.get(0);
			prodottoAttivo = true;
			idAttivazione = prodottoAttivato.getIdAttivazione();
			
		} else {
			// find any Richiesta Prodotto not associated with valid ProdottoAttivato
			
			List<RichiestaProdotto> richiesteProdotto = richiestaProdottoRepository.findAllByProdottoAttivatoProdottoAttivabileIdProdottoAttAndProdottoAttivatoEnteIdEnteAndFlgFineRich(idProdottoAtt, idEnte, 0);
			if (richiesteProdotto != null && richiesteProdotto.size() > 0) {
				idAttivazione = richiesteProdotto.get(0).getProdottoAttivato().getIdAttivazione();
			}
		}
		
		// invoke /frontoffice/prodotto-attivato/{id}
		
		if (idAttivazione != null) {
			ProdottoAttivatoWithUsersDTO prodottoAttivatoWithUsers = getProdottoAttivatoWithUsers(idAttivazione);
			
			RichiestaProdottoWithStepsDTO richiestaProdottoWithSteps = prodottoAttivatoWithUsers.getRichiestaProdottoWithStep();
			result.setDatiRichiesta(richiestaProdottoWithSteps);
			
			if (prodottoAttivo) {
				result.setProdottoAttivato(prodottoAttivatoWithUsers);
				
				boolean hasRequest = result.getDatiRichiesta() != null;
				boolean isValid = prodottoAttivatoWithUsers.isValid(); 
				
				// stato
				if (!isValid && !hasRequest) {
					prodottoAttivatoWithUsers.setStato(Constants.PRODOTTO_ATTIVATO_STATO_DA_ATTIVARE); 
				} else if (!isValid && hasRequest) {
					prodottoAttivatoWithUsers.setStato(Constants.PRODOTTO_ATTIVATO_STATO_IN_FASE_DI_ATTIVAZIONE); 
				} else if (isValid) {
					prodottoAttivatoWithUsers.setStato(Constants.PRODOTTO_ATTIVATO_STATO_ATTIVATO); 
				}
			}
		}
		
		ProcedimentoProdottoWithStepsDTO datiProcedimento = null;
		List<StepProcedimentoProdottoDTO> steps = null;
		if (result.getDatiRichiesta() != null) {
			StepProcedimentoProdottoDTO stepProcedimentoProdotto = result.getDatiRichiesta().getStepProcedimentoProdotto();
			
			datiProcedimento = ObjectMapperUtils.map(stepProcedimentoProdotto.getProcedimentoProdotto(), ProcedimentoProdottoWithStepsDTO.class);
			
			steps = new ArrayList<StepProcedimentoProdottoDTO>(1);
			steps.add(stepProcedimentoProdotto);
			
			datiProcedimento.setStep(steps);
			
		} else {
			
			if (codTipoRich != null) {				
				datiProcedimento = procedimentoProdottoService.getProcedimentoProdottoPrimoStep(idProdottoAtt, codTipoRich);
				if (datiProcedimento == null) {
					LOG.error("ProdottoAttivatoService: non è stato trovato un procedimento valido per avviare la richiesta");
					throw new MyExtranetMissingOperationException("Non è possibile avviare la richiesta per mancanza di configurazione");
				}
			}
		}
		

		if (datiProcedimento != null) {			
			Integer idProdottoProc = datiProcedimento.getIdProdottoProc();
			List<StepProcedimentoProdotto> stepProcedimento = stepProcedimentoProdottoRepository.getAllByProcedimentoProdottoIdProdottoProcAndNotFlgFineRichOrderByNumStepAsc(idProdottoProc, 2);
			List<StepDescDTO> descStep = ObjectMapperUtils.mapAll(stepProcedimento, StepDescDTO.class);
			datiProcedimento.setDescStep(descStep);
		}
		
		result.setDatiProcedimento(datiProcedimento);
		
		return result;
	}

	@Override
	@Transactional(rollbackFor = Exception.class, readOnly = true)
	public ProdottoAttivatoWithUsersDTO getProdottoAttivatoWithUsers(Integer idAttivazione) throws Exception {
		Optional<ProdottoAttivato> prodottoAttivatoOptional = prodottoAttivatoRepository.findById(idAttivazione);

		if(prodottoAttivatoOptional.isPresent()) {
			ProdottoAttivatoWithUsersDTO prodottoAttivato = ObjectMapperUtils.map(prodottoAttivatoOptional.get(), ProdottoAttivatoWithUsersDTO.class);
			
			populateLists(prodottoAttivato);
			
			// get richiesta prodotto
			List<Integer> idProdAttivRichList = richiestaProdottoRepository.getIdProdAttivRichByIdAttivazioneAndFlgFineRich(idAttivazione, 0);
			if (idProdAttivRichList != null && idProdAttivRichList.size() > 0) {
				Integer idProdAttivRich = idProdAttivRichList.get(0);
				
				RichiestaProdottoWithStepsDTO richiestaProdotto = richiestaProdottoService.getRichiestaProdottoWithSteps(idProdAttivRich);
				prodottoAttivato.setRichiestaProdottoWithStep(richiestaProdotto);
			}
			
			return prodottoAttivato;
		} else {
			throw new Exception("ProdottoAttivato not found");
		}
	}

	private void populateLists(ProdottoAttivatoWithUsersDTO prodottoAttivato) {
		List<UtenteProdottoAttivato> users = utenteProdottoAttivatoRepository.findAllByProdottoAttivatoIdAttivazioneAndFlgEnabledOrderByUtenteCognomeAscQuery(prodottoAttivato.getIdAttivazione(), 1);
		
		prodottoAttivato.setUtenteProdottoAttivatoList(ObjectMapperUtils.mapAll(users, UtenteProdottoAttivatoDTO.class));
	}

	@Override
	@Transactional(rollbackFor = Exception.class, readOnly = false)
	public ProdottoAttivatoDTO insertProdottoAttivato(@Valid ProdottoAttivatoDTO newProdottoAttivato) throws Exception {
		
		ProdottoAttivato prodottoAttivato = ObjectMapperUtils.map(newProdottoAttivato, ProdottoAttivato.class);
		
		// add audit fields
		AuditWithValidityModel audit = (AuditWithValidityModel) prodottoAttivato;
		AuditUtils.fillAudit(audit);
		
		fillDefaults(prodottoAttivato);
				
		// check if there is another record for the same prodotto and ente
		checkForExistingRecord(prodottoAttivato);
		
		ProdottoAttivato savedProdottoAttivato = prodottoAttivatoRepository.save(prodottoAttivato);
		
		return ObjectMapperUtils.map(savedProdottoAttivato, ProdottoAttivatoDTO.class);
	}

	private void checkForExistingRecord(ProdottoAttivato prodottoAttivato) throws Exception {
		ProdottoAttivabile prodottoAttivabile = prodottoAttivato.getProdottoAttivabile();
		Ente ente = prodottoAttivato.getEnte();
		
		List<ProdottoAttivato> records = prodottoAttivatoRepository.findAllCurrentByProdottoAttivabileAndEnte(prodottoAttivabile, ente);
		if (records != null && records.size() > 0) {
			throw new Exception("Prodotto already present");
		}
	}

	private void fillDefaults(ProdottoAttivato prodottoAttivato) {
		
		if (prodottoAttivato.getDtInizioVal() == null) {
			prodottoAttivato.setDtInizioVal(Date.valueOf(Constants.DEFAULT_DATE_END_VALIDITY));
		}
		
	}

	@Override
	@Transactional(rollbackFor = Exception.class, readOnly = false)
	public ProdottoAttivatoDTO updateProdottoAttivato(ProdottoAttivatoDTO patchProdottoAttivato, boolean isPatch) throws Exception {
		
		// retrieve original
		Optional<ProdottoAttivato> originalProdottoAttivatoOptional = prodottoAttivatoRepository.findById(patchProdottoAttivato.getIdAttivazione());
		if (!originalProdottoAttivatoOptional.isPresent()) {
			throw new Exception("Prodotto attivato not found");
		}
		ProdottoAttivato originalProdottoAttivato = originalProdottoAttivatoOptional.get();		

		ProdottoAttivato prodottoAttivato = ProdottoAttivatoDTO.patchProdottoAttivato(patchProdottoAttivato, originalProdottoAttivato, isPatch, true);
		
		// set audit fields
		AuditModel audit = (AuditModel) prodottoAttivato;
		AuditUtils.fillAudit(audit);
		
				
		ProdottoAttivato savedProdottoAttivato = prodottoAttivatoRepository.save(prodottoAttivato);
		
		return ObjectMapperUtils.map(savedProdottoAttivato, ProdottoAttivatoDTO.class);

	}

	@Override
	@Transactional(rollbackFor = Exception.class, readOnly = true)
	public PagedData<ProdottoAttivabileEnteDTO> getProdottiAttivabiliEnte(
			ProdottoAttivabileEnteFilterDTO prodottoAttivabileEnteFilter, Integer pageNumber, Integer pageSize,
			String sortProperty, String sortDirection) {
		if (sortProperty == null) sortProperty = "dtAttivabileDa";
		if (sortDirection == null) sortDirection = "ASC";
		
		Pageable pageable = PagedData.buildPageable(pageNumber, pageSize, sortProperty, sortDirection);
		
		// get ids of Prodotti Attivabili with valid Prodotti Attivati
		List<Integer> idsWithValidProdottoAttivato = prodottoAttivatoRepository.getIdProdottoAttListForValidByIdEnte(prodottoAttivabileEnteFilter.getIdEnte());
		
		// get ids of Prodotti Attivabili with open requests
		List<Integer> idsWithOpenRequest = richiestaProdottoRepository.getIdProdottoAttListForOpenByIdEnte(prodottoAttivabileEnteFilter.getIdEnte());
		
		idsWithValidProdottoAttivato.addAll(idsWithOpenRequest);
		List<Integer> listOfIdsToInclude = idsWithValidProdottoAttivato;
		
		// get prodotti attivabili
		ProdottoAttivabileFilterDTO prodottoAttivabileFilter = new ProdottoAttivabileFilterDTO();
		prodottoAttivabileFilter.setIsAttivabileOrIn(listOfIdsToInclude);
		Page<ProdottoAttivabile> page = prodottoAttivabileRepository.findAll(RepositoryUtils.buildProdottoAttivabileFilterSpecification(prodottoAttivabileFilter), pageable);
		
		List<ProdottoAttivabileEnteDTO> prodottiAttivabiliEnte = ObjectMapperUtils.mapAll(page.getContent(), ProdottoAttivabileEnteDTO.class);
		
		Ente ente = new Ente();
		ente.setIdEnte(prodottoAttivabileEnteFilter.getIdEnte());
		
		List<ProdottoAttivato> prodottiAttivatiEnte = prodottoAttivatoRepository.findAllByEnte(ente);
		
		// integrate data of activated products
		for (ProdottoAttivabileEnteDTO prodottoAttivabile : prodottiAttivabiliEnte) {
			
			Optional<ProdottoAttivato> prodottoAttivatoOptional = prodottiAttivatiEnte.stream()
				.filter(p -> p.getProdottoAttivabile().getIdProdottoAtt().equals(prodottoAttivabile.getIdProdottoAtt()))
				.sorted(new Comparator<ProdottoAttivato>() {

					@Override
					public int compare(ProdottoAttivato arg0, ProdottoAttivato arg1) {
						
						// sort by dtIns desc
						int comp = arg1.getDtIns().compareTo(arg0.getDtIns());
						
						if (comp == 0) {
							// sort by idAttivazione desc
							comp = arg1.getIdAttivazione().compareTo(arg0.getIdAttivazione());
						}
						
						return comp;
					}
				})
				.findFirst();
			
			if (prodottoAttivatoOptional.isPresent()) {
				ProdottoAttivato prodottoAttivato = prodottoAttivatoOptional.get();
				
				prodottoAttivabile.setIdAttivazione(prodottoAttivato.getIdAttivazione());
				prodottoAttivabile.setIdEnte(prodottoAttivato.getEnte().getIdEnte());
				prodottoAttivabile.setDtInizioVal(prodottoAttivato.getDtInizioVal() != null ? prodottoAttivato.getDtInizioVal().toLocalDate() : null);
				prodottoAttivabile.setDtFineVal(prodottoAttivato.getDtFineVal() != null ? prodottoAttivato.getDtFineVal().toLocalDate() : null);
				
				// numRich
				Integer numRich = richiestaProdottoRepository.countAllByProdottoAttivatoIdAttivazioneAndFlgFineRich(prodottoAttivato.getIdAttivazione(), 0);
				prodottoAttivabile.setNumRich(numRich);
				
				// stato
				if (!isValid(prodottoAttivato) && numRich.equals(0)) {
					prodottoAttivabile.setStato(Constants.PRODOTTO_ATTIVATO_STATO_DA_ATTIVARE); 
				} else if(!isValid(prodottoAttivato) && numRich.equals(1)) {
					prodottoAttivabile.setStato(Constants.PRODOTTO_ATTIVATO_STATO_IN_FASE_DI_ATTIVAZIONE); 
				} else if (isValid(prodottoAttivato)) {
					prodottoAttivabile.setStato(Constants.PRODOTTO_ATTIVATO_STATO_ATTIVATO); 
				}
			}
		}

		List<ProdottoAttivabileEnteDTO> sortedprodottiAttivabiliEnte = prodottiAttivabiliEnte.stream().sorted(Comparator.comparing(ProdottoAttivabileEnteDTO::getNomeProdottoAttiv)).collect(Collectors.toList());
		
		PagedData<ProdottoAttivabileEnteDTO> pagedData = new PagedData<ProdottoAttivabileEnteDTO>(sortedprodottiAttivabiliEnte, page);
		return pagedData;

	}
	
	@Override
	@Transactional(rollbackFor = Exception.class, readOnly = true)
	public List<StatEntiProdottiRow> getStatEntiProdottiRows() {
		List<IStatEntiProdottiRow> results = prodottoAttivatoRepository.getStatEntiProdottiRows();
		
		List<StatEntiProdottiRow> convertedResults = ObjectMapperUtils.mapAll(results, StatEntiProdottiRow.class);
		
		return convertedResults;
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class, readOnly = true)
	public List<StatServiziErogatiRow> getStatServiziErogatiRows() {
		List<IStatServiziErogatiRow> results = prodottoAttivatoRepository.getStatServiziErogatiRows();
		
		List<StatServiziErogatiRow> convertedResults = ObjectMapperUtils.mapAll(results, StatServiziErogatiRow.class);
		
		return convertedResults;
	}

	@Override
	@Transactional(rollbackFor = Exception.class, readOnly = false)
	public MessaggioDTO sendMessaggioToUtentiProdottiAttivato(
			ProdottoAttivatoComunicazioneFilterDTO prodottoAttivatoComunicazioneFilter, MessaggioDTO messaggio)
			throws Exception {
		
		
		// get list of utenti prodotto attivato
		String sortProperty = "idUtenteProd";
		String sortDirection = "ASC";
		
		Pageable pageable = PagedData.buildPageable(0, 999999, sortProperty, sortDirection);
		
		Page<UtenteProdottoAttivato> page = utenteProdottoAttivatoRepository.findAll(RepositoryUtils.buildUtenteProdottoAttivatoComunicazioneFilterSpecification(prodottoAttivatoComunicazioneFilter), pageable);

		List<UtenteProdottoAttivato> utenti = page.toList();
		
		if (utenti.size() == 0) {
			return null;
		}
		
		Integer idAttivazione = utenti.get(0).getProdottoAttivato().getIdAttivazione();
		String idContenuto = idAttivazione != null ? "" + idAttivazione : null;
		messaggio.setIdContenuto(idContenuto);
		
		// concatenate email addresses
		String[] indirizzi = utenti.stream()
				.map(u -> (StringUtils.hasText(u.getEmail()) ? u.getEmail() : u.getUtente().getEmail())).toArray(String[]::new);
		
		String indirizzo = String.join(";", indirizzi);
		messaggio.setIndirizzo(indirizzo);
		
		// insert and send messaggio
		MessaggioDTO savedMessaggio = messaggioService.insertAndSendMessaggio(messaggio);
		
		// link utenti with messaggio
		Integer idMessaggio = savedMessaggio.getIdMessaggio();
		
		Integer[] utentiIds = utenti.stream()
				.filter(u -> u.getUtente() != null)
				.map(u -> u.getUtente().getIdUtente()).toArray(Integer[]::new);
		
		utenteMessaggiService.linkUtentiWithMessaggio(utentiIds, idMessaggio);
		
		return savedMessaggio;
	}

	@Override
	@Transactional(rollbackFor = Exception.class, readOnly = false)
	public ProdottoAttivatoDTO genericAvanzamentoStepRichiesta(GenericAvanzamentoStepRichiestaInputDTO input)
			throws Exception {
		ExtendedGenericAvanzamentoStepRichiestaInputDTO inputData = ObjectMapperUtils.map(input, ExtendedGenericAvanzamentoStepRichiestaInputDTO.class);
	
		// validate input
		validateGenericAvanzamentoStepRichiestaInput(inputData);
		
		// retrieve necessary data
		retrieveMissingInputData(inputData);
		
		Integer idAttivazione = inputData.getIdAttivazione();
		// insert prodotto attivato
		if (idAttivazione == null) {
			ProdottoAttivato prodottoAttivato = new ProdottoAttivato();
			
			ProdottoAttivabile prodottoAttivabile = new ProdottoAttivabile();
			prodottoAttivabile.setIdProdottoAtt(inputData.getIdProdottoAtt());
			prodottoAttivato.setProdottoAttivabile(prodottoAttivabile);
			
			Ente ente = new Ente();
			ente.setIdEnte(inputData.getIdEnte());
			prodottoAttivato.setEnte(ente);
			
			// add audit fields
			AuditModel audit = (AuditModel) prodottoAttivato;
			AuditUtils.fillAudit(audit);
			
			LOG.debug("Inserting new ProdottoAttivato");
			ProdottoAttivato savedProdottoAttivato = prodottoAttivatoRepository.save(prodottoAttivato);
			idAttivazione = savedProdottoAttivato.getIdAttivazione();
			LOG.debug("Inserted new ProdottoAttivato with idAttivazione = " + idAttivazione);
		}
		
		// RichiestaProdotto
		Integer idProdAttivRich = inputData.getIdProdAttivRich();
		if (idProdAttivRich == null) {
			// insert
			RichiestaProdotto richiestaProdotto = new RichiestaProdotto();
			
			ProdottoAttivato prodottoAttivato = new ProdottoAttivato();
			prodottoAttivato.setIdAttivazione(idAttivazione);
			richiestaProdotto.setProdottoAttivato(prodottoAttivato);
			
			ProcedimentoProdotto procedimentoProdotto = new ProcedimentoProdotto();
			procedimentoProdotto.setIdProdottoProc(inputData.getIdProdottoProc());
			richiestaProdotto.setProcedimentoProdotto(procedimentoProdotto);
			
			richiestaProdotto.setNumVersione(1);
			richiestaProdotto.setDtRich(LocalDate.now());
			richiestaProdotto.setCodStato(inputData.getNewStep().getCodStato());
			richiestaProdotto.setUltimeNote(inputData.getNote());
			richiestaProdotto.setFlgFineRich(inputData.getNewStep().getFlgFineRich());
			
			// add audit fields
			AuditModel audit = (AuditModel) richiestaProdotto;
			AuditUtils.fillAudit(audit);
			
			LOG.debug("Inserting new RichiestaProdotto");
			RichiestaProdotto savedRichiestaProdotto = richiestaProdottoRepository.save(richiestaProdotto);
			idProdAttivRich = savedRichiestaProdotto.getIdProdAttivRich();
			LOG.debug("Inserted new RichiestaProdotto with idProdAttivRich = " + idProdAttivRich);
		} else {
			// update
			Optional<RichiestaProdotto> richiestaProdottoOptional = richiestaProdottoRepository.findById(idProdAttivRich);
			if (richiestaProdottoOptional.isPresent()) {
				RichiestaProdotto richiestaProdotto = richiestaProdottoOptional.get();
				
				richiestaProdotto.setCodStato(inputData.getNewStep().getCodStato());
				richiestaProdotto.setUltimeNote(inputData.getNote());
				richiestaProdotto.setFlgFineRich(inputData.getNewStep().getFlgFineRich());
				
				// add audit fields
				AuditModel audit = (AuditModel) richiestaProdotto;
				AuditUtils.fillAudit(audit);
				
				LOG.debug(String.format("Updating RichiestaProdotto with idProdAttivRich = %d", idProdAttivRich));
				richiestaProdottoRepository.save(richiestaProdotto);
				LOG.debug(String.format("Updated RichiestaProdotto with idProdAttivRich = %d", idProdAttivRich));
			} else {
				LOG.error(String.format("RichiestaProdotto with idProdAttivRich = %d not found", idProdAttivRich));
			}
		}
		
		// StepRichiestaProdotto
		Integer idStep;
		{			
			StepRichiestaProdotto stepRichiestaProdotto = new StepRichiestaProdotto();
			
			RichiestaProdotto richiestaProdotto = new RichiestaProdotto();
			richiestaProdotto.setIdProdAttivRich(idProdAttivRich);
			stepRichiestaProdotto.setRichiestaProdotto(richiestaProdotto);
			
			stepRichiestaProdotto.setDtStep(Timestamp.valueOf(LocalDateTime.now()));
			stepRichiestaProdotto.setEsecutore(inputData.getEsecutore());
			stepRichiestaProdotto.setCodStato(inputData.getNewStep().getCodStato()); 
			stepRichiestaProdotto.setNoteStep(inputData.getNote());
			stepRichiestaProdotto.setEsitoStep(inputData.getEsito());
			
			// add audit fields
			AuditModel audit = (AuditModel) stepRichiestaProdotto;
			AuditUtils.fillAudit(audit);
			
			LOG.debug("Inserting new StepRichiestaProdotto");
			StepRichiestaProdotto savedStepRichiestaProdotto = stepRichiestaProdottoRepository.save(stepRichiestaProdotto);
			idStep = savedStepRichiestaProdotto.getIdStep();
			LOG.debug("Inserted new StepRichiestaProdotto with idStep = " + idStep);
		}
		
		// update list of DocumentoRichiestaProdotto
		List<OperazioneDocumentoRichiestaProdottoDTO> documenti = inputData.getDocumenti();
		if (documenti != null) {
			for (OperazioneDocumentoRichiestaProdottoDTO operazione : documenti) {
				if (Constants.OPERAZIONE_ELIMINA.equalsIgnoreCase(operazione.getTipoOperazione())) {
					DocumentoRichiestaProdottoDTO doc = operazione.getDocumentoRichiestaProdotto();
					LOG.debug(String.format("Deleting DocumentoRichiestaProdotto with idProdRichDoc = %d", doc.getIdProdRichDoc()));
					documentoRichiestaProdottoRepository.deleteById(doc.getIdProdRichDoc());
					LOG.debug(String.format("DocumentoRichiestaProdotto deleted (%d)", doc.getIdProdRichDoc()));
				}
				
				if (Constants.OPERAZIONE_MODIFICA.equalsIgnoreCase(operazione.getTipoOperazione())) {
					DocumentoRichiestaProdottoDTO doc = operazione.getDocumentoRichiestaProdotto();
					
					Integer idProdRichDoc = doc.getIdProdRichDoc();
					if (idProdRichDoc == null) {
						// insert
						DocumentoRichiestaProdotto documento = new DocumentoRichiestaProdotto();
						
						RichiestaProdotto richiestaProdotto = new RichiestaProdotto();
						richiestaProdotto.setIdProdAttivRich(idProdAttivRich);
						documento.setRichiestaProdotto(richiestaProdotto);
						
						StepRichiestaProdotto stepRichiestaProdotto = new StepRichiestaProdotto();
						stepRichiestaProdotto.setIdStep(idStep);
						documento.setStepRichiestaProdotto(stepRichiestaProdotto);
						
						documento.setIdDocumento(doc.getIdDocumento());
						documento.setNomeDocumento(doc.getNomeDocumento());
						documento.setFlgEnabled(doc.getFlgEnabled() != null ? doc.getFlgEnabled() : 1);
						
						LOG.debug(String.format("Inserting new DocumentoRichiestaProdotto (%s)", doc.getIdDocumento()));
						DocumentoRichiestaProdotto savedDocumento = documentoRichiestaProdottoRepository.save(documento);
						LOG.debug(String.format("Inserted new DocumentoRichiestaProdotto (%s) with IdProdRichDoc = %d", doc.getIdDocumento(), savedDocumento.getIdProdRichDoc()));
						
					} else {
						// update
						Optional<DocumentoRichiestaProdotto> documentoOptional = documentoRichiestaProdottoRepository.findById(idProdRichDoc);
						if (documentoOptional.isPresent()) {
							DocumentoRichiestaProdotto documento = documentoOptional.get();
							
							documento.setIdDocumento(doc.getIdDocumento());
							documento.setNomeDocumento(doc.getNomeDocumento());
							if (doc.getFlgEnabled() != null) {								
								documento.setFlgEnabled(doc.getFlgEnabled());
							}

							LOG.debug("Updating DocumentoRichiestaProdotto with idProdRichDoc = " + idProdRichDoc);
							documentoRichiestaProdottoRepository.save(documento);
							LOG.debug("Updated DocumentoRichiestaProdotto with idProdRichDoc = " + idProdRichDoc);
							
						} else {
							LOG.error(String.format("DocumentoRichiestaProdotto with idProdRichDoc = %d not found", idProdRichDoc));
						}
					}
				}
			}
		}
		
		// update list of UtenteRichiestaProdotto
		List<OperazioneUtenteRichiestaProdottoDTO> utenti = inputData.getUtenti();
		if (utenti != null) {
			for (OperazioneUtenteRichiestaProdottoDTO operazione : utenti) {
				if (Constants.OPERAZIONE_ELIMINA.equalsIgnoreCase(operazione.getTipoOperazione())) {
					UtenteRichiestaProdottoDTO utente = operazione.getUtenteRichiestaProdotto();
					LOG.debug(String.format("Deleting UtenteRichiestaProdotto with idUtenteRich = %d", utente.getIdUtenteRich()));
					utenteRichiestaProdottoRepository.deleteById(utente.getIdUtenteRich());
					LOG.debug(String.format("UtenteRichiestaProdotto deleted (%d)", utente.getIdUtenteRich()));
				}
				
				if (Constants.OPERAZIONE_MODIFICA.equalsIgnoreCase(operazione.getTipoOperazione())) {
					UtenteRichiestaProdottoDTO ut = operazione.getUtenteRichiestaProdotto();
					
					Integer idUtenteRich = ut.getIdUtenteRich();
					if (idUtenteRich == null) {
						// insert
						UtenteRichiestaProdotto utente = new UtenteRichiestaProdotto();
						
						RichiestaProdotto richiestaProdotto = new RichiestaProdotto();
						richiestaProdotto.setIdProdAttivRich(idProdAttivRich);
						utente.setRichiestaProdotto(richiestaProdotto);
						
						if (ut.getUtenteProdottoAttivato() != null) {
							UtenteProdottoAttivato utenteProdottoAttivato = new UtenteProdottoAttivato();
							utenteProdottoAttivato.setIdUtenteProd(ut.getUtenteProdottoAttivato().getIdUtenteProd());
							utente.setUtenteProdottoAttivato(utenteProdottoAttivato);							
						}
						
						utente.setRichOper(ut.getRichOper());
						
						RuoloProdotto ruoloProdotto = new RuoloProdotto();
						ruoloProdotto.setCodRuolo(ut.getRuoloProdotto().getCodRuolo());
						utente.setRuoloProdotto(ruoloProdotto);
						
						if (ut.getUtente() != null) {
							Utente utente2 = utenteRepository.getOne(ut.getUtente().getIdUtente());
							utente.setUtente(utente2);
						}
						
						utente.setCodFiscale(ut.getCodFiscale());
						utente.setNome(ut.getNome());
						utente.setCognome(ut.getCognome());
						utente.setEmail(ut.getEmail());
						utente.setTelefono(ut.getTelefono());
						
						// add audit fields
						AuditModel audit = (AuditModel) utente;
						AuditUtils.fillAudit(audit);
						
						LOG.debug("Inserting new UtenteRichiestaProdotto");
						UtenteRichiestaProdotto savedUtenteRichiestaProdotto = utenteRichiestaProdottoRepository.save(utente);
						idUtenteRich = savedUtenteRichiestaProdotto.getIdUtenteRich();
						LOG.debug("Inserted new UtenteRichiestaProdotto with idUtenteRich = " + idUtenteRich);
						
					} else {
						// update
						Optional<UtenteRichiestaProdotto> utenteOptional = utenteRichiestaProdottoRepository.findById(idUtenteRich);
						if (utenteOptional.isPresent()) {
							UtenteRichiestaProdotto utente = utenteOptional.get();
							
							if (ut.getUtenteProdottoAttivato() != null) {
								UtenteProdottoAttivato utenteProdottoAttivato = new UtenteProdottoAttivato();
								utenteProdottoAttivato.setIdUtenteProd(ut.getUtenteProdottoAttivato().getIdUtenteProd());
								utente.setUtenteProdottoAttivato(utenteProdottoAttivato);							
							}
							
							utente.setRichOper(ut.getRichOper());
							
							RuoloProdotto ruoloProdotto = new RuoloProdotto();
							ruoloProdotto.setCodRuolo(ut.getRuoloProdotto().getCodRuolo());
							utente.setRuoloProdotto(ruoloProdotto);
							
							if (ut.getUtente() != null) {
								Utente utente2 = new Utente();
								utente2.setIdUtente(ut.getUtente().getIdUtente());
								utente.setUtente(utente2);
							}
							
							utente.setCodFiscale(ut.getCodFiscale());
							utente.setNome(ut.getNome());
							utente.setCognome(ut.getCognome());
							utente.setEmail(ut.getEmail());
							utente.setTelefono(ut.getTelefono());
							
							// add audit fields
							AuditModel audit = (AuditModel) utente;
							AuditUtils.fillAudit(audit);

							LOG.debug("Updating UtenteRichiestaProdotto with idUtenteRich = " + idUtenteRich);
							utenteRichiestaProdottoRepository.save(utente);
							LOG.debug("Updated UtenteRichiestaProdotto with idUtenteRich = " + idUtenteRich);
							
						} else {
							LOG.error(String.format("UtenteRichiestaProdotto with idUtenteRich = %d not found", idUtenteRich));
						}
					}
				}
			}
		}
		
		LOG.debug(String.format("The new step has flgFineRich = %d", inputData.getNewStep().getFlgFineRich()));
		// if the request is completed
		if (inputData.getNewStep().getFlgFineRich().equals(1)) {
			
			// activate product
			if (inputData.getFlgAttivaProdotto() != null && inputData.getFlgAttivaProdotto().equals(1)) {
				LOG.debug(String.format("Setting validity dates for ProdottoAttivato with idAttivazione = %d", idAttivazione));
				prodottoAttivatoRepository.setValidityDates(idAttivazione, Date.valueOf(LocalDate.now()), Date.valueOf(Constants.DEFAULT_DATE_END_VALIDITY));
				LOG.debug(String.format("Validity dates for ProdottoAttivato with idAttivazione = %d have been set", idAttivazione));
			}
			
			// align users
			// get request users
			List<UtenteRichiestaProdotto> utentiRichiesta = utenteRichiestaProdottoRepository.findAllByRichiestaProdottoIdProdAttivRich(idProdAttivRich);
			
			for (UtenteRichiestaProdotto utenteRichiesta : utentiRichiesta) {
				
				if (utenteRichiesta.getRichOper() == null) continue;
				
				switch (utenteRichiesta.getRichOper()) {
				case Constants.UTENTE_RICHIESTA_PRODOTTO_OPERAZIONE_CANCELLAZIONE:
				{
					// delete -> flgEnabled set to 0										
					UtenteProdottoAttivato utenteProdottoAttivato = utenteRichiesta.getUtenteProdottoAttivato();
					utenteProdottoAttivato.setFlgEnabled(0);
					
					// add audit fields
					AuditModel audit = (AuditModel) utenteProdottoAttivato;
					AuditUtils.fillAudit(audit);
					
					LOG.debug(String.format("Disabling UtenteProdottoAttivato with IdUtenteProd = %d", utenteRichiesta.getUtenteProdottoAttivato().getIdUtenteProd()));
					utenteProdottoAttivatoRepository.save(utenteProdottoAttivato);					
					LOG.debug(String.format("UtenteProdottoAttivato with IdUtenteProd = %d disabled", utenteRichiesta.getUtenteProdottoAttivato().getIdUtenteProd()));
				}	
					break;
				case Constants.UTENTE_RICHIESTA_PRODOTTO_OPERAZIONE_MODIFICA:
					// update
				{					
					UtenteProdottoAttivato utenteProdottoAttivato = utenteRichiesta.getUtenteProdottoAttivato();
					utenteProdottoAttivato.setRuolo(utenteRichiesta.getRuoloProdotto());
					utenteProdottoAttivato.setUtente(utenteRichiesta.getUtente());
					utenteProdottoAttivato.setCodFiscale(utenteRichiesta.getCodFiscale() != null ? utenteRichiesta.getCodFiscale() : utenteRichiesta.getUtente().getCodFiscale());
					utenteProdottoAttivato.setNome(utenteRichiesta.getNome());
					utenteProdottoAttivato.setCognome(utenteRichiesta.getCognome());
					utenteProdottoAttivato.setEmail(utenteRichiesta.getEmail());
					utenteProdottoAttivato.setTelefono(utenteRichiesta.getTelefono());
					
					// add audit fields
					AuditModel audit = (AuditModel) utenteProdottoAttivato;
					AuditUtils.fillAudit(audit);
					
					LOG.debug(String.format("Updating UtenteProdottoAttivato with IdUtenteProd = %d", utenteProdottoAttivato.getIdUtenteProd()));
					utenteProdottoAttivatoRepository.save(utenteProdottoAttivato);
					LOG.debug(String.format("UtenteProdottoAttivato with IdUtenteProd = %d updated", utenteProdottoAttivato.getIdUtenteProd()));
				}
					
					break;
				case Constants.UTENTE_RICHIESTA_PRODOTTO_OPERAZIONE_INSERIMENTO:
					// insert
				{					
					UtenteProdottoAttivato utenteProdottoAttivato = new UtenteProdottoAttivato();
					
					ProdottoAttivato prodottoAttivato = new ProdottoAttivato();
					prodottoAttivato.setIdAttivazione(idAttivazione);
					utenteProdottoAttivato.setProdottoAttivato(prodottoAttivato);
					
					utenteProdottoAttivato.setFlgEnabled(1);
					utenteProdottoAttivato.setRuolo(utenteRichiesta.getRuoloProdotto());
					utenteProdottoAttivato.setUtente(utenteRichiesta.getUtente());
					utenteProdottoAttivato.setCodFiscale(utenteRichiesta.getCodFiscale() != null ? utenteRichiesta.getCodFiscale() : utenteRichiesta.getUtente().getCodFiscale());
					utenteProdottoAttivato.setNome(utenteRichiesta.getNome());
					utenteProdottoAttivato.setCognome(utenteRichiesta.getCognome());
					utenteProdottoAttivato.setEmail(utenteRichiesta.getEmail());
					utenteProdottoAttivato.setTelefono(utenteRichiesta.getTelefono());					

					// add audit fields
					AuditModel audit = (AuditModel) utenteProdottoAttivato;
					AuditUtils.fillAudit(audit);

					LOG.debug("Inserting new UtenteProdottoAttivato");
					UtenteProdottoAttivato savedUtenteProdottoAttivato = utenteProdottoAttivatoRepository.save(utenteProdottoAttivato);
					LOG.debug(String.format("New UtenteProdottoAttivato inserted with IdUtenteProd = %d updated", savedUtenteProdottoAttivato.getIdUtenteProd()));					
				}
					
					break;

				default:
					break;
				}
			}
		}
		
		// send notification email
		sendNotificationNewStepRichiestaProdotto(inputData);
		
		Optional<ProdottoAttivato> result = prodottoAttivatoRepository.findById(idAttivazione);
		if (result.isPresent()) {
			return ObjectMapperUtils.map(result.get(), ProdottoAttivatoDTO.class);
		}
		
		return null;
	}

	private Messaggio sendNotificationNewStepRichiestaProdotto(ExtendedGenericAvanzamentoStepRichiestaInputDTO inputData) throws Exception {
		// get rappresentante ente
		Integer idEnte = inputData.getIdEnte();
		Ente ente = enteRepository.getOne(idEnte);
		
		Utente utenteRappresentante = null;
		
		List<RappresentanteEnte> rappresentanti = rappresentanteEnteRepository.findAllActiveRepresentatives(Constants.RAPPRESENTANTE_ENTE_TIPO_RAP, ente, Date.valueOf(LocalDate.now()));
		String rappresentanteEmailAddress = null;
		if (rappresentanti != null && rappresentanti.size() > 0) {
			utenteRappresentante = rappresentanti.get(0).getUtente();
			rappresentanteEmailAddress = utenteRappresentante.getEmail();
		}
		
		// additional addresses
		String addresses = rappresentanteEmailAddress != null ? rappresentanteEmailAddress + ";" + getProductsOfficialAddress() : getProductsOfficialAddress();

		Messaggio messaggio = new Messaggio();
		messaggio.setArea(Constants.MESSAGGIO_AREA_PRODOTTO);
		messaggio.setTipo(Constants.MESSAGGIO_TIPO_COMUNICAZIONE);
		messaggio.setDestinatario(utenteRappresentante != null ? String.format("%s %s", utenteRappresentante.getNome(), utenteRappresentante.getCognome()) : "Responsabile prodotti");
		messaggio.setIndirizzo(addresses);
		
		// ensure we have idProdottoAtt
		if (inputData.getIdProdottoAtt() == null) {
			ProdottoAttivabile prodottoAttivabile = richiestaProdottoRepository.getProdottoAttivabileByIdProdAttivRich(inputData.getIdProdAttivRich());
			inputData.setIdProdottoAtt(prodottoAttivabile.getIdProdottoAtt());
		}
		
		StringSubstitutor substitutor = getPlaceholderSubstitutor(messaggio, inputData, null);
		
		String stepRichiestaEmailTitle = 
				myConfigService.getConcatValue(ipa + stepRichiestaProdottoEmailTitleKey, "");
		
		messaggio.setTitolo(substitutor.replace(stepRichiestaEmailTitle));
		String bodyTemplate = 
				myConfigService.getConcatValue(ipa + stepRichiestaProdottoEmailBodyTemplateFileKey, "");
		messaggio.setMessaggio(substitutor.replace(bodyTemplate));
		
		// insert and send messaggio
		Messaggio savedMessaggio = messaggioService.insertAndSendMessaggio(messaggio, null);

		if (utenteRappresentante != null) {			
			// link utente with messaggio
			Integer idMessaggio = savedMessaggio.getIdMessaggio();
			Integer[] utenteIds = { utenteRappresentante.getIdUtente() };
			
			utenteMessaggiService.linkUtentiWithMessaggio(utenteIds, idMessaggio);
		}
		
		return savedMessaggio;	
	}

	private StringSubstitutor getPlaceholderSubstitutor(Messaggio messaggio, ExtendedGenericAvanzamentoStepRichiestaInputDTO inputData, Map<String, String> customValuesMap) {
		
		Integer idProdottoAtt = inputData.getIdProdottoAtt();
		ProdottoAttivabile pa = prodottoAttivabileRepository.getOne(idProdottoAtt);
		
		String idProdotto = pa.getIdProdotto();
		String prodottoTitolo = null;
		String prodottoDescrizione = null;
		if (StringUtils.hasText(idProdotto)) {			
			MyExtranetContent prodotto = myPortalService.getContentById(idProdotto);
			prodottoTitolo = prodotto.getTitolo();
			prodottoDescrizione = prodotto.getDescrizione();
		} else {
			prodottoTitolo = pa.getNomeProdottoAttiv();
			prodottoDescrizione = pa.getDesAttivazioneBreve();
		}
		
		Optional<ProcedimentoProdotto> procedimentoOptional = procedimentoProdottoRepository.findById(inputData.getIdProdottoProc());
		ProcedimentoProdotto procedimentoProdotto = procedimentoOptional.isPresent() ? procedimentoOptional.get() : null;
		
		Map<String, String> valuesMap = new HashMap();
		valuesMap.putAll(Map.of(
                "prodotto.titolo", it.regioneveneto.myp3.myextranet.utils.StringUtils.stringOrEmpty(prodottoTitolo),
                "prodotto.descrizione", it.regioneveneto.myp3.myextranet.utils.StringUtils.stringOrEmpty(prodottoDescrizione),
                "procedimentoprodotto.descrizione", procedimentoProdotto != null ? procedimentoProdotto.getDesProdottoProc() : "",
                "richiestaprodotto.stato", inputData.getNewStep().getCodStato(),
                "richiestaprodotto.note", inputData.getNote() != null ?  inputData.getNote() : "",
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
	
	private void retrieveMissingInputData(ExtendedGenericAvanzamentoStepRichiestaInputDTO inputData) throws MyExtranetException {
		
		Integer idProdAttivRich = inputData.getIdProdAttivRich();
		Integer idProdottoAtt = inputData.getIdProdottoAtt();
		String codTipoRich = inputData.getCodTipoRich();
		
		// idProdotto
		if (idProdottoAtt != null) {
			ProdottoAttivabile pa = prodottoAttivabileRepository.getOne(idProdottoAtt);
			if (pa != null) {
				inputData.setIdProdotto(pa.getIdProdotto());
			}
		}
		
		// idProdottoProc
		Integer idProdottoProc;
		if (idProdAttivRich != null) {
			idProdottoProc = richiestaProdottoRepository.getIdProdottoProcByIdProdAttivRich(idProdAttivRich);
		} else {
			idProdottoProc = procedimentoProdottoRepository.getIdProdottoProcByIdProdottoAttAndCodTipoRich(idProdottoAtt, codTipoRich);
		}
		inputData.setIdProdottoProc(idProdottoProc);
		
		// codTipoRich
		if (!StringUtils.hasText(codTipoRich)) {
			codTipoRich = procedimentoProdottoRepository.getCodTipoRichByIdProdottoProc(idProdottoProc);
			inputData.setCodTipoRich(codTipoRich);
		}
		
		// codStatoOld
		String codStatoOld = inputData.getCodStatoOld();
		if (!StringUtils.hasText(codStatoOld)) {
			if (idProdAttivRich != null) {
				codStatoOld = richiestaProdottoRepository.getCodStatoByIdProdAttivRich(idProdAttivRich);
			} else {
				List<String> rows = stepProcedimentoProdottoRepository.getCodStatoByIdProdottoProcAndCompetenza(idProdottoProc, inputData.getEsecutore());
				if (rows.size() > 0) {
					codStatoOld = rows.get(0);
				} else {
					throw new MyExtranetException("Impossibile determinare lo stato di partenza. Controllare la configurazione.");
				}
			}
			inputData.setCodStatoOld(codStatoOld);
		}
		
		// newStep
		String codStatoNew = inputData.getCodStatoNew();
		StepProcedimentoProdotto newStep = null;
		if (StringUtils.hasText(codStatoNew)) {
			newStep = stepProcedimentoProdottoRepository.findOneByProcedimentoProdottoIdProdottoProcAndCodStato(idProdottoProc, codStatoNew).get();
		} else {
			Integer oldNumStep = stepProcedimentoProdottoRepository.getNumStepByProcedimentoProdottoIdProdottoProcAndCodStato(idProdottoProc, codStatoOld);
			if (inputData.getFlgAnnulla() != null && inputData.getFlgAnnulla().equals(1)) {
				newStep = stepProcedimentoProdottoRepository.findFirstByProcedimentoProdottoIdProdottoProcAndFlgFineRichOrderByNumStepDesc(idProdottoProc, 2).get();
			} else if (inputData.getEsito() != null && inputData.getEsito().equals(1)) {
				newStep = stepProcedimentoProdottoRepository.findOneByProcedimentoProdottoIdProdottoProcAndNumStep(idProdottoProc, oldNumStep + 1).get();
			} else if (inputData.getEsito() != null && inputData.getEsito().equals(0)) {
				newStep = stepProcedimentoProdottoRepository.findOneByProcedimentoProdottoIdProdottoProcAndNumStep(idProdottoProc, oldNumStep - 1).get();
			} else {
				throw new MyExtranetException("Impossibile definire il nuovo step con i dati in ingresso");
			}
		}
		inputData.setNewStep(newStep);
	}

	private void validateGenericAvanzamentoStepRichiestaInput(ExtendedGenericAvanzamentoStepRichiestaInputDTO input) throws MyExtranetValidationException {
		
		Integer idAttivazione = input.getIdAttivazione();
		Integer idEnte = input.getIdEnte();
		Integer idProdottoAtt = input.getIdProdottoAtt();
		Integer idProdAttivRich = input.getIdProdAttivRich();
		String codTipoRich = input.getCodTipoRich();
		
		if (idProdAttivRich != null) {
			idAttivazione = richiestaProdottoRepository.getIdAttivazioneByIdProdAttivRich(idProdAttivRich);
			input.setIdAttivazione(idAttivazione);
		}
		
		if (idProdAttivRich == null) {
			if (idProdottoAtt == null || !StringUtils.hasText(codTipoRich)) {
				throw new MyExtranetValidationException("codTipoRich e idProdottoAtt devono essere valorizzati");
			}
			
			if (idAttivazione != null) {
				List<RichiestaProdotto> existingList = richiestaProdottoRepository.findAllByProdottoAttivatoIdAttivazioneAndFlgFineRich(idAttivazione, 0);
				if (existingList.size() > 0) {
					throw new MyExtranetValidationException("Esiste già una richiesta in corso per il prodotto");
				}
			}
		}
		
		if (!StringUtils.hasText(codTipoRich) && idProdAttivRich == null) {
			throw new MyExtranetValidationException("idProdAttivRich deve essere valorizzato se codTipoRich non lo è");
		}
		
		Integer flgAttivaProdotto;
		// get flgAttivaProdotto
		if (StringUtils.hasText(codTipoRich)) {
			TipoRichiestaProdotto tipoRichiestaProdotto = tipoRichiestaProdottoRepository.getOne(codTipoRich);
			flgAttivaProdotto = tipoRichiestaProdotto.getFlgAttivaProdotto();
		} else {
			// get it from idProdAttivRich
			flgAttivaProdotto = richiestaProdottoRepository.getFlgAttivaProdottoByIdProdAttivRich(idProdAttivRich);
		}
		// set it so we'll have it later on
		input.setFlgAttivaProdotto(flgAttivaProdotto);

		
		if (idAttivazione == null) {
			if (idEnte == null || idProdottoAtt == null) {
				throw new MyExtranetValidationException("idEnte e idProdottoAtt devono essere valorizzati");
			}
						
			// check if existing
			List<ProdottoAttivato> existingList = prodottoAttivatoRepository.findAllByProdottoAttivabileIdProdottoAttAndEnteIdEnte(idProdottoAtt, idEnte);
			if (existingList.size() > 0) {
				ProdottoAttivato prodottoAttivato = existingList.get(0);
				idAttivazione = prodottoAttivato.getIdAttivazione();
				input.setIdAttivazione(idAttivazione);
				
				if (flgAttivaProdotto != null && flgAttivaProdotto.equals(1)) {
					if (isValid(prodottoAttivato)) {
						throw new MyExtranetValidationException("Esiste già un prodotto attivato per quel prodotto e quell'ente");
					}
				}
			}
			
		}
		
	}
	
	
	private boolean isValid(ProdottoAttivato prodottoAttivato) {
		
		Date today = Date.valueOf(LocalDate.now());
		
		boolean isValid = prodottoAttivato.getDtInizioVal() != null && prodottoAttivato.getDtFineVal() != null &&
				prodottoAttivato.getDtInizioVal().compareTo(today) <= 0 &&
						prodottoAttivato.getDtFineVal().compareTo(today) > 0;

		return isValid;
	}

}
