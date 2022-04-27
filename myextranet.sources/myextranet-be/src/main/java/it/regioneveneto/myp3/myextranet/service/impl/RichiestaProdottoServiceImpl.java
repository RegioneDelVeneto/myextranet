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
import java.util.stream.Collectors;

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

import it.regioneveneto.myp3.mybox.ContentMetadata;
import it.regioneveneto.myp3.mychannel.client.model.Attachment;
import it.regioneveneto.myp3.myextranet.bean.UtentiProdottiAttivatiEnteExcelReportInput;
import it.regioneveneto.myp3.myextranet.bean.UtentiProdottiAttivatiEnteExcelReportInput.ProdottoUtenti;
import it.regioneveneto.myp3.myextranet.bean.UtentiProdottiAttivatiEnteExcelReportInput.Utente;
import it.regioneveneto.myp3.myextranet.exception.MyExtranetException;
import it.regioneveneto.myp3.myextranet.model.DocumentoRichiestaProdotto;
import it.regioneveneto.myp3.myextranet.model.Ente;
import it.regioneveneto.myp3.myextranet.model.Messaggio;
import it.regioneveneto.myp3.myextranet.model.MyExtranetContent;
import it.regioneveneto.myp3.myextranet.model.ProcedimentoProdotto;
import it.regioneveneto.myp3.myextranet.model.ProdottoAttivabile;
import it.regioneveneto.myp3.myextranet.model.RappresentanteEnte;
import it.regioneveneto.myp3.myextranet.model.RichiestaProdotto;
import it.regioneveneto.myp3.myextranet.model.StepProcedimentoProdotto;
import it.regioneveneto.myp3.myextranet.model.StepRichiestaProdotto;
import it.regioneveneto.myp3.myextranet.model.UtenteProdottoAttivato;
import it.regioneveneto.myp3.myextranet.model.UtenteRichiestaProdotto;
import it.regioneveneto.myp3.myextranet.repository.DocumentoRichiestaProdottoRepository;
import it.regioneveneto.myp3.myextranet.repository.RappresentanteEnteRepository;
import it.regioneveneto.myp3.myextranet.repository.RichiestaProdottoRepository;
import it.regioneveneto.myp3.myextranet.repository.StepProcedimentoProdottoRepository;
import it.regioneveneto.myp3.myextranet.repository.StepRichiestaProdottoRepository;
import it.regioneveneto.myp3.myextranet.repository.UtenteRichiestaProdottoRepository;
import it.regioneveneto.myp3.myextranet.service.ExcelReportService;
import it.regioneveneto.myp3.myextranet.service.MessaggioService;
import it.regioneveneto.myp3.myextranet.service.MyBoxService;
import it.regioneveneto.myp3.myextranet.service.MyConfigService;
import it.regioneveneto.myp3.myextranet.service.MyPortalService;
import it.regioneveneto.myp3.myextranet.service.RichiestaProdottoService;
import it.regioneveneto.myp3.myextranet.service.UtenteMessaggiService;
import it.regioneveneto.myp3.myextranet.utils.Constants;
import it.regioneveneto.myp3.myextranet.utils.ObjectMapperUtils;
import it.regioneveneto.myp3.myextranet.utils.RepositoryUtils;
import it.regioneveneto.myp3.myextranet.web.dto.ComunicazioneRichiestaProdottoInputDTO;
import it.regioneveneto.myp3.myextranet.web.dto.DocumentoRichiestaProdottoOutputDTO;
import it.regioneveneto.myp3.myextranet.web.dto.EnteDTO;
import it.regioneveneto.myp3.myextranet.web.dto.MessaggioDTO;
import it.regioneveneto.myp3.myextranet.web.dto.PagedData;
import it.regioneveneto.myp3.myextranet.web.dto.RichiestaProdottoDTO;
import it.regioneveneto.myp3.myextranet.web.dto.RichiestaProdottoFilterDTO;
import it.regioneveneto.myp3.myextranet.web.dto.RichiestaProdottoWithStepsDTO;
import it.regioneveneto.myp3.myextranet.web.dto.StepProcedimentoProdottoDTO;
import it.regioneveneto.myp3.myextranet.web.dto.StepRichiestaProdottoOutputDTO;
import it.regioneveneto.myp3.myextranet.web.dto.UtenteRichiestaProdottoDTO;

@Service
public class RichiestaProdottoServiceImpl implements RichiestaProdottoService {
    private static final Logger LOG = LoggerFactory.getLogger(RichiestaProdottoServiceImpl.class);

