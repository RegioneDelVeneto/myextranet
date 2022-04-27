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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name="myext_ente_categoria")
@NamedQuery(name="CategoriaEnte.findAll", query="SELECT c FROM CategoriaEnte c")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class CategoriaEnte implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_categoria", unique = true, nullable = false)
	private Integer idCategoria;

	@Column(name="cod_categoria", unique=true, nullable=false, length=30)
	private String codCategoria;

	@Column(name="des_categoria", nullable=false, length=100)
	private String desCategoria;


	public Integer getIdCategoria() {
		return idCategoria;
	}

	public void setIdCategoria(Integer idCategoria) {
		this.idCategoria = idCategoria;
	}

	public String getCodCategoria() {
		return codCategoria;
	}

	public void setCodCategoria(String codCategoria) {
		this.codCategoria = codCategoria;
	}

	public String getDesCategoria() {
		return desCategoria;
	}

	public void setDesCategoria(String desCategoria) {
		this.desCategoria = desCategoria;
	}

	public CategoriaEnte() {
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		CategoriaEnte categoria = (CategoriaEnte) o;
		return Objects.equals(idCategoria, categoria.idCategoria);
	}

	@Override
	public int hashCode() {
		return Objects.hash(idCategoria);
	}
}
