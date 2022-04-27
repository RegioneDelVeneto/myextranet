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

import javax.validation.constraints.NotNull;

import org.springframework.util.StringUtils;

import it.regioneveneto.myp3.myextranet.model.ProcedimentoProdotto;
import it.regioneveneto.myp3.myextranet.model.ProdottoAttivabile;
import it.regioneveneto.myp3.myextranet.model.TipoRichiestaProdotto;
import it.regioneveneto.myp3.myextranet.utils.ObjectMapperUtils;

public class StepProcedimentoProdottoFilterDTO implements Serializable {
	
	private static final long serialVersionUID = 1L;

	protected Integer idProdottoProc;
	protected Integer numStep;
	protected String codStato;
	protected String desStato;
	protected String competenza;
	protected Integer flgAggiornaUtenti;
	protected Integer flgFineRich;
	protected String azioni;

	public Integer getIdProdottoProc() {
		return idProdottoProc;
	}

	public void setIdProdottoProc(Integer idProdottoProc) {
		this.idProdottoProc = idProdottoProc;
	}

	public Integer getNumStep() {
		return numStep;
	}

	public void setNumStep(Integer numStep) {
		this.numStep = numStep;
	}

	public String getCodStato() {
		return codStato;
	}

	public void setCodStato(String codStato) {
		this.codStato = codStato;
	}

	public String getDesStato() {
		return desStato;
	}

	public void setDesStato(String desStato) {
		this.desStato = desStato;
	}

	public String getCompetenza() {
		return competenza;
	}

	public void setCompetenza(String competenza) {
		this.competenza = competenza;
	}

	public Integer getFlgAggiornaUtenti() {
		return flgAggiornaUtenti;
	}

	public void setFlgAggiornaUtenti(Integer flgAggiornaUtenti) {
		this.flgAggiornaUtenti = flgAggiornaUtenti;
	}

	public Integer getFlgFineRich() {
		return flgFineRich;
	}

	public void setFlgFineRich(Integer flgFineRich) {
		this.flgFineRich = flgFineRich;
	}

	public String getAzioni() {
		return azioni;
	}

	public void setAzioni(String azioni) {
		this.azioni = azioni;
	}

	@Override
	public String toString() {
		return "StepProcedimentoProdottoFilterDTO [idProdottoProc=" + idProdottoProc + ", numStep=" + numStep
				+ ", codStato=" + codStato + ", desStato=" + desStato + ", competenza=" + competenza
				+ ", flgAggiornaUtenti=" + flgAggiornaUtenti + ", flgFineRich=" + flgFineRich + ", azioni=" + azioni
				+ "]";
	}

}
