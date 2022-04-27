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
import java.sql.Timestamp;
import java.time.LocalDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.util.StringUtils;

import it.regioneveneto.myp3.myextranet.model.RichiestaProdotto;
import it.regioneveneto.myp3.myextranet.model.StepRichiestaProdotto;
import it.regioneveneto.myp3.myextranet.utils.ObjectMapperUtils;

public class StepRichiestaProdottoDTO implements Serializable {
	
	private static final long serialVersionUID = 1L;

	protected Integer idStep;
	@NotNull(message = "RichiestaProdotto è obbligatorio")
	protected RichiestaProdottoDTO richiestaProdotto;
	@NotNull(message = "DtStep è obbligatorio")
	protected Timestamp dtStep;
	@NotBlank(message = "Esecutore è obbligatorio")
	protected String esecutore;
	@NotBlank(message = "CodStato è obbligatorio")
	protected String codStato;
	
	protected Integer esitoStep;
	@NotBlank(message = "NoteStep è obbligatorio")
	protected String noteStep;

	public Integer getIdStep() {
		return idStep;
	}

	public void setIdStep(Integer idStep) {
		this.idStep = idStep;
	}

	public RichiestaProdottoDTO getRichiestaProdotto() {
		return richiestaProdotto;
	}

	public void setRichiestaProdotto(RichiestaProdottoDTO richiestaProdotto) {
		this.richiestaProdotto = richiestaProdotto;
	}

	public Timestamp getDtStep() {
		return dtStep;
	}

	public void setDtStep(Timestamp dtStep) {
		this.dtStep = dtStep;
	}

	public String getEsecutore() {
		return esecutore;
	}

	public void setEsecutore(String esecutore) {
		this.esecutore = esecutore;
	}

	public String getCodStato() {
		return codStato;
	}

	public void setCodStato(String codStato) {
		this.codStato = codStato;
	}

	public Integer getEsitoStep() {
		return esitoStep;
	}

	public void setEsitoStep(Integer esitoStep) {
		this.esitoStep = esitoStep;
	}

	public String getNoteStep() {
		return noteStep;
	}

	public void setNoteStep(String noteStep) {
		this.noteStep = noteStep;
	}

	@Override
	public String toString() {
		return "StepRichiestaProdottoDTO [idStep=" + idStep + ", richiestaProdotto=" + richiestaProdotto + ", dtStep="
				+ dtStep + ", esecutore=" + esecutore + ", codStato=" + codStato + ", esitoStep=" + esitoStep
				+ ", noteStep=" + noteStep + "]";
	}

	public static StepRichiestaProdotto patchStepRichiestaProdotto(StepRichiestaProdottoDTO patchStepRichiestaProdotto, StepRichiestaProdotto originalStepRichiestaProdotto, boolean ignoreNulls, boolean cloneOriginal) {
		StepRichiestaProdotto stepRichiestaProdottoResult;
		
		if (cloneOriginal) {
			originalStepRichiestaProdotto = ObjectMapperUtils.map(originalStepRichiestaProdotto, new StepRichiestaProdotto());
		}
		
		if(ignoreNulls) {
			stepRichiestaProdottoResult = originalStepRichiestaProdotto;
					
			// richiestaProdotto
			if (patchStepRichiestaProdotto.getRichiestaProdotto() != null) {
				RichiestaProdotto richiestaProdotto = new RichiestaProdotto();
				richiestaProdotto.setIdProdAttivRich(patchStepRichiestaProdotto.getRichiestaProdotto().getIdProdAttivRich());
				originalStepRichiestaProdotto.setRichiestaProdotto(richiestaProdotto);
			}
			
			// dtStep
			if (patchStepRichiestaProdotto.getDtStep() != null) {
				originalStepRichiestaProdotto.setDtStep(patchStepRichiestaProdotto.getDtStep());
			}
			
			// esecutore
			if(StringUtils.hasText(patchStepRichiestaProdotto.getEsecutore())) {
				originalStepRichiestaProdotto.setEsecutore(patchStepRichiestaProdotto.getEsecutore());
			}
		
			// codStato
			if(StringUtils.hasText(patchStepRichiestaProdotto.getCodStato())) {
				originalStepRichiestaProdotto.setCodStato(patchStepRichiestaProdotto.getCodStato());
			}
			
			// esitoStep
			if (patchStepRichiestaProdotto.getEsitoStep() != null) {
				originalStepRichiestaProdotto.setEsitoStep(patchStepRichiestaProdotto.getEsitoStep());
			}
			
			// noteStep
			if(StringUtils.hasText(patchStepRichiestaProdotto.getNoteStep())) {
				originalStepRichiestaProdotto.setNoteStep(patchStepRichiestaProdotto.getNoteStep());
			}
				
		} else {
			stepRichiestaProdottoResult = ObjectMapperUtils.map(patchStepRichiestaProdotto, originalStepRichiestaProdotto);
		}
		
		return stepRichiestaProdottoResult;
	}

	
}
