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
@Table(name = "myext_prodotto_proc")
@NamedQuery(name = "ProcedimentoProdotto.findAll", query = "SELECT p FROM ProcedimentoProdotto p")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class ProcedimentoProdotto extends AuditModel implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_prodotto_proc", unique = true, nullable = false)
	private Integer idProdottoProc;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_prodotto_att", nullable = false)
	private ProdottoAttivabile prodotto;
	
	@Column(name = "num_versione", nullable = false)
	private Integer numVersione;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cod_tipo_rich", nullable = false)
	private TipoRichiestaProdotto tipoRichiestaProdotto;
	
	@Column(name = "des_prodotto_proc", nullable = false, length = 100)
	private String desProdottoProc;
	
	@Column(name = "des_prodotto_proc_estesa", nullable = true, length = 2000)
	private String desProdottoProcEstesa;
	
	@Column(name = "flg_enabled", nullable = false)
	private Integer flgEnabled;

	public Integer getIdProdottoProc() {
		return idProdottoProc;
	}

	public void setIdProdottoProc(Integer idProdottoProc) {
		this.idProdottoProc = idProdottoProc;
	}

	public ProdottoAttivabile getProdotto() {
		return prodotto;
	}

	public void setProdotto(ProdottoAttivabile prodotto) {
		this.prodotto = prodotto;
	}

	public Integer getNumVersione() {
		return numVersione;
	}

	public void setNumVersione(Integer numVersione) {
		this.numVersione = numVersione;
	}

	public TipoRichiestaProdotto getTipoRichiestaProdotto() {
		return tipoRichiestaProdotto;
	}

	public void setTipoRichiestaProdotto(TipoRichiestaProdotto tipoRichiestaProdotto) {
		this.tipoRichiestaProdotto = tipoRichiestaProdotto;
	}

	public String getDesProdottoProc() {
		return desProdottoProc;
	}

	public void setDesProdottoProc(String desProdottoProc) {
		this.desProdottoProc = desProdottoProc;
	}

	public String getDesProdottoProcEstesa() {
		return desProdottoProcEstesa;
	}

	public void setDesProdottoProcEstesa(String desProdottoProcEstesa) {
		this.desProdottoProcEstesa = desProdottoProcEstesa;
	}

	public Integer getFlgEnabled() {
		return flgEnabled;
	}

	public void setFlgEnabled(Integer flgEnabled) {
		this.flgEnabled = flgEnabled;
	}

	public ProcedimentoProdotto() {
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		ProcedimentoProdotto procedimento = (ProcedimentoProdotto) o;
		return Objects.equals(idProdottoProc, procedimento.idProdottoProc);
	}

	@Override
	public int hashCode() {
		return Objects.hash(idProdottoProc);
	}
}