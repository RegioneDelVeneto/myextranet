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
import java.sql.Timestamp;
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
 * The persistent class for the myext_prodotto_rich_step database table.
 * 
 */
@Entity
@Table(name = "myext_prodotto_rich_step")
@NamedQuery(name = "StepRichiestaProdotto.findAll", query = "SELECT s FROM StepRichiestaProdotto s")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class StepRichiestaProdotto extends AuditModel implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_step", unique = true, nullable = false)
	private Integer idStep;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_prod_attiv_rich", nullable = false)
	private RichiestaProdotto richiestaProdotto;
	
	@Column(name = "dt_step", nullable = false)
	private Timestamp dtStep;
	
	@Column(name = "esecutore", nullable = false, length = 5)
	private String esecutore;
	
	@Column(name = "cod_stato", nullable = false, length = 30)
	private String codStato;

	@Column(name = "esito_step", nullable = true)
	private Integer esitoStep;
	
	@Column(name = "note_step", nullable = false, length = 500)
	private String noteStep;
	
	public Integer getIdStep() {
		return idStep;
	}

	public void setIdStep(Integer idStep) {
		this.idStep = idStep;
	}

	public RichiestaProdotto getRichiestaProdotto() {
		return richiestaProdotto;
	}

	public void setRichiestaProdotto(RichiestaProdotto richiestaProdotto) {
		this.richiestaProdotto = richiestaProdotto;
	}

	public Timestamp getDtStep() {
		return dtStep;
	}

	public void setDtStep(Timestamp dtStep) {
		this.dtStep = dtStep;
	}

	public String getEsecutore() {
		return esecutore;
	}

	public void setEsecutore(String esecutore) {
		this.esecutore = esecutore;
	}

	public String getCodStato() {
		return codStato;
	}

	public void setCodStato(String codStato) {
		this.codStato = codStato;
	}

	public Integer getEsitoStep() {
		return esitoStep;
	}

	public void setEsitoStep(Integer esitoStep) {
		this.esitoStep = esitoStep;
	}

	public String getNoteStep() {
		return noteStep;
	}

	public void setNoteStep(String noteStep) {
		this.noteStep = noteStep;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		StepRichiestaProdotto step = (StepRichiestaProdotto) o;
		return Objects.equals(idStep, step.idStep);
	}

	@Override
	public int hashCode() {
		return Objects.hash(idStep);
	}
}