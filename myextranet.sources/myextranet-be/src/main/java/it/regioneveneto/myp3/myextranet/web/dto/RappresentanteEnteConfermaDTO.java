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

import java.time.LocalDate;

public class RappresentanteEnteConfermaDTO {
	
	private Integer idRappr;
	private Integer flgConferma;
	private String motivazione;
	private String numProtocollo;
	private LocalDate dtProtocollo;


	public RappresentanteEnteConfermaDTO() {		
	}


	public Integer getFlgConferma() {
		return flgConferma;
	}

	public void setFlgConferma(Integer flgConferma) {
		this.flgConferma = flgConferma;
	}

	public String getMotivazione() {
		return motivazione;
	}

	public void setMotivazione(String motivazione) {
		this.motivazione = motivazione;
	}

	public String getNumProtocollo() {
		return numProtocollo;
	}

	public void setNumProtocollo(String numProtocollo) {
		this.numProtocollo = numProtocollo;
	}

	public LocalDate getDtProtocollo() {
		return dtProtocollo;
	}

	public void setDtProtocollo(LocalDate dtProtocollo) {
		this.dtProtocollo = dtProtocollo;
	}


	public Integer getIdRappr() {
		return idRappr;
	}

	public void setIdRappr(Integer idRappr) {
		this.idRappr = idRappr;
	}

	@Override
	public java.lang.String toString() {
		return "RappresentanteEnteConfermaDTO{" +
				"idRappr=" + idRappr +
				", flgConferma=" + flgConferma +
				", motivazione='" + motivazione + '\'' +
				", numProtocollo='" + numProtocollo + '\'' +
				", dtProtocollo=" + dtProtocollo +
				'}';
	}

}
