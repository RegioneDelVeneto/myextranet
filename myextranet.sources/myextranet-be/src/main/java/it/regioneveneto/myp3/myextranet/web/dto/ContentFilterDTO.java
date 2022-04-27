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

import java.util.Date;

public class ContentFilterDTO {

	private String titolo;
	private String descrizione;
	private String prodotto;
	private String progetto;
	private Long dataDa;
	private Long dataA;
	private String parent;
	private Boolean myext_attivabile;
	
	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

	public String getProdotto() {
		return prodotto;
	}

	public void setProdotto(String prodotto) {
		this.prodotto = prodotto;
	}

	public String getProgetto() {
		return progetto;
	}

	public void setProgetto(String progetto) {
		this.progetto = progetto;
	}

	public Long getDataDa() {
		return dataDa;
	}

	public void setDataDa(Long dataDa) {
		this.dataDa = dataDa;
	}

	public Long getDataA() {
		return dataA;
	}

	public void setDataA(Long dataA) {
		this.dataA = dataA;
	}

	public Boolean getMyext_attivabile() {
		return myext_attivabile;
	}

	public void setMyext_attivabile(Boolean myext_attivabile) {
		this.myext_attivabile = myext_attivabile;
	}

	public String getTitolo() {
		return titolo;
	}

	public void setTitolo(String titolo) {
		this.titolo = titolo;
	}

	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	@Override
	public String toString() {
		return "ContentFilterDTO [titolo=" + titolo + ", descrizione=" + descrizione + ", prodotto=" + prodotto
				+ ", progetto=" + progetto + ", dataDa=" + dataDa + ", dataA=" + dataA + ", parent=" + parent
				+ ", myext_attivabile=" + myext_attivabile + "]";
	}


	

}
