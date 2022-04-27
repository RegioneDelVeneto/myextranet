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

import it.regioneveneto.myp3.myextranet.utils.BeanUtils;

public class IscrittoEventoFilterDTO implements Serializable {
	
	private Integer idIscritto;
	private String idEvento;
	private Integer flgRelatore;
	private String nome;
	private String cognome;
	private Integer idEnte;
	private Integer idUtente;
	private String azienda;


	public Integer getIdIscritto() {
		return idIscritto;
	}

	public void setIdIscritto(Integer idIscritto) {
		this.idIscritto = idIscritto;
	}

	public String getIdEvento() {
		return idEvento;
	}

	public void setIdEvento(String idEvento) {
		this.idEvento = idEvento;
	}

	public Integer getFlgRelatore() {
		return flgRelatore;
	}

	public void setFlgRelatore(Integer flgRelatore) {
		this.flgRelatore = flgRelatore;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getCognome() {
		return cognome;
	}

	public void setCognome(String cognome) {
		this.cognome = cognome;
	}

	public Integer getIdEnte() {
		return idEnte;
	}

	public void setIdEnte(Integer idEnte) {
		this.idEnte = idEnte;
	}

	public String getFlgRelatoreStr() {
		return BeanUtils.flagToString(flgRelatore);
	}

	public void setFlgRelatoreStr(String flgRelatoreStr) {
		this.flgRelatore = BeanUtils.stringToFlag(flgRelatoreStr);
	}

	public Integer getIdUtente() {
		return idUtente;
	}

	public void setIdUtente(Integer idUtente) {
		this.idUtente = idUtente;
	}

	public String getAzienda() {
		return azienda;
	}

	public void setAzienda(String azienda) {
		this.azienda = azienda;
	}

	@Override
	public String toString() {
		return "IscrittoEventoFilterDTO [idIscritto=" + idIscritto + ", idEvento=" + idEvento + ", flgRelatore="
				+ flgRelatore + ", nome=" + nome + ", cognome=" + cognome + ", idEnte=" + idEnte + ", idUtente="
				+ idUtente + ", azienda=" + azienda + "]";
	}

}
