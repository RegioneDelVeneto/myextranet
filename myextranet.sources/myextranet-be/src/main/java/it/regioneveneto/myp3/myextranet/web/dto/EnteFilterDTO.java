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
package it.regioneveneto.myp3.myextranet.web.dto;

import java.io.Serializable;

public class EnteFilterDTO implements Serializable {

	private String codIpa;
	private String denominazione;
	private Boolean activeOnly; 
	private Integer idCategoria;
    
    //Getter and setter

	public String getCodIpa() {
		return codIpa;
	}

	public void setCodIpa(String codIpa) {
		this.codIpa = codIpa;
	}

	public String getDenominazione() {
		return denominazione;
	}

	public void setDenominazione(String denominazione) {
		this.denominazione = denominazione;
	}

	public Boolean isActiveOnly() {
		return activeOnly;
	}

	public void setActiveOnly(Boolean activeOnly) {
		this.activeOnly = activeOnly;
	}

	public Integer getIdCategoria() {
		return idCategoria;
	}

	public void setIdCategoria(Integer idCategoria) {
		this.idCategoria = idCategoria;
	}

	@Override
	public String toString() {
		return "EnteFilterDTO [codIpa=" + codIpa + ", denominazione=" + denominazione + ", activeOnly=" + activeOnly
				+ ", idCategoria=" + idCategoria + "]";
	}


}
