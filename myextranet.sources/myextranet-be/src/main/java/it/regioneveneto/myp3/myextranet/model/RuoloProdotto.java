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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


/**
 * The persistent class for the myext_prodotto_ruoli database table.
 * 
 */
@Entity
@Table(name="myext_prodotto_ruoli")
@NamedQuery(name="RuoloProdotto.findAll", query="SELECT r FROM RuoloProdotto r")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class RuoloProdotto implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="cod_ruolo", unique=true, nullable=false, length=15)
	private String codRuolo;

	@Column(name="des_ruolo", nullable=false, length=100)
	private String desRuolo;
	
	@Column(name="num_ruolo", nullable=false)
	private Integer numRuolo;

	public RuoloProdotto() {
	}

	public String getCodRuolo() {
		return codRuolo;
	}

	public void setCodRuolo(String codRuolo) {
		this.codRuolo = codRuolo;
	}

	public String getDesRuolo() {
		return desRuolo;
	}

	public void setDesRuolo(String desRuolo) {
		this.desRuolo = desRuolo;
	}

	public Integer getNumRuolo() {
		return numRuolo;
	}

	public void setNumRuolo(Integer numRuolo) {
		this.numRuolo = numRuolo;
	}


}