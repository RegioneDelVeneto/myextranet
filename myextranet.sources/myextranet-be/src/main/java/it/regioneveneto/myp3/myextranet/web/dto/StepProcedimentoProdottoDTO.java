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
import it.regioneveneto.myp3.myextranet.model.StepProcedimentoProdotto;
import it.regioneveneto.myp3.myextranet.model.TipoRichiestaProdotto;
import it.regioneveneto.myp3.myextranet.utils.ObjectMapperUtils;

public class StepProcedimentoProdottoDTO implements Serializable {
	
	private static final long serialVersionUID = 1L;

	protected Integer idStatoConf;
	@NotNull(message = "ProcedimentoProdotto è obbligatorio")
	protected ProcedimentoProdottoDTO procedimentoProdotto;
	@NotNull(message = "NumStep è obbligatorio")
	protected Integer numStep;
	@NotNull(message = "CodStato è obbligatorio")
	protected String codStato;
	@NotNull(message = "DesStato è obbligatorio")
	protected String desStato;
	@NotNull(message = "Competenza è obbligatorio")
	protected String competenza;
	protected Integer flgAggiornaUtenti;
	@NotNull(message = "FlgFineRich è obbligatorio")
	protected Integer flgFineRich;
	protected String azioni;

	public Integer getIdStatoConf() {
		return idStatoConf;
	}

	public void setIdStatoConf(Integer idStatoConf) {
		this.idStatoConf = idStatoConf;
	}

	public ProcedimentoProdottoDTO getProcedimentoProdotto() {
		return procedimentoProdotto;
	}

	public void setProcedimentoProdotto(ProcedimentoProdottoDTO procedimentoProdotto) {
		this.procedimentoProdotto = procedimentoProdotto;
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
		return "StepProcedimentoProdottoDTO [idStatoConf=" + idStatoConf + ", procedimentoProdotto="
				+ procedimentoProdotto + ", numStep=" + numStep + ", codStato=" + codStato + ", desStato=" + desStato
				+ ", competenza=" + competenza + ", flgAggiornaUtenti=" + flgAggiornaUtenti + ", flgFineRich="
				+ flgFineRich + ", azioni=" + azioni + "]";
	}

	public static StepProcedimentoProdotto patchStepProcedimentoProdotto(StepProcedimentoProdottoDTO patchStepProcedimentoProdotto, StepProcedimentoProdotto originalStepProcedimentoProdotto, boolean ignoreNulls, boolean cloneOriginal) {
		StepProcedimentoProdotto stepProcedimentoProdottoResult;
		
		if (cloneOriginal) {
			originalStepProcedimentoProdotto = ObjectMapperUtils.map(originalStepProcedimentoProdotto, new StepProcedimentoProdotto());
		}
		
		if(ignoreNulls) {
			stepProcedimentoProdottoResult = originalStepProcedimentoProdotto;
					
			// procedimentoProdotto
			if (patchStepProcedimentoProdotto.getProcedimentoProdotto() != null) {
				ProcedimentoProdotto procedimentoProdotto = new ProcedimentoProdotto();
				procedimentoProdotto.setIdProdottoProc(patchStepProcedimentoProdotto.getProcedimentoProdotto().getIdProdottoProc());
				originalStepProcedimentoProdotto.setProcedimentoProdotto(procedimentoProdotto);
			}
			
			// numStep
			if (patchStepProcedimentoProdotto.getNumStep() != null) {
				originalStepProcedimentoProdotto.setNumStep(patchStepProcedimentoProdotto.getNumStep());
			}
			
			// codStato
			if(StringUtils.hasText(patchStepProcedimentoProdotto.getCodStato())) {
				originalStepProcedimentoProdotto.setCodStato(patchStepProcedimentoProdotto.getCodStato());
			}
			
			// desStato
			if(StringUtils.hasText(patchStepProcedimentoProdotto.getDesStato())) {
				originalStepProcedimentoProdotto.setDesStato(patchStepProcedimentoProdotto.getDesStato());
			}
			
			// competenza
			if(StringUtils.hasText(patchStepProcedimentoProdotto.getCompetenza())) {
				originalStepProcedimentoProdotto.setCompetenza(patchStepProcedimentoProdotto.getCompetenza());
			}
			
			// flgAggiornaUtenti
			if (patchStepProcedimentoProdotto.getFlgAggiornaUtenti() != null) {
				originalStepProcedimentoProdotto.setFlgAggiornaUtenti(patchStepProcedimentoProdotto.getFlgAggiornaUtenti());
			}
			
			// flgFineRich
			if (patchStepProcedimentoProdotto.getFlgFineRich() != null) {
				originalStepProcedimentoProdotto.setFlgFineRich(patchStepProcedimentoProdotto.getFlgFineRich());
			}
				
		} else {
			stepProcedimentoProdottoResult = ObjectMapperUtils.map(patchStepProcedimentoProdotto, originalStepProcedimentoProdotto);
		}
		
		return stepProcedimentoProdottoResult;
	}

	
}
