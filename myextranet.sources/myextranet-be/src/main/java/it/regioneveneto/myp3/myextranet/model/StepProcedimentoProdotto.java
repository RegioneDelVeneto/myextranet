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
 * The persistent class for the myext_prodotto_proc database table.
 * 
 */
@Entity
@Table(name = "myext_prodotto_proc_step")
@NamedQuery(name = "StepProcedimentoProdotto.findAll", query = "SELECT s FROM StepProcedimentoProdotto s")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class StepProcedimentoProdotto extends AuditModel implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_stato_conf", unique = true, nullable = false)
	private Integer idStatoConf;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_prodotto_proc", nullable = false)
	private ProcedimentoProdotto procedimentoProdotto;
	
	@Column(name = "num_step", nullable = false)
	private Integer numStep;
	
	@Column(name = "cod_stato", nullable = false, length = 100)
	private String codStato;
	
	@Column(name = "des_stato", nullable = false)
	private String desStato;
	
	@Column(name = "competenza", nullable = false, length = 5)
	private String competenza;
	
	@Column(name = "flg_aggiorna_utenti", nullable = true)
	private Integer flgAggiornaUtenti;
	
	@Column(name = "flg_fine_rich", nullable = false)
	private Integer flgFineRich;
	
	@Column(name = "azioni", nullable = true)
	private String azioni;
	
	public Integer getIdStatoConf() {
		return idStatoConf;
	}

	public void setIdStatoConf(Integer idStatoConf) {
		this.idStatoConf = idStatoConf;
	}

	public ProcedimentoProdotto getProcedimentoProdotto() {
		return procedimentoProdotto;
	}

	public void setProcedimentoProdotto(ProcedimentoProdotto procedimentoProdotto) {
		this.procedimentoProdotto = procedimentoProdotto;
	}

	public Integer getNumStep() {
		return numStep;
	}

	public void setNumStep(Integer numStep) {
		this.numStep = numStep;
	}

	public String getCodStato() {
		return codStato;
	}

	public void setCodStato(String codStato) {
		this.codStato = codStato;
	}

	public String getDesStato() {
		return desStato;
	}

	public void setDesStato(String desStato) {
		this.desStato = desStato;
	}

	public String getCompetenza() {
		return competenza;
	}

	public void setCompetenza(String competenza) {
		this.competenza = competenza;
	}

	public Integer getFlgAggiornaUtenti() {
		return flgAggiornaUtenti;
	}

	public void setFlgAggiornaUtenti(Integer flgAggiornaUtenti) {
		this.flgAggiornaUtenti = flgAggiornaUtenti;
	}

	public Integer getFlgFineRich() {
		return flgFineRich;
	}

	public void setFlgFineRich(Integer flgFineRich) {
		this.flgFineRich = flgFineRich;
	}

	public String getAzioni() {
		return azioni;
	}

	public void setAzioni(String azioni) {
		this.azioni = azioni;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		StepProcedimentoProdotto step = (StepProcedimentoProdotto) o;
		return Objects.equals(idStatoConf, step.idStatoConf);
	}

	@Override
	public int hashCode() {
		return Objects.hash(idStatoConf);
	}
}