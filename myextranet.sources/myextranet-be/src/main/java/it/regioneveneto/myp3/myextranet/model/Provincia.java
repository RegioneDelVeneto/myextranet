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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.io.Serializable;


/**
 * The persistent class for the myext_provincia database table.
 * 
 */
@Entity
@Table(name="myext_provincia")
@NamedQuery(name="Provincia.findAll", query="SELECT p FROM Provincia p")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Provincia implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="cod_provincia", unique=true, nullable=false, length=3)
	private String codProvincia;

	@Column(name="des_provincia", nullable=false, length=100)
	private String desProvincia;


	public Provincia() {
	}

	public String getCodProvincia() {
		return this.codProvincia;
	}

	public void setCodProvincia(String codProvincia) {
		this.codProvincia = codProvincia;
	}

	public String getDesProvincia() {
		return this.desProvincia;
	}

	public void setDesProvincia(String desProvincia) {
		this.desProvincia = desProvincia;
	}

}