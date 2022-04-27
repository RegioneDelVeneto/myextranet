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

public class UtenteMessaggiFilterDTO implements Serializable {
	private Integer idUtente;
	private Integer idMessaggio;
	private Integer flgOnline;
	private Integer flgReadOnline;

	// Getter and setter
	public Integer getIdUtente() {
		return idUtente;
	}

	public void setIdUtente(Integer idUtente) {
		this.idUtente = idUtente;
	}

	public Integer getIdMessaggio() {
		return idMessaggio;
	}

	public void setIdMessaggio(Integer idMessaggio) {
		this.idMessaggio = idMessaggio;
	}

	public Integer getFlgOnline() {
		return flgOnline;
	}

	public void setFlgOnline(Integer flgOnline) {
		this.flgOnline = flgOnline;
	}

	public Integer getFlgReadOnline() {
		return flgReadOnline;
	}

	public void setFlgReadOnline(Integer flgReadOnline) {
		this.flgReadOnline = flgReadOnline;
	}

	@Override
	public String toString() {
		return "UtenteMessaggiFilterDTO [idUtente=" + idUtente + ", idMessaggio=" + idMessaggio + ", flgOnline="
				+ flgOnline + ", flgReadOnline=" + flgReadOnline + "]";
	}

}
