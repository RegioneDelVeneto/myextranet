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
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.regioneveneto.myp3.myextranet.model.AuditWithValidityModel;
import it.regioneveneto.myp3.myextranet.model.Ente;
import it.regioneveneto.myp3.myextranet.model.Utente;
import it.regioneveneto.myp3.myextranet.repository.UtenteRepository;
import it.regioneveneto.myp3.myextranet.service.MessaggioService;
import it.regioneveneto.myp3.myextranet.service.UtenteMessaggiService;
import it.regioneveneto.myp3.myextranet.service.UtenteService;
import it.regioneveneto.myp3.myextranet.utils.AuditUtils;
import it.regioneveneto.myp3.myextranet.utils.BeanUtils;
import it.regioneveneto.myp3.myextranet.utils.Constants;
import it.regioneveneto.myp3.myextranet.utils.ObjectMapperUtils;
import it.regioneveneto.myp3.myextranet.utils.RepositoryUtils;
import it.regioneveneto.myp3.myextranet.web.dto.CollaboratoreProgettoDTO;
import it.regioneveneto.myp3.myextranet.web.dto.EnteDTO;
import it.regioneveneto.myp3.myextranet.web.dto.MessaggioDTO;
import it.regioneveneto.myp3.myextranet.web.dto.PagedData;
import it.regioneveneto.myp3.myextranet.web.dto.UtenteDTO;
import it.regioneveneto.myp3.myextranet.web.dto.UtenteFilterDTO;

@Service
public class UtenteServiceImpl implements UtenteService {
    private static final Logger LOG = LoggerFactory.getLogger(UtenteServiceImpl.class);

    @Autowired
    UtenteRepository utenteRepository;
    
    @Autowired
    MessaggioService messaggioService;
    
    @Autowired
    UtenteMessaggiService utenteMessaggiService;
    
    @Autowired
    CacheManager cacheManager;

	@Override
	@Transactional(rollbackFor = Exception.class, readOnly = true)
	public PagedData<UtenteDTO> getUtenti(int pageNumber, int pageSize, String sortProperty, String sortDirection) {
		if (sortProperty == null) sortProperty = "cognome";
		if (sortDirection == null) sortDirection = "ASC";
		
		Pageable pageable = PagedData.buildPageable(pageNumber, pageSize, sortProperty, sortDirection);
		
		Page<Utente> page = utenteRepository.findAll(pageable);

		PagedData<UtenteDTO> pagedData = new PagedData<UtenteDTO>(page, UtenteDTO.class);
		return pagedData;
	}

	@Override
	@Transactional(rollbackFor = Exception.class, readOnly = true)
	public PagedData<UtenteDTO> getUtenti(UtenteFilterDTO utenteFilter, int pageNumber, int pageSize,
			String sortProperty, String sortDirection) {
		if (sortProperty == null) sortProperty = "cognome";
		if (sortDirection == null) sortDirection = "ASC";
		
		Pageable pageable = PagedData.buildPageable(pageNumber, pageSize, sortProperty, sortDirection);
		
		Page<Utente> page = utenteRepository.findAll(RepositoryUtils.buildUtenteFilterSpecification(utenteFilter) , pageable);

		PagedData<UtenteDTO> pagedData = new PagedData<UtenteDTO>(page, UtenteDTO.class);
		return pagedData;
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class, readOnly = true)
	public UtenteDTO getUtente(Integer id ) throws Exception {
			
		Optional<Utente> utente = utenteRepository.findById(id);
		
		if(utente.isPresent()) {

			UtenteDTO utenteData = ObjectMapperUtils.map(utente.get(), UtenteDTO.class);
			return utenteData;
		}
		else {
			throw new Exception("User not found");
		}
		
	}

	@Override
	@Transactional(rollbackFor = Exception.class, readOnly = false)
	public UtenteDTO insertUtente(@Valid UtenteDTO newUtente) {
		
		Utente utente = ObjectMapperUtils.map(newUtente, Utente.class);
		
		// add audit fields
		AuditWithValidityModel audit = (AuditWithValidityModel) utente;
		AuditUtils.fillAudit(audit);
		
		// new utente has flgArchived = 0
		utente.setFlgArchived(0);
		
		Utente savedUtente = utenteRepository.save(utente);
		
		// invalidate cache
		evictUserValidityFromCache(savedUtente.getCodFiscale());
		
		return ObjectMapperUtils.map(savedUtente, UtenteDTO.class);
	}

