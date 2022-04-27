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

public class CollaboratoreProgettoFilterDTO implements Serializable {
	private Integer idCollab;
	private Integer idUtente;
	private String idProgetto;
	private Integer flgCoord;
	private Integer flgConferma;
	private String motivConferma;
	
	private String nome;
	private String cognome;
	private String denominazioneEnte;
	
	// Utente fields
	private Integer idEnte;
	private String azienda;
	
	private Boolean validOnly;
	

	public Integer getIdCollab() {
		return idCollab;
	}

	public void setIdCollab(Integer idCollab) {
		this.idCollab = idCollab;
	}

	public String getIdProgetto() {
		return idProgetto;
	}

	public void setIdProgetto(String idProgetto) {
		this.idProgetto = idProgetto;
	}

	public Integer getFlgCoord() {
		return flgCoord;
	}

	public void setFlgCoord(Integer flgCoord) {
		this.flgCoord = flgCoord;
	}

	public Integer getFlgConferma() {
		return flgConferma;
	}

	public void setFlgConferma(Integer flgConferma) {
		this.flgConferma = flgConferma;
	}

	public String getMotivConferma() {
		return motivConferma;
	}

	public void setMotivConferma(String motivConferma) {
		this.motivConferma = motivConferma;
	}

	public String getFlgCoordStr() {
		return BeanUtils.flagToString(flgCoord);
	}

	public void setFlgCoordStr(String flgCoordStr) {
		this.flgCoord = BeanUtils.stringToFlag(flgCoordStr);
	}

	public String getFlgConfermaStr() {
		return BeanUtils.flagToString(flgConferma);
	}

	public void setFlgConfermaStr(String flgConfermaStr) {
		this.flgConferma = BeanUtils.stringToFlag(flgConfermaStr);
	}

	public Integer getIdUtente() {
		return idUtente;
	}

	public void setIdUtente(Integer idUtente) {
		this.idUtente = idUtente;
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

	public String getDenominazioneEnte() {
		return denominazioneEnte;
	}

	public void setDenominazioneEnte(String denominazioneEnte) {
		this.denominazioneEnte = denominazioneEnte;
	}

	public Integer getIdEnte() {
		return idEnte;
	}

	public void setIdEnte(Integer idEnte) {
		this.idEnte = idEnte;
	}

	public String getAzienda() {
		return azienda;
	}

	public void setAzienda(String azienda) {
		this.azienda = azienda;
	}

	public Boolean isValidOnly() {
		return validOnly;
	}

	public void setValidOnly(Boolean validOnly) {
		this.validOnly = validOnly;
	}

	@Override
	public String toString() {
		return "CollaboratoreProgettoFilterDTO [idCollab=" + idCollab + ", idUtente=" + idUtente + ", idProgetto="
				+ idProgetto + ", flgCoord=" + flgCoord + ", flgConferma=" + flgConferma + ", motivConferma="
				+ motivConferma + ", nome=" + nome + ", cognome=" + cognome + ", denominazioneEnte=" + denominazioneEnte
				+ ", IdEnte=" + idEnte + ", azienda=" + azienda + ", validOnly=" + validOnly + "]";
	}
}
