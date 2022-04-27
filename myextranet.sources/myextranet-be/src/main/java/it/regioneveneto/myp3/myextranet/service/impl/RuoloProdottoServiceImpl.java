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

import it.regioneveneto.myp3.myextranet.model.RuoloProdotto;
import it.regioneveneto.myp3.myextranet.repository.RuoloProdottoRepository;
import it.regioneveneto.myp3.myextranet.service.RuoloProdottoService;
import it.regioneveneto.myp3.myextranet.utils.RepositoryUtils;
import it.regioneveneto.myp3.myextranet.web.dto.PagedData;
import it.regioneveneto.myp3.myextranet.web.dto.RuoloProdottoDTO;

@Service
public class RuoloProdottoServiceImpl implements RuoloProdottoService {
    private static final Logger LOG = LoggerFactory.getLogger(RuoloProdottoServiceImpl.class);

    @Autowired
    RuoloProdottoRepository ruoloProdottoRepository;

	@Override
	@Transactional(rollbackFor = Exception.class, readOnly = true)
	public PagedData<RuoloProdottoDTO> getRuoliProdotto(RuoloProdottoDTO ruoloProdottoFilter, int pageNumber,
			int pageSize, String sortProperty, String sortDirection) {
		if (sortProperty == null) sortProperty = "codRuolo";
		if (sortDirection == null) sortDirection = "ASC";
		
		Pageable pageable = PagedData.buildPageable(pageNumber, pageSize, sortProperty, sortDirection);
		
		Page<RuoloProdotto> page = ruoloProdottoRepository.findAll(RepositoryUtils.buildRuoloProdottoFilterSpecification(ruoloProdottoFilter), pageable);

		PagedData<RuoloProdottoDTO> pagedData = new PagedData<RuoloProdottoDTO>(page, RuoloProdottoDTO.class);
		return pagedData;

	}
}
