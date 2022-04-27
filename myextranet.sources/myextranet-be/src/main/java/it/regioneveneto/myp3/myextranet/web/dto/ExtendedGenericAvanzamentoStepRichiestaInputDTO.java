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

import it.regioneveneto.myp3.myextranet.model.StepProcedimentoProdotto;

public class ExtendedGenericAvanzamentoStepRichiestaInputDTO extends GenericAvanzamentoStepRichiestaInputDTO {

	protected Integer idProdottoProc;
	protected StepProcedimentoProdotto newStep;
	protected Integer flgAttivaProdotto;
	protected String idProdotto;

	public Integer getIdProdottoProc() {
		return idProdottoProc;
	}

	public void setIdProdottoProc(Integer idProdottoProc) {
		this.idProdottoProc = idProdottoProc;
	}

	public StepProcedimentoProdotto getNewStep() {
		return newStep;
	}

	public void setNewStep(StepProcedimentoProdotto newStep) {
		this.newStep = newStep;
	}

	public Integer getFlgAttivaProdotto() {
		return flgAttivaProdotto;
	}

	public void setFlgAttivaProdotto(Integer flgAttivaProdotto) {
		this.flgAttivaProdotto = flgAttivaProdotto;
	}

	public String getIdProdotto() {
		return idProdotto;
	}

	public void setIdProdotto(String idProdotto) {
		this.idProdotto = idProdotto;
	}

	public ExtendedGenericAvanzamentoStepRichiestaInputDTO() {
		
	}

}
