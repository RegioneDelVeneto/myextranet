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

import it.regioneveneto.myp3.myextranet.utils.StringUtils;

public class StatServiziErogatiRecordDTO extends CSVOutputProducer implements Serializable {

	private static final long serialVersionUID = 1L;

	private String tipologiaEnte;
	private int numProdotti;
	private int numServizi;

	public String getTipologiaEnte() {
		return tipologiaEnte;
	}

	public void setTipologiaEnte(String tipologiaEnte) {
		this.tipologiaEnte = tipologiaEnte;
	}

	public int getNumProdotti() {
		return numProdotti;
	}

	public void setNumProdotti(int numProdotti) {
		this.numProdotti = numProdotti;
	}

	public int getNumServizi() {
		return numServizi;
	}

	public void setNumServizi(int numServizi) {
		this.numServizi = numServizi;
	}

	@Override
	public String toString() {
		return "StatServiziErogatiRecordDTO [tipologiaEnte=" + tipologiaEnte + ", numProdotti=" + numProdotti
				+ ", numServizi=" + numServizi + "]";
	}

	@Override
	public String toCSVRow() {
		
		return 
				"\"" + StringUtils.stringOrEmpty(tipologiaEnte) + "\"" + separator +
				numProdotti + separator + 
				numServizi; 
	}
	
	@Override
	public String csvHeader() {
		return 
				"Tipologia ente" + separator +
				"Num. prodotti" + separator +
				"Num. servizi"
				;

	}
	
	public StatServiziErogatiRecordDTO() {
	}

}