	@Override
	@Transactional(rollbackFor = Exception.class, readOnly = false)
	public UtenteDTO updateUtente(@Valid UtenteDTO patchUtente, boolean isPatch) throws Exception {
		
		// retrieve original
		Optional<Utente> originalUtenteOptional = utenteRepository.findById(patchUtente.getIdUtente());
		if (!originalUtenteOptional.isPresent()) {
			throw new Exception("Utente not found");
		}
		Utente originalUtente = originalUtenteOptional.get();
		
		// check if ente has changed
		boolean enteHasChanged = hasEnteChanged(patchUtente, originalUtente);
		
		Utente utente = UtenteDTO.patchUtente(patchUtente, originalUtente, isPatch, true);
		
		if (enteHasChanged) {
			// save historical record. Two steps to ensure detachment from DB
			UtenteDTO historicalDTO = ObjectMapperUtils.map(originalUtente, UtenteDTO.class);
			Utente historical = ObjectMapperUtils.map(historicalDTO, Utente.class);
			historical.setIdUtente(null);
			historical.setDtInizioVal(originalUtente.getDtInizioVal());
			historical.setDtFineVal(Date.valueOf(LocalDate.now()));
			historical.setFlgArchived(1);
			historical.setDtIns(originalUtente.getDtIns());
			historical.setIdOperIns(originalUtente.getIdOperIns());
			historical.setOperIns(originalUtente.getOperIns());
			historical.setDtUltMod(originalUtente.getDtUltMod());
			historical.setIdOperUltMod(originalUtente.getIdOperUltMod());
			historical.setOperUltMod(originalUtente.getOperUltMod());
			
			Utente savedHistorical = utenteRepository.save(historical);
			
			utente.setUtentePadre(savedHistorical);
		}
		
		// set audit fields
		AuditWithValidityModel audit = (AuditWithValidityModel) utente;
		AuditUtils.fillAudit(audit);
		
		Utente savedUtente = utenteRepository.save(utente);
		
		// invalidate cache
		evictUserValidityFromCache(originalUtente.getCodFiscale());
		
		return ObjectMapperUtils.map(savedUtente, UtenteDTO.class);
	}
	
	private boolean hasEnteChanged(UtenteDTO utenteDto, Utente utente) {
		EnteDTO enteDto = utenteDto.getEnte();
		Ente ente = utente.getEnte();
		
		if (ente == null && enteDto == null) {
			return false;
		}
		
		if (ente != null) {
			if (enteDto == null) {
				return true;
			} else {
				return !BeanUtils.isEqual(ente.getIdEnte(), enteDto.getIdEnte());
			}
		} else {
			return true;
		}
	
	}

	@Override
	@Transactional(rollbackFor = Exception.class, readOnly = false)
	public MessaggioDTO sendMessaggioToUtenti(UtenteFilterDTO utenteFilter, MessaggioDTO messaggio) throws Exception {
		
		// get list of utenti
		String sortProperty = "cognome";
		String sortDirection = "ASC";
		
		Pageable pageable = PagedData.buildPageable(0, 999999, sortProperty, sortDirection);
		
		Page<Utente> page = utenteRepository.findAll(RepositoryUtils.buildUtenteFilterSpecification(utenteFilter) , pageable);
		
		List<Utente> utenti = page.toList();
		
		// concatenate email addresses
		String[] indirizzi = utenti.stream()
				.map(i -> i.getEmail()).toArray(String[]::new);
		
		String indirizzo = String.join(";", indirizzi);
		messaggio.setIndirizzo(indirizzo);
		
		// insert and send messaggio
		MessaggioDTO savedMessaggio = messaggioService.insertAndSendMessaggio(messaggio);
		
		// link utenti with messaggio
		Integer idMessaggio = savedMessaggio.getIdMessaggio();
		
		Integer[] utentiIds = utenti.stream()
				.map(i -> i.getIdUtente()).toArray(Integer[]::new);
		
		utenteMessaggiService.linkUtentiWithMessaggio(utentiIds, idMessaggio);
		
		return savedMessaggio;
	}

	@Override
	@Cacheable(value = Constants.CACHE_NAME_USER_VALIDITY, key = "#fiscalCode")
	public Map<String, Object> getUserValidityProperties(String fiscalCode) {
		
		LOG.debug(String.format("*** Calling getUserValidityProperties(\"%s\")", fiscalCode));
		
		boolean userExpired = false;
		boolean tenantExpired = false;
		Integer userId = null;
		
		Utente utente = utenteRepository.findFirstByCodFiscaleAndFlgArchivedOrderByDtInizioValDesc(fiscalCode, 0);
		if (utente == null) {
			
		} else {
			userId = utente.getIdUtente();
			LocalDate now = LocalDate.now();
			
			if (utente.getDtFineVal() != null && utente.getDtFineVal().before(Date.valueOf(now))) {
				// expired user
				userExpired = true;
			} else if (utente.getEnte() != null && utente.getEnte().getDtFineVal() != null && utente.getEnte().getDtFineVal().before(Date.valueOf(now))) {
				// expired ente
				tenantExpired = true;
			}
		}
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("userExpired", userExpired);
		map.put("tenantExpired", tenantExpired);
		map.put("userId", userId);
		return map;
	}
	
	public void evictUserValidityFromCache(String code) {
		LOG.debug(String.format("Evicting validity from cache for code \"%s\"", code));
    	Cache cache = cacheManager.getCache(Constants.CACHE_NAME_USER_VALIDITY);
    	if (cache != null) {
    		cache.evict(code);
    		LOG.debug(String.format("Validity for code \"%s\" evicted from cache \"%s\"", code, Constants.CACHE_NAME_USER_VALIDITY));
    	} else {
    		LOG.debug(String.format("Cache \"%s\" not found", Constants.CACHE_NAME_USER_VALIDITY));
    	}
    }
}
