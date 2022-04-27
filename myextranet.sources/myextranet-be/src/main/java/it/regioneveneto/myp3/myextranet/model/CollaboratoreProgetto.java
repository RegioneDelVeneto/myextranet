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
package it.regioneveneto.myp3.myextranet.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * The persistent class for the myext_evento_iscritti database table.
 * 
 */
@Entity
@Table(name = "myext_progetto_collab")
@NamedQuery(name = "CollaboratoreProgetto.findAll", query = "SELECT c FROM CollaboratoreProgetto c")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class CollaboratoreProgetto extends AuditWithValidityModel implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_collab", unique = true, nullable = false)
	private Integer idCollab;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_utente", nullable = false)
	private Utente utente;

	@Column(name = "id_progetto", nullable = false, length = 250)
	private String idProgetto;

	@Column(name = "flg_coord", nullable = false)
	private Integer flgCoord;

	@Column(name = "flg_conferma", nullable = false)
	private Integer flgConferma;

	@Column(name = "dt_richiesta", nullable = false)
	protected LocalDate dtRichiesta;

	@Column(name = "dt_conferma", nullable = true)
	protected LocalDate dtConferma;
	
	@Column(name="motiv_conferma", nullable=true, length=1000)
	protected String motivConferma;

	public Integer getIdCollab() {
		return idCollab;
	}

	public void setIdCollab(Integer idCollab) {
		this.idCollab = idCollab;
	}

	public Utente getUtente() {
		return utente;
	}

	public void setUtente(Utente utente) {
		this.utente = utente;
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

	public LocalDate getDtRichiesta() {
		return dtRichiesta;
	}

	public void setDtRichiesta(LocalDate dtRichiesta) {
		this.dtRichiesta = dtRichiesta;
	}

	public LocalDate getDtConferma() {
		return dtConferma;
	}

	public void setDtConferma(LocalDate dtConferma) {
		this.dtConferma = dtConferma;
	}

	public String getMotivConferma() {
		return motivConferma;
	}

	public void setMotivConferma(String motivConferma) {
		this.motivConferma = motivConferma;
	}

	public CollaboratoreProgetto() {
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		CollaboratoreProgetto collaboratore = (CollaboratoreProgetto) o;
		return Objects.equals(idCollab, collaboratore.idCollab);
	}

	@Override
	public int hashCode() {
		return Objects.hash(idCollab);
	}
}