    @Autowired
    RichiestaProdottoRepository richiestaProdottoRepository;   
    
    @Autowired
    StepRichiestaProdottoRepository stepRichiestaProdottoRepository;
    
    @Autowired
    DocumentoRichiestaProdottoRepository documentoRichiestaProdottoRepository;
    
    @Autowired
    UtenteRichiestaProdottoRepository utenteRichiestaProdottoRepository;
    
    @Autowired
    StepProcedimentoProdottoRepository stepProcedimentoProdottoRepository;
    
    @Autowired
    RappresentanteEnteRepository rappresentanteEnteRepository;
    
    @Autowired
    MyPortalService myPortalService;
    
    @Autowired
    MyConfigService myConfigService;
    
    @Autowired
    MessaggioService messaggioService;
    
    @Autowired
    UtenteMessaggiService utenteMessaggiService;
    
    @Autowired
    ExcelReportService excelReportService;
    
    @Autowired
    MyBoxService myBoxService;
    
    private final String ipa;
    
    private final String responsabileProdottiEmailAddressKey;
    private final String productActivationRequestDetailTemplateKey;
        
	public RichiestaProdottoServiceImpl(
			@Value("${myportal.ipa}") String ipa,
			@Value("${mychannel.addresses.responsabileProdotti.myConfigKeys.emailAddress}") String responsabileProdottiEmailAddressKey,
			@Value("${myportal.myConfigKeys.productActivationRequestDetailTemplate}") String productActivationRequestDetailTemplateKey
			) {
		super();
		this.ipa = ipa;
		this.responsabileProdottiEmailAddressKey = responsabileProdottiEmailAddressKey;
		this.productActivationRequestDetailTemplateKey = productActivationRequestDetailTemplateKey;
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class, readOnly = true)
	public EnteDTO getEnteByIdProdAttivRich(Integer idProdAttivRich) throws Exception {
		
		Ente ente = richiestaProdottoRepository.getEnteByIdProdAttivRich(idProdAttivRich);
		return ObjectMapperUtils.map(ente, EnteDTO.class);
	}

	@Override
	@Transactional(rollbackFor = Exception.class, readOnly = true)
	public RichiestaProdottoWithStepsDTO getRichiestaProdottoWithSteps(Integer idProdAttivRich) throws Exception {
		Optional<RichiestaProdotto> richiestaProdottoOptional = richiestaProdottoRepository.findById(idProdAttivRich);

		if(richiestaProdottoOptional.isPresent()) {
			RichiestaProdottoWithStepsDTO richiestaProdotto = ObjectMapperUtils.map(richiestaProdottoOptional.get(), RichiestaProdottoWithStepsDTO.class);
			
			populateLists(richiestaProdotto);
			
			// get right step
			Optional<StepProcedimentoProdotto> stepOptional = stepProcedimentoProdottoRepository.findOneByProcedimentoProdottoIdProdottoProcAndCodStato(
					richiestaProdotto.getProcedimentoProdotto().getIdProdottoProc(), 
					richiestaProdotto.getCodStato());
			
			richiestaProdotto.setStepProcedimentoProdotto(
					stepOptional.isPresent() ? 
							ObjectMapperUtils.map(stepOptional.get(), StepProcedimentoProdottoDTO.class) : 
								null);
			
			return richiestaProdotto;
		} else {
			throw new Exception("RichiestaProdotto not found");
		}
	}

	private void populateLists(RichiestaProdottoWithStepsDTO richiestaProdotto) {
		
		populateStepRichiestaProdottoList(richiestaProdotto);
		
		populateDocumentoRichiestaProdottoList(richiestaProdotto);
		
		populateUtenteRichiestaProdottoList(richiestaProdotto);
		
	}

	private void populateUtenteRichiestaProdottoList(RichiestaProdottoWithStepsDTO richiestaProdotto) {
		// populate UtenteRichiestaProdottoDTOs
		
		// qui non dovrebbero serivire i DEL se non vogliamo mostrarli
		List<UtenteRichiestaProdotto> users = utenteRichiestaProdottoRepository.findAllByRichiestaProdottoIdProdAttivRich(richiestaProdotto.getIdProdAttivRich());
		
		// simplify
		users.stream().forEach(u -> {
			if (u.getRichiestaProdotto() != null) {				
				RichiestaProdotto r = new RichiestaProdotto();
				r.setIdProdAttivRich(u.getRichiestaProdotto().getIdProdAttivRich());
				u.setRichiestaProdotto(r);
			}
			
			if (u.getUtenteProdottoAttivato() != null) {				
				UtenteProdottoAttivato up = new UtenteProdottoAttivato();
				up.setIdUtenteProd(u.getUtenteProdottoAttivato().getIdUtenteProd());
				u.setUtenteProdottoAttivato(up);
			}					
			
		});
		
		richiestaProdotto.setUtenteRichiestaProdottoList(ObjectMapperUtils.mapAll(users, UtenteRichiestaProdottoDTO.class));

		
	}

