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

public class RappresentanteEnteFilterDTO implements Serializable {

	private String nomeUtente;
	private String cognomeUtente;
	private Integer idEnte;
	private Integer flgConferma;
	private String tipoRappr;
	private Integer flgAttivo;
	private Integer flgRichInCorso;

	public String getNomeUtente() {
		return nomeUtente;
	}

	public void setNomeUtente(String nomeUtente) {
		this.nomeUtente = nomeUtente;
	}

	public String getCognomeUtente() {
		return cognomeUtente;
	}

	public void setCognomeUtente(String cognomeUtente) {
		this.cognomeUtente = cognomeUtente;
	}

	public Integer getIdEnte() {
		return idEnte;
	}

	public void setIdEnte(Integer idEnte) {
		this.idEnte = idEnte;
	}

	public Integer getFlgConferma() {
		return flgConferma;
	}

	public void setFlgConferma(Integer flgConferma) {
		this.flgConferma = flgConferma;
	}

	public String getTipoRappr() {
		return tipoRappr;
	}

	public void setTipoRappr(String tipoRappr) {
		this.tipoRappr = tipoRappr;
	}

	public Integer getFlgAttivo() {
		return flgAttivo;
	}

	public void setFlgAttivo(Integer flgAttivo) {
		this.flgAttivo = flgAttivo;
	}

	public Integer getFlgRichInCorso() {
		return flgRichInCorso;
	}

	public void setFlgRichInCorso(Integer flgRichInCorso) {
		this.flgRichInCorso = flgRichInCorso;
	}

	@Override
	public String toString() {
		return "RappresentanteEnteFilterDTO [nomeUtente=" + nomeUtente + ", cognomeUtente=" + cognomeUtente
				+ ", idEnte=" + idEnte + ", flgConferma=" + flgConferma + ", tipoRappr=" + tipoRappr + ", flgAttivo="
				+ flgAttivo + ", flgRichInCorso=" + flgRichInCorso + "]";
	}
	
}
