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
 * The persistent class for the myext_prodotto_rich database table.
 * 
 */
@Entity
@Table(name = "myext_prodotto_rich")
@NamedQuery(name = "RichiestaProdotto.findAll", query = "SELECT r FROM RichiestaProdotto r")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class RichiestaProdotto extends AuditModel implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_prod_attiv_rich", unique = true, nullable = false)
	private Integer idProdAttivRich;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_attivazione", nullable = false)
	private ProdottoAttivato prodottoAttivato;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_prodotto_proc", nullable = false)
	private ProcedimentoProdotto procedimentoProdotto;

	@Column(name = "num_versione", nullable = false)
	private Integer numVersione;
	
	@Column(name = "dt_rich", nullable = false)
	private LocalDate dtRich;
	
	@Column(name = "cod_stato", nullable = false, length = 100)
	private String codStato;
	
	@Column(name = "ultime_note", nullable = true)
	private String ultimeNote;

	@Column(name = "flg_fine_rich", nullable = false)
	private Integer flgFineRich;

	public Integer getIdProdAttivRich() {
		return idProdAttivRich;
	}

	public void setIdProdAttivRich(Integer idProdAttivRich) {
		this.idProdAttivRich = idProdAttivRich;
	}

	public ProdottoAttivato getProdottoAttivato() {
		return prodottoAttivato;
	}

	public void setProdottoAttivato(ProdottoAttivato prodottoAttivato) {
		this.prodottoAttivato = prodottoAttivato;
	}

	public ProcedimentoProdotto getProcedimentoProdotto() {
		return procedimentoProdotto;
	}

	public void setProcedimentoProdotto(ProcedimentoProdotto procedimentoProdotto) {
		this.procedimentoProdotto = procedimentoProdotto;
	}

	public Integer getNumVersione() {
		return numVersione;
	}

	public void setNumVersione(Integer numVersione) {
		this.numVersione = numVersione;
	}

	public LocalDate getDtRich() {
		return dtRich;
	}

	public void setDtRich(LocalDate dtRich) {
		this.dtRich = dtRich;
	}

	public String getCodStato() {
		return codStato;
	}

	public void setCodStato(String codStato) {
		this.codStato = codStato;
	}

	public String getUltimeNote() {
		return ultimeNote;
	}

	public void setUltimeNote(String ultimeNote) {
		this.ultimeNote = ultimeNote;
	}

	public Integer getFlgFineRich() {
		return flgFineRich;
	}

	public void setFlgFineRich(Integer flgFineRich) {
		this.flgFineRich = flgFineRich;
	}

	public RichiestaProdotto() {
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		RichiestaProdotto richiestaProdotto = (RichiestaProdotto) o;
		return Objects.equals(idProdAttivRich, richiestaProdotto.idProdAttivRich);
	}

	@Override
	public int hashCode() {
		return Objects.hash(idProdAttivRich);
	}
}