	private void populateDocumentoRichiestaProdottoList(RichiestaProdottoWithStepsDTO richiestaProdotto) {
		// populate DocumentoRichiestaProdottoDTOs
		List<DocumentoRichiestaProdotto> documents = documentoRichiestaProdottoRepository.findAllByRichiestaProdottoIdProdAttivRich(richiestaProdotto.getIdProdAttivRich());
		
		// simplify
		documents.stream().forEach(d -> {
			if (d.getRichiestaProdotto() != null) {				
				RichiestaProdotto r = new RichiestaProdotto();
				r.setIdProdAttivRich(d.getRichiestaProdotto().getIdProdAttivRich());
				d.setRichiestaProdotto(r);
			}
			
			if (d.getStepRichiestaProdotto() != null) {				
				StepRichiestaProdotto s = new StepRichiestaProdotto();
				s.setIdStep(d.getStepRichiestaProdotto().getIdStep());
				d.setStepRichiestaProdotto(s);
			}
		});
		
		
		List<DocumentoRichiestaProdottoOutputDTO> documentDTOs = ObjectMapperUtils.mapAll(documents, DocumentoRichiestaProdottoOutputDTO.class);
		
		// integrate with metadata
		documentDTOs.forEach(d -> {
			populateMetadata(d);
		});
		
		richiestaProdotto.setDocumentoRichiestaProdottoList(documentDTOs);
	}
	
    private void populateMetadata(DocumentoRichiestaProdottoOutputDTO doc) {
		
    	String idStr = doc.getIdDocumento();
    	if (idStr.length() >= 32) {
    		try {
				String id = idStr.substring(idStr.length() - 32);
				ContentMetadata metadata = myBoxService.getFileMetadata(id);
				
				myBoxService.cleanMetadataFileName(metadata);
				
				doc.setMetadata(metadata);
			} catch (MyExtranetException e) {
				LOG.error("Cannot retrieve MyBox metadata for " + idStr, e);
			}
    		
    	}
    	
	}

	private void populateStepRichiestaProdottoList(RichiestaProdottoWithStepsDTO richiestaProdotto) {
		// populate StepRichiestaProdottoDTOs
		List<StepRichiestaProdotto> steps = stepRichiestaProdottoRepository.findAllByRichiestaProdottoIdProdAttivRich(richiestaProdotto.getIdProdAttivRich());
		
		// simplify
		steps.stream().forEach(s -> {
			
			if (s.getRichiestaProdotto() != null) {				
				RichiestaProdotto r = new RichiestaProdotto();
				r.setIdProdAttivRich(s.getRichiestaProdotto().getIdProdAttivRich());
				s.setRichiestaProdotto(r);
			}
		});
		
		richiestaProdotto.setStepRichiestaProdottoList(ObjectMapperUtils.mapAll(steps, StepRichiestaProdottoOutputDTO.class));
	}

	@Override
	@Transactional(rollbackFor = Exception.class, readOnly = true)
	public PagedData<RichiestaProdottoDTO> getRichiesteProdotto(RichiestaProdottoFilterDTO richiestaProdottoFilter,
			Integer pageNumber, Integer pageSize, String sortProperty, String sortDirection) {
		if (sortProperty == null) sortProperty = "dtRich";
		if (sortDirection == null) sortDirection = "ASC";
		
		Pageable pageable = PagedData.buildPageable(pageNumber, pageSize, sortProperty, sortDirection);
		
		Page<RichiestaProdotto> page = richiestaProdottoRepository.findAll(RepositoryUtils.buildRichiestaProdottoFilterSpecification(richiestaProdottoFilter), pageable);

		PagedData<RichiestaProdottoDTO> pagedData = new PagedData<RichiestaProdottoDTO>(page, RichiestaProdottoDTO.class);
		return pagedData;
	}

