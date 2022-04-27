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

import it.regioneveneto.myp3.myextranet.bean.IStatProdottiRow;
import it.regioneveneto.myp3.myextranet.bean.StatProdottiRow;
import it.regioneveneto.myp3.myextranet.model.AuditModel;
import it.regioneveneto.myp3.myextranet.model.IscrittoEvento;
import it.regioneveneto.myp3.myextranet.model.ProdottoAttivabile;
import it.regioneveneto.myp3.myextranet.repository.ProdottoAttivabileRepository;
import it.regioneveneto.myp3.myextranet.service.ProdottoAttivabileService;
import it.regioneveneto.myp3.myextranet.utils.AuditUtils;
import it.regioneveneto.myp3.myextranet.utils.Constants;
import it.regioneveneto.myp3.myextranet.utils.ObjectMapperUtils;
import it.regioneveneto.myp3.myextranet.utils.RepositoryUtils;
import it.regioneveneto.myp3.myextranet.web.dto.IscrittoEventoDTO;
import it.regioneveneto.myp3.myextranet.web.dto.PagedData;
import it.regioneveneto.myp3.myextranet.web.dto.ProdottoAttivabileDTO;
import it.regioneveneto.myp3.myextranet.web.dto.ProdottoAttivabileFilterDTO;

@Service
public class ProdottoAttivabileServiceImpl implements ProdottoAttivabileService {
    private static final Logger LOG = LoggerFactory.getLogger(ProdottoAttivabileServiceImpl.class);

    @Autowired
    ProdottoAttivabileRepository prodottoAttivabileRepository;   
    


	public ProdottoAttivabileServiceImpl() {
		super();
	}

	@Override
	@Transactional(rollbackFor = Exception.class, readOnly = true)
	public PagedData<ProdottoAttivabileDTO> getProdottiAttivabili(ProdottoAttivabileFilterDTO prodottoAttivabileFilter, int pageNumber,
			int pageSize, String sortProperty, String sortDirection) {
		if (sortProperty == null) sortProperty = "dtAttivabileDa";
		if (sortDirection == null) sortDirection = "ASC";
		
		Pageable pageable = PagedData.buildPageable(pageNumber, pageSize, sortProperty, sortDirection);
		
		Page<ProdottoAttivabile> page = prodottoAttivabileRepository.findAll(RepositoryUtils.buildProdottoAttivabileFilterSpecification(prodottoAttivabileFilter), pageable);

		PagedData<ProdottoAttivabileDTO> pagedData = new PagedData<ProdottoAttivabileDTO>(page, ProdottoAttivabileDTO.class);
		return pagedData;

	}
	
	@Override
	@Transactional(rollbackFor = Exception.class, readOnly = true)
	public ProdottoAttivabileDTO getProdottoAttivabile(Integer idProdottoAtt) throws Exception {
								
		Optional<ProdottoAttivabile> prodottoAttivabileOptional = prodottoAttivabileRepository.findById(idProdottoAtt);

		if(prodottoAttivabileOptional.isPresent()) {
			ProdottoAttivabileDTO prodottoAttivabile = ObjectMapperUtils.map(prodottoAttivabileOptional.get(), ProdottoAttivabileDTO.class);
			return prodottoAttivabile;
		} else {
			throw new Exception("ProdottoAttivabile not found");
		}

	}

	@Override
	@Transactional(rollbackFor = Exception.class, readOnly = false)
	public ProdottoAttivabileDTO insertProdottoAttivabile(@Valid ProdottoAttivabileDTO newProdottoAttivabile) throws Exception {
		
		ProdottoAttivabile prodottoAttivabile = ObjectMapperUtils.map(newProdottoAttivabile, ProdottoAttivabile.class);
		
		// add audit fields
		AuditModel audit = (AuditModel) prodottoAttivabile;
		AuditUtils.fillAudit(audit);
		
		fillDefaults(prodottoAttivabile);
				
		// check if there is another record for the same prodotto already
		checkForExistingRecord(prodottoAttivabile);
		
		ProdottoAttivabile savedProdottoAttivabile = prodottoAttivabileRepository.save(prodottoAttivabile);
		
		return ObjectMapperUtils.map(savedProdottoAttivabile, ProdottoAttivabileDTO.class);
	}

	private void checkForExistingRecord(ProdottoAttivabile prodottoAttivabile) throws Exception {
		String idProdotto = prodottoAttivabile.getIdProdotto();
		
		if (idProdotto == null) return;
		
		Optional<ProdottoAttivabile> record = prodottoAttivabileRepository.findOneByIdProdotto(idProdotto);
		if (record.isPresent()) {
			throw new Exception("Prodotto already present");
		}
	}

	private void fillDefaults(ProdottoAttivabile prodottoAttivabile) {
		LocalDate today = LocalDate.now();
		
		if (prodottoAttivabile.getDtAttivabileDa() == null) {
			prodottoAttivabile.setDtAttivabileDa(today);
		}
		if (prodottoAttivabile.getDtAttivabileA() == null) {
			prodottoAttivabile.setDtAttivabileA(Constants.DEFAULT_DATE_END_VALIDITY);
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class, readOnly = false)
	public ProdottoAttivabileDTO updateProdottoAttivabile(ProdottoAttivabileDTO patchProdottoAttivabile, boolean isPatch) throws Exception {
		
		// retrieve original
		Optional<ProdottoAttivabile> originalProdottoAttivabileOptional = prodottoAttivabileRepository.findById(patchProdottoAttivabile.getIdProdottoAtt());
		if (!originalProdottoAttivabileOptional.isPresent()) {
			throw new Exception("Prodotto attivabile not found");
		}
		ProdottoAttivabile originalProdottoAttivabile = originalProdottoAttivabileOptional.get();
		
		ProdottoAttivabile prodottoAttivabile = ProdottoAttivabileDTO.patchProdottoAttivabile(patchProdottoAttivabile, originalProdottoAttivabile, isPatch, true);
		
		// set audit fields
		AuditModel audit = (AuditModel) prodottoAttivabile;
		AuditUtils.fillAudit(audit);
				
		fillDefaults(prodottoAttivabile);
		
		ProdottoAttivabile savedProdottoAttivabile = prodottoAttivabileRepository.save(prodottoAttivabile);
		
		return ObjectMapperUtils.map(savedProdottoAttivabile, ProdottoAttivabileDTO.class);

	}
	
	@Override
	@Transactional(rollbackFor = Exception.class, readOnly = true)
	public List<StatProdottiRow> getStatProdottiRows() {
		List<IStatProdottiRow> results = prodottoAttivabileRepository.getStatProdottiRows();
		
		List<StatProdottiRow> convertedResults = ObjectMapperUtils.mapAll(results, StatProdottiRow.class);
		
		return convertedResults;
	}

	@Override
	@Transactional(rollbackFor = Exception.class, readOnly = false)
	public ProdottoAttivabileDTO deleteProdottoAttivabile(Integer idProdottoAtt) throws Exception {
		// retrieve original
		Optional<ProdottoAttivabile> originalProdottoAttivabileOptional = prodottoAttivabileRepository.findById(idProdottoAtt);
		if (!originalProdottoAttivabileOptional.isPresent()) {
			throw new Exception("Prodotto not found");
		}
		ProdottoAttivabile originalProdottoAttivabile = originalProdottoAttivabileOptional.get();
		ProdottoAttivabileDTO prodottoAttivabile = ObjectMapperUtils.map(originalProdottoAttivabile, ProdottoAttivabileDTO.class);
		
		prodottoAttivabileRepository.delete(originalProdottoAttivabile);
				
		return prodottoAttivabile;
	}

}
