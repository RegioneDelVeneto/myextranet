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

public class RichiestaProdottoWithStepsDTO extends RichiestaProdottoDTO {

	List<StepRichiestaProdottoOutputDTO> stepRichiestaProdottoList = new ArrayList<StepRichiestaProdottoOutputDTO>();
	List<DocumentoRichiestaProdottoOutputDTO> documentoRichiestaProdottoList = new ArrayList<DocumentoRichiestaProdottoOutputDTO>();
	List<UtenteRichiestaProdottoDTO> utenteRichiestaProdottoList = new ArrayList<UtenteRichiestaProdottoDTO>();
	
	StepProcedimentoProdottoDTO stepProcedimentoProdotto;

	public List<StepRichiestaProdottoOutputDTO> getStepRichiestaProdottoList() {
		return stepRichiestaProdottoList;
	}

	public void setStepRichiestaProdottoList(List<StepRichiestaProdottoOutputDTO> stepRichiestaProdottoList) {
		this.stepRichiestaProdottoList = stepRichiestaProdottoList;
	}

	public List<DocumentoRichiestaProdottoOutputDTO> getDocumentoRichiestaProdottoList() {
		return documentoRichiestaProdottoList;
	}

	public void setDocumentoRichiestaProdottoList(List<DocumentoRichiestaProdottoOutputDTO> documentoRichiestaProdottoList) {
		this.documentoRichiestaProdottoList = documentoRichiestaProdottoList;
	}

	public List<UtenteRichiestaProdottoDTO> getUtenteRichiestaProdottoList() {
		return utenteRichiestaProdottoList;
	}

	public void setUtenteRichiestaProdottoList(List<UtenteRichiestaProdottoDTO> utenteRichiestaProdottoList) {
		this.utenteRichiestaProdottoList = utenteRichiestaProdottoList;
	}

	public StepProcedimentoProdottoDTO getStepProcedimentoProdotto() {
		return stepProcedimentoProdotto;
	}

	public void setStepProcedimentoProdotto(StepProcedimentoProdottoDTO stepProcedimentoProdotto) {
		this.stepProcedimentoProdotto = stepProcedimentoProdotto;
	}

	public RichiestaProdottoWithStepsDTO() {
	}

}