	@Override
	@Transactional(rollbackFor = Exception.class, readOnly = true)
	public UtentiProdottiAttivatiEnteExcelReportInput buildUtentiProdottiAttivatiEnteExcelReportInput(
			Integer idProdAttivRich) throws Exception {
		
		UtentiProdottiAttivatiEnteExcelReportInput res = new UtentiProdottiAttivatiEnteExcelReportInput();
		
		Ente ente = richiestaProdottoRepository.getEnteByIdProdAttivRich(idProdAttivRich);
		
		if (ente == null) throw new MyExtranetException("Impossibile trovare l'ente associato");
		
		res.setEnte(ente.getDenominazione());
		
		List<RappresentanteEnte> raps = rappresentanteEnteRepository.findAllActiveRepresentatives(Constants.RAPPRESENTANTE_ENTE_TIPO_RAP, ente, Date.valueOf(LocalDate.now()));
		if (raps.size() > 0) {
			RappresentanteEnte rap = raps.get(0);
			
			UtentiProdottiAttivatiEnteExcelReportInput.Utente rappresentante = ObjectMapperUtils.map(rap.getUtente(), UtentiProdottiAttivatiEnteExcelReportInput.Utente.class);
			rappresentante.setRuolo("Referente dell'Ente");
			res.setRappresentante(rappresentante);
		}
				
		ProdottoAttivabile prodottoAttivabile = richiestaProdottoRepository.getProdottoAttivabileByIdProdAttivRich(idProdAttivRich);
		
		UtentiProdottiAttivatiEnteExcelReportInput.ProdottoUtenti prodottoUtenti = new UtentiProdottiAttivatiEnteExcelReportInput.ProdottoUtenti();
		
		prodottoUtenti.setDescrizioneProdotto(prodottoAttivabile.getNomeProdottoAttiv());

		List<UtenteRichiestaProdotto> utentiRichiesta = utenteRichiestaProdottoRepository.findAllByRichiestaProdottoIdProdAttivRich(idProdAttivRich);
		
		List<Utente> utenti = utentiRichiesta.stream().map(u -> {
			UtentiProdottiAttivatiEnteExcelReportInput.Utente utente = ObjectMapperUtils.map(u, UtentiProdottiAttivatiEnteExcelReportInput.Utente.class);
			
			it.regioneveneto.myp3.myextranet.model.Utente ut = u.getUtente();
			
			if (utente.getNome() == null) utente.setNome(ut.getNome());
			if (utente.getCognome() == null) utente.setCognome(ut.getCognome());
			if (utente.getCodFiscale() == null) utente.setCodFiscale(ut.getCodFiscale());
			if (utente.getEmail() == null) utente.setEmail(ut.getEmail());
			if (utente.getTelefono() == null) utente.setTelefono(ut.getTelefono());
			
			utente.setRuolo(u.getRuoloProdotto().getDesRuolo());
			utente.setStato(u.getRichOper());
			
			return utente;
			}).collect(Collectors.toList());
		
		prodottoUtenti.setUtenti(utenti);
		
		List<ProdottoUtenti> prodotti = new ArrayList<UtentiProdottiAttivatiEnteExcelReportInput.ProdottoUtenti>(1);
		prodotti.add(prodottoUtenti);
		
		res.setProdotti(prodotti);
		
		return res;
	}

