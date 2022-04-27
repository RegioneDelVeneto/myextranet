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
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.regioneveneto.myp3.myextranet.exception.EnteExpiredException;
import it.regioneveneto.myp3.myextranet.exception.EnteNotPresentException;
import it.regioneveneto.myp3.myextranet.exception.MyExtranetValidationException;
import it.regioneveneto.myp3.myextranet.model.AuditWithValidityModel;
import it.regioneveneto.myp3.myextranet.model.Ente;
import it.regioneveneto.myp3.myextranet.repository.EnteRepository;
import it.regioneveneto.myp3.myextranet.service.EnteService;
import it.regioneveneto.myp3.myextranet.service.MyBoxService;
import it.regioneveneto.myp3.myextranet.utils.AuditUtils;
import it.regioneveneto.myp3.myextranet.utils.ObjectMapperUtils;
import it.regioneveneto.myp3.myextranet.utils.RepositoryUtils;
import it.regioneveneto.myp3.myextranet.web.dto.EnteDTO;
import it.regioneveneto.myp3.myextranet.web.dto.EnteFilterDTO;
import it.regioneveneto.myp3.myextranet.web.dto.PagedData;

@Service
public class EnteServiceImpl implements EnteService {
	private static final Logger LOG = LoggerFactory.getLogger(EnteServiceImpl.class);

	@Autowired
	EnteRepository enteRepository;

	@Autowired
	MyBoxService myBoxService;

	@Override
	@Transactional(rollbackFor = Exception.class, readOnly = true)
	public PagedData<EnteDTO> getEnti(int pageNumber, int pageSize, String sortProperty, String sortDirection) {
		if (sortProperty == null)
			sortProperty = "denominazione";
		if (sortDirection == null)
			sortDirection = "ASC";

		Pageable pageable = PagedData.buildPageable(pageNumber, pageSize, sortProperty, sortDirection);

		Page<Ente> page = enteRepository.findAll(pageable);

		PagedData<EnteDTO> pagedData = new PagedData<EnteDTO>(page, EnteDTO.class);
		return pagedData;
	}

	@Override
	@Transactional(rollbackFor = Exception.class, readOnly = true)
	public PagedData<EnteDTO> getEnti(EnteFilterDTO enteFilter, int pageNumber, int pageSize, String sortProperty,
			String sortDirection) {
		if (sortProperty == null)
			sortProperty = "denominazione";
		if (sortDirection == null)
			sortDirection = "ASC";

		Pageable pageable = PagedData.buildPageable(pageNumber, pageSize, sortProperty, sortDirection);

		Page<Ente> page = enteRepository.findAll(RepositoryUtils.buildEnteFilterSpecification(enteFilter), pageable);

		PagedData<EnteDTO> pagedData = new PagedData<EnteDTO>(page, EnteDTO.class);
		return pagedData;
	}

	@Override
	public void checkEnteIsValid(EnteDTO ente) throws EnteExpiredException, EnteNotPresentException {
		if (ente == null)
			return;

		Optional<Ente> foundOptional = enteRepository.findById(ente.getIdEnte());

		if (foundOptional.isPresent()) {
			LocalDate now = LocalDate.now();
			Ente e = foundOptional.get();
			if (e.getDtFineVal() != null && e.getDtFineVal().before(Date.valueOf(now))) {
				throw new EnteExpiredException("Ente expired");
			}

		} else {
			throw new EnteNotPresentException("Ente not found");
		}

	}

	@Override
	@Transactional(rollbackFor = Exception.class, readOnly = false)
	public EnteDTO insertEnte(@Valid EnteDTO newEnte) throws Exception {

		Ente ente = ObjectMapperUtils.map(newEnte, Ente.class);

		// add audit fields
		AuditWithValidityModel audit = (AuditWithValidityModel) ente;
		AuditUtils.fillAudit(audit);

		// controllo sul cod ipa su enti attivi.

		checkCodIpaEnte(ente);
		
		// if logo is null and there is a logoFileContent, store it in MyBox
		if (newEnte.getLogo() == null && newEnte.getLogoFileInputStream() != null
				&& newEnte.getLogoFileMetadata() != null) {
			String logoContentId = myBoxService.storeFileContents(newEnte.getLogoFileInputStream(),
					newEnte.getLogoFileMetadata());
			String downloadUrl = myBoxService.buildDownloadUrl(logoContentId);
			ente.setLogo(downloadUrl);
		}
		
		
		Ente savedEnte = enteRepository.save(ente);

		return ObjectMapperUtils.map(savedEnte, EnteDTO.class);
	}

	@Override
	@Transactional(rollbackFor = Exception.class, readOnly = false)
	public EnteDTO updateEnte(@Valid EnteDTO patchEnte, boolean isPatch) throws Exception {

		// retrieve original
		Optional<Ente> originalEnteOptional = enteRepository.findById(patchEnte.getIdEnte());
		if (!originalEnteOptional.isPresent()) {
			throw new Exception("Ente not found");
		}
		Ente originalEnte = originalEnteOptional.get();

		Ente ente = EnteDTO.patchEnte(patchEnte, originalEnte, isPatch, true);

		checkCodIpaEnte(ente);
		
		// set audit fields
		AuditWithValidityModel audit = (AuditWithValidityModel) ente;
		AuditUtils.fillAudit(audit);

		// if there is a logoFileContent, store it in MyBox
		if (patchEnte.getLogoFileInputStream() != null && patchEnte.getLogoFileMetadata() != null) {
			String logoContentId = myBoxService.storeFileContents(patchEnte.getLogoFileInputStream(),
					patchEnte.getLogoFileMetadata());
			String downloadUrl = myBoxService.buildDownloadUrl(logoContentId);
			ente.setLogo(downloadUrl);
		}

		Ente savedEnte = enteRepository.save(ente);

		return ObjectMapperUtils.map(savedEnte, EnteDTO.class);
	}

	private void checkCodIpaEnte(Ente ente) throws MyExtranetValidationException {

		String ipa = ente.getCodIpa();
		
		Integer id = ente.getIdEnte();
		
		List<Ente> enti = enteRepository.getEntiByCodIpaAndValidity(ipa);
		
		if(enti.size() >0 && enti.stream().anyMatch( e -> e.getIdEnte() != id)) {
			throw new MyExtranetValidationException("Il codice ipa inserito appartiene già ad un ente valido");
		}
		
		
	}

	@Override
	@Transactional(rollbackFor = Exception.class, readOnly = true)
	public EnteDTO getEnte(Integer idEnte) throws Exception {
		Optional<Ente> ente = enteRepository.findById(idEnte);

		if (ente.isPresent()) {
			EnteDTO enteData = ObjectMapperUtils.map(ente.get(), EnteDTO.class);
			return enteData;
		} else {
			throw new Exception("Ente not found");
		}
	}
}
