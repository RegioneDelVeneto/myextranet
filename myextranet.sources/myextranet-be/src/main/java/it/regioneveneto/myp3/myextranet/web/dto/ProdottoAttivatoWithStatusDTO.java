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

public class ProdottoAttivatoWithStatusDTO extends ProdottoAttivatoDTO {

	private static final long serialVersionUID = 1L;	
	
	protected Integer numRich;
	protected Integer stato;

	public Integer getNumRich() {
		return numRich;
	}

	public void setNumRich(Integer numRich) {
		this.numRich = numRich;
	}

	public Integer getStato() {
		return stato;
	}

	public void setStato(Integer stato) {
		this.stato = stato;
	}

	public ProdottoAttivatoWithStatusDTO() {
	}

}
