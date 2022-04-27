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
import java.util.Objects;


/**
 * The persistent class for the myext_comune database table.
 * 
 */
@Entity
@Table(name="myext_comune")
@NamedQuery(name="Comune.findAll", query="SELECT c FROM Comune c")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Comune implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="cod_comune", unique=true, nullable=false, length=10)
	private String codComune;

	@Column(name="des_comune", nullable=false, length=255)
	private String desComune;
	
	@Column(name="geo_long", nullable=false, length=30)
	private String geoLong;

	@Column(name="geo_lat", nullable=false, length=30)
	private String geoLat;

	@Column(name="cod_catastale", length=5)
	private String codCatastale;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="cod_provincia", nullable=false)
	private Provincia provincia;

	public Comune() {
	}

	public String getCodComune() {
		return codComune;
	}

	public void setCodComune(String codComune) {
		this.codComune = codComune;
	}

	public String getDesComune() {
		return desComune;
	}

	public void setDesComune(String desComune) {
		this.desComune = desComune;
	}

	public String getGeoLong() {
		return geoLong;
	}

	public void setGeoLong(String geoLong) {
		this.geoLong = geoLong;
	}

	public String getGeoLat() {
		return geoLat;
	}

	public void setGeoLat(String geoLat) {
		this.geoLat = geoLat;
	}

	public String getCodCatastale() {
		return codCatastale;
	}

	public void setCodCatastale(String codCatastale) {
		this.codCatastale = codCatastale;
	}

	public Provincia getProvincia() {
		return provincia;
	}

	public void setProvincia(Provincia provincia) {
		this.provincia = provincia;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Comune comune = (Comune) o;
		return Objects.equals(codComune, comune.codComune);
	}

	@Override
	public int hashCode() {
		return Objects.hash(codComune);
	}
}