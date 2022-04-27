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
package it.regioneveneto.myp3.myextranet.web.dto;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import it.regioneveneto.myp3.myextranet.bean.StatProdottiRow;
import it.regioneveneto.myp3.myextranet.model.MyExtranetContent;
import it.regioneveneto.myp3.myextranet.utils.StringUtils;

public class StatProdottiRecordDTO extends CSVOutputProducer implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String prodotto;
	private int numEntiServiti;
	private int numOperatoriCoinvolti;

	public String getProdotto() {
		return prodotto;
	}

	public void setProdotto(String prodotto) {
		this.prodotto = prodotto;
	}

	public int getNumEntiServiti() {
		return numEntiServiti;
	}

	public void setNumEntiServiti(int numEntiServiti) {
		this.numEntiServiti = numEntiServiti;
	}

	public int getNumOperatoriCoinvolti() {
		return numOperatoriCoinvolti;
	}

	public void setNumOperatoriCoinvolti(int numOperatoriCoinvolti) {
		this.numOperatoriCoinvolti = numOperatoriCoinvolti;
	}

	@Override
	public String toString() {
		return "StatProdottiRecordDTO [prodotto=" + prodotto + ", numEntiServiti=" + numEntiServiti
				+ ", numOperatoriCoinvolti=" + numOperatoriCoinvolti + "]";
	}

	@Override
	public String toCSVRow() {
		
		return 
				"\"" + StringUtils.stringOrEmpty(prodotto) + "\"" + separator +
				numEntiServiti + separator + 
				numOperatoriCoinvolti; 
	}
	
	

	@Override
	public String csvHeader() {
		return 
				"Prodotto" + separator +
				"Num. enti serviti" + separator +
				"Num. operatori coinvolti"
				;

	}
	
	public static StatProdottiRecordDTO fromStatProdottiRow(StatProdottiRow row) {
		StatProdottiRecordDTO record = new StatProdottiRecordDTO();
		
		record.setProdotto(row.getProdotto());
		record.setNumEntiServiti(row.getNumEntiServiti());
		record.setNumOperatoriCoinvolti(row.getNumOperatoriCoinvolti());
		
		return record;
	}

	public void resolveRefs(List<MyExtranetContent> refs) {
		this.setProdotto(resolveRefTitolo(refs, this.getProdotto()));
	}
	
	private String resolveRefTitolo(List<MyExtranetContent> refs, String refToResolve) {
		if (refToResolve == null) return null;
		
		Optional refTitoloOptional = refs.stream().filter(r -> r.getId().equals(refToResolve)).map(r -> r.getTitolo()).findAny();
		return refTitoloOptional.isPresent() ? (String) refTitoloOptional.get() : refToResolve;
	}



}
