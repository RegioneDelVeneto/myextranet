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

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.regioneveneto.myp3.myextranet.bean.IStatNomeValoreRow;
import it.regioneveneto.myp3.myextranet.bean.StatNomeValoreRow;
import it.regioneveneto.myp3.myextranet.repository.ProdottoAttivabileRepository;
import it.regioneveneto.myp3.myextranet.repository.ProdottoAttivatoRepository;
import it.regioneveneto.myp3.myextranet.repository.UtenteProdottoAttivatoRepository;
import it.regioneveneto.myp3.myextranet.service.StatisticheService;
import it.regioneveneto.myp3.myextranet.utils.ObjectMapperUtils;

@Service
public class StatisticheServiceImpl implements StatisticheService {
	private static final Logger LOG = LoggerFactory.getLogger(StatisticheServiceImpl.class);
	
    @Autowired
    ProdottoAttivatoRepository prodottoAttivatoRepository;   
    
    @Autowired
    ProdottoAttivabileRepository prodottoAttivabileRepository;
    
    @Autowired
    UtenteProdottoAttivatoRepository utenteProdottoAttivatoRepository;


	public StatisticheServiceImpl() {
		
	}

	@Override
	@Transactional(rollbackFor = Exception.class, readOnly = true)
	public List<StatNomeValoreRow> getStatGeneraliRows() {
		
		List<StatNomeValoreRow> list = new ArrayList<StatNomeValoreRow>();
		
		// enti serviti
		IStatNomeValoreRow entiServiti = prodottoAttivatoRepository.getNumEntiServiti();
		list.add(ObjectMapperUtils.map(entiServiti, StatNomeValoreRow.class));
		
		// prodotti erogati
		IStatNomeValoreRow prodottiErogati = prodottoAttivabileRepository.getNumProdottiErogati();
		list.add(ObjectMapperUtils.map(prodottiErogati, StatNomeValoreRow.class));
		
		// operatori prodotti
		IStatNomeValoreRow operatoriProdotti = utenteProdottoAttivatoRepository.getNumOperatoriProdotti();
		list.add(ObjectMapperUtils.map(operatoriProdotti, StatNomeValoreRow.class));

		return list;
	}

}
