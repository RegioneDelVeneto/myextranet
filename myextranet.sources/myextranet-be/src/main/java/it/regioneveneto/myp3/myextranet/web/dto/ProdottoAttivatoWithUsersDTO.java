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

import java.util.ArrayList;
import java.util.List;

public class ProdottoAttivatoWithUsersDTO extends ProdottoAttivatoDTO {

	List<UtenteProdottoAttivatoDTO> utenteProdottoAttivatoList = new ArrayList<UtenteProdottoAttivatoDTO>();
	RichiestaProdottoWithStepsDTO richiestaProdottoWithStep;
	
	private Integer stato;

	public List<UtenteProdottoAttivatoDTO> getUtenteProdottoAttivatoList() {
		return utenteProdottoAttivatoList;
	}

	public void setUtenteProdottoAttivatoList(List<UtenteProdottoAttivatoDTO> utenteProdottoAttivatoList) {
		this.utenteProdottoAttivatoList = utenteProdottoAttivatoList;
	}

	public RichiestaProdottoWithStepsDTO getRichiestaProdottoWithStep() {
		return richiestaProdottoWithStep;
	}

	public void setRichiestaProdottoWithStep(RichiestaProdottoWithStepsDTO richiestaProdottoWithStep) {
		this.richiestaProdottoWithStep = richiestaProdottoWithStep;
	}

	public Integer getStato() {
		return stato;
	}

	public void setStato(Integer stato) {
		this.stato = stato;
	}

	public ProdottoAttivatoWithUsersDTO() {		
	}

}
