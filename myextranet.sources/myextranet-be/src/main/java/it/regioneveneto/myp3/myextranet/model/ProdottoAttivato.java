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
* The persistent class for the myext_prodotto_attivato database table.
* 
*/
@Entity
@Table(name = "myext_prodotto_attivato")
@NamedQuery(name = "ProdottoAttivato.findAll", query = "SELECT p FROM ProdottoAttivato p")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class ProdottoAttivato extends AuditWithValidityModel implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_attivazione", unique = true, nullable = false)
	private Integer idAttivazione;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_prodotto_att", nullable = false)
	private ProdottoAttivabile prodottoAttivabile;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_ente", nullable = false)
	private Ente ente;

	public Integer getIdAttivazione() {
		return idAttivazione;
	}

	public void setIdAttivazione(Integer idAttivazione) {
		this.idAttivazione = idAttivazione;
	}

	public ProdottoAttivabile getProdottoAttivabile() {
		return prodottoAttivabile;
	}

	public void setProdottoAttivabile(ProdottoAttivabile prodottoAttivabile) {
		this.prodottoAttivabile = prodottoAttivabile;
	}

	public Ente getEnte() {
		return ente;
	}

	public void setEnte(Ente ente) {
		this.ente = ente;
	}

	public ProdottoAttivato() {
		
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		ProdottoAttivato prodotto = (ProdottoAttivato) o;
		return Objects.equals(idAttivazione, prodotto.idAttivazione);
	}

	@Override
	public int hashCode() {
		return Objects.hash(idAttivazione);
	}

}