	@Override
	@Transactional(rollbackFor = Exception.class, readOnly = false)
	public MessaggioDTO sendMessaggioAboutRichiestaProdotto(Integer idProdAttivRich, ComunicazioneRichiestaProdottoInputDTO input) throws Exception {
		
		Messaggio messaggio = ObjectMapperUtils.map(input, Messaggio.class);
		
        // set some properties
        messaggio.setTipo(Constants.MESSAGGIO_TIPO_COMUNICAZIONE);
        messaggio.setArea(Constants.MESSAGGIO_AREA_PRODOTTO);
        messaggio.setIdContenuto("" + idProdAttivRich);
        messaggio.setDestinatario("Ai collaboratori");
                
        Ente ente = richiestaProdottoRepository.getEnteByIdProdAttivRich(idProdAttivRich);
        
        it.regioneveneto.myp3.myextranet.model.Utente utenteRappresentante = null;
        
		List<RappresentanteEnte> rappresentanti = rappresentanteEnteRepository.findAllActiveRepresentatives(Constants.RAPPRESENTANTE_ENTE_TIPO_RAP, ente, Date.valueOf(LocalDate.now()));
		String rappresentanteEmailAddress = null;
		if (rappresentanti != null && rappresentanti.size() > 0) {
			utenteRappresentante = rappresentanti.get(0).getUtente();
			rappresentanteEmailAddress = utenteRappresentante.getEmail();
		}
		
		// additional addresses
		String addresses = rappresentanteEmailAddress != null ? rappresentanteEmailAddress + ";" + getProductsOfficialAddress() : getProductsOfficialAddress();
		if (StringUtils.hasText(messaggio.getIndirizzo())) {
			addresses = messaggio.getIndirizzo() + ";" + addresses;
		}
		messaggio.setIndirizzo(addresses);
		
		// prepare data for request description section of message
		StringSubstitutor substitutor = getPlaceholderSubstitutor(messaggio, idProdAttivRich, null);
		String productActivationRequestDetailTemplate = 
				myConfigService.getConcatValue(ipa + productActivationRequestDetailTemplateKey, "");
		
		String productActivationRequestDetailStr = substitutor.replace(productActivationRequestDetailTemplate);
		
		messaggio.setMessaggio(messaggio.getMessaggio() + productActivationRequestDetailStr);
		
		// attach report utenti prodotto
		Attachment[] attachments = null;
		if (Integer.valueOf(1).equals(input.getFlgAllegaUtentiRich())) {
			attachments = new Attachment[1];
			Attachment attachment = new Attachment();
			
	    	UtentiProdottiAttivatiEnteExcelReportInput reportInput;
			try {
				reportInput = buildUtentiProdottiAttivatiEnteExcelReportInput(idProdAttivRich);
			} catch (Exception e) {
				throw new MyExtranetException("Impossibile generare report utenti richiesta prodotto", e);
			}

	        byte[] report = excelReportService.generateUtentiProdottiAttivatiEnteExcelReport(reportInput);
	        attachment.setContent(report);
	        attachment.setMimeType("application/vnd.ms-excel");
	        attachment.setName("utenti_prodotto.xls");
			
			attachments[0] = attachment;
		}
		
		// insert and send messaggio
		Messaggio savedMessaggio = messaggioService.insertAndSendMessaggio(messaggio, attachments);

		if (utenteRappresentante != null) {			
			// link utente with messaggio
			Integer idMessaggio = savedMessaggio.getIdMessaggio();
			Integer[] utenteIds = { utenteRappresentante.getIdUtente() };
			
			utenteMessaggiService.linkUtentiWithMessaggio(utenteIds, idMessaggio);
		}
		
		return ObjectMapperUtils.map(savedMessaggio, MessaggioDTO.class);	

	}


	private StringSubstitutor getPlaceholderSubstitutor(Messaggio messaggio, Integer idProdAttivRich, Map<String, String> customValuesMap) {
		ProdottoAttivabile prodottoAttivabile = richiestaProdottoRepository.getProdottoAttivabileByIdProdAttivRich(idProdAttivRich);
		String idProdotto = prodottoAttivabile.getIdProdotto();
		String prodottoTitolo = null;
		String prodottoDescrizione = null;

		if (StringUtils.hasText(idProdotto)) {
			MyExtranetContent prodotto = myPortalService.getContentById(idProdotto);
			prodottoTitolo = prodotto.getTitolo();
			prodottoDescrizione = prodotto.getDescrizione();
		} else {
			prodottoTitolo = prodottoAttivabile.getNomeProdottoAttiv();
			prodottoDescrizione = prodottoAttivabile.getDesAttivazioneBreve();
		}
		
		RichiestaProdotto richiestaProdotto = richiestaProdottoRepository.findById(idProdAttivRich).get();
		
		ProcedimentoProdotto procedimentoProdotto = richiestaProdotto.getProcedimentoProdotto();
		
		Map<String, String> valuesMap = new HashMap();
		valuesMap.putAll(Map.of(
                "prodotto.titolo", it.regioneveneto.myp3.myextranet.utils.StringUtils.stringOrEmpty(prodottoTitolo),
                "prodotto.descrizione", it.regioneveneto.myp3.myextranet.utils.StringUtils.stringOrEmpty(prodottoDescrizione),
                "procedimentoprodotto.descrizione", procedimentoProdotto != null ? procedimentoProdotto.getDesProdottoProc() : "",
                "richiestaprodotto.stato", richiestaProdotto.getCodStato()
        ));
		
		if (customValuesMap != null) {
			valuesMap.putAll(customValuesMap);
		}
		
		return new StringSubstitutor(valuesMap);
	}

	private String getProductsOfficialAddress() {
		return myConfigService.getConcatValue(ipa + responsabileProdottiEmailAddressKey, "");
	}

}
