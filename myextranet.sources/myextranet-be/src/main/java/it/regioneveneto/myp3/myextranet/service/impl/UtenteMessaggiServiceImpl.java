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

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.regioneveneto.myp3.myextranet.model.AuditModel;
import it.regioneveneto.myp3.myextranet.model.UtenteMessaggi;
import it.regioneveneto.myp3.myextranet.model.key.UtenteMessaggiKey;
import it.regioneveneto.myp3.myextranet.repository.UtenteMessaggiRepository;
import it.regioneveneto.myp3.myextranet.service.UtenteMessaggiService;
import it.regioneveneto.myp3.myextranet.utils.AuditUtils;
import it.regioneveneto.myp3.myextranet.utils.ObjectMapperUtils;
import it.regioneveneto.myp3.myextranet.utils.RepositoryUtils;
import it.regioneveneto.myp3.myextranet.web.dto.MessaggioDTO;
import it.regioneveneto.myp3.myextranet.web.dto.PagedData;
import it.regioneveneto.myp3.myextranet.web.dto.UtenteDTO;
import it.regioneveneto.myp3.myextranet.web.dto.UtenteMessaggiDTO;
import it.regioneveneto.myp3.myextranet.web.dto.UtenteMessaggiFilterDTO;

@Service
public class UtenteMessaggiServiceImpl implements UtenteMessaggiService {
	
	@Autowired
	UtenteMessaggiRepository utenteMessaggiRepository;

	@Override
	@Transactional(rollbackFor = Exception.class, readOnly = true)
	public PagedData<UtenteMessaggiDTO> getUtentiMessaggi(UtenteMessaggiFilterDTO utenteMessaggiFilter, int pageNumber,
			int pageSize, String sortProperty, String sortDirection) {
		if (sortProperty == null) sortProperty = "flgOnline";
		if (sortDirection == null) sortDirection = "ASC";
		
		Pageable pageable = PagedData.buildPageable(pageNumber, pageSize, sortProperty, sortDirection);
		
		Page<UtenteMessaggi> page = utenteMessaggiRepository.findAll(RepositoryUtils.buildUtenteMessaggiFilterSpecification(utenteMessaggiFilter) , pageable);

		PagedData<UtenteMessaggiDTO> pagedData = new PagedData<UtenteMessaggiDTO>(page, UtenteMessaggiDTO.class);
		return pagedData;
	}

	@Override
	@Transactional(rollbackFor = Exception.class, readOnly = false)
	public UtenteMessaggiDTO insertUtenteMessaggi(@Valid UtenteMessaggiDTO newUtenteMessaggi) {
		
		UtenteMessaggi utenteMessaggi = ObjectMapperUtils.map(newUtenteMessaggi, UtenteMessaggi.class);
		
		// add audit fields
		AuditModel audit = (AuditModel) utenteMessaggi;
		AuditUtils.fillAudit(audit);
		
		UtenteMessaggi savedUtenteMessaggi = utenteMessaggiRepository.save(utenteMessaggi);
		
		return ObjectMapperUtils.map(savedUtenteMessaggi, UtenteMessaggiDTO.class);
	}

	@Override
	@Transactional(rollbackFor = Exception.class, readOnly = false)
	public UtenteMessaggiDTO updateUtenteMessaggi(@Valid UtenteMessaggiDTO patchUtenteMessaggi, boolean isPatch)
			throws Exception {
		
		// retrieve original
		UtenteMessaggiKey utenteMessaggiKey = new UtenteMessaggiKey(patchUtenteMessaggi.getUtente().getIdUtente(), patchUtenteMessaggi.getMessaggio().getIdMessaggio());
		Optional<UtenteMessaggi> originalUtenteMessaggiOptional = utenteMessaggiRepository.findById(utenteMessaggiKey);
		if (!originalUtenteMessaggiOptional.isPresent()) {
			throw new Exception("UtenteMessaggi not found");
		}
		UtenteMessaggi originalUtenteMessaggi = originalUtenteMessaggiOptional.get();
		
		UtenteMessaggi utenteMessaggi = UtenteMessaggiDTO.patchUtenteMessaggi(patchUtenteMessaggi, originalUtenteMessaggi, isPatch, true);
		
		// set audit fields
		AuditModel audit = (AuditModel) utenteMessaggi;
		AuditUtils.fillAudit(audit);
		
		UtenteMessaggi savedUtenteMessaggi = utenteMessaggiRepository.save(utenteMessaggi);
		
		return ObjectMapperUtils.map(savedUtenteMessaggi, UtenteMessaggiDTO.class);
	}

	@Override
	public void linkUtentiWithMessaggio(Integer[] utentiIds, Integer idMessaggio) throws Exception {
		UtenteMessaggiDTO utenteMessaggi;
		
		// remove duplicates
		utentiIds = removeDuplicates(utentiIds);
		
		for (Integer idUtente : utentiIds) {
			utenteMessaggi = new UtenteMessaggiDTO();
			utenteMessaggi.setFlgOnline(1);
			utenteMessaggi.setFlgReadOnline(0);
			
			// utente
			UtenteDTO utente = new UtenteDTO();
			utente.setIdUtente(idUtente);
			
			// messaggio
			MessaggioDTO messaggio = new MessaggioDTO();
			messaggio.setIdMessaggio(idMessaggio);
			
			utenteMessaggi.setUtente(utente);
			utenteMessaggi.setMessaggio(messaggio);
			
			insertUtenteMessaggi(utenteMessaggi);
		}
	}

	private Integer[] removeDuplicates(Integer[] utentiIds) {
		LinkedHashSet<Integer> set = new LinkedHashSet<Integer>(Arrays.asList(utentiIds));
		
		return set.toArray(new Integer[] {});
	}

}
