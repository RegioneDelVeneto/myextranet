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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.regioneveneto.myp3.myextranet.model.Comune;
import it.regioneveneto.myp3.myextranet.repository.ComuneRepository;
import it.regioneveneto.myp3.myextranet.service.ComuneService;
import it.regioneveneto.myp3.myextranet.utils.RepositoryUtils;
import it.regioneveneto.myp3.myextranet.web.dto.ComuneDTO;
import it.regioneveneto.myp3.myextranet.web.dto.ComuneFilterDTO;
import it.regioneveneto.myp3.myextranet.web.dto.PagedData;
import it.regioneveneto.myp3.myextranet.web.dto.ProvinciaDTO;

@Service
public class ComuneServiceImpl implements ComuneService {
    private static final Logger LOG = LoggerFactory.getLogger(ComuneServiceImpl.class);

    @Autowired
    ComuneRepository comuneRepository;

	@Override
	@Transactional(rollbackFor = Exception.class, readOnly = true)
	public PagedData<ComuneDTO> getComuniByProvincia(ProvinciaDTO provincia, int pageNumber, int pageSize, String sortProperty, String sortDirection) {
		if (sortProperty == null) sortProperty = "desComune";
		if (sortDirection == null) sortDirection = "ASC";
		
		Pageable pageable = PagedData.buildPageable(pageNumber, pageSize, sortProperty, sortDirection);
		
		Page<Comune> page = comuneRepository.findByProvincia_codProvincia(provincia.getCodProvincia(), pageable);

		PagedData<ComuneDTO> pagedData = new PagedData<ComuneDTO>(page, ComuneDTO.class);
		return pagedData;
	}

	@Override
	@Transactional(rollbackFor = Exception.class, readOnly = true)
	public PagedData<ComuneDTO> getComuni(ComuneFilterDTO comuneFilter, int pageNumber, int pageSize,
			String sortProperty, String sortDirection) {
		if (sortProperty == null) sortProperty = "desComune";
		if (sortDirection == null) sortDirection = "ASC";
		
		Pageable pageable = PagedData.buildPageable(pageNumber, pageSize, sortProperty, sortDirection);
		
		Page<Comune> page = comuneRepository.findAll(RepositoryUtils.buildComuneFilterSpecification(comuneFilter), pageable);

		PagedData<ComuneDTO> pagedData = new PagedData<ComuneDTO>(page, ComuneDTO.class);
		return pagedData;
	}
}
