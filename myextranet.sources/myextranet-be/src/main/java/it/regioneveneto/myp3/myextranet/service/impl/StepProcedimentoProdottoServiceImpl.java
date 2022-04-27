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

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.regioneveneto.myp3.myextranet.model.StepProcedimentoProdotto;
import it.regioneveneto.myp3.myextranet.repository.StepProcedimentoProdottoRepository;
import it.regioneveneto.myp3.myextranet.service.StepProcedimentoProdottoService;
import it.regioneveneto.myp3.myextranet.utils.ObjectMapperUtils;
import it.regioneveneto.myp3.myextranet.utils.RepositoryUtils;
import it.regioneveneto.myp3.myextranet.web.dto.PagedData;
import it.regioneveneto.myp3.myextranet.web.dto.StepProcedimentoProdottoDTO;
import it.regioneveneto.myp3.myextranet.web.dto.StepProcedimentoProdottoFilterDTO;

@Service
public class StepProcedimentoProdottoServiceImpl implements StepProcedimentoProdottoService {
    private static final Logger LOG = LoggerFactory.getLogger(StepProcedimentoProdottoServiceImpl.class);

    @Autowired
    StepProcedimentoProdottoRepository stepProcedimentoProdottoRepository;   
    
	public StepProcedimentoProdottoServiceImpl() {
		super();
	}

	@Override
	@Transactional(rollbackFor = Exception.class, readOnly = true)
	public PagedData<StepProcedimentoProdottoDTO> getStepProcedimentoProdotto(StepProcedimentoProdottoFilterDTO stepProcedimentoProdottoFilter, int pageNumber,
			int pageSize, String sortProperty, String sortDirection) {
		if (sortProperty == null) sortProperty = "idStatoConf";
		if (sortDirection == null) sortDirection = "ASC";
		
		Pageable pageable = PagedData.buildPageable(pageNumber, pageSize, sortProperty, sortDirection);
		
		Page<StepProcedimentoProdotto> page = stepProcedimentoProdottoRepository.findAll(RepositoryUtils.buildStepProcedimentoProdottoFilterSpecification(stepProcedimentoProdottoFilter), pageable);

		PagedData<StepProcedimentoProdottoDTO> pagedData = new PagedData<StepProcedimentoProdottoDTO>(page, StepProcedimentoProdottoDTO.class);
		return pagedData;

	}
	
	@Override
	@Transactional(rollbackFor = Exception.class, readOnly = true)
	public StepProcedimentoProdottoDTO getStepProcedimentoProdotto(Integer idStatoConf) throws Exception {
								
		Optional<StepProcedimentoProdotto> stepProcedimentoProdottoOptional = stepProcedimentoProdottoRepository.findById(idStatoConf);

		if(stepProcedimentoProdottoOptional.isPresent()) {
			StepProcedimentoProdottoDTO stepProcedimentoProdotto = ObjectMapperUtils.map(stepProcedimentoProdottoOptional.get(), StepProcedimentoProdottoDTO.class);
			return stepProcedimentoProdotto;
		} else {
			throw new Exception("StepProcedimentoProdotto not found");
		}

	}

